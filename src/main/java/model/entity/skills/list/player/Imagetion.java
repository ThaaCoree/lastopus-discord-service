package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.EventBus;
import controller.event.events.ActionEvent;
import controller.event.events.SkillUse;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.entity.units.Unit;
import model.type.*;
import util.LogWriterUtil;

public class Imagetion extends Skill {

    public static String NAME = "Imagetion";

    public Imagetion() {
        super();
        setDescription("ครอบครอง Counter [Twisted]\n" +
                "เมื่อเริ่มการต่อสู้ จะเชื่อมต่อกับพันธมิตรทั้งหมด เมื่อจู่โจมหรือใช้สกิล ฮีล XA หน่วยและมอบ Debris XB หน่วยให้กับพันธมิตรที่สูญเสียพลังชีวิตไปมากที่สุด\n" +
                "เมื่อ Imagetion ส่งผล, ได้รับ Twisted หนึ่งสแต็ค สูงสุด XD สแต็ค, แต่ละสแต็คจะเพิ่ม CritChance XC\n" +
                "ผลของสกิลนี้จะหมดลงเมื่อจบการต่อสู้");
        setActionType("Passive");
        setManaReservePercent(0.6);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.6*MATK*(1+HealAMP)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.HEALING);
        getSkillMultiplier().get("XA").getTags().add(SkillType.CONNECTION);
        getSkillMultiplier().get("XA").getTags().add(SkillType.RECOVERY);

        addMultiplier("XB", "0.3*MATK");
        addTag("XB", SkillType.SPELL);
        addTag("XB", SkillType.DEBRIS);
        addTag("XB", SkillType.CONNECTION);

        addMultiplier("XC", "0.0001*INT");
        addTag("XC", SkillType.SCALING);
        addTag("XC", SkillType.CRITICAL);
        setPercent("XC");

        addMultiplier("XD", "10");
        addTag("XD", SkillType.LIMIT);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
//                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
        );
//        spec    .addFields(
//                new SkillInputSpec.InputField<String>("Mode", SkillInputSpec.InputType.SELECT, 0)
//                        .options(List.of("choice","choice"), 0)
//                        .labelProvider(String::toString, 0)
//        , 0, 0)
//                .addFields(
//                        new SkillInputSpec.InputField<String>("Damage", SkillInputSpec.InputType.NUMBER,1)
//                , 0, 1);
        return spec;
    }

    @Override
    public void calculateExtra() {
        addCounter(CounterName.TWISTED);
        double twisted = getUser().getCounter().get(CounterName.TWISTED);
        getSkillModifier().getStatModifierSafe(StatType.CRITCHANCE).setGlobalMult(twisted * getMultiplierResult("XC"));
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ActionEvent.class, EventPhase.POST, 0, event -> {
            if (event.unit_source == getUser()) {
                if (!event.hasActType(ActType.STRIKE)) return;
                LogWriterUtil.log(event.unit_source.getName()+"'s Imagetion Activated");
                double xa = getSkillMultiplier().get("XA").getResult();
                Unit heal_target = new Unit();
                double health_amount = Double.MAX_VALUE;
                for (Unit unit : getAllies(combatFlow)) {
                    double hp = unit.getHealth().getRemaining();
                    if (hp < health_amount) {
                        health_amount = hp;
                        heal_target = unit;
                    }
                }
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), heal_target)
                                .effect(ActionEffectType.HEALTH_RECOVER, xa, 1)
                                .addActType(ActType.HEAL, ActType.HEALTH_RECOVER, ActType.SKILL_TRIGGER)
                                .build()
                );
                getUser().counterIncrement(CounterName.TWISTED);
            }
        });

        eventBus.register(SkillUse.class, EventPhase.POST, 0, event -> {
            if (event.source == getUser()) {
                LogWriterUtil.log(event.source.getName()+"'s Imagetion Activated");
                double xa = getSkillMultiplier().get("XA").getResult();
                Unit heal_target = new Unit();
                double health_amount = Double.MAX_VALUE;
                for (Unit unit : getAllies(combatFlow)) {
                    double hp = unit.getHealth().getRemaining();
                    if (hp < health_amount) {
                        health_amount = hp;
                        heal_target = unit;
                    }
                }
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), heal_target)
                                .effect(ActionEffectType.HEALTH_RECOVER, xa, 1)
                                .addActType(ActType.HEAL, ActType.HEALTH_RECOVER, ActType.SKILL_TRIGGER)
                                .build()
                );
                getUser().counterIncrement(CounterName.TWISTED);
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

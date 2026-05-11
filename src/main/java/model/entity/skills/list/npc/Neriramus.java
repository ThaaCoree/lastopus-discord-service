package model.entity.skills.list.npc;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.entity.units.Unit;
import model.type.ActType;
import model.type.ActionEffectType;
import model.type.SkillType;

public class Neriramus extends Skill {

    public static String NAME = "Neriramus";

    public Neriramus() {
        super();
        setDescription("ฮีลให้กับเป้าหมาย XA หน่วย ฮีลส่วนเกินจะถูกส่งต่อไปยังยูนิตพันธมิตรอื่นที่เลือกสูงสุด XB เป้าหมาย");
        setActionType("Action");
        setManaCost(10);
        setCooldown(2);
        getSkillMultiplier().put("XA",new SkillMultiplier("3.5*MATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.RECOVERY);
        getSkillMultiplier().get("XA").getTags().add(SkillType.HEALING);

        getSkillMultiplier().put("XB",new SkillMultiplier("2"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.LIMIT);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.ALLIES, 1)
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

    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        if (!skillTarget.getTarget(0).isEmpty()) {
            double xa = getSkillMultiplier().get("XA").getResult();
            double remain_heal = 0;
            for (String name : skillTarget.getTarget(0)) {
                Unit target = combatFlow.findUnit(name);
                double current_hp = target.getHealth().getRemaining();
                double usable_hp = target.getHealth().getUsable();
                if (current_hp+xa > usable_hp) {
                    remain_heal = current_hp+xa - usable_hp;
                }
            }
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .effect(ActionEffectType.HEALTH_RECOVER, xa, 1)
                            .addActType(ActType.HEAL, ActType.HEALTH_RECOVER, ActType.CAST)
                            .build()
            );

            for (String name : skillTarget.getTarget(1)) {
                Unit target = combatFlow.findUnit(name);
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), target)
                                .effect(ActionEffectType.HEALTH_RECOVER, xa, 1)
                                .addActType(ActType.HEAL, ActType.HEALTH_RECOVER, ActType.CAST)
                                .build()
                );
                double current_hp = target.getHealth().getRemaining();
                double usable_hp = target.getHealth().getUsable();
                if (current_hp+remain_heal > usable_hp) {
                    remain_heal = current_hp+remain_heal - usable_hp;
                } else {
                    remain_heal = 0;
                }
            }
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

    }

    @Override
    public String getName() {
        return NAME;
    }
}

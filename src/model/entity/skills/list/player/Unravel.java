package model.entity.skills.list.player;

import main.controller.CombatFlow;
import main.controller.event.EventBus;
import main.controller.event.events.ActionEffect;
import main.controller.event.events.ActionEvent;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.entity.units.Unit;
import model.type.*;
import util.LogWriterUtil;

import java.util.List;

public class Unravel extends Skill {

    public static String NAME = "Unravel";

    public Unravel() {
        super();
        setDescription("การโจมตีทั้งหมดจะถูกแบ่งออกเป็น XA ครั้ง โดยในแต่ละครั้งจะสร้างความเสียหายไม่ต่ำกว่า XB จากความเสียหายตั้งต้น");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        setManaReservePercent(0.1);
        getSkillMultiplier().put("XA", new SkillMultiplier("((Accuracy*1.5)/(PATK+RATK+MATK)) * (1+AttackSPD/5)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XA").getTags().add(SkillType.FIGHTING_STYLE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.10"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.LIMIT);
        getSkillMultiplier().get("XB").getTags().add(SkillType.FIGHTING_STYLE);
        getSkillMultiplier().get("XB").setPercent(true);
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
        double patk = getUser().getStats().get(StatType.PHYSICALATTACK).getFinal();
        if (patk == 0) {
            getSkillMultiplier().put("XA",new SkillMultiplier("1"));
        }
        calculateAllMultiplier();
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ActionEvent.class, EventPhase.PRE, 0, (ActionEvent event) -> {
            if (!event.hasActType(ActType.ATTACK) || event.unit_source != getUser() || event.hasActType(ActType.SKILL_TRIGGER)
                || event.event_source.equals(getName())) return;
            for (Unit unit : event.unit_target) {
                if (!event.canDamage(unit.getName())) continue;
                for (ActionEffect actionEffect : event.effects.get(unit.getName())) {
                    if (actionEffect.type == ActionEffectType.DAMAGE_PHYSICAL ||
                            actionEffect.type == ActionEffectType.DAMAGE_MAGICAL ||
                            actionEffect.type == ActionEffectType.DAMAGE_PURE ||
                            actionEffect.type == ActionEffectType.DAMAGE_TRUE) {
                        double xa = getSkillMultiplier().get("XA").getResult();
                        double xb = getSkillMultiplier().get("XB").getResult();
                        double damage = actionEffect.finalValue;
                        if (damage/xa < damage*xb) {
                            damage *= xb;
                        } else {
                            damage /= xa;
                        }
                        actionEffect.finalValue = damage;
                        event.damage_times = (int) xa;
                        LogWriterUtil.log(">Unravel triggered");
                    }
                }
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

package model.entity.skills.list.npc;

import main.controller.CombatFlow;
import main.controller.event.EventBus;
import main.controller.event.events.ActionEffect;
import main.controller.event.events.ActionEvent;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.*;
import util.LogWriterUtil;

import java.util.concurrent.ThreadLocalRandom;

public class Auto_Guard extends Skill {

    public static String NAME = "Auto Guard";

    public Auto_Guard() {
        super();
        setDescription("สลายกระดาษ XA แผ่น มอบ Debris XB หน่วยให้กับยูนิตเป้าหมาย" +
                "Debris นี้จะไม่หายไปเมื่อรับความเสียหาย คงอยู่เป็นเวลา XC เทิร์น");
        setActionType("Reaction");
        setManaCost(4);
        setCooldown(3);
        getSkillMultiplier().put("XA",new SkillMultiplier("3"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.DRAWBACK);
        getSkillMultiplier().get("XA").getTags().add(SkillType.REQUIREMENT);

        getSkillMultiplier().put("XB",new SkillMultiplier("1.5*MATK"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.DEFENSE);
        getSkillMultiplier().get("XB").getTags().add(SkillType.DEBRIS);

        getSkillMultiplier().put("XC",new SkillMultiplier("1"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.DURATION);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
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
        double xa = getSkillMultiplier().get("XA").getResult();
        double xb = getSkillMultiplier().get("XB").getResult();
        double xc = getSkillMultiplier().get("XC").getResult();
        getUser().counterSum(CounterName.PAPER,xa*-1);

        Conditions condition = new Conditions("Auto Guard");
        condition.setDescription("มี Debris ไม่ต่ำกว่า "+xb+" หน่วย");
        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.ADVANCED);

        if (!skillTarget.getTarget(0).isEmpty()) {
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .effect(ActionEffectType.CREATE_DEBRIS, xb, 1)
                            .condition(condition, (int) xc)
                            .addActType(ActType.CAST, ActType.CREATE_DEBRIS)
                            .build()
            );
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        combatFlow.getAllUnit().forEach((name, unit) -> {
            if (unit.hasCondition("Auto Guard")) {
                double oldDebris = unit.getDebris().getRemaining();
                if (oldDebris < getSkillMultiplier().get("XB").getResult()) {
                    unit.getDebris().setRemaining(getSkillMultiplier().get("XB").getResult());
                }
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

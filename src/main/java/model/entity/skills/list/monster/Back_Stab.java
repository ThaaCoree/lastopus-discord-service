package model.entity.skills.list.monster;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

public class Back_Stab extends Skill implements SkillWithCondition {

    public static String NAME = "Back Stab";

    public Back_Stab() {
        super();
        setDescription("โจมตีระยะประชิดใส่เป้าหมาย ใช้งานได้เมื่ออยู่ในรัศมี 1 เมตรของเป้าหมายเท่านั้น\n" +
                "การโจมตีครั้งนี้เป็นการจู่โจมจากจุดบอด สร้างความเสียหายกายภาพ XA หน่วย เป้าหมายที่ได้รับความเสียหายจากสกิลนี้จะได้รับสถานะ Back Stabbed\n" +
                "Back Stabbed : ลด MSPD และ Evasion ลง XB เป็นเวลา XC เทิร์น");
        setActionType("Action");
        setManaCost(0);
        setCooldown(3);
        getSkillMultiplier().put("XA",new SkillMultiplier("1.6*PATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.4*(1+DebuffAMP)"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XB").getTags().add(SkillType.MOVEMENT);
        getSkillMultiplier().get("XB").setPercent(true);

        getSkillMultiplier().put("XC",new SkillMultiplier("3"));
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
        if (!skillTarget.getTarget(0).isEmpty()) {
            double xa = getSkillMultiplier().get("XA").getResult();
            int duration = (int) getSkillMultiplier().get("XC").getResult();
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Back Stabbed");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .effect(ActionEffectType.DAMAGE_PHYSICAL, xa, 1)
                            .condition(condition, duration)
                            .addActType(ActType.ATTACK, ActType.STRIKE)
                            .build()
            );
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Back Stabbed");
        condition.getStatModifiers(StatType.MOVEMENTSPEED).setGlobalMult(getSkillMultiplier().get("XA").getResult() * (-1));
        condition.getStatModifiers(StatType.EVASION).setGlobalMult(getSkillMultiplier().get("XA").getResult() * (-1));

        condition.setConditionType(ConditionType.DEBUFF);
        condition.setConditionTierType(ConditionTierType.GENERAL);

        //remove and re-add to database
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(condition.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(condition.getName(), condition);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
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

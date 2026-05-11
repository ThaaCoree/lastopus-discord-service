package model.entity.skills.list.player;

import main.controller.CombatFlow;
import main.controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

public class Gaze_From_The_False_Moon extends Skill implements SkillWithCondition {

    public static String NAME = "Gaze From The False Moon";

    public Gaze_From_The_False_Moon() {
        super();
        setDescription("ใช้งานได้เมื่อมี The Forgotten Pages อย่างน้อย XA ใบเท่านั้น ทำการกล่าวบทอ้อนวอนต่อจันทราเหนือท้องทะเลช่วยชี้ทางผู้แสวงในหนทางแห่งปัญญา\n" +
                "เข้าสู่ร่าง [ 24 Saint Of The Pale Moon ] ซึ่งทำให้สกิลไม่มีการสำรองมานาหรือพลังชีวิตเป็นเวลา XB รอบเทิร์น\n" +
                "จากนั้นเลือกพันธมิตรหนึ่งคน สร้างร่างโคลนน้ำที่จะทำตามการกระทำทุกอย่างของยูนิตเป้าหมายในมุมตรงข้ามจนกว่าจะหมดระยะเวลาสกิล\n" +
                "เมื่อออกจากร่าง [ 24 Saint Of The Pale Moon ] เข้าสู่สถานะ False Moon Punishment ซึ่งทำให้ไม่สามารถใช้งาน Reaction เป็นระยะเวลา XC รอบเทิร์น");
        setActionType("Turn");
        setManaCost(24);
        setCooldown(8);
        getPureTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().put("XA",new SkillMultiplier("6"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.REQUIREMENT);

        getSkillMultiplier().put("XB",new SkillMultiplier("2"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XC",new SkillMultiplier("2"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.DURATION);
        getSkillMultiplier().get("XC").getTags().add(SkillType.DRAWBACK);
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

    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        if (!skillTarget.getTarget(0).isEmpty()) {

        } else {
            skillTarget.getTarget(0).add(getUser().getName());
        }
        int duration = (int) getSkillMultiplier().get("XB").getResult();
        Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("24 Saint Of The Pale Moon");
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(),getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                        .condition(condition, duration)
                        .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                        .build());
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("24 Saint Of The Pale Moon");
        condition.getStatModifiers(StatType.RESERVATION).setOverride(0);

        condition.setConditionType(ConditionType.NEUTRAL);
        condition.setConditionTierType(ConditionTierType.UNDISPELLABLE);

        Conditions condition2 = new Conditions("False Moon Punishment");

        condition2.setDescription("ไม่สามารถใช้งาน Reaction ได้");
        condition2.setConditionType(ConditionType.NEUTRAL);
        condition2.setConditionTierType(ConditionTierType.UNDISPELLABLE);

        //remove and re-add to database
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(condition.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(condition.getName(), condition);
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(condition2.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(condition2.getName(), condition2);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
            ConditionManager.reapplyCondition(condition2, unit);
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

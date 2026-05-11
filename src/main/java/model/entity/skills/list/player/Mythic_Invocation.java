package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

import java.util.List;

public class Mythic_Invocation extends Skill implements SkillWithCondition {

    public static String NAME = "Mythic Invocation";

    public Mythic_Invocation() {
        super();
        setDescription("เมื่อใช้งาน เริ่มร่าย Ceremony ที่สิ้นสุดเมื่อจบรอบเทิร์นหน้าของตนเอง ในระหว่างที่กำลังร่ายนี้ มอบสถานะ Gift of Storyteller ซึ่งเพิ่ม HealAMP XA ให้กับพันธมิตรอื่นทั้งหมดเป็นเวลา XB รอบเทิร์น ครั้งถัดไปที่ยูนิตพันธมิตรอื่นจู่โจมจะได้รับฮีล XC หน่วย\n" +
                "เมื่อร่ายเสร็จสิ้น สำแดงผลของธาตุลม, น้ำ หรือแสงหนึ่งครั้ง ผู้ใช้เข้าสู่ \"จันทราทิวากาล\" ได้รับสถานะ Moonbound Day และ Stories' End เป็นเวลา XD รอบเทิร์น\n" +
                "Moonbound Day : เพิ่ม DEF XE หน่วย");
        setActionType("Action");
        setManaCost(11);
        setCooldown(5);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.5*HealAMP"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XA").getTags().add(SkillType.CEREMONY);
        getSkillMultiplier().get("XA").setPercent(true);

        getSkillMultiplier().put("XB",new SkillMultiplier("2"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.1*MATK*(1+HealAMP)"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.RECOVERY);
        getSkillMultiplier().get("XC").getTags().add(SkillType.HEALING);
        getSkillMultiplier().get("XC").getTags().add(SkillType.CEREMONY);

        getSkillMultiplier().put("XD",new SkillMultiplier("2"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XE",new SkillMultiplier("2*MATK*(1+BuffAMP)"));
        getSkillMultiplier().get("XE").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XE").getTags().add(SkillType.CEREMONY);
        getSkillMultiplier().get("XE").getTags().add(SkillType.DEFENSE);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        List<String> choices = List.of("Start Ceremony", "Finish Ceremony");
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser(), choices
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.CUSTOM, 0)
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
        if (skillTarget.getTarget(0).contains("Start Ceremony")) {
            int duration = (int) getSkillMultiplier().get("XB").getResult();
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Gift of Storyteller");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), getOtherAllies(combatFlow))
                            .condition(condition, duration)
                            .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                            .build());
        }

        if (skillTarget.getTarget(0).contains("Finish Ceremony")) {
            int duration = (int) getSkillMultiplier().get("XD").getResult();
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Moonbound Day");
            Conditions condition2 = combatFlow.getDatabase().getAllConditionMap().get("Stories' End");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), getUser())
                            .condition(condition, duration)
                            .condition(condition2, duration)
                            .addActType(ActType.CONDITION_GIVEN)
                            .build());
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Moonbound Day");
        condition.getStatModifiers(StatType.PHYSICALDEFENSE).setFlat(getSkillMultiplier().get("XE").getResult());
        condition.getStatModifiers(StatType.MAGICALDEFENSE).setFlat(getSkillMultiplier().get("XE").getResult());
        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.ADVANCED);

        Conditions condition2 = new Conditions("Gift of Storyteller");
        condition2.getStatModifiers(StatType.HEALAMPLIFIER).setGlobalMult(getSkillMultiplier().get("XA").getResult());
        condition2.setConditionType(ConditionType.BUFF);
        condition2.setConditionTierType(ConditionTierType.ADVANCED);

        //remove and re-add to database
        addConditionToDatabase(condition,combatFlow);
        addConditionToDatabase(condition2,combatFlow);

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

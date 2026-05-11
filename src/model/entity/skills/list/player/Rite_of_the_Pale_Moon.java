package model.entity.skills.list.player;

import main.controller.CombatFlow;
import main.controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

import java.util.List;

public class Rite_of_the_Pale_Moon extends Skill implements SkillWithCondition {

    public static String NAME = "Rite of the Pale Moon";

    public Rite_of_the_Pale_Moon() {
        super();
        setDescription("เพิ่มความเร็วเคลื่อนที่และโอกาสหลบหลีก เลือกได้ 3 ระดับ ความเร็วและระยะเวลาของสกิลจะแปรผกผันกัน\n" +
                "แบ่งเป็นระดับ Stiff (XA) XC รอบเทิร์น , Swift (XB) XD รอบเทิร์น และ Secure (XB) XE รอบเทิร์น\n" +
                "โดยที่ เมื่อเลือกความเร็วสูงสุดสกิลจะเปลี่ยนรูปแบบเป็น Reaction และสามารถใช้งานพร้อมกับการเคลื่อนที่ได้");
        setActionType("Combine / Reaction");
        setManaCost(5);
        setCooldown(4);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.28*(1+BuffAMP)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XA").getTags().add(SkillType.MOVEMENT);
        getSkillMultiplier().get("XA").setPercent(true);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.34*(1+BuffAMP)"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XB").getTags().add(SkillType.MOVEMENT);
        getSkillMultiplier().get("XB").setPercent(true);

        getSkillMultiplier().put("XC",new SkillMultiplier("4"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XD",new SkillMultiplier("2"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XE",new SkillMultiplier("1"));
        getSkillMultiplier().get("XE").getTags().add(SkillType.DURATION);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        List<String> level = List.of("Stiff", "Swift", "Secure");
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser(), level
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
        int duration = 99;

        Conditions condition = new Conditions();
        if (skillTarget.getTarget(0).contains("Stiff")) {
        condition = combatFlow.getDatabase().getAllConditionMap().get("Rite of the Pale Moon Stiff");
        duration = (int) getSkillMultiplier().get("XC").getResult();
        }
        if (skillTarget.getTarget(0).contains("Swift")) {
            condition = combatFlow.getDatabase().getAllConditionMap().get("Rite of the Pale Moon Swift");
            duration = (int) getSkillMultiplier().get("XD").getResult();
        }
        if (skillTarget.getTarget(0).contains("Secure")) {
            condition = combatFlow.getDatabase().getAllConditionMap().get("Rite of the Pale Moon Secure");
            duration = (int) getSkillMultiplier().get("XE").getResult();
        }

        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(),getUser(), getUser())
                        .condition(condition, duration)
                        .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                        .build());
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        double buffAMP = 1+getUser().getStats().get(StatType.BUFFAMPLIFIER).getFinal();
        double debuffAMP = 1+getUser().getStats().get(StatType.DEBUFFAMPLIFIER).getFinal();
        Conditions stiff = new Conditions("Rite of the Pale Moon Stiff");
        Conditions swift = new Conditions("Rite of the Pale Moon Swift");
        Conditions secure = new Conditions("Rite of the Pale Moon Secure");
        stiff.getStatModifiers(StatType.MOVEMENTSPEED).setGlobalMult(getSkillMultiplier().get("XA").getResult());
        stiff.getStatModifiers(StatType.EVASION).setGlobalMult(getSkillMultiplier().get("XA").getResult());
        swift.getStatModifiers(StatType.MOVEMENTSPEED).setGlobalMult(getSkillMultiplier().get("XB").getResult());
        swift.getStatModifiers(StatType.EVASION).setGlobalMult(getSkillMultiplier().get("XB").getResult());
        secure.getStatModifiers(StatType.MOVEMENTSPEED).setGlobalMult(getSkillMultiplier().get("XB").getResult());
        secure.getStatModifiers(StatType.EVASION).setGlobalMult(getSkillMultiplier().get("XB").getResult());

        stiff.setConditionType(ConditionType.BUFF);
        stiff.setConditionTierType(ConditionTierType.GENERAL);
        swift.setConditionType(ConditionType.BUFF);
        swift.setConditionTierType(ConditionTierType.GENERAL);
        secure.setConditionType(ConditionType.BUFF);
        secure.setConditionTierType(ConditionTierType.GENERAL);

        //remove and re-add to database
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(stiff.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(stiff.getName(), stiff);
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(swift.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(swift.getName(), swift);
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(secure.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(secure.getName(), secure);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(stiff, unit);
            ConditionManager.reapplyCondition(swift, unit);
            ConditionManager.reapplyCondition(secure, unit);
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

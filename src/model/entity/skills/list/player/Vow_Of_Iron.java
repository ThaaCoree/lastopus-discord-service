package model.entity.skills.list.player;

import main.controller.CombatFlow;
import main.controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillWithCondition;
import model.entity.units.Unit;
import model.modifier.TransferModifier;
import model.type.*;

import java.util.List;

public class Vow_Of_Iron extends Skill implements SkillWithCondition {

    public static String NAME = "Vow of Iron";

    public Vow_Of_Iron() {
        super();
        setDescription("ได้รับความสามารถในการแปลงสเตตัสหลัก (STR, DEX, INT) เป็นสเตตัสหลักอื่นค่าก็ได้อย่างอิสระ\n" +
                "หากอยู่ในระหว่างการต่อสู้ จะใช้งาน Combined Action ในการแปลงหนึ่งค่า");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.RESOURCE);
        setManaReservePercent(0.7);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        List<String> choices = List.of("STR>DEX","STR>INT", "DEX>STR", "DEX>INT","INT>STR", "INT>DEX");
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
        Unit user = getUser();
        if (user.hasCondition("Heavenly Vow (STR>DEX)"))
            addVow(StatusType.STRENGTH, StatusType.DEXTERITY);

        if (user.hasCondition("Heavenly Vow (STR>INT)"))
            addVow(StatusType.STRENGTH, StatusType.INTELLIGENCE);

        if (user.hasCondition("Heavenly Vow (DEX>STR)"))
            addVow(StatusType.DEXTERITY, StatusType.STRENGTH);

        if (user.hasCondition("Heavenly Vow (DEX>INT)"))
            addVow(StatusType.DEXTERITY, StatusType.INTELLIGENCE);

        if (user.hasCondition("Heavenly Vow (INT>STR)"))
            addVow(StatusType.INTELLIGENCE, StatusType.STRENGTH);

        if (user.hasCondition("Heavenly Vow (INT>DEX)"))
            addVow(StatusType.INTELLIGENCE, StatusType.DEXTERITY);
    }

    private void addVow(StatusType source, StatusType target) {
        TransferModifier tm = new TransferModifier();
        tm.setTransferType(TransferType.CONVERSION);
        tm.setSourceStatus(source);
        tm.setTargetStatus(target);
        tm.setTransferPercent(1);
        tm.setTransferRatio(1);

        getSkillModifier().addTransferModifier(tm);
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        if (skillTarget.getTarget(0).contains("STR>DEX")) {
            ConditionManager.removeCondition(getUser(), "Heavenly Vow (STR>INT)");
            int duration = 99;
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Heavenly Vow (STR>DEX)");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(),getUser(), getUser())
                            .condition(condition, duration)
                            .addActType(ActType.CONDITION_GIVEN, ActType.SKILL_TRIGGER)
                            .build());
        }

        if (skillTarget.getTarget(0).contains("STR>INT")) {
            ConditionManager.removeCondition(getUser(), "Heavenly Vow (STR>DEX)");
            int duration = 99;
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Heavenly Vow (STR>INT)");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(),getUser(), getUser())
                            .condition(condition, duration)
                            .addActType(ActType.CONDITION_GIVEN, ActType.SKILL_TRIGGER)
                            .build());
        }

        if (skillTarget.getTarget(0).contains("DEX>STR")) {
            ConditionManager.removeCondition(getUser(), "Heavenly Vow (DEX>INT)");
            int duration = 99;
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Heavenly Vow (DEX>STR)");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(),getUser(), getUser())
                            .condition(condition, duration)
                            .addActType(ActType.CONDITION_GIVEN, ActType.SKILL_TRIGGER)
                            .build());
        }

        if (skillTarget.getTarget(0).contains("DEX>INT")) {
            ConditionManager.removeCondition(getUser(), "Heavenly Vow (DEX>STR)");
            int duration = 99;
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Heavenly Vow (DEX>INT)");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(),getUser(), getUser())
                            .condition(condition, duration)
                            .addActType(ActType.CONDITION_GIVEN, ActType.SKILL_TRIGGER)
                            .build());
        }

        if (skillTarget.getTarget(0).contains("INT>STR")) {
            ConditionManager.removeCondition(getUser(), "Heavenly Vow (INT>DEX)");
            int duration = 99;
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Heavenly Vow (INT>STR)");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(),getUser(), getUser())
                            .condition(condition, duration)
                            .addActType(ActType.CONDITION_GIVEN, ActType.SKILL_TRIGGER)
                            .build());
        }

        if (skillTarget.getTarget(0).contains("INT>DEX")) {
            ConditionManager.removeCondition(getUser(), "Heavenly Vow (INT>STR)");
            int duration = 99;
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Heavenly Vow (INT>DEX)");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(),getUser(), getUser())
                            .condition(condition, duration)
                            .addActType(ActType.CONDITION_GIVEN, ActType.SKILL_TRIGGER)
                            .build());
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition1 = new Conditions("Heavenly Vow (STR>DEX)");
        condition1.setConditionType(ConditionType.NEUTRAL);
        condition1.setConditionTierType(ConditionTierType.UNDISPELLABLE);

        Conditions condition2 = new Conditions("Heavenly Vow (STR>INT)");
        condition2.setConditionType(ConditionType.NEUTRAL);
        condition2.setConditionTierType(ConditionTierType.UNDISPELLABLE);

        Conditions condition3 = new Conditions("Heavenly Vow (DEX>STR)");
        condition3.setConditionType(ConditionType.NEUTRAL);
        condition3.setConditionTierType(ConditionTierType.UNDISPELLABLE);

        Conditions condition4 = new Conditions("Heavenly Vow (DEX>INT)");
        condition4.setConditionType(ConditionType.NEUTRAL);
        condition4.setConditionTierType(ConditionTierType.UNDISPELLABLE);

        Conditions condition5 = new Conditions("Heavenly Vow (INT>STR)");
        condition5.setConditionType(ConditionType.NEUTRAL);
        condition5.setConditionTierType(ConditionTierType.UNDISPELLABLE);

        Conditions condition6 = new Conditions("Heavenly Vow (INT>DEX)");
        condition6.setConditionType(ConditionType.NEUTRAL);
        condition6.setConditionTierType(ConditionTierType.UNDISPELLABLE);

        //remove and re-add to database
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(condition1.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(condition1.getName(), condition1);
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(condition2.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(condition2.getName(), condition2);
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(condition3.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(condition3.getName(), condition3);
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(condition4.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(condition4.getName(), condition4);
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(condition5.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(condition5.getName(), condition5);
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(condition6.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(condition6.getName(), condition6);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition1, unit);
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

package main.java.model.entity.skills.list.monster;

import main.java.controller.CombatFlow;
import main.java.controller.event.events.ActionEvent;
import main.java.manager.ConditionManager;
import main.java.model.entity.Conditions;
import main.java.model.entity.skills.*;
import main.java.model.entity.units.Unit;
import main.java.model.type.*;

public class Sun_Bless extends Skill implements SkillWithCondition {

    public static String NAME = "Sun Bless";

    public Sun_Bless() {
        super();
        setDescription("มอบพลังแห่งตะวันให้กับพันธมิตรทั้งหมด\n" +
                "STR, AGI, DEX XA หน่วย\n" +
                "VIT, INT, WIS XB หน่วย\n");
        setActionType("Turn");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.8*DEX*(1+BuffAMP)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.BUFF);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.6*INT*(1+BuffAMP)"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.BUFF);

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
        Conditions condition = combatFlow.findCondition("Sun Bless");

        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(),getUser(), getAllies(combatFlow))
                        .condition(condition, 99)
                        .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                        .build());
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Sun Bless");
        condition.getStatusModifiers(StatusType.STRENGTH).setFlat(getSkillMultiplier().get("XA").getResult());
        condition.getStatusModifiers(StatusType.AGILITY).setFlat(getSkillMultiplier().get("XA").getResult());
        condition.getStatusModifiers(StatusType.DEXTERITY).setFlat(getSkillMultiplier().get("XA").getResult());
        condition.getStatusModifiers(StatusType.VITALITY).setFlat(getSkillMultiplier().get("XB").getResult());
        condition.getStatusModifiers(StatusType.INTELLIGENCE).setFlat(getSkillMultiplier().get("XB").getResult());
        condition.getStatusModifiers(StatusType.WISDOM).setFlat(getSkillMultiplier().get("XB").getResult());

        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.BOUND);

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

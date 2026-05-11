package model.entity.skills.list.monster;

import main.controller.CombatFlow;
import main.controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

public class Snow_Fall extends Skill implements SkillWithCondition {

    public static String NAME = "Snow Fall";

    public Snow_Fall() {
        super();
        setDescription("เรียกหิมะลงมายังพื้นที่ ปกคลุมทั่วสนาม ยูนิตศัตรูทั้งหมดรับสถานะ Bone Chill\n" +
                "Bone Chill : ลด AGI ลง XA, ลด WIS ลง XB");
        setActionType("Action");
        setManaCost(0);
        setCooldown(4);
        getPureTags().add(SkillType.SPELL);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.4*(1+DebuffAMP)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XA").setPercent(true);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.4*(1+DebuffAMP)"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DEBUFF);
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

    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        int duration = 99;
        Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Bone Chill");
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(),getUser(), getEnemies(combatFlow))
                        .condition(condition, duration)
                        .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                        .build());
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Bone Chill");
        condition.getStatusModifiers(StatusType.AGILITY).setGlobalMult(getSkillMultiplier().get("XA").getResult()*(-1));
        condition.getStatusModifiers(StatusType.WISDOM).setGlobalMult(getSkillMultiplier().get("XB").getResult()*(-1));

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

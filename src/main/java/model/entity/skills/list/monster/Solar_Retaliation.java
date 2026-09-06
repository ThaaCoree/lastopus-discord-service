package model.entity.skills.list.monster;

import controller.CombatFlow;
import controller.event.EventBus;
import controller.event.events.ActionEvent;
import controller.event.events.ResourceEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

import java.util.List;

public class Solar_Retaliation extends Skill implements SkillWithCondition {

    public static String NAME = "Solar Retaliation";

    public Solar_Retaliation() {
        super();
        setDescription("ยูนิตที่สร้างความเสียหายให้กับยูนิตนี้จะได้รับสถานะ Solar Dehydration หนึ่งสแต็คตลอดการต่อสู้ ซึ่งลดสแตทส่วนใหญ่ XA\n" +
                "ลบล้างได้ด้วยการดื่มน้ำ");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.DEBUFF);

        getSkillMultiplier().put("XA",new SkillMultiplier("0.2*(1+DebuffAMP)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XA").getTags().add(SkillType.OPUS);
        getSkillMultiplier().get("XA").setPercent(true);
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
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Solar Dehydration");
        double multiplier = getSkillMultiplier().get("XA").getResult() * -1;

        condition.getStatModifiers(StatType.PHYSICALATTACK).setGlobalMult(multiplier);
        condition.getStatModifiers(StatType.MAGICALATTACK).setGlobalMult(multiplier);
        condition.getStatModifiers(StatType.RANGEDATTACK).setGlobalMult(multiplier);
        condition.getStatModifiers(StatType.MAGICALDEFENSE).setGlobalMult(multiplier);
        condition.getStatModifiers(StatType.PHYSICALDEFENSE).setGlobalMult(multiplier);
        condition.getStatModifiers(StatType.DEFLECTION).setGlobalMult(multiplier);
        condition.getStatModifiers(StatType.ACCURACY).setGlobalMult(multiplier);
        condition.getStatModifiers(StatType.SPEED).setGlobalMult(multiplier);
        condition.getStatModifiers(StatType.MOVEMENTSPEED).setGlobalMult(multiplier);
        condition.getStatModifiers(StatType.HEALAMPLIFIER).setGlobalMult(multiplier);
        condition.getStatModifiers(StatType.BUFFAMPLIFIER).setGlobalMult(multiplier);
        condition.getStatModifiers(StatType.DEBUFFAMPLIFIER).setGlobalMult(multiplier);
        condition.getStatModifiers(StatType.MANAREGEN).setOverride(0);

        condition.setConditionType(ConditionType.DEBUFF);
        condition.setConditionTierType(ConditionTierType.ADVANCED);

        addConditionToDatabase(condition, combatFlow);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ResourceEvent.class, EventPhase.POST, 0, (event) -> {
            if (event.target != getUser()) return;
            if (!event.isDamage()) return;

            Conditions condition = combatFlow.findCondition("Solar Dehydration");

            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), event.target)
                            .condition(condition, 99)
                            .addActType(ActType.CONDITION_GIVEN)
                            .build()
            );
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

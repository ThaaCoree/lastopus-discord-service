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

public class Immunization extends Skill implements SkillWithCondition {

    public static String NAME = "Immunization";

    public Immunization() {
        super();
        setDescription("เมื่อได้รับความเสียหาย, ครั้งถัดไปที่ได้รับความเสียหายเดิม ลดความเสียหายนั้นจนเหลือ 1 หน่วย");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.DEFENSE);
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
        Conditions physical = new Conditions("Physical Immune");
        physical.setDescription("รับความเสียหายกายภาพจากทุกแหล่ง 1 หน่วย");
        physical.setConditionType(ConditionType.NEUTRAL);
        physical.setConditionTierType(ConditionTierType.BOUND);

        Conditions magical = new Conditions("Magical Immune");
        magical.setDescription("รับความเสียหายเวทจากทุกแหล่ง 1 หน่วย");
        magical.setConditionType(ConditionType.NEUTRAL);
        magical.setConditionTierType(ConditionTierType.BOUND);

        Conditions pure = new Conditions("Pure Immune");
        pure.setDescription("รับความเสียหายโดยตรงจากทุกแหล่ง 1 หน่วย");
        pure.setConditionType(ConditionType.NEUTRAL);
        pure.setConditionTierType(ConditionTierType.BOUND);

        addConditionToDatabase(physical, combatFlow);
        addConditionToDatabase(magical, combatFlow);
        addConditionToDatabase(pure, combatFlow);

    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ActionEvent.class, EventPhase.POST, -5, (ActionEvent event) -> {
            if (!event.unit_target.contains(getUser())) return;
            if (event.event_source.equals(getName())) return;

            if (getUser().hasCondition("Physical Immune")) {
                event.addOverrideModifier(ActionEffectType.DAMAGE_PHYSICAL, 1);
                sendSkillTriggerEvent(combatFlow, "Immunization Triggered, taking 1 physical damage");
            }
            if (getUser().hasCondition("Magical Immune")) {
                event.addOverrideModifier(ActionEffectType.DAMAGE_MAGICAL, 1);
                sendSkillTriggerEvent(combatFlow, "Immunization Triggered, taking 1 magical damage");
            }
            if (getUser().hasCondition("Pure Immune")) {
                event.addOverrideModifier(ActionEffectType.DAMAGE_PURE, 1);
                sendSkillTriggerEvent(combatFlow, "Immunization Triggered, taking 1 pure damage");
            }
        });

        eventBus.register(ResourceEvent.class, EventPhase.POST, 0, event -> {
            if (event.target != getUser()) return;
            if (event.effectType == ActionEffectType.DAMAGE_PHYSICAL) {
                Conditions condition = combatFlow.findCondition("Physical Immune");
                ConditionManager.applyCondition(condition, getUser(), getUser(), 99);
                ConditionManager.removeCondition(getUser(), "Magical Immune");
                ConditionManager.removeCondition(getUser(), "Pure Immune");
            }
            if (event.effectType == ActionEffectType.DAMAGE_MAGICAL) {
                Conditions condition = combatFlow.findCondition("Magical Immune");
                ConditionManager.applyCondition(condition, getUser(), getUser(), 99);
                ConditionManager.removeCondition(getUser(), "Physical Immune");
                ConditionManager.removeCondition(getUser(), "Pure Immune");
            }
            if (event.effectType == ActionEffectType.DAMAGE_PURE) {
                Conditions condition = combatFlow.findCondition("Pure Immune");
                ConditionManager.applyCondition(condition, getUser(), getUser(), 99);
                ConditionManager.removeCondition(getUser(), "Physical Immune");
                ConditionManager.removeCondition(getUser(), "Magical Immune");
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

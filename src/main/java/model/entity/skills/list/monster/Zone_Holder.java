package model.entity.skills.list.monster;

import controller.CombatFlow;
import controller.event.EventBus;
import controller.event.events.ActionEvent;
import controller.event.events.ResourceEvent;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.units.Unit;
import model.type.*;

public class Zone_Holder extends Skill {

    public static String NAME = "Zone Holder";

    public Zone_Holder() {
        super();
        setDescription("เมย์เซฟมีทักษะในการควบคุมทุกสิ่งในพื้นที่ แม้แต่สิ่งที่ละเอียดละออที่สุดของศัตรู\n" +
                "เมื่อได้รับความเสียหายเวทหรือกายภาพ มอบสถานะ Sword Sap หรือ Wand Sap ให้ตามลำดับ\n" +
                "ยูนิตที่มีสถานะ Sword Sap หรือ Wand Sap 5 สแต็คหรือมากกว่า จะสร้างความเสียหายกายภาพหรือเวทมนตร์เป็น 0 ตามลำดับ\n" +
                "เมื่อยูนิตศัตรูทำการฟื้นฟู ผู้มอบการฟื้นฟูได้รับสถานะ Stolen Light, หากมีสถานะ Stolen Light 5 สแต็คหรือมากกว่า การฮีลจะกลายเป็นการสร้างความเสียหายจริงให้กับตัวเองแทน");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
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
//        , 0, 0);
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
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ResourceEvent.class, EventPhase.POST, 0, (ResourceEvent event) -> {
            if (event.effectType != ActionEffectType.DAMAGE_MAGICAL
                    && event.effectType != ActionEffectType.DAMAGE_PHYSICAL
                    && event.effectType != ActionEffectType.HEALTH_RECOVER) return;

            if (event.target == getUser()) {
                Conditions condition = new Conditions("Sword Sap");
                condition.setDescription("???");

                condition.setConditionType(ConditionType.DEBUFF);
                condition.setConditionTierType(ConditionTierType.ADVANCED);

                if (event.effectType == ActionEffectType.DAMAGE_MAGICAL) {
                    condition.setName("Wand Sap");
                }

                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), event.source)
                                .condition(condition, 1)
                                .addActType(ActType.CONDITION_GIVEN, ActType.SKILL_TRIGGER)
                                .build()
                );
            } else {
            if (event.effectType == ActionEffectType.HEALTH_RECOVER) {
                Conditions condition = new Conditions("Stolen Light");
                condition.setDescription("???");

                condition.setConditionType(ConditionType.DEBUFF);
                condition.setConditionTierType(ConditionTierType.ADVANCED);

                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), event.source)
                                .condition(condition, 1)
                                .addActType(ActType.CONDITION_GIVEN, ActType.SKILL_TRIGGER)
                                .build()
                );
            }
            }
        });

        eventBus.register(ResourceEvent.class, EventPhase.MODIFY, -5, event -> {
            int sword_count = event.source.hasXCondition("Sword Sap");
            int wand_count = event.source.hasXCondition("Wand Sap");
            int stolen_count = event.source.hasXCondition("Stolen Light");
            if (sword_count >= 5 && event.effectType == ActionEffectType.DAMAGE_PHYSICAL) {
                event.amount = 0;
            }
            if (wand_count >= 5 && event.effectType == ActionEffectType.DAMAGE_MAGICAL) {
                event.amount = 0;
            }
            if (stolen_count >= 5 && event.effectType == ActionEffectType.HEALTH_RECOVER) {
                double heal = event.amount;
                event.amount = 0;
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), event.source)
                                .effect(ActionEffectType.DAMAGE_TRUE, heal, 1)
                                .addActType(ActType.STRIKE, ActType.SKILL_TRIGGER)
                                .build()
                );
            }
        });

    }

    @Override
    public String getName() {
        return NAME;
    }
}

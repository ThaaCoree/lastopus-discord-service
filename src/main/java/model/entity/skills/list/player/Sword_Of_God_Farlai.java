package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import model.entity.Conditions;
import model.entity.items.EquipmentSlot;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillMultiplier;
import model.entity.skills.SkillTarget;
import model.modifier.BasicModifier;
import model.type.*;

import java.util.Map;

public class Sword_Of_God_Farlai extends Skill {

    public static String NAME = "Sword of God, Farlai";

    public Sword_Of_God_Farlai() {
        super();
        setDescription("ได้รับสแตทจากอาวุธใดๆก็ตามในมือเป็น XA จากปกติ 100% และนับว่ามันเป็นดาบ\n" +
                "อาวุธดังกล่าวจะพังลงเมื่อถูกใช้งานจนถึงขีดจำกัด\n" +
                "หากไม่ถืออาวุธใดๆอยู่เลย, เพิ่ม ATK XB");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        setManaReservePercent(0.1);
        getSkillMultiplier().put("XA",new SkillMultiplier("1.75"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XA").getTags().add(SkillType.FIGHTING_STYLE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIMIT);
        getSkillMultiplier().get("XA").setPercent(true);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.25"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XB").getTags().add(SkillType.FIGHTING_STYLE);
        getSkillMultiplier().get("XB").getTags().add(SkillType.LIMIT);
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
//        , 0, 0);
//                .addFields(
//                        new SkillInputSpec.InputField<String>("Damage", SkillInputSpec.InputType.NUMBER,1)
//                , 0, 1);
        return spec;
    }

    @Override
    public void calculateExtra() {
        double xb = getSkillMultiplier().get("XB").getResult();
        boolean empty_hand = true;
        double multiplier = getMultiplierResult("XA")-1;

        for (Map.Entry<Integer, EquipmentSlot> equipmentSlot : getUser().getEquipmentSlots().entrySet()) {
            if (equipmentSlot.getValue().getEquipmentType().equals(EquipmentType.WEAPON)) {
                if (equipmentSlot.getValue().getEquipment() != null) {
                    empty_hand = false;
                    for (StatusType statusType : StatusType.values()) {
                        BasicModifier modifier = equipmentSlot.getValue().getEquipment().getModifiers().getStatusModifierSafe(statusType);
                        BasicModifier skillModifier = getSkillModifier().getStatusModifierSafe(statusType);

                        skillModifier.sumFlat(modifier.getFlat() * multiplier);
                        skillModifier.sumGlobalMult(modifier.getGlobalMult() * multiplier);
                        skillModifier.sumMult(modifier.getMult() * multiplier);
                        skillModifier.sumEquipmentMult(modifier.getEquipmentMult() * multiplier);
                        skillModifier.sumPassiveMult(modifier.getPassiveMult() * multiplier);
                    }
                    for (StatType statType : StatType.values()) {
                        BasicModifier modifier = equipmentSlot.getValue().getEquipment().getModifiers().getStatModifierSafe(statType);
                        BasicModifier skillModifier = getSkillModifier().getStatModifierSafe(statType);

                        skillModifier.sumFlat(modifier.getFlat() * multiplier);
                        skillModifier.sumGlobalMult(modifier.getGlobalMult() * multiplier);
                        skillModifier.sumMult(modifier.getMult() * multiplier);
                        skillModifier.sumEquipmentMult(modifier.getEquipmentMult() * multiplier);
                        skillModifier.sumPassiveMult(modifier.getPassiveMult() * multiplier);
                    }
                }
            }
        }

        if (empty_hand) {
            getSkillModifier().getStatModifierSafe(StatType.PHYSICALATTACK).setGlobalMult(xb);
            getSkillModifier().getStatModifierSafe(StatType.MAGICALATTACK).setGlobalMult(xb);
            getSkillModifier().getStatModifierSafe(StatType.RANGEDATTACK).setGlobalMult(xb);
        }
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
//        EventBus eventBus = combatFlow.getEventBus();
//        eventBus.register(ActionEvent.class, EventPhase.POST, 0, (ActionEvent event) -> {
//            if (!event.hasActType(ActType.HEAL) || event.unit_source != getUser() || event.event_source.equals(getName())) return;
//            List<Unit> targets = event.unit_target;
//            double heal_amount = event.getHeal();
//
//            sendActionEvent(combatFlow.getEventBus(),
//                                ActionEvent.builder(getName(), getUser(), targets)
//                                        .effect(ActionEffectType.HEALTH_RECOVER,heal_amount, 1)
//                                        .addActType(ActType.HEAL, ActType.HEALTH_RECOVER, ActType.SKILL_TRIGGER)
//                                        .build()
//                        );
//        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

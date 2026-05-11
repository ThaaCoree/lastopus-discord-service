package model.entity.skills.list.item.utility;

import controller.CombatFlow;
import model.entity.PassiveNode;
import model.entity.UniqueModifier;
import model.entity.items.Equipment;
import model.entity.items.EquipmentSlot;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillInstance;
import model.entity.units.Unit;
import model.modifier.BasicModifier;
import model.type.*;

import java.util.HashMap;
import java.util.Map;

public class Omnistrength extends Skill {

    public static String NAME = "Omnistrength";

    public Omnistrength() {
        super();
        setDescription("การเพิ่ม GlobalPATK ทั้งหมดจะส่งผลกับ GlobalRATK ด้วย");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.PHYSICAL);
        getPureTags().add(SkillType.SCALING);
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
        Unit unit = getUser();
        Map<StatType, Double> equipMultSum = new HashMap<>();
        Map<StatType, Double> passiveMultSum = new HashMap<>();

        Map<StatType, Double> globalMultProduct = new HashMap<>();

        // เริ่มต้นค่า
        for (StatType type : StatType.values()) {
            equipMultSum.put(type, 0.0);
            passiveMultSum.put(type, 0.0);
            globalMultProduct.put(type, 1.0);
        }

        for (StatType type : StatType.values()) {
            if (type != StatType.PHYSICALATTACK) continue;
            globalMultProduct.put(StatType.RANGEDATTACK, 1.0);
            double modifier = 0;

            for (PassiveNode node : unit.getAllocatedPassives().values()) {
                BasicModifier basicModifier = node.getStatModifiers().get(type);
                if (basicModifier == null) continue;
                equipMultSum.merge(type, basicModifier.getEquipmentMult(), Double::sum);
                passiveMultSum.merge(type, basicModifier.getPassiveMult(), Double::sum);
            }
            for (EquipmentSlot slot : unit.getEquipmentSlots().values()) {
                if (slot.getEquipment() == null) continue;
                BasicModifier basicModifier = slot.getEquipment().getStatModifiers().get(type);
                if (basicModifier == null) continue;
                equipMultSum.merge(type, basicModifier.getEquipmentMult(), Double::sum);
                passiveMultSum.merge(type, basicModifier.getPassiveMult(), Double::sum);
            }

            for (PassiveNode node : unit.getAllocatedPassives().values()) {
                if (node.getStatModifiers().get(type) == null) continue;
                modifier = node.getStatModifiers().get(type).getGlobalMult();
                if (Double.isNaN(modifier) || modifier == 0.0) continue;
                globalMultProduct.merge(StatType.RANGEDATTACK,(modifier*(1+passiveMultSum.get(type))+1), (oldVal, newVal) -> oldVal * newVal);
            }
            for (EquipmentSlot slot : unit.getEquipmentSlots().values()) {
                Equipment equipment = slot.getEquipment();
                if (equipment == null) continue;

                double handMultiplier = 1;
                if (unit.isMixTwoHanded() && slot.getEquipmentType().equals(EquipmentType.WEAPON)) {
                    handMultiplier = unit.getMixTwoHandedMult();
                }
                double equipmentMod = 1;
                for (Map.Entry<Integer, PassiveNode> entry : unit.getAllocatedPassives().entrySet()) {
                    Double mult = null;
                    Map<EquipmentType, Double> multMap = entry.getValue().getEquipmentSlotMult();
                    if (multMap != null) {
                        mult = multMap.get(equipment.getEquipmentType());
                    }
                    if (mult != null) {
                        equipmentMod += mult;
                    }
                }
                if (slot.getEquipment().getStatModifiers().get(type) == null) continue;
                modifier = slot.getEquipment().getStatModifiers().get(type).getGlobalMult();
                if (Double.isNaN(modifier) || modifier == 0.0) continue;
                globalMultProduct.merge(StatType.RANGEDATTACK, ((modifier * handMultiplier * equipmentMod*(1+equipMultSum.get(type)))+1), (oldVal, newVal) -> oldVal * newVal);
            }
            for (SkillInstance instance : unit.getAllSkill().values()) {
                if (instance.getSkillData() == null) continue;
                if (instance.getSkillData().getSkillModifier().getStatModifiers().get(type) == null) continue;
                modifier = instance.getSkillData().getSkillModifier().getStatModifiers().get(type).getGlobalMult();
                if (Double.isNaN(modifier) || modifier == 0.0) continue;
                if (instance.getInstanceBundle().getStatModifiers().get(type) == null) continue;
                double skillToMult = instance.getInstanceBundle().getStatModifiers().get(type).getGlobalMult();
                globalMultProduct.merge(StatType.RANGEDATTACK, skillToMult+1, (oldVal, newVal) -> oldVal * newVal);
            }
            for (CardType cardType : unit.getCard().keySet()) {
                if (unit.getCard().get(cardType).getStatModifiers(cardType).get(type) == null) continue;
                modifier = unit.getCard().get(cardType).getStatModifiers(cardType).get(type).getGlobalMult();
                if (Double.isNaN(modifier) || modifier == 0.0) continue;
                globalMultProduct.merge(StatType.RANGEDATTACK,modifier+1, (oldVal,newVal) -> oldVal * newVal);
            }
            for (UniqueModifier uniqueModifier : unit.getUniqueModifier()) {
                if (uniqueModifier.getModifiers().getStatModifiers().get(type) == null) continue;
                modifier = uniqueModifier.getModifiers().getStatModifiers().get(type).getGlobalMult();
                if (Double.isNaN(modifier) || modifier == 0.0) continue;
                globalMultProduct.merge(StatType.RANGEDATTACK,modifier+1, (oldVal,newVal) -> oldVal * newVal);
            }
        }
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

    }

    @Override
    public String getName() {
        return NAME;
    }
}

package main.java.manager;

import model.entity.*;
import model.entity.items.Equipment;
import model.entity.items.EquipmentSlot;
import model.entity.units.Unit;
import model.type.CardType;
import model.type.EquipmentType;
import model.type.SkillType;

import java.util.Map;

public class SkillModifierManager {
    private final Unit unit;

    public SkillModifierManager(Unit unit) {
        this.unit = unit;
    }

    public void calculateSkillModifier() {
        unit.getFlatSkillModifiers().clear();
        unit.getMultSkillModifiers().clear();
        for (SkillType tag : SkillType.values()) {
            for (UniqueModifier modifier : unit.getUniqueModifier()) {
                if (modifier.getModifiers().getSkillModifiers().get(tag) == null) continue;
                unit.getFlatSkillModifiers().merge(tag, modifier.getModifiers().getSkillModifiers().get(tag).getFlat(), Double::sum);
                unit.getMultSkillModifiers().merge(tag, modifier.getModifiers().getSkillModifiers().get(tag).getMult(), Double::sum);
            }

            for (PassiveNode node : unit.getAllocatedPassives().values()) {
                if (node.getModifiers().getSkillModifiers().get(tag) == null) continue;
                unit.getFlatSkillModifiers().merge(tag, node.getModifiers().getSkillModifiers().get(tag).getFlat(), Double::sum);
                unit.getMultSkillModifiers().merge(tag, node.getModifiers().getSkillModifiers().get(tag).getMult(), Double::sum);
            }

            for (EquipmentSlot slot : unit.getEquipmentSlots().values()) {
                if (slot.getEquipment() == null) continue;
                Equipment equipment = slot.getEquipment();
                if (equipment.getModifiers().getSkillModifiers().get(tag) == null) continue;
                double twoHandMult = 1;
                if (unit.isMixTwoHanded() && equipment.getEquipmentType().equals(EquipmentType.WEAPON)) {
                    twoHandMult = unit.getMixTwoHandedMult();
                }
                unit.getFlatSkillModifiers().merge(tag, equipment.getModifiers().getSkillModifiers().get(tag).getFlat() * twoHandMult, Double::sum);
                unit.getMultSkillModifiers().merge(tag, equipment.getModifiers().getSkillModifiers().get(tag).getMult() * twoHandMult, Double::sum);
            }

            for (Map.Entry<CardType, Card> entry : unit.getCard().entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) continue;
                CardType cardType = entry.getKey();
                Card card = entry.getValue();
                if (card.getModifiers().get(cardType).getSkillModifiers().get(tag) == null) continue;
                unit.getFlatSkillModifiers().merge(tag, card.getModifiers().get(cardType).getSkillModifiers().get(tag).getFlat(), Double::sum);
                unit.getMultSkillModifiers().merge(tag, card.getModifiers().get(cardType).getSkillModifiers().get(tag).getMult(), Double::sum);
            }

            for (ConditionInstance conditionInstance : unit.getConditionInstances().values()) {
                Conditions condition = conditionInstance.getCondition();
                if (condition == null) continue;
                if (condition.getModifiers().getSkillModifiers().get(tag) == null) continue;
                double ia = 1;
                for (PassiveNode node : unit.getAllocatedPassives().values()) {
                    if (node.getName().equals("Intense Affection")) {
                        ia = 2;
                        break;
                    }
                }
                unit.getFlatSkillModifiers().merge(tag, condition.getModifiers().getSkillModifiers().get(tag).getFlat() * ia, Double::sum);
                unit.getMultSkillModifiers().merge(tag, condition.getModifiers().getSkillModifiers().get(tag).getMult() * ia, Double::sum);
            }
        }
    }
}

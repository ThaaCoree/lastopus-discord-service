package util;

import factory.SkillFactory;
import model.entity.*;
import model.entity.items.Consumable;
import model.entity.skills.SkillInstance;
import model.entity.units.Unit;
import model.modifier.BasicModifier;
import model.modifier.SkillModifier;
import model.modifier.TransferModifier;
import model.type.*;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class StatTranslateUtil {
    private static final Map<StatusType, String> statusToWrite = new HashMap<>();
    private static final Map<StatType, String> statToWrite = new HashMap<>();
    private static final Map<EquipmentType, String> equipToWrite = new HashMap<>();
    private static final Map<ResourceType, String> resourceToWrite = new HashMap<>();
    static {
        statusToWrite.put(StatusType.STRENGTH, "STR");
        statusToWrite.put(StatusType.AGILITY, "AGI");
        statusToWrite.put(StatusType.VITALITY, "VIT");
        statusToWrite.put(StatusType.DEXTERITY, "DEX");
        statusToWrite.put(StatusType.WISDOM, "WIS");
        statusToWrite.put(StatusType.INTELLIGENCE, "INT");
        statusToWrite.put(StatusType.LUCK, "LUK");
        statToWrite.put(StatType.HEALTHPOINT, "HP");
        statToWrite.put(StatType.MANAPOINT, "MP");
        statToWrite.put(StatType.PHYSICALATTACK, "PATK");
        statToWrite.put(StatType.MAGICALATTACK, "MATK");
        statToWrite.put(StatType.RANGEDATTACK, "RATK");
        statToWrite.put(StatType.PHYSICALDEFENSE, "PDEF");
        statToWrite.put(StatType.MAGICALDEFENSE, "MDEF");
        statToWrite.put(StatType.MOVEMENTSPEED, "MSPD");
        statToWrite.put(StatType.HEALAMPLIFIER, "HealAMP");
        statToWrite.put(StatType.BUFFAMPLIFIER, "BuffAMP");
        statToWrite.put(StatType.DEBUFFAMPLIFIER, "DebuffAMP");
        statToWrite.put(StatType.CRITCHANCE, "CritChance");
        statToWrite.put(StatType.CRITDAMAGE, "CritDamage");
        statToWrite.put(StatType.MANAREGEN, "ManaRegen");
        statToWrite.put(StatType.ACCURACY, "Accuracy");
        statToWrite.put(StatType.EVASION, "Evasion");
        statToWrite.put(StatType.PHYSICALBLOCK, "PBLOCK");
        statToWrite.put(StatType.MAGICALBLOCK, "MBLOCK");
        statToWrite.put(StatType.DAMAGEREDUCTION, "DMGReduction");
        statToWrite.put(StatType.DAMAGEAMPLIFIER, "DMGAmplifier");
        statToWrite.put(StatType.ATTACKSPEED, "AttackSPD");
        statToWrite.put(StatType.CASTSPEED, "CastSPD");
        statToWrite.put(StatType.PHYSICALPENETRATE, "PhysicalPen");
        statToWrite.put(StatType.MAGICALPENETRATE, "MagicalPen");
        statToWrite.put(StatType.RESERVATION, "Reservation");
        statToWrite.put(StatType.CRITSHIELD, "CritShield");
        statToWrite.put(StatType.SPEED, "Speed");
        statToWrite.put(StatType.HEALTHREGEN, "HealthRegen");
        statToWrite.put(StatType.POISONAMP, "PoisonAMP");
        statToWrite.put(StatType.IGNITEAMP, "IgniteAMP");
        statToWrite.put(StatType.BLEEDAMP, "BleedAMP");
        statToWrite.put(StatType.IGNOREMDEF, "Ignore PDEF");
        statToWrite.put(StatType.IGNOREPDEF, "Ignore MDEF");
        statToWrite.put(StatType.DEBUFFRESISTANCE, "Debuff Resistance");
        equipToWrite.put(EquipmentType.HELMET, "Helmet");
        equipToWrite.put(EquipmentType.ARMOR, "Armor");
        equipToWrite.put(EquipmentType.BOOTS, "Boots");
        equipToWrite.put(EquipmentType.GLOVES, "Gloves");
        equipToWrite.put(EquipmentType.ACCESSORY, "Accessory");
        equipToWrite.put(EquipmentType.WEAPON, "Weapon");
        resourceToWrite.put(ResourceType.HEALTH, "Health");
        resourceToWrite.put(ResourceType.MANA, "Mana");
        resourceToWrite.put(ResourceType.DEBRIS, "Debris");
    }

    public static void translatePassiveNodeStatusDesc(PassiveNode node) {
        Map<StatType, BasicModifier> statMap = node.getStatModifiers();
        Map<StatusType, BasicModifier> statusMap = node.getStatusModifiers();
        Map<Integer, TransferModifier> transferMap = node.getTransferModifiers();
        Map<EquipmentType, Double> equipmentModMap = node.getEquipmentSlotMult();
        Map<SkillType, SkillModifier> skillModMap = node.getSkillModifiers();
        //reset statusDesc
        node.setStatusDescription("");
        DecimalFormat df = new DecimalFormat("0.##");

        statusMap.forEach((key, modifier) -> {
            double flat = modifier.getFlat();
            double mult = modifier.getMult();
            double equipMult = modifier.getEquipmentMult();
            double globalMult = modifier.getGlobalMult();
            double passiveMult = modifier.getPassiveMult();
            double override = modifier.getOverride();
            String status = statusToWrite.get(key);

            String result = "";
            if (flat != 0) {
                    result = result + df.format(flat) + " " + status + "\n";
            }
            if (equipMult != 0) {
                result = result + df.format(equipMult*100) + "% Equipment" + status + "\n";
            }
            if (globalMult != 0) {
                result = result + df.format(globalMult*100) + "% Global" + status + "\n";
            }
            if (passiveMult != 0) {
                result = result + df.format(passiveMult*100) + "% Passive" + status + "\n";
            }
            if (!Double.isNaN(override)) {
                    result = result + "Set " + df.format(status) + " to " + override + "\n";
            }
            node.addStatusDescription(result);
        });

        statMap.forEach((key, modifier) -> {
            double flat = modifier.getFlat();
            double mult = modifier.getMult();
            double equipMult = modifier.getEquipmentMult();
            double globalMult = modifier.getGlobalMult();
            double passiveMult = modifier.getPassiveMult();
            double override = modifier.getOverride();
            String stat = statToWrite.get(key);

            String result = "";
            if (flat != 0) {
                if (key == StatType.ATTACKSPEED || key == StatType.RESERVATION || key == StatType.CASTSPEED || key == StatType.CRITCHANCE || key == StatType.CRITDAMAGE
                || key ==  StatType.CRITSHIELD || key == StatType.HEALAMPLIFIER || key == StatType.BUFFAMPLIFIER || key == StatType.DEBUFFAMPLIFIER || key == StatType.DAMAGEAMPLIFIER
                || key == StatType.DAMAGEREDUCTION || key == StatType.POISONAMP || key == StatType.IGNITEAMP || key == StatType.BLEEDAMP
                || key == StatType.IGNOREPDEF || key == StatType.IGNOREMDEF || key == StatType.DEBUFFRESISTANCE) {
                    result = result + df.format(flat*100) + "% " + stat + "\n";
                } else {
                    result = result + df.format(flat) + " " + stat + "\n";
                }
            }
            if (equipMult != 0) {
                    result = result + df.format(equipMult*100) + "% Equipment" + stat + "\n";
            }
            if (globalMult != 0) {
                    result = result + df.format(globalMult*100) + "% Global" + stat + "\n";
            }
            if (passiveMult != 0) {
                    result = result + df.format(passiveMult*100) + "% Passive" + stat + "\n";
            }
            if (!Double.isNaN(override)) {
                    result = result + "Set " + stat + " to " + df.format(override) + "\n";
            }
            node.addStatusDescription(result);
        });

        transferMap.forEach((key, modifier) -> {
            TransferType transferType = modifier.getTransferType();
            String sourceStat = statToWrite.get(modifier.getSourceStat());
            String targetStat = statToWrite.get(modifier.getTargetStat());
            String sourceStatus = statusToWrite.get(modifier.getSourceStatus());
            String targetStatus = statusToWrite.get(modifier.getTargetStatus());
            double transferPercent = modifier.getTransferPercent();
            double transferRatio = modifier.getTransferRatio();

            String result = "";

            if (transferType == TransferType.CONVERSION) {
                if (sourceStat != null) {
                    if (transferRatio != 1) {
                        result = result + df.format(transferPercent*100) + "% of " + sourceStat + " Converted to " + targetStat + " at " + (int) (transferRatio*100) + "% \n";
                    } else {
                        result = result + df.format(transferPercent*100) + "% of " + sourceStat + " Converted to " + targetStat + "\n";
                    }
                }
                if (sourceStatus != null) {
                    if (transferRatio != 1) {
                        result = result + df.format(transferPercent*100) + "% of " + sourceStatus + " Converted to " + targetStatus + " at " + (int) (transferRatio*100) + "% \n";
                    } else {
                        result = result + df.format(transferPercent*100) + "% of " + sourceStatus + " Converted to " + targetStatus + "\n";
                    }
                }
            }
            if (transferType == TransferType.GAIN) {
                if (sourceStat != null) {
                        result = result + "Gain " + df.format(transferPercent*100) + "% of " + sourceStat + " as Extra " + targetStat + "\n";
                }
                if (sourceStatus != null) {
                        result = result + "Gain " + df.format(transferPercent*100) + "% of " + sourceStatus + " as Extra " + targetStatus + "\n";
                }
            }
            node.addStatusDescription(result);
        });

        equipmentModMap.forEach((key, modifier) -> {
            String equipmentType = equipToWrite.get(key);
            double mod = modifier;
            String result = "";

            if (mod != 0) {
                result = result + "Amplify " + equipmentType + " by " + df.format(mod*100) + "%\n";
            }

            node.addStatusDescription(result);
        });

        skillModMap.forEach((key, modifier) -> {
            double flat = modifier.getFlat();
            double mult = modifier.getMult();
            StringBuilder result = new StringBuilder();

            if (flat > 0) {
                result.append("ตัวคูณสกิลประเภท ").append(key.writeAsString()).append(" เพิ่มขึ้น ").append(df.format(flat)).append("\n");
            }
            if (flat < 0) {
                result.append("ตัวคูณสกิลประเภท ").append(key.writeAsString()).append(" ลดลง ").append(df.format(Math.abs(flat))).append("\n");
            }
            if (mult > 0) {
                result.append("ตัวคูณสกิลประเภท ").append(key.writeAsString()).append(" เพิ่มขึ้น ").append(df.format(mult*100)).append("%").append("\n");
            }
            if (mult < 0) {
                result.append("ตัวคูณสกิลประเภท ").append(key.writeAsString()).append(" ลดลง ").append(df.format(Math.abs(mult*100))).append("%").append("\n");
            }

            node.addStatusDescription(result.toString());
        });
        if (node.getStatusPoints() > 0) {
            node.addStatusDescription("Status Point +" + Integer.toString(node.getStatusPoints()));
        }
    }

    public static String translateStatusDesc(ModifierBundle modifierBundle, Map<String, SkillInstance> skillInstanceMap) {
        Map<StatType, BasicModifier> statMap = modifierBundle.getStatModifiers();
        Map<StatusType, BasicModifier> statusMap = modifierBundle.getStatusModifiers();
        Map<Integer, TransferModifier> transferMap = modifierBundle.getTransferModifiers();
        Map<SkillType, SkillModifier> skillMap = modifierBundle.getSkillModifiers();
        //reset statusDesc
        DecimalFormat df = new DecimalFormat("0.##");

        StringBuilder result = new StringBuilder();

        statusMap.forEach((key, modifier) -> {
            double flat = modifier.getFlat();
            double mult = modifier.getMult();
            double equipMult = modifier.getEquipmentMult();
            double globalMult = modifier.getGlobalMult();
            double passiveMult = modifier.getPassiveMult();
            double override = modifier.getOverride();
            String status = statusToWrite.get(key);

            if (flat != 0) {
                result.append(df.format(flat)).append(" ").append(status).append("\n");
            }
            if (equipMult != 0) {
                result.append(df.format(equipMult * 100)).append("% Equipment").append(status).append("\n");
            }
            if (globalMult != 0) {
                result.append(df.format(globalMult * 100)).append("% Global").append(status).append("\n");
            }
            if (passiveMult != 0) {
                result.append(df.format(passiveMult * 100)).append("% Passive").append(status).append("\n");
            }
            if (!Double.isNaN(override)) {
                result.append("Set ").append(status).append(" to ").append(df.format(override)).append("\n");
            }
        });

        statMap.forEach((key, modifier) -> {
            double flat = modifier.getFlat();
            double mult = modifier.getMult();
            double equipMult = modifier.getEquipmentMult();
            double globalMult = modifier.getGlobalMult();
            double passiveMult = modifier.getPassiveMult();
            double override = modifier.getOverride();
            String stat = statToWrite.get(key);

            boolean percent = key == StatType.ATTACKSPEED || key == StatType.RESERVATION || key == StatType.CASTSPEED ||
                    key == StatType.CRITCHANCE || key == StatType.CRITDAMAGE || key == StatType.CRITSHIELD ||
                    key == StatType.HEALAMPLIFIER || key == StatType.BUFFAMPLIFIER || key == StatType.DEBUFFAMPLIFIER ||
                    key == StatType.DAMAGEAMPLIFIER || key == StatType.DAMAGEREDUCTION || key == StatType.POISONAMP ||
                    key == StatType.BLEEDAMP || key == StatType.IGNITEAMP ||
                    key == StatType.IGNOREPDEF || key == StatType.IGNOREMDEF || key == StatType.DEBUFFRESISTANCE;
            if (flat != 0) {
                if (percent) {
                    result.append(df.format(flat * 100)).append("% ").append(stat).append("\n");
                } else {
                    result.append(df.format(flat)).append(" ").append(stat).append("\n");
                }
            }
            if (equipMult != 0) {
                result.append(df.format(equipMult * 100)).append("% Equipment").append(stat).append("\n");
            }
            if (globalMult != 0) {
                result.append(df.format(globalMult * 100)).append("% Global").append(stat).append("\n");
            }
            if (passiveMult != 0) {
                result.append(df.format(passiveMult * 100)).append("% Passive").append(stat).append("\n");
            }
            if (!Double.isNaN(override)) {
                if (percent) {
                    result.append("Set ").append(stat).append(" to ").append(df.format(override*100)).append("%\n");
                } else {
                    result.append("Set ").append(stat).append(" to ").append(df.format(override)).append("\n");
                }
            }
        });

        transferMap.forEach((key, modifier) -> {
            TransferType transferType = modifier.getTransferType();
            String sourceStat = statToWrite.get(modifier.getSourceStat());
            String targetStat = statToWrite.get(modifier.getTargetStat());
            String sourceStatus = statusToWrite.get(modifier.getSourceStatus());
            String targetStatus = statusToWrite.get(modifier.getTargetStatus());
            double transferPercent = modifier.getTransferPercent();
            double transferRatio = modifier.getTransferRatio();

            if (transferType == TransferType.CONVERSION) {
                if (sourceStat != null) {
                    if (transferRatio != 1) {
                        result.append(df.format(transferPercent * 100)).append("% of ").append(sourceStat)
                                .append(" Converted to ").append(targetStat)
                                .append(" at ").append((int)(transferRatio * 100)).append("%\n");
                    } else {
                        result.append(df.format(transferPercent * 100)).append("% of ").append(sourceStat)
                                .append(" Converted to ").append(targetStat).append("\n");
                    }
                }
                if (sourceStatus != null) {
                    if (transferRatio != 1) {
                        result.append(df.format(transferPercent * 100)).append("% of ").append(sourceStatus)
                                .append(" Converted to ").append(targetStatus)
                                .append(" at ").append((int)(transferRatio * 100)).append("%\n");
                    } else {
                        result.append(df.format(transferPercent * 100)).append("% of ").append(sourceStatus)
                                .append(" Converted to ").append(targetStatus).append("\n");
                    }
                }
            }

            if (transferType == TransferType.GAIN) {
                if (sourceStat != null) {
                    result.append("Gain ").append(df.format(transferPercent * 100)).append("% of ")
                            .append(sourceStat).append(" as Extra ").append(targetStat).append("\n");
                }
                if (sourceStatus != null) {
                    result.append("Gain ").append(df.format(transferPercent * 100)).append("% of ")
                            .append(sourceStatus).append(" as Extra ").append(targetStatus).append("\n");
                }
            }
        });

        skillMap.forEach((key, modifier) -> {
            double flat = modifier.getFlat();
            double mult = modifier.getMult();

            if (flat > 0) {
                result.append("ตัวคูณสกิลประเภท ").append(key.writeAsString()).append(" เพิ่มขึ้น ").append(df.format(flat)).append("\n");
            }
            if (flat < 0) {
                result.append("ตัวคูณสกิลประเภท ").append(key.writeAsString()).append(" ลดลง ").append(df.format(Math.abs(flat))).append("\n");
            }
            if (mult > 0) {
                result.append("ตัวคูณสกิลประเภท ").append(key.writeAsString()).append(" เพิ่มขึ้น ").append(df.format(mult*100)).append("%").append("\n");
            }
            if (mult < 0) {
                result.append("ตัวคูณสกิลประเภท ").append(key.writeAsString()).append(" ลดลง ").append(df.format(Math.abs(mult*100))).append("%").append("\n");
            }
        });

        result.append("\n");
        if (skillInstanceMap != null) {
            skillInstanceMap.forEach((name, skill) -> {
                result.append("ได้รับสกิล ").append(name).append("\n");
                Unit unit = new Unit();
                unit.calculateEverything();
                unit.getSkillList().put(name, new SkillInstance(SkillFactory.getSkill(name, unit, false)));
                unit.reloadSkill();
                SkillInstance unit_bound = unit.getSkillList().get(name);
                result.append(name).append(" : ");
                String cooldown = unit_bound.getSkillData().getTranslatedCooldown();
                String mana_cost = unit_bound.getSkillData().getTranslatedCost();
                String act = unit_bound.getSkillData().getActionType();
                String description = unit_bound.getSkillData().getDescription();
                if (!cooldown.isEmpty() && !cooldown.equals("0")) {
                    result.append("คูลดาวน์ ").append(cooldown).append(" เทิร์น, ");
                }
                if (!mana_cost.isEmpty() && !mana_cost.equals("0")) {
                    result.append("ใช้ ").append(mana_cost).append(", ");
                }
                if (!act.isEmpty()) {
                    result.append(act);
                }
                String replacedDescription = description;
                for (char c = 'A'; c <= 'Z'; c++) {
                    String key = "X" + c;
                    String replace = "";
                    if (unit_bound.getSkillData().getSkillMultiplier().get(key) != null) {
                        if (unit_bound.getSkillData().getSkillMultiplier().get(key).isPercent()) {
                            replace = unit_bound.getSkillData().getSkillMultiplier().get(key).getFormula()+"*100 (%)";
                        } else {
                            replace = unit_bound.getSkillData().getSkillMultiplier().get(key).getFormula();
                        }
                        replacedDescription = replacedDescription.replace(
                                key,
                                replace
                        );
                    }

                }
                description = replacedDescription;
                result.append("\n").append(description);
            });
        }

        return result.toString();
    }

    public static void translateConsumableStatusDesc(Consumable consumable) {
        StringBuilder result = new StringBuilder();
        Map<ResourceType, Double> restoreFlat = consumable.getRestoredFlat();
        Map<ResourceType, Double> restorePercent = consumable.getRestoredPercent();
        Map<ResourceType, Double> restoreMissingPercent = consumable.getRestoredMissingPercent();
        DecimalFormat df = new DecimalFormat("0.##");
        //reset StatusDesc
        consumable.setStatusDescription("");

        for (ResourceType type : ResourceType.values()) {
            if (restoreFlat.get(type) != null && restoreFlat.get(type) != 0) {
                result.append("Recover ")
                        .append(df.format(restoreFlat.get(type)))
                        .append(" ")
                        .append(resourceToWrite.get(type))
                        .append("\n");
            }

            if (restorePercent.get(type) != null && restorePercent.get(type) != 0) {
                result.append("Recover ")
                        .append(df.format(restorePercent.get(type)*100))
                        .append("% Max ")
                        .append(resourceToWrite.get(type))
                        .append("\n");
            }

            if (restoreMissingPercent.get(type) != null && restoreMissingPercent.get(type) != 0) {
                result.append("Recover ")
                        .append(df.format(restoreMissingPercent.get(type)*100))
                        .append("% Missing ")
                        .append(resourceToWrite.get(type))
                        .append("\n");
            }
        }
        for (TimedCondition tcon : consumable.getConditionsGiven()) {
            result.append("สถานะ ")
                    .append(tcon.getCondition().getName())
                    .append(" เป็นเวลา ")
                    .append(df.format(tcon.getDuration()))
                    .append(" รอบเทิร์น\n");
        }
        consumable.setStatusDescription(result.toString());
    }
}

package calculator;

import model.entity.ModifierBundle;
import model.entity.items.Rune;
import model.entity.units.Unit;
import model.type.StatType;
import model.type.StatusType;

import java.util.ArrayList;
import java.util.List;

public class RuneCalculator {
    public static ModifierBundle calculateRuneForUnit(Unit unit) {
        ModifierBundle modifierBundle = new ModifierBundle();
        List<Rune> runeModifiers = calculateRuneModifiers(unit);
        for (Rune socketedRune : runeModifiers) {
            //status
            for (StatusType statusType : StatusType.values()) {
                if (socketedRune.getModifiers().getStatusModifiers().get(statusType) == null) continue;
                modifierBundle.getStatusModifierSafe(statusType).sumFlat(
                        socketedRune.getModifiers().getStatusModifiers().get(statusType).getFlat()
                );
                modifierBundle.getStatusModifierSafe(statusType).sumMult(
                        socketedRune.getModifiers().getStatusModifiers().get(statusType).getMult()
                );
                modifierBundle.getStatusModifierSafe(statusType).sumGlobalMult(
                        socketedRune.getModifiers().getStatusModifiers().get(statusType).getGlobalMult()
                );
                modifierBundle.getStatusModifierSafe(statusType).sumPassiveMult(
                        socketedRune.getModifiers().getStatusModifiers().get(statusType).getPassiveMult()
                );
                modifierBundle.getStatusModifierSafe(statusType).setOverride(
                        socketedRune.getModifiers().getStatusModifiers().get(statusType).getOverride()
                );
            }

            //stats
            for (StatType statType : StatType.values()) {
                if (socketedRune.getModifiers().getStatModifiers().get(statType) == null) continue;
                modifierBundle.getStatModifierSafe(statType).sumFlat(
                        socketedRune.getModifiers().getStatModifiers().get(statType).getFlat()
                );
                modifierBundle.getStatModifierSafe(statType).sumMult(
                        socketedRune.getModifiers().getStatModifiers().get(statType).getMult()
                );
                modifierBundle.getStatModifierSafe(statType).sumGlobalMult(
                        socketedRune.getModifiers().getStatModifiers().get(statType).getGlobalMult()
                );
                modifierBundle.getStatModifierSafe(statType).sumPassiveMult(
                        socketedRune.getModifiers().getStatModifiers().get(statType).getPassiveMult()
                );
                modifierBundle.getStatModifierSafe(statType).setOverride(
                        socketedRune.getModifiers().getStatModifiers().get(statType).getOverride()
                );
            }

        }
        return modifierBundle;
    }


    public static List<Rune> calculateRuneModifiers(Unit unit) {
        // ต้องไม่ลืม deep copy
        List<Rune> list = new ArrayList<>();
        for (Rune socketedRune : unit.getSocketed_runes()) {
            list.add(socketedRune.copy());
        }

        calculateGrandSpectrum(list);

        return list;
    }


    public static void calculateGrandSpectrum(List<Rune> list) {
        // นับจำนวน Grand Spectrum ทั้งหมดก่อน
        long grandSpectrumCount = list.stream()
                .filter(r -> r.getName().contains("Grand Spectrum"))
                .count();

        if (grandSpectrumCount == 0) return;

        for (Rune rune : list) {
            if (!rune.getName().contains("Grand Spectrum")) continue;

            double mult = 0.07 * grandSpectrumCount;

            if (rune.getName().contains("Might")) {
                rune.getModifiers().getStatusModifierSafe(StatusType.STRENGTH).setGlobalMult(mult);
            } else if (rune.getName().contains("Quick")) {
                rune.getModifiers().getStatusModifierSafe(StatusType.AGILITY).setGlobalMult(mult);
            } else if (rune.getName().contains("Tough")) {
                rune.getModifiers().getStatusModifierSafe(StatusType.VITALITY).setGlobalMult(mult);
            } else if (rune.getName().contains("Alert")) {
                rune.getModifiers().getStatusModifierSafe(StatusType.DEXTERITY).setGlobalMult(mult);
            } else if (rune.getName().contains("Insight")) {
                rune.getModifiers().getStatusModifierSafe(StatusType.WISDOM).setGlobalMult(mult);
            } else if (rune.getName().contains("Wise")) {
                rune.getModifiers().getStatusModifierSafe(StatusType.INTELLIGENCE).setGlobalMult(mult);
            } else if (rune.getName().contains("Bless")) {
                rune.getModifiers().getStatusModifierSafe(StatusType.LUCK).setGlobalMult(mult);
            }
        }
    }
}

package calculator;

import model.entity.ModifierBundle;
import model.entity.items.Rune;
import model.entity.units.Unit;
import model.type.StatType;
import model.type.StatusType;

public class RuneCalculator {
    public static ModifierBundle calculateRuneForUnit(Unit unit) {
        ModifierBundle modifierBundle = new ModifierBundle();
        for (Rune socketedRune : unit.getSocketed_runes()) {
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

    public static ModifierBundle calculateRuneModifiers() {
        // ต้องไม่ลืม deep copy
        return new ModifierBundle();
    }
}

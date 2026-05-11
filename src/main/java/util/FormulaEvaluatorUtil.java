package util;

import model.entity.units.Unit;
import model.type.ResourceType;
import model.type.StatType;
import model.type.StatusType;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.HashMap;
import java.util.Map;

public class FormulaEvaluatorUtil {


    public static double evaluateFormula(String formula, Unit unit) {
        Map<String, Double> variables = new HashMap<>();

        variables.put("STR", unit.getStatuses().get(StatusType.STRENGTH).getFinal());
        variables.put("AGI", unit.getStatuses().get(StatusType.AGILITY).getFinal());
        variables.put("VIT", unit.getStatuses().get(StatusType.VITALITY).getFinal());
        variables.put("DEX", unit.getStatuses().get(StatusType.DEXTERITY).getFinal());
        variables.put("WIS", unit.getStatuses().get(StatusType.WISDOM).getFinal());
        variables.put("INT", unit.getStatuses().get(StatusType.INTELLIGENCE).getFinal());
        variables.put("LUK", unit.getStatuses().get(StatusType.LUCK).getFinal());

        variables.put("HP", unit.getStats().get(StatType.HEALTHPOINT).getFinal());
        variables.put("MP", unit.getStats().get(StatType.MANAPOINT).getFinal());
        variables.put("PATK", unit.getStats().get(StatType.PHYSICALATTACK).getFinal());
        variables.put("MATK", unit.getStats().get(StatType.MAGICALATTACK).getFinal());
        variables.put("RATK", unit.getStats().get(StatType.RANGEDATTACK).getFinal());
        variables.put("PDEF", unit.getStats().get(StatType.PHYSICALDEFENSE).getFinal());
        variables.put("MDEF", unit.getStats().get(StatType.MAGICALDEFENSE).getFinal());
        variables.put("MSPD", unit.getStats().get(StatType.MOVEMENTSPEED).getFinal());
        variables.put("HealAMP", unit.getStats().get(StatType.HEALAMPLIFIER).getFinal());
        variables.put("BuffAMP", unit.getStats().get(StatType.BUFFAMPLIFIER).getFinal());
        variables.put("DebuffAMP", unit.getStats().get(StatType.DEBUFFAMPLIFIER).getFinal());
        variables.put("CritChance", unit.getStats().get(StatType.CRITCHANCE).getFinal());
        variables.put("CritDamage", unit.getStats().get(StatType.CRITDAMAGE).getFinal());
        variables.put("ManaRegen", unit.getStats().get(StatType.MANAREGEN).getFinal());
        variables.put("Accuracy", unit.getStats().get(StatType.ACCURACY).getFinal());
        variables.put("Evasion", unit.getStats().get(StatType.EVASION).getFinal());
        variables.put("PBlock", unit.getStats().get(StatType.PHYSICALBLOCK).getFinal());
        variables.put("MBlock", unit.getStats().get(StatType.MAGICALBLOCK).getFinal());
        variables.put("DamageRED", unit.getStats().get(StatType.DAMAGEREDUCTION).getFinal());
        variables.put("DamageAMP", unit.getStats().get(StatType.DAMAGEAMPLIFIER).getFinal());
        variables.put("AttackSPD", unit.getStats().get(StatType.ATTACKSPEED).getFinal());
        variables.put("CastSPD", unit.getStats().get(StatType.CASTSPEED).getFinal());
        variables.put("PhysicalPen", unit.getStats().get(StatType.PHYSICALPENETRATE).getFinal());
        variables.put("MagicalPen", unit.getStats().get(StatType.MAGICALPENETRATE).getFinal());
        variables.put("Reservation", unit.getStats().get(StatType.RESERVATION).getFinal());
        variables.put("CritShield", unit.getStats().get(StatType.CRITSHIELD).getFinal());
        variables.put("Speed", unit.getStats().get(StatType.SPEED).getFinal());
        variables.put("HealthRegen", unit.getStats().get(StatType.HEALTHREGEN).getFinal());
        variables.put("PoisonAMP", unit.getStats().get(StatType.POISONAMP).getFinal());
        variables.put("IgniteAMP", unit.getStats().get(StatType.IGNITEAMP).getFinal());
        variables.put("BleedAMP", unit.getStats().get(StatType.BLEEDAMP).getFinal());

        variables.put("UsableHP", unit.getResources().get(ResourceType.HEALTH).getUsable());
        variables.put("UsableMP", unit.getResources().get(ResourceType.MANA).getUsable());
        variables.put("RemainingHP", unit.getResources().get(ResourceType.HEALTH).getRemaining());
        variables.put("RemainingMP", unit.getResources().get(ResourceType.MANA).getRemaining());

        double reservedHP = unit.getStats().get(StatType.HEALTHPOINT).getFinal() - unit.getResources().get(ResourceType.HEALTH).getUsable();
        double reservedMP = unit.getStats().get(StatType.MANAPOINT).getFinal() - unit.getResources().get(ResourceType.MANA).getUsable();
        variables.put("ReservedHP", reservedHP);
        variables.put("ReservedMP", reservedMP);

        double missingHP = unit.getResources().get(ResourceType.HEALTH).getUsable() - unit.getResources().get(ResourceType.HEALTH).getRemaining();
        double missingMP = unit.getResources().get(ResourceType.MANA).getUsable() - unit.getResources().get(ResourceType.MANA).getRemaining();
        variables.put("MissingHP", missingHP);
        variables.put("MissingMP", missingMP);

        // ใช้ exp4j เพื่อ evaluate สูตร
        ExpressionBuilder builder = new ExpressionBuilder(formula);
        builder.variables(variables.keySet());
        Expression expression = builder.build();
        expression.setVariables(variables);
        double to_return;
        try {
            to_return = expression.evaluate();
        } catch (ArithmeticException e) {
            to_return = 0;
        }
        return to_return;
    }
}

package util;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class FormulaUtils {
    public static double evaluateFormula(String input) {
        try {
            Expression expression = new ExpressionBuilder(input).build();
            return expression.evaluate();
        } catch (Exception e) {
            return 0;
        }
    }
}

package model.entity.skills;

import model.type.SkillType;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SkillMultiplier {
    private String formula = "";
    private Set<SkillType> tags = new HashSet<>();
    private double result;
    private boolean percent;

    public SkillMultiplier(String formula) {
        this.formula = formula;
        result = 0;
        percent = false;
    }

    public SkillMultiplier(double number) {
        result = number;
        percent = false;
    }

    public String getResultString() {
        DecimalFormat df = new DecimalFormat("0.##");
        if (percent) {
            return df.format(result * 100) + "%";  // => "25.00%"
        } else {
            return df.format(result);
        }
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public Set<SkillType> getTags() {
        return tags;
    }

    public String getTagString() {
        String result = tags.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        return result;
    }

    public void setTags(Set<SkillType> tags) {
        this.tags = tags;
    }

    public void addTag(SkillType type) {
        this.tags.add(type);
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }

    public void addResult(double toAdd) {
        this.result += toAdd;
    }

    public void multResult(double toMult) {
        this.result *= toMult;
    }

    public boolean isPercent() {
        return percent;
    }

    public void setPercent(boolean percent) {
        this.percent = percent;
    }
}

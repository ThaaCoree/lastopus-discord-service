package main.java.model.modifier;

public class SkillModifier {
    private double flat;
    private double mult;

    public SkillModifier() {
        this.flat = 0;
        this.mult = 0;
    }

    public double getFlat() {
        return flat;
    }

    public void setFlat(double flat) {
        this.flat = flat;
    }

    public double getMult() {
        return mult;
    }

    public void setMult(double mult) {
        this.mult = mult;
    }

    public void sumFlat(double toSum) {
        flat += toSum;
    }

    public void sumMult(double toSum) {
        mult += toSum;
    }

    public SkillModifier copy() {
        SkillModifier copy = new SkillModifier();
        copy.flat = this.flat;
        copy.mult = this.mult;
        return copy;
    }
}

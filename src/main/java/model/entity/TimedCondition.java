package model.entity;

public class TimedCondition {
    private Conditions condition;
    private double duration;
    public TimedCondition() {
        condition = new Conditions();
        duration = 0;
    }

    public TimedCondition(Conditions condition, double duration) {
        this.condition = condition;
        this.duration = duration;
    }

    public Conditions getCondition() {
        return condition;
    }

    public void setCondition(Conditions condition) {
        this.condition = condition;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }
}

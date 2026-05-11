package main.java.model.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import main.java.model.entity.units.Unit;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConditionInstance {
    private Conditions condition;
    @JsonIgnore
    private Unit source;
    private String sourceName;
    private int appliedTime = 0;
    private int duration;
    private int durationRemain = 0;
    private Map<String, Double> numberRecord = new LinkedHashMap<>();

    public ConditionInstance(Conditions condition, Unit source, int duration) {
        this.condition = condition;
        this.source = source;
        this.duration = duration;
        this.durationRemain = duration;
        sourceName = source.getName();
    }

    public ConditionInstance(Conditions condition, int duration) {
        this.condition = condition;
        this.duration = duration;
        this.durationRemain = duration;
    }

    public ConditionInstance() {

    }

    public void sumAppliedTime(int toSum){
        this.appliedTime += toSum;
        calculateDurationRemain();
    }

    public double getNumberRecordOrDefault(String key, double defaultValue) {
        return numberRecord.getOrDefault(key, defaultValue);
    }

    public void putNumberRecord(String key, double to_put) {
        numberRecord.put(key, to_put);
    }

    public void calculateDurationRemain() {
        durationRemain = duration - appliedTime;
    }

    @JsonIgnore
    public boolean isExpired() {
        return duration <= appliedTime;
    }

    public Conditions getCondition() {
        return condition;
    }

    public void setCondition(Conditions condition) {
        this.condition = condition;
    }

    public Unit getSource() {
        return source;
    }

    public void setSource(Unit source) {
        this.source = source;
    }

    public double getAppliedTime() {
        return appliedTime;
    }

    public void setAppliedTime(int appliedTime) {
        this.appliedTime = appliedTime;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getDurationRemain() {
        return durationRemain;
    }

    public void setDurationRemain(int durationRemain) {
        this.durationRemain = durationRemain;
    }

    public String getSourceName() {
        return sourceName;
    }
}

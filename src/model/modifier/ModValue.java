package model.modifier;

import java.util.EnumMap;
import java.util.Map;

public class ModValue {

    private double baseValue;
    private double currentValue;
    private double finalValue;

    public ModValue(double baseValue) {
        this.baseValue = baseValue;
        this.currentValue = baseValue;
        this.finalValue = baseValue;
    }

    public ModValue() {
        this.baseValue = 1;
        this.currentValue = 1;
        this.finalValue = 1;
    }

    public ModValue copy() {
        ModValue copy = new ModValue();
        copy.baseValue = this.baseValue;
        copy.currentValue = this.currentValue;
        copy.finalValue = this.finalValue;
        return copy;
    }

    // --- Getters ---
    public double getBase() {
        return baseValue;
    }

    public double getCurrent() {
        return currentValue;
    }

    public double getFinal() {
        return finalValue;
    }

    // --- Setters ---
    public void setBase(double value) {
        this.baseValue = value;
    }

    public void setCurrent(double value) {
        this.currentValue = value;
    }

    public void setFinal(double value) {
        this.finalValue = value;
    }

    // --- Utility ---
    public void resetCurrentToBase() {
        this.currentValue = baseValue;
    }

    public void resetFinalToCurrent() {
        this.finalValue = currentValue;
    }

    public void addToBase(double number) {
        this.baseValue += number;
    }

    public void sumToCurrent(double number) {
        this.currentValue += number;
    }

    public void sumToFinal(double number) {
        this.finalValue += number;
    }
    
    public void multToBase(double number) {
        this.baseValue *= number;
    }

    public void multToCurrent(double number) {
        this.currentValue *= number;
    }

    public void multToFinal(double number) {
        this.finalValue *= number;
    }

    @Override
    public String toString() {
        return "Base: " + baseValue +
                ", Current: " + currentValue +
                ", Final: " + finalValue;
    }
}

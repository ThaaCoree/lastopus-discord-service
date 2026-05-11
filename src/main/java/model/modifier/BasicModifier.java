package main.java.model.modifier;

public class BasicModifier {
    private double flat;
    private double mult;
    private double override;
    private double globalMult;
    private double equipmentMult;
    private double passiveMult;

    public BasicModifier() {
        this.flat = 0;
        this.mult = 0;
        this.globalMult = 0;
        this.equipmentMult = 0;
        this.override = Double.NaN;
        this.passiveMult = 0;
    }

    public BasicModifier(double flat, double mult, double override) {
        this.flat = flat;
        this.mult = mult;
        this.override = override;
    }

    public BasicModifier copy() {
        BasicModifier copy = new BasicModifier();
        copy.flat = this.flat;
        copy.mult = this.mult;
        copy.override = this.override;
        copy.globalMult = this.globalMult;
        copy.equipmentMult = this.equipmentMult;
        copy.passiveMult = this.passiveMult;
        return copy;
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

    public double getOverride() {
        return override;
    }

    public void setOverride(double override) {
        this.override = override;
    }

    public double getGlobalMult() {
        return globalMult;
    }

    public double getEquipmentMult() {
        return equipmentMult;
    }

    public void setGlobalMult(double globalMult) {
        this.globalMult = globalMult;
    }

    public void setEquipmentMult(double equipmentMult) {
        this.equipmentMult = equipmentMult;
    }

    public double getPassiveMult() {
        return passiveMult;
    }

    public void setPassiveMult(double passiveMult) {
        this.passiveMult = passiveMult;
    }

    public void sumFlat(double toSum) {
        flat += toSum;
    }

    public void sumMult(double toSum) {
        mult += toSum;
    }

    public void sumGlobalMult(double toSum) {
        globalMult += toSum;
    }

    public void sumEquipmentMult(double toSum) {
        equipmentMult += toSum;
    }

    public void sumPassiveMult(double toSum) {
        passiveMult += toSum;
    }


    @Override
    public String toString() {
        return "BasicModifier{" +
                "flat=" + flat +
                ", mult=" + mult +
                ", override=" + override +
                ", globalMult=" + globalMult +
                ", equipmentMult=" + equipmentMult +
                '}';
    }
}

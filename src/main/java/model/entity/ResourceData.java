package main.java.model.entity;

public class ResourceData {
    private double reservedFlat;
    private double reservedPercent;
    private double usable;
    private double remaining;
    
    public void sumReservedFlat(double toSum){
        reservedFlat += toSum;
    }
    
    public void productReservedFlat(double toProduct) {
        reservedFlat *= toProduct;
    }

    public void sumReservedPercent(double toSum){
        reservedPercent += toSum;
    }

    public void productReservedPercent(double toProduct) {
        reservedPercent *= toProduct;
    }

    public void sumUsable(double toSum){
        usable += toSum;
    }

    public void sumRemaining(double toSum){
        remaining += toSum;
    }

    public ResourceData copy() {
        ResourceData copy = new ResourceData();
        copy.reservedFlat = this.reservedFlat;
        copy.reservedPercent = this.reservedPercent;
        copy.usable = this.usable;
        copy.remaining = this.remaining;
        return copy;
    }

    public double getReservedFlat() {
        return reservedFlat;
    }

    public void setReservedFlat(double reservedFlat) {
        this.reservedFlat = reservedFlat;
    }

    public double getReservedPercent() {
        return reservedPercent;
    }

    public void setReservedPercent(double reservedPercent) {
        this.reservedPercent = reservedPercent;
    }

    public double getUsable() {
        return usable;
    }

    public void setUsable(double usable) {
        this.usable = usable;
    }

    public double getRemaining() {
        return remaining;
    }

    public void setRemaining(double toSet) {
        this.remaining = toSet;
    }
}

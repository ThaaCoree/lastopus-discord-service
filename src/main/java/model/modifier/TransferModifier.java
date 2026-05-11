package model.modifier;

import model.type.StatType;
import model.type.StatusType;
import model.type.TransferType;

public class TransferModifier {
    private StatType sourceStat;
    private StatType targetStat;
    private StatusType sourceStatus;
    private StatusType targetStatus;
    private double transferPercent;
    private double transferRatio;
    private TransferType transferType;

    public TransferModifier(StatType sourceStat, StatType targetStat, StatusType sourceStatus, StatusType targetStatus, double transferPercent, double transferRatio) {
        this.sourceStat = sourceStat;
        this.targetStat = targetStat;
        this.sourceStatus = sourceStatus;
        this.targetStatus = targetStatus;
        this.transferPercent = transferPercent;
        this.transferRatio = transferRatio;
    }

    public TransferModifier(StatType sourceStat, StatType targetStat, StatusType sourceStatus, StatusType targetStatus, double transferPercent) {
        this.sourceStat = sourceStat;
        this.targetStat = targetStat;
        this.sourceStatus = sourceStatus;
        this.targetStatus = targetStatus;
        this.transferPercent = transferPercent;
        this.transferRatio = 1;
    }

    public TransferModifier() {

    }

    public StatType getSourceStat() {
        return sourceStat;
    }

    public StatType getTargetStat() {
        return targetStat;
    }

    public StatusType getSourceStatus() {
        return sourceStatus;
    }

    public StatusType getTargetStatus() {
        return targetStatus;
    }

    public double getTransferPercent() {
        return transferPercent;
    }

    public double getTransferRatio() {
        return transferRatio;
    }

    public void setSourceStat(StatType sourceStat) {
        this.sourceStat = sourceStat;
    }

    public void setTargetStat(StatType targetStat) {
        this.targetStat = targetStat;
    }

    public void setSourceStatus(StatusType sourceStatus) {
        this.sourceStatus = sourceStatus;
    }

    public void setTargetStatus(StatusType targetStatus) {
        this.targetStatus = targetStatus;
    }

    public void setTransferPercent(double transferPercent) {
        this.transferPercent = transferPercent;
    }

    public void setTransferRatio(double transferRatio) {
        this.transferRatio = transferRatio;
    }

    public TransferType getTransferType() {
        return transferType;
    }

    public void setTransferType(TransferType transferType) {
        this.transferType = transferType;
    }

    @Override
    public String toString() {
        return "TransferModifier{" +
                "sourceStat=" + sourceStat +
                ", targetStat=" + targetStat +
                ", sourceStatus=" + sourceStatus +
                ", targetStatus=" + targetStatus +
                ", transferPercent=" + transferPercent +
                ", transferRatio=" + transferRatio +
                '}';
    }

    public TransferModifier copy() {
        TransferModifier copy = new TransferModifier();

        copy.sourceStat = this.sourceStat;
        copy.targetStat = this.targetStat;
        copy.sourceStatus = this.sourceStatus;
        copy.targetStatus = this.targetStatus;
        copy.transferPercent = this.transferPercent;
        copy.transferRatio = this.transferRatio;
        copy.transferType = this.transferType;

        return copy;
    }
}

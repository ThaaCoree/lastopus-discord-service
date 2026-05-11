package main.java.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import main.java.model.type.StatusType;

public class Race {
    private String name;
    private StatusType strongStatus;
    private StatusType weakStatus;
    private String description;

    public Race(String name) {
        this.name = name;
    }

    public Race() {
    }

    public String getName() {
        return name;
    }

    public StatusType getStrongStatus() {
        return strongStatus;
    }

    public void setStrongStatus(StatusType strongStatus) {
        this.strongStatus = strongStatus;
    }

    public StatusType getWeakStatus() {
        return weakStatus;
    }

    public void setWeakStatus(StatusType weakStatus) {
        this.weakStatus = weakStatus;
    }

    public String getDescription() {
        return description;
    }

    @JsonIgnore
    public String getHumanDescription() {
        if (strongStatus != null && weakStatus != null) {
            return "ได้รับ 2 " + strongStatus.writeAsString() + " ทุกเลเวล \nไม่สามารถมีค่า " + weakStatus.writeAsString() + " สูงกว่าเลเวลได้";
        }
        return "";
    }
}

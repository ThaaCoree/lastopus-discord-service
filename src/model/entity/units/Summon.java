package model.entity.units;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import model.type.UnitType;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class Summon extends Unit {

    @JsonIgnore
    private Unit owner;

    private final Map<Integer, String> opusMove = new LinkedHashMap<>();
    private String nick_name;
    private String behavior;
    private String intimacy;
    private double soulCost;

    public Summon(Unit owner) {
        this.owner = owner;
        setUnitType(UnitType.SUMMON);
        nick_name = "";
        intimacy = "";
        soulCost = 0;
    }

    public Summon(String name) {
        setUnitType(UnitType.SUMMON);
        nick_name = "";
        intimacy = "";
        soulCost = 0;
    }

    public Summon() {
        setUnitType(UnitType.SUMMON);
        nick_name = "";
        intimacy = "";
        soulCost = 0;
    }

    public Unit getOwner() {
        return owner;
    }

    public void setOwner(Unit owner) {
        this.owner = owner;
    }

    public String getBehavior() {
        return behavior;
    }

    public String getIntimacy() {
        return intimacy;
    }

    public double getSoulCost() {
        return soulCost;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }

    public void setIntimacy(String intimacy) {
        this.intimacy = intimacy;
    }

    public void setSoulCost(double soulCost) {
        this.soulCost = soulCost;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public Map<Integer, String> getOpusMove() {
        return opusMove;
    }
}

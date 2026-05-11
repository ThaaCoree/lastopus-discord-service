package model.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ActType {
    ATTACK,
    STRIKE,
    CAST,
    HEALTH_RECOVER,
    MANA_RECOVER,
    HEAL,
    CREATE_DEBRIS,
    SKILL_TRIGGER,
    CONDITION_GIVEN;

    @JsonValue
    public String toJson() {
        return name(); // หรือจะ return "Player" ก็ได้
    }
}

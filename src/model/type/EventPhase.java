package model.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EventPhase {
    PRE,
    MODIFY,
    POST;

    @JsonValue
    public String toJson() {
        return name(); // หรือจะ return "Player" ก็ได้
    }
}
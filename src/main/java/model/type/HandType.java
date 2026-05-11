package main.java.model.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum HandType {
    ONE_HANDED,
    TWO_HANDED,
    NONE;

    @JsonValue
    public String toJson() {
        return name(); // หรือจะ return "Player" ก็ได้
    }
}

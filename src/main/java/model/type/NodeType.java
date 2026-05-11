package main.java.model.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum NodeType {
    SMALL("Small"),
    NOTABLE("Notable"),
    KEYSTONE("Keystone");

    private final String displayName;

    NodeType(String displayName) {
        this.displayName = displayName;
    }

    public String writeAsString() {
        return displayName;
    }

    @JsonValue
    public String toJson() {
        return name(); // หรือจะ return "Player" ก็ได้
    }
}

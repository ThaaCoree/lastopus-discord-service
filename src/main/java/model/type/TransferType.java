package model.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TransferType {
    CONVERSION,
    GAIN;


    @JsonValue
    public String toJson() {
        return name(); // หรือจะ return "Player" ก็ได้
    }
}

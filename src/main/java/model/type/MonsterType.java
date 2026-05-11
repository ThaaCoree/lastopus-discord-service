package model.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MonsterType {
    NORMAL,
    ELITE,
    BOSS,
    OPUS_HOLDER;

    public String writeAsString() {
        switch (this) {
            case NORMAL: return "Normal";
            case ELITE: return "Elite";
            case BOSS: return "Boss";
            case OPUS_HOLDER: return "Opus Holder";
            default: return name();
        }
    }

    @JsonValue
    public String toJson() {
        return name(); // หรือจะ return "Player" ก็ได้
    }
}

package main.java.model.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CounterName {
    FLICKER("Flicker"),
    DOOM("Doom"),
    BLACK_SCYTHE_OF_THE_COMET("Black Scythe of the Comet"),
    EXECRATION_EXECUTOR("Execration Executor"),
    SHRUNKEN_KNOWLEDGES("Shrunken Knowledges"),
    THE_FORGOTTEN_PAGES("The Forgotten Pages"),
    USED_MANA("Used Mana"),
    CONNECT_STRIKE("Connect Strike"),
    CERTAINTY_REWRITE("Certainty Rewrite"),
    AELVA_RIND("Aelva Rind"),
    AELVA_RIND_THIS_TURN("Aelva Rind (This Turn)"),
    SHADOW_TOTEM("Shadow Totem"),
    MOONLIGHT_SONATA("Moonlight Sonata"),
    FRAGMENT_OF_SEIDR("Fragment of Seidr"),
    PAPER("Paper"),
    GALDR("Galdr"),
    IllusionAndDream("Illusion & Dream"),
    TO_BEYOND("To Beyond"),
    PROVIDENCE("Providence"),
    SERAPHIM("Seraphim"),
    CHERUBIM("Cherubim"),
    OPHANIM("Ophanim"),
    STORM_DANCE("Storm Dance"),
    FOCUS_FIRE("Focus Fire"),
    MOONDANCE("Moondance"),
    IN_DEPTH("In Depth"),
    SOUL_DRAINED("Soul Drained"),
    GRIM_FEAST("Grim Feast"),
    LUMEN_DISTORT("Lumen Distort"),
    MARK_OF_THE_BLOOD_FANG("Mark of the Blood Fang"),
    FATE_CHARGE("Fate Charge"),
    ABNORMAL_EVOLUTION("Abnormal Evolution"),
    WING("Wing"),
    LIMB("Limb"),
    HALO("Halo"),
    FEATHER_PROTECT("Feather Protect"),
    CLAWS("Claws"),
    FANGS("Fang"),
    DESPAIR("Despair"),
    RAPID_SLASH("Rapid Slash"),
    IMPALE("Impale"),
    GATHERING_STORM("Gathering Storm"),
    REFENTIO("Refentio"),
    INCANTATION("Incantation");

    private final String displayName;

    CounterName(String displayName) {
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

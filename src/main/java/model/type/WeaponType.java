package main.java.model.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum WeaponType {
    NOT_A_WEAPON("Not a Weapon"),
    BOW("Bow"),
    QUIVER("Quiver"),
    SWORD("Sword"),
    JAVELIN("Javelin"),
    KNIFE("Knife"),
    SHIELD("Shield"),
    LONGSWORD("Longsword"),
    GAUNTLET("Gauntlet"),
    FLAIL("Flail"),
    WARHAMMER("Warhammer"),
    CHAKRAM("Chakram"),
    BATON("Baton"),
    SCYTHE("Scythe"),
    WHIP("Whip"),
    CROSSBOW("Crossbow"),
    WAND("Wand"),
    STAFF("Staff"),
    MAGIC_ORB("Magic Orb"),
    MAGIC_BOOK("Magic Book"),
    THROWING("Throwing"),
    QUARTERSTAFF("Quarterstaff"),
    LONGBOW("Longbow"),
    GREATSWORD("Greatsword"),
    LANTERN("Lantern"),
    MIRROR("Mirror"),
    CHAIN("Chain"),
    PISTOL("Pistol"),
    RPG("RPG"),
    SNIPER_RIFLE("Sniper Rifle"),
    KATAR("Katar"),
    TALISMAN("Talisman");

    private final String displayName;

    WeaponType(String displayName) {
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

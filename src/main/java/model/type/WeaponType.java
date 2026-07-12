package model.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum WeaponType {
    NOT_A_WEAPON("Not a Weapon", HandType.NONE),
    BOW("Bow", HandType.TWO_HANDED),
    QUIVER("Quiver", HandType.ONE_HANDED),
    SWORD("Sword", HandType.ONE_HANDED),
    JAVELIN("Javelin", HandType.ONE_HANDED),
    KNIFE("Knife", HandType.ONE_HANDED),
    SHIELD("Shield", HandType.ONE_HANDED),
    LONGSWORD("Longsword", HandType.TWO_HANDED),
    GAUNTLET("Gauntlet", HandType.TWO_HANDED),
    FLAIL("Flail", HandType.TWO_HANDED),
    WARHAMMER("Warhammer", HandType.TWO_HANDED),
    CHAKRAM("Chakram", HandType.TWO_HANDED),
    BATON("Baton", HandType.TWO_HANDED),
    SCYTHE("Scythe", HandType.TWO_HANDED),
    WHIP("Whip", HandType.TWO_HANDED),
    CROSSBOW("Crossbow", HandType.TWO_HANDED),
    WAND("Wand", HandType.ONE_HANDED),
    STAFF("Staff", HandType.TWO_HANDED),
    MAGIC_ORB("Magic Orb", HandType.ONE_HANDED),
    MAGIC_BOOK("Magic Book", HandType.ONE_HANDED),
    THROWING("Throwing", HandType.TWO_HANDED),
    QUARTERSTAFF("Quarterstaff", HandType.TWO_HANDED),
    LONGBOW("Longbow", HandType.TWO_HANDED),
    GREATSWORD("Greatsword", HandType.TWO_HANDED),
    LANTERN("Lantern", HandType.TWO_HANDED),
    MIRROR("Mirror", HandType.ONE_HANDED),
    CHAIN("Chain", HandType.ONE_HANDED),
    PISTOL("Pistol", HandType.ONE_HANDED),
    RPG("RPG", HandType.TWO_HANDED),
    SNIPER_RIFLE("Sniper Rifle", HandType.TWO_HANDED),
    KATAR("Katar", HandType.ONE_HANDED),
    TALISMAN("Talisman", HandType.ONE_HANDED),
    TURRET("Turret", HandType.TWO_HANDED);

    private final String displayName;
    private HandType handType;

    WeaponType(String displayName, HandType handType) {
        this.displayName = displayName;
        this.handType = handType;
    }

    public boolean twoHanded() {
        return handType == HandType.TWO_HANDED;
    }

    public String writeAsString() {
        return displayName;
    }

    @JsonValue
    public String toJson() {
        return name(); // หรือจะ return "Player" ก็ได้
    }
}

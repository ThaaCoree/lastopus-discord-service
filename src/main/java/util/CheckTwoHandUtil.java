package util;

import model.entity.items.Equipment;
import model.type.HandType;
import model.type.WeaponType;

import java.util.LinkedHashMap;
import java.util.Map;

public class CheckTwoHandUtil {

    private static Map<WeaponType, HandType> match = new LinkedHashMap<>();

    static {
        match.put(WeaponType.NOT_A_WEAPON, HandType.NONE);
        match.put(WeaponType.BOW, HandType.TWO_HANDED);
        match.put(WeaponType.QUIVER, HandType.NONE);
        match.put(WeaponType.SWORD, HandType.ONE_HANDED);
        match.put(WeaponType.JAVELIN, HandType.ONE_HANDED);
        match.put(WeaponType.KNIFE, HandType.ONE_HANDED);
        match.put(WeaponType.SHIELD, HandType.ONE_HANDED);
        match.put(WeaponType.LONGSWORD, HandType.TWO_HANDED);
        match.put(WeaponType.GAUNTLET, HandType.TWO_HANDED);
        match.put(WeaponType.FLAIL, HandType.ONE_HANDED);
        match.put(WeaponType.WARHAMMER, HandType.TWO_HANDED);
        match.put(WeaponType.CHAKRAM, HandType.TWO_HANDED);
        match.put(WeaponType.BATON, HandType.TWO_HANDED);
        match.put(WeaponType.SCYTHE, HandType.TWO_HANDED);
        match.put(WeaponType.WHIP, HandType.ONE_HANDED);
        match.put(WeaponType.CROSSBOW, HandType.TWO_HANDED);
        match.put(WeaponType.WAND, HandType.ONE_HANDED);
        match.put(WeaponType.STAFF, HandType.TWO_HANDED);
        match.put(WeaponType.MAGIC_ORB, HandType.ONE_HANDED);
        match.put(WeaponType.MAGIC_BOOK, HandType.ONE_HANDED);
        match.put(WeaponType.THROWING, HandType.TWO_HANDED);
        match.put(WeaponType.QUARTERSTAFF, HandType.TWO_HANDED);
        match.put(WeaponType.LONGBOW, HandType.TWO_HANDED);
        match.put(WeaponType.GREATSWORD, HandType.TWO_HANDED);
        match.put(WeaponType.LANTERN, HandType.TWO_HANDED);
        match.put(WeaponType.MIRROR, HandType.ONE_HANDED);
        match.put(WeaponType.CHAIN, HandType.ONE_HANDED);
        match.put(WeaponType.PISTOL, HandType.ONE_HANDED);
        match.put(WeaponType.RPG, HandType.TWO_HANDED);
        match.put(WeaponType.SNIPER_RIFLE, HandType.TWO_HANDED);
        match.put(WeaponType.KATAR, HandType.ONE_HANDED);
        match.put(WeaponType.TALISMAN, HandType.ONE_HANDED);
    }

    public static boolean isTwoHanded(Equipment equipment) {
        return match.get(equipment.getWeaponType()) == HandType.TWO_HANDED;
    }

    public static boolean isOneHanded(Equipment equipment) {
        return match.get(equipment.getWeaponType()) == HandType.ONE_HANDED;
    }

}

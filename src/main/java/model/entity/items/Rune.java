package model.entity.items;

import com.fasterxml.jackson.annotation.JsonIgnore;
import model.entity.ModifierBundle;
import model.entity.skills.SkillInstance;
import model.entity.units.Unit;
import model.modifier.BasicModifier;
import model.modifier.TransferModifier;
import model.type.*;
import util.StatTranslateUtil;
import util.WeightedRandom;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Rune extends Item {
    private ModifierBundle modifiers = new ModifierBundle();
    private Map<String, SkillInstance> skills = new HashMap<>();
    private boolean[][] shape;
    private boolean unique_rune;
    private int baseRow;
    private int baseCol;

    public Rune(String name) {
        this.setName(name);
        setItemType(ItemType.RUNE);
        setDescription("");
        setStatusDescription("");
        setLore("");
        setPrice("");
        shape = new boolean[][]{
                {false,true, true},
                {true, true, false}
        };
        unique_rune = false;
        //default shape = S
    }

    public Rune() {
        super("");
    }

    public void promptShape(String name) {
        if (name.equals("S")) {
            this.shape = new boolean[][]{
                    {false,true, true},
                    {true, true, false}
            };
        }

        if (name.equals("Z")) {
            this.shape = new boolean[][]{
                    {true,true, false},
                    {false, true, true}
            };
        }

        if (name.equals("J")) {
            this.shape = new boolean[][]{
                    {false,true},
                    {false,true},
                    {true, true}
            };
        }

        if (name.equals("L")) {
            this.shape = new boolean[][]{
                    {true, false},
                    {true, false},
                    {true, true}
            };
        }

        if (name.equals("T")) {
            this.shape = new boolean[][]{
                    {true,true, true},
                    {false, true, false}
            };
        }

        if (name.equals("3")) {
            this.shape = new boolean[][]{
                    {true,true, true}
            };
        }

        if (name.equals("2")) {
            this.shape = new boolean[][]{
                    {true, true}
            };
        }

        if (name.equals("1")) {
            this.shape = new boolean[][]{
                    {true}
            };
        }

        if (name.equals("Corner")) {
            this.shape = new boolean[][]{
                    {true, false},
                    {true, true}
            };
        }

        if (name.equals("Cube")) {
            this.shape = new boolean[][]{
                    {true, true},
                    {true, true}
            };
        }

        if (name.equals("Flower")) {
            this.shape = new boolean[][]{
                    {false,true, false},
                    {true, true, true},
                    {false,true, false}
            };
        }

        if (name.equals("Italic")) {
            this.shape = new boolean[][]{
                    {false,false, true},
                    {false, true, false},
                    {true,false, false}
            };
        }

        if (name.equals("C")) {
            this.shape = new boolean[][]{
                    {true,true, false},
                    {true, false, false},
                    {true,true, false}
            };
        }

        if (name.equals("5")) {
            this.shape = new boolean[][]{
                    {true,true,true,true,true}
            };
        }

        if (name.equals("X")) {
            this.shape = new boolean[][]{
                    {true,false, true},
                    {false, true, false},
                    {true,false, true}
            };
        }
    }

    @JsonIgnore
    public String getShapeName() {
        for (int i = 0; i<4 ; i++) {
            if (Arrays.deepEquals(this.shape, new boolean[][]{
                    {false,true, true},
                    {true, true, false}
            })) {
                return "S";
            }
            if (Arrays.deepEquals(this.shape, new boolean[][]{
                    {true,true, false},
                    {false, true, true}
            })) {
                return "Z";
            }
            if (Arrays.deepEquals(this.shape, new boolean[][]{
                    {false,true},
                    {false,true},
                    {true, true}
            })) {
                return "J";
            }
            if (Arrays.deepEquals(this.shape, new boolean[][]{
                    {true, false},
                    {true, false},
                    {true, true}
            })) {
                return "L";
            }
            if (Arrays.deepEquals(this.shape, new boolean[][]{
                    {true,true, true},
                    {false, true, false}
            })) {
                return "T";
            }
            if (Arrays.deepEquals(this.shape, new boolean[][]{
                    {true,true, true}
            })) {
                return "3";
            }
            if (Arrays.deepEquals(this.shape, new boolean[][]{
                    {true,true}
            })) {
                return "2";
            }
            if (Arrays.deepEquals(this.shape, new boolean[][]{
                    {true}
            })) {
                return "1";
            }
            if (Arrays.deepEquals(this.shape, new boolean[][]{
                    {true, false},
                    {true, true}
            })) {
                return "Corner";
            }
            if (Arrays.deepEquals(this.shape, new boolean[][]{
                    {true, true},
                    {true, true}
            })) {
                return "Cube";
            }
            if (Arrays.deepEquals(this.shape, new boolean[][]{
                    {false, true, false},
                    {true, true, true},
                    {false, true, false}
            })) {
                return "Flower";
            }
            if (Arrays.deepEquals(this.shape, new boolean[][]{
                    {false, false, true},
                    {false, true, false},
                    {true, false, false}
            })) {
                return "Italic";
            }
            if (Arrays.deepEquals(this.shape, new boolean[][]{
                    {true, true, false},
                    {true, false, false},
                    {true, true, false}
            })) {
                return "C";
            }
            if (Arrays.deepEquals(this.shape, new boolean[][]{
                    {true, true, true, true ,true}
            })) {
                return "5";
            }
            if (Arrays.deepEquals(this.shape, new boolean[][]{
                    {true, false, true},
                    {false, true, false},
                    {true, false, true}
            })) {
                return "X";
            }

            rotate90(this);
        }

        return "No Match Shape";
    }

    public static void rotate90(Rune rune) {
        boolean[][] shape = rune.getShape();
        int rows = shape.length;
        int cols = shape[0].length;

        boolean[][] result = new boolean[cols][rows];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                result[c][rows - 1 - r] = shape[r][c];
            }
        }

        rune.setShape(result);
    }

    public int occupying_slots() {
        int count = 0;

        for (boolean[] row : shape) {
            for (boolean cell : row) {
                if (cell) count++;
            }
        }

        return count;
    }

    public boolean isUnique_rune() {
        return unique_rune;
    }

    public void setUnique_rune(boolean unique_rune) {
        this.unique_rune = unique_rune;
    }

    public ModifierBundle getModifiers() {
        return modifiers;
    }

    public Map<StatType, BasicModifier> getStatModifiers() {
        return modifiers.getStatModifiers();
    }

    public Map<StatusType, BasicModifier> getStatusModifiers() {
        return modifiers.getStatusModifiers();
    }

    public Map<Integer, TransferModifier> getTransferModifiers() {
        return modifiers.getTransferModifiers();
    }

    public Map<String, SkillInstance> getSkills() {
        return skills;
    }

    public boolean[][] getShape() {
        return shape;
    }

    public void setShape(boolean[][] shape) {
        this.shape = shape;
    }

    public int getBaseRow() {
        return baseRow;
    }

    public void setBaseRow(int baseRow) {
        this.baseRow = baseRow;
    }

    public int getBaseCol() {
        return baseCol;
    }

    public void setBaseCol(int baseCol) {
        this.baseCol = baseCol;
    }

    public void randomise_stats(Unit unit) {
        this.modifiers = random_modifiers(unit, occupying_slots());

        //translate stats
        setStatusDescription(StatTranslateUtil.translateStatusDesc(modifiers, skills));
    }

    public static Rune randomRune(Unit unit) {
        WeightedRandom<String> random_shape = new WeightedRandom<>();
        random_shape.add("S", 15);
        random_shape.add("Z", 15);
        random_shape.add("J", 15);
        random_shape.add("L", 15);
        random_shape.add("T", 15);
        random_shape.add("Cube", 15);

        random_shape.add("Italic", 25);
        random_shape.add("Corner", 25);
        random_shape.add("3", 25);

        random_shape.add("2", 20);
        random_shape.add("1", 15);

        random_shape.add("5", 5);
        random_shape.add("Flower", 5);
        random_shape.add("C", 5);
        random_shape.add("X", 5);

        random_shape.add("Unique", 1);

        if (random_shape.roll().equals("Unique")) {
            Rune rune = new Rune();
            return rune;
        } else {
            Rune rune = new Rune();
            random_shape.remove("Unique");
            String shape_name = random_shape.roll();
            rune.promptShape(shape_name);
            rune.randomise_stats(unit);
            if (shape_name.equals("1")) {
                rune.setName("One-Block Rune");
            } else if (shape_name.equals("2")) {
                rune.setName("Two-Block Rune");
            } else if (shape_name.equals("3")) {
                rune.setName("Three-Block Rune");
            } else if (shape_name.equals("5")) {
                rune.setName("Five-Block Rune");
            } else {
                rune.setName(shape_name+"-Shape Rune");
            }
            return rune;
        }
    }

    private ModifierBundle random_modifiers(Unit unit, int blocks) {
        ModifierBundle bundle = new ModifierBundle();
        WeightedRandom<StatusType> status_random = new WeightedRandom<>();
        status_random.add(StatusType.STRENGTH, 7);
        status_random.add(StatusType.AGILITY, 10);
        status_random.add(StatusType.VITALITY, 10);
        status_random.add(StatusType.DEXTERITY, 7);
        status_random.add(StatusType.WISDOM, 10);
        status_random.add(StatusType.INTELLIGENCE, 7);
        status_random.add(StatusType.LUCK, 4);

        WeightedRandom<StatType> stat_random = new WeightedRandom<>();
        stat_random.add(StatType.HEALTHPOINT, 10);
        stat_random.add(StatType.MANAPOINT, 10);

        stat_random.add(StatType.HEALTHREGEN, 2);

        stat_random.add(StatType.PHYSICALATTACK, 10);
        stat_random.add(StatType.MAGICALATTACK, 10);
        stat_random.add(StatType.RANGEDATTACK, 10);

        stat_random.add(StatType.PHYSICALDEFENSE, 10);
        stat_random.add(StatType.MAGICALDEFENSE, 10);

        stat_random.add(StatType.MOVEMENTSPEED, 5);

        stat_random.add(StatType.HEALAMPLIFIER, 8);
        stat_random.add(StatType.BUFFAMPLIFIER, 7);
        stat_random.add(StatType.DEBUFFAMPLIFIER, 7);

        stat_random.add(StatType.CRITDAMAGE, 7);
        stat_random.add(StatType.CRITSHIELD, 4);

        stat_random.add(StatType.POISONAMP, 3);
        stat_random.add(StatType.IGNITEAMP, 3);
        stat_random.add(StatType.BLEEDAMP, 3);

        stat_random.add(StatType.ACCURACY, 8);
        stat_random.add(StatType.EVASION, 8);

        stat_random.add(StatType.PHYSICALBLOCK, 5);
        stat_random.add(StatType.MAGICALBLOCK, 5);

        stat_random.add(StatType.DAMAGEREDUCTION, 1);
        stat_random.add(StatType.DAMAGEAMPLIFIER, 1);

        stat_random.add(StatType.ATTACKSPEED, 5);
        stat_random.add(StatType.CASTSPEED, 5);

        stat_random.add(StatType.PHYSICALPENETRATE, 7);
        stat_random.add(StatType.MAGICALPENETRATE, 7);
        stat_random.add(StatType.RESERVATION, 2);

        stat_random.add(StatType.SPEED, 3);
        stat_random.add(StatType.DEBUFFRESISTANCE, 3);

        WeightedRandom<List<Double>> range_random = new WeightedRandom<>();

        Map<StatType, Double> min_stat = Map.ofEntries(
                Map.entry(StatType.HEALTHPOINT,       12.0),
                Map.entry(StatType.MANAPOINT,         0.7),
                Map.entry(StatType.HEALTHREGEN,        5.0),
                Map.entry(StatType.PHYSICALATTACK,    1.4),
                Map.entry(StatType.MAGICALATTACK,     1.4),
                Map.entry(StatType.RANGEDATTACK,      1.4),
                Map.entry(StatType.PHYSICALDEFENSE,   8.0),
                Map.entry(StatType.MAGICALDEFENSE,    8.0),
                Map.entry(StatType.MOVEMENTSPEED,      0.25),
                Map.entry(StatType.HEALAMPLIFIER,      0.7),
                Map.entry(StatType.BUFFAMPLIFIER,      0.35),
                Map.entry(StatType.DEBUFFAMPLIFIER,    0.35),
                Map.entry(StatType.CRITDAMAGE,         3.0),
                Map.entry(StatType.CRITSHIELD,         1.0),
                Map.entry(StatType.POISONAMP,          0.25),
                Map.entry(StatType.IGNITEAMP,          0.25),
                Map.entry(StatType.BLEEDAMP,           0.25),
                Map.entry(StatType.ACCURACY,           11.0),
                Map.entry(StatType.EVASION,            11.0),
                Map.entry(StatType.PHYSICALBLOCK,      1.5),
                Map.entry(StatType.MAGICALBLOCK,       1.5),
                Map.entry(StatType.DAMAGEREDUCTION,    0.5),
                Map.entry(StatType.DAMAGEAMPLIFIER,    0.5),
                Map.entry(StatType.ATTACKSPEED,        0.75),
                Map.entry(StatType.CASTSPEED,          0.75),
                Map.entry(StatType.PHYSICALPENETRATE,  2.2),
                Map.entry(StatType.MAGICALPENETRATE,   2.2),
                Map.entry(StatType.RESERVATION,        -0.25),
                Map.entry(StatType.SPEED,              0.14),
                Map.entry(StatType.DEBUFFRESISTANCE,   0.5)
        );

        Map<StatType, Double> max_stat = Map.ofEntries(
                Map.entry(StatType.HEALTHPOINT,       48.0),
                Map.entry(StatType.MANAPOINT,         2.8),
                Map.entry(StatType.HEALTHREGEN,        20.0),
                Map.entry(StatType.PHYSICALATTACK,    4.8),
                Map.entry(StatType.MAGICALATTACK,     4.8),
                Map.entry(StatType.RANGEDATTACK,      4.8),
                Map.entry(StatType.PHYSICALDEFENSE,   32.0),
                Map.entry(StatType.MAGICALDEFENSE,    32.0),
                Map.entry(StatType.MOVEMENTSPEED,      1.0),
                Map.entry(StatType.HEALAMPLIFIER,      2.8),
                Map.entry(StatType.BUFFAMPLIFIER,      1.4),
                Map.entry(StatType.DEBUFFAMPLIFIER,    1.4),
                Map.entry(StatType.CRITDAMAGE,         12.0),
                Map.entry(StatType.CRITSHIELD,         4.0),
                Map.entry(StatType.POISONAMP,          1.0),
                Map.entry(StatType.IGNITEAMP,          1.0),
                Map.entry(StatType.BLEEDAMP,           1.0),
                Map.entry(StatType.ACCURACY,           44.0),
                Map.entry(StatType.EVASION,            44.0),
                Map.entry(StatType.PHYSICALBLOCK,      6.0),
                Map.entry(StatType.MAGICALBLOCK,       6.0),
                Map.entry(StatType.DAMAGEREDUCTION,    2.0),
                Map.entry(StatType.DAMAGEAMPLIFIER,    2.0),
                Map.entry(StatType.ATTACKSPEED,        3.0),
                Map.entry(StatType.CASTSPEED,          3.0),
                Map.entry(StatType.PHYSICALPENETRATE,  8.8),
                Map.entry(StatType.MAGICALPENETRATE,   8.8),
                Map.entry(StatType.RESERVATION,        -1.0),
                Map.entry(StatType.SPEED,              0.48),
                Map.entry(StatType.DEBUFFRESISTANCE,   2.0)
        );

        WeightedRandom<Boolean> mod_type = new WeightedRandom<>();
        mod_type.add(true, 1);
        mod_type.add(false, 3);

        for (int i = 0; i < blocks; i++) {
            range_random.clear();
            if (mod_type.roll()) {
                double min = 1;
                double max = 4;
                range_random.add(range(minRangeCalculate(unit, 1, min), maxRangeCalculate(unit, 1, max)), 5);
                if (blocks > 1) {
                    range_random.add(range(minRangeCalculate(unit, 2, min), maxRangeCalculate(unit, 2, max)), 3);
                    if (blocks > 2) {
                        range_random.add(range(minRangeCalculate(unit, 3, min), maxRangeCalculate(unit, 3, max)), 3);
                        if (blocks > 3) {
                            range_random.add(range(minRangeCalculate(unit, 4, min), maxRangeCalculate(unit, 4, max)), 2);
                            if (blocks > 4) {
                                range_random.add(range(minRangeCalculate(unit, 5, min), maxRangeCalculate(unit, 5, max)), 1);
                            }
                        }
                    }
                }
                List<Double> min_max = range_random.roll();
                int min_roll = min_max.get(0).intValue();
                int max_roll = min_max.get(1).intValue();
                int status_roll = ThreadLocalRandom.current().nextInt(min_roll, max_roll);
                bundle.getStatusModifierSafe(status_random.roll()).sumFlat(status_roll);

            } else {
                StatType statType = stat_random.roll();
                double stat_min = min_stat.get(statType);
                double stat_max = max_stat.get(statType);
                double stat_roll;
                range_random.add(range(minRangeCalculate(unit, 1, stat_min), maxRangeCalculate(unit, 1, stat_max)), 5);
                if (blocks > 1) {
                    range_random.add(range(minRangeCalculate(unit, 2, stat_min), maxRangeCalculate(unit, 2, stat_max)), 3);
                    if (blocks > 2) {
                        range_random.add(range(minRangeCalculate(unit, 3, stat_min), maxRangeCalculate(unit, 3, stat_max)), 3);
                        if (blocks > 3) {
                            range_random.add(range(minRangeCalculate(unit, 4, stat_min), maxRangeCalculate(unit, 4, stat_max)), 2);
                            if (blocks > 4) {
                                range_random.add(range(minRangeCalculate(unit, 5, stat_min), maxRangeCalculate(unit, 5, stat_max)), 1);
                            }
                        }
                    }
                }
                List<Double> min_max_stat = range_random.roll();
                double stat_min_roll = min_max_stat.get(0);
                double stat_max_roll = min_max_stat.get(1);
                if (statType == StatType.RESERVATION) {
                    stat_roll = ThreadLocalRandom.current().nextDouble(stat_max_roll, stat_min_roll);
                } else {
                    stat_roll = ThreadLocalRandom.current().nextDouble(stat_min_roll, stat_max_roll);
                }
                if (statType == StatType.ATTACKSPEED || statType == StatType.RESERVATION || statType == StatType.CASTSPEED || statType == StatType.CRITCHANCE || statType == StatType.CRITDAMAGE
                        || statType ==  StatType.CRITSHIELD || statType == StatType.HEALAMPLIFIER || statType == StatType.BUFFAMPLIFIER || statType == StatType.DEBUFFAMPLIFIER || statType == StatType.DAMAGEAMPLIFIER
                        || statType == StatType.DAMAGEREDUCTION || statType == StatType.POISONAMP || statType == StatType.IGNITEAMP || statType == StatType.BLEEDAMP
                        || statType == StatType.IGNOREPDEF || statType == StatType.IGNOREMDEF || statType == StatType.DEBUFFRESISTANCE) {
                    stat_roll /= 100;
                } else {
                    if (statType == StatType.MOVEMENTSPEED || statType == StatType.SPEED) {
                    } else {
                        stat_roll = Math.floor(stat_roll);
                    }
                }

                bundle.getStatModifierSafe(statType).sumFlat(stat_roll);
            }
        }

        return bundle;
    }

    private static Double minRangeCalculate(Unit unit, int blocks, double number) {
        double to_return = number;
        to_return *= ((double) unit.getLevel() /16);
        to_return *= Math.pow(1.75, blocks-1);
        return to_return;
    }

    private static Double maxRangeCalculate(Unit unit, int blocks, double number) {
        double to_return = number;
        to_return *= ((double) unit.getLevel() /16);
        to_return *= Math.pow(1.4, blocks-1);
        return to_return;
    }

    private static List<Double> range(double min, double max) {
        return List.of(min, max);
    }

    @Override
    public String toString() {
        return "Equipment{" +
                "isUnique=" + isUnique_rune() +
                ", modifiers=" + modifiers.getStatModifiers() +
                ", " + modifiers.getStatusModifiers() +
                ", " + modifiers.getTransferModifiers() +
                "}";
    }
}

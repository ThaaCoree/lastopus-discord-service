package model.entity.items;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import model.entity.ModifierBundle;
import model.entity.skills.SkillInstance;
import model.modifier.BasicModifier;
import model.modifier.TransferModifier;
import model.type.*;
import util.StatTranslateUtil;
import util.WeightedRandom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Rune extends Item {
    private ModifierBundle modifiers = new ModifierBundle();
    private final Map<String, SkillInstance> skills = new HashMap<>();
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

        if (name.equals("C")) {
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
                return "C";
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

    public void randomise_stats() {
        if (occupying_slots() == 1) {
            this.modifiers = one_block_random();
        }

        //translate stats
        setStatusDescription(StatTranslateUtil.translateStatusDesc(modifiers, skills));
    }

    private ModifierBundle one_block_random() {
        ModifierBundle bundle = new ModifierBundle();
        WeightedRandom<StatusType> status_random = new WeightedRandom<>();
        status_random.add(StatusType.STRENGTH, 5);
        status_random.add(StatusType.AGILITY, 5);
        status_random.add(StatusType.VITALITY, 5);
        status_random.add(StatusType.DEXTERITY, 5);
        status_random.add(StatusType.WISDOM, 5);
        status_random.add(StatusType.INTELLIGENCE, 5);
        status_random.add(StatusType.LUCK, 3);

        WeightedRandom<Double> number_random = new WeightedRandom<>();
        number_random.add(1.0, 5);
        number_random.add(2.0, 5);
        number_random.add(3.0, 3);
        number_random.add(4.0, 1);
        bundle.getStatusModifierSafe(status_random.roll()).setFlat(number_random.roll());

        return bundle;
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

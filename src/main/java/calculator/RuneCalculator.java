package calculator;

import model.entity.ModifierBundle;
import model.entity.items.Rune;
import model.entity.units.Unit;
import model.modifier.TransferModifier;
import model.type.StatType;
import model.type.StatusType;

import java.util.*;

public class RuneCalculator {
    public static ModifierBundle calculateRuneForUnit(Unit unit) {
        ModifierBundle modifierBundle = new ModifierBundle();
        List<Rune> runeModifiers = calculateRuneModifiers(unit);
        for (Rune socketedRune : runeModifiers) {
            //status
            for (StatusType statusType : StatusType.values()) {
                if (socketedRune.getModifiers().getStatusModifiers().get(statusType) == null) continue;
                modifierBundle.getStatusModifierSafe(statusType).sumFlat(
                        socketedRune.getModifiers().getStatusModifiers().get(statusType).getFlat()
                );
                modifierBundle.getStatusModifierSafe(statusType).sumMult(
                        socketedRune.getModifiers().getStatusModifiers().get(statusType).getMult()
                );
                modifierBundle.getStatusModifierSafe(statusType).sumGlobalMult(
                        socketedRune.getModifiers().getStatusModifiers().get(statusType).getGlobalMult()
                );
                modifierBundle.getStatusModifierSafe(statusType).sumPassiveMult(
                        socketedRune.getModifiers().getStatusModifiers().get(statusType).getPassiveMult()
                );
                modifierBundle.getStatusModifierSafe(statusType).setOverride(
                        socketedRune.getModifiers().getStatusModifiers().get(statusType).getOverride()
                );
            }

            //stats
            for (StatType statType : StatType.values()) {
                if (socketedRune.getModifiers().getStatModifiers().get(statType) == null) continue;
                modifierBundle.getStatModifierSafe(statType).sumFlat(
                        socketedRune.getModifiers().getStatModifiers().get(statType).getFlat()
                );
                modifierBundle.getStatModifierSafe(statType).sumMult(
                        socketedRune.getModifiers().getStatModifiers().get(statType).getMult()
                );
                modifierBundle.getStatModifierSafe(statType).sumGlobalMult(
                        socketedRune.getModifiers().getStatModifiers().get(statType).getGlobalMult()
                );
                modifierBundle.getStatModifierSafe(statType).sumPassiveMult(
                        socketedRune.getModifiers().getStatModifiers().get(statType).getPassiveMult()
                );
                modifierBundle.getStatModifierSafe(statType).setOverride(
                        socketedRune.getModifiers().getStatModifiers().get(statType).getOverride()
                );
            }

            for (TransferModifier transferModifier : socketedRune.getModifiers().getTransferModifiers().values()) {
                modifierBundle.addTransferModifier(transferModifier);
            }
        }
        return modifierBundle;
    }


    public static List<Rune> calculateRuneModifiers(Unit unit) {
        // ต้องไม่ลืม deep copy
        List<Rune> list = new ArrayList<>();
        for (Rune socketedRune : unit.getSocketed_runes()) {
            list.add(socketedRune.copy());
        }
        int[][] board = runeboardMapping(unit.getRune_board(), unit.getSocketed_runes());

        calculateGrandSpectrum(list);
        calculateTimeFlow(list, board);
        calculateTalented(list);
        calculateEchoes(list, board);
        calculatePlanetCore(list, board);
        calculateStardiver(list, board, unit);

        return list;
    }

    public static void calculateStardiver(List<Rune> list, int[][] board, Unit unit) {
        int points = 0;
        for (int i = 0; i < list.size(); i++) {
            Rune rune = list.get(i);
            if (!rune.getName().contains("Stardiver")) continue;
            if (points > 0) continue;

            for (int r = 0; r < board.length; r++) {
                for (int c = 0; c < r; c++) {
                    if (board[r][c] != -1) continue;
                    points++;
                }
            }
        }
        unit.setRemainingPassiveTreePoint(unit.getRemainingPassiveTreePoint() + points);
    }

    public static void calculateTimeFlow(List<Rune> list, int[][] board) {
        Map<Integer, Set<String>> spaceAxisDirections = new LinkedHashMap<>();
        for (int i = 0; i < list.size(); i++) {
            Rune rune = list.get(i);
            if (!rune.getName().contains("Time Flow")) continue;
            getAdjacentRuneWithDirection(board, rune, list, spaceAxisDirections);
        }

        for (Map.Entry<Integer, Set<String>> entry : spaceAxisDirections.entrySet()) {
            Rune spaceAxis = list.get(entry.getKey());
            if (!spaceAxis.getName().contains("Space Axis")) continue;

            Set<String> directions = entry.getValue();
            if (directions.contains("UP")) {
                spaceAxis.getModifiers().getStatModifierSafe(StatType.MAGICALATTACK).setGlobalMult(0.06);
            }
            if (directions.contains("DOWN")) {
                spaceAxis.getModifiers().getStatModifierSafe(StatType.HEALTHPOINT).setGlobalMult(0.06);
            }
            if (directions.contains("LEFT")) {
                spaceAxis.getModifiers().getStatModifierSafe(StatType.PHYSICALATTACK).setGlobalMult(0.06);
            }
            if (directions.contains("RIGHT")) {
                spaceAxis.getModifiers().getStatModifierSafe(StatType.RANGEDATTACK).setGlobalMult(0.06);
            }
        }
    }

    public static void calculateEchoes(List<Rune> list, int[][] board) {
        for (int i = 0; i < list.size(); i++) {
            Rune rune = list.get(i);
            if (!rune.getName().contains("Echoes")) continue;
            if (rune.getBaseRow() == 0) continue;
            int index = board[rune.getBaseRow()-1][rune.getBaseCol()];
            if (index < 0) continue; // เพิ่มตรงนี้
            if (list.get(index).getName().contains("Echoes")) continue;
            rune.setModifiers(list.get(index).getModifiers().deepcopy());
        }
    }

    public static void calculatePlanetCore(List<Rune> list, int[][] board) {
        for (int i = 0; i < list.size(); i++) {
            Rune rune = list.get(i);
            if (!rune.getName().contains("Planet Core")) continue;

            Set<Integer> indexes = new LinkedHashSet<>();
            getAdjacentRune(board, rune, indexes, list, true);

            for (int k = 0; k < list.size(); k++) {
                Rune affected = list.get(k);
                if (indexes.contains(k)) {
                    affected.getModifiers().multiplyAllModifiers(2);
                } else {
                    affected.getModifiers().multiplyAllModifiers(0);
                }
            }
        }
    }

    public static void calculateTalented(List<Rune> list) {
        for (int i = 0; i < list.size(); i++) {
            Rune rune = list.get(i);
            if (!rune.getName().contains("Talented")) continue;
            for (int k = 0; k < list.size(); k++) {
                Rune affected = list.get(k);
                affected.getModifiers().multiplyAllStatusModifiers(1.5);
                affected.getModifiers().multiplyAllStatusTransferModifiers(1.5);
            }
        }
    }

    public static void calculateGrandSpectrum(List<Rune> list) {
        // นับจำนวน Grand Spectrum ทั้งหมดก่อน
        long grandSpectrumCount = list.stream()
                .filter(r -> r.getName().contains("Grand Spectrum"))
                .count();

        if (grandSpectrumCount == 0) return;

        for (Rune rune : list) {
            if (!rune.getName().contains("Grand Spectrum")) continue;

            double mult = 0.07 * grandSpectrumCount;

            if (rune.getName().contains("Might")) {
                rune.getModifiers().getStatusModifierSafe(StatusType.STRENGTH).setGlobalMult(mult);
            } else if (rune.getName().contains("Quick")) {
                rune.getModifiers().getStatusModifierSafe(StatusType.AGILITY).setGlobalMult(mult);
            } else if (rune.getName().contains("Tough")) {
                rune.getModifiers().getStatusModifierSafe(StatusType.VITALITY).setGlobalMult(mult);
            } else if (rune.getName().contains("Alert")) {
                rune.getModifiers().getStatusModifierSafe(StatusType.DEXTERITY).setGlobalMult(mult);
            } else if (rune.getName().contains("Insight")) {
                rune.getModifiers().getStatusModifierSafe(StatusType.WISDOM).setGlobalMult(mult);
            } else if (rune.getName().contains("Wise")) {
                rune.getModifiers().getStatusModifierSafe(StatusType.INTELLIGENCE).setGlobalMult(mult);
            } else if (rune.getName().contains("Bless")) {
                rune.getModifiers().getStatusModifierSafe(StatusType.LUCK).setGlobalMult(mult);
            }
        }
    }

    public static int[][] runeboardMapping(boolean[][] runeboard, List<Rune> runes) {
        int rows = runeboard.length;
        int cols = runeboard[0].length;
        int[][] grid = new int[rows][cols];

        // Step 1: fill จาก runeboard
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = runeboard[r][c] ? -1 : -2;
            }
        }

        // Step 2: วาง rune แต่ละตัวลงไป
        for (int i = 0; i < runes.size(); i++) {
            Rune rune = runes.get(i);
            boolean[][] shape = rune.getShape();

            for (int dr = 0; dr < shape.length; dr++) {
                for (int dc = 0; dc < shape[dr].length; dc++) {
                    if (!shape[dr][dc]) continue;

                    int r = rune.getBaseRow() + dr;
                    int c = rune.getBaseCol() + dc;

                    // bounds check ป้องกัน ArrayIndexOutOfBounds
                    if (r >= 0 && r < rows && c >= 0 && c < cols) {
                        grid[r][c] = i;
                    }
                }
            }
        }

        return grid;
    }

    public static void getAdjacentRune(int[][] board, Rune rune, Set<Integer> set, List<Rune> list, boolean radius) {
        boolean[][] shape = rune.getShape();

        int own_index = -1;

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != rune) continue;
            own_index = i;
        }

        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (!shape[r][c]) continue;

                getAdjacentRuneIndices(board, rune.getBaseRow()+r, rune.getBaseCol()+c, set, own_index, radius);
            }
        }
    }

    public static void getAdjacentRuneIndices(int[][] grid, int row, int col, Set<Integer> set, int excludeIndex, boolean radius) {

        int[] dr;
        int[] dc;

        if (radius) {
            dr = new int[]{-1, -1, -1, 0, 0, 1, 1, 1};
            dc = new int[]{-1, 0, 1, -1, 1, -1, 0, 1};
        } else {
            dr = new int[]{-1, 0, 0, 1};
            dc = new int[]{0, -1, 1, 0};
        }

        for (int d = 0; d < 8; d++) {
            int r = row + dr[d];
            int c = col + dc[d];

            if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length) continue;

            if (grid[r][c] >= 0 && grid[r][c] != excludeIndex) {
                set.add(grid[r][c]);
            }
        }
    }

    public static void getAdjacentRuneWithDirection( int[][] board, Rune rune, List<Rune> list, Map<Integer, Set<String>> result) {
        boolean[][] shape = rune.getShape();
        int ownIndex = list.indexOf(rune);

        int[] dr = {-1,  0,  0,  1};
        int[] dc = { 0, -1,  1,  0};
        String[] dirNames = {"UP", "LEFT", "RIGHT", "DOWN"};

        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (!shape[r][c]) continue;

                int boardRow = rune.getBaseRow() + r;
                int boardCol = rune.getBaseCol() + c;

                for (int d = 0; d < 4; d++) {
                    int nr = boardRow + dr[d];
                    int nc = boardCol + dc[d];

                    if (nr < 0 || nr >= board.length || nc < 0 || nc >= board[0].length) continue;

                    int index = board[nr][nc];
                    if (index < 0 || index == ownIndex) continue;

                    result
                            .computeIfAbsent(index, k -> new LinkedHashSet<>())
                            .add(dirNames[d]);
                }
            }
        }
    }
}

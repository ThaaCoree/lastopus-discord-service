package ui;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import app.Database;
import model.entity.items.Rune;
import model.entity.units.Unit;

import java.util.ArrayList;
import java.util.List;

public class RuneBoardPane extends StackPane {
    private RuneBoardPropertyPanel propertyPanel;
    GridCell[][] cells = new GridCell[6][6];
    private boolean[][] occupied = new boolean[6][6];
    private boolean[][] currentShape;
    private boolean holdingRune = false;
    private Pane ghost;
    int block_size = 125;
    GridPane grid;
    int dragged_col = 0;
    int dragged_row = 0;
    Unit unit;
    private Rune current_rune;

    public RuneBoardPane(Unit unit, Database database) {

        setStyle("-fx-background-color: #2c2c2c;");
        this.unit = unit;

        propertyPanel = new RuneBoardPropertyPanel(unit, this, database);
        grid = buildGrid();
        VBox content_box = new VBox();
        ComboBox<String> base_type = new ComboBox<>();
        base_type.getItems().addAll("None","Standard","1","2","3","4","5","6","7","8","9","10","11","12");
        content_box.getChildren().add(base_type);
        content_box.getChildren().add(grid);

        content_box.setAlignment(Pos.CENTER);
        getChildren().add(content_box);
        setAlignment(Pos.CENTER);

        base_type.setOnAction(e-> {
            chooseSocket(base_type.getValue());
        });

        addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            if (!holdingRune || ghost == null) return;

            Node cell00 = cells[0][0];
            Point2D origin = cell00.localToScene(0, 0);
            Point2D mouse = new Point2D(e.getSceneX(), e.getSceneY());

            double dx = mouse.getX() - origin.getX();
            double dy = mouse.getY() - origin.getY();

            int col = (int)(dx / block_size);
            int row = (int)(dy / block_size);

            Point2D originParent = ghost.getParent().sceneToLocal(origin);

            ghost.setLayoutX(0);
            ghost.setLayoutY(0);

            ghost.setTranslateX(originParent.getX() + col * block_size);
            ghost.setTranslateY(originParent.getY() + row * block_size);

            preview(currentShape, row, col);
            dragged_row = row;
            dragged_col = col;
        });

        refreshContent();
    }

    public RuneBoardPane() {

    }

    public void refreshContent() {
        propertyPanel.refreshContents();

        for (int r = 0; r < occupied.length; r++) {
            for (int c = 0; c < occupied[0].length; c++) {
                boolean[][] board = unit.getRune_board();
                if (board[r][c]) {
                    occupied[r][c] = false;
                } else {
                    occupied[r][c] = true;
                }
            }
        }
        for (int r = 0; r < cells.length; r++) {
            for (int c = 0; c < cells[0].length; c++) {
                cells[r][c].updateStyle();
                boolean[][] board = unit.getRune_board();
                if (board[r][c]) {
                    cells[r][c].setStyle("-fx-background-color: gray;");
                } else {
                    cells[r][c].setStyle("-fx-background-color: black;");
                }
            }
        }

        List<Rune> runes = unit.getSocketed_runes();

        int rune_count = 0;

        for (Rune rune : runes) {
            if (rune == null) continue;
            List<String> colors = new ArrayList<>();
            colors.add("blue");
            colors.add("red");
            colors.add("green");
            colors.add("yellow");
            colors.add("pink");
            colors.add("white");
            colors.add("crimson");

            String color = colors.get(rune_count);

            boolean[][] shape = rune.getShape();

            for (int r = 0; r < shape.length; r++) {
                for (int c = 0; c < shape[0].length; c++) {

                    if (!shape[r][c]) continue;

                    int gr = rune.getBaseRow() + r;
                    int gc = rune.getBaseCol() + c;

                    cells[gr][gc].setStyle("-fx-background-color: " + color + ";");
                }
            }

            rune_count++;
            if (rune_count > colors.toArray().length) {
                rune_count = 0;
            }
        }
    }

    public void placeRune(boolean[][] shape, Rune rune) {
        removeGhost();

        current_rune = rune;
        setCurrentRune(shape);
        createGhost();
    }

    public void setCurrentRune(boolean[][] shape) {
        this.currentShape = cloneShape(shape);
        this.holdingRune = true;
    }

    private void createGhost() {
        if (ghost != null) getChildren().remove(ghost);

        ghost = new Pane();
        ghost.setMouseTransparent(true); // 🔥 สำคัญมาก

        int size = block_size;

        for (int r = 0; r < currentShape.length; r++) {
            for (int c = 0; c < currentShape[0].length; c++) {

                if (!currentShape[r][c]) continue;

                Pane cell = new Pane();
                cell.setPrefSize(size, size);
                cell.setStyle("-fx-background-color: rgba(0,200,0,0.35);");

                cell.setLayoutX(c * size);
                cell.setLayoutY(r * size);

                ghost.getChildren().add(cell);
            }
        }

        getChildren().add(ghost);
    }

    private void putPieceDown() {
        if (!holdingRune || ghost == null) return;

        if (canPlace(currentShape, dragged_row, dragged_col)) {
            place(currentShape, dragged_row, dragged_col);

            holdingRune = false;
            currentShape = null;

            removeGhost();
            clearPreview();
        }
    }

    public void removePiece(int row, int col) {
        if (holdingRune) return;

        Rune rune = unit.findRune(row, col);
        if (rune == null) return;

        for (int r = 0; r < rune.getShape().length; r++) {
            for (int c = 0; c < rune.getShape()[0].length; c++) {

                if (!rune.getShape()[r][c]) continue;

                int br = rune.getBaseRow() + r;
                int bc = rune.getBaseCol() + c;

                occupied[br][bc] = false;
            }
        }

        unit.getSocketed_runes().remove(rune);

        unit.addRuneToInventory(rune);

        refreshContent();
    }

    private void removeGhost() {
        if (ghost != null) {
            getChildren().remove(ghost);
            ghost = null;
        }
    }

    private boolean[][] cloneShape(boolean[][] shape) {
        int rows = shape.length;
        int cols = shape[0].length;

        boolean[][] copy = new boolean[rows][cols];

        for (int r = 0; r < rows; r++) {
            System.arraycopy(shape[r], 0, copy[r], 0, cols);
        }

        return copy;
    }

    private GridPane buildGrid() {
        GridPane grid = new GridPane();

        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                GridCell cell = new GridCell(r, c);

                cells[r][c] = cell; // 🔥 เก็บ reference

                grid.add(cell, c, r);
            }
        }
        grid.setAlignment(Pos.CENTER);
        return grid;
    }

    private int toGridCol(double x) {
        return (int)(x / block_size);
    }

    private int toGridRow(double y) {
        return (int)(y / block_size);
    }

    private int getShapeWidth(boolean[][] shape) {
        return shape[0].length;
    }

    private int getShapeHeight(boolean[][] shape) {
        return shape.length;
    }

    private boolean canPlace(boolean[][] shape, int baseRow, int baseCol) {
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[0].length; c++) {

                if (!shape[r][c]) continue;

                int gr = baseRow + r;
                int gc = baseCol + c;

                if (gr < 0 || gc < 0 || gr >= 6 || gc >= 6)
                    return false;

                if (occupied[gr][gc])
                    return false;
            }
        }
        return true;
    }

    private void place(boolean[][] shape, int baseRow, int baseCol) {

        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[0].length; c++) {

                if (!shape[r][c]) continue;

                int gr = baseRow + r;
                int gc = baseCol + c;

                occupied[gr][gc] = true;
                cells[gr][gc].setFilled(true);
            }
        }
        current_rune.setBaseCol(baseCol);
        current_rune.setBaseRow(baseRow);
        unit.getSocketed_runes().add(current_rune);

        unit.removeRuneFromInventory(current_rune);

        current_rune = null;

        refreshContent();
    }

    private void preview(boolean[][] shape, int baseRow, int baseCol) {
        clearPreview();

        boolean can = canPlace(shape, baseRow, baseCol);

        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[0].length; c++) {

                if (!shape[r][c]) continue;

                int gr = baseRow + r;
                int gc = baseCol + c;

                if (outOfBounds(gr, gc)) continue;

                if (can) {
                    cells[gr][gc].getStyleClass().add("preview-valid");
                } else {
                    cells[gr][gc].getStyleClass().add("preview-invalid");
                }
            }
        }
    }

    private boolean outOfBounds(int r, int c) {
        return r < 0 || c < 0 || r >= cells.length || c >= cells[0].length;
    }

    private void clearPreview() {
        for (GridCell[] row : cells) {
            for (GridCell cell : row) {
                cell.getStyleClass().removeAll("preview-valid", "preview-invalid");
            }
        }
    }

    public RuneBoardPropertyPanel getPropertyPanel() {
        return propertyPanel;
    }

    public void setPropertyPanel(RuneBoardPropertyPanel propertyPanel) {
        this.propertyPanel = propertyPanel;
    }

    class GridCell extends Pane {
        private final int row;
        private final int col;

        private boolean filled;
        private boolean highlighted;

        public GridCell(int row, int col) {
            this.row = row;
            this.col = col;

            setPrefSize(block_size, block_size);
            getStyleClass().add("grid-cell");

            initEvent();
            updateStyle();
        }

        private void initEvent() {
            setOnMouseExited(e -> {
                setHighlighted(false);
            });

            setOnMouseClicked(e -> {
                if (e.getButton().equals(MouseButton.SECONDARY)) {
                    removePiece(row, col);
                    putPieceDown();
                    refreshContent();
                }
            });

            setOnMouseEntered(e -> {
                setHighlighted(true);

                if (!holdingRune) return;
                preview(currentShape, row, col);
            });
        }

        public void setFilled(boolean filled) {
            this.filled = filled;
            updateStyle();
        }

        public void setHighlighted(boolean highlighted) {
            this.highlighted = highlighted;
            updateStyle();
        }

        private void updateStyle() {
            getStyleClass().removeAll("filled", "highlight");

            List<Rune> runes = unit.getSocketed_runes();

            for (Rune rune : runes) {
                if (rune == null) continue;

                boolean[][] shape = rune.getShape();

                for (int r = 0; r < shape.length; r++) {
                    for (int c = 0; c < shape[0].length; c++) {

                        if (!shape[r][c]) continue;

                        int gr = rune.getBaseRow() + r;
                        int gc = rune.getBaseCol() + c;

                        occupied[gr][gc] = true;
                        if (gr == row && gc == col) {
                            filled = true;
                        }
                    }
                }
            }
            if (highlighted) {
                getStyleClass().add("highlight");
            }
        }

        public int getRow() { return row; }
        public int getCol() { return col; }
    }

    public void chooseSocket(String socket_name) {
        propertyPanel.removeAllRunes();

        if (socket_name.equals("None")) {
            unit.setRune_board(new boolean[][]{
                    {false, false, false,  false,  false, false},
                    {false, false,  false,  false,  false,  false},
                    {false,  false,  false,  false,  false,  false },
                    {false,  false,  false,  false,  false,  false },
                    {false, false,  false,  false,  false,  false},
                    {false, false, false,  false,  false, false}
            });
        }

        if (socket_name.equals("Standard")) {
            unit.setRune_board(new boolean[][]{
                    {false, false, true,  true,  false, false},
                    {false, true,  true,  true,  true,  false},
                    {true,  true,  true,  true,  true,  true },
                    {true,  true,  true,  true,  true,  true },
                    {false, true,  true,  true,  true,  false},
                    {false, false, true,  true,  false, false}
            });
        }

        if (socket_name.equals("1")) {
            unit.setRune_board(new boolean[][]{
                    {true , true , true , true , true , true },
                    {true , false, true , true , true , false},
                    {true , false, false, true , false, false},
                    {true , true , true , true , true , true },
                    {true , true , true , true , true , true },
                    {false, false, false, true , true , false},
            });
        }

        if (socket_name.equals("2")) {
            unit.setRune_board(new boolean[][]{
                    {false, true , true , false , true , true },
                    {true , true , true , true , true , false },
                    {true , false , true , false , true , false },
                    {true , false , true , true , true , false },
                    {true , true , false , false , true , true },
                    {true , true , true , true , true , true },
            });
        }

        if (socket_name.equals("3")) {
            unit.setRune_board(new boolean[][]{
                    {false , true , false , true , false , false },
                    {true , true , true , true , true , true },
                    {true , true , true , true , true , true },
                    {false , true , false , true , false , true },
                    {false , true , false , true , false , true },
                    {true , true , true , true , true , true },
            });
        }

        if (socket_name.equals("4")) {
            unit.setRune_board(new boolean[][]{
                    {true , true , true , false , true , false },
                    {true , true , true , true , true , true },
                    {false , false , false , false , true , true },
                    {true , true , true , true , false , true },
                    {true , true , false , true , true , true },
                    {false , true , true , true , true , false },
            });
        }

        if (socket_name.equals("5")) {
            unit.setRune_board(new boolean[][]{
                    {false , true , true , true , true , true },
                    {true , true , false , true , true , true },
                    {true , true , false , true , false , true },
                    {false , false , false , true , true , true },
                    {true , true , true , true , false , true },
                    {true , true , true , true , false , false },
            });
        }

        if (socket_name.equals("6")) {
            unit.setRune_board(new boolean[][]{
                    {true , false , false , true , true , true },
                    {true , true , true , true , true , false },
                    {true , false , true , false , true , false },
                    {false , false , true , true , true , false },
                    {true , true , true , true , true , false },
                    {true , true , true , true , true , true },
            });
        }

        if (socket_name.equals("7")) {
            unit.setRune_board(new boolean[][]{
                    {true , true , true , false , true , true },
                    {true , true , true , true , true , true },
                    {false , true , false , false , true , true },
                    {false , true , true , true , false , true },
                    {false , true , false , true , false , true },
                    {true , true , true , true , true , false },
            });
        }

        if (socket_name.equals("8")) {
            unit.setRune_board(new boolean[][]{
                    {false , true , true , true , true , true },
                    {false , true , true , false , true , true },
                    {true , true , true , false , true , true },
                    {true , true , true , false , false , false },
                    {true , true , true , true , true , false },
                    {true , false , true , false , true , true },
            });
        }

        if (socket_name.equals("9")) {
            unit.setRune_board(new boolean[][]{
                    {false , false , true , false , true , false },
                    {true , true , true , true , true , true },
                    {false , false , true , false , true , false },
                    {true , true , true , true , true , true },
                    {true , false , true , true , false , true },
                    {true , true , true , true , true , true },
            });
        }

        if (socket_name.equals("10")) {
            unit.setRune_board(new boolean[][]{
                    {true , true , true , true , true , false },
                    {true , true , false , false , true , true },
                    {true , true , true , true , true , true },
                    {true , false , true , false , true , false },
                    {true , true , false , false , true , false },
                    {true , true , false , true , true , true },
            });
        }

        if (socket_name.equals("11")) {
            unit.setRune_board(new boolean[][]{
                    {false , false , true , true , true , false },
                    {false , true , true , false , true , true },
                    {true , true , false , true , true , true },
                    {true , false , true , false , true , true },
                    {true , true , true , true , false , true },
                    {false , true , true , true , true , true },
            });
        }

        if (socket_name.equals("12")) {
            unit.setRune_board(new boolean[][]{
                    {true , true , true , true , true , true },
                    {true , true , true , false , true , false },
                    {true , false , true , false , true , false },
                    {false , true , true , true , true , true },
                    {true , true , true , false , true , false },
                    {true , true , true , false , true , false },
            });
        }

        refreshContent();
    }
}

package ui;

import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import app.Database;
import model.entity.items.Item;
import model.entity.items.Rune;
import model.entity.units.Unit;
import util.SearchableListView;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RuneBoardPropertyPanel extends ScrollPane {

    private Label nameLabel;
    private Database database;
    private Unit unit;
    private RuneBoardPane runeBoardPane;
    Button remove_all_runes = new Button("Remove All Runes");
    Button add = new Button("+");
    Button remove = new Button("-");
    Button remove_ten = new Button("-10");
    Button random_rune = new Button("Random Rune");
    VBox whole_box = new VBox();

    public RuneBoardPropertyPanel(Unit unit, RuneBoardPane runeBoardPane, Database database) {
        this.unit = unit;
        this.database = database;
        this.runeBoardPane = runeBoardPane;
        // กำหนดพื้นหลังสีเข้ม
        this.getStyleClass().add("custom-panel");
        this.getStylesheets().add(getClass().getResource("/styles/theme.css").toExternalForm());

        this.setPadding(new Insets(15));
        this.setPrefWidth(250);
        this.setMaxWidth(300);
        add.setOnAction(e-> {
            unit.increaseRuneInventory();
            refreshContents();
        });
        remove.setOnAction(e-> {
            unit.decreaseRuneInventory();
            refreshContents();
        });
        remove_ten.setOnAction(e-> {
            for (int i = 0; i < 10; i++) {
                unit.decreaseRuneInventory();
            }
            refreshContents();
        });
        random_rune.setOnAction(e-> {
            unit.addRuneToInventory(Rune.randomRune(unit, database.getAllRuneMap()));
            refreshContents();
        });

        remove_all_runes.setOnAction(e-> {
            removeAllRunes();
            runeBoardPane.refreshContent();
        });
        whole_box.getChildren().addAll(remove_all_runes,add,remove, remove_ten, random_rune );
        for (Map.Entry<Integer, Rune> entry : unit.getRune_inventory().entrySet()) {
            VBox row = createRuneInventoryRow(entry);
            whole_box.getChildren().add(row);
        }
        setContent(whole_box);
        runeBoardPane.setPropertyPanel(this);
    }

    public void removeAllRunes() {
        List<Rune> runes = unit.getSocketed_runes();

        for (Rune rune : runes) {
            unit.addRuneToInventory(rune);
        }
        runes.clear();

        //Final Choice Handling (in case of bug)
//        for (int r = 0; r < board.length; r++) {
//            for (int c = 0; c < board[0].length; c++) {
//                board[r][c] = null;
//            }
//        }
    }

    public void refreshContents() {
        whole_box.getChildren().clear();
        whole_box.getChildren().addAll(remove_all_runes,add, remove, remove_ten, random_rune);

        for (Map.Entry<Integer, Rune> entry : unit.getRune_inventory().entrySet()) {
            VBox row = createRuneInventoryRow(entry);
            whole_box.getChildren().add(row);
        }
    }

    private VBox createRuneInventoryRow(Map.Entry<Integer, Rune> entry) {
        VBox row = new VBox();
        row.setPrefWidth(1000);
        row.setStyle("-fx-border-width: 0 0 2 0; -fx-border-color: crimson; -fx-padding: 0");

        int slotIndex = entry.getKey();
        Rune rune = entry.getValue();
        if (rune == null) return new VBox();

        Label slotNum = createLabel(String.valueOf(slotIndex + 1), 50);
        Label itemStatus = createWrappingLabel(rune.getStatusDescription() + rune.getDescription(), 300);

        TextField itemName = new TextField(rune.getName());
        itemName.setPrefSize(200, 40);
        itemName.setStyle("-fx-font-size: 20px; -fx-padding: 0");

        Popup itemPopup = new Popup();
        ListView<String> itemListView = createRuneSearchList(itemName, itemPopup, slotIndex);
        itemPopup.getContent().add(itemListView);

        itemName.setOnKeyReleased(e -> {
            if (!itemPopup.isShowing()) {
                Bounds screenBounds = itemName.localToScreen(itemName.getBoundsInParent());
                if (screenBounds != null) {
                    itemPopup.show(itemName, screenBounds.getMaxX(), screenBounds.getMinY());
                }
            }
            if (e.getCode() == KeyCode.ESCAPE) {
                itemPopup.hide();
                e.consume();
            }
        });

        Button randomise = new Button("Randomise Stat");
        randomise.setOnAction(e -> {
            rune.randomise_stats(unit);
            refreshContents();
        });

        Button place = new Button("Place");
        place.setOnAction(e-> {
            if (rune.getName().equals("")) return;
            runeBoardPane.placeRune(rune.getShape(), rune);
        });

        Button rotate = new Button("Rotate");
        rotate.setOnAction(e-> {
            if (rune.getName().equals("")) return;
            Rune.rotate90(rune);
            runeBoardPane.placeRune(rune.getShape(), rune);
        });
        HBox slotNumBox = new HBox(slotNum);
        slotNumBox.setAlignment(Pos.CENTER_LEFT);
        HBox itemNameBox = new HBox(itemName);
        itemNameBox.setAlignment(Pos.CENTER_LEFT);
        HBox itemStatusBox = new HBox(itemStatus);
        itemStatusBox.setAlignment(Pos.CENTER_LEFT);

        slotNumBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: teal; -fx-padding: 0");
        itemNameBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: teal; -fx-padding: 0");
        itemStatusBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: teal; -fx-padding: 0");

        row.getChildren().addAll(slotNumBox, itemNameBox, itemStatusBox, randomise, place, rotate);
        return row;
    }

    private Label createLabel(String text, double width) {
        Label label = new Label(text);
        label.setPrefSize(width, 40);
        label.setStyle("-fx-font-size: 16px; -fx-padding: 0");
        return label;
    }

    private Label createWrappingLabel(String text, double width) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setPrefWidth(width);
        label.setMinHeight(Region.USE_PREF_SIZE); // สำคัญ: ให้ความสูงปรับตามเนื้อหา
        label.setStyle("-fx-font-size: 16px; -fx-padding: 0");
        return label;
    }

    private ListView<String> createRuneSearchList(TextField itemName, Popup popup, int slotIndex) {
        ListView<String> listView = new ListView<>();
        List<String> itemNames = database.getAllRuneMap().values().stream()
                .map(Item::getName)
                .collect(Collectors.toList());
        SearchableListView.makeSearchable(listView, FXCollections.observableArrayList(itemNames), itemName);

        listView.setOnMouseClicked(e -> {
            String selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                popup.hide();
                unit.getRune_inventory().put(slotIndex, database.getAllRuneMap().get(selected));
                refreshContents();
            }
        });

        listView.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                String selected = listView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    popup.hide();
                    unit.getRune_inventory().put(slotIndex, database.getAllRuneMap().get(selected));
                    refreshContents();
                }
            } else if (e.getCode() == KeyCode.ESCAPE) {
                popup.hide();
                e.consume();
            }
        });

        return listView;
    }
}

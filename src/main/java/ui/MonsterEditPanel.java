package main.ui;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import main.Database;
import model.entity.*;
import model.entity.units.Monster;
import model.type.*;

import java.util.*;

public class MonsterEditPanel extends ScrollPane {

    private final Database database;
    private final ToggleGroup modeToggle = new ToggleGroup();
    private final Button editModeBtn = new Button("Edit");
    private final Button createModeBtn = new Button("Create");
    private final Button backBtn = new Button("Back");
    private final Button toInvenBtn = new Button("To Inventory");
    private final HBox changePageBtn = new HBox();
    private final List<Button> allListPaneBtn = new ArrayList<>();
    private boolean isEditMode = false;
    private final MonsterListPane listPane;
    private final VBox mainBox = new VBox();
    private final HBox listPaneButtonBox = new HBox();
    private final HBox invenPaneButtonBox = new HBox();
    private boolean confirmDeletion = false;
    private Monster toMake;
    private InventoryPane inventoryPane;
    private TreePane treePane;

    public MonsterEditPanel(Database database, MonsterListPane listPane) {
        toMake = new Monster();
        changePageBtn.getChildren().addAll(toInvenBtn);

        allListPaneBtn.add(editModeBtn);
        allListPaneBtn.add(createModeBtn);
        this.database = database;
        this.listPane = listPane;
        getStylesheets().add(getClass().getResource("/styles/theme.css").toExternalForm());
        setPrefWidth(400);
        getStyleClass().add("right-pane");

        listPaneButtonBox.getChildren().addAll(editModeBtn,createModeBtn);

        editModeBtn.setOnAction(e-> {
            editMode();
        });
        createModeBtn.setOnAction(e-> {
            createMode();
        });
        toInvenBtn.setOnAction(e -> {
            Monster selectedMonster = listPane.getListView().getSelectionModel().getSelectedItem();
            if (selectedMonster != null) {
                inventoryMode(selectedMonster);
            } else {
                System.out.println("No monster selected!");
            }
        });
        backBtn.setOnAction(e-> {
            createMode();
        });

        createMode();
        setButtonToSelected(createModeBtn, allListPaneBtn);
        setContent(mainBox);
    }

    public void editMode() {
        listPane.toList();
        mainBox.getChildren().clear();

        setButtonToSelected(editModeBtn, allListPaneBtn);
        isEditMode = true;
        confirmDeletion = false;
        Button deleteButton = new Button("DELETE");

        VBox modeBox = new VBox();
        Monster selectedMonster = listPane.getListView().getSelectionModel().getSelectedItem();
        deleteButton.setOnAction(e -> {
            if (!confirmDeletion) {
                System.out.println("Delete button clicked, click again to confirm deletion.");
                confirmDeletion = true;
            } else {
                if (selectedMonster != null) {
                    database.getAllMonsterMap().remove(selectedMonster.getName());
                    database.getAllCardMap().remove(selectedMonster.getName()+" Card");
                }
                listPane.getListView().refresh();
                listPane.getMonsterList().setAll(database.getAllMonsterMap().values());
                setButtonToSelected(createModeBtn, allListPaneBtn);
                editMode();
            }
        });

        if (selectedMonster != null) {
            modeBox.getChildren().addAll(
                    editNameArea(selectedMonster),
                    editTypeArea(selectedMonster),
                    editBehavior(selectedMonster),
                    opusMove(selectedMonster));
        }
        mainBox.getChildren().addAll(changePageBtn,listPaneButtonBox, deleteButton,modeBox);
    }

    public void createMode() {
        listPane.toList();
        mainBox.getChildren().clear();
        if (treePane != null)
            treePane.getChildren().clear();
        setButtonToSelected(createModeBtn, allListPaneBtn);
        isEditMode = false;
        confirmDeletion = false;

        toMake = new Monster("New Monster");
        Button createButton = new Button("CREATE");
        VBox modeBox = new VBox();

        createButton.setOnAction(e -> {
            database.getAllMonsterMap().put(toMake.getName(),toMake);
            database.getAllCardMap().put(toMake.getName() + " Card", new Card(toMake.getName() + " Card"));
            database.mapAllUnit();
            database.initCounterAllUnit();
            database.translateEverything();
            listPane.getMonsterList().setAll(database.getAllMonsterMap().values());
            listPane.getListView().refresh();
            editMode();
        });
        modeBox.getChildren().addAll(
                editNameArea(toMake),
                editTypeArea(toMake),
                editBehavior(toMake),
                opusMove(toMake));
        mainBox.getChildren().addAll(changePageBtn,listPaneButtonBox, createButton,modeBox);
    }

    public void inventoryMode(Monster monster) {
        listPane.toInventory(monster);
        mainBox.getChildren().clear();
        VBox editingBox = new VBox();
        Region spacer = new Region();
        spacer.setMinHeight(100);
        createStatusField(monster, editingBox);
        editingBox.getChildren().add(spacer);
        createStatField(monster, editingBox);
        createSoulCostField(monster, editingBox);

        mainBox.getChildren().addAll(backBtn, editingBox);
    }

    public Node editNameArea(Monster monster) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("Name");
        TextArea textArea = new TextArea(monster.getName());
        textArea.setWrapText(true);
        textArea.setMaxHeight(100);
        textArea.setMaxWidth(350);
        textArea.setOnKeyReleased(event -> {
            monster.setName(textArea.getText());
            listPane.getListView().refresh();
        });
        contentBox.getChildren().addAll(indicatorLabel,textArea);
        return contentBox;
    }

    public Node editTypeArea(Monster monster) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("Monster Type");
        ComboBox<String> type = new ComboBox<>();
        for (MonsterType monsterType : MonsterType.values()) {
            type.getItems().add(monsterType.writeAsString());
        }
        type.setValue(monster.getMonsterType().writeAsString());
        type.setMaxHeight(100);
        type.setMaxWidth(350);
        type.setOnAction(event -> {
            for (MonsterType monsterType : MonsterType.values()) {
                if (type.getValue().toLowerCase().equals(monsterType.writeAsString().toLowerCase())) {
                    monster.setMonsterType(monsterType);
                    break;
                } else {
                    monster.setMonsterType(MonsterType.NORMAL);
                }
            }
            listPane.getListView().refresh();
        });
        contentBox.getChildren().addAll(indicatorLabel,type);
        return contentBox;
    }

    public Node editBehavior(Monster monster) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("Monster Behavior");
        TextArea textArea = new TextArea(monster.getBehavior());
        textArea.setWrapText(true);
        textArea.setMaxHeight(100);
        textArea.setMaxWidth(350);
        textArea.setOnKeyReleased(event -> {
            monster.setBehavior(textArea.getText());
            listPane.getListView().refresh();
        });
        contentBox.getChildren().addAll(indicatorLabel,textArea);
        return contentBox;
    }

    public Node opusMove(Monster monster) {
        VBox contentBox = new VBox();
        VBox fieldBox = new VBox();
        Button addMove = new Button("Add Opus Move");
        addMove.setOnAction(e -> {
            createOpusMoveField(monster, fieldBox);
        });
        editOpusMoveField(monster, fieldBox);
        contentBox.getChildren().addAll(addMove,fieldBox);

        return contentBox;
    }

    public void createOpusMoveField(Monster monster, VBox fieldBox) {
        int numberIndex = 1;
        while (monster.getOpusMove().containsKey(numberIndex)) {
            numberIndex++;
        }
        final int finalNumberIndex = numberIndex;
        HBox content = new HBox();
        Label label = new Label("Opus Move No."+numberIndex);
        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setPrefHeight(80);
        textArea.setMaxWidth(350);
        textArea.setPromptText("Move Description");
        monster.getOpusMove().put(finalNumberIndex, "");
        textArea.setOnKeyReleased(e-> {
            monster.getOpusMove().put(finalNumberIndex, textArea.getText());
        });
        content.getChildren().addAll(label);
        fieldBox.getChildren().addAll(content, textArea);
    }

    public void editOpusMoveField(Monster monster, VBox fieldBox) {
        for (Map.Entry<Integer, String> entry : monster.getOpusMove().entrySet()) {
            final int numberIndex = entry.getKey();
            HBox labelAndDel = new HBox();
            Label label = new Label("Opus Move No." + numberIndex);
            TextArea textArea = new TextArea();
            Button delButton = new Button("Delete");
            textArea.setWrapText(true);
            textArea.setPrefHeight(80);
            textArea.setMaxWidth(350);
            textArea.setPromptText("Move Description");
            textArea.setText(entry.getValue());
            textArea.setOnKeyReleased(e -> {
                monster.getOpusMove().put(numberIndex, textArea.getText());
            });
            delButton.setOnAction(e-> {
                monster.getOpusMove().remove(numberIndex);
                refreshEditPanel();
            });
            labelAndDel.getChildren().addAll(label, delButton);
            fieldBox.getChildren().addAll(labelAndDel, textArea);
        }
    }

    public void createStatField(Monster monster, VBox editingBox) {
        for (StatType type : StatType.values()) {
            HBox statBox = new HBox();
            Label indicator = new Label(type.writeAsString());
            TextField field = new TextField();
            String oldVal = Double.toString(monster.getMonsterModifier().getStatModifierSafe(type).getFlat());
            field.setText(oldVal);
            field.setPromptText(type.writeAsString());
            field.setOnKeyReleased(e-> {
                double newVal;
                try {
                    newVal = Double.parseDouble(field.getText());
                } catch (NumberFormatException ex) {
                    newVal = 0;
                }
                monster.getMonsterModifier().getStatModifiers().get(type).setFlat(newVal);
                monster.calculateEverything();
                if (inventoryPane != null) {
                    inventoryPane.refreshContents();
                }
            });
            statBox.getChildren().addAll(indicator,field);
            editingBox.getChildren().add(statBox);
        }
    }

    public void createStatusField(Monster monster, VBox editingBox) {
        for (StatusType type : StatusType.values()) {
            HBox statusBox = new HBox();
            Label indicator = new Label(type.writeAsString());
            TextField field = new TextField();
            String oldVal = Double.toString(monster.getMonsterModifier().getStatusModifiers().get(type).getFlat());
            field.setText(oldVal);
            field.setPromptText(type.writeAsString());
            field.setOnKeyReleased(e-> {
                double newVal;
                try {
                    newVal = Double.parseDouble(field.getText());
                } catch (NumberFormatException ex) {
                    newVal = 0;
                }
                monster.getMonsterModifier().getStatusModifiers().get(type).setFlat(newVal);
                monster.calculateEverything();
                if (inventoryPane != null) {
                    inventoryPane.refreshContents();
                }
            });
            statusBox.getChildren().addAll(indicator,field);
            editingBox.getChildren().add(statusBox);
        }
    }

    public void createSoulCostField(Monster monster, VBox editingBox) {
        Label indicator = new Label("Soul Cost");
        TextField field = new TextField();
        String oldVal = Double.toString(monster.getSoulCost());
        field.setText(oldVal);
        field.setPromptText("Soul Cost");
        field.setOnKeyReleased(e-> {
            double newVal;
            try {
                newVal = Double.parseDouble(field.getText());
            } catch (NumberFormatException ex) {
                newVal = 0;
            }
            monster.setSoulCost(newVal);
            if (inventoryPane != null) {
                inventoryPane.refreshContents();
            }
        });
        editingBox.getChildren().addAll(indicator, field);
    }

    public void setButtonToSelected(Button button, List<Button> allButtons) {
        for(Button b : allButtons) {
            b.getStyleClass().remove("button-selected");
        }
        button.getStyleClass().remove("button-selected");
        button.getStyleClass().add("button-selected");
    }

    public void refreshEditPanel() {
        if (isEditMode) {
            editMode();
        } else {
            createMode();
        }
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    public void setInventoryPane(InventoryPane inventoryPane) {
        this.inventoryPane = inventoryPane;
    }

    public void setTreePane(TreePane treePane) {
        this.treePane = treePane;
    }
}

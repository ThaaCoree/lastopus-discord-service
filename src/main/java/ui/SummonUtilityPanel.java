package ui;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import app.Database;
import model.entity.units.Summon;
import model.entity.units.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SummonUtilityPanel extends ScrollPane {

    private final Database database;
    private final ToggleGroup modeToggle = new ToggleGroup();
    private final Button editModeBtn = new Button("Edit");
    private final Button createModeBtn = new Button("Create");
    private final Button backBtn = new Button("Back");
    private final Button toInvenBtn = new Button("To Inventory");
    private final HBox changePageBtn = new HBox();
    private final List<Button> allListPaneBtn = new ArrayList<>();
    private boolean isEditMode = false;
    private final SummonListPane listPane;
    private final VBox mainBox = new VBox();
    private final HBox listPaneButtonBox = new HBox();
    private final HBox invenPaneButtonBox = new HBox();
    private boolean confirmDeletion = false;
    private Summon toMake;
    private InventoryPane inventoryPane;
    private TreePane treePane;

    public SummonUtilityPanel(Database database, SummonListPane listPane) {
        toMake = new Summon();
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
            Summon selectedSummon = listPane.getListView().getSelectionModel().getSelectedItem();
            if (selectedSummon != null) {
                inventoryMode(selectedSummon);
            } else {
                System.out.println("No summon selected!");
            }
        });
        backBtn.setOnAction(e-> {
            setPrefWidth(400);
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
        Summon selectedSummon = listPane.getListView().getSelectionModel().getSelectedItem();
        deleteButton.setOnAction(e -> {
            if (!confirmDeletion) {
                System.out.println("Delete button clicked, click again to confirm deletion.");
                confirmDeletion = true;
            } else {
                if (selectedSummon != null) {
                    selectedSummon.getOwner().getSummons().remove(selectedSummon.getName());
                    database.mapEverything();
                    refreshEditPanel();
                }
                listPane.getListView().refresh();
                listPane.getSummonList().setAll(database.getAllSummon().values());
                setButtonToSelected(createModeBtn, allListPaneBtn);
                editMode();
            }
        });

        if (selectedSummon != null) {
            modeBox.getChildren().addAll(
                    editNameArea(selectedSummon),
                    editNicknameArea(selectedSummon),
                    editOwnerNameArea(selectedSummon),
                    editSoulCostArea(selectedSummon),
                    editIntimacy(selectedSummon),
                    opusMove(selectedSummon));
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

        toMake = new Summon("New Summon");

        Button createButton = new Button("CREATE");
        VBox modeBox = new VBox();

        createButton.setOnAction(e -> {
            if (toMake.getOwner() != null) {
                toMake.getOwner().getSummons().put(toMake.getName(), toMake);
                database.mapEverything();
                listPane.getSummonList().setAll(database.getAllSummon().values());
                listPane.getListView().refresh();
                editMode();
            } else {
                System.out.println("Summon has no owner!");
            }
        });
        modeBox.getChildren().addAll(
                editNameArea(toMake),
                editNicknameArea(toMake),
                editOwnerNameArea(toMake),
                editSoulCostArea(toMake),
                editIntimacy(toMake),
                opusMove(toMake));
        mainBox.getChildren().addAll(changePageBtn,listPaneButtonBox, createButton,modeBox);
    }

    public void inventoryMode(Summon summon) {
        listPane.toInventory(summon);
        mainBox.getChildren().clear();
        VBox editingBox = new VBox();
        setPrefWidth(100);

        mainBox.getChildren().addAll(backBtn, editingBox);
    }

    public Node editNameArea(Summon summon) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("Name");
        TextArea textArea = new TextArea(summon.getName());
        textArea.setWrapText(true);
        textArea.setMaxHeight(100);
        textArea.setMaxWidth(350);
        textArea.setOnKeyReleased(event -> {
            summon.setName(textArea.getText());
            listPane.getListView().refresh();
        });
        contentBox.getChildren().addAll(indicatorLabel,textArea);
        return contentBox;
    }

    public Node editNicknameArea(Summon summon) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("Nickname");
        TextArea textArea = new TextArea(summon.getNick_name());
        textArea.setWrapText(true);
        textArea.setMaxHeight(100);
        textArea.setMaxWidth(350);
        textArea.setOnKeyReleased(event -> {
            summon.setNick_name(textArea.getText());
            listPane.getListView().refresh();
        });
        contentBox.getChildren().addAll(indicatorLabel,textArea);
        return contentBox;
    }

    public Node editOwnerNameArea(Summon summon) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("Owner Name");
        TextArea textArea = new TextArea();
        if (summon.getOwner() != null) {
             textArea.setText(summon.getOwner().getName());
        }
        textArea.setWrapText(true);
        textArea.setMaxHeight(100);
        textArea.setMaxWidth(350);
        textArea.setOnKeyReleased(event -> {
            if (database.getAllUnit().get(textArea.getText()) != null) {
                Unit oldOwner = summon.getOwner();
                if (oldOwner != null) {
                    oldOwner.getSummons().remove(summon.getName());
                }
                Unit owner = database.getAllUnit().get(textArea.getText());
                summon.setOwner(owner);
                owner.getSummons().put(summon.getName(), summon);
                listPane.getListView().refresh();
            }
        });
        contentBox.getChildren().addAll(indicatorLabel,textArea);
        return contentBox;
    }

    public Node editSoulCostArea(Summon summon) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("Soul Cost");
        TextField cost = new TextField();
        cost.setText(Double.toString(summon.getSoulCost()));
        cost.setMaxHeight(100);
        cost.setMaxWidth(350);
        cost.setOnKeyReleased(event -> {
            summon.setSoulCost(Double.parseDouble(cost.getText()));
            listPane.getListView().refresh();
        });
        contentBox.getChildren().addAll(indicatorLabel,cost);
        return contentBox;
    }

    public Node editIntimacy(Summon summon) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("Summon Intimacy");
        TextArea textArea = new TextArea(summon.getIntimacy());
        textArea.setWrapText(true);
        textArea.setMaxHeight(100);
        textArea.setMaxWidth(350);
        textArea.setOnKeyReleased(event -> {
            summon.setIntimacy(textArea.getText());
            listPane.getListView().refresh();
        });
        contentBox.getChildren().addAll(indicatorLabel,textArea);
        return contentBox;
    }

    public Node opusMove(Summon summon) {
        VBox contentBox = new VBox();
        VBox fieldBox = new VBox();
        Button addMove = new Button("Add Opus Move");
        addMove.setOnAction(e -> {
            createOpusMoveField(summon, fieldBox);
        });
        editOpusMoveField(summon, fieldBox);
        contentBox.getChildren().addAll(addMove,fieldBox);

        return contentBox;
    }

    public void createOpusMoveField(Summon summon, VBox fieldBox) {
        int numberIndex = 1;
        while (summon.getOpusMove().containsKey(numberIndex)) {
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
        summon.getOpusMove().put(finalNumberIndex, "");
        textArea.setOnKeyReleased(e-> {
            summon.getOpusMove().put(finalNumberIndex, textArea.getText());
        });
        content.getChildren().addAll(label);
        fieldBox.getChildren().addAll(content, textArea);
    }

    public void editOpusMoveField(Summon summon, VBox fieldBox) {
        for (Map.Entry<Integer, String> entry : summon.getOpusMove().entrySet()) {
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
                summon.getOpusMove().put(numberIndex, textArea.getText());
            });
            delButton.setOnAction(e-> {
                summon.getOpusMove().remove(numberIndex);
                refreshEditPanel();
            });
            labelAndDel.getChildren().addAll(label, delButton);
            fieldBox.getChildren().addAll(labelAndDel, textArea);
        }
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

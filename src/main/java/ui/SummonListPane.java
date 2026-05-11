package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import app.Database;
import model.entity.UniqueModifier;
import model.entity.units.Summon;
import model.entity.units.Unit;

public class SummonListPane extends StackPane {

    private final TextField searchField;
    private final ListView<Summon> listView;
    private final ObservableList<Summon> monsterList;
    private final FilteredList<Summon> filteredMonsters;
    private final Database database;
    private final SummonUtilityPanel editPanel;
    private final VBox content = new VBox();

    public SummonListPane(Database database) {
        this.database = database;
        editPanel = new SummonUtilityPanel(database, this);

        getStylesheets().add(getClass().getResource("/styles/theme.css").toExternalForm());

        this.monsterList = FXCollections.observableArrayList(database.getAllSummon().values());

        this.filteredMonsters = new FilteredList<>(monsterList, p -> true);

        // สร้างช่อง search
        this.searchField = new TextField();
        searchField.setPromptText("Search summon...");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal.toLowerCase().trim();
            String[] keywords = filter.split("\\s+"); // แยกคำด้วยช่องว่าง

            filteredMonsters.setPredicate(monster -> {
                if (monster == null) return false;

                StringBuilder sb = new StringBuilder();
                sb.append(monster.getName()).append(" ");
                sb.append(monster.getOpusName()).append(" ");
                sb.append(monster.getNick_name()).append(" ");
                sb.append(monster.getOpusDescription()).append(" ");
                sb.append(monster.getUnitType().writeAsString()).append(" ");
                sb.append(monster.getOwner().toString()).append(" ");
                sb.append(monster.getLevel()).append(" ");
                for (UniqueModifier uniqueModifier : monster.getUniqueModifier()) {
                    sb.append(uniqueModifier.getName()).append(" ");
                }

                String searchable = sb.toString().toLowerCase();

                // ต้องผ่านทุก keyword ถึงจะผ่าน
                for (String keyword : keywords) {
                    if (!searchable.contains(keyword)) {
                        return false;
                    }
                }
                return true;
            });
        });

        // ส่ง
        listView = new ListView<>(filteredMonsters);
        listView.setMinWidth(1300);
        listView.setMaxWidth(1300);
        listView.setMinHeight(800);
        listView.setCellFactory(lv -> new ListCell<Summon>() {
            @Override
            protected void updateItem(Summon summon, boolean empty) {
                super.updateItem(summon, empty);
                if (empty || summon == null) {
                    setGraphic(null);
                } else {

                    // Title
                    Label nameLabel = new Label(summon.getName());
                    nameLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    nameLabel.setMinWidth(300);
                    nameLabel.setMaxWidth(300);

                    // Subtitle
                    Label ownerLabel = new Label(summon.getOwner().getName());
                    ownerLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    ownerLabel.setMinWidth(150);
                    ownerLabel.setMaxWidth(150);

                    // Subtitle
                    Label nicknameLabel = new Label(summon.getNick_name());
                    nicknameLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    nicknameLabel.setMinWidth(150);
                    nicknameLabel.setMaxWidth(150);

                    Label soulCostLabel = new Label(Double.toString(summon.getSoulCost()));
                    soulCostLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    soulCostLabel.setMinWidth(100);
                    soulCostLabel.setMaxWidth(100);

                    Label intimacyLabel = new Label(summon.getIntimacy());
                    intimacyLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    intimacyLabel.setMinWidth(150);
                    intimacyLabel.setMaxWidth(150);

                    HBox content = new HBox(2, nameLabel, nicknameLabel, ownerLabel, soulCostLabel, intimacyLabel);
                    content.setPadding(new Insets(5));

                    setGraphic(content);
                }
            }
        });

        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (editPanel.isEditMode()) {
                    editPanel.editMode();
                }
            }
        });

        // จัด layout
        content.getChildren().addAll(searchField,listView);
        content.setPadding(new Insets(10));
        this.getChildren().clear();
        this.getChildren().add(content);
    }

    public void toInventory(Unit unit) {
        this.getChildren().clear();
        InventoryPane inventoryPane = new InventoryPane(unit, database);
        editPanel.setInventoryPane(inventoryPane);
        this.getChildren().add(inventoryPane);
    }

    public void toList() {
        this.getChildren().clear();
        this.getChildren().add(content);
    }
    public SummonUtilityPanel getEditPanel() {
        return editPanel;
    }

    public ListView<Summon> getListView() {
        return listView;
    }

    public ObservableList<Summon> getSummonList() {
        return monsterList;
    }
}

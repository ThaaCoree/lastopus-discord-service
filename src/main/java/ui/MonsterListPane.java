package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import app.Database;
import model.entity.units.Monster;
import model.entity.UniqueModifier;
import model.entity.units.Unit;

public class MonsterListPane extends StackPane {

    private final TextField searchField;
    private final ListView<Monster> listView;
    private final ObservableList<Monster> monsterList;
    private final FilteredList<Monster> filteredMonsters;
    private final Database database;
    private final MonsterEditPanel editPanel;
    private final VBox content = new VBox();

    public MonsterListPane(Database database) {
        this.database = database;
        editPanel = new MonsterEditPanel(database, this);

        getStylesheets().add(getClass().getResource("/styles/theme.css").toExternalForm());

        this.monsterList = FXCollections.observableArrayList(database.getAllMonsterMap().values());

        this.filteredMonsters = new FilteredList<>(monsterList, p -> true);

        // สร้างช่อง search
        this.searchField = new TextField();
        searchField.setPromptText("Search monster...");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal.toLowerCase().trim();
            String[] keywords = filter.split("\\s+"); // แยกคำด้วยช่องว่าง

            filteredMonsters.setPredicate(monster -> {
                if (monster == null) return false;

                StringBuilder sb = new StringBuilder();
                sb.append(monster.getName()).append(" ");
                sb.append(monster.getOpusName()).append(" ");
                sb.append(monster.getOpusDescription()).append(" ");
                sb.append(monster.getUnitType().writeAsString()).append(" ");
                sb.append(monster.getMonsterType().writeAsString()).append(" ");
                sb.append(monster.getLevel()).append(" ");
                sb.append(monster.getBehavior()).append(" ");
                sb.append(monster.getSoulCost()).append(" ");
                for (UniqueModifier uniqueModifier : monster.getUniqueModifier()) {
                    sb.append(uniqueModifier.getName()).append(" ");
                }
                for (String opusMove : monster.getOpusMove().values()) {
                    sb.append(opusMove).append(" ");
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
        listView.setCellFactory(lv -> new ListCell<Monster>() {
            @Override
            protected void updateItem(Monster monster, boolean empty) {
                super.updateItem(monster, empty);
                if (empty || monster == null) {
                    setGraphic(null);
                } else {

                    // Title
                    Label nameLabel = new Label(monster.getName());
                    nameLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    nameLabel.setMinWidth(300);
                    nameLabel.setMaxWidth(300);

                    // Subtitle
                    Label typeLabel = new Label(monster.getMonsterType().writeAsString());
                    typeLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    typeLabel.setMinWidth(150);
                    typeLabel.setMaxWidth(150);

                    // Description
                    Label descLabel = new Label(monster.getBehavior());
                    descLabel.setWrapText(true);
                    descLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    descLabel.setMinWidth(600);
                    descLabel.setMaxWidth(600);

                    HBox content = new HBox(2, nameLabel, typeLabel, descLabel);
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
    public MonsterEditPanel getEditPanel() {
        return editPanel;
    }

    public ListView<Monster> getListView() {
        return listView;
    }

    public ObservableList<Monster> getMonsterList() {
        return monsterList;
    }
}

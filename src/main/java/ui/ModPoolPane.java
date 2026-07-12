package ui;

import app.Database;
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
import model.entity.Shop;
import model.entity.ShopItem;
import model.entity.items.crafted_equipments.CraftModPool;

import java.util.Map;

public class ModPoolPane extends StackPane {

    private final TextField searchField;
    private final ListView<CraftModPool> listView;
    private final ObservableList<CraftModPool> poolList;
    private final FilteredList<CraftModPool> filteredPools;
    private final Database database;
    private final ModPoolEditPanel editPanel;
    private final VBox content = new VBox();

    public ModPoolPane(Database database) {
        this.database = database;
        editPanel = new ModPoolEditPanel(database, this);

        getStylesheets().add(getClass().getResource("/styles/theme.css").toExternalForm());

        this.poolList = FXCollections.observableArrayList(database.getAllModPools().values());

        this.filteredPools = new FilteredList<>(poolList, p -> true);

        // สร้างช่อง search
        this.searchField = new TextField();
        searchField.setPromptText("Search pool...");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal.toLowerCase().trim();
            String[] keywords = filter.split("\\s+"); // แยกคำด้วยช่องว่าง

            filteredPools.setPredicate(pool -> {
                if (pool == null) return false;

                StringBuilder sb = new StringBuilder();
                sb.append(pool.getPool_name()).append(" ");
                sb.append(pool.getDescription()).append(" ");
                sb.append(pool.getFixed_mods_description()).append(" ");

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
        listView = new ListView<>(filteredPools);
        listView.setMinWidth(800);
        listView.setMaxWidth(800);
        listView.setMinHeight(800);
        listView.setCellFactory(lv -> new ListCell<CraftModPool>() {
            @Override
            protected void updateItem(CraftModPool pool, boolean empty) {
                super.updateItem(pool, empty);
                if (empty || pool == null) {
                    setGraphic(null);
                } else {

                    // Title
                    Label nameLabel = new Label(pool.getPool_name());
                    nameLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    nameLabel.setMinWidth(300);
                    nameLabel.setMaxWidth(300);

                    // Subtitle
                    Label cityLabel = new Label(pool.getFixed_mods_description());
                    cityLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    cityLabel.setMinWidth(150);
                    cityLabel.setMaxWidth(150);

                    // Description
                    Label descLabel = new Label(pool.getDescription());
                    descLabel.setWrapText(true);
                    descLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    descLabel.setMinWidth(600);
                    descLabel.setMaxWidth(600);

                    HBox content = new HBox(2, nameLabel, cityLabel, descLabel);
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

    public void toList() {
        this.getChildren().clear();
        this.getChildren().add(content);
    }
    public ModPoolEditPanel getEditPanel() {
        return editPanel;
    }

    public ListView<CraftModPool> getListView() {
        return listView;
    }

    public ObservableList<CraftModPool> getPoolList() {
        return poolList;
    }
}

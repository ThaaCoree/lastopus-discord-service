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
import model.entity.items.crafted_equipments.CraftedEquipment;

public class CraftingListPane extends StackPane {

    private final TextField searchField;
    private final ListView<CraftedEquipment> listView;
    private final ObservableList<CraftedEquipment> shopList;
    private final FilteredList<CraftedEquipment> filteredShops;
    private final Database database;
    private final CraftingEditPanel editPanel;
    private final VBox content = new VBox();

    public CraftingListPane(Database database) {
        this.database = database;
        editPanel = new CraftingEditPanel(database, this);

        getStylesheets().add(getClass().getResource("/styles/theme.css").toExternalForm());

        this.shopList = FXCollections.observableArrayList(database.getAllCraftedEquipments().values());

        this.filteredShops = new FilteredList<>(shopList, p -> true);

        // สร้างช่อง search
        this.searchField = new TextField();
        searchField.setPromptText("Search shop...");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal.toLowerCase().trim();
            String[] keywords = filter.split("\\s+"); // แยกคำด้วยช่องว่าง

            filteredShops.setPredicate(equipment -> {
                if (equipment == null) return false;
                StringBuilder sb = new StringBuilder();
                sb.append(equipment.getName()).append(" ");
                sb.append(equipment.getEquipmentType().writeAsString()).append(" ");
                sb.append(equipment.getWeaponType().writeAsString()).append(" ");
                sb.append(equipment.getDescription()).append(" ");
                sb.append(equipment.getStatusDescription()).append(" ");
                sb.append(equipment.getLore()).append(" ");

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
        listView = new ListView<>(filteredShops);
        listView.setMinWidth(1300);
        listView.setMaxWidth(1300);
        listView.setMinHeight(800);
        listView.setCellFactory(lv -> new ListCell<CraftedEquipment>() {
            @Override
            protected void updateItem(CraftedEquipment equipment, boolean empty) {
                super.updateItem(equipment, empty);
                if (empty || equipment == null) {
                    setGraphic(null);
                } else {

                    // Title
                    Label nameLabel = new Label(equipment.getName());
                    nameLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    nameLabel.setMinWidth(300);
                    nameLabel.setMaxWidth(300);

                    // Subtitle
                    Label cityLabel = new Label(equipment.getEquipmentType().writeAsString());
                    cityLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    cityLabel.setMinWidth(150);
                    cityLabel.setMaxWidth(150);

                    // Description
                    Label descLabel = new Label(equipment.getDescription()+"\n"+equipment.getStatusDescription());
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
    public CraftingEditPanel getEditPanel() {
        return editPanel;
    }

    public ListView<CraftedEquipment> getListView() {
        return listView;
    }

    public ObservableList<CraftedEquipment> getEquipmentList() {
        return shopList;
    }
}

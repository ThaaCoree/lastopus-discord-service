package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import app.Database;
import model.entity.items.Equipment;
import model.entity.items.Item;

public class ItemListPane extends StackPane {

    private final TextField searchField;
    private final ListView<Item> listView;
    private final ObservableList<Item> itemList;
    private final FilteredList<Item> filteredItems;
    private final Database database;
    private final ItemEditPanel editPanel;

    public ItemListPane(Database database) {
        this.database = database;
        editPanel = new ItemEditPanel(database, this);

        getStylesheets().add(getClass().getResource("/styles/theme.css").toExternalForm());

        this.itemList = FXCollections.observableArrayList(database.getAllItemMap().values());

        this.filteredItems = new FilteredList<>(itemList, p -> true);

        // สร้างช่อง search
        this.searchField = new TextField();
        searchField.setPromptText("Search item...");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal.toLowerCase().trim();
            String[] keywords = filter.split("\\s+"); // แยกคำด้วยช่องว่าง

            filteredItems.setPredicate(item -> {
                if (item == null) return false;

                StringBuilder sb = new StringBuilder();
                sb.append(item.getName()).append(" ");
                sb.append(item.getDescription()).append(" ");
                sb.append(item.getStatusDescription()).append(" ");
                sb.append(item.getLore()).append(" ");
                sb.append(item.getPrice()).append(" ");
                sb.append(item.getItemType().writeAsString()).append(" ");

                if (item instanceof Equipment) {
                    Equipment equipment = (Equipment) item;
                    sb.append(equipment.getEquipmentType().writeAsString()).append(" ");
                    sb.append(equipment.getWeaponType().writeAsString());
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
        listView = new ListView<>(filteredItems);
        listView.setMinWidth(1300);
        listView.setMaxWidth(1300);
        listView.setMinHeight(800);
        listView.setCellFactory(lv -> new ListCell<Item>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    // Status
                    Label statusLabel = new Label(item.getStatusDescription() + "\n\n" + item.getDescription());
                    statusLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    statusLabel.setWrapText(true);
                    statusLabel.setMinHeight(60);
                    statusLabel.setMinWidth(400);
                    statusLabel.setMaxWidth(400);

                    // Title
                    Label nameLabel = new Label(item.getName());
                    nameLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    nameLabel.minHeightProperty().bind(statusLabel.heightProperty());
                    nameLabel.setMinWidth(200);
                    nameLabel.setMaxWidth(200);

                    // Subtitle
                    Label typeLabel = new Label(item.getItemType().writeAsString());
                    typeLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    typeLabel.minHeightProperty().bind(statusLabel.heightProperty());
                    typeLabel.setMinWidth(70);
                    typeLabel.setMaxWidth(70);

                    // Description
                    Label descLabel = new Label(item.getLore());
                    descLabel.setWrapText(true);
                    descLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    descLabel.minHeightProperty().bind(statusLabel.heightProperty());
                    descLabel.setMinWidth(500);
                    descLabel.setMaxWidth(500);

                    Label priceLabel = new Label(item.getPrice());
                    priceLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    priceLabel.minHeightProperty().bind(statusLabel.heightProperty());
                    priceLabel.setMinWidth(100);
                    priceLabel.setMaxWidth(100);

                    Label weightLabel = new Label("Weight: "+Integer.toString(item.getWeight()));
                    weightLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    weightLabel.minHeightProperty().bind(statusLabel.heightProperty());
                    weightLabel.setMinWidth(120);
                    weightLabel.setMaxWidth(120);

                    HBox content = new HBox(2, nameLabel, typeLabel, descLabel, statusLabel, priceLabel, weightLabel);
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
        VBox content = new VBox(10, searchField, listView);
        content.setPadding(new Insets(10));
        this.getChildren().add(content);
    }

    public ItemEditPanel getEditPanel() {
        return editPanel;
    }

    public ListView<Item> getListView() {
        return listView;
    }

    public ObservableList<Item> getItemList() {
        return itemList;
    }
}

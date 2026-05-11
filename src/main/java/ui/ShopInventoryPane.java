package main.ui;

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
import main.Database;
import model.entity.ShopItem;
import model.entity.items.Equipment;

public class ShopInventoryPane extends StackPane {

    private final TextField searchField;
    private final ListView<ShopItem> listView;
    private final ObservableList<ShopItem> itemList;
    private final FilteredList<ShopItem> filteredItems;
    private final Database database;
    private ShopEditPanel editPanel;

    public ShopInventoryPane(Database database, String ownerName, ShopEditPanel editPanel) {
        this.database = database;
        this.editPanel = editPanel;

        getStylesheets().add(getClass().getResource("/styles/theme.css").toExternalForm());

        this.itemList = FXCollections.observableArrayList(database.getAllShop().get(ownerName).getList().values());

        this.filteredItems = new FilteredList<>(itemList, p -> true);

        // สร้างช่อง search
        this.searchField = new TextField();
        searchField.setPromptText("Search item...");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal.toLowerCase().trim();
            String[] keywords = filter.split("\\s+"); // แยกคำด้วยช่องว่าง

            filteredItems.setPredicate(item -> {
                if (item.getItem() == null) return false;

                StringBuilder sb = new StringBuilder();
                sb.append(item.getItem().getName()).append(" ");
                sb.append(item.getItem().getDescription()).append(" ");
                sb.append(item.getItem().getStatusDescription()).append(" ");
                sb.append(item.getItem().getLore()).append(" ");
                sb.append(item.getPrice()).append(" ");
                sb.append(item.getItem().getItemType().writeAsString()).append(" ");

                if (item.getItem() instanceof Equipment) {
                    Equipment equipment = (Equipment) item.getItem();
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
        listView.setCellFactory(lv -> new ListCell<ShopItem>() {
            @Override
            protected void updateItem(ShopItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    // Status
                    if (item.getItem() == null) setGraphic(null);
                    if (item.getItem() == null) return;
                    Label statusLabel = new Label(item.getItem().getStatusDescription() + "\n\n" + item.getItem().getDescription());
                    statusLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    statusLabel.setWrapText(true);
                    statusLabel.setMinHeight(60);
                    statusLabel.setMinWidth(300);
                    statusLabel.setMaxWidth(300);

                    // Title
                    Label nameLabel = new Label(item.getItem().getName());
                    nameLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    nameLabel.minHeightProperty().bind(statusLabel.heightProperty());
                    nameLabel.setMinWidth(200);
                    nameLabel.setMaxWidth(200);

                    // Subtitle
                    Label typeLabel = new Label(item.getItem().getItemType().writeAsString());
                    typeLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    typeLabel.minHeightProperty().bind(statusLabel.heightProperty());
                    typeLabel.setMinWidth(70);
                    typeLabel.setMaxWidth(70);

                    // Description
                    Label descLabel = new Label(item.getItem().getLore());
                    descLabel.setWrapText(true);
                    descLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    descLabel.minHeightProperty().bind(statusLabel.heightProperty());
                    descLabel.setMinWidth(300);
                    descLabel.setMaxWidth(300);

                    Label priceLabel = new Label(item.getPrice());
                    priceLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    priceLabel.minHeightProperty().bind(statusLabel.heightProperty());
                    priceLabel.setMinWidth(100);
                    priceLabel.setMaxWidth(100);

                    Label stockLabel = new Label("Stock : "+Integer.toString(item.getStock()));
                    stockLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    stockLabel.minHeightProperty().bind(statusLabel.heightProperty());
                    stockLabel.setMinWidth(100);
                    stockLabel.setMaxWidth(100);

                    HBox content = new HBox(2, nameLabel, typeLabel, descLabel, statusLabel, priceLabel, stockLabel);
                    content.setPadding(new Insets(5));

                    setGraphic(content);
                }
            }
        });

        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                editPanel.refreshInventoryContents();
            }
        });

        // จัด layout
        VBox content = new VBox(10, searchField, listView);
        content.setPadding(new Insets(10));
        this.getChildren().add(content);
    }

    public ListView<ShopItem> getListView() {
        return listView;
    }

    public ObservableList<ShopItem> getItemList() {
        return itemList;
    }
}

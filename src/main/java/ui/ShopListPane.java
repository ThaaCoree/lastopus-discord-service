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
import model.entity.Shop;
import model.entity.ShopItem;

import java.util.Map;

public class ShopListPane extends StackPane {

    private final TextField searchField;
    private final ListView<Shop> listView;
    private final ObservableList<Shop> shopList;
    private final FilteredList<Shop> filteredShops;
    private final Database database;
    private final ShopEditPanel editPanel;
    private final VBox content = new VBox();

    public ShopListPane(Database database) {
        this.database = database;
        editPanel = new ShopEditPanel(database, this);

        getStylesheets().add(getClass().getResource("/styles/theme.css").toExternalForm());

        this.shopList = FXCollections.observableArrayList(database.getAllShop().values());

        this.filteredShops = new FilteredList<>(shopList, p -> true);

        // สร้างช่อง search
        this.searchField = new TextField();
        searchField.setPromptText("Search shop...");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal.toLowerCase().trim();
            String[] keywords = filter.split("\\s+"); // แยกคำด้วยช่องว่าง

            filteredShops.setPredicate(shop -> {
                if (shop == null) return false;

                for (Map.Entry<Integer, ShopItem> entry : shop.getList().entrySet()) {
                    ShopItem shopItem = entry.getValue();
                    if (shopItem == null) continue;

                        StringBuilder sb = new StringBuilder();
                        sb.append(shopItem.getItem().getName()).append(" ");
                        sb.append(shopItem.getItem().getItemType()).append(" ");
                        sb.append(shopItem.getItem().getLore()).append(" ");
                        sb.append(shopItem.getItem().getPrice()).append(" ");
                        sb.append(shopItem.getItem().getStatusDescription()).append(" ");
                        sb.append(shopItem.getItem().getDescription()).append(" ");
                        sb.append(shopItem.getPrice()).append(" ");

                        String searchable = sb.toString().toLowerCase();

                        // ต้องผ่านทุก keyword ถึงจะผ่าน
                        for (String keyword : keywords) {
                            if (!searchable.contains(keyword)) {
                                return false;
                            }
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
        listView.setCellFactory(lv -> new ListCell<Shop>() {
            @Override
            protected void updateItem(Shop shop, boolean empty) {
                super.updateItem(shop, empty);
                if (empty || shop == null) {
                    setGraphic(null);
                } else {

                    // Title
                    Label nameLabel = new Label(shop.getOwnerName());
                    nameLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    nameLabel.setMinWidth(300);
                    nameLabel.setMaxWidth(300);

                    // Subtitle
                    Label cityLabel = new Label(shop.getCityName());
                    cityLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    cityLabel.setMinWidth(150);
                    cityLabel.setMaxWidth(150);

                    // Description
                    Label descLabel = new Label(shop.getDescription());
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

    public void toInventory(Shop shop) {
        this.getChildren().clear();
        ShopInventoryPane inventoryPane = new ShopInventoryPane(database, shop.getOwnerName(), editPanel);
        editPanel.setInventoryPane(inventoryPane);
        this.getChildren().add(inventoryPane);
    }

    public void toList() {
        this.getChildren().clear();
        this.getChildren().add(content);
    }
    public ShopEditPanel getEditPanel() {
        return editPanel;
    }

    public ListView<Shop> getListView() {
        return listView;
    }

    public ObservableList<Shop> getShopList() {
        return shopList;
    }
}

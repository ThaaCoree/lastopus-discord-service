package ui;

import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import app.Database;
import manager.InventoryManager;
import model.entity.Shop;
import model.entity.ShopItem;
import model.entity.items.Item;
import model.entity.units.Unit;
import model.type.CurrencyType;
import util.SearchableListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShopEditPanel extends ScrollPane {

    private final Database database;
    private final ToggleGroup modeToggle = new ToggleGroup();
    private final Button editModeBtn = new Button("Edit");
    private final Button createModeBtn = new Button("Create");
    private final Button backBtn = new Button("Back");
    private final Button toInvenBtn = new Button("To Inventory");
    private final HBox changePageBtn = new HBox();
    private final List<Button> allListPaneBtn = new ArrayList<>();
    private boolean isEditMode = false;
    private final ShopListPane listPane;
    private final VBox mainBox = new VBox();
    private final HBox listPaneButtonBox = new HBox();
    private final HBox invenPaneButtonBox = new HBox();
    private boolean confirmDeletion = false;
    private Shop toMake;
    private ShopInventoryPane shopInventoryPane;
    private TreePane treePane;
    private Shop currentShop;
    private String addingItemName;
    private String addingItemPrice;
    private String addingItemStock;
    private String removingItemName;
    private String buyer = "";
    private int price_in_number = 0;

    public ShopEditPanel(Database database, ShopListPane listPane) {
        toMake = new Shop("New Owner Name", "New City Name", false);
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
            Shop selectedShop = listPane.getListView().getSelectionModel().getSelectedItem();
            if (selectedShop != null) {
                shopInventoryMode(selectedShop);
            } else {
                System.out.println("No shop selected!");
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
        Shop selectedShop = listPane.getListView().getSelectionModel().getSelectedItem();
        deleteButton.setOnAction(e -> {
            if (!confirmDeletion) {
                System.out.println("Delete button clicked, click again to confirm deletion.");
                confirmDeletion = true;
            } else {
                if (selectedShop != null) {
                    database.getAllShop().remove(selectedShop.getOwnerName());
                }
                listPane.getListView().refresh();
                listPane.getShopList().setAll(database.getAllShop().values());
                setButtonToSelected(createModeBtn, allListPaneBtn);
                editMode();
            }
        });

        if (selectedShop != null) {
            modeBox.getChildren().addAll(
                    editOwnerNameArea(selectedShop),
                    editCityNameArea(selectedShop),
                    editDescription(selectedShop),
                    toggleOpen(selectedShop));
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

        toMake = new Shop("New Owner Name", "New City Name", false);
        Button createButton = new Button("CREATE");
        VBox modeBox = new VBox();

        createButton.setOnAction(e -> {
            database.getAllShop().put(toMake.getOwnerName(),toMake);
            database.translateEverything();
            listPane.getShopList().setAll(database.getAllShop().values());
            listPane.getListView().refresh();
            editMode();
        });
        modeBox.getChildren().addAll(
                editOwnerNameArea(toMake),
                editCityNameArea(toMake),
                editDescription(toMake));
        mainBox.getChildren().addAll(changePageBtn,listPaneButtonBox, createButton,modeBox);
    }

    public void shopInventoryMode(Shop shop) {
        listPane.toInventory(shop);
        currentShop = shop;
        mainBox.getChildren().clear();
        VBox editingBox = new VBox();
        Region spacer = new Region();
        spacer.setMinHeight(100);
        Region spacer2 = new Region();
        spacer2.setMinHeight(100);
        addItem(shop, editingBox);
        editingBox.getChildren().add(spacer);
        removeItem(shop, editingBox);
        editingBox.getChildren().add(spacer2);
        editPriceAndStock(shop, editingBox);

        mainBox.getChildren().addAll(backBtn, editingBox);
    }

    public Node editOwnerNameArea(Shop shop) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("Owner Name");
        TextArea textArea = new TextArea(shop.getOwnerName());
        textArea.setWrapText(true);
        textArea.setMaxHeight(100);
        textArea.setMaxWidth(350);
        textArea.setOnKeyReleased(event -> {
            shop.setOwnerName(textArea.getText());
            listPane.getListView().refresh();
        });
        contentBox.getChildren().addAll(indicatorLabel,textArea);
        return contentBox;
    }

    public Node editCityNameArea(Shop shop) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("City Name");
        TextArea textArea = new TextArea(shop.getCityName());
        textArea.setWrapText(true);
        textArea.setMaxHeight(100);
        textArea.setMaxWidth(350);
        textArea.setOnKeyReleased(event -> {
            shop.setCityName(textArea.getText());
            listPane.getListView().refresh();
        });
        contentBox.getChildren().addAll(indicatorLabel,textArea);
        return contentBox;
    }

    public Node editDescription(Shop shop) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("Shop Behavior");
        TextArea textArea = new TextArea(shop.getDescription());
        textArea.setWrapText(true);
        textArea.setMaxHeight(100);
        textArea.setMaxWidth(350);
        textArea.setOnKeyReleased(event -> {
            shop.setDescription(textArea.getText());
            listPane.getListView().refresh();
        });
        contentBox.getChildren().addAll(indicatorLabel,textArea);
        return contentBox;
    }

    public Node toggleOpen(Shop shop) {
        VBox box = new VBox();
        CheckBox checkbox = new CheckBox();
        checkbox.setSelected(shop.isOpen());
        checkbox.setOnAction(e-> {
            shop.setOpen(checkbox.isSelected());
        });

        box.getChildren().add(checkbox);
        return box;
    }

    public void addItem(Shop shop, VBox box) {
        TextField itemName = new TextField(addingItemName);
        itemName.setPrefSize(200, 40);

        Popup itemPopup = new Popup();
        ListView<String> itemListView = createItemSearchList(itemName, itemPopup, shop);
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

        TextField price = new TextField(addingItemPrice);
        TextField stock = new TextField(addingItemStock);

        itemName.setPromptText("Name");
        price.setPromptText("Price");
        stock.setPromptText("Stock");

        price.setPrefSize(200, 40);
        stock.setPrefSize(200, 40);

        price.setOnKeyReleased(e -> {
            addingItemPrice = price.getText();
        });
        stock.setOnKeyReleased(e -> {
            addingItemStock = stock.getText();
        });

        Button add = new Button("Add");

        add.setOnAction(e -> {
            if (database.getAllItemMap().get(addingItemName) != null) {
                Item item = database.getAllItemMap().get(addingItemName);
                int stockNum;
                if (isNumber(addingItemStock)) {
                    stockNum = Integer.parseInt(addingItemStock);
                } else {
                    stockNum = 1;
                }

                if (addingItemPrice == null || addingItemPrice.isEmpty()) {
                    addingItemPrice = item.getPrice();
                }
                shop.addToShop(item, addingItemPrice, stockNum);

                addingItemStock = "0";
                addingItemName = "";
                addingItemPrice = "";
            }
            shopInventoryMode(shop);
        });

        box.getChildren().addAll(itemName, price, stock, add);
    }

    public void removeItem(Shop shop, VBox box) {
        HBox mainBox = new HBox();
        TextField name = new TextField(removingItemName);
        name.setPromptText("Slot Number");
        name.setOnKeyReleased(e -> {
            removingItemName = name.getText();
        });

        Button remove = new Button("Remove");
        mainBox.getChildren().add(remove);

        remove.setOnAction(e -> {
            ShopItem shopItem = shopInventoryPane.getListView().getSelectionModel().getSelectedItem();
            shop.removeFromShop(shopItem.getItem().getName());
            shopInventoryMode(shop);
        });

        box.getChildren().addAll(mainBox);
    }

    public void editPriceAndStock(Shop shop, VBox box) {
        ShopItem shopItem = shopInventoryPane.getListView().getSelectionModel().getSelectedItem();
        if (shopItem == null) return;

        TextField price = new TextField(shopItem.getPrice());
        TextField stock = new TextField(Integer.toString(shopItem.getStock()));
        ComboBox<String> buyer_combo = new ComboBox<String>();
        Button buy_button = new Button("Buy");
        TextField actual_price = new TextField();

        price.setOnKeyReleased(e-> {
            shopItem.setPrice(price.getText());
            shopInventoryPane.getListView().refresh();
        });

        stock.setOnKeyReleased(e-> {
            if (isNumber(stock.getText())) {
                shopItem.setStock(Integer.parseInt(stock.getText()));
            } else {
                shopItem.setStock(-1);
            }
            shopInventoryPane.getListView().refresh();
        });

        for(Map.Entry<String, Unit> entry : database.getAllPlayerMap().entrySet()) {
            buyer_combo.getItems().add(entry.getKey());
        }

        buyer_combo.setOnAction(e-> {
            buyer = buyer_combo.getValue();
        });
        buyer_combo.setValue(buyer);

        actual_price.setPromptText("Price in Number");
        actual_price.setText(Integer.toString(price_in_number));
        actual_price.setOnKeyReleased(e-> {
            price_in_number = Integer.parseInt(actual_price.getText());
        });

        buy_button.setOnAction(e-> {
            Unit buying_unit = database.findUnit(buyer);

            int currency = 0;
            currency += buying_unit.getPurse().get(CurrencyType.PLATINUM)*10*100*98;
            currency += buying_unit.getPurse().get(CurrencyType.GOLD)*10*100;
            currency += buying_unit.getPurse().get(CurrencyType.SILVER)*10;
            currency += buying_unit.getPurse().get(CurrencyType.COPPER);

            if (currency >= price_in_number) {

            InventoryManager inv = buying_unit.getInventoryManager();

                int remain = currency - price_in_number;
                buying_unit.setCurrency(CurrencyType.PLATINUM, 0);
                buying_unit.setCurrency(CurrencyType.GOLD, 0);
                buying_unit.setCurrency(CurrencyType.SILVER, 0);
                buying_unit.setCurrency(CurrencyType.COPPER, 0);

                int platinum = remain / (10*100*98);
                remain %= (10*100*98);

                int gold = remain / (10*100);
                remain %= (10*100);

                int silver = remain / 10;
                int copper = remain % 10;

                buying_unit.addCurrency(CurrencyType.PLATINUM, platinum);
                buying_unit.addCurrency(CurrencyType.GOLD, gold);
                buying_unit.addCurrency(CurrencyType.SILVER, silver);
                buying_unit.addCurrency(CurrencyType.COPPER, copper);

                shopItem.reduceStock();
                inv.addItem(shopItem.getItem());
                stock.setText(Integer.toString(shopItem.getStock()));
                shopInventoryPane.getListView().refresh();
            }
        });

        box.getChildren().addAll(price,stock,buyer_combo,buy_button, actual_price);
    }

    private ListView<String> createItemSearchList(TextField itemName, Popup popup, Shop shop) {
        ListView<String> listView = new ListView<>();
        List<String> itemNames = database.getAllItemMap().values().stream()
                .map(Item::getName)
                .collect(Collectors.toList());
        SearchableListView.makeSearchable(listView, FXCollections.observableArrayList(itemNames), itemName);

        listView.setOnMouseClicked(e -> {
            String selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                popup.hide();
                addingItemName = selected;
                shopInventoryMode(shop);
            }
        });

        listView.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                String selected = listView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    popup.hide();
                    addingItemName = selected;
                    shopInventoryMode(shop);
                }
            } else if (e.getCode() == KeyCode.ESCAPE) {
                popup.hide();
                e.consume();
            }
        });

        return listView;
    }

    public void refreshInventoryContents() {
        mainBox.getChildren().clear();
        VBox editingBox = new VBox();
        Region spacer = new Region();
        spacer.setMinHeight(100);
        Region spacer2 = new Region();
        spacer2.setMinHeight(100);
        addItem(currentShop, editingBox);
        editingBox.getChildren().add(spacer);
        removeItem(currentShop, editingBox);
        editingBox.getChildren().add(spacer2);
        editPriceAndStock(currentShop, editingBox);

        mainBox.getChildren().addAll(backBtn, editingBox);
    }

    public static boolean isNumber(String str) {
        if (str == null || str.isEmpty()) return false;
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
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

    public void setInventoryPane(ShopInventoryPane shopInventoryPane) {
        this.shopInventoryPane = shopInventoryPane;
    }

    public void setTreePane(TreePane treePane) {
        this.treePane = treePane;
    }
}

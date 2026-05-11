package main.ui;

import com.google.api.services.sheets.v4.model.Request;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import main.Database;
import model.entity.Shop;
import model.entity.ShopItem;
import model.entity.items.Equipment;
import model.entity.skills.Skill;
import model.entity.skills.SkillInstance;
import model.entity.skills.SkillMultiplier;
import model.entity.units.Unit;
import model.type.UnitType;
import util.AsyncUtil;
import util.GoogleSheetsUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MainPane extends BorderPane {

    private Database database;
    private boolean onInventory = false;
    private boolean onTree = false;
    private boolean onRune = false;
    private String onPlayer = "";
    private List<Button> changeUnitTypeButtons = new ArrayList<>();
    private List<Button> allLeftNavigatorButtons = new ArrayList<>();
    private Map<String, Button> allUnitButtons = new HashMap<>();
    private TreePane treePane = new TreePane();
    private Button combat1Button = new Button("Main Combat");
    private Button inventoryButton = new Button("Inventory");
    private Button treeButton = new Button("Passive Tree");
    private Button cardCollectButton = new Button("Card");
    private Button itemButton = new Button("Item List");
    private Button monsterButton = new Button("Monster List");
    private Button summonButton = new Button("Summon List");
    private Button conditionButton = new Button("Condition List");
    private Button shopButton = new Button("Shop");
    private Button runeButton = new Button("Rune Board");

    public MainPane(Database database) {
        this.database = database;


        getStyleClass().add("custom-panel");
        getStylesheets().add(getClass().getResource("/styles/theme.css").toExternalForm());

        setTop(createTopNavigator());
        setLeft(createLeftNavigator());
    }

    public Node createTopNavigator() {
        HBox mainBox = new HBox();
//        Region spacer = new Region();
//        HBox.setHgrow(spacer, Priority.ALWAYS);

        mainBox.getStyleClass().add("top-pane");

        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(1);
        flowPane.setVgap(1);
        flowPane.setPrefWidth(1650);

        VBox switcher = new VBox();
        switcher.setPrefWidth(200);
        switcher.getStyleClass().add("sub-pane");

        VBox saver = new VBox();
        saver.setPrefWidth(300);
        saver.getStyleClass().add("sub-pane");

        Button saveButton = new Button("SaveJSON");
        Button toInSession = new Button("InSession");
        Button toPlayer = new Button("Player");
        Button toNPC = new Button("NPC");
        Button updateShop = new Button("UpdateSHOP");

        changeUnitTypeButtons.add(toInSession);
        changeUnitTypeButtons.add(toPlayer);
        changeUnitTypeButtons.add(toNPC);
        toPlayer.setOnAction(e-> {
            changeUnitPane(flowPane, "Player");
            setButtonToSelected(toPlayer, changeUnitTypeButtons);});
        toNPC.setOnAction(e-> {
            changeUnitPane(flowPane, "NPC");
            setButtonToSelected(toNPC, changeUnitTypeButtons);});
        toInSession.setOnAction(e-> {
            changeUnitPane(flowPane, "Session");
            setButtonToSelected(toInSession, changeUnitTypeButtons);});
        saveButton.setOnAction(e -> {
            database.saveJson();
            AsyncUtil.runAsync(() -> {
                try {
                    GoogleSheetsUtil sheetsUtil = new GoogleSheetsUtil();
                    AtomicInteger skillStartRow = new AtomicInteger(1);
                    for (Unit unit : database.getAllUnit().values()) {
                        if (unit.getUnitType() == UnitType.PLAYER || unit.getUnitType() == UnitType.NPC){
                            List<Request> requests = unit.buildWriteRequests();
                            sheetsUtil.takeRequests(requests);
                            if (unit.getUnitType() == UnitType.PLAYER) {
                                sheetsUtil.takeRequests(skillWrite(unit, skillStartRow));
                            }
                            System.out.println("done processing for "+unit.getName());
                        }
                    }
                    sheetsUtil.requestSet();
                    String sessionId = UUID.randomUUID().toString();  // สร้าง id ใหม่สำหรับ session นี้
                    long startTime = System.currentTimeMillis();
                    System.out.println("Start processRequest: " + startTime + " Session: " + sessionId);

                    sheetsUtil.processRequest(GoogleSheetsUtil.viewerSheetId);

                    sheetsUtil.requestClear();
                    sheetsUtil.takeRequests(database.writeItemToSheet());
                    sheetsUtil.takeRequests(database.writeCardToSheet());
                    sheetsUtil.takeRequests(database.writeBuffToSheet());
                    sheetsUtil.requestSet();
                    sheetsUtil.processRequest(GoogleSheetsUtil.databaseSheetId);

                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    System.out.println("End processRequest: took " + duration + " ms Session: " + sessionId);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        });

        updateShop.setOnAction(e-> {
            database.saveJson();
            AsyncUtil.runAsync(() -> {
                updateShops();
            });
        });

        switcher.getChildren().add(toPlayer);
        switcher.getChildren().add(toNPC);

        HBox updateBox = new HBox(updateShop, saveButton);

        saver.getChildren().add(updateBox);
        saver.getChildren().add(toInSession);
        saver.setAlignment(Pos.CENTER);

        mainBox.getChildren().addAll(flowPane, saver, switcher);
        return mainBox;
    }

    public List<Request> skillWrite(Unit unit, AtomicInteger skillStartRow) {
        List<Request> requests = new ArrayList<>();
        try {
            GoogleSheetsUtil sheetsUtil = new GoogleSheetsUtil();
            // หา sheetId
            Integer sheetId = sheetsUtil.getSheetIdByName(GoogleSheetsUtil.viewerSheetId, "SkillList");
            if (sheetId == null) {
                return new ArrayList<>();
            }
            skillWriteAll(requests, sheetId, unit, skillStartRow);
        } catch (Exception ex) {
            ex.printStackTrace(); // หรือจะ throw ต่อก็ได้
        }

        return requests;
    }

    public void skillWriteAll(List<Request> requests, int sheetId, Unit unit, AtomicInteger skillStartRow) throws Exception {
        String range = "B"+skillStartRow.toString();
        int skillCount = 0;
        List<List<Object>> toAppend = new ArrayList<>();
        List<Object> name = new ArrayList<>();
        name.add(unit.getName());
        toAppend.add(name);

        List<Object> indicator = new ArrayList<>();
        indicator.add("ชื่อสกิล");
        indicator.add("ประเภทสกิล");
        indicator.add("ความสามารถ");
        indicator.add("รูปแบบ");
        indicator.add("Cooldown");
        indicator.add("Cost");
        indicator.add("ตัวคูณ");
        toAppend.add(indicator);

        for (SkillInstance skillInstance : unit.getAllSkill().values()) {
            List<Object> row = new ArrayList<>();
            Skill skill = skillInstance.getSkillData();

            row.add(skill.getName());
            row.add(skill.getTranslatedTag());
            row.add(skill.getDescription());
            row.add(skill.getActionType());
            row.add(skill.getTranslatedCooldown());
            row.add(skill.getTranslatedCost());
            StringBuilder mult = new StringBuilder();

            for (Map.Entry<String, SkillMultiplier> entry : skill.getSkillMultiplier().entrySet()) {
                String xx = entry.getKey();
                String multiplier = entry.getValue().getFormula();
                String tag = entry.getValue().getTagString();
                mult.append(xx).append(" : ").append(multiplier).append(" [").append(tag).append("]\n");
            }
            row.add(mult.toString());

            toAppend.add(row);
            skillCount++;
        }

        if (skillCount <= 23) {
            int fill = 24-skillCount;
            for (int i = 0; i < fill; i++) {
                List<Object> row = new ArrayList<>();
                row.add("");
                row.add("");
                row.add("");
                row.add("");
                row.add("");
                row.add("");
                row.add("");
                toAppend.add(row);
            }
        }

        requests.add(GoogleSheetsUtil.buildUpdateCellsRequest(sheetId, range, toAppend));
        skillStartRow.getAndAdd(27);
    }

    public Node createLeftNavigator() {
        FlowPane leftNavigator = new FlowPane();
        leftNavigator.getStyleClass().add("left-pane");
        leftNavigator.setHgap(1);
        leftNavigator.setVgap(1);
        leftNavigator.setPrefWidth(200);

        allLeftNavigatorButtons.add(combat1Button);
        allLeftNavigatorButtons.add(inventoryButton);
        allLeftNavigatorButtons.add(treeButton);
        allLeftNavigatorButtons.add(cardCollectButton);
        allLeftNavigatorButtons.add(itemButton);
        allLeftNavigatorButtons.add(monsterButton);
        allLeftNavigatorButtons.add(summonButton);
        allLeftNavigatorButtons.add(conditionButton);
        allLeftNavigatorButtons.add(shopButton);
        allLeftNavigatorButtons.add(runeButton);

        combat1Button.setOnAction(e-> {
            combatButtonPressed();
        });
        inventoryButton.setOnAction(e-> {
            inventoryButtonPressed();
        });
        treeButton.setOnAction(e-> {
            treeButtonPress();
        });
        cardCollectButton.setOnAction(e-> {
            cardListButtonPressed();
        });
        itemButton.setOnAction(e-> {
            itemListButtonPressed();
        });
        monsterButton.setOnAction(e-> {
            monsterListButtonPressed();
        });
        summonButton.setOnAction(e-> {
            summonListButtonPressed();
        });
        conditionButton.setOnAction(e-> {
            conditionListButtonPressed();
        });
        shopButton.setOnAction(e-> {
            shopListButtonPressed();
        });
        runeButton.setOnAction(e-> {
            runeButtonPressed();
        });

        combat1Button.setPrefWidth(200);
        inventoryButton.setPrefWidth(99);
        treeButton.setPrefWidth(99);
        runeButton.setPrefWidth(200);
        cardCollectButton.setPrefWidth(75);
        conditionButton.setPrefWidth(124);
        itemButton.setPrefWidth(99);
        monsterButton.setPrefWidth(99);
        summonButton.setPrefWidth(124);
        shopButton.setPrefWidth(75);

        leftNavigator.getChildren().addAll(combat1Button,
                inventoryButton, treeButton,
                runeButton,
                itemButton, monsterButton,
                conditionButton, cardCollectButton,
                summonButton, shopButton);

        return leftNavigator;
    }

    public void changeUnitPane(FlowPane flowPane, String type) {
        flowPane.getChildren().clear();
        allUnitButtons.clear();

        for (Unit unit : database.getAllUnit().values()) {
            switch (type) {
                case "Player":
                    if (!unit.isPlayer()) continue;
                    break;
                case "NPC":
                    if (!unit.isNpc()) continue;
                    break;
                case "Session":
                    if (!unit.isInSession()) continue;
                    break;
                default:
                    break;
            }
            Button btn = new Button(unit.getName());
            allUnitButtons.put(unit.getName(),btn);
            btn.setMaxWidth(120);
            btn.setMinWidth(80);
            btn.setOnAction(e -> {
                unitButtonPressed(unit);
            setButtonToSelected(btn, allUnitButtons);});
            flowPane.getChildren().add(btn);
        }
    }

    public void setButtonToSelected(Button button, List<Button> allButtons) {
        for(Button b : allButtons) {
            b.getStyleClass().remove("button-selected");
        }
        button.getStyleClass().remove("button-selected");
        button.getStyleClass().add("button-selected");
    }

    public void setButtonToSelected(Button button, Map<String, Button> allButtons) {
        for(Button b : allButtons.values()) {
            b.getStyleClass().remove("button-selected");
        }
        button.getStyleClass().remove("button-selected");
        button.getStyleClass().add("button-selected");
    }

    public void setUnitButtonToSelected(Map<String, Button> allUnitButtons, String unitName) {
        for (Map.Entry<String, Button> entry : allUnitButtons.entrySet()) {
            String buttonName = entry.getKey();
            Button button = entry.getValue();
            if (buttonName.equals(unitName)) {
                button.getStyleClass().remove("button-selected");
                button.getStyleClass().add("button-selected");
            } else {
                button.getStyleClass().remove("button-selected");
            }
        }
    }

    public void unitButtonPressed(Unit unit) {
        onPlayer = unit.getName();
        if (onTree) {
            treeButtonPress();
        } else if (onInventory) {
            inventoryButtonPressed();
        } else {
            runeButtonPressed();
        }
        setUnitButtonToSelected(allUnitButtons, unit.getName());
    }

    public void inventoryButtonPressed() {
        treePane.getChildren().clear();
        if (onPlayer.isEmpty()) {
            onPlayer = "Akivili";
        }
        for (Unit unit : database.getAllUnit().values()) {
            if (unit.getName().equals(onPlayer)) {
                InventoryPane invenPane = new InventoryPane(unit,database);
                InventoryUtilityPanel utilityPanel = new InventoryUtilityPanel();
                setCenter(invenPane);
                setRight(utilityPanel);
                setUnitButtonToSelected(allUnitButtons, unit.getName());
            }
        }
        onInventory = true;
        onTree = false;
        onRune = false;
        setButtonToSelected(inventoryButton, allLeftNavigatorButtons);
    }

    public void treeButtonPress() {
        treePane.getChildren().clear();
        if (onPlayer.isEmpty()) {
            onPlayer = "Akivili";
        }
        for (Unit unit : database.getAllUnit().values()) {
            if (unit.getName().equals(onPlayer)) {
                treePane = new TreePane(unit);
                setCenter(treePane);
                setRight(treePane.getPropertyPanel());
                treePane.toBack();
                setUnitButtonToSelected(allUnitButtons, unit.getName());
            }
        }
        onInventory = false;
        onTree = true;
        onRune = false;
        setButtonToSelected(treeButton, allLeftNavigatorButtons);
    }

    public void runeButtonPressed() {
        treePane.getChildren().clear();
        if (onPlayer.isEmpty()) {
            onPlayer = "Akivili";
        }
        for (Unit unit : database.getAllUnit().values()) {
            if (unit.getName().equals(onPlayer)) {

                RuneBoardPane runePane = new RuneBoardPane(unit, database);
                RuneBoardPropertyPanel utilityPanel = new RuneBoardPropertyPanel(unit, runePane, database);
                setCenter(runePane);
                setRight(utilityPanel);
                setUnitButtonToSelected(allUnitButtons, unit.getName());
            }
        }
        onInventory = false;
        onTree = false;
        onRune = true;
        setButtonToSelected(runeButton, allLeftNavigatorButtons);
    }

    public void combatButtonPressed() {
        treePane.getChildren().clear();
        setButtonToSelected(combat1Button, allUnitButtons);
        setButtonToSelected(combat1Button, allLeftNavigatorButtons);
        CombatPane combatPane = new CombatPane(database.getCombatController());
        setCenter(combatPane);
        setRight(combatPane.getUtilityPanel());
    }

    public void itemListButtonPressed() {
        treePane.getChildren().clear();
        setButtonToSelected(itemButton, allLeftNavigatorButtons);
        setButtonToSelected(itemButton, allUnitButtons);
        ItemListPane itemPane = new ItemListPane(database);
        setCenter(itemPane);
        setRight(itemPane.getEditPanel());
    }

    public void conditionListButtonPressed() {
        treePane.getChildren().clear();
        setButtonToSelected(conditionButton, allLeftNavigatorButtons);
        setButtonToSelected(conditionButton, allUnitButtons);
        ConditionListPane conditionPane = new ConditionListPane(database);
        setCenter(conditionPane);
        setRight(conditionPane.getEditPanel());
    }

    public void cardListButtonPressed() {
        treePane.getChildren().clear();
        setButtonToSelected(cardCollectButton, allLeftNavigatorButtons);
        setButtonToSelected(cardCollectButton, allUnitButtons);
        CardListPane cardPane = new CardListPane(database);
        setCenter(cardPane);
        setRight(cardPane.getEditPanel());
    }

    public void monsterListButtonPressed() {
        treePane.getChildren().clear();
        setButtonToSelected(monsterButton, allLeftNavigatorButtons);
        setButtonToSelected(monsterButton, allUnitButtons);
        MonsterListPane monsterPane = new MonsterListPane(database);
        setCenter(monsterPane);
        setRight(monsterPane.getEditPanel());
    }

    public void summonListButtonPressed() {
        treePane.getChildren().clear();
        setButtonToSelected(summonButton, allLeftNavigatorButtons);
        setButtonToSelected(summonButton, allUnitButtons);
        SummonListPane summonPane = new SummonListPane(database);
        setCenter(summonPane);
        setRight(summonPane.getEditPanel());
    }

    public void shopListButtonPressed() {
        treePane.getChildren().clear();
        setButtonToSelected(shopButton, allLeftNavigatorButtons);
        setButtonToSelected(shopButton, allUnitButtons);
        ShopListPane shopPane = new ShopListPane(database);
        setCenter(shopPane);
        setRight(shopPane.getEditPanel());
    }

    public void updateShops() {
            try {
                GoogleSheetsUtil sheetsUtil = new GoogleSheetsUtil();
                int index = 1;
                for (Map.Entry<String, Shop> entry : database.getAllShop().entrySet()) {
                    if (!entry.getValue().isOpen()) continue;
                    List<Request> requests = new ArrayList<>();
                    int baseRow = 4;
                    int step = 10;
                    Integer sheetId = sheetsUtil.getSheetIdByName(GoogleSheetsUtil.viewerSheetId, "Shop");
                    String indicatorRange = "B" + (baseRow-1 + (index-1) * step);
                    String cityRange = "C" + (baseRow-1 + (index-1) * step);
                    int columnIndex = 2;
                    int itemIndex = 1;
                    List<List<Object>> indicatorAppend;
                    indicatorAppend = new ArrayList<>(List.of(
                            List.of(entry.getValue().getOwnerName()+"'s "+entry.getValue().getDescription())
                    ));
                    requests.add(GoogleSheetsUtil.buildUpdateCellsRequest(sheetId, indicatorRange, indicatorAppend));
                    List<List<Object>> cityAppend;
                    cityAppend = new ArrayList<>(List.of(
                            List.of(entry.getValue().getCityName())
                    ));
                    requests.add(GoogleSheetsUtil.buildUpdateCellsRequest(sheetId, cityRange, cityAppend));
                    for (ShopItem shopItem : entry.getValue().getList().values()) {
                        String range = getExcelColumnName(columnIndex) + (baseRow + (index-1) * step);
                        List<List<Object>> toAppend; // ประกาศไว้ก่อน
                        if (shopItem.getItem() instanceof Equipment) {
                            if (shopItem.getItem() == null) continue;
                            Equipment equipment = (Equipment) shopItem.getItem();
                            toAppend = new ArrayList<>(List.of(
                                    List.of(itemIndex),
                                    List.of(equipment.getName()),
                                    List.of(equipment.getLore()),
                                    List.of(equipment.getStatusDescription() + "\n" + shopItem.getItem().getDescription()),
                                    List.of(shopItem.getPrice()),
                                    List.of("Stocks : " + shopItem.getStock()),
                                    List.of(equipment.getEquipmentType().writeAsString()),
                                    List.of(equipment.getWeaponType().writeAsString())
                            ));
                        } else {
                            if (shopItem.getItem() == null) continue;
                            toAppend = new ArrayList<>(List.of(
                                    List.of(itemIndex),
                                    List.of(shopItem.getItem().getName()),
                                    List.of(shopItem.getItem().getLore()),
                                    List.of(shopItem.getItem().getStatusDescription() + "\n" + shopItem.getItem().getDescription()),
                                    List.of(shopItem.getPrice()),
                                    List.of("Stock : " + shopItem.getStock()),
                                    List.of(shopItem.getItem().getItemType().writeAsString())
                            ));
                        }
                        itemIndex++;
                        columnIndex++;
                        requests.add(GoogleSheetsUtil.buildUpdateCellsRequest(sheetId, range, toAppend));
                    }
                    index++;
                    sheetsUtil.takeRequests(requests);
                }
                sheetsUtil.requestSet();
                String sessionId = UUID.randomUUID().toString();  // สร้าง id ใหม่สำหรับ session นี้
                long startTime = System.currentTimeMillis();
                System.out.println("Start processRequest: " + startTime + " Session: " + sessionId);

                sheetsUtil.processRequest(GoogleSheetsUtil.viewerSheetId);

                sheetsUtil.requestClear();
                sheetsUtil.takeRequests(database.writeItemToSheet());
                sheetsUtil.requestSet();
                sheetsUtil.processRequest(GoogleSheetsUtil.databaseSheetId);

                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                System.out.println("End processRequest: took " + duration + " ms Session: " + sessionId);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
    }

    public static String getExcelColumnName(int columnNumber) {
        StringBuilder columnName = new StringBuilder();

        while (columnNumber > 0) {
            columnNumber--; // ทำให้เริ่มจาก 0
            int remainder = columnNumber % 26;
            columnName.insert(0, (char) ('A' + remainder));
            columnNumber /= 26;
        }

        return columnName.toString();
    }
}

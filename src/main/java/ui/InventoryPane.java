package ui;

import com.google.api.services.sheets.v4.model.Request;
import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Popup;
import app.Database;
import factory.SkillFactory;
import model.entity.*;
import model.entity.items.Equipment;
import model.entity.items.EquipmentSlot;
import model.entity.items.Item;
import model.entity.items.Rune;
import model.entity.skills.SkillInstance;
import model.entity.units.Unit;
import model.modifier.ModValue;
import model.type.*;
import util.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class InventoryPane extends ScrollPane {
    Unit unit;
    Database database;
    VBox mainBox = new VBox();
    HBox row1 = new HBox();
    HBox row2 = new HBox();
    GoogleSheetsUtil sheetsUtil;

    public InventoryPane(Unit unit, Database database) {
        try {
            sheetsUtil = new GoogleSheetsUtil();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.unit = unit;
        this.database = database;
        unit.calculateEverything();
        setFitToHeight(true);
        setFitToWidth(true);

        getStylesheets().add(getClass().getResource("/styles/theme.css").toExternalForm());

        setPadding(new Insets(30));

        mainBox.setSpacing(20);
        row1.setSpacing(20);
        row2.setSpacing(20);
        mainBox.getChildren().addAll(row1, row2);

        row1.getChildren().addAll(basicInfoContent(), statPanel1Content(), statPanel2Content(), statPanel3Content(), resourcePanel(), uniquePanel(), counterPanel());
        row2.getChildren().addAll(inventoryPaneContent(), equipmentPaneContent(), skillPanel());

        row2.setSpacing(50);
        setContent(mainBox);
    }

    public Node basicInfoContent() {
        VBox contentBox = new VBox();
        contentBox.setPrefWidth(400);
        contentBox.setMinHeight(640);
        Button write = new Button("Write To Sheet");
        HBox row1 = new HBox();
        HBox row2 = new HBox();
        HBox row3 = new HBox();
        HBox row4 = new HBox();
        Region spacer1 = new Region();
        Region spacer2 = new Region();
        Region spacer3 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        HBox.setHgrow(spacer3, Priority.ALWAYS);

        Label name = new Label (unit.getName());
        Label race = new Label();
        if (unit.getRace().getName() != null) {
            race.setText("Race: " +
                    unit.getRace().getName() + "\n" +
                    unit.getRace().getDescription());
            if (unit.getRace().getName().equals("Human")) {
                race.setText("Race: \n" +
                        unit.getRace().getName() + "\n" +
                        unit.getRace().getHumanDescription());
            }
        }
        Label opusName = new Label();
        Label opusDesc = new Label();
        Label primaryLabel = new Label("Primary Card");
        Label secondaryLabel = new Label("Secondary Card");
        ComboBox<String> primaryCard = new ComboBox<>();
        ComboBox<String> secondaryCard = new ComboBox<>();
        Label level = new Label();
        Label remainingStatusPoint = new Label();
        primaryCard.getItems().add("None");
        secondaryCard.getItems().add("None");
        CheckBox inSession = new CheckBox("In Session");
        CheckBox mixTwoHanded = new CheckBox("Mix-TwoHanded");
        CheckBox mermaidean = new CheckBox("Mermaidean's Strength");
        Button levelIncrease = new Button("+");
        Button levelDecrease = new Button("-");
        levelIncrease.setMinHeight(30);
        levelIncrease.setMaxHeight(30);
        levelIncrease.setMinWidth(30);
        levelIncrease.setMaxWidth(30);
        levelDecrease.setMinHeight(30);
        levelDecrease.setMaxHeight(30);
        levelDecrease.setMinWidth(30);
        levelDecrease.setMaxWidth(30);

        inSession.setSelected(unit.isInSession());
        mixTwoHanded.setSelected(unit.isMixTwoHanded());

        write.setOnAction(e -> {
            AsyncUtil.runAsync(() -> {
                try {
                    List<Request> requests = unit.buildWriteRequests(sheetsUtil);
                    sheetsUtil.takeRequests(requests);
                    sheetsUtil.requestSet();
                    String sessionId = UUID.randomUUID().toString();  // สร้าง id ใหม่สำหรับ session นี้
                    long startTime = System.currentTimeMillis();
                    System.out.println("Start processRequest: " + startTime + " Session: " + sessionId);

                    sheetsUtil.processRequest(GoogleSheetsUtil.viewerSheetId);

                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    System.out.println("End processRequest: took " + duration + " ms Session: " + sessionId);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        });

        levelIncrease.setOnAction(e-> {
            unit.levelIncrement();
            unit.calculateEverything();
            refreshContents();
        });
        levelDecrease.setOnAction(e-> {
            unit.levelDecrement();
            unit.calculateEverything();
            refreshContents();
        });
        inSession.setOnAction(e -> {
            unit.setInSession(inSession.isSelected());
        });
        mixTwoHanded.setOnAction(e -> {
            unit.setMixTwoHanded(mixTwoHanded.isSelected());
            unit.calculateEverything();
            refreshContents();
        });

        for (CardType type : CardType.values()) {
            if (unit.getCard().get(type) != null) {
                if (type == CardType.PRIMARY) {
                    primaryCard.setValue(unit.getCard().get(type).getName());
                }
                if (type == CardType.SECONDARY) {
                    secondaryCard.setValue(unit.getCard().get(type).getName());
                }
            }
        }
        if (database.getAllCardMap() != null)
        for (Card card : database.getAllCardMap().values()) {
            primaryCard.getItems().add(card.getName());
            secondaryCard.getItems().add(card.getName());
        }
        primaryCard.setPrefHeight(30);
        secondaryCard.setPrefHeight(30);
        primaryCard.setPrefWidth(150);
        secondaryCard.setPrefWidth(150);
        primaryCard.setOnAction(e-> {
            String selected = primaryCard.getValue();
            if (selected.equals("None")) {
                unit.getCardManager().unequipCard(CardType.PRIMARY);
            } else {
                cardEquipped(selected, CardType.PRIMARY);
            }
            refreshContents();
        });
        secondaryCard.setOnAction(e -> {
            String selected = secondaryCard.getValue();
            if (selected.equals("None")) {
                unit.getCardManager().unequipCard(CardType.SECONDARY);
            } else {
                cardEquipped(selected, CardType.SECONDARY);
            }
            refreshContents();
        });
        opusName.setText("Opus : " + unit.getOpusName());
        opusDesc.setText(unit.getOpusDescription());
        race.setWrapText(true);
        opusName.setWrapText(true);
        opusDesc.setWrapText(true);
        level.setText("Level : "+unit.getLevel());
        remainingStatusPoint.setText("Points Left : "+unit.getRemainingStatusPoint());

        name.setStyle("-fx-font-size: 22px;");
        race.setStyle("-fx-font-size: 14px;");
        opusName.setStyle("-fx-font-size: 14px;");
        opusDesc.setStyle("-fx-font-size: 14px;");
        level.setStyle("-fx-font-size: 20px;");
        remainingStatusPoint.setStyle("-fx-font-size: 20px;");

        row1.getChildren().addAll(name, spacer1, inSession);
        row2.getChildren().addAll(level, levelIncrease, levelDecrease, spacer2, mixTwoHanded);
        row3.getChildren().addAll(remainingStatusPoint, spacer3);
        contentBox.getChildren().addAll(write ,row1, row2, row3, primaryLabel, primaryCard, secondaryLabel, secondaryCard, race, opusName, opusDesc);
        return contentBox;
    }

    public Node statPanel1Content() {
        HBox contentBox = new HBox();
        contentBox.setMinWidth(300);
        contentBox.setMinHeight(500);

        VBox indicator = new VBox();
        VBox raised = new VBox();
        VBox overall = new VBox();

        indicator.setStyle("-fx-border-color: white; -fx-border-width: 0 2 0 0; -fx-padding: 0; -fx-font-size: 20px;");
        raised.setStyle("-fx-border-color: white; -fx-border-width: 0 2 0 0; -fx-padding: 0; -fx-font-size: 20px;");
        overall.setStyle("-fx-border-color: white; -fx-border-width: 0 2 0 0; -fx-padding: 0; -fx-font-size: 20px;");

        indicator.setMinWidth(100);
        indicator.setMinHeight(480);
        indicator.setMaxWidth(100);
        indicator.setMaxHeight(480);
        raised.setMinWidth(120);
        raised.setMinHeight(480);
        raised.setMaxWidth(120);
        raised.setMaxHeight(480);
        overall.setMinWidth(100);
        overall.setMinHeight(480);
        overall.setMaxWidth(100);
        overall.setMaxHeight(480);

        Map<StatusType, ModValue> status = unit.getStatuses();
        Map<StatusType, Integer> raisedStatus = unit.getRaisedStatuses();

        Label statusLabel = new Label("Status\n\n\n");
        Label raisedLabel = new Label("Raised\n\n\n");
        Label overallLabel = new Label("Overall\n\n\n");

        statusLabel.setStyle("-fx-font-size: 20px; -fx-border-width: 0 0 2 0; -fx-border-color: white; -fx-padding: 15");
        raisedLabel.setStyle("-fx-font-size: 20px; -fx-border-width: 0 0 2 0; -fx-border-color: white; -fx-padding: 15");
        overallLabel.setStyle("-fx-font-size: 20px; -fx-border-width: 0 0 2 0; -fx-border-color: white; -fx-padding: 15");

        indicator.getChildren().add(statusLabel);
        raised.getChildren().add(raisedLabel);
        overall.getChildren().add(overallLabel);

        statusLabel.setMinWidth(100);
        overallLabel.setMinWidth(100);
        raisedLabel.setPrefWidth(120);
        statusLabel.setMaxHeight(30);
        overallLabel.setMaxHeight(30);
        raisedLabel.setMaxHeight(30);

        for (StatusType type : StatusType.values()) {
            HBox raisedBox = new HBox();
            Label indicatorToShow = new Label();
            Label overallToShow = new Label();
            TextField raisedToShow = new TextField();
            indicatorToShow.setText(type.writeAsString() + "\n\n");
            overallToShow.setText(String.format("%.2f",status.get(type).getFinal()) + "\n\n");
            raisedToShow.setText(Integer.toString(raisedStatus.get(type)) + "\n\n");
            Button plus = new Button("+");
            Button minus = new Button("-");
            plus.setOnAction(e-> {
                unit.getStatusManager().increaseStatusByOne(type);
                refreshContents();
            });
            minus.setOnAction(e-> {
                unit.getStatusManager().decreaseStatusByOne(type);
                refreshContents();
            });

            raisedToShow.setPrefWidth(75);
            indicatorToShow.setStyle("-fx-font-size: 20px; -fx-border-width: 0 0 2 0; -fx-border-color: white; -fx-padding: 15");
            overallToShow.setStyle("-fx-font-size: 20px; -fx-border-width: 0 0 2 0; -fx-border-color: white; -fx-padding: 15");
            raisedBox.setStyle("-fx-font-size: 20px; -fx-border-width: 0 0 2 0; -fx-border-color: white; -fx-padding: 6");
            raisedToShow.setStyle("-fx-font-size: 20px; -fx-padding: 0;");
            plus.setStyle("-fx-font-size: 22px; -fx-padding: 0;");
            minus.setStyle("-fx-font-size: 22px; -fx-padding: 0;");
            raisedBox.setMinHeight(60);
            raisedBox.setMaxHeight(60);
            raisedBox.prefWidth(75);
            raisedBox.setAlignment(Pos.CENTER);

            raisedToShow.setOnKeyReleased(e->{
                if (raisedToShow.getText().isEmpty()) return;
                if (Double.isNaN(Double.parseDouble(raisedToShow.getText()))) return;
                int input = Integer.parseInt(raisedToShow.getText());
                unit.getRaisedStatuses().put(type, input);
            });

            indicatorToShow.setMinWidth(100);
            indicatorToShow.setMinHeight(60);
            indicatorToShow.setMaxWidth(100);
            indicatorToShow.setMaxHeight(60);
            overallToShow.setMinWidth(100);
            overallToShow.setMinHeight(60);
            overallToShow.setMaxWidth(100);
            overallToShow.setMaxHeight(60);
            raisedToShow.setMinHeight(30);
            raisedToShow.setMaxHeight(30);
            plus.setMinWidth(25);
            plus.setMinHeight(30);
            plus.setMaxWidth(25);
            plus.setMaxHeight(30);
            minus.setMinWidth(25);
            minus.setMinHeight(30);
            minus.setMaxWidth(25);
            minus.setMaxHeight(30);

            raisedBox.getChildren().addAll(raisedToShow,plus,minus);
            indicator.getChildren().add(indicatorToShow);
            raised.getChildren().add(raisedBox);
            overall.getChildren().add(overallToShow);
        }

        contentBox.getChildren().addAll(indicator, raised, overall);

        return contentBox;
    }

    public Node statPanel2Content() {
        HBox contentBox = new HBox();
        contentBox.setMinWidth(300);
        contentBox.setMinHeight(500);

        VBox indicator = new VBox();
        VBox overall = new VBox();

        indicator.setStyle("-fx-border-color: orange; -fx-border-width: 0 2 0 0; -fx-padding: 0; -fx-font-size: 20px;");
        overall.setStyle("-fx-border-color: orange; -fx-border-width: 0 2 0 0; -fx-padding: 0; -fx-font-size: 20px;");

        indicator.setMinWidth(150);
        indicator.setMinHeight(500);
        overall.setMinWidth(150);
        overall.setMinHeight(500);

        Map<StatType, ModValue> stat = unit.getStats();

        Label statlabel = new Label("Stat\n\n\n");
        Label overallLabel = new Label("Overall\n\n\n");

        statlabel.setStyle("-fx-font-size: 20px; -fx-border-width: 0 0 2 0; -fx-border-color: orange; -fx-padding: 15");
        overallLabel.setStyle("-fx-font-size: 20px; -fx-border-width: 0 0 2 0; -fx-border-color: orange; -fx-padding: 15");

        indicator.getChildren().add(statlabel);
        overall.getChildren().add(overallLabel);

        statlabel.setMinWidth(150);
        overallLabel.setMinWidth(150);
        statlabel.setMaxHeight(30);
        overallLabel.setMaxHeight(30);

        StatType[] allStats = StatType.values();
        int half = 18;

        for (int i = 0; i < half; i++) {
            Label indicatorToShow = new Label();
            Label overallToShow = new Label();
            indicatorToShow.setText(allStats[i].writeAsString() + "\n\n");
            double statValue = stat.get(allStats[i]).getFinal();
            if (allStats[i] == StatType.CRITCHANCE || allStats[i] == StatType.CRITDAMAGE || allStats[i] == StatType.HEALAMPLIFIER || allStats[i] == StatType.BUFFAMPLIFIER ||
                    allStats[i] == StatType.DEBUFFAMPLIFIER) {
                overallToShow.setText(String.format("%.2f", statValue*100) + "%\n\n");
            } else {
                overallToShow.setText(String.format("%.2f", statValue) + "\n\n");
            }

            indicatorToShow.setStyle("-fx-font-size: 20px; -fx-border-width: 0 0 2 0; -fx-border-color: orange; -fx-padding: 0");
            overallToShow.setStyle("-fx-font-size: 20px; -fx-border-width: 0 0 2 0; -fx-border-color: orange; -fx-padding: 0");
            indicatorToShow.setMinWidth(150);
            overallToShow.setMinWidth(150);
            indicatorToShow.setMaxHeight(15);
            overallToShow.setMaxHeight(15);

            indicator.getChildren().add(indicatorToShow);
            overall.getChildren().add(overallToShow);
        }

        contentBox.getChildren().addAll(indicator, overall);

        return contentBox;
    }

    public Node statPanel3Content() {
        HBox contentBox = new HBox();
        contentBox.setMinWidth(300);
        contentBox.setMinHeight(500);

        VBox indicator = new VBox();
        VBox overall = new VBox();

        indicator.setStyle("-fx-border-color: orange; -fx-border-width: 0 2 0 0; -fx-padding: 0; -fx-font-size: 20px;");
        overall.setStyle("-fx-border-color: orange; -fx-border-width: 0 2 0 0; -fx-padding: 0; -fx-font-size: 20px;");

        indicator.setMinWidth(150);
        indicator.setMinHeight(500);
        overall.setMinWidth(150);
        overall.setMinHeight(500);

        Map<StatType, ModValue> stat = unit.getStats();

        Label statlabel = new Label("Stat\n\n\n");
        Label overallLabel = new Label("Overall\n\n\n");

        statlabel.setStyle("-fx-font-size: 20px; -fx-border-width: 0 0 2 0; -fx-border-color: orange; -fx-padding: 15");
        overallLabel.setStyle("-fx-font-size: 20px; -fx-border-width: 0 0 2 0; -fx-border-color: orange; -fx-padding: 15");

        indicator.getChildren().add(statlabel);
        overall.getChildren().add(overallLabel);

        statlabel.setMinWidth(150);
        overallLabel.setMinWidth(150);
        statlabel.setMaxHeight(30);
        overallLabel.setMaxHeight(30);

        StatType[] allStats = StatType.values();
        int half = 18;

        for (int i = half; i < 35; i++) {
            Label indicatorToShow = new Label();
            Label overallToShow = new Label();
            indicatorToShow.setText(allStats[i].writeAsString() + "\n\n");
            double statValue = stat.get(allStats[i]).getFinal();
            if (allStats[i] == StatType.DAMAGEAMPLIFIER || allStats[i] == StatType.DAMAGEREDUCTION || allStats[i] == StatType.ATTACKSPEED || allStats[i] == StatType.CASTSPEED ||
                    allStats[i] == StatType.RESERVATION || allStats[i] == StatType.CRITSHIELD) {
                overallToShow.setText(String.format("%.2f", statValue*100) + "%\n\n");
            } else {
                overallToShow.setText(String.format("%.2f", statValue) + "\n\n");
            }

            indicatorToShow.setStyle("-fx-font-size: 20px; -fx-border-width: 0 0 2 0; -fx-border-color: orange; -fx-padding: 0");
            overallToShow.setStyle("-fx-font-size: 20px; -fx-border-width: 0 0 2 0; -fx-border-color: orange; -fx-padding: 0");
            indicatorToShow.setMinWidth(150);
            overallToShow.setMinWidth(150);
            indicatorToShow.setMaxHeight(15);
            overallToShow.setMaxHeight(15);

            indicator.getChildren().add(indicatorToShow);
            overall.getChildren().add(overallToShow);
        }

        contentBox.getChildren().addAll(indicator, overall);

        return contentBox;
    }

    public Node equipmentPaneContent() {
        int slotNumber = 0;
        if (unit.getEquipmentSlots() != null)
        for (EquipmentSlot slot : unit.getEquipmentSlots().values()) {
            slotNumber++;
        }

        VBox wholeBox = new VBox();

        HBox equipmentBox = new HBox();
        VBox slotNumBox = new VBox();
        VBox equipTypeBox = new VBox();
        VBox equipNameBox = new VBox();

        equipmentBox.setMinWidth(450);
        equipmentBox.setMaxWidth(450);
        equipmentBox.setMaxHeight(slotNumber*60);

        slotNumBox.setStyle("-fx-border-color: deepskyblue; -fx-border-width: 0 2 0 0; -fx-padding: 0; -fx-font-size: 20px;");
        equipTypeBox.setStyle("-fx-border-color: deepskyblue; -fx-border-width: 0 2 0 0; -fx-padding: 0; -fx-font-size: 20px;");
        equipNameBox.setStyle("-fx-border-color: deepskyblue; -fx-border-width: 0 2 0 0; -fx-padding: 0; -fx-font-size: 20px;");

        for (Map.Entry<Integer, EquipmentSlot> slotEntry : unit.getEquipmentSlots().entrySet()) {
            Label slotNum = new Label(Integer.toString(slotEntry.getKey()));
            Label equipType = new Label(slotEntry.getValue().getEquipmentType().writeAsString());
            ComboBox<String> equipName = new ComboBox<>();

            slotNum.setMinHeight(60);
            equipType.setMinHeight(60);
            equipName.setMinHeight(60);
            slotNum.setMaxHeight(60);
            equipType.setMaxHeight(60);
            equipName.setMaxHeight(60);
            slotNum.setMinWidth(20);
            equipType.setMinWidth(80);
            equipName.setMinWidth(350);
            slotNum.setMaxWidth(20);
            equipType.setMaxWidth(80);
            equipName.setMaxWidth(350);

            equipName.getItems().add("None");
            for (Map.Entry<Integer, Item> entry : unit.getInventoryItems().entrySet()) {
                if (entry.getValue() instanceof Equipment) {
                    if (((Equipment) entry.getValue()).getEquipmentType().writeAsString().equals(equipType.getText()))
                    equipName.getItems().add(entry.getValue().getName());
                }
                }

            if (unit.getEquipmentSlots().get(slotEntry.getKey()).getEquipment() != null) {
                equipName.setValue(unit.getEquipmentSlots().get(slotEntry.getKey()).getEquipment().getName());
            } else {
                equipName.setValue("");
            }
            equipName.valueProperty().addListener((obs, oldVal, newVal) -> {
                String selected = equipName.getValue();
                if (selected.equals("None")) {
                    unit.getEquipmentManager().unequip(slotEntry.getKey());
                } else {
                    unit.getEquipmentManager().unequip(slotEntry.getKey());
                    unit.getEquipmentManager().equip(slotEntry.getKey(), database.getAllEquipmentMap().get(selected));
                }
                refreshContents();
            });

            slotNum.setStyle("-fx-font-size: 20px; -fx-border-width: 0 0 2 0; -fx-border-color: deepskyblue; -fx-padding: 0");
            equipType.setStyle("-fx-font-size: 20px; -fx-border-width: 0 0 2 0; -fx-border-color: deepskyblue; -fx-padding: 0");
            equipName.setStyle("-fx-font-size: 20px; -fx-border-width: 0 0 2 0; -fx-border-color: deepskyblue; -fx-padding: 0");

            slotNumBox.getChildren().add(slotNum);
            equipTypeBox.getChildren().add(equipType);
            equipNameBox.getChildren().add(equipName);
        }

        equipmentBox.getChildren().addAll(slotNumBox,equipTypeBox,equipNameBox);
        wholeBox.getChildren().addAll(equipmentBox, createCurrencyContent());

        Button add = new Button("+");
        Button remove = new Button("-");
        add.setOnAction(e-> {
            unit.increaseRuneInventory();
            refreshContents();
        });
        remove.setOnAction(e-> {
            unit.decreaseRuneInventory();
            refreshContents();
        });

        wholeBox.getChildren().addAll(add, remove);

        for (Map.Entry<Integer, Rune> entry : unit.getRune_inventory().entrySet()) {
            HBox row = createRuneInventoryRow(entry);
            wholeBox.getChildren().add(row);
        }

        return wholeBox;
    }

    public Node inventoryPaneContent() {
        VBox wholeBox = new VBox();
        wholeBox.getChildren().add(createHeaderRow());

        for (Map.Entry<Integer, Item> entry : unit.getInventoryItems().entrySet()) {
            HBox row = createInventoryRow(entry);
            wholeBox.getChildren().add(row);
        }
        Button add = new Button("+");
        Button remove = new Button("-");
        HBox modifySlot = new HBox();
        add.setOnAction(e-> {
            unit.getInventoryManager().increaseSlot();
            refreshContents();
        });
        remove.setOnAction(e-> {
            unit.getInventoryManager().decreaseSlot();
            refreshContents();
        });
        modifySlot.getChildren().addAll(add, remove);
        wholeBox.getChildren().add(modifySlot);

        for (Map.Entry<Integer, Item> entry : unit.getBackpackItems().entrySet()) {
            HBox row = createBackpackRow(entry);
            wholeBox.getChildren().add(row);
        }

        return wholeBox;
    }



    public Node createCurrencyContent() {
        VBox whole_box = new VBox();

        for (CurrencyType type : CurrencyType.values()) {
            HBox row = new HBox();
            row.setStyle("-fx-border-width: 0 0 2 0; -fx-border-color: crimson; -fx-padding: 0");
            Label type_label = new Label(type.writeAsString());
            type_label.prefWidth(70);
            TextField amount = new TextField(unit.getPurse().get(type).toString());

            amount.setOnKeyReleased(e->{
                if (amount.getText().isEmpty()) return;
                if (Double.isNaN(Double.parseDouble(amount.getText()))) return;
                if (amount.getText().isEmpty()) {
                    unit.getPurse().put(type, 0);
                    return;
                }
                int input = Integer.parseInt(amount.getText());
                unit.getPurse().put(type, input);
            });

            HBox nameBox = new HBox(type_label);
            nameBox.setAlignment(Pos.CENTER_LEFT);
            nameBox.setPrefWidth(70);
            HBox amountBox = new HBox(amount);
            amountBox.setAlignment(Pos.CENTER_LEFT);
            amountBox.setPrefWidth(50);

            nameBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: crimson; -fx-padding: 0");
            amountBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: crimson; -fx-padding: 0");

            row.getChildren().addAll(nameBox, amountBox);
            whole_box.getChildren().add(row);
        }
        return whole_box;
    }

    private HBox createHeaderRow() {
        HBox header = new HBox();
        header.setPrefWidth(1500);

        header.getChildren().addAll(
                createHeaderLabel("Slot", 50),
                createHeaderLabel("qty", 111),
                createHeaderLabel("Type", 100),
                createHeaderLabel("Name", 200),
                createHeaderLabel("Description", 400),
                createHeaderLabel("Status", 300)
        );

        return header;
    }

    private HBox createHeaderLabel(String text, double width) {
        Label label = new Label(text);
        label.setPrefSize(width, 40);
        label.setStyle("-fx-font-size: 20px; -fx-padding: 0");
        label.setAlignment(Pos.CENTER);
        HBox content = new HBox(label);
        content.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: hotpink; -fx-padding: 0");
        return content;
    }

    private HBox createInventoryRow(Map.Entry<Integer, Item> entry) {
        HBox row = new HBox();
        row.setPrefWidth(1500);
        row.setStyle("-fx-border-width: 0 0 2 0; -fx-border-color: blue; -fx-padding: 0");

        int slotIndex = entry.getKey();
        Item item = entry.getValue();

        Label slotNum = createLabel(String.valueOf(slotIndex + 1), 50);
        TextField quantityField = new TextField(unit.getInventoryItemAmount().get(slotIndex).toString());
        quantityField.setPrefSize(80, 40);
        quantityField.setStyle("-fx-font-size: 20px;");
        Label itemType = createLabel(item.getItemType().writeAsString(), 100);
        Label itemDesc = createWrappingLabel(item.getLore(), 400);
        Label itemStatus = createWrappingLabel(item.getStatusDescription() + item.getDescription(), 300);

        TextField itemName = new TextField(item.getName());
        itemName.setPrefSize(200, 40);
        itemName.setStyle("-fx-font-size: 20px; -fx-padding: 0");

        Popup itemPopup = new Popup();
        ListView<String> itemListView = createItemSearchList(itemName, itemPopup, slotIndex);
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

        // quantity control
        Button plus = new Button("+");
        Button minus = new Button("-");
        plus.setOnAction(e -> {
            unit.getInventoryManager().increaseQuantityByOne(slotIndex);
            refreshContents();
        });
        minus.setOnAction(e -> {
            unit.getInventoryManager().decreaseQuantityByOne(slotIndex);
            refreshContents();
        });
        quantityField.setOnKeyReleased(e-> {
            try {
                unit.getInventoryManager().setQuantity(slotIndex, Integer.parseInt(quantityField.getText()));
            } catch (NumberFormatException ex) {
                refreshContents();
            }
        });
        HBox quantityBox = new HBox(quantityField, plus, minus);
        quantityBox.setAlignment(Pos.CENTER_LEFT);
        HBox slotNumBox = new HBox(slotNum);
        slotNumBox.setAlignment(Pos.CENTER_LEFT);
        HBox itemTypeBox = new HBox(itemType);
        itemTypeBox.setAlignment(Pos.CENTER_LEFT);
        HBox itemNameBox = new HBox(itemName);
        itemNameBox.setAlignment(Pos.CENTER_LEFT);
        HBox itemDescBox = new HBox(itemDesc);
        itemDescBox.setAlignment(Pos.CENTER_LEFT);
        HBox itemStatusBox = new HBox(itemStatus);
        itemStatusBox.setAlignment(Pos.CENTER_LEFT);

        quantityBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: blue; -fx-padding: 0");
        slotNumBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: blue; -fx-padding: 0");
        itemTypeBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: blue; -fx-padding: 0");
        itemNameBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: blue; -fx-padding: 0");
        itemDescBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: blue; -fx-padding: 0");
        itemStatusBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: blue; -fx-padding: 0");


        Label weight = new Label("Weight: "+Integer.toString(item.getWeight()));
        weight.setStyle("-fx-font-size: 14px");
        weight.setPrefWidth(100);
        weight.setAlignment(Pos.CENTER);
        Button to_backpack = new Button("V");
        to_backpack.setOnAction(e -> {
            int amount = unit.getInventoryItemAmount().get(slotIndex);
            for (int i = 0 ; i < amount ; i++) {
                unit.getInventoryManager().addItemToBackpack(item);
            }
            unit.getInventoryManager().removeItem(item.getName());
            unit.calculateBackpackSlot();
            refreshContents();
        });

        row.getChildren().addAll(slotNumBox, quantityBox, itemTypeBox, itemNameBox, itemDescBox, itemStatusBox, weight, to_backpack);
        return row;
    }

    private HBox createBackpackRow(Map.Entry<Integer, Item> entry) {
        HBox row = new HBox();
        row.setPrefWidth(1000);
        row.setStyle("-fx-border-width: 0 0 2 0; -fx-border-color: teal; -fx-padding: 0");

        int slotIndex = entry.getKey();
        Item item = entry.getValue();

        Label slotNum = createLabel(String.valueOf(slotIndex + 1), 50);
        Label weight = new Label(Integer.toString(unit.getBackpackItems().get(slotIndex).getWeight()));
        weight.setStyle("-fx-font-size: 16px");
        weight.setPrefWidth(35);
        weight.setAlignment(Pos.CENTER);
        Label itemType = createLabel(item.getItemType().writeAsString(), 100);
        Label itemDesc = createWrappingLabel(item.getLore(), 400);
        Label itemStatus = createWrappingLabel(item.getStatusDescription() + item.getDescription(), 300);

        TextField itemName = new TextField(item.getName());
        itemName.setPrefSize(200, 40);
        itemName.setStyle("-fx-font-size: 20px; -fx-padding: 0");

        Popup itemPopup = new Popup();
        ListView<String> itemListView = createItemSearchList(itemName, itemPopup, slotIndex);
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

        // quantity control
        HBox weightBox = new HBox(weight);
        weightBox.setAlignment(Pos.CENTER_LEFT);
        HBox slotNumBox = new HBox(slotNum);
        slotNumBox.setAlignment(Pos.CENTER_LEFT);
        HBox itemTypeBox = new HBox(itemType);
        itemTypeBox.setAlignment(Pos.CENTER_LEFT);
        HBox itemNameBox = new HBox(itemName);
        itemNameBox.setAlignment(Pos.CENTER_LEFT);
        HBox itemDescBox = new HBox(itemDesc);
        itemDescBox.setAlignment(Pos.CENTER_LEFT);
        HBox itemStatusBox = new HBox(itemStatus);
        itemStatusBox.setAlignment(Pos.CENTER_LEFT);

        weightBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: teal; -fx-padding: 0");
        slotNumBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: teal; -fx-padding: 0");
        itemTypeBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: teal; -fx-padding: 0");
        itemNameBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: teal; -fx-padding: 0");
        itemDescBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: teal; -fx-padding: 0");
        itemStatusBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: teal; -fx-padding: 0");

        Button to_inventory = new Button("^");
        to_inventory.setOnAction(e -> {
            unit.getInventoryManager().addItem(item);
            unit.getInventoryManager().removeItemFromBackpack(slotIndex);
            unit.calculateBackpackSlot();
            refreshContents();
        });

        row.getChildren().addAll(slotNumBox, weightBox, itemTypeBox, itemNameBox, itemDescBox, itemStatusBox, to_inventory);
        return row;
    }

    private HBox createRuneInventoryRow(Map.Entry<Integer, Rune> entry) {
        HBox row = new HBox();
        row.setPrefWidth(1000);
        row.setStyle("-fx-border-width: 0 0 2 0; -fx-border-color: crimson; -fx-padding: 0");

        int slotIndex = entry.getKey();
        Rune rune = entry.getValue();
        if (rune == null) return new HBox();
        Label slotNum = createLabel(String.valueOf(slotIndex + 1), 50);
        Label itemStatus = createWrappingLabel(rune.getStatusDescription() + rune.getDescription(), 300);

        TextField itemName = new TextField(rune.getName());
        itemName.setPrefSize(200, 40);
        itemName.setStyle("-fx-font-size: 20px; -fx-padding: 0");

        Popup itemPopup = new Popup();
        ListView<String> itemListView = createRuneSearchList(itemName, itemPopup, slotIndex);
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

        Button randomise = new Button("Randomise Stat");
        randomise.setOnAction(e -> {
            rune.randomise_stats(unit);
            refreshContents();
        });
        HBox slotNumBox = new HBox(slotNum);
        slotNumBox.setAlignment(Pos.CENTER_LEFT);
        HBox itemNameBox = new HBox(itemName);
        itemNameBox.setAlignment(Pos.CENTER_LEFT);
        HBox itemStatusBox = new HBox(itemStatus);
        itemStatusBox.setAlignment(Pos.CENTER_LEFT);

        slotNumBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: teal; -fx-padding: 0");
        itemNameBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: teal; -fx-padding: 0");
        itemStatusBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: teal; -fx-padding: 0");

        row.getChildren().addAll(slotNumBox, itemNameBox, itemStatusBox, randomise);
        return row;
    }

    private Label createLabel(String text, double width) {
        Label label = new Label(text);
        label.setPrefSize(width, 40);
        label.setStyle("-fx-font-size: 16px; -fx-padding: 0");
        return label;
    }

    private Label createWrappingLabel(String text, double width) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setPrefWidth(width);
        label.setMinHeight(Region.USE_PREF_SIZE); // สำคัญ: ให้ความสูงปรับตามเนื้อหา
        label.setStyle("-fx-font-size: 16px; -fx-padding: 0");
        return label;
    }

    private ListView<String> createItemSearchList(TextField itemName, Popup popup, int slotIndex) {
        ListView<String> listView = new ListView<>();
        List<String> itemNames = database.getAllTypeItemMap().values().stream()
                .map(Item::getName)
                .collect(Collectors.toList());
        SearchableListView.makeSearchable(listView, FXCollections.observableArrayList(itemNames), itemName);

        listView.setOnMouseClicked(e -> {
            String selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                popup.hide();
                unit.getInventoryManager().replaceItem(slotIndex, database.getAllTypeItemMap().get(selected));
                refreshContents();
            }
        });

        listView.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                String selected = listView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    popup.hide();
                    unit.getInventoryManager().replaceItem(slotIndex, database.getAllTypeItemMap().get(selected));
                    refreshContents();
                }
            } else if (e.getCode() == KeyCode.ESCAPE) {
                popup.hide();
                e.consume();
            }
        });

        return listView;
    }

    private ListView<String> createRuneSearchList(TextField itemName, Popup popup, int slotIndex) {
        ListView<String> listView = new ListView<>();
        List<String> itemNames = database.getAllRuneMap().values().stream()
                .map(Item::getName)
                .collect(Collectors.toList());
        SearchableListView.makeSearchable(listView, FXCollections.observableArrayList(itemNames), itemName);

        listView.setOnMouseClicked(e -> {
            String selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                popup.hide();
                unit.getRune_inventory().put(slotIndex, database.getAllRuneMap().get(selected));
                refreshContents();
            }
        });

        listView.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                String selected = listView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    popup.hide();
                    unit.getRune_inventory().put(slotIndex, database.getAllRuneMap().get(selected));
                    refreshContents();
                }
            } else if (e.getCode() == KeyCode.ESCAPE) {
                popup.hide();
                e.consume();
            }
        });

        return listView;
    }

    public Node resourcePanel() {
        VBox contentBox = new VBox();
        for(ResourceType resourceType : ResourceType.values()) {
            VBox resourceBox = new VBox();
            resourceBox.setMinWidth(300);
            resourceBox.setMinHeight(100);
            double usable = unit.getResources().get(resourceType).getUsable();
            DecimalFormat df = new DecimalFormat("0.##");
            double reservedPercent = unit.getResources().get(resourceType).getReservedPercent();
            double reservedFlat = unit.getResources().get(resourceType).getReservedFlat();
            double remaining = unit.getResources().get(resourceType).getRemaining();
            Label maxLabel = new Label("Max "+ resourceType.writeAsString() + ": "+ usable);
            Label reservedLabel = new Label("Reserved "+ resourceType.writeAsString() + ": " + df.format(reservedPercent*100) + "%, Flat: "+ df.format(reservedFlat));
            reservedLabel.setStyle("-fx-font-size: 16px");
            TextField remainingField = new TextField();
            if (resourceType == ResourceType.HEALTH) {
                maxLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: crimson;");
            }
            if (resourceType == ResourceType.MANA) {
                maxLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: dodgerblue;");
            }
            if (resourceType == ResourceType.DEBRIS) {
                maxLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: goldenrod;");
            }

            remainingField.setText(df.format(remaining));
            remainingField.setOnKeyReleased(e -> {
                if (e.getCode() == KeyCode.ENTER) {
                    double oldRemaining = unit.getResources().get(resourceType).getRemaining();
                    double toSendBack = FormulaUtils.evaluateFormula(remainingField.getText());
                    unit.getResources().get(resourceType).setRemaining(toSendBack);
                    System.out.printf("[LOG] Unit: %s | Resource: %s | Remaining: %.2f -> %.2f (Change: %.2f)%n",
                            unit.getName(),
                            resourceType.name(),
                            oldRemaining,
                            toSendBack,
                            toSendBack - oldRemaining
                    );
                    refreshContents();
                }
            });
            resourceBox.getChildren().addAll(maxLabel, reservedLabel, remainingField);
            contentBox.getChildren().add(resourceBox);
        }
        Label backpack_slot_label = new Label("Backpack Slot: "+unit.getBackpackSlot());
        backpack_slot_label.setStyle("-fx-font-size: 22px; -fx-text-fill: teal;");
        Button short_rest = new Button("Short Rest");
        Button short_sleep = new Button("Short Sleep");
        Button full_rest = new Button("Full Rest");

        short_rest.setOnAction(e-> {
            double old_hp = unit.getResources().get(ResourceType.HEALTH).getRemaining();
            double usable_hp = unit.getResources().get(ResourceType.HEALTH).getUsable();
            double usable_mp = unit.getResources().get(ResourceType.MANA).getUsable();
            double missing = usable_hp - old_hp;
            unit.sumRemainingHealth(missing*0.5);
            unit.setRemainingMana(usable_mp);
            refreshContents();
        });
        short_sleep.setOnAction(e-> {
            double usable_hp = unit.getResources().get(ResourceType.HEALTH).getUsable();
            double usable_mp = unit.getResources().get(ResourceType.MANA).getUsable();
            unit.sumRemainingHealth(usable_hp*0.5);
            unit.setRemainingMana(usable_mp);
            refreshContents();
        });
        full_rest.setOnAction(e-> {
            double usable_hp = unit.getResources().get(ResourceType.HEALTH).getUsable();
            double usable_mp = unit.getResources().get(ResourceType.MANA).getUsable();
            unit.setRemainingHealth(usable_hp);
            unit.setRemainingMana(usable_mp);
            refreshContents();
        });
        contentBox.getChildren().addAll(backpack_slot_label,short_rest, short_sleep, full_rest);
        return contentBox;
    }

    public Node uniquePanel() {
        VBox contentBox = new VBox(10); // เพิ่ม spacing ระหว่างกล่องแต่ละแถว
        contentBox.setPadding(new Insets(10)); // เพิ่ม padding รอบนอก

        for (UniqueModifier uniqueModifier : unit.getUniqueModifier()) {
            HBox uniqueBox = new HBox(10); // เพิ่ม spacing ระหว่างองค์ประกอบในแถว
            uniqueBox.setPadding(new Insets(5));
            uniqueBox.setAlignment(Pos.CENTER_LEFT); // จัดให้อยู่ชิดซ้ายและเรียงกลางแนวตั้ง
            uniqueBox.setStyle("-fx-border-color: #ccc; -fx-background-color: #292929;");

            UniqueType to_get_string = uniqueModifier.getName();
            String unique_name = "";
            if (uniqueModifier.getName() != null) {
                unique_name = uniqueModifier.getName().writeAsString();
            }
            Label name = new Label(unique_name);
            name.setMinWidth(100);

            CheckBox active = new CheckBox();
            active.setSelected(uniqueModifier.isActive());

            Button delete = new Button("Delete");
            delete.setStyle("-fx-text-fill: red;");

            active.setOnAction(e -> {
                boolean isActive = active.isSelected();
                uniqueModifier.setActive(isActive);
                unit.calculateEverything();
                refreshContents();
            });

            delete.setOnAction(e -> {
                unit.getUniqueModifier().removeIf(mod -> mod.getName() == uniqueModifier.getName());
                refreshContents();
            });

            uniqueBox.getChildren().addAll(active, name, delete);
            contentBox.getChildren().add(uniqueBox);
        }

        for (CityName cityName : unit.getCurrent_city()) {
            HBox cityBox = new HBox(10); // เพิ่ม spacing ระหว่างองค์ประกอบในแถว
            cityBox.setPadding(new Insets(5));
            cityBox.setAlignment(Pos.CENTER_LEFT); // จัดให้อยู่ชิดซ้ายและเรียงกลางแนวตั้ง
            cityBox.setStyle("-fx-border-color: #ccc; -fx-background-color: #292929;");

            String city_name = "";
            city_name = cityName.writeAsString();
            Label name = new Label(city_name);
            name.setMinWidth(100);

            Button delete = new Button("Delete");
            delete.setStyle("-fx-text-fill: red;");

            delete.setOnAction(e -> {
                unit.getCurrent_city().removeIf(city -> city == cityName);
                refreshContents();
            });

            cityBox.getChildren().addAll(name, delete);
            contentBox.getChildren().add(cityBox);
        }

        // แถวเพิ่ม Unique ใหม่
        HBox addBox = new HBox(10);
        addBox.setPadding(new Insets(10, 0, 0, 0));
        addBox.setAlignment(Pos.CENTER_LEFT);

        ComboBox<UniqueType> uniqueType = new ComboBox<>();
        uniqueType.getItems().addAll(UniqueType.values());
        uniqueType.setPromptText("Select Unique Type");

        Button add = new Button("Add Unique");
        add.setOnAction(e -> {
            UniqueType typeToAdd = uniqueType.getValue();
            if (typeToAdd != null) {
                unit.getUniqueModifier().add(new UniqueModifier(typeToAdd));
                refreshContents();
            }
        });

        HBox addCityBox = new HBox(10);
        addBox.setPadding(new Insets(10, 0, 0, 0));
        addBox.setAlignment(Pos.CENTER_LEFT);

        ComboBox<CityName> cityName = new ComboBox<>();
        cityName.getItems().addAll(CityName.values());
        cityName.setPromptText("Select City Name");

        Button addCity = new Button("Add City");
        addCity.setOnAction(e -> {
            CityName city = cityName.getValue();
            if (city != null) {
                unit.getCurrent_city().add(city);
                refreshContents();
            }
        });

        addBox.getChildren().addAll(uniqueType, add);
        addCityBox.getChildren().addAll(cityName, addCity);
        contentBox.getChildren().addAll(addBox, addCityBox);

        return contentBox;
    }

    public Node skillPanel() {
        VBox skillPaneContent = new VBox();
        skillPaneContent.setSpacing(2);

        // Header
        HBox header = new HBox();
        header.setSpacing(5);

        header.getChildren().addAll(
                createHeaderLabel("Slot", 50),
                createHeaderLabel("Type", 100),
                createHeaderLabel("Name", 150),
                createHeaderLabel("Description", 400),
                createHeaderLabel("Act", 100),
                createHeaderLabel("onCD", 100),
                createHeaderLabel("Cost", 100),
                createHeaderLabel("Cooldown", 100),
                createHeaderLabel("", 80) // For delete button
        );
        skillPaneContent.getChildren().add(header);

        int skillSlot = 1;
        for (Map.Entry<String, SkillInstance> entry : unit.getAllSkill().entrySet()) {
            HBox row = new HBox();
            row.setSpacing(5);
            row.setStyle("-fx-border-color: hotpink; -fx-border-width: 0 0 1 0;");

            Label slotNum = createLabel(String.valueOf(skillSlot++), 50);
            CheckBox activate = new CheckBox();
            activate.setSelected(entry.getValue().isReserving());
            activate.setOnAction(e-> {
                entry.getValue().setReserving(activate.isSelected());
                unit.calculateEverything();
                refreshContents();
            });
            Label skillType = createWrappingLabel(entry.getValue().getSkillData().getTranslatedTag(), 100);
            Label skillName = createWrappingLabel(entry.getValue().getSkillData().getName(), 150);
            Label skillDesc = createWrappingLabel(entry.getValue().getSkillData().getTranslatedDesc(), 400);
            Label skillAct = createWrappingLabel(entry.getValue().getSkillData().getActionType(), 100);
            Label skillCost = createWrappingLabel(entry.getValue().getSkillData().getTranslatedCost(), 100);
            Label skillCooldown = createWrappingLabel(entry.getValue().getSkillData().getTranslatedCooldown(), 100);

            HBox cdBox = new HBox(2);
            Label cdLabel = createLabel(String.valueOf(entry.getValue().getOnCooldown()), 40);
            Button plus = new Button("+");
            Button minus = new Button("-");
            plus.setOnAction(e -> {
                entry.getValue().cooldownIncrement();
                refreshContents();
            });
            minus.setOnAction(e -> {
                entry.getValue().cooldownDecrement();
                refreshContents();
            });
            cdBox.getChildren().addAll(cdLabel, plus, minus);
            cdBox.setMinWidth(100);
            cdBox.setMaxWidth(100);
            cdBox.setAlignment(Pos.CENTER_LEFT);

            Button deleteButton = new Button("Delete");
            deleteButton.setStyle("-fx-text-fill: red;");
            deleteButton.setOnAction(e -> {
                unit.getSkillList().remove(entry.getKey());
                refreshContents();
            });

            cdBox.setAlignment(Pos.CENTER_LEFT);
            HBox slotNumBox = new HBox(slotNum, activate);
            slotNumBox.setAlignment(Pos.CENTER_LEFT);
            HBox skillTypeBox = new HBox(skillType);
            skillTypeBox.setAlignment(Pos.CENTER_LEFT);
            HBox skillNameBox = new HBox(skillName);
            skillNameBox.setAlignment(Pos.CENTER_LEFT);
            HBox skillDescBox = new HBox(skillDesc);
            skillDescBox.setAlignment(Pos.CENTER_LEFT);
            HBox skillActBox = new HBox(skillAct);
            skillActBox.setAlignment(Pos.CENTER_LEFT);
            HBox skillCooldownBox = new HBox(skillCooldown);
            skillCooldownBox.setAlignment(Pos.CENTER_LEFT);
            HBox skillCostBox = new HBox(skillCost);
            skillCostBox.setAlignment(Pos.CENTER_LEFT);

            cdBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: hotpink; -fx-padding: 0");
            slotNumBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: hotpink; -fx-padding: 0");
            skillTypeBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: hotpink; -fx-padding: 0");
            skillNameBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: hotpink; -fx-padding: 0");
            skillDescBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: hotpink; -fx-padding: 0");
            skillActBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: hotpink; -fx-padding: 0");
            skillCooldownBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: hotpink; -fx-padding: 0");
            skillCostBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: hotpink; -fx-padding: 0");

            row.getChildren().addAll(slotNumBox, skillTypeBox, skillNameBox, skillDescBox, skillActBox, cdBox, skillCostBox, skillCooldownBox, deleteButton);
            skillPaneContent.getChildren().add(row);
        }

        // Add Skill section
        HBox addBox = new HBox(5);
        TextField skillNameField = new TextField("Name");
        Button createButton = new Button("Add");
        addBox.getChildren().addAll(skillNameField, createButton);

        Popup choosePopup = new Popup();
        ListView<String> itemListView = new ListView<>();
        List<String> itemNameList = SkillFactory.skillNames;

        createButton.setOnAction(e -> {
            String name = skillNameField.getText();
            if (SkillFactory.skillNames.contains(name)) {
                unit.getSkillList().put(name, new SkillInstance(SkillFactory.getSkill(name, unit, false)));
                unit.reloadSkill();
                refreshContents();
            } else {
                System.out.println("Skill Not Found");
                refreshContents();
            }
        });

        SearchableListView.makeSearchable(itemListView, FXCollections.observableArrayList(itemNameList), skillNameField);
        itemListView.setOnMouseClicked(e -> {
            String selectedItem = itemListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                choosePopup.hide();
                skillNameField.setText(selectedItem);
            }
        });
        itemListView.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.ESCAPE) {
                String selectedItem = itemListView.getSelectionModel().getSelectedItem();
                choosePopup.hide();
                skillNameField.setText(selectedItem);
                e.consume();
            }
        });
        choosePopup.getContent().add(itemListView);

        skillNameField.setOnKeyReleased(e -> {
            if (!choosePopup.isShowing()) {
                Bounds screenBounds = skillNameField.localToScreen(skillNameField.getBoundsInParent());
                if (screenBounds != null) {
                    choosePopup.show(skillNameField, screenBounds.getMaxX(), screenBounds.getMinY());
                }
            }
            if (e.getCode() == KeyCode.ESCAPE) {
                choosePopup.hide();
                e.consume();
            }
        });

        skillPaneContent.getChildren().add(addBox);
        return skillPaneContent;
    }

    private Node counterPanel() {
        VBox counterPaneContent = new VBox();
        counterPaneContent.setSpacing(2);

        // Header
        HBox header = new HBox();
        header.setSpacing(5);

        header.getChildren().addAll(
                createHeaderLabel("Name", 200),
                createHeaderLabel("Count", 100),
                createHeaderLabel("", 80)
        );
        counterPaneContent.getChildren().add(header);

        for (Map.Entry<CounterName, Double> entry : unit.getCounter().entrySet()) {
            HBox row = new HBox();
            row.setSpacing(5);
            row.setStyle("-fx-border-color: hotpink; -fx-border-width: 0 0 1 0;");

            Label countName = createWrappingLabel(entry.getKey().writeAsString(), 200);

            HBox countBox = new HBox(2);
            TextField countLabel = new TextField(String.valueOf(entry.getValue()));
            Button plus = new Button("+");
            Button minus = new Button("-");
            plus.setOnAction(e -> {
                unit.counterIncrement(entry.getKey());
                refreshContents();
            });
            minus.setOnAction(e -> {
                unit.counterDecrement(entry.getKey());
                refreshContents();
            });
            countLabel.setOnKeyReleased(e-> {
                unit.counterSet(entry.getKey(), Double.parseDouble(countLabel.getText()));
            });
            countBox.getChildren().addAll(countLabel, plus, minus);
            countBox.setMinWidth(150);
            countBox.setMaxWidth(150);
            countBox.setAlignment(Pos.CENTER_LEFT);

            Button deleteButton = new Button("Delete");
            deleteButton.setStyle("-fx-text-fill: red;");
            deleteButton.setOnAction(e -> {
                unit.getCounter().remove(entry.getKey());
                unit.getRawCounterMap().remove(entry.getKey());
                refreshContents();
            });

            countBox.setAlignment(Pos.CENTER_LEFT);
            HBox skillNameBox = new HBox(countName);
            skillNameBox.setAlignment(Pos.CENTER_LEFT);

            countBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: hotpink; -fx-padding: 0");
            skillNameBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: hotpink; -fx-padding: 0");

            row.getChildren().addAll(skillNameBox, countBox, deleteButton);
            counterPaneContent.getChildren().add(row);
        }

        // Add Counter section
        HBox addBox = new HBox(5);
        TextField counterNameField = new TextField("Counter Name");
        Button createButton = new Button("Add");
        addBox.getChildren().addAll(counterNameField, createButton);
        List<String> itemNameList = new ArrayList<>();

        Popup choosePopup = new Popup();
        ListView<String> itemListView = new ListView<>();
        for (CounterName name : CounterName.values()) {
            itemNameList.add(name.writeAsString());
        }

        createButton.setOnAction(e -> {
            String name = counterNameField.getText();
            for (CounterName counterName : CounterName.values()) {
                if (counterName.writeAsString().equals(name)) {
                    unit.getCounter().put(counterName, 0.0);
                    unit.getRawCounterMap().put(counterName, 0.0);
                }
            }
            refreshContents();
        });

        SearchableListView.makeSearchable(itemListView, FXCollections.observableArrayList(itemNameList), counterNameField);
        itemListView.setOnMouseClicked(e -> {
            String selectedItem = itemListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                choosePopup.hide();
                counterNameField.setText(selectedItem);
            }
        });
        itemListView.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.ESCAPE) {
                String selectedItem = itemListView.getSelectionModel().getSelectedItem();
                choosePopup.hide();
                counterNameField.setText(selectedItem);
                e.consume();
            }
        });
        choosePopup.getContent().add(itemListView);

        counterNameField.setOnKeyReleased(e -> {
            if (!choosePopup.isShowing()) {
                Bounds screenBounds = counterNameField.localToScreen(counterNameField.getBoundsInParent());
                if (screenBounds != null) {
                    choosePopup.show(counterNameField, screenBounds.getMaxX(), screenBounds.getMinY());
                }
            }
            if (e.getCode() == KeyCode.ESCAPE) {
                choosePopup.hide();
                e.consume();
            }
        });

        counterPaneContent.getChildren().add(addBox);
        return counterPaneContent;
    }

    private Label createHeaderLabel(String text, int width) {
        Label label = new Label(text);
        label.setMinWidth(width);
        label.setMaxWidth(width);
        label.setMinHeight(30);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        return label;
    }


    public void cardEquipped(String cardName, CardType type) {
        Card toEquip = database.getAllCardMap().get(cardName);
            unit.getCardManager().replaceCard(type, toEquip);
    }

    public void refreshContents() {
        row1.getChildren().clear();
        row2.getChildren().clear();
        row1.getChildren().addAll(basicInfoContent(), statPanel1Content(), statPanel2Content(), statPanel3Content(), resourcePanel(), uniquePanel(), counterPanel());
        row2.getChildren().addAll(inventoryPaneContent(),equipmentPaneContent(), skillPanel());
    }

}
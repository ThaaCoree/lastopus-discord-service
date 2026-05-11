package ui;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import app.Database;
import model.entity.*;
import model.modifier.BasicModifier;
import model.modifier.SkillModifier;
import model.modifier.TransferModifier;
import model.type.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CardEditPanel extends ScrollPane {

    private final Database database;
    private final ToggleGroup modeToggle = new ToggleGroup();
    private final Button editModeBtn = new Button("Edit");
    private final Button createModeBtn = new Button("Create");
    private final List<Button> allBtn = new ArrayList<>();
    private boolean isEditMode = true;
    private final CardListPane listPane;
    private final VBox mainBox = new VBox();
    private final HBox mainButtonBox = new HBox();
    private boolean confirmDeletion = false;
    private Card toMake;

    public CardEditPanel(Database database, CardListPane listPane) {
        toMake = new Card();

        allBtn.add(editModeBtn);
        allBtn.add(createModeBtn);
        this.database = database;
        this.listPane = listPane;
        getStylesheets().add(getClass().getResource("/styles/theme.css").toExternalForm());
        setPrefWidth(400);
        getStyleClass().add("right-pane");

        mainButtonBox.getChildren().addAll(editModeBtn);

        editModeBtn.setOnAction(e-> {
            editMode();
        });
//        createModeBtn.setOnAction(e-> {
//            createMode();
//        });
//
//        createMode();
        editMode();
        setButtonToSelected(editModeBtn, allBtn);
        setContent(mainBox);
    }

    public void editMode() {
        mainBox.getChildren().clear();

        setButtonToSelected(editModeBtn, allBtn);
        isEditMode = true;
        confirmDeletion = false;
//        Button deleteButton = new Button("DELETE");

        VBox modeBox = new VBox();
        if (listPane.getListView() != null) {
            Card selectedCard = listPane.getListView().getSelectionModel().getSelectedItem();
//            deleteButton.setOnAction(e -> {
//                if (!confirmDeletion) {
//                    System.out.println("Delete button clicked, click again to confirm deletion.");
//                    confirmDeletion = true;
//                } else {
//                    if (selectedCard != null)
//                        database.getAllCard().removeIf(databaseItem -> selectedCard.getName().equals(databaseItem.getName()));
//                    listPane.getListView().refresh();
//                    listPane.getCardList().setAll(database.getAllCard());
//                    setButtonToSelected(createModeBtn, allBtn);
//                    editMode();
//                }
//            });

            if (selectedCard != null) {
                modeBox.getChildren().addAll(
                        editTypeNameArea(CardType.PRIMARY, selectedCard),
                        editAbi(CardType.PRIMARY, selectedCard),
                        statusAndStats(CardType.PRIMARY, selectedCard),
                        editTypeNameArea(CardType.SECONDARY, selectedCard),
                        editAbi(CardType.SECONDARY, selectedCard),
                        statusAndStats(CardType.SECONDARY, selectedCard)
                );
            }
        }
        mainBox.getChildren().addAll(mainButtonBox,modeBox);
    }

//    public void createMode() {
//        mainBox.getChildren().clear();
//        setButtonToSelected(createModeBtn, allBtn);
//        isEditMode = false;
//        confirmDeletion = false;
//
//        toMake = new Card("New Card");
//        Button createButton = new Button("CREATE");
//        VBox modeBox = new VBox();
//
//        createButton.setOnAction(e -> {
//            database.getAllCard().add(toMake);
//            database.updateEveryMap();
//            database.translateEverything();
//            listPane.getCardList().setAll(database.getAllCard());
//            listPane.getListView().refresh();
//            editMode();
//        });
//        modeBox.getChildren().addAll(
//                editNameArea(toMake),
//                editTypeNameArea(CardType.PRIMARY, toMake),
//                editAbi(CardType.PRIMARY, toMake),
//                statusAndStats(CardType.PRIMARY, toMake),
//                editTypeNameArea(CardType.SECONDARY, toMake),
//                editAbi(CardType.SECONDARY, toMake),
//                statusAndStats(CardType.SECONDARY,toMake)
//                );
//
//        mainBox.getChildren().addAll(mainButtonBox, createButton,modeBox);
//    }

    public Node editNameArea(Card card) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("Name");
        TextArea textArea = new TextArea(card.getName());
        textArea.setWrapText(true);
        textArea.setMaxHeight(100);
        textArea.setMaxWidth(350);
        textArea.setOnKeyReleased(event -> {
            card.setName(textArea.getText());
            listPane.getListView().refresh();
        });
        contentBox.getChildren().addAll(indicatorLabel,textArea);
        return contentBox;
    }

    public Node editTypeNameArea(CardType type, Card card) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label(type.writeAsString() + " Name");
        TextArea textArea = new TextArea(card.getAbilityName().get(type));
        textArea.setWrapText(true);
        textArea.setMaxHeight(100);
        textArea.setMaxWidth(350);
        textArea.setOnKeyReleased(event -> {
            card.getAbilityName().put(type, textArea.getText());
            listPane.getListView().refresh();
        });
        contentBox.getChildren().addAll(indicatorLabel,textArea);
        return contentBox;
    }

    public Node editAbi(CardType type, Card card) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("Description");
        TextArea textArea = new TextArea(card.getDescription().get(type));
        textArea.setWrapText(true);
        textArea.setMaxHeight(100);
        textArea.setMaxWidth(350);
        textArea.setOnKeyReleased(event -> {
            card.getDescription().put(type, textArea.getText());
            listPane.getListView().refresh();
        });
        contentBox.getChildren().addAll(indicatorLabel,textArea);
        return contentBox;
    }

    public Node statusAndStats(CardType type, Card card) {
        VBox contentBox = new VBox();
        VBox statBox = new VBox();
        VBox statusBox = new VBox();
        VBox transferBox = new VBox();
        VBox skillModBox = new VBox();

        Button addStatus = new Button("Add "+ type.writeAsString() +" Status");
        Button addStat = new Button("Add "+ type.writeAsString() +" Stat");
        Button addTransfer = new Button("Add "+ type.writeAsString() +" Transfer");
        Button addSkillMod = new Button("Add " + type.writeAsString() + " SkillMod");
        statusBox.getChildren().add(addStatus);
        statBox.getChildren().add(addStat);
        transferBox.getChildren().add(addTransfer);
        skillModBox.getChildren().add(addSkillMod);

        AtomicInteger transferCount = new AtomicInteger(0);

        addStatus.setOnAction(e -> {
            createStatusField(card, statusBox, type);
        });
        addStat.setOnAction(e -> {
            createStatField(card, statBox, type);
        });
        addTransfer.setOnAction(e -> {
            createTransferField(card, transferBox, transferCount, type);
        });
        addSkillMod.setOnAction(e -> {
            createSkillModifierField(card, skillModBox, type);
        });

        editStatusField(card, statusBox, type);
        editStatField(card, statBox, type);
        editTransferField(card, transferBox, transferCount, type);

        contentBox.getChildren().addAll(statusBox, statBox, transferBox, skillModBox);
        return contentBox;
    }

    public void createSkillModifierField(Card card, VBox skillModBox, CardType cardType) {
        ComboBox<SkillType> tag = new ComboBox<>();
        for (SkillType type : SkillType.values()) {
            tag.getItems().add(type);
        }
        TextField flat = new TextField();
        TextField mult = new TextField();
        flat.setPromptText("Flat");
        mult.setPromptText("Mult");
        tag.setOnAction(e-> {
            try {
                card.getModifiers().get(cardType).getSkillModifierSafe(tag.getValue()).setFlat(Double.parseDouble(flat.getText()));
            } catch (NumberFormatException ex) {
                card.getModifiers().get(cardType).getSkillModifierSafe(tag.getValue()).setFlat(0);
            }
            try {
                card.getModifiers().get(cardType).getSkillModifierSafe(tag.getValue()).setMult(Double.parseDouble(mult.getText()));
            } catch (NumberFormatException ex) {
                card.getModifiers().get(cardType).getSkillModifierSafe(tag.getValue()).setMult(0);
            }
        });
        flat.setOnKeyReleased(e-> {
            try {
                card.getModifiers().get(cardType).getSkillModifierSafe(tag.getValue()).setFlat(Double.parseDouble(flat.getText()));
            } catch (NumberFormatException ex) {
                card.getModifiers().get(cardType).getSkillModifierSafe(tag.getValue()).setFlat(0);
            }
            try {
                card.getModifiers().get(cardType).getSkillModifierSafe(tag.getValue()).setMult(Double.parseDouble(mult.getText()));
            } catch (NumberFormatException ex) {
                card.getModifiers().get(cardType).getSkillModifierSafe(tag.getValue()).setMult(0);
            }
        });
        mult.setOnKeyReleased(e-> {
            try {
                card.getModifiers().get(cardType).getSkillModifierSafe(tag.getValue()).setFlat(Double.parseDouble(flat.getText()));
            } catch (NumberFormatException ex) {
                card.getModifiers().get(cardType).getSkillModifierSafe(tag.getValue()).setFlat(0);
            }
            try {
                card.getModifiers().get(cardType).getSkillModifierSafe(tag.getValue()).setMult(Double.parseDouble(mult.getText()));
            } catch (NumberFormatException ex) {
                card.getModifiers().get(cardType).getSkillModifierSafe(tag.getValue()).setMult(0);
            }
        });
        skillModBox.getChildren().addAll(tag, flat, mult);
    }

    public void editSkillModifierField(Card card, VBox skillModBox, CardType cardType) {
        for (Map.Entry<SkillType, SkillModifier> entry : card.getModifiers().get(cardType).getSkillModifiers().entrySet()) {
            ComboBox<SkillType> tag = new ComboBox<>();
            for (SkillType type : SkillType.values()) {
                tag.getItems().add(type);
            }
            tag.setValue(entry.getKey());
            TextField flat = new TextField();
            TextField mult = new TextField();
            flat.setPromptText("Flat");
            mult.setPromptText("Mult");
            flat.setText(Double.toString(entry.getValue().getFlat()));
            mult.setText(Double.toString(entry.getValue().getMult()));
            tag.setOnAction(e -> {
                try {
                    card.getModifiers().get(cardType).getSkillModifiers().get(tag.getValue()).setFlat(Double.parseDouble(flat.getText()));
                } catch (NumberFormatException ex) {
                    card.getModifiers().get(cardType).getSkillModifiers().get(tag.getValue()).setFlat(0);
                }
                try {
                    card.getModifiers().get(cardType).getSkillModifiers().get(tag.getValue()).setMult(Double.parseDouble(mult.getText()));
                } catch (NumberFormatException ex) {
                    card.getModifiers().get(cardType).getSkillModifiers().get(tag.getValue()).setMult(0);
                }
            });
            flat.setOnKeyReleased(e -> {
                try {
                    card.getModifiers().get(cardType).getSkillModifiers().get(tag.getValue()).setFlat(Double.parseDouble(flat.getText()));
                } catch (NumberFormatException ex) {
                    card.getModifiers().get(cardType).getSkillModifiers().get(tag.getValue()).setFlat(0);
                }
                try {
                    card.getModifiers().get(cardType).getSkillModifiers().get(tag.getValue()).setMult(Double.parseDouble(mult.getText()));
                } catch (NumberFormatException ex) {
                    card.getModifiers().get(cardType).getSkillModifiers().get(tag.getValue()).setMult(0);
                }
            });
            mult.setOnKeyReleased(e -> {
                try {
                    card.getModifiers().get(cardType).getSkillModifiers().get(tag.getValue()).setFlat(Double.parseDouble(flat.getText()));
                } catch (NumberFormatException ex) {
                    card.getModifiers().get(cardType).getSkillModifiers().get(tag.getValue()).setFlat(0);
                }
                try {
                    card.getModifiers().get(cardType).getSkillModifiers().get(tag.getValue()).setMult(Double.parseDouble(mult.getText()));
                } catch (NumberFormatException ex) {
                    card.getModifiers().get(cardType).getSkillModifiers().get(tag.getValue()).setMult(0);
                }
            });
            skillModBox.getChildren().addAll(tag, flat, mult);
        }
    }

    public void createTransferField(Card card, VBox transferBox, AtomicInteger transferCount, CardType type) {
        final int transferIndex = transferCount.getAndIncrement();
        TransferModifier transferToPut = new TransferModifier();
        ComboBox<TransferType> transferType = new ComboBox<>();
        ComboBox<StatusType> statusSourceCombo = new ComboBox<>();
        ComboBox<StatusType> statusTargetCombo = new ComboBox<>();
        ComboBox<StatType> statSourceCombo = new ComboBox<>();
        ComboBox<StatType> statTargetCombo = new ComboBox<>();
        TextArea transferPercent = new TextArea();
        TextArea transferRatio = new TextArea();
        transferPercent.setPromptText("Transfer Percent");
        transferRatio.setPromptText("Transfer Ratio");
        transferPercent.setMaxHeight(40);
        transferRatio.setMaxHeight(40);
        transferPercent.setMaxWidth(350);
        transferRatio.setMaxWidth(350);


        transferType.getItems().addAll(TransferType.values());
        statusSourceCombo.getItems().addAll(StatusType.values());
        statusTargetCombo.getItems().addAll(StatusType.values());
        statSourceCombo.getItems().addAll(StatType.values());
        statTargetCombo.getItems().addAll(StatType.values());

        Runnable update = () -> updateTransferModifier(
                transferToPut, card, transferType.getValue(),
                statusSourceCombo.getValue(),
                statusTargetCombo.getValue(),
                statSourceCombo.getValue(),
                statTargetCombo.getValue(),
                transferPercent.getText(),
                transferRatio.getText(),
                transferIndex, type
        );

        transferType.setOnAction(e -> update.run());
        statusSourceCombo.setOnAction(e -> update.run());
        statusTargetCombo.setOnAction(e -> update.run());
        statSourceCombo.setOnAction(e -> update.run());
        statTargetCombo.setOnAction(e -> update.run());
        transferPercent.setOnKeyReleased(e -> update.run());
        transferRatio.setOnKeyReleased(e -> update.run());

        transferBox.getChildren().addAll(transferType,statusSourceCombo,statusTargetCombo,statSourceCombo,statTargetCombo,transferPercent,transferRatio);
    }

    public void editTransferField(Card card, VBox transferBox, AtomicInteger transferCount, CardType type) {
        for (TransferModifier tm : card.getTransferModifiers(type).values()) {
            final int transferIndex = transferCount.getAndIncrement();
            TransferModifier transferToPut = new TransferModifier();
            ComboBox<TransferType> transferType = new ComboBox<>();
            ComboBox<StatusType> statusSourceCombo = new ComboBox<>();
            ComboBox<StatusType> statusTargetCombo = new ComboBox<>();
            ComboBox<StatType> statSourceCombo = new ComboBox<>();
            ComboBox<StatType> statTargetCombo = new ComboBox<>();
            TextArea transferPercent = new TextArea();
            TextArea transferRatio = new TextArea();
            transferPercent.setPromptText("Transfer Percent");
            transferRatio.setPromptText("Transfer Ratio");
            transferPercent.setMaxHeight(40);
            transferRatio.setMaxHeight(40);
            transferPercent.setMaxWidth(350);
            transferRatio.setMaxWidth(350);
            HBox row = new HBox();
            Button delType = new Button("Delete Transfer");
            delType.setOnAction(e -> {
                card.getTransferModifiers(type).remove(transferIndex);
                editMode();
            });
            row.getChildren().addAll(transferType, delType);

            transferType.setValue(tm.getTransferType());
            statusSourceCombo.setValue(tm.getSourceStatus());
            statusTargetCombo.setValue(tm.getTargetStatus());
            statSourceCombo.setValue(tm.getSourceStat());
            statTargetCombo.setValue(tm.getTargetStat());
            transferPercent.setText(Double.toString(tm.getTransferPercent()));
            transferRatio.setText(Double.toString(tm.getTransferRatio()));

            transferType.getItems().addAll(TransferType.values());
            statusSourceCombo.getItems().addAll(StatusType.values());
            statusTargetCombo.getItems().addAll(StatusType.values());
            statSourceCombo.getItems().addAll(StatType.values());
            statTargetCombo.getItems().addAll(StatType.values());

            Runnable update = () -> updateTransferModifier(
                    transferToPut, card, transferType.getValue(),
                    statusSourceCombo.getValue(),
                    statusTargetCombo.getValue(),
                    statSourceCombo.getValue(),
                    statTargetCombo.getValue(),
                    transferPercent.getText(),
                    transferRatio.getText(),
                    transferIndex, type
            );

            transferType.setOnAction(e -> update.run());
            statusSourceCombo.setOnAction(e -> update.run());
            statusTargetCombo.setOnAction(e -> update.run());
            statSourceCombo.setOnAction(e -> update.run());
            statTargetCombo.setOnAction(e -> update.run());
            transferPercent.setOnKeyReleased(e -> update.run());
            transferRatio.setOnKeyReleased(e -> update.run());

            transferBox.getChildren().addAll(row, statusSourceCombo, statusTargetCombo, statSourceCombo, statTargetCombo, transferPercent, transferRatio);
        }
    }

    public void updateTransferModifier(TransferModifier transferToPut, Card card,
                                       TransferType transferType, StatusType sourceStatus, StatusType targetStatus, StatType sourceStat, StatType targetStat,
                                       String transferPercent, String transferRatio, int transferIndex, CardType type) {
        if (transferType == null) return;

        double valuePercent = parseOrDefault(transferPercent, 0);
        double valueRatio = parseOrDefault(transferRatio, 1);

        transferToPut.setTransferType(transferType);
        transferToPut.setSourceStatus(sourceStatus);
        transferToPut.setTargetStatus(targetStatus);
        transferToPut.setSourceStat(sourceStat);
        transferToPut.setTargetStat(targetStat);
        transferToPut.setTransferPercent(valuePercent);
        transferToPut.setTransferRatio(valueRatio);

        card.getTransferModifiers(type).put(transferIndex, transferToPut);
    }

    public void createStatusField(Card card, VBox statusBox, CardType type) {
        BasicModifier basicModToPut = new BasicModifier();
        ComboBox<StatusType> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll(StatusType.values());

        Map<String, TextArea> fields = createModifierFields();

        Runnable update = () -> updateModifier(
                basicModToPut, card, typeCombo.getValue(),
                fields.get("flat").getText(),
                fields.get("mult").getText(),
                fields.get("override").getText(),
                fields.get("globalMult").getText(),
                fields.get("equipmentMult").getText(),
                fields.get("passiveMult").getText(),
                type
        );

        typeCombo.setOnAction(e -> update.run());
        fields.values().forEach(f -> f.setOnKeyReleased(e -> update.run()));

        statusBox.getChildren().add(typeCombo);
        statusBox.getChildren().addAll(fields.values());
    }


    public void editStatusField(Card card, VBox statusBox, CardType type) {
        for (Map.Entry<StatusType, BasicModifier> entry : card.getStatusModifiers(type).entrySet()) {
            BasicModifier basicModToPut = new BasicModifier();
            HBox row = new HBox();

            ComboBox<StatusType> typeCombo = new ComboBox<>();
            typeCombo.getItems().addAll(StatusType.values());
            typeCombo.setValue(entry.getKey());

            Button delType = new Button("Delete Status");
            delType.setOnAction(e -> {
                card.getStatusModifiers(type).remove(entry.getKey());
                editMode();
            });

            row.getChildren().addAll(typeCombo, delType);

            Map<String, TextArea> fields = createModifierFields();
            fillModifierFields(fields, entry.getValue());

            Runnable update = () -> updateModifier(
                    basicModToPut, card, typeCombo.getValue(),
                    fields.get("flat").getText(),
                    fields.get("mult").getText(),
                    fields.get("override").getText(),
                    fields.get("globalMult").getText(),
                    fields.get("equipmentMult").getText(),
                    fields.get("passiveMult").getText(),
                    type
            );

            typeCombo.setOnAction(e -> update.run());
            fields.values().forEach(f -> f.setOnKeyReleased(e -> update.run()));

            statusBox.getChildren().addAll(row);
            statusBox.getChildren().addAll(fields.values());
        }
    }

    public void createStatField(Card card, VBox statBox, CardType type) {
        BasicModifier basicModToPut = new BasicModifier();
        ComboBox<StatType> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll(StatType.values());

        Map<String, TextArea> fields = createModifierFields();

        Runnable update = () -> updateStatModifier(
                basicModToPut, card, typeCombo.getValue(),
                fields.get("flat").getText(),
                fields.get("mult").getText(),
                fields.get("override").getText(),
                fields.get("globalMult").getText(),
                fields.get("equipmentMult").getText(),
                fields.get("passiveMult").getText(),
                type
        );

        typeCombo.setOnAction(e -> update.run());
        fields.values().forEach(f -> f.setOnKeyReleased(e -> update.run()));

        statBox.getChildren().add(typeCombo);
        statBox.getChildren().addAll(fields.values());
    }


    public void editStatField(Card card, VBox statBox, CardType type) {
        for (Map.Entry<StatType, BasicModifier> entry : card.getStatModifiers(type).entrySet()) {
            BasicModifier basicModToPut = new BasicModifier();
            HBox row = new HBox();

            ComboBox<StatType> typeCombo = new ComboBox<>();
            typeCombo.getItems().addAll(StatType.values());
            typeCombo.setValue(entry.getKey());

            Button delType = new Button("Delete Stat");
            delType.setOnAction(e -> {
                card.getStatModifiers(type).remove(entry.getKey());
                editMode();
            });

            row.getChildren().addAll(typeCombo, delType);

            Map<String, TextArea> fields = createModifierFields();
            fillModifierFields(fields, entry.getValue());

            Runnable update = () -> updateStatModifier(
                    basicModToPut, card, typeCombo.getValue(),
                    fields.get("flat").getText(),
                    fields.get("mult").getText(),
                    fields.get("override").getText(),
                    fields.get("globalMult").getText(),
                    fields.get("equipmentMult").getText(),
                    fields.get("passiveMult").getText(),
                    type
            );

            typeCombo.setOnAction(e -> update.run());
            fields.values().forEach(f -> f.setOnKeyReleased(e -> update.run()));

            statBox.getChildren().addAll(row);
            statBox.getChildren().addAll(fields.values());
        }
    }

    private Map<String, TextArea> createModifierFields() {
        Map<String, TextArea> map = new LinkedHashMap<>();
        map.put("flat", new TextArea());
        map.put("mult", new TextArea());
        map.put("override", new TextArea());
        map.put("globalMult", new TextArea());
        map.put("equipmentMult", new TextArea());
        map.put("passiveMult", new TextArea());

        map.forEach((key, area) -> {
            area.setPromptText(capitalize(key));
            area.setMaxHeight(20);
            area.setMaxWidth(350);
        });

        return map;
    }

    private void fillModifierFields(Map<String, TextArea> fields, BasicModifier mod) {
        if (mod == null) return;

        fields.get("flat").setText(mod.getFlat() != 0.0 ? Double.toString(mod.getFlat()) : "");
        fields.get("mult").setText(mod.getMult() != 0.0 ? Double.toString(mod.getMult()) : "");
        fields.get("override").setText(!Double.isNaN(mod.getOverride()) ? Double.toString(mod.getOverride()) : "");
        fields.get("globalMult").setText(mod.getGlobalMult() != 0.0 ? Double.toString(mod.getGlobalMult()) : "");
        fields.get("equipmentMult").setText(mod.getEquipmentMult() != 0.0 ? Double.toString(mod.getEquipmentMult()) : "");
        fields.get("passiveMult").setText(mod.getPassiveMult() != 0.0 ? Double.toString(mod.getPassiveMult()) : "");
    }

    private String capitalize(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }


    private void updateModifier(BasicModifier basicModToPut, Card card,
                                StatusType statusType, String flat, String mult, String override, String globalMult,
                                String equipmentMult, String passiveMult, CardType type) {
        if (statusType == null) return;

        double valueFlat = parseOrDefault(flat, 0);
        double valueMult = parseOrDefault(mult, 0);
        double valueOverride = parseOrDefault(override, Double.NaN);
        double valueGlobalMult = parseOrDefault(globalMult, 0);
        double valueEquipMult = parseOrDefault(equipmentMult, 0);
        double valuePassiveMult = parseOrDefault(passiveMult, 0);

        basicModToPut.setFlat(valueFlat);
        basicModToPut.setGlobalMult(valueGlobalMult);
        basicModToPut.setEquipmentMult(valueEquipMult);
        basicModToPut.setPassiveMult(valuePassiveMult);
        basicModToPut.setOverride(valueOverride);
        basicModToPut.setMult(valueMult);

        card.getStatusModifiers(type).put(statusType, basicModToPut);
    }

    private void updateStatModifier(BasicModifier basicModToPut, Card card,
                                    StatType statType, String flat, String mult, String override,
                                    String globalMult, String equipmentMult, String passiveMult, CardType type) {
        if (statType == null) return;

        double valueFlat = parseOrDefault(flat, 0);
        double valueMult = parseOrDefault(mult, 0);
        double valueOverride = parseOrDefault(override, Double.NaN);
        double valueGlobalMult = parseOrDefault(globalMult, 0);
        double valueEquipMult = parseOrDefault(equipmentMult, 0);
        double valuePassiveMult = parseOrDefault(passiveMult, 0);

        basicModToPut.setFlat(valueFlat);
        basicModToPut.setGlobalMult(valueGlobalMult);
        basicModToPut.setEquipmentMult(valueEquipMult);
        basicModToPut.setPassiveMult(valuePassiveMult);
        basicModToPut.setOverride(valueOverride);
        basicModToPut.setMult(valueMult);

        card.getStatModifiers(type).put(statType, basicModToPut);
    }


    private double parseOrDefault(String value, double defaultValue) {
        try {
            return value == null || value.isEmpty() ? defaultValue : Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private void setupModifierFields(TextArea field, String prompt) {
        field.setPromptText(prompt);
        field.setMaxHeight(20);
        field.setMaxWidth(350);
    }


    public Node createStatField(VBox statBox) {
        return null;
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
//            createMode();
        }
    }

    public boolean isEditMode() {
        return isEditMode;
    }
}

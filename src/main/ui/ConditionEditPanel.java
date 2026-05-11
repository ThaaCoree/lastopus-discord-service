package main.ui;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import main.Database;
import model.entity.*;
import model.modifier.BasicModifier;
import model.modifier.SkillModifier;
import model.modifier.TransferModifier;
import model.type.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ConditionEditPanel extends ScrollPane {

    private final Database database;
    private final ToggleGroup modeToggle = new ToggleGroup();
    private final Button editModeBtn = new Button("Edit");
    private final Button createModeBtn = new Button("Create");
    private final List<Button> allBtn = new ArrayList<>();
    private boolean isEditMode = false;
    private final ConditionListPane listPane;
    private final VBox mainBox = new VBox();
    private final HBox mainButtonBox = new HBox();
    private boolean confirmDeletion = false;
    private Conditions toMake;

    public ConditionEditPanel(Database database, ConditionListPane listPane) {
        toMake = new Conditions();

        allBtn.add(editModeBtn);
        allBtn.add(createModeBtn);
        this.database = database;
        this.listPane = listPane;
        getStylesheets().add(getClass().getResource("/styles/theme.css").toExternalForm());
        setPrefWidth(400);
        getStyleClass().add("right-pane");

        mainButtonBox.getChildren().addAll(editModeBtn,createModeBtn);

        editModeBtn.setOnAction(e-> {
            editMode();
        });
        createModeBtn.setOnAction(e-> {
            createMode();
        });

        createMode();
        setButtonToSelected(createModeBtn, allBtn);
        setContent(mainBox);
    }

    public void editMode() {
        mainBox.getChildren().clear();

        setButtonToSelected(editModeBtn, allBtn);
        isEditMode = true;
        confirmDeletion = false;
        Button deleteButton = new Button("DELETE");

        VBox modeBox = new VBox();
        Conditions selectedCondition = listPane.getListView().getSelectionModel().getSelectedItem();
        deleteButton.setOnAction(e -> {
            if (!confirmDeletion) {
                System.out.println("Delete button clicked, click again to confirm deletion.");
                confirmDeletion = true;
            } else {
                if (selectedCondition != null)
                    database.getAllConditionMap().remove(selectedCondition.getName());
                listPane.getListView().refresh();
                listPane.getConditionList().setAll(database.getAllConditionMap().values());
                setButtonToSelected(createModeBtn, allBtn);
                editMode();
            }
        });

        if (selectedCondition != null) {
            modeBox.getChildren().addAll(
                    editNameArea(selectedCondition),
                    editTierTypeArea(selectedCondition),
                    editTypeArea(selectedCondition),
                    editItemAbility(selectedCondition),
                    statusAndStats(selectedCondition));
        }
        mainBox.getChildren().addAll(mainButtonBox, deleteButton,modeBox);
    }

    public void createMode() {
        mainBox.getChildren().clear();
        setButtonToSelected(createModeBtn, allBtn);
        isEditMode = false;
        confirmDeletion = false;

        toMake = new Conditions("New Condition");
        Button createButton = new Button("CREATE");
        VBox modeBox = new VBox();

        createButton.setOnAction(e -> {
            database.getAllConditionMap().put(toMake.getName(),toMake);
            database.translateEverything();
            listPane.getConditionList().setAll(database.getAllConditionMap().values());
            listPane.getListView().refresh();
            editMode();
        });
        modeBox.getChildren().addAll(
                editNameArea(toMake),
                editTierTypeArea(toMake),
                editTypeArea(toMake),
                editItemAbility(toMake),
                statusAndStats(toMake));


        mainBox.getChildren().addAll(mainButtonBox, createButton,modeBox);
    }

    public Node editNameArea(Conditions condition) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("Name");
        TextArea textArea = new TextArea(condition.getName());
        textArea.setWrapText(true);
        textArea.setMaxHeight(100);
        textArea.setMaxWidth(350);
        textArea.setOnKeyReleased(event -> {
            condition.setName(textArea.getText());
            listPane.getListView().refresh();
        });
        contentBox.getChildren().addAll(indicatorLabel,textArea);
        return contentBox;
    }

    public Node editTypeArea(Conditions condition) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("Condition Type");
        TextArea textArea = new TextArea(condition.getConditionType().writeAsString());
        textArea.setWrapText(true);
        textArea.setMaxHeight(100);
        textArea.setMaxWidth(350);
        textArea.setOnKeyReleased(event -> {
            for (ConditionType type : ConditionType.values()) {
                if (textArea.getText().toLowerCase().equals(type.writeAsString().toLowerCase())) {
                    condition.setConditionType(type);
                    break;
                } else {
                    condition.setConditionType(ConditionType.NEUTRAL);
                }
            }
            listPane.getListView().refresh();
        });
        contentBox.getChildren().addAll(indicatorLabel,textArea);
        return contentBox;
    }

    public Node editTierTypeArea(Conditions condition) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("Tier Type");
        TextArea textArea = new TextArea(condition.getConditionTierType().writeAsString());
        textArea.setWrapText(true);
        textArea.setMaxHeight(100);
        textArea.setMaxWidth(350);
        textArea.setOnKeyReleased(event -> {
            for (ConditionTierType type : ConditionTierType.values()) {
                if (textArea.getText().toLowerCase().equals(type.writeAsString().toLowerCase())) {
                    condition.setConditionTierType(type);
                    break;
                } else {
                    condition.setConditionTierType(ConditionTierType.BASIC);
                }
            }
            listPane.getListView().refresh();
        });
        contentBox.getChildren().addAll(indicatorLabel,textArea);
        return contentBox;
    }

    public Node editItemAbility(Conditions condition) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("Description");
        TextArea textArea = new TextArea(condition.getDescription());
        textArea.setWrapText(true);
        textArea.setMaxHeight(100);
        textArea.setMaxWidth(350);
        textArea.setOnKeyReleased(event -> {
            condition.setDescription(textArea.getText());
            listPane.getListView().refresh();
        });
        contentBox.getChildren().addAll(indicatorLabel,textArea);
        return contentBox;
    }

    public Node statusAndStats(Conditions condition) {
        VBox contentBox = new VBox();
        VBox statBox = new VBox();
        VBox statusBox = new VBox();
        VBox transferBox = new VBox();
        VBox skillModBox = new VBox();

        Button addStatus = new Button("Add Status");
        Button addStat = new Button("Add Stat");
        Button addTransfer = new Button("Add Transfer");
        Button addSkillMod = new Button("Add SkillMod");
        statusBox.getChildren().add(addStatus);
        statBox.getChildren().add(addStat);
        transferBox.getChildren().add(addTransfer);
        skillModBox.getChildren().add(addSkillMod);

        AtomicInteger transferCount = new AtomicInteger(0);

        addStatus.setOnAction(e -> {
            createStatusField(condition, statusBox);
        });
        addStat.setOnAction(e -> {
            createStatField(condition, statBox);
        });
        addTransfer.setOnAction(e -> {
            createTransferField(condition, transferBox, transferCount);
        });
        addSkillMod.setOnAction(e-> {
            createSkillModifierField(condition, skillModBox);
        });

        editStatusField(condition, statusBox);
        editStatField(condition, statBox);
        editTransferField(condition, transferBox, transferCount);
        editSkillModifierField(condition, skillModBox);

        contentBox.getChildren().addAll(statusBox, statBox, transferBox, skillModBox);
        return contentBox;
    }

    public void createSkillModifierField(Conditions condition, VBox skillModBox) {
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
                condition.getModifiers().getSkillModifierSafe(tag.getValue()).setFlat(Double.parseDouble(flat.getText()));
            } catch (NumberFormatException ex) {
                condition.getModifiers().getSkillModifierSafe(tag.getValue()).setFlat(0);
            }
            try {
                condition.getModifiers().getSkillModifierSafe(tag.getValue()).setMult(Double.parseDouble(flat.getText()));
            } catch (NumberFormatException ex) {
                condition.getModifiers().getSkillModifierSafe(tag.getValue()).setMult(0);
            }
        });
        flat.setOnKeyReleased(e-> {
            try {
                condition.getModifiers().getSkillModifierSafe(tag.getValue()).setFlat(Double.parseDouble(flat.getText()));
            } catch (NumberFormatException ex) {
                condition.getModifiers().getSkillModifierSafe(tag.getValue()).setFlat(0);
            }
            try {
                condition.getModifiers().getSkillModifierSafe(tag.getValue()).setMult(Double.parseDouble(flat.getText()));
            } catch (NumberFormatException ex) {
                condition.getModifiers().getSkillModifierSafe(tag.getValue()).setMult(0);
            }
        });
        mult.setOnKeyReleased(e-> {
            try {
                condition.getModifiers().getSkillModifierSafe(tag.getValue()).setFlat(Double.parseDouble(flat.getText()));
            } catch (NumberFormatException ex) {
                condition.getModifiers().getSkillModifierSafe(tag.getValue()).setFlat(0);
            }
            try {
                condition.getModifiers().getSkillModifierSafe(tag.getValue()).setMult(Double.parseDouble(flat.getText()));
            } catch (NumberFormatException ex) {
                condition.getModifiers().getSkillModifierSafe(tag.getValue()).setMult(0);
            }
        });
        skillModBox.getChildren().addAll(tag, flat, mult);
    }

    public void editSkillModifierField(Conditions condition, VBox skillModBox) {
        for (Map.Entry<SkillType, SkillModifier> entry : condition.getModifiers().getSkillModifiers().entrySet()) {
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
                    condition.getModifiers().getSkillModifiers().get(tag.getValue()).setFlat(Double.parseDouble(flat.getText()));
                } catch (NumberFormatException ex) {
                    condition.getModifiers().getSkillModifiers().get(tag.getValue()).setFlat(0);
                }
                try {
                    condition.getModifiers().getSkillModifiers().get(tag.getValue()).setMult(Double.parseDouble(mult.getText()));
                } catch (NumberFormatException ex) {
                    condition.getModifiers().getSkillModifiers().get(tag.getValue()).setMult(0);
                }
            });
            flat.setOnKeyReleased(e -> {
                try {
                    condition.getModifiers().getSkillModifiers().get(tag.getValue()).setFlat(Double.parseDouble(flat.getText()));
                } catch (NumberFormatException ex) {
                    condition.getModifiers().getSkillModifiers().get(tag.getValue()).setFlat(0);
                }
                try {
                    condition.getModifiers().getSkillModifiers().get(tag.getValue()).setMult(Double.parseDouble(mult.getText()));
                } catch (NumberFormatException ex) {
                    condition.getModifiers().getSkillModifiers().get(tag.getValue()).setMult(0);
                }
            });
            mult.setOnKeyReleased(e -> {
                try {
                    condition.getModifiers().getSkillModifiers().get(tag.getValue()).setFlat(Double.parseDouble(flat.getText()));
                } catch (NumberFormatException ex) {
                    condition.getModifiers().getSkillModifiers().get(tag.getValue()).setFlat(0);
                }
                try {
                    condition.getModifiers().getSkillModifiers().get(tag.getValue()).setMult(Double.parseDouble(mult.getText()));
                } catch (NumberFormatException ex) {
                    condition.getModifiers().getSkillModifiers().get(tag.getValue()).setMult(0);
                }
            });
            skillModBox.getChildren().addAll(tag, flat, mult);
        }
    }

    public void createTransferField(Conditions condition, VBox transferBox, AtomicInteger transferCount) {
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
                transferToPut, condition, transferType.getValue(),
                statusSourceCombo.getValue(),
                statusTargetCombo.getValue(),
                statSourceCombo.getValue(),
                statTargetCombo.getValue(),
                transferPercent.getText(),
                transferRatio.getText(),
                transferIndex
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

    public void editTransferField(Conditions condition, VBox transferBox, AtomicInteger transferCount) {
        for (TransferModifier tm : condition.getTransferModifiers().values()) {
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
                condition.getTransferModifiers().remove(transferIndex);
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
                    transferToPut, condition, transferType.getValue(),
                    statusSourceCombo.getValue(),
                    statusTargetCombo.getValue(),
                    statSourceCombo.getValue(),
                    statTargetCombo.getValue(),
                    transferPercent.getText(),
                    transferRatio.getText(),
                    transferIndex
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

    public void updateTransferModifier(TransferModifier transferToPut, Conditions condition,
                                       TransferType transferType, StatusType sourceStatus, StatusType targetStatus, StatType sourceStat, StatType targetStat,
                                       String transferPercent, String transferRatio, int transferIndex) {
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

        condition.getTransferModifiers().put(transferIndex, transferToPut);
    }

    public void createStatusField(Conditions condition, VBox statusBox) {
        BasicModifier basicModToPut = new BasicModifier();
        ComboBox<StatusType> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll(StatusType.values());

        Map<String, TextArea> fields = createModifierFields();

        Runnable update = () -> updateModifier(
                basicModToPut, condition, typeCombo.getValue(),
                fields.get("flat").getText(),
                fields.get("mult").getText(),
                fields.get("override").getText(),
                fields.get("globalMult").getText(),
                fields.get("equipmentMult").getText(),
                fields.get("passiveMult").getText()
        );

        typeCombo.setOnAction(e -> update.run());
        fields.values().forEach(f -> f.setOnKeyReleased(e -> update.run()));

        statusBox.getChildren().add(typeCombo);
        statusBox.getChildren().addAll(fields.values());
    }


    public void editStatusField(Conditions condition, VBox statusBox) {
        for (Map.Entry<StatusType, BasicModifier> entry : condition.getModifiers().getStatusModifiers().entrySet()) {
            BasicModifier basicModToPut = new BasicModifier();
            HBox row = new HBox();

            ComboBox<StatusType> typeCombo = new ComboBox<>();
            typeCombo.getItems().addAll(StatusType.values());
            typeCombo.setValue(entry.getKey());

            Button delType = new Button("Delete Status");
            delType.setOnAction(e -> {
                condition.getModifiers().getStatusModifiers().remove(entry.getKey());
                editMode();
            });

            row.getChildren().addAll(typeCombo, delType);

            Map<String, TextArea> fields = createModifierFields();
            fillModifierFields(fields, entry.getValue());

            Runnable update = () -> updateModifier(
                    basicModToPut, condition, typeCombo.getValue(),
                    fields.get("flat").getText(),
                    fields.get("mult").getText(),
                    fields.get("override").getText(),
                    fields.get("globalMult").getText(),
                    fields.get("equipmentMult").getText(),
                    fields.get("passiveMult").getText()
            );

            typeCombo.setOnAction(e -> update.run());
            fields.values().forEach(f -> f.setOnKeyReleased(e -> update.run()));

            statusBox.getChildren().addAll(row);
            statusBox.getChildren().addAll(fields.values());
        }
    }

    public void createStatField(Conditions condition, VBox statBox) {
        BasicModifier basicModToPut = new BasicModifier();
        ComboBox<StatType> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll(StatType.values());

        Map<String, TextArea> fields = createModifierFields();

        Runnable update = () -> updateStatModifier(
                basicModToPut, condition, typeCombo.getValue(),
                fields.get("flat").getText(),
                fields.get("mult").getText(),
                fields.get("override").getText(),
                fields.get("globalMult").getText(),
                fields.get("equipmentMult").getText(),
                fields.get("passiveMult").getText()
        );

        typeCombo.setOnAction(e -> update.run());
        fields.values().forEach(f -> f.setOnKeyReleased(e -> update.run()));

        statBox.getChildren().add(typeCombo);
        statBox.getChildren().addAll(fields.values());
    }


    public void editStatField(Conditions condition, VBox statBox) {
        for (Map.Entry<StatType, BasicModifier> entry : condition.getModifiers().getStatModifiers().entrySet()) {
            BasicModifier basicModToPut = new BasicModifier();
            HBox row = new HBox();

            ComboBox<StatType> typeCombo = new ComboBox<>();
            typeCombo.getItems().addAll(StatType.values());
            typeCombo.setValue(entry.getKey());

            Button delType = new Button("Delete Stat");
            delType.setOnAction(e -> {
                condition.getModifiers().getStatModifiers().remove(entry.getKey());
                editMode();
            });

            row.getChildren().addAll(typeCombo, delType);

            Map<String, TextArea> fields = createModifierFields();
            fillModifierFields(fields, entry.getValue());

            Runnable update = () -> updateStatModifier(
                    basicModToPut, condition, typeCombo.getValue(),
                    fields.get("flat").getText(),
                    fields.get("mult").getText(),
                    fields.get("override").getText(),
                    fields.get("globalMult").getText(),
                    fields.get("equipmentMult").getText(),
                    fields.get("passiveMult").getText()
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


    private void updateModifier(BasicModifier basicModToPut, Conditions condition,
                                StatusType statusType, String flat, String mult, String override, String globalMult,
                                String equipmentMult, String passiveMult) {
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

        condition.getModifiers().getStatusModifiers().put(statusType, basicModToPut);
    }

    private void updateStatModifier(BasicModifier basicModToPut, Conditions condition,
                                    StatType statType, String flat, String mult, String override,
                                    String globalMult, String equipmentMult, String passiveMult) {
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

        condition.getModifiers().getStatModifiers().put(statType, basicModToPut);
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
            createMode();
        }
    }

    public boolean isEditMode() {
        return isEditMode;
    }
}

package PassiveTree.PassiveTreeEditor;

import PassiveTree.PassiveNodeView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entity.PassiveNode;
import model.modifier.BasicModifier;
import model.modifier.SkillModifier;
import model.modifier.TransferModifier;
import model.type.*;

import java.util.*;

public class PassivePropertyEditorPanel extends VBox {
    private TextField nameField;
    private TextField descriptionField;
    private TextField loreField;
    private VBox vbox;
    private int statCount = 0;
    private int statusCount = 0;
    private int transferCount = 0;
    private int equipmentSlotModCount = 0;
    private int skillModCount = 0;
    private int statusPointCount = 0;
    private PassiveTreeEditorApp passiveTreeEditorApp;
    private Map<Integer, TextField> statFields = new LinkedHashMap<>();
    private Map<Integer, ComboBox<StatType>> statFieldTypes = new LinkedHashMap<>();
    private Map<Integer, ComboBox<String>> statFieldModifiers = new LinkedHashMap<>();
    private Map<Integer, ComboBox<StatusType>> statusFieldTypes = new LinkedHashMap<>();
    private Map<Integer, TextField> statusFields = new LinkedHashMap<>();
    private Map<Integer, ComboBox<String>> statusFieldModifiers = new LinkedHashMap<>();
    private Map<Integer, ComboBox<TransferType>> transferFieldTypes = new LinkedHashMap<>();
    private Map<Integer, ComboBox<StatType>> transferSourceStatField = new LinkedHashMap<>();
    private Map<Integer, ComboBox<StatType>> transferTargetStatField = new LinkedHashMap<>();
    private Map<Integer, ComboBox<StatusType>> transferSourceStatusField = new LinkedHashMap<>();
    private Map<Integer, ComboBox<StatusType>> transferTargetStatusField = new LinkedHashMap<>();
    private Map<Integer, TextField> transferPercentField = new LinkedHashMap<>();
    private Map<Integer, TextField> transferRatioField = new LinkedHashMap<>();
    private Map<Integer, TextField> equipmentSlotModField = new LinkedHashMap<>();
    private Map<Integer, ComboBox<EquipmentType>> equipmentSlotModType = new LinkedHashMap<>();
    private Map<Integer, ComboBox<String>> skillModModifiers = new LinkedHashMap<>();
    private Map<Integer, TextField> skillModField = new LinkedHashMap<>();
    private int statusPointField = 0;
    private Map<Integer, ComboBox<SkillType>> skillModType = new LinkedHashMap<>();
    private CheckBox smallNode = new CheckBox("Small");
    private CheckBox notableNode = new CheckBox("Notable");
    private CheckBox keystoneNode = new CheckBox("Keystone");

    public PassivePropertyEditorPanel(PassiveTreeEditorApp passiveTreeEditorApp) {
        this.passiveTreeEditorApp = passiveTreeEditorApp;
        this.setSpacing(10);
        this.setPadding(new Insets(15));
        this.setPrefWidth(250);

        this.getStyleClass().add("custom-panel");
        this.getStylesheets().add(getClass().getResource("/styles/theme.css").toExternalForm());

        vbox = new VBox(10);

        // ใส่ VBox เข้าไปใน ScrollPane
        ScrollPane scrollPane = new ScrollPane(vbox);

// ตั้งค่า ScrollPane นิดหน่อย
        scrollPane.setFitToWidth(true); // ให้ VBox กว้างเท่า ScrollPane
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // ไม่ให้มี scrollbar แนวนอน
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // แนวตั้งมีเมื่อจำเป็น

        // Label: "Edit Node Property"
        Label title = new Label("Edit Node Property");

        // TextField สำหรับ Label ของ Node
        Label nameLabel = new Label("Node name:");
        Label addLabel = new Label("Add Stat or Status: ");
        Label addNodeButtonLabel = new Label("Add Node");
        Label descriptionLabel = new Label("Add Description");
        Label loreLabel = new Label("Add Lore");
        nameLabel.setTextFill(Color.LIGHTGRAY);
        addLabel.setTextFill(Color.LIGHTGRAY);
        addNodeButtonLabel.setTextFill(Color.LIGHTGRAY);
        descriptionLabel.setTextFill(Color.LIGHTGRAY);
        loreLabel.setTextFill(Color.LIGHTGRAY);
        nameField = new TextField();
        descriptionField = new TextField();
        loreField = new TextField();

        Button addButton = new Button("Add Stat");
        Button addNodeButton = new Button( "Add Node");
        Button editNodeButton = new Button(  "Edit Node");

        smallNode.setOnAction(e -> {
            if (smallNode.isSelected()) {
                notableNode.setSelected(false);
                keystoneNode.setSelected(false);
            }
        });
        notableNode.setOnAction(e -> {
            if (notableNode.isSelected()) {
                smallNode.setSelected(false);
                keystoneNode.setSelected(false);
            }
        });
        keystoneNode.setOnAction(e -> {
            if (keystoneNode.isSelected()) {
                notableNode.setSelected(false);
                smallNode.setSelected(false);
            }
        });
        addNodeButton.setOnAction(e -> addNewNode());
        addButton.setOnAction(e -> showChoiceDialog());
        editNodeButton.setOnAction(e -> editNode());

        // เพิ่มทั้งหมดเข้า panel
        this.getChildren().addAll(
                title,
                nameLabel, nameField,
                descriptionLabel, descriptionField,
                loreLabel, loreField,
                addLabel, addButton, scrollPane,
                addNodeButtonLabel, addNodeButton, editNodeButton,
                smallNode, notableNode, keystoneNode
        );
    }

    private void showChoiceDialog() {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Add Stat or Status");

        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #2e2e2e;");

        // Header
        Label headerLabel = new Label("Add Stat or Status");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        headerLabel.setTextFill(Color.WHITE);

        // ComboBox (Dropdown)
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll("Stat", "Status", "Transfer", "EquipMod", "SkillMod", "StatusPoint");
        comboBox.setPromptText("Select Type");
        comboBox.setPrefWidth(150);

        // ปุ่ม OK
        Button okButton = new Button("OK");
        okButton.setDefaultButton(true);  // กด Enter ได้
        okButton.setOnAction(e -> {
            String selected = comboBox.getValue();
            if (selected != null) {
                System.out.println("Selected: " + selected);
                if (selected.equals("Stat")) {
                    addStatField();
                } else if (selected.equals("Status")) {
                    addStatusField();
                } else if (selected.equals("Transfer")) {
                    addTransferField();
                } else if (selected.equals("EquipMod")) {
                    addEquipmentModField();
                } else if (selected.equals("SkillMod")) {
                    addSkillModField();
                } else if (selected.equals("StatusPoint")) {
                    addStatusPointField();
                }
                dialogStage.close();
            }
        });

        // ปุ่ม Cancel
        Button cancelButton = new Button("Cancel");
        cancelButton.setCancelButton(true);  // กด Esc ได้
        cancelButton.setOnAction(e -> dialogStage.close());

        // ปุ่มทั้งสองเรียงในแนวนอน
        HBox buttonBox = new HBox(10, okButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        vbox.getChildren().addAll(headerLabel, comboBox, buttonBox);

        Scene scene = new Scene(vbox, 300, 220);
        scene.getStylesheets().add(getClass().getResource("/styles/theme.css").toExternalForm());
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }


    private void addNewNode() {
        // รับข้อมูลจาก PassivePropertyEditorPanel
        String nodeName = this.getNameText();  // รับ label จากฟอร์ม
        String nodeDescription = this.getDescriptionText();
        String nodeLore = this.getLore();
        double x = 2400;      // รับค่า X
        double y = 2400;      // รับค่า Y


        // เช็คว่า label ไม่เป็นค่าว่างและตำแหน่ง X, Y ถูกต้องหรือไม่
        if (nodeName.isEmpty()) {
            // แสดงข้อความเตือนหรือจัดการกรณีกรอกข้อมูลไม่ถูกต้อง
            System.out.println("Invalid node name!");
            return;
        }

        // สร้าง PassiveNode ใหม่ (หรือ PassiveNodeView ถ้าเป็นแบบนี้)
        PassiveNode newNode = new PassiveNode();  // สร้าง PassiveNode ใหม่
        newNode.setX(x);
        newNode.setY(y);
        int nodeIdToPut = 1;
        for (PassiveNodeView nodeView : passiveTreeEditorApp.getTreePane().getNodeViews()) {
            nodeIdToPut ++;
        }
        newNode.setId(nodeIdToPut);
        newNode.setName(nodeName);
        newNode.setDescription(nodeDescription);
        newNode.setLore(nodeLore);
        Map<StatType, BasicModifier> statToAdd = new LinkedHashMap<>();
        Map<StatusType, BasicModifier> statusToAdd = new LinkedHashMap<>();
        Map<Integer, TransferModifier> transferModify = new LinkedHashMap<>();

        statFields.forEach((key, textField) -> {
            double numberStatToAdd = Double.parseDouble(textField.getText());
            String modifierToAdd = statFieldModifiers.get(key).getValue();
            BasicModifier basicModifier = translateBasicModifier(modifierToAdd,numberStatToAdd);
            StatType typeStatToAdd = statFieldTypes.get(key).getValue();
            if (statToAdd.containsKey(typeStatToAdd)) {
                statToAdd.put(typeStatToAdd, translateBasicModifierMerge(modifierToAdd,numberStatToAdd,statToAdd.get(typeStatToAdd)));
            } else {
                statToAdd.put(typeStatToAdd, basicModifier);
            }
        });

        statusFields.forEach((key, textField) -> {
            double numberStatusToAdd = Double.parseDouble(textField.getText());
            String modifierToAdd = statusFieldModifiers.get(key).getValue();
            BasicModifier basicModifier = translateBasicModifier(modifierToAdd,numberStatusToAdd);
            StatusType typeStatusToAdd = statusFieldTypes.get(key).getValue();
            if (statusToAdd.containsKey(typeStatusToAdd)) {
                statusToAdd.put(typeStatusToAdd, translateBasicModifierMerge(modifierToAdd,numberStatusToAdd,statusToAdd.get(typeStatusToAdd)));
            } else {
                statusToAdd.put(typeStatusToAdd, basicModifier);
            }
        });

        transferFieldTypes.forEach((key, field) -> {
            TransferModifier transferModifier = new TransferModifier();
            transferModifier.setTransferType(field.getValue());
            transferModifier.setSourceStat(transferSourceStatField.get(key).getValue());
            transferModifier.setTargetStat(transferTargetStatField.get(key).getValue());
            transferModifier.setSourceStatus(transferSourceStatusField.get(key).getValue());
            transferModifier.setTargetStatus(transferTargetStatusField.get(key).getValue());
            double transferPercent;
            double transferRatio;
            try {
                transferPercent = Double.parseDouble(transferPercentField.get(key).getText());
            } catch (NumberFormatException e) {
                System.out.println("transferPercent is empty; changing to 1");
                transferPercent = 1.0;
            }
            try {
                transferRatio = Double.parseDouble(transferRatioField.get(key).getText());
            } catch (NumberFormatException e) {
                System.out.println("transferRatio is empty; changing to 1");
                transferRatio = 1.0;
            }
            transferModifier.setTransferPercent(transferPercent);
            transferModifier.setTransferRatio(transferRatio);
            transferModify.put(key,transferModifier);
        });
        newNode.setStatModifiers(statToAdd);
        newNode.setStatusModifiers(statusToAdd);
        Map<EquipmentType, Double> equipMod = new LinkedHashMap<>();
        equipmentSlotModField.forEach((key, value) -> {
            EquipmentType modType = equipmentSlotModType.get(key).getValue();
            double valueInDouble = Double.parseDouble(value.getText());
            equipMod.put(modType, valueInDouble);
        });
        newNode.setEquipmentSlotMult(equipMod);
        // skillModAdd
        Map<SkillType, SkillModifier> skillMod = new LinkedHashMap<>();
        skillModField.forEach((key, value) -> {
            SkillType modType = skillModType.get(key).getValue();
            double valueInDouble = Double.parseDouble(value.getText());
            String modifierToAdd = skillModModifiers.get(key).getValue();
            SkillModifier skillModToPut = translateSkillModifier(modifierToAdd, valueInDouble);
            if (skillMod.containsKey(modType)) {
                skillMod.put(modType, translateSkillModifierMerge(modifierToAdd,valueInDouble,skillMod.get(modType)));
            } else {
                skillMod.put(modType, skillModToPut);
            }
        });
        newNode.setSkillModifiers(skillMod);
        newNode.setStatusPoints(statusPointField);

        newNode.setTransferModifiers(transferModify);
        if (smallNode.isSelected()) {
            newNode.setNodeType(NodeType.SMALL);
        } else if (notableNode.isSelected()) {
            newNode.setNodeType(NodeType.NOTABLE);
        } else if (keystoneNode.isSelected()) {
            newNode.setNodeType(NodeType.KEYSTONE);
        }

        statFields = new LinkedHashMap<>();
        statusFields = new LinkedHashMap<>();
        statFieldTypes = new LinkedHashMap<>();
        statusFieldTypes = new LinkedHashMap<>();
        statFieldModifiers = new LinkedHashMap<>();
        statusFieldModifiers = new LinkedHashMap<>();
        transferFieldTypes = new LinkedHashMap<>();
        transferSourceStatField = new LinkedHashMap<>();
        transferTargetStatField = new LinkedHashMap<>();
        transferSourceStatusField = new LinkedHashMap<>();
        transferTargetStatusField = new LinkedHashMap<>();
        transferPercentField = new LinkedHashMap<>();
        transferRatioField = new LinkedHashMap<>();
        equipmentSlotModField = new LinkedHashMap<>();
        equipmentSlotModType = new LinkedHashMap<>();
        skillModField = new LinkedHashMap<>();
        skillModModifiers = new LinkedHashMap<>();
        skillModType = new LinkedHashMap<>();

        // สร้าง PassiveNodeView ใหม่
        PassiveNodeView newNodeView = new PassiveNodeView(newNode);

        // ตั้งค่า layout ของ Node ใหม่
        newNodeView.setLayoutX(x);
        newNodeView.setLayoutY(y);

        // เพิ่ม node ใหม่ลงใน nodeViews
        passiveTreeEditorApp.getTreePane().getNodeViews().add(newNodeView);
        // เพื่ม node ใหม่ลงใน passiveNodeList ของ TreePane
        int index = 1;
        for (Map.Entry<Integer, PassiveNode> entry : passiveTreeEditorApp.getTreePane().getPassiveNodeList().entrySet()) {
            if (!passiveTreeEditorApp.getTreePane().getPassiveNodeList().containsKey(index)) break;
            index++;
        }
        passiveTreeEditorApp.getTreePane().getPassiveNodeList().put(index,newNode);

        // เพิ่ม Node ใหม่ลงใน Pane เพื่อแสดงบน UI
        passiveTreeEditorApp.getTreePane().getContentGroup().getChildren().add(newNodeView);

        newNodeView.setOnMouseEntered(e -> passiveTreeEditorApp.getTreePane().updatePropertyPanel(newNode));
        newNodeView.setOnMouseExited(e -> passiveTreeEditorApp.getTreePane().updateEmptyPropertyPanel());

        passiveTreeEditorApp.makeDraggable(newNodeView);

        // รีเซ็ตค่าหรือแจ้งเตือนผู้ใช้ว่ามีการเพิ่ม Node ใหม่
        this.setNameText("");  // รีเซ็ตฟอร์ม

        System.out.println("New node added: " + nodeName);

        int loopToRemove = statCount*3+statusCount*3+transferCount*7+equipmentSlotModCount*2+skillModCount*3+statusPointCount;

        for (int i = 0; i < loopToRemove ; i++) {
            if (!vbox.getChildren().isEmpty()) {
                vbox.getChildren().remove(vbox.getChildren().size() - 1);
            }
        }
        statCount = 0;
        statusCount = 0;
        transferCount = 0;
        equipmentSlotModCount = 0;
        skillModCount = 0;
        statusPointCount = 0;
        statusPointField = 0;
    }

    private void editNode() {
        List<PassiveNodeView> selected = new ArrayList<>();
        for (PassiveNodeView view : passiveTreeEditorApp.getNodeViews()) {
            if (view.isSelected()) {
                PassiveNode node = view.getPassiveNode();
                node.setName(this.getNameText());
                node.setDescription(this.getDescriptionText());
                node.setLore(this.getLore());
                Map<StatType, BasicModifier> statToAdd = new LinkedHashMap<>();
                Map<StatusType, BasicModifier> statusToAdd = new LinkedHashMap<>();
                Map<Integer, TransferModifier> transferModify = new LinkedHashMap<>();
                Map<SkillType, SkillModifier> skillMod = new LinkedHashMap<>();
                Map<EquipmentType, Double> equipMod = new LinkedHashMap<>();
                equipmentSlotModField.forEach((key, value) -> {
                    EquipmentType modType = equipmentSlotModType.get(key).getValue();
                    double valueInDouble = Double.parseDouble(value.getText());
                    equipMod.put(modType, valueInDouble);
                });
                node.setEquipmentSlotMult(equipMod);

                statFields.forEach((key, textField) -> {
                    double numberStatToAdd = Double.parseDouble(textField.getText());
                    String modifierToAdd = statFieldModifiers.get(key).getValue();
                    BasicModifier basicModifier = translateBasicModifier(modifierToAdd,numberStatToAdd);
                    StatType typeStatToAdd = statFieldTypes.get(key).getValue();
                    if (statToAdd.containsKey(typeStatToAdd)) {
                        statToAdd.put(typeStatToAdd, translateBasicModifierMerge(modifierToAdd,numberStatToAdd,statToAdd.get(typeStatToAdd)));
                    } else {
                        statToAdd.put(typeStatToAdd, basicModifier);
                    }
                });

                statusFields.forEach((key, textField) -> {
                    double numberStatusToAdd = Double.parseDouble(textField.getText());
                    String modifierToAdd = statusFieldModifiers.get(key).getValue();
                    BasicModifier basicModifier = translateBasicModifier(modifierToAdd,numberStatusToAdd);
                    StatusType typeStatusToAdd = statusFieldTypes.get(key).getValue();
                    if (statusToAdd.containsKey(typeStatusToAdd)) {
                        statusToAdd.put(typeStatusToAdd, translateBasicModifierMerge(modifierToAdd,numberStatusToAdd,statusToAdd.get(typeStatusToAdd)));
                    } else {
                        statusToAdd.put(typeStatusToAdd, basicModifier);
                    }
                });

                transferFieldTypes.forEach((key, field) -> {
                    TransferModifier transferModifier = new TransferModifier();
                    transferModifier.setTransferType(field.getValue());
                    transferModifier.setSourceStat(transferSourceStatField.get(key).getValue());
                    transferModifier.setTargetStat(transferTargetStatField.get(key).getValue());
                    transferModifier.setSourceStatus(transferSourceStatusField.get(key).getValue());
                    transferModifier.setTargetStatus(transferTargetStatusField.get(key).getValue());
                    double transferPercent;
                    double transferRatio;
                    try {
                        transferPercent = Double.parseDouble(transferPercentField.get(key).getText());
                    } catch (NumberFormatException e) {
                        System.out.println("transferPercent is empty; changing to 1");
                        transferPercent = 1.0;
                    }
                    try {
                        transferRatio = Double.parseDouble(transferRatioField.get(key).getText());
                    } catch (NumberFormatException e) {
                        System.out.println("transferRatio is empty; changing to 1");
                        transferRatio = 1.0;
                    }
                    transferModifier.setTransferPercent(transferPercent);
                    transferModifier.setTransferRatio(transferRatio);
                    transferModify.put(key,transferModifier);
                });

                skillModField.forEach((key, textField) -> {
                    double numberStatusToAdd = Double.parseDouble(textField.getText());
                    String modifierToAdd = skillModModifiers.get(key).getValue();
                    SkillModifier skillModifier = translateSkillModifier(modifierToAdd,numberStatusToAdd);
                    SkillType skillType = skillModType.get(key).getValue();
                    if (skillMod.containsKey(skillType)) {
                        skillMod.put(skillType, translateSkillModifierMerge(modifierToAdd,numberStatusToAdd,skillMod.get(skillType)));
                    } else {
                        skillMod.put(skillType, skillModifier);
                    }
                });
                node.setStatModifiers(statToAdd);
                node.setStatusModifiers(statusToAdd);
                node.setTransferModifiers(transferModify);
                node.setSkillModifiers(skillMod);
                node.setStatusPoints(statusPointField);
            }
        }
        statFields = new LinkedHashMap<>();
        statusFields = new LinkedHashMap<>();
        statFieldTypes = new LinkedHashMap<>();
        statusFieldTypes = new LinkedHashMap<>();
        statFieldModifiers = new LinkedHashMap<>();
        statusFieldModifiers = new LinkedHashMap<>();
        transferFieldTypes = new LinkedHashMap<>();
        transferSourceStatField = new LinkedHashMap<>();
        transferTargetStatField = new LinkedHashMap<>();
        transferSourceStatusField = new LinkedHashMap<>();
        transferTargetStatusField = new LinkedHashMap<>();
        transferPercentField = new LinkedHashMap<>();
        transferRatioField = new LinkedHashMap<>();
        equipmentSlotModField = new LinkedHashMap<>();
        equipmentSlotModType = new LinkedHashMap<>();
        skillModField = new LinkedHashMap<>();
        skillModModifiers = new LinkedHashMap<>();
        skillModType = new LinkedHashMap<>();

        int loopToRemove = statCount*3+statusCount*3+transferCount*7+equipmentSlotModCount*2+skillModCount*3+statusPointCount;

        for (int i = 0; i < loopToRemove ; i++) {
            if (!vbox.getChildren().isEmpty()) {
                vbox.getChildren().remove(vbox.getChildren().size() - 1);
            }
        }
        statCount = 0;
        statusCount = 0;
        transferCount = 0;
        equipmentSlotModCount = 0;
        skillModCount = 0;
        statusPointField = 0;
    }

    private void addStatField() {
        statCount++;
        TextField newStatField = new TextField();
        newStatField.setPromptText("Stat "+statCount);
        ComboBox<StatType> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(StatType.values());
        comboBox.setPromptText("Select Stat");
        comboBox.setPrefWidth(100);
        ComboBox<String> modifyBox = new ComboBox<>();
        modifyBox.getItems().addAll("Flat", "Mult", "GlobalMult", "EquipmentMult", "PassiveMult", "Override");
        modifyBox.setPromptText("Modifier");
        modifyBox.setPrefWidth(100);

        vbox.getChildren().add(comboBox);
        vbox.getChildren().add(modifyBox);
        vbox.getChildren().add(newStatField);
        statFields.put(statCount, newStatField);
        statFieldTypes.put(statCount, comboBox);
        statFieldModifiers.put(statCount, modifyBox);
    }

    private void addStatusField() {
        statusCount++;
        TextField newStatusField = new TextField();
        newStatusField.setPromptText("Status "+statusCount);
        ComboBox<StatusType> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(StatusType.values());
        comboBox.setPromptText("Select Status");
        comboBox.setPrefWidth(100);
        ComboBox<String> modifyBox = new ComboBox<>();
        modifyBox.getItems().addAll("Flat", "Mult", "GlobalMult", "EquipmentMult", "PassiveMult", "Override");
        modifyBox.setPromptText("Modifier");
        modifyBox.setPrefWidth(100);

        vbox.getChildren().add(comboBox);
        vbox.getChildren().add(modifyBox);
        vbox.getChildren().add(newStatusField);
        statusFields.put(statusCount, newStatusField);
        statusFieldTypes.put(statusCount, comboBox);
        statusFieldModifiers.put(statusCount, modifyBox);
    }

    private void addTransferField() {
        transferCount++;
        ComboBox<TransferType> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(TransferType.values());
        comboBox.setPromptText("Select Transfer");
        comboBox.setPrefWidth(100);
        
        //sourceStat
        ComboBox<StatType> sourceStatComboBox = new ComboBox<>();
        sourceStatComboBox.getItems().addAll(StatType.values());
        sourceStatComboBox.setPromptText("Source Stat");
        sourceStatComboBox.setPrefWidth(100);
        
        //targetStat
        ComboBox<StatType> targetStatComboBox = new ComboBox<>();
        targetStatComboBox.getItems().addAll(StatType.values());
        targetStatComboBox.setPromptText("Target Stat");
        targetStatComboBox.setPrefWidth(100);
        
        //sourceStatus
        ComboBox<StatusType> sourceStatusComboBox = new ComboBox<>();
        sourceStatusComboBox.getItems().addAll(StatusType.values());
        sourceStatusComboBox.setPromptText("Source Status");
        sourceStatusComboBox.setPrefWidth(100);
        
        //targetStatus
        ComboBox<StatusType> targetStatusComboBox = new ComboBox<>();
        targetStatusComboBox.getItems().addAll(StatusType.values());
        targetStatusComboBox.setPromptText("Target Status");
        targetStatusComboBox.setPrefWidth(100);
        
        //Percent and Ratio
        TextField percentField = new TextField();
        percentField.setPromptText("Transfer Percent "+transferCount);
        TextField ratioField = new TextField();
        ratioField.setPromptText("Transfer Ratio "+transferCount);

        vbox.getChildren().add(comboBox);
        vbox.getChildren().add(sourceStatComboBox);
        vbox.getChildren().add(targetStatComboBox);
        vbox.getChildren().add(sourceStatusComboBox);
        vbox.getChildren().add(targetStatusComboBox);
        vbox.getChildren().add(percentField);
        vbox.getChildren().add(ratioField);
        transferFieldTypes.put(transferCount, comboBox);
        transferSourceStatField.put(transferCount, sourceStatComboBox);
        transferTargetStatField.put(transferCount, targetStatComboBox);
        transferSourceStatusField.put(transferCount, sourceStatusComboBox);
        transferTargetStatusField.put(transferCount, targetStatusComboBox);
        transferPercentField.put(transferCount, percentField);
        transferRatioField.put(transferCount, ratioField);
    }

    private void addEquipmentModField() {
        equipmentSlotModCount++;
        TextField newEquipmentModField = new TextField();
        newEquipmentModField.setPromptText("EquipmentMod "+equipmentSlotModCount);
        ComboBox<EquipmentType> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(EquipmentType.values());
        comboBox.setPromptText("Select EquipmentSlot");
        comboBox.setPrefWidth(100);

        vbox.getChildren().add(comboBox);
        vbox.getChildren().add(newEquipmentModField);
        equipmentSlotModField.put(equipmentSlotModCount, newEquipmentModField);
        equipmentSlotModType.put(equipmentSlotModCount, comboBox);
    }

    private void addSkillModField() {
        skillModCount++;
        TextField newSkillModField = new TextField();
        newSkillModField.setPromptText("SkillMod "+skillModCount);
        ComboBox<SkillType> comboBox = new ComboBox<>();
        ComboBox<String> modifyBox = new ComboBox<>();
        modifyBox.getItems().addAll("Flat", "Mult");
        modifyBox.setPromptText("Modifier");
        modifyBox.setPrefWidth(100);

        comboBox.getItems().addAll(SkillType.values());
        comboBox.setPromptText("Select SkillMod");
        comboBox.setPrefWidth(100);

        vbox.getChildren().add(comboBox);
        vbox.getChildren().add(modifyBox);
        vbox.getChildren().add(newSkillModField);
        skillModField.put(skillModCount, newSkillModField);
        skillModType.put(skillModCount, comboBox);
        skillModModifiers.put(skillModCount, modifyBox);
    }

    private void addStatusPointField() {
        statusPointCount++;
        TextField newSkillModField = new TextField();
        newSkillModField.setPromptText("Status Point "+statusPointCount);

        newSkillModField.setOnKeyReleased(e-> {
            statusPointField = Integer.parseInt(e.getText());
        });

        vbox.getChildren().add(newSkillModField);
    }

    private BasicModifier translateBasicModifier(String modifier, double number) {
        BasicModifier toReturn = new BasicModifier();
        switch (modifier) {
            case "Flat":
                toReturn.setFlat(number);
                break;
            case "Mult":
                toReturn.setMult(number);
                break;
            case "GlobalMult":
                toReturn.setGlobalMult(number);
                break;
            case "EquipmentMult":
                toReturn.setEquipmentMult(number);
                break;
            case "PassiveMult":
                toReturn.setPassiveMult(number);
                break;
            case "Override":
                toReturn.setOverride(number);
                break;
        }
            return toReturn;
    }

    private BasicModifier translateBasicModifierMerge(String modifier, double number, BasicModifier toReturn) {
        switch (modifier) {
            case "Flat":
                toReturn.setFlat(number);
                break;
            case "Mult":
                toReturn.setMult(number);
                break;
            case "GlobalMult":
                toReturn.setGlobalMult(number);
                break;
            case "EquipmentMult":
                toReturn.setEquipmentMult(number);
                break;
            case "PassiveMult":
                toReturn.setPassiveMult(number);
                break;
            case "Override":
                toReturn.setOverride(number);
                break;
        }
        return toReturn;
    }

    private SkillModifier translateSkillModifier(String modifier, double number) {
        SkillModifier toReturn = new SkillModifier();
        switch (modifier) {
            case "Flat":
                toReturn.setFlat(number);
                break;
            case "Mult":
                toReturn.setMult(number);
                break;
        }
        return toReturn;
    }

    private SkillModifier translateSkillModifierMerge(String modifier, double number, SkillModifier toReturn) {
        switch (modifier) {
            case "Flat":
                toReturn.setFlat(number);
                break;
            case "Mult":
                toReturn.setMult(number);
                break;
        }
        return toReturn;
    }

    private String getValueInStatField(int id) {
        TextField field = statFields.get(id);
        if (field != null) {
            return field.getText();
        }
        return null;
    }

    // Getter ใช้ดึงค่าจาก field
    public String getNameText() {
        return nameField.getText();
    }

    // Setter ใช้โหลดข้อมูลเดิมมาแก้ไข
    public void setNameText(String text) {
        nameField.setText(text);
    }

    public String getDescriptionText() {
        return descriptionField.getText();
    }

    public String getLore() {
        return loreField.getText();
    }

    public void setDescriptionText(String text) {
        descriptionField.setText(text);
    }

    public void setStatusDescriptionText(String text) {
        loreField.setText(text);
    }

}

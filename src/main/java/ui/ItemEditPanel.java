package ui;

import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import app.Database;
import factory.SkillFactory;
import model.entity.*;
import model.entity.items.*;
import model.entity.skills.SkillInstance;
import model.modifier.BasicModifier;
import model.modifier.SkillModifier;
import model.modifier.TransferModifier;
import model.type.*;
import util.SearchableListView;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemEditPanel extends ScrollPane {

    private Database database;
    private final ToggleGroup modeToggle = new ToggleGroup();
    private final Button editModeBtn = new Button("Edit");
    private final Button createModeBtn = new Button("Create");
    private final List<Button> allBtn = new ArrayList<>();
    private boolean isEditMode = false;
    private final ItemListPane listPane;
    private final VBox mainBox = new VBox();
    private final HBox mainButtonBox = new HBox();
    private boolean confirmDeletion = false;
    private Item toMake;

    public ItemEditPanel(Database database, ItemListPane listPane) {
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

        VBox modeBox = new VBox();
        Item selectedItem = listPane.getListView().getSelectionModel().getSelectedItem();
        if (selectedItem != null) {

            Map<String, SkillInstance> skill_map = null;
            if (toMake instanceof Equipment equipment) {
                skill_map = equipment.getSkills();
            }
            if (toMake instanceof Dream  dream) {
                skill_map = dream.getSkills();
            }
            if (toMake instanceof Rune rune) {
                skill_map = rune.getSkills();
            }
            modeBox.getChildren().addAll(
                    typeIndicatorButton(selectedItem),
                    editNameArea(selectedItem),
                    editTypeArea(selectedItem),
                    editLoreArea(selectedItem),
                    editItemAbility(selectedItem),
                    editPriceArea(selectedItem),
                    editWeightArea(selectedItem),
                    editEquipment(selectedItem),
                    editDream(selectedItem),
                    editRune(selectedItem),
                    editSkills(skill_map),
                    editConsumable(selectedItem));
        }
        mainBox.getChildren().addAll(mainButtonBox,modeBox);
    }

    public void createMode() {
        mainBox.getChildren().clear();
        setButtonToSelected(createModeBtn, allBtn);
        isEditMode = false;
        confirmDeletion = false;
        HBox buttonBox = new HBox();
        Button itemType = new Button("Item");
        Button equipType = new Button("Equipment");
        Button usableType = new Button("Usable");
        Button dreamType = new Button("Dream");
        Button runeType = new Button("Rune");
        Button createButton = new Button("CREATE");
        List<Button> allTypeButtons = new ArrayList<>();

        allTypeButtons.add(itemType);
        allTypeButtons.add(equipType);
        allTypeButtons.add(usableType);
        allTypeButtons.add(dreamType);
        allTypeButtons.add(runeType);

        buttonBox.getChildren().addAll(itemType,equipType,usableType, dreamType, runeType, createButton);

        VBox modeBox = new VBox();
        VBox formBox = new VBox();

        itemType.setOnAction(e -> {
           toMake = new Item("New Item");
           setButtonToSelected(itemType,allTypeButtons);
           makeCreateForm(formBox);
        });
        equipType.setOnAction(e -> {
            toMake = new Equipment("New Equipment");
            toMake.setItemType(ItemType.EQUIPMENT);
            setButtonToSelected(equipType,allTypeButtons);
            makeCreateForm(formBox);
        });
        usableType.setOnAction(e -> {
            toMake = new Consumable("New Usable");
            setButtonToSelected(usableType,allTypeButtons);
            makeCreateForm(formBox);
        });
        dreamType.setOnAction(e -> {
            toMake = new Dream("New Dream");
            toMake.setItemType(ItemType.DREAM);
            setButtonToSelected(dreamType,allTypeButtons);
            makeCreateForm(formBox);
        });
        runeType.setOnAction(e -> {
            toMake = new Rune("New Rune");
            toMake.setItemType(ItemType.RUNE);
            setButtonToSelected(runeType,allTypeButtons);
            makeCreateForm(formBox);
        });

        createButton.setOnAction(e -> {
            if (toMake instanceof Rune item) {
                database.getAllRuneMap().put(toMake.getName(), item);
            } else if (toMake instanceof Consumable item) {
                database.getAllConsumableMap().put(toMake.getName(), item);
            } else if (toMake instanceof Equipment item) {
                database.getAllEquipmentMap().put(toMake.getName(), item);
            } else if (toMake instanceof Dream item) {
                database.getAllDreamItem().put(toMake.getName(), item);
            } else {
                database.getAllTypeItemMap().put(toMake.getName(),toMake);
            }
            database.translateEverything();
            database.mapEverything();
            listPane.getItemList().setAll(database.getAllTypeItemMap().values());
            listPane.getListView().refresh();
            editMode();
        });

        modeBox.getChildren().addAll(buttonBox, formBox);

        mainBox.getChildren().addAll(mainButtonBox,modeBox);
    }

    public Node typeIndicatorButton(Item item) {
        HBox buttonBox = new HBox();
        Button itemType = new Button("Item");
        Button equipType = new Button("Equipment");
        Button usableType = new Button("Usable");
        Button dreamType = new Button("Dream");
        Button runeType = new Button("Rune");
        List<Button> allTypeButtons = new ArrayList<>();
        Button deleteButton = new Button("DELETE");

        deleteButton.setOnAction(e -> {
            if (!confirmDeletion) {
                System.out.println("Delete button clicked, click again to confirm deletion.");
                confirmDeletion = true;
            } else {
                database.getAllTypeItemMap().remove(item.getName());
                listPane.getListView().refresh();
                listPane.getItemList().setAll(database.getAllTypeItemMap().values());
                setButtonToSelected(createModeBtn, allBtn);
                editMode();
            }
        });

        allTypeButtons.add(itemType);
        allTypeButtons.add(equipType);
        allTypeButtons.add(usableType);
        allTypeButtons.add(dreamType);
        allTypeButtons.add(runeType);
        if (item instanceof Equipment) {
            setButtonToSelected(equipType, allTypeButtons);
        } else if (item instanceof Consumable) {
            setButtonToSelected(usableType, allTypeButtons);
        } else if (item instanceof Dream) {
            setButtonToSelected(dreamType, allTypeButtons);
        } else if (item instanceof Rune) {
            setButtonToSelected(runeType, allTypeButtons);
        } else {
            setButtonToSelected(itemType, allTypeButtons);
        }
        buttonBox.getChildren().addAll(itemType,equipType,usableType, dreamType, runeType,deleteButton);
        return buttonBox;
    }

    private void makeCreateForm(VBox formBox) {
        formBox.getChildren().clear();
        Map<String, SkillInstance> skill_map = null;
        if (toMake instanceof Equipment equipment) {
            skill_map = equipment.getSkills();
        }
        if (toMake instanceof Dream  dream) {
            skill_map = dream.getSkills();
        }
        if (toMake instanceof Rune rune) {
            skill_map = rune.getSkills();
            rune.setUnique_rune(true);
        }
        formBox.getChildren().addAll(
                editNameArea(toMake),
                editTypeArea(toMake),
                editLoreArea(toMake),
                editItemAbility(toMake),
                editPriceArea(toMake),
                editWeightArea(toMake),
                editEquipment(toMake),
                editDream(toMake),
                editRune(toMake),
                editSkills(skill_map),
                editConsumable(toMake));
    }

    public Node editNameArea(Item item) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("Name");
        TextArea textArea = new TextArea(item.getName());
        textArea.setWrapText(true);
        textArea.setMaxHeight(100);
        textArea.setMaxWidth(350);
        textArea.setOnKeyReleased(event -> {
            item.setName(textArea.getText());
            listPane.getListView().refresh();
        });
        contentBox.getChildren().addAll(indicatorLabel,textArea);
        return contentBox;
    }

    public Node editTypeArea(Item item) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("Item Type");
        TextArea textArea = new TextArea(item.getItemType().writeAsString());
        textArea.setWrapText(true);
        textArea.setMaxHeight(100);
        textArea.setMaxWidth(350);
        textArea.setOnKeyReleased(event -> {
            for (ItemType type : ItemType.values()) {
                if (textArea.getText().toLowerCase().equals(type.writeAsString().toLowerCase())) {
                    item.setItemType(type);
                    break;
                } else {
                    item.setItemType(ItemType.NONE);
                }
            }
            listPane.getListView().refresh();
        });
        contentBox.getChildren().addAll(indicatorLabel,textArea);
        return contentBox;
    }

    public Node editLoreArea(Item item) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("Lore");
        TextArea textArea = new TextArea(item.getLore());
        textArea.setWrapText(true);
        textArea.setMaxHeight(100);
        textArea.setMaxWidth(350);
        textArea.setOnKeyReleased(event -> {
            item.setLore(textArea.getText());
            listPane.getListView().refresh();
        });
        contentBox.getChildren().addAll(indicatorLabel,textArea);
        return contentBox;
    }

    public Node editItemAbility(Item item) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("Item Ability");
        TextArea textArea = new TextArea(item.getDescription());
        textArea.setWrapText(true);
        textArea.setMaxHeight(100);
        textArea.setMaxWidth(350);
        textArea.setOnKeyReleased(event -> {
            item.setDescription(textArea.getText());
            listPane.getListView().refresh();
        });
        contentBox.getChildren().addAll(indicatorLabel,textArea);
        return contentBox;
    }

    public Node editPriceArea(Item item) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("Price");
        TextArea textArea = new TextArea(item.getPrice());
        textArea.setWrapText(true);
        textArea.setMaxHeight(100);
        textArea.setMaxWidth(150);
        textArea.setOnKeyReleased(event -> {
            item.setPrice(textArea.getText());
            listPane.getListView().refresh();
        });
        contentBox.getChildren().addAll(indicatorLabel,textArea);
        return contentBox;
    }

    public Node editWeightArea(Item item) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("Weight");
        TextArea textArea = new TextArea(Integer.toString(item.getWeight()));
        textArea.setWrapText(true);
        textArea.setMaxHeight(30);
        textArea.setMaxWidth(150);
        textArea.setOnKeyReleased(event -> {
            if (Double.isNaN(Double.parseDouble(textArea.getText()))) return;
            item.setWeight(Integer.parseInt(textArea.getText()));
            listPane.getListView().refresh();
        });
        contentBox.getChildren().addAll(indicatorLabel,textArea);
        return contentBox;
    }

    public Node editEquipment(Item item) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("Equipment");
        if (item instanceof Equipment) {
            Equipment equipment = (Equipment) item;
            Label equipTypeLabel = new Label("Equipment Type");
            TextArea equipTypeArea = new TextArea(equipment.getEquipmentType().writeAsString());
            equipTypeArea.setWrapText(true);
            equipTypeArea.setMaxHeight(50);
            equipTypeArea.setMaxWidth(350);
            equipTypeArea.setOnKeyReleased(event -> {
                for (EquipmentType type : EquipmentType.values()) {
                    if (equipTypeArea.getText().toLowerCase().equals(type.writeAsString().toLowerCase())) {
                        equipment.setEquipmentType(type);
                        break;
                    } else {
                        equipment.setEquipmentType(EquipmentType.HELMET);
                    }
                }
                listPane.getListView().refresh();
            });
            Label weaponTypeLabel = new Label("Weapon Type");
            TextArea weaponTypeArea = new TextArea(equipment.getWeaponType().writeAsString());
            weaponTypeArea.setWrapText(true);
            weaponTypeArea.setMaxHeight(50);
            weaponTypeArea.setMaxWidth(350);
            weaponTypeArea.setOnKeyReleased(event -> {
                for (WeaponType type : WeaponType.values()) {
                    if (weaponTypeArea.getText().toLowerCase().equals(type.writeAsString().toLowerCase())) {
                        equipment.setWeaponType(type);
                        break;
                    } else {
                        equipment.setWeaponType(WeaponType.NOT_A_WEAPON);
                    }
                }
                listPane.getListView().refresh();
            });

            Label backpack_slot_label = new Label("Backpack Slot");
            TextArea backpack_slot_area = new TextArea(Integer.toString(equipment.getBackpackSlot()));
            backpack_slot_area.setWrapText(true);
            backpack_slot_area.setMaxHeight(50);
            backpack_slot_area.setMaxWidth(350);
            backpack_slot_area.setOnKeyReleased(event -> {
                if (Double.isNaN(Double.parseDouble(backpack_slot_area.getText()))) return;
                equipment.setBackpackSlot(Integer.parseInt(backpack_slot_area.getText()));
                listPane.getListView().refresh();
            });

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
                createStatusField(equipment.getStatusModifiers(), statusBox);
            });
            addStat.setOnAction(e -> {
                createStatField(equipment.getStatModifiers(), statBox);
            });
            addTransfer.setOnAction(e -> {
                createTransferField(equipment.getTransferModifiers(), transferBox, transferCount);
            });
            addSkillMod.setOnAction(e-> {
                createSkillModifierField(equipment.getModifiers(), skillModBox);
            });

            editStatusField(equipment.getStatusModifiers(), statusBox);
            editStatField(equipment.getStatModifiers(), statBox);
            editTransferField(equipment.getTransferModifiers(), transferBox, transferCount);
            editSkillModifierField(equipment.getModifiers(), skillModBox);


            contentBox.getChildren().addAll(indicatorLabel,equipTypeLabel,equipTypeArea,weaponTypeLabel,weaponTypeArea,backpack_slot_label, backpack_slot_area, statusBox, statBox, transferBox, skillModBox);
            return contentBox;
        } else {
            return new VBox();
        }
    }

    public Node editSkills(Map<String, SkillInstance> map) {
        VBox contentBox = new VBox();

        if (map == null) return new VBox();

        Label indicatorLabel = new Label("Skill");
        contentBox.getChildren().add(indicatorLabel);

        for (Map.Entry<String, SkillInstance> entry : map.entrySet()) {
            Label name = new Label(entry.getKey());
            Button remove = new Button("Remove");
            remove.setOnAction(e -> {
                map.remove(entry.getKey());
                editMode();
            });
            contentBox.getChildren().add(name);
            contentBox.getChildren().add(remove);
        }

        TextField skillNameField = new TextField();
        skillNameField.setMaxHeight(100);
        skillNameField.setMaxWidth(350);
        Button createButton = new Button("Add");

        Popup choosePopup = new Popup();
        ListView<String> itemListView = new ListView<>();
        List<String> itemNameList = SkillFactory.skillNames;

        createButton.setOnAction(e -> {
            String name = skillNameField.getText();
            if (SkillFactory.skillNames.contains(name)) {
                map.put(name, new SkillInstance(SkillFactory.getSkill(name)));
                System.out.println("Skill added");
                skillNameField.setText("");
            } else {
                System.out.println("Skill not found");
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

        contentBox.getChildren().addAll(skillNameField, createButton);

        return contentBox;
    }

    public Node editDream(Item item) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("Dream ");
        if (item instanceof Dream) {
            Dream dream = (Dream) item;
            Label equipTypeLabel = new Label("Node Type");
            TextArea nodeTypeArea = new TextArea(dream.getNodeType().writeAsString());
            nodeTypeArea.setWrapText(true);
            nodeTypeArea.setMaxHeight(50);
            nodeTypeArea.setMaxWidth(350);
            nodeTypeArea.setOnKeyReleased(event -> {
                for (NodeType type : NodeType.values()) {
                    if (nodeTypeArea.getText().toLowerCase().equals(type.writeAsString().toLowerCase())) {
                        dream.setNodeType(type);
                        break;
                    } else {
                        dream.setNodeType(NodeType.SMALL);
                    }
                }
                listPane.getListView().refresh();
            });

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
                createStatusField(dream.getStatusModifiers(), statusBox);
            });
            addStat.setOnAction(e -> {
                createStatField(dream.getStatModifiers(), statBox);
            });
            addTransfer.setOnAction(e -> {
                createTransferField(dream.getTransferModifiers(), transferBox, transferCount);
            });
            addSkillMod.setOnAction(e-> {
                createSkillModifierField(dream.getModifiers(), skillModBox);
            });

            editStatusField(dream.getStatusModifiers(), statusBox);
            editStatField(dream.getStatModifiers(), statBox);
            editTransferField(dream.getTransferModifiers(), transferBox, transferCount);
            editSkillModifierField(dream.getModifiers(), skillModBox);


            contentBox.getChildren().addAll(indicatorLabel,equipTypeLabel,nodeTypeArea, statusBox, statBox, transferBox, skillModBox);
            return contentBox;
        } else {
            return new VBox();
        }
    }

    public Node editRune(Item item) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("Rune ");
        if (item instanceof Rune rune) {
            CheckBox runeType = new CheckBox("Unique Rune");
            TextField runeShape = new TextField("Rune Shape");
            TextField runeWeight = new TextField("Rune Weight");
            runeShape.setText(rune.getShapeName());
            runeType.setSelected(rune.isUnique_rune());
            runeType.setWrapText(true);
            runeType.setMaxHeight(50);
            runeType.setMaxWidth(350);
            runeWeight.setText(Integer.toString(rune.getUnique_weight()));
            runeType.setOnAction(event -> {
                rune.setUnique_rune(runeType.isSelected());
                listPane.getListView().refresh();
            });
            runeShape.setOnKeyReleased(e-> {
                rune.promptShape(runeShape.getText());
            });
            runeWeight.setOnKeyReleased(e-> {
                rune.setUnique_weight(Integer.parseInt(runeWeight.getText()));
            });

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
                createStatusField(rune.getStatusModifiers(), statusBox);
            });
            addStat.setOnAction(e -> {
                createStatField(rune.getStatModifiers(), statBox);
            });
            addTransfer.setOnAction(e -> {
                createTransferField(rune.getTransferModifiers(), transferBox, transferCount);
            });
            addSkillMod.setOnAction(e-> {
                createSkillModifierField(rune.getModifiers(), skillModBox);
            });

            editStatusField(rune.getStatusModifiers(), statusBox);
            editStatField(rune.getStatModifiers(), statBox);
            editTransferField(rune.getTransferModifiers(), transferBox, transferCount);
            editSkillModifierField(rune.getModifiers(), skillModBox);


            contentBox.getChildren().addAll(indicatorLabel, runeShape,runeType, runeWeight, statusBox, statBox, transferBox, skillModBox);
            return contentBox;
        } else {
            return new VBox();
        }
    }

    public void createSkillModifierField(ModifierBundle bundle, VBox skillModBox) {
        ComboBox<SkillType> tag = new ComboBox<>();
        Button delTag = new Button("Delete");
        for (SkillType type : SkillType.values()) {
            tag.getItems().add(type);
        }
        TextField flat = new TextField();
        TextField mult = new TextField();
        flat.setPromptText("Flat");
        mult.setPromptText("Mult");
        tag.setOnAction(e-> {
            try {
                bundle.getSkillModifierSafe(tag.getValue()).setFlat(Double.parseDouble(flat.getText()));
            } catch (NumberFormatException ex) {
                bundle.getSkillModifierSafe(tag.getValue()).setFlat(0);
            }
            try {
                bundle.getSkillModifierSafe(tag.getValue()).setMult(Double.parseDouble(mult.getText()));
            } catch (NumberFormatException ex) {
                bundle.getSkillModifierSafe(tag.getValue()).setMult(0);
            }
        });
        flat.setOnKeyReleased(e-> {
            try {
                bundle.getSkillModifierSafe(tag.getValue()).setFlat(Double.parseDouble(flat.getText()));
            } catch (NumberFormatException ex) {
                bundle.getSkillModifierSafe(tag.getValue()).setFlat(0);
            }
            try {
                bundle.getSkillModifierSafe(tag.getValue()).setMult(Double.parseDouble(mult.getText()));
            } catch (NumberFormatException ex) {
                bundle.getSkillModifierSafe(tag.getValue()).setMult(0);
            }
        });
        mult.setOnKeyReleased(e-> {
            try {
                bundle.getSkillModifierSafe(tag.getValue()).setFlat(Double.parseDouble(flat.getText()));
            } catch (NumberFormatException ex) {
                bundle.getSkillModifierSafe(tag.getValue()).setFlat(0);
            }
            try {
                bundle.getSkillModifierSafe(tag.getValue()).setMult(Double.parseDouble(mult.getText()));
            } catch (NumberFormatException ex) {
                bundle.getSkillModifierSafe(tag.getValue()).setMult(0);
            }
        });
        delTag.setOnAction(e-> {
            bundle.getSkillModifiers().remove(tag.getValue());
            editMode();
        });

        HBox tagBox = new HBox(tag, delTag);
        skillModBox.getChildren().addAll(tagBox, flat, mult);
    }

    public void editSkillModifierField(ModifierBundle bundle, VBox skillModBox) {
        for (Map.Entry<SkillType, SkillModifier> entry : bundle.getSkillModifiers().entrySet()) {
            ComboBox<SkillType> tag = new ComboBox<>();
            Button delTag = new Button("Delete");
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
                    bundle.getSkillModifierSafe(tag.getValue()).setFlat(Double.parseDouble(flat.getText()));
                } catch (NumberFormatException ex) {
                    bundle.getSkillModifierSafe(tag.getValue()).setFlat(0);
                }
                try {
                    bundle.getSkillModifierSafe(tag.getValue()).setMult(Double.parseDouble(mult.getText()));
                } catch (NumberFormatException ex) {
                    bundle.getSkillModifierSafe(tag.getValue()).setMult(0);
                }
            });
            flat.setOnKeyReleased(e -> {
                try {
                    bundle.getSkillModifierSafe(tag.getValue()).setFlat(Double.parseDouble(flat.getText()));
                } catch (NumberFormatException ex) {
                    bundle.getSkillModifierSafe(tag.getValue()).setFlat(0);
                }
                try {
                    bundle.getSkillModifierSafe(tag.getValue()).setMult(Double.parseDouble(mult.getText()));
                } catch (NumberFormatException ex) {
                    bundle.getSkillModifierSafe(tag.getValue()).setMult(0);
                }
            });
            mult.setOnKeyReleased(e -> {
                try {
                    bundle.getSkillModifierSafe(tag.getValue()).setFlat(Double.parseDouble(flat.getText()));
                } catch (NumberFormatException ex) {
                    bundle.getSkillModifierSafe(tag.getValue()).setFlat(0);
                }
                try {
                    bundle.getSkillModifierSafe(tag.getValue()).setMult(Double.parseDouble(mult.getText()));
                } catch (NumberFormatException ex) {
                    bundle.getSkillModifierSafe(tag.getValue()).setMult(0);
                }
            });
            delTag.setOnAction(e-> {
                bundle.getSkillModifiers().remove(tag.getValue());
                editMode();
            });

            HBox tagBox = new HBox(tag, delTag);
            skillModBox.getChildren().addAll(tagBox, flat, mult);
        }
    }

    public void createTransferField(Map<Integer, TransferModifier> map, VBox transferBox, AtomicInteger transferCount) {
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
                transferToPut, map, transferType.getValue(),
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

    public void editTransferField(Map<Integer, TransferModifier> map, VBox transferBox, AtomicInteger transferCount) {
        for (TransferModifier tm : map.values()) {
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
                map.remove(transferIndex);
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
                    transferToPut, map, transferType.getValue(),
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

    public void updateTransferModifier(TransferModifier transferToPut, Map<Integer, TransferModifier> map,
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

        map.put(transferIndex, transferToPut);
    }

    public Node editConsumable(Item item) {
        VBox contentBox = new VBox();
        if (item instanceof Consumable) {
            Consumable consumable = (Consumable) item;
            Label indicatorLabel = new Label("Consumable");
            VBox restoreBox = new VBox();
            Button addCondition = new Button("Add Condition");

            for (ResourceType type : ResourceType.values()) {
                Label typeIndicator = new Label(type.writeAsString());
                restoreBox.getChildren().add(typeIndicator);
                for (int i = 0; i < 3; i++) {
                    String restoretype = "";
                    switch (i) {
                        case 0 : restoretype = "Flat";
                            break;
                        case 1 : restoretype = "Percent";
                            break;
                        case 2 : restoretype = "MissingPercent";
                            break;
                        default:
                            break;
                    }
                    double restoreAmount = 0;
                    String finalRestoreType = restoretype;
                    TextArea restoreArea = new TextArea();
                    restoreArea.setPromptText(type + restoretype);
                    restoreArea.setMaxHeight(40);
                    restoreArea.setMaxWidth(350);
                    String amountBeforeEdit;
                    switch (finalRestoreType) {
                        case "Flat" : amountBeforeEdit = consumable.getRestoredFlat().get(type) == 0.0 ? "" : Double.toString(consumable.getRestoredFlat().get(type));;
                            break;
                        case "Percent" : amountBeforeEdit = consumable.getRestoredPercent().get(type) == 0.0 ? "" : Double.toString(consumable.getRestoredPercent().get(type));;
                            break;
                        case "MissingPercent" : amountBeforeEdit = consumable.getRestoredMissingPercent().get(type) == 0.0 ? "" : Double.toString(consumable.getRestoredMissingPercent().get(type));;
                            break;
                        default: amountBeforeEdit = "";
                        break;
                    }
                    restoreArea.setText(amountBeforeEdit);
                    restoreArea.setOnKeyReleased(e -> {
                        double value;
                        try {
                            value = Double.parseDouble(restoreArea.getText());
                        } catch (NumberFormatException ex) {
                            value = 0;
                        }
                        switch (finalRestoreType) {
                            case "Flat" : consumable.getRestoredFlat().put(type, value);
                                break;
                            case "Percent" : consumable.getRestoredPercent().put(type, value);
                                break;
                            case "MissingPercent" : consumable.getRestoredMissingPercent().put(type, value);
                                break;
                        }
                    });

                    restoreBox.getChildren().add(restoreArea);
                }
            }

            addCondition.setOnAction(e -> {
                createConditionField(consumable, contentBox);
            });

            editConditionField(consumable, contentBox);

            contentBox.getChildren().addAll(indicatorLabel, restoreBox, addCondition);
            return contentBox;
        } else {
            return new VBox();
        }
    }

    public void createConditionField(Consumable consumable, VBox contentBox) {
        ComboBox<String> condition = new ComboBox<>();
        for (Conditions con : database.getAllConditionMap().values()) {
            condition.getItems().add(con.getName());
        }
        TextArea duration = new TextArea();
        duration.setPromptText("Duration");

        condition.setOnAction(e -> {
            double dura;
            try {
                dura = Double.parseDouble(duration.getText());
            } catch (NumberFormatException ex) {
                dura = 0;
            }

            for (Conditions con : database.getAllConditionMap().values()) {
                if (condition.getValue().equals(con.getName())) {
                    consumable.getConditionsGiven().removeIf(tc -> tc.getCondition().getName().equals(condition.getValue()));
                    consumable.getConditionsGiven().add(new TimedCondition(con, dura));
                }
            }
        });

        duration.setOnKeyReleased(e -> {
            double dura;
            try {
                dura = Double.parseDouble(duration.getText());
            } catch (NumberFormatException ex) {
                dura = 0;
            }

            for (Conditions con : database.getAllConditionMap().values()) {
                if (condition.getValue().equals(con.getName())) {
                    consumable.getConditionsGiven().removeIf(tc -> tc.getCondition().getName().equals(condition.getValue()));
                    consumable.getConditionsGiven().add(new TimedCondition(con, dura));
                }
            }
        });

        contentBox.getChildren().addAll(condition, duration);
    }

    public void editConditionField(Consumable consumable, VBox contentBox) {
        for (TimedCondition timedCondition : consumable.getConditionsGiven()) {
            ComboBox<String> condition = new ComboBox<>();
            HBox row = new HBox();
            for (Conditions con : database.getAllConditionMap().values()) {
                condition.getItems().add(con.getName());
            }
            TextArea duration = new TextArea();
            duration.setPromptText("Duration");
            condition.setValue(timedCondition.getCondition().getName());

            duration.setText(Double.toString(timedCondition.getDuration()));

            Button delButton = new Button("Delete Condition");
            delButton.setOnAction(e -> {
                for (Conditions con : database.getAllConditionMap().values()) {
                    if (condition.getValue().equals(con.getName())) {
                        consumable.getConditionsGiven().removeIf(tc -> tc.getCondition().getName().equals(condition.getValue()));
                        editMode();
                    }
                }
            });

            condition.setOnAction(e -> {
                double dura;
                try {
                    dura = Double.parseDouble(duration.getText());
                } catch (NumberFormatException ex) {
                    dura = 0;
                }

                for (Conditions con : database.getAllConditionMap().values()) {
                    if (condition.getValue().equals(con.getName())) {
                        consumable.getConditionsGiven().removeIf(tc -> tc.getCondition().getName().equals(condition.getValue()));
                        consumable.getConditionsGiven().add(new TimedCondition(con, dura));
                    }
                }
            });

            duration.setOnKeyReleased(e -> {
                double dura;
                try {
                    dura = Double.parseDouble(duration.getText());
                } catch (NumberFormatException ex) {
                    dura = 0;
                }

                for (Conditions con : database.getAllConditionMap().values()) {
                    if (condition.getValue().equals(con.getName())) {
                        consumable.getConditionsGiven().removeIf(tc -> tc.getCondition().getName().equals(condition.getValue()));
                        consumable.getConditionsGiven().add(new TimedCondition(con, dura));
                    }
                }
            });

            row.getChildren().addAll(condition, delButton);
            contentBox.getChildren().addAll(row, duration);
        }
    }

    public void createStatusField(Map<StatusType, BasicModifier> map, VBox statusBox) {
        BasicModifier basicModToPut = new BasicModifier();
        ComboBox<StatusType> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll(StatusType.values());

        Map<String, TextArea> fields = createModifierFields();

        Runnable update = () -> updateModifier(
                basicModToPut, map, typeCombo.getValue(),
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

    public void editStatusField(Map<StatusType, BasicModifier> map, VBox statusBox) {
        for (Map.Entry<StatusType, BasicModifier> entry : map.entrySet()) {
            BasicModifier basicModToPut = new BasicModifier();
            HBox row = new HBox();

            ComboBox<StatusType> typeCombo = new ComboBox<>();
            typeCombo.getItems().addAll(StatusType.values());
            typeCombo.setValue(entry.getKey());

            Button delType = new Button("Delete Status");
            delType.setOnAction(e -> {
                map.remove(entry.getKey());
                editMode();
            });

            row.getChildren().addAll(typeCombo, delType);

            Map<String, TextArea> fields = createModifierFields();
            fillModifierFields(fields, entry.getValue());

            Runnable update = () -> updateModifier(
                    basicModToPut, map, typeCombo.getValue(),
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

    public void createStatField(Map<StatType, BasicModifier> map, VBox statBox) {
        BasicModifier basicModToPut = new BasicModifier();
        ComboBox<StatType> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll(StatType.values());

        Map<String, TextArea> fields = createModifierFields();

        Runnable update = () -> updateStatModifier(
                basicModToPut, map, typeCombo.getValue(),
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

    public void editStatField(Map<StatType, BasicModifier> map, VBox statBox) {
        for (Map.Entry<StatType, BasicModifier> entry : map.entrySet()) {
            BasicModifier basicModToPut = new BasicModifier();
            HBox row = new HBox();

            ComboBox<StatType> typeCombo = new ComboBox<>();
            typeCombo.getItems().addAll(StatType.values());
            typeCombo.setValue(entry.getKey());

            Button delType = new Button("Delete Stat");
            delType.setOnAction(e -> {
                map.remove(entry.getKey());
                editMode();
            });

            row.getChildren().addAll(typeCombo, delType);

            Map<String, TextArea> fields = createModifierFields();
            fillModifierFields(fields, entry.getValue());

            Runnable update = () -> updateStatModifier(
                    basicModToPut, map, typeCombo.getValue(),
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


    private void updateModifier(BasicModifier basicModToPut,  Map<StatusType, BasicModifier> map,
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

            map.put(statusType, basicModToPut);
    }

    private void updateStatModifier(BasicModifier basicModToPut,  Map<StatType, BasicModifier> map,
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

        map.put(statType, basicModToPut);
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

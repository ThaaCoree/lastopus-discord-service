package ui;

import app.Database;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.entity.Shop;
import model.entity.items.crafted_equipments.CraftModPool;
import model.entity.items.crafted_equipments.CraftedMod;
import model.type.CityName;
import model.type.ModifierType;
import model.type.StatType;
import model.type.StatusType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ModPoolEditPanel extends ScrollPane {

    private final Database database;
    private final ToggleGroup modeToggle = new ToggleGroup();
    private final Button editModeBtn = new Button("Edit");
    private final Button createModeBtn = new Button("Create");
    private final HBox changePageBtn = new HBox();
    private final List<Button> allListPaneBtn = new ArrayList<>();
    private boolean isEditMode = false;
    private final ModPoolPane listPane;
    private final VBox mainBox = new VBox();
    private final HBox listPaneButtonBox = new HBox();
    private boolean confirmDeletion = false;
    private CraftModPool toMake;
    private TreePane treePane;

    public ModPoolEditPanel(Database database, ModPoolPane listPane) {
        toMake = new CraftModPool("New Pool");

        allListPaneBtn.add(editModeBtn);
        allListPaneBtn.add(createModeBtn);
        this.database = database;
        this.listPane = listPane;
        getStylesheets().add(getClass().getResource("/styles/theme.css").toExternalForm());
        setPrefWidth(800);
        getStyleClass().add("right-pane");

        listPaneButtonBox.getChildren().addAll(editModeBtn,createModeBtn);

        editModeBtn.setOnAction(e-> {
            editMode();
        });
        createModeBtn.setOnAction(e-> {
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
        CraftModPool selectedPool = listPane.getListView().getSelectionModel().getSelectedItem();
        deleteButton.setOnAction(e -> {
            if (!confirmDeletion) {
                System.out.println("Delete button clicked, click again to confirm deletion.");
                confirmDeletion = true;
            } else {
                if (selectedPool != null) {
                    database.getAllModPools().remove(selectedPool.getPool_name());
                }
                listPane.getListView().refresh();
                listPane.getPoolList().setAll(database.getAllModPools().values());
                setButtonToSelected(createModeBtn, allListPaneBtn);
                editMode();
            }
        });

        if (selectedPool != null) {
            modeBox.getChildren().addAll(
                    editPoolName(selectedPool),
                    allowDuplicate(selectedPool),
                    addModsToPool(selectedPool));
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

        toMake = new CraftModPool("New Mod Pool");
        Button createButton = new Button("CREATE");
        VBox modeBox = new VBox();

        createButton.setOnAction(e -> {
            database.getAllModPools().put(toMake.getPool_name(),toMake);
            database.translateEverything();
            listPane.getPoolList().setAll(database.getAllModPools().values());
            listPane.getListView().refresh();
            editMode();
        });
        modeBox.getChildren().addAll(
                editPoolName(toMake),
                allowDuplicate(toMake),
                addModsToPool(toMake));
        mainBox.getChildren().addAll(changePageBtn,listPaneButtonBox, createButton,modeBox);
    }

    public Node editPoolName(CraftModPool shop) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("Pool name");
        TextArea textArea = new TextArea(shop.getPool_name());
        textArea.setWrapText(true);
        textArea.setMaxHeight(100);
        textArea.setMaxWidth(350);
        textArea.setOnKeyReleased(event -> {
            shop.setPool_name(textArea.getText());
            listPane.getListView().refresh();
        });
        contentBox.getChildren().addAll(indicatorLabel,textArea);
        return contentBox;
    }

    public Node addModsToPool(CraftModPool pool) {

        VBox contentBox = new VBox();
        VBox add_line = new VBox();

        VBox current_mods = new VBox();

        Label indicatorLabel = new Label("Add mods to pool");
        Button addButton = new Button("Add Stat Mod");
        CheckBox fixed = new CheckBox("Fixed Mod?");
        CheckBox stat_mod = new CheckBox("Stat Mod?");
        ComboBox<ModifierType> modifierType = new ComboBox<>();
        modifierType.getItems().addAll(ModifierType.values());
        modifierType.setValue(ModifierType.FLAT);

        addButton.setOnAction(e-> {
            addMod(contentBox, stat_mod.isSelected(), pool, fixed.isSelected(), modifierType.getValue(), current_mods);
            showCurrentMods(current_mods, pool);
        });

        add_line.getChildren().addAll(modifierType, stat_mod, fixed, addButton);
        showCurrentMods(current_mods, pool);
        contentBox.getChildren().addAll(indicatorLabel,add_line, current_mods);
        return contentBox;
    }

    public Node allowDuplicate(CraftModPool pool) {
        VBox box = new VBox();
        CheckBox checkbox = new CheckBox("Allow Duplicate?");
        checkbox.setSelected(pool.isCan_duplicate_mod());
        checkbox.setOnAction(e-> {
            pool.setCan_duplicate_mod(checkbox.isSelected());
        });

        box.getChildren().add(checkbox);
        return box;
    }

    public void showCurrentMods(VBox box, CraftModPool pool) {
        box.getChildren().clear();
        AtomicInteger counter = new AtomicInteger(1);
        loopDisplayMods(box, pool.getWeaponMods(), "Weapon", counter);
        loopDisplayMods(box, pool.getArmorMods(), "Armor", counter);
        loopDisplayMods(box, pool.getAccessoryMods(), "Accessory", counter);
    }

    public void loopDisplayMods(VBox box, List<CraftedMod> list, String equipType, AtomicInteger counter) {
        list.forEach((mod) -> {
            HBox line = new HBox();
            box.getChildren().add(line);

            StringBuilder sb = new StringBuilder(Integer.toString(counter.getAndIncrement()));
            sb.append(". ").append(equipType).append(" : ");

            Label main_label = new Label();
            if (mod.isStatMod()) {
                sb.append(mod.getStatType().writeAsString());
            } else {
                sb.append(mod.getStatusType().writeAsString());
            }
            if (mod.isFixed()) {
                sb.append("\nFixed mod");
            } else {
                sb.append("\nWeight : ").append(mod.getWeight());
            }
            sb.append("\nType : ").append(mod.getModifierType().writeAsString());
            if (mod.isNegative()) {
                sb.append("\nNegative mod\n");
            } else {
                sb.append("\nPositive mod\n");
            }

            main_label.setText(sb.toString());
            line.getChildren().add(main_label);
            Button remove = new Button("Remove");
            remove.setOnAction(e-> {
                list.remove(mod);
                box.getChildren().remove(line);
            });
            line.getChildren().add(remove);
        });
    }

    public void addMod(VBox box, boolean stat_mod, CraftModPool pool, boolean fixed, ModifierType modifierType, VBox current_mod_box) {
        VBox main_box = new VBox();
        CraftedMod mod = new CraftedMod(modifierType, false, fixed);
        box.getChildren().add(main_box);
        ComboBox<String> equipmentType = new ComboBox<>();
        equipmentType.getItems().addAll("Weapon", "Armor", "Accessory");

        CheckBox negative = new CheckBox("Negative?");
        negative.setSelected(mod.isNegative());
        negative.setOnAction(e-> {
            mod.setNegative(negative.isSelected());
        });

        TextField weight = new TextField("Weight");
        if (!fixed) {
            weight.setText("1");
        }

        if (stat_mod) {
            ComboBox<StatType> statType = new ComboBox<>();
            statType.getItems().addAll(StatType.values());

            Button addButton = new Button("Add This Mod");
            addButton.setOnAction(e-> {
                if (!fixed) {
                    switch (equipmentType.getValue()) {
                        case ("Weapon") -> pool.addWeaponMod(statType.getValue(), Integer.parseInt(weight.getText()), negative.isSelected(), modifierType, false);
                        case ("Armor") -> pool.addArmorMod(statType.getValue(), Integer.parseInt(weight.getText()), negative.isSelected(), modifierType, false);
                        case ("Accessory") -> pool.addAccessoryMod(statType.getValue(), Integer.parseInt(weight.getText()), negative.isSelected(), modifierType, false);
                    }
                } else {
                    switch (equipmentType.getValue()) {
                        case ("Weapon") -> pool.addWeaponMod(statType.getValue(), 0, negative.isSelected(), modifierType, true);
                        case ("Armor") -> pool.addArmorMod(statType.getValue(), 0, negative.isSelected(), modifierType, true);
                        case ("Accessory") -> pool.addAccessoryMod(statType.getValue(), 0, negative.isSelected(), modifierType, true);
                    }
                }
                showCurrentMods(current_mod_box, pool);
                box.getChildren().remove(main_box);
            });
            main_box.getChildren().addAll(equipmentType,statType, weight, negative, addButton);
        } else {
            ComboBox<StatusType> statusType = new ComboBox<>();
            statusType.getItems().addAll(StatusType.values());

            Button addButton = new Button("Add This Mod");
            addButton.setOnAction(e-> {
                if (!fixed) {
                    switch (equipmentType.getValue()) {
                        case ("Weapon") -> pool.addWeaponMod(statusType.getValue(), Integer.parseInt(weight.getText()), negative.isSelected(), modifierType, false);
                        case ("Armor") -> pool.addArmorMod(statusType.getValue(), Integer.parseInt(weight.getText()), negative.isSelected(), modifierType, false);
                        case ("Accessory") -> pool.addAccessoryMod(statusType.getValue(), Integer.parseInt(weight.getText()), negative.isSelected(), modifierType, false);
                    }
                } else {
                    switch (equipmentType.getValue()) {
                        case ("Weapon") -> pool.addWeaponMod(statusType.getValue(), 0, negative.isSelected(), modifierType, true);
                        case ("Armor") -> pool.addArmorMod(statusType.getValue(), 0, negative.isSelected(), modifierType, true);
                        case ("Accessory") -> pool.addAccessoryMod(statusType.getValue(), 0, negative.isSelected(), modifierType, true);
                    }
                }
                showCurrentMods(current_mod_box, pool);
                box.getChildren().remove(main_box);
            });
            main_box.getChildren().addAll(equipmentType,statusType, weight, negative, addButton);
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

    public void setTreePane(TreePane treePane) {
        this.treePane = treePane;
    }
}

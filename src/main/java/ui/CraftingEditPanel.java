package ui;

import app.Database;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.entity.items.Item;
import model.entity.items.catalysts.CatalystEffect;
import model.entity.items.catalysts.CatalystFactory;
import model.entity.items.crafted_equipments.*;
import model.type.EquipmentType;
import model.type.ItemType;
import model.type.MaterialRole;
import model.type.WeaponType;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CraftingEditPanel extends ScrollPane {

    private final Database database;
    private final ToggleGroup modeToggle = new ToggleGroup();
    private final Button editModeBtn = new Button("Edit");
    private final Button createModeBtn = new Button("Create");
    private final List<Button> allListPaneBtn = new ArrayList<>();
    private boolean isEditMode = false;
    private final CraftingListPane listPane;
    private final VBox mainBox = new VBox();
    private final HBox listPaneButtonBox = new HBox();
    private boolean confirmDeletion = false;
    private CraftedEquipment toMake;
    private TreePane treePane;
    private CatalystFactory catalystFactory = new CatalystFactory();

    public CraftingEditPanel(Database database, CraftingListPane listPane) {
        toMake = new CraftedEquipment("New Crafted Equipment");

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
        CraftedEquipment selectedShop = listPane.getListView().getSelectionModel().getSelectedItem();
        deleteButton.setOnAction(e -> {
            if (!confirmDeletion) {
                System.out.println("Delete button clicked, click again to confirm deletion.");
                confirmDeletion = true;
            } else {
                if (selectedShop != null) {
                    database.getAllCraftedEquipments().remove(selectedShop.getName());
                }
                listPane.getListView().refresh();
                listPane.getEquipmentList().setAll(database.getAllCraftedEquipments().values());
                setButtonToSelected(createModeBtn, allListPaneBtn);
                editMode();
            }
        });

        if (selectedShop != null) {
            modeBox.getChildren().addAll(
                    editEquipmentName(selectedShop),
                    editType(selectedShop),
                    editMaterials(selectedShop));
        }
        mainBox.getChildren().addAll(listPaneButtonBox, deleteButton,modeBox);
    }

    public void createMode() {
        listPane.toList();
        mainBox.getChildren().clear();
        if (treePane != null)
            treePane.getChildren().clear();
        setButtonToSelected(createModeBtn, allListPaneBtn);
        isEditMode = false;
        confirmDeletion = false;

        toMake = new CraftedEquipment("New Crafted Equipment");
        Button createButton = new Button("CREATE");
        VBox modeBox = new VBox();

        createButton.setOnAction(e -> {
            database.getAllCraftedEquipments().put(toMake.getName(),toMake);
            database.translateEverything();
            listPane.getEquipmentList().setAll(database.getAllCraftedEquipments().values());
            listPane.getListView().refresh();
            editMode();
        });
        modeBox.getChildren().addAll(
                editEquipmentName(toMake),
                editType(toMake),
                editMaterials(toMake));
        mainBox.getChildren().addAll(listPaneButtonBox, createButton,modeBox);
    }

    public Node editEquipmentName(CraftedEquipment equipment) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("Equipment Name");
        TextArea textArea = new TextArea(equipment.getName());
        textArea.setWrapText(true);
        textArea.setMaxHeight(100);
        textArea.setMaxWidth(350);
        textArea.setOnKeyReleased(event -> {
            equipment.setName(textArea.getText());
            listPane.getListView().refresh();
        });
        contentBox.getChildren().addAll(indicatorLabel,textArea);
        return contentBox;
    }

    public Node editType(CraftedEquipment equipment) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("Equipment Type");
        TextArea textArea = new TextArea(equipment.getEquipmentType().writeAsString());
        textArea.setWrapText(true);
        textArea.setMaxHeight(100);
        textArea.setMaxWidth(350);
        textArea.setOnKeyReleased(event -> {
            for (EquipmentType type : EquipmentType.values()) {
            if (textArea.getText().equalsIgnoreCase(type.writeAsString())) {
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
        contentBox.getChildren().addAll(indicatorLabel,textArea, weaponTypeLabel, weaponTypeArea);
        return contentBox;
    }

    public Node editMaterials(CraftedEquipment equipment) {
        VBox contentBox = new VBox();
        Label indicatorLabel = new Label("Materials");
        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setMaxHeight(100);
        textArea.setMaxWidth(350);
        VBox display_box = new VBox();
        VBox show_mods = new VBox();

        Button add_base = new Button("Add Base");
        add_base.setOnAction(e-> {
            CraftingMaterial material = database.getAllMaterials().get(textArea.getText());
            if (material == null) {
                System.out.println("Material not found");
                return;
            }
            equipment.addMaterial(material, MaterialRole.BASE);
            displayCurrentMaterial(display_box, equipment);
            showMods(show_mods, equipment);
        });

        Button add_boost = new Button("Add Boost");
        add_boost.setOnAction(e-> {
            CraftingMaterial material = database.getAllMaterials().get(textArea.getText());
            if (material == null) {
                System.out.println("Material not found");
                return;
            }
            equipment.addMaterial(material, MaterialRole.BOOST);
            displayCurrentMaterial(display_box, equipment);
            showMods(show_mods, equipment);
        });
        displayCurrentMaterial(display_box, equipment);
        showMods(show_mods, equipment);

        Button craft = new Button("Craft");
        craft.setOnAction(e-> {
            Crafter.craft(equipment);
            showMods(show_mods, equipment);
            listPane.getListView().refresh();
        });

        ComboBox<String> catalyst_select = new ComboBox<>();
        for (Map.Entry<String, Item> entry : database.getAllNormalItemMap().entrySet()) {
            if (entry.getValue().getItemType() == ItemType.CATALYST) {
                catalyst_select.getItems().add(entry.getValue().getName());
            }
        }
        Button apply_catalyst = new Button("Apply Catalyst");
        apply_catalyst.setOnAction(e-> {
            Crafter.useCatalyst(catalyst_select.getValue(), equipment);
            showMods(show_mods, equipment);
            listPane.getListView().refresh();
        });

        contentBox.getChildren().addAll(indicatorLabel,textArea, add_base, add_boost, display_box, craft, show_mods, catalyst_select, apply_catalyst);
        return contentBox;
    }

    public void showMods(VBox box, CraftedEquipment equipment) {
        box.getChildren().clear();

        for (ModInstance modInstance : equipment.getModInstances()) {
            CraftingMaterial base_material = null;
            for (MaterialInstance materialInstance : equipment.getMaterialInstances()) {
                if (materialInstance.getMaterial_id() == modInstance.getBase_material_id()) {
                    base_material = materialInstance.getMaterial();
                    break;
                }
            }
            if (base_material == null) {
                base_material = new CraftingMaterial(new Item("material not found"));
            }

            Label label = new Label();
            DecimalFormat df = new DecimalFormat("#.##");
            label.setText(modInstance.getMod_id()+". "+modInstance.getMod().getAffectingModString()+" ["+modInstance.getPool_name()+"] \n" +
                    "from material id : "+modInstance.getBase_material_id()+" ("+base_material.getName()+"\n" +
                    "tier : "+modInstance.getTier()+"\n" +
                    "rolled : "+df.format(modInstance.getBase_rolled())+" | final value : "+ df.format(modInstance.getFinal_value())+"\n");

            box.getChildren().add(label);
        }
    }

    public void displayCurrentMaterial(VBox display_box, CraftedEquipment equipment) {
        display_box.getChildren().clear();
        for (MaterialInstance materialInstance : equipment.getMaterialInstances()) {
            StringBuilder sb = new StringBuilder();
            sb.append(materialInstance.getMaterial().getName()).append(" : ").append(materialInstance.getMaterial_role().writeAsString()).append("\n");
            Label label = new Label(sb.toString());

            Button delete = new Button("Delete");
            delete.setOnAction(e-> {
                equipment.removeMaterial(materialInstance);
                displayCurrentMaterial(display_box, equipment);
            });
            display_box.getChildren().addAll(label, delete);
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

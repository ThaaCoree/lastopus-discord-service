package model.entity.items.crafted_equipments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import model.entity.items.Equipment;
import model.type.EquipmentType;
import model.type.MaterialRole;
import model.type.StatTag;
import util.WeightedRandom;

import java.util.ArrayList;
import java.util.List;

public class CraftedEquipment extends Equipment {
    String id;
    public int mod_id = 0;
    private int next_material_id = 0;
    private int next_catalyst_id = 0;
    public List<ModInstance> modInstances = new ArrayList<>();
    public List<MaterialInstance> materialInstances = new ArrayList<>();
    public List<CatalystInstance> catalystInstances = new ArrayList<>();

    public CraftedEquipment() {

    }

    public CraftedEquipment(String name) {
        super(name);
    }

    public void addMaterial(CraftingMaterial material, MaterialRole role) {
        MaterialInstance instance = new MaterialInstance (next_material_id++, material, role);
        materialInstances.add(instance);
    }

    public void removeMaterial(MaterialInstance materialInstance) {
        materialInstances.remove(materialInstance);
    }

    public void addCatalyst(String catalyst_name) {
        CatalystInstance instance = new CatalystInstance (next_catalyst_id++, catalyst_name);
        catalystInstances.add(instance);
    }

    public void resetMaterial() {
        materialInstances.clear();
    }

    public List<MaterialInstance> getMaterialInstances() {
        return materialInstances;
    }

    public void setMaterialInstances(List<MaterialInstance> materialInstances) {
        this.materialInstances = materialInstances;
    }

    public List<CatalystInstance> getCatalystInstances() {
        return catalystInstances;
    }

    public void setCatalystInstances(List<CatalystInstance> catalystInstances) {
        this.catalystInstances = catalystInstances;
    }

    public List<ModInstance> getModInstances() {
        return modInstances;
    }

    @JsonIgnore
    public int getMod_id() {
        return mod_id;
    }

    @JsonIgnore
    public void setMod_id(int mod_id) {
        this.mod_id = mod_id;
    }

    @JsonIgnore
    public int getNext_catalyst_idAndIncrement() {
        return next_catalyst_id++;
    }
}

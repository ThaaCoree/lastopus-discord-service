package model.entity.items.crafted_equipments;

import model.type.MaterialRole;

public class MaterialInstance {
    public int material_id;
    public CraftingMaterial material;
    public MaterialRole material_role;

    public MaterialInstance() {

    }
    public MaterialInstance(int material_id, CraftingMaterial material, MaterialRole material_role) {
        this.material_id = material_id;
        this.material = material;
        this.material_role = material_role;
    }

    public int getMaterial_id() {
        return material_id;
    }

    public void setMaterial_id(int material_id) {
        this.material_id = material_id;
    }

    public CraftingMaterial getMaterial() {
        return material;
    }

    public void setMaterial(CraftingMaterial material) {
        this.material = material;
    }

    public MaterialRole getMaterial_role() {
        return material_role;
    }

    public void setMaterial_role(MaterialRole material_role) {
        this.material_role = material_role;
    }
}

package model.entity.items.crafted_equipments;

public class CatalystInstance {
    int catalyst_id;
    String catalyst_name;

    public CatalystInstance(int catalyst_index, String catalyst_name) {
        this.catalyst_id = catalyst_index;
        this.catalyst_name = catalyst_name;
    }
}
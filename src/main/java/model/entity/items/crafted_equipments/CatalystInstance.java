package model.entity.items.crafted_equipments;

public class CatalystInstance {
    int catalyst_id;
    String catalyst_name;

    public CatalystInstance(int catalyst_id, String catalyst_name) {
        this.catalyst_id = catalyst_id;
        this.catalyst_name = catalyst_name;
    }

    public CatalystInstance() {

    }

    public int getCatalyst_id() {
        return catalyst_id;
    }

    public void setCatalyst_id(int catalyst_id) {
        this.catalyst_id = catalyst_id;
    }

    public String getCatalyst_name() {
        return catalyst_name;
    }

    public void setCatalyst_name(String catalyst_name) {
        this.catalyst_name = catalyst_name;
    }
}
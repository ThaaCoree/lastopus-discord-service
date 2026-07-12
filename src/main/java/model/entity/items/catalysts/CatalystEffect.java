package model.entity.items.catalysts;

import model.entity.items.crafted_equipments.CraftedEquipment;

public interface CatalystEffect {
    boolean canApply(CraftedEquipment equipment);          // validation rule
    String apply(CraftedEquipment equipment);
    String getCatalyst_name();
    boolean brick(CraftedEquipment equipment);
}

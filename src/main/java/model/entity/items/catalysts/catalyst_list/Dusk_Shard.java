package model.entity.items.catalysts.catalyst_list;

import model.entity.items.catalysts.CatalystEffect;
import model.entity.items.catalysts.ValidationResult;
import model.entity.items.crafted_equipments.CraftedEquipment;
import model.entity.items.crafted_equipments.Crafter;
import model.entity.items.crafted_equipments.ModInstance;

import java.util.Set;

public class Dusk_Shard implements CatalystEffect {
    String catalyst_name = "Dusk Shard";

    @Override
    public ValidationResult canApply(CraftedEquipment equipment) {
        return new ValidationResult(true, "");
    }

    @Override
    public String apply(CraftedEquipment equipment) {
        // ต้อง snapshot ก่อน craft เพราะ craft จะปลดล็อกม็อดล็อก
        Set<ModInstance> lockeds = Crafter.snapshotLockedMods(equipment.getModInstances());
        StringBuilder sb = new StringBuilder();
        Crafter.craft(equipment, sb);

        for (ModInstance modInstance : equipment.getModInstances()) {
            if (lockeds.contains(modInstance)) continue;
            modInstance.multiplyFinal_value(1.2);
        }

        return sb.toString()+"คราฟท์ " + equipment.getName() + " ใหม่โดยเพิ่มค่าตัวเลขให้กับทุกม็อด 20% แล้ว";
    }

    @Override
    public boolean brick(CraftedEquipment equipment) {
        return false;
    }

    @Override
    public String getCatalyst_name() {
        return catalyst_name;
    }

}

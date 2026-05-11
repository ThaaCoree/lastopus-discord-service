package app.servicecontroller;

import app.Database;
import app.service.ServiceDatabase;
import app.servicemodel.PlayerMessage;
import model.entity.items.Equipment;
import model.entity.items.EquipmentSlot;
import model.entity.units.Unit;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import util.GoogleSheetsUtil;

import java.util.Map;

@RestController
public class DiscordController {

    private MongoTemplate mongoTemplate;
    private ServiceDatabase database;

    public DiscordController(MongoTemplate mongoTemplate, ServiceDatabase database) {
        this.mongoTemplate = mongoTemplate;
        this.database = database;
    }

    @PostMapping("/equip")
    public String equip(@RequestBody PlayerMessage playerMessage) {
        String name = "";
        for (String role : playerMessage.roles) {
            if (role.equals("Christ")) {
                name = "Christ";
            }
        }
        Unit unit = database.findUnit(name);
        Equipment equipment = database.findEquipment(playerMessage.message);
        if (unit != null) {
            if (equipment != null) {
                unit.getEquipmentManager().equip(equipment, 1);
                unit.writeToSheet();
                database.save_player(database.allPlayerMap);
                return "สวมใส่ "+equipment.getName()+" ในช่อง "+equipment.getEquipmentType().writeAsString()+" แล้ว";
            } else {
                return "ไม่พบ Equipment";
            }
        } else {
            return "No Role!";
        }
    }

    @PostMapping("/unequip")
    public String unequip(@RequestBody PlayerMessage playerMessage) {
        String name = "";
        for (String role : playerMessage.roles) {
            if (role.equals("Christ")) {
                name = "Christ";
            }
        }
        Equipment equipment = null;
        Unit unit = database.findUnit(name);
        int slot = 0;

        if (unit != null) {
        for (Map.Entry<Integer, EquipmentSlot> entry : unit.getEquipmentSlots().entrySet()) {
            if (entry.getValue().getEquipment().getName().equals(playerMessage.message)) {
                slot = entry.getKey();
                equipment = entry.getValue().getEquipment();
            }
        }
            if (equipment != null) {
                unit.getEquipmentManager().unequip(slot);
                return "ถอด "+equipment.getName()+" จากช่อง "+equipment.getEquipmentType().writeAsString()+" แล้ว";
            } else {
                return "ไม่พบ Equipment";
            }
        } else {
            return "No Role!";
        }
    }
}
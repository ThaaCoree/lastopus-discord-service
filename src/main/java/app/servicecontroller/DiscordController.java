package app.servicecontroller;

import app.service.ServiceDatabase;
import app.servicemodel.PlayerMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import model.entity.items.Equipment;
import model.entity.items.EquipmentSlot;
import model.entity.items.Item;
import model.entity.items.Rune;
import model.entity.units.Unit;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
        String name = getPlayerName(playerMessage.roles);
        Unit unit = database.findPlayer(name);
        Equipment equipment;
        if (unit != null) {
            equipment = unit.findEquipment(playerMessage.message);
            if (equipment != null) {
                unit.getEquipmentManager().equip(equipment, 1);
                writeintoSheet(unit);
                return "สวมใส่ "+equipment.getName()+" ในช่อง "+equipment.getEquipmentType().writeAsString()+" ของ "+unit.getName()+" แล้ว";
            } else {
                return "ไม่พบ Equipment";
            }
        } else {
            return "No Role!";
        }
    }

    @PostMapping("/unequip")
    public String unequip(@RequestBody PlayerMessage playerMessage) {
        String name = getPlayerName(playerMessage.roles);
        Equipment equipment = null;
        Unit unit = database.findPlayer(name);
        int slot = 0;

        if (unit != null) {
        for (Map.Entry<Integer, EquipmentSlot> entry : unit.getEquipmentSlots().entrySet()) {
            if (entry.getValue().getEquipment() == null) continue;
            if (entry.getValue().getEquipment().getName().equals(playerMessage.message)) {
                slot = entry.getKey();
                equipment = entry.getValue().getEquipment();
            }
        }
            if (equipment != null) {
                unit.getEquipmentManager().unequip(slot);
                writeintoSheet(unit);
                return "ถอด "+equipment.getName()+" จากช่อง "+equipment.getEquipmentType().writeAsString()+" ของ "+unit.getName()+" แล้ว";
            } else {
                return "ไม่พบ Equipment";
            }
        } else {
            return "No Role!";
        }


    }

    @PostMapping("/give")
    public String give(@RequestBody PlayerMessage playerMessage) {
        String name = getPlayerName(playerMessage.roles);
        Unit giver = database.findPlayer(name);
        if (giver != null) {
            if (playerMessage.mentionedUsers == null || playerMessage.mentionedUsers.isEmpty()) {
                return "กรุณาแท็กเป้าหมาย";
            }
            String target_name = getPlayerName(playerMessage.mentionedUsers.get(0).roles, false);
            Unit target = database.findPlayer(target_name);
            if (target == null) return "กรุณาแท็กเป้าหมายที่ถูกต้อง";
            int amount = 0;
            try {
                amount = Integer.parseInt(playerMessage.args.get(playerMessage.args.size() - 1));
            } catch (NumberFormatException e) {
                return "จำนวนไม่ถูกต้อง";
            }
            if (amount <= 0) {
                return "จำนวนต้องมากกว่า 0";
            }
            String itemName = String.join(" ", playerMessage.args.subList(1, playerMessage.args.size() - 1));
            Item item = giver.findItemInventory(itemName);
            if (item == null) return "ไม่พบไอเทม";
            int original_amount = giver.getInventoryManager().getQuantityFromInventory(item.getName());
            if (amount > original_amount) return "มีไอเทมไม่เพียงพอ";

            if (item instanceof Rune rune) {
                target.addRuneToInventory((Rune) rune);
                giver.removeRuneFromInventory((Rune) rune);
                writeintoSheet(giver);
                writeintoSheet(target);
                return giver.getName() + " มอบ " + item.getName() + " ให้กับ " + target_name + " " + amount + " หน่วย\n" +
                        "<@" + playerMessage.mentionedUsers.get(0).id + ">";
            } else {
                target.getInventoryManager().addItem(item, amount);
                giver.getInventoryManager().removeItem(item.getName(), amount);

                System.out.println("giver inventory: " + giver.getInventoryItems());
                System.out.println("giver in map: " + database.allPlayerMap.get(name).getInventoryItems());

                writeintoSheet(giver);
                writeintoSheet(target);
                return giver.getName() + " มอบ " + item.getName() + " ให้กับ " + target_name + " " + amount + " หน่วย\n" +
                        "<@" + playerMessage.mentionedUsers.get(0).id + ">";
            }
        } else {
            return "No Role!";
        }
    }

    @PostMapping("/buyrune")
    public String buyrune(@RequestBody PlayerMessage playerMessage) {
        String name = getPlayerName(playerMessage.roles);
        Unit unit = database.findPlayer(name);
        if (unit != null) {
            int amount;
        try {
            amount = Integer.parseInt(playerMessage.message);
        } catch (NumberFormatException e) {
            return "จำนวนไม่ถูกต้อง";
        }
        if (amount > 20) {
            return "จำกัดการซื้อครั้งละไม่เกิน 20 ชิ้น!";
        }
            StringBuilder stringBuilder = new StringBuilder(unit.getName());
            stringBuilder.append(" ได้รับ\n");

            for (int i = 0; i < amount; i++) {
                Rune rune = Rune.randomRune(unit, database.allRuneMap);
                unit.addRuneToInventory(rune);
                stringBuilder.append("## ").append(i+1).append(". ").append("[").append(rune.getName()).append("]");
                if (rune.isUnique_rune()) {
                    stringBuilder.append(" UNIQUE RUNE! ");
                }
                stringBuilder.append("\n").append(rune.getStatusDescription()).append(rune.getDescription()).append("\n");
            }
            writeintoSheet(unit);
            return stringBuilder.toString();
        } else {
            return "No Role!";
        }
    }

    public boolean isGM(List<String> roles) {
        return roles.contains("GM");
    }

    public String getPlayerName(List<String> roles, boolean load_mongo) {
        if (load_mongo) {
            database.load_player();
        }
        for (String role : roles) {
            if (role.equals("Christ")) {
                return "Christ";
            }
            if (role.equals("Leda")) {
                return "Leda";
            }
            if (role.equals("Akivili")) {
                return "Akivili";
            }
            if (role.equals("Pumpkin'Slayerman")) {
                return "Pumpkin'Slayerman";
            }
            if (role.equals("Acheros Aki")) {
                return "Acheros Aki";
            }
            if (role.equals("Twelve")) {
                return "Twelve";
            }
            if (role.equals("Slafier")) {
                return "Slafier";
            }
            if (role.equals("Shiranui")) {
                return "Shiranui";
            }
            if (role.equals("Four-Leaf Clover777")) {
                return "Four-Leaf Clover777";
            }
            if (role.equals("Onebrek")) {
                return "Onebrek";
            }
            if (role.equals("Aard Archer")) {
                return "Aard Archer";
            }
            if (role.equals("Esther")) {
                return "Esther";
            }
            if (role.equals("Yasha")) {
                return "Yasha";
            }
            if (role.equals("Voahri")) {
                return "Voahri";
            }
            if (role.equals("Scarlet")) {
                return "Scarlet";
            }
        }
        return "";
    }

    public String getPlayerName(List<String> roles) {
        return getPlayerName(roles, true);
    }

    public void writeintoSheet(Unit unit) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            String json = mapper.writeValueAsString(unit);
            database.save_player(json);
            unit.writeToSheet(database.load_credentials());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void savePlayer(Unit unit) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            String json = mapper.writeValueAsString(unit);
            database.save_player(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
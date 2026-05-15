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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        List<String> args = playerMessage.args;
        String itemName = playerMessage.message;

        if (unit != null) {
            equipment = unit.findEquipment(itemName);
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

    @PostMapping("/equip2")
    public String equip2(@RequestBody PlayerMessage playerMessage) {
        String name = getPlayerName(playerMessage.roles);
        Unit unit = database.findPlayer(name);
        Equipment equipment;
        String itemName = playerMessage.message;

        if (unit != null) {
            equipment = unit.findEquipment(itemName);
            if (equipment != null) {
                unit.getEquipmentManager().equip(equipment, 2);
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
            if (entry.getValue().getEquipment().getName().equalsIgnoreCase(playerMessage.message)) {
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
                return "กรุณาใช้คำสั่ง ?giverune";
            } else {
                target.getInventoryManager().addItem(item, amount);
                giver.getInventoryManager().removeItem(item.getName(), amount);

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
            int price = 20;
        try {
            amount = Integer.parseInt(playerMessage.message);
        } catch (NumberFormatException e) {
            return "จำนวนไม่ถูกต้อง";
        }
        if (amount > 20) {
            return "จำกัดการซื้อครั้งละไม่เกิน 20 ชิ้น!";
        }
            if (price*amount > unit.getCopperCoin()) {
                return "มีเงินไม่เพียงพอ!";
            }
            StringBuilder stringBuilder = new StringBuilder(unit.getName());
            stringBuilder.append(" ได้รับ\n");

            for (int i = 0; i < amount; i++) {
                Rune rune = Rune.randomRune(unit, database.allRuneMap);
                unit.addRuneToInventory(rune);
                unit.reduceCopperCoin(price);
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

    @PostMapping("/giverune")
    public String giverune(@RequestBody PlayerMessage playerMessage) {
        String name = getPlayerName(playerMessage.roles);
        Unit giver = database.findPlayer(name);
        if (giver != null) {
            if (playerMessage.mentionedUsers == null || playerMessage.mentionedUsers.isEmpty()) {
                return "กรุณาแท็กเป้าหมาย";
            }
            String target_name = getPlayerName(playerMessage.mentionedUsers.get(0).roles, false);
            Unit target = database.findPlayer(target_name);
            if (target == null) return "กรุณาแท็กเป้าหมายที่ถูกต้อง";
            int index = 0;
            try {
                index = Integer.parseInt(playerMessage.args.get(playerMessage.args.size() - 1));
            } catch (NumberFormatException e) {
                return "หมายเลขไม่ถูกต้อง";
            }
            if (index < 0) {
                return "หมายเลขต้องมากกว่า -1";
            }
            Rune rune = giver.findRune(index);
            if (rune == null) return "ไม่พบรูน";
            {
                target.addRuneToInventory(rune);
                giver.removeRuneFromInventory(rune);

                writeintoSheet(giver);
                writeintoSheet(target);
                return giver.getName() + " มอบ \n" + rune.getName()+ "\n" + rune.getStatusDescription() + rune.getDescription() + " ให้กับ " + target_name + " แล้ว\n" +
                        "<@" + playerMessage.mentionedUsers.get(0).id + ">";
            }
        } else {
            return "No Role!";
        }
    }

    @PostMapping("/pay")
    public String pay(@RequestBody PlayerMessage playerMessage) {
        String name = getPlayerName(playerMessage.roles);
        Unit giver = database.findPlayer(name);
        if (giver != null) {
            String target_name = getPlayerName(playerMessage.mentionedUsers.get(0).roles, false);
            Unit target = database.findPlayer(target_name);

            int amount;
            try {
                amount = Integer.parseInt(playerMessage.args.get(playerMessage.args.size() - 1));
            } catch (NumberFormatException e) {
                return "จำนวนไม่ถูกต้อง";
            }
            if (amount <= 0) {
                return "จำนวนน้อยกว่า 0 ไม่ได้";
            }

            if (amount > giver.getCopperCoin()) return "มีเงินไม่มากพอ";

            target.increaseCopperCoin(amount);
            giver.reduceCopperCoin(amount);
            writeintoSheet(giver);
            writeintoSheet(target);
            return giver.getName() + " มอบเงินมูลค่า "+amount+" ให้กับ "+target.getName()+" แล้ว\n" +
                    "<@" + playerMessage.mentionedUsers.get(0).id + ">";
        } else {
            return "No Role!";
        }
    }

    @PostMapping("/update")
    public String update(@RequestBody PlayerMessage playerMessage) {
        String name = getPlayerName(playerMessage.roles);
        Unit updater = database.findPlayer(name);
        if (updater != null) {
            updater.writeToSheet(database.load_credentials());
            return "อัพเดทชีทของ "+updater.getName()+" เสร็จสิ้น";
        } else {
            return "No Role!";
        }
    }

    @PostMapping("/load_database")
    public String load_database(@RequestBody PlayerMessage playerMessage) {
        if (isGM(playerMessage.roles)) {
            database.loadMongo();
            return "อัพเดท Database แล้ว";
        } else {
            return "นี่เป็นคำสั่งสำหรับ GM เท่านั้น";
        }
    }

    @PostMapping("/copper_coin")
    public String copper_coin(@RequestBody PlayerMessage playerMessage) {
        if (isGM(playerMessage.roles)) {
            String name = getPlayerName(playerMessage.mentionedUsers.get(0).roles);
            Unit unit = database.findPlayer(name);
            int amount;
            try {
                amount = Integer.parseInt(playerMessage.args.get(playerMessage.args.size() - 1));
            } catch (NumberFormatException e) {
                return "จำนวนไม่ถูกต้อง";
            }

            unit.increaseCopperCoin(amount);
            writeintoSheet(unit);
            return "มอบเงิน "+amount+" ให้กับ "+unit.getName()+" แล้ว";
        } else {
            return "นี่เป็นคำสั่งสำหรับ GM เท่านั้น";
        }
    }

    @PostMapping("/runecrush")
    public String runecrush(@RequestBody PlayerMessage playerMessage) {
        String name = getPlayerName(playerMessage.roles);
        Unit unit = database.findPlayer(name);
        if (unit != null) {
            List<Integer> numbers = playerMessage.args.stream()
                    .filter(arg -> arg.matches("\\d+"))
                    .map(Integer::parseInt)
                    .toList();
            int dust_amount = 0;
            for (Integer number : numbers) {
                unit.getRune_inventory().remove(number);
                dust_amount++;
            }
            Item dust = database.allNormalItemMap.get("Rune Dust");
            unit.getInventoryManager().addItem(dust, dust_amount);

            return "ย่อยสลายรูนเรียบร้อย! ได้รับ "+dust_amount+" Rune Dust";
        } else {
            return "No Role!";
        }
    }

    @PostMapping("/runecreate")
    public String runecreate(@RequestBody PlayerMessage playerMessage) {
        String name = getPlayerName(playerMessage.roles);
        Unit unit = database.findPlayer(name);
        if (unit != null) {
            int amount;
            try {
                amount = Integer.parseInt(playerMessage.args.get(playerMessage.args.size() - 1));
            } catch (NumberFormatException e) {
                return "จำนวนไม่ถูกต้อง";
            }
            if (amount > 20) {
                return "สร้างรูนมากกว่า 20 ชิ้นต่อครั้งไม่ได้";
            }
            String itemName = String.join(" ", playerMessage.args.subList(1, playerMessage.args.size() - 1));

            StringBuilder stringBuilder = new StringBuilder(unit.getName()+" สร้างรูน\n");

            List<Rune> created_list = new ArrayList<>();
            for (int i = 0; i < amount; i++) {
                Rune rune = Rune.createRandomRune(unit, database.allRuneMap, itemName);
                created_list.add(rune);
                stringBuilder.append("## ").append(i+1).append(". ").append("[").append(rune.getName()).append("]");
                if (rune.isUnique_rune()) {
                    stringBuilder.append(" UNIQUE RUNE! ");
                }
                stringBuilder.append("\n").append(rune.getStatusDescription()).append(rune.getDescription()).append("\n");
            }
            int used_dust = 0;
            for (Rune rune : created_list) {
                used_dust += rune.occupying_slots();
            }
            int have_dust = unit.findItemAmount("Rune Dust");
            if (used_dust > have_dust) {
                return "มีผงรูนไม่พอ";
            }

            for (Rune rune : created_list) {
                unit.addRuneToInventory(rune);
            }
            unit.getInventoryManager().reduceItem("Rune Dust", used_dust);
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
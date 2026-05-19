package app.servicecontroller;

import app.service.ServiceDatabase;
import app.servicemodel.PlayerMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.api.services.sheets.v4.model.Request;
import model.entity.Shop;
import model.entity.ShopItem;
import model.entity.items.Equipment;
import model.entity.items.EquipmentSlot;
import model.entity.items.Item;
import model.entity.items.Rune;
import model.entity.units.Unit;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ui.MainPane;
import util.GoogleSheetsUtil;
import util.WeightedRandom;

import java.util.*;
import java.util.stream.Collectors;

import static ui.MainPane.getExcelColumnName;

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
            String target_name = getPlayerName(playerMessage.mentionedUsers.get(0).roles);
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
            String target_name = getPlayerName(playerMessage.mentionedUsers.get(0).roles);
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
            String target_name = getPlayerName(playerMessage.mentionedUsers.get(0).roles);
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
        long start = System.currentTimeMillis();
        long t;

        String name = getPlayerName(playerMessage.roles);

        Unit updater = database.findPlayer(name);

        if (updater != null) {
            var credentials = database.load_credentials();

            updater.writeToSheet(credentials);

            System.out.println("Total: " + (System.currentTimeMillis() - start) + "ms");
            return "อัพเดทชีทของ " + updater.getName() + " เสร็จสิ้น";
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
                if (unit.getRune_inventory().get(number) != null) {
                    dust_amount++;
                }
                unit.getRune_inventory().remove(number);
            }
            Item dust = database.allNormalItemMap.get("Rune Dust");
            unit.getInventoryManager().addItem(dust, dust_amount);
            writeintoSheet(unit);
            return "ย่อยสลายรูนเรียบร้อย! ได้รับ "+dust_amount+" Rune Dust อย่าลืมรีเซ็ตหน้าเว็บรูนบอร์ดนะ!";
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
            String itemName = String.join(" ", playerMessage.args.subList(0, playerMessage.args.size() - 1));

            StringBuilder stringBuilder = new StringBuilder(unit.getName()+" สร้างรูน\n");

            int used_dust = 0;

            List<Rune> created_list = new ArrayList<>();
            for (int i = 0; i < amount; i++) {
                Rune rune = Rune.createRandomRune(unit, database.allRuneMap, itemName);
                if (rune == null) return "กรุณาใส่ชื่อรูนที่ถูกต้อง";
                WeightedRandom<Boolean> chance_one_break = new WeightedRandom<>();
                chance_one_break.add(true, 2);
                chance_one_break.add(false, 8);
                if (chance_one_break.roll() && rune.occupying_slots() == 1) {
                    stringBuilder.append("## ").append(i + 1).append(". ").append("[Rune Break!]").append("\n");
                    used_dust += 1;
                } else {
                    created_list.add(rune);
                    stringBuilder.append("## ").append(i + 1).append(". ").append("[").append(rune.getName()).append("]");
                    if (rune.isUnique_rune()) {
                        stringBuilder.append(" UNIQUE RUNE! ");
                    }
                    stringBuilder.append("\n").append(rune.getStatusDescription()).append(rune.getDescription()).append("\n");
                }
            }
            System.out.println(created_list);
            for (Rune rune : created_list) {
                used_dust += rune.occupying_slots();
            }
            int have_dust = unit.findItemAmount("Rune Dust");
            if (used_dust > have_dust) {
                return "มีผงรูนไม่พอ";
            }

            for (Rune rune : created_list) {
                unit.addRuneToInventory(rune);
                System.out.println(rune);
            }
            unit.getInventoryManager().reduceItem("Rune Dust", used_dust);
            writeintoSheet(unit);
            return stringBuilder.toString();
        } else {
            return "No Role!";
        }
    }

    @PostMapping("/itembuy")
    public String itembuy(@RequestBody PlayerMessage playerMessage) {
        String name = getPlayerName(playerMessage.roles);
        Unit unit = database.findPlayer(name);
        if (unit != null) {
            String[] parts = playerMessage.message.split("/");
            // parts[0] = "Alexa Shop "
            // parts[1] = " Blue Berry 7"

            String shopName = parts[0].trim();
            String[] itemParts;
            try {
                itemParts = parts[1].trim().split(" ");
            } catch (ArrayIndexOutOfBoundsException e) {
                return "ต้องแบ่งชื่อร้านและชื่อไอเทมด้วยอักษร / ";
            }
            int quantity = 0;
            try {
                quantity = Integer.parseInt(itemParts[itemParts.length - 1]);
            } catch (NumberFormatException e) {
                return "จำนวนไม่ถูกต้อง";
            }
            if (quantity == 0) {
                return "ซื้อไอเทม 0 ชิ้นไม่ได้";
            }
            String itemName = String.join(" ", Arrays.copyOfRange(itemParts, 0, itemParts.length - 1));

            // shopName = "Alexa Shop"
            // itemName = "Blue Berry"
            // quantity = 7

            Shop shop = database.allShop.get(shopName);
            if (shop == null) return "ไม่พบชื่อร้านดังกล่าว";
            if (!unit.getCurrent_city().contains(shop.getCity())) {
                return "ตัวละคร "+unit.getName()+" ไม่ได้อยู่ในพื้นที่เดียวกับร้านดังกล่าว";
            }
            Item item = new Item("");
            int stock = -1;
            int price = -1;
            ShopItem shopItem = new ShopItem();
            for (ShopItem si : shop.getList().values()) {
                if (si.getItem() == null) continue;
                if (si.getItem().getName().equals(itemName)) {
                    item = si.getItem();
                    stock = si.getStock();
                    price = si.getPrice_in_copper();
                    shopItem = si;
                    break;
                }
            }
            if (item == null || item.getName().isEmpty()) {
                return "ไม่พบไอเทมดังกล่าว";
            }
            if (price == -1) {
                return "ไม่พบราคาของไอเทมดังกล่าว";
            }
            if (stock < quantity) {
                return "ในร้านมีจำนวนไอเทมไม่เพียงพอ";
            }
            if (unit.getCopperCoin() < price*quantity) {
                return "มีเงินไม่เพียงพอ";
            }
            unit.reduceCopperCoin(price*quantity);
            unit.getInventoryManager().addItem(item, quantity);
            shopItem.setStock(shopItem.getStock() - quantity);
            updateShops();

            database.save_shop();
            writeintoSheet(unit);
            return unit.getName()+" ซื้อ "+item.getName()+" จากร้านของ "+shopName+" เป็นจำนวน "+quantity+" ชิ้นแล้ว";
        } else {
            return "No Role!";
        }
    }

    public boolean isGM(List<String> roles) {
        return roles.contains("GM");
    }

    public String getPlayerName(List<String> roles) {
        String name = "";

        for (String role : roles) {
            if (role.equals("Christ")) {
                name = "Christ";
            }
            if (role.equals("Leda")) {
                name = "Leda";
            }
            if (role.equals("Akivili")) {
                name = "Akivili";
            }
            if (role.equals("Pumpkin'Slayerman")) {
                name = "Pumpkin'Slayerman";
            }
            if (role.equals("Acheros Aki")) {
                name = "Acheros Aki";
            }
            if (role.equals("Twelve")) {
                name = "Twelve";
            }
            if (role.equals("Slafier")) {
                name = "Slafier";
            }
            if (role.equals("Shiranui")) {
                name = "Shiranui";
            }
            if (role.equals("Four-Leaf Clover777")) {
                name = "Four-Leaf Clover777";
            }
            if (role.equals("Onebrek")) {
                name = "Onebrek";
            }
            if (role.equals("Aard Archer")) {
                name = "Aard Archer";
            }
            if (role.equals("Esther")) {
                name = "Esther";
            }
            if (role.equals("Yasha")) {
                name = "Yasha";
            }
            if (role.equals("Voahri")) {
                name = "Voahri";
            }
            if (role.equals("Scarlet")) {
                name = "Scarlet";
            }
        }
        if (!name.isEmpty()) {
            Unit unit = database.findPlayer(name);
            database.load_player(unit);
        }
        return name;
    }

    public void writeintoSheet(Unit unit) {
        unit.calculateEverything();
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

    public void updateShops() {
        try {
            GoogleSheetsUtil sheetsUtil = new GoogleSheetsUtil(database.load_credentials());
            int index = 1;
            for (Map.Entry<String, Shop> entry : database.allShop.entrySet()) {
                if (!entry.getValue().isOpen()) continue;
                List<Request> requests = new ArrayList<>();
                int baseRow = 4;
                int step = 10;
                Integer sheetId = sheetsUtil.getSheetIdByName(GoogleSheetsUtil.viewerSheetId, "Shop");
                String indicatorRange = "B" + (baseRow-1 + (index-1) * step);
                String cityRange = "C" + (baseRow-1 + (index-1) * step);
                int columnIndex = 2;
                int itemIndex = 1;
                List<List<Object>> indicatorAppend;
                indicatorAppend = new ArrayList<>(List.of(
                        List.of(entry.getValue().getOwnerName())
                ));
                requests.add(GoogleSheetsUtil.buildUpdateCellsRequest(sheetId, indicatorRange, indicatorAppend));
                List<List<Object>> cityAppend;
                cityAppend = new ArrayList<>(List.of(
                        List.of(entry.getValue().getCityName())
                ));
                requests.add(GoogleSheetsUtil.buildUpdateCellsRequest(sheetId, cityRange, cityAppend));
                for (ShopItem shopItem : entry.getValue().getList().values()) {
                    String range = getExcelColumnName(columnIndex) + (baseRow + (index-1) * step);
                    List<List<Object>> toAppend; // ประกาศไว้ก่อน
                    if (shopItem.getItem() instanceof Equipment) {
                        if (shopItem.getItem() == null) continue;
                        Equipment equipment = (Equipment) shopItem.getItem();
                        toAppend = new ArrayList<>(List.of(
                                List.of(itemIndex),
                                List.of(equipment.getName()),
                                List.of(equipment.getLore()),
                                List.of(equipment.getStatusDescription() + "\n" + shopItem.getItem().getDescription()),
                                List.of("Price : "+shopItem.getPrice_in_copper()),
                                List.of("Stocks : " + shopItem.getStock()),
                                List.of(equipment.getEquipmentType().writeAsString()),
                                List.of(equipment.getWeaponType().writeAsString())
                        ));
                    } else {
                        if (shopItem.getItem() == null) continue;
                        toAppend = new ArrayList<>(List.of(
                                List.of(itemIndex),
                                List.of(shopItem.getItem().getName()),
                                List.of(shopItem.getItem().getLore()),
                                List.of(shopItem.getItem().getStatusDescription() + "\n" + shopItem.getItem().getDescription()),
                                List.of("Price : "+shopItem.getPrice_in_copper()),
                                List.of("Stock : " + shopItem.getStock()),
                                List.of(shopItem.getItem().getItemType().writeAsString())
                        ));
                    }
                    itemIndex++;
                    columnIndex++;
                    requests.add(GoogleSheetsUtil.buildUpdateCellsRequest(sheetId, range, toAppend));
                }
                index++;
                sheetsUtil.takeRequests(requests);
            }
            sheetsUtil.requestSet();
            String sessionId = UUID.randomUUID().toString();  // สร้าง id ใหม่สำหรับ session นี้
            long startTime = System.currentTimeMillis();
            System.out.println("Start processRequest: " + startTime + " Session: " + sessionId);

            sheetsUtil.processRequest(GoogleSheetsUtil.viewerSheetId);

            sheetsUtil.requestClear();

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("End processRequest: took " + duration + " ms Session: " + sessionId);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
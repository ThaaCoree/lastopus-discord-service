package app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.api.services.sheets.v4.model.Request;
import controller.CombatFlow;
import com.fasterxml.jackson.core.type.TypeReference;
import app.servicemodel.SaveRequest;
import model.entity.*;
import model.entity.items.*;
import model.entity.units.Monster;
import model.entity.units.Summon;
import model.entity.units.Unit;
import model.type.CardType;
import model.type.UnitType;
import util.GoogleSheetsUtil;
import util.JsonUtils;
import util.StatTranslateUtil;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class Database {

    private Map<String, Item> allTypeItemMap = new LinkedHashMap<>();
    private Map<String, Item> allNormalItemMap;
    private Map<String, Equipment> allEquipmentMap;
    private Map<String, Consumable> allConsumableMap;
    private Map<String, Dream> allDreamItem;
    private Map<String, PassiveNode> allDream = new LinkedHashMap<>();
    private Map<String, Rune> allRuneMap;
    private Map<String, Unit> allPlayerMap;
    private Map<String, Unit> allNPCMap;
    private Map<String, Monster> allMonsterMap;
    private Map<String, Conditions> allConditionMap ;
    private Map<String, Card> allCardMap;
    private Map<Integer, PassiveNode> allPassiveMap;
    private Map<String, Shop> allShop;
    private Map<String, Unit> allUnit = new LinkedHashMap<>();
    private Map<String, Summon> allSummon = new LinkedHashMap<>();
    private CombatFlow combatFlow = JsonUtils.loadFromFile("/json/combatFlow.json", new TypeReference<CombatFlow>() {});

    public Database() {
        loadMongo();
//        loadItemFromJson();
        mapEverything();
        updateEverything();
        initCounterAllUnit();
        if (combatFlow != null) {
            combatFlow.registerDatabase(this);
            combatFlow.initCombatFlow();
        }
    }

    public void createPlayer(String name) {
        Unit player = new Unit(name, UnitType.PLAYER);
        allPlayerMap.put(name, player);
    }

    public void createNPC(String name) {
        Unit npc = new Unit(name, UnitType.NPC);
        allNPCMap.put(name, npc);
    }
    
    public void saveJson() {
        translateEverything();
        updateEverything();
        if (allPlayerMap != null)
            JsonUtils.saveToFile(allPlayerMap, "src/main/resources/json/players.json");
        if (allMonsterMap != null)
            JsonUtils.saveToFile(allMonsterMap, "src/main/resources/json/monsters.json");
        if (allNPCMap != null)
            JsonUtils.saveToFile(allNPCMap, "src/main/resources/json/npcs.json");

            JsonUtils.saveToFile(allPassiveMap, "src/main/resources/json/passives.json");
        if (allNormalItemMap != null)
            JsonUtils.saveToFile(allNormalItemMap, "src/main/resources/json/items.json");
        if (allConditionMap != null)
            JsonUtils.saveToFile(allConditionMap, "src/main/resources/json/conditions.json");
        if (allCardMap != null)
            JsonUtils.saveToFile(allCardMap, "src/main/resources/json/cards.json");
        if (allShop != null)
            JsonUtils.saveToFile(allShop, "src/main/resources/json/shops.json");
        if (combatFlow != null)
            JsonUtils.saveToFile(combatFlow, "src/main/resources/json/combatFlow.json");

        JsonUtils.saveToFile(allEquipmentMap, "src/main/resources/json/equipments.json");
        JsonUtils.saveToFile(allDreamItem, "src/main/resources/json/dreams.json");
        JsonUtils.saveToFile(allConsumableMap, "src/main/resources/json/consumables.json");
        JsonUtils.saveToFile(allRuneMap, "src/main/resources/json/runes.json");

        saveMongo();
    }

    public void loadItemFromJson() {
        allMonsterMap = JsonUtils.loadFromFile("/json/monsters.json", new TypeReference<Map<String, Monster>>() {});
        allCardMap = JsonUtils.loadFromFile("/json/cards.json", new TypeReference<Map<String, Card>>() {});
        allConditionMap = JsonUtils.loadFromFile("/json/conditions.json", new TypeReference<Map<String, Conditions>>() {});
        allNormalItemMap = JsonUtils.loadFromFile("/json/items.json", new TypeReference<Map<String, Item>>() {});
        allConsumableMap = JsonUtils.loadFromFile("/json/consumables.json", new TypeReference<Map<String, Consumable>>() {});
        allDreamItem = JsonUtils.loadFromFile("/json/dreams.json", new TypeReference<Map<String, Dream>>() {});
        allEquipmentMap = JsonUtils.loadFromFile("/json/equipments.json", new TypeReference<Map<String, Equipment>>() {});
        allRuneMap = JsonUtils.loadFromFile("/json/runes.json", new TypeReference<Map<String, Rune>>() {});
    }

    public void loadMongo() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request_player = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8081/load_all"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request_player, HttpResponse.BodyHandlers.ofString());
            System.out.println("request sent");

            String body = response.body();
            ObjectMapper mapper = new ObjectMapper();

            SaveRequest res = mapper.readValue(body, SaveRequest.class);
            allNormalItemMap = res.getAllItemMap();
            allCardMap = res.getAllCardMap();
            allConditionMap = res.getAllConditionMap();
            allConsumableMap = res.getAllConsumableMap();
            allDreamItem = res.getAllDreamItem();
            allEquipmentMap = res.getAllEquipmentMap();
            allMonsterMap = res.getAllMonsterMap();
            allNPCMap = res.getAllNPCMap();
            allPassiveMap = res.getAllPassiveMap();
            allPlayerMap = res.getAllPlayerMap();
            allRuneMap = res.getAllRuneMap();
            allShop = res.getAllShop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveMongo() {
        translateEverything();
        updateEverything();

        try {
            HttpClient client = HttpClient.newHttpClient();


            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            List<String> to_save = new ArrayList<>();
            to_save.add("player");
            to_save.add("npc");
            to_save.add("monster");
            to_save.add("item");
            to_save.add("equipment");
            to_save.add("consumable");
            to_save.add("dream");
            to_save.add("rune");
            to_save.add("card");
            to_save.add("condition");
            to_save.add("shop");
//            to_save.add("summon");
            to_save.add("passive");

            for (String filler : to_save) {
                String json = "";
                if (filler.equals("player")) {
                    if (allPlayerMap == null) continue;
                    json = mapper.writeValueAsString(allPlayerMap);
                }
                if (filler.equals("npc")) {
                    if (allNPCMap == null) continue;
                    json = mapper.writeValueAsString(allNPCMap);
                }
                if (filler.equals("monster")) {
                    if (allMonsterMap == null) continue;
                    json = mapper.writeValueAsString(allMonsterMap);
                }
                if (filler.equals("item")) {
                    if (allNormalItemMap == null) continue;
                    json = mapper.writeValueAsString(allNormalItemMap);
                }
                if (filler.equals("equipment")) {
                    if (allEquipmentMap == null) continue;
                    json = mapper.writeValueAsString(allEquipmentMap);
                }
                if (filler.equals("consumable")) {
                    if (allConsumableMap == null) continue;
                    json = mapper.writeValueAsString(allConsumableMap);
                }
                if (filler.equals("dream")) {
                    if (allDream == null) continue;
                    json = mapper.writeValueAsString(allDreamItem);
                }
                if (filler.equals("rune")) {
                    if (allRuneMap == null) continue;
                    json = mapper.writeValueAsString(allRuneMap);
                }
                if (filler.equals("card")) {
                    if (allCardMap == null) continue;
                    json = mapper.writeValueAsString(allCardMap);
                }
                if (filler.equals("condition")) {
                    if (allConditionMap == null) continue;
                    json = mapper.writeValueAsString(allConditionMap);
                }
                if (filler.equals("summon")) {
                    if (allSummon == null) continue;
//                    json = mapper.writeValueAsString(allSummon);
                }
                if (filler.equals("shop")) {
                    if (allShop == null) continue;
                    json = mapper.writeValueAsString(allShop);
                }
                if (filler.equals("passive")) {
                    if (allPassiveMap == null) continue;
                    json = mapper.writeValueAsString(allPassiveMap);
                }


                HttpRequest request_player = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8081/save_" + filler))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();
                client.send(request_player, HttpResponse.BodyHandlers.ofString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateEverything() {
        updateUnitObjects();
        updateShopObjects();
    }

    public void mapEverything() {
        if (allPlayerMap != null)
        allUnit.putAll(allPlayerMap);
        if (allNPCMap != null)
        allUnit.putAll(allNPCMap);
        if (allMonsterMap != null)
        allUnit.putAll(allMonsterMap);

        if (allNormalItemMap != null) allTypeItemMap.putAll(allNormalItemMap);
        if (allConsumableMap != null) allTypeItemMap.putAll(allConsumableMap);
        if (allEquipmentMap != null) allTypeItemMap.putAll(allEquipmentMap);
        if (allRuneMap != null) allTypeItemMap.putAll(allRuneMap);
        if (allDreamItem != null) allTypeItemMap.putAll(allDreamItem);


        for (Unit unit : allUnit.values()) {
            for (Summon summon : unit.getSummons().values()) {
                allSummon.put(summon.getName(), summon);
            }
        }
        if (allDreamItem != null) {
            for (Item item : allDreamItem.values()) {
                if (item instanceof Dream) {
                    Dream dream = (Dream) item;
                    PassiveNode node = new PassiveNode();
                    node.setName(dream.getName());
                    node.setModifiers(dream.getModifiers());
                    node.setStatusDescription(StatTranslateUtil.translateStatusDesc(dream.getModifiers(), null));
                    node.setDescription(dream.getDescription());
                    node.setLore(dream.getLore());
                    node.setNodeType(dream.getNodeType());
                    node.setDream(true);
                    allDream.put(dream.getName(), node);
                }
            }
        }
        allUnit.putAll(allSummon);
    }

    public void updateUnitObjects() {
        if (allNormalItemMap == null) {
            System.out.println("allItem is null");
            return;
        }
        if (allCardMap == null) {
            System.out.println("allCard is null");
            return;
        }
        if (allConditionMap == null) {
            System.out.println("allCondition is null");
            return;
        }
        for (Unit unit : allUnit.values()) {
            for (Map.Entry<Integer, Item> entry : unit.getInventoryItems().entrySet()) {
                if (entry.getValue() instanceof Rune rune) {
                    if (!rune.isUnique_rune()) continue;
                }

                String name = entry.getValue().getName();
                Item newItem = allNormalItemMap.get(name);
                Equipment newEquipment = allEquipmentMap.get(name);
                Dream newDream = allDreamItem.get(name);
                Consumable newConsumable = allConsumableMap.get(name);
                Rune newRune = new Rune();
                if (allRuneMap != null) {
                    newRune = allRuneMap.get(name);
                }

                if (newItem != null) {
                    entry.setValue(newItem);
                }
                if (newEquipment != null) {
                    entry.setValue(newEquipment);
                }
                if (newDream != null) {
                    entry.setValue(newDream);
                }
                if (newConsumable != null) {
                    entry.setValue(newConsumable);
                }
                if (newRune != null) {
                    entry.setValue(newRune);
                }
            }

            for (Map.Entry<Integer, Item> entry : unit.getBackpackItems().entrySet()) {
                if (entry.getValue() instanceof Rune rune) {
                    if (!rune.isUnique_rune()) continue;
                }

                String name = entry.getValue().getName();
                Item newItem = allNormalItemMap.get(name);
                Equipment newEquipment = allEquipmentMap.get(name);
                Dream newDream = allDreamItem.get(name);
                Consumable newConsumable = allConsumableMap.get(name);
                Rune newRune = allRuneMap.get(name);

                if (newItem != null) {
                    entry.setValue(newItem);
                }
                if (newEquipment != null) {
                    entry.setValue(newEquipment);
                }
                if (newDream != null) {
                    entry.setValue(newDream);
                }
                if (newConsumable != null) {
                    entry.setValue(newConsumable);
                }
                if (newRune != null) {
                    entry.setValue(newRune);
                }
            }

            for (Map.Entry<Integer, PassiveNode> entry : unit.getAllocatedPassives().entrySet()) {
                int id = entry.getValue().getId();
                PassiveNode newObject = allPassiveMap.get(id);

                if (newObject != null) {
                    if (entry.getValue().isDream()) {
                        double x = newObject.getX();
                        double y = newObject.getY();
                        List<Integer> connectedNodes = newObject.getConnectedNodes();
                        PassiveNode dream = allDream.get(entry.getValue().getName());
                        dream.setX(x);
                        dream.setY(y);
                        dream.setConnectedNodes(connectedNodes);
                    } else {
                        entry.setValue(newObject);
                    }
                }
            }
            for (Map.Entry<CardType, Card> entry : unit.getCard().entrySet()) {
                String name = entry.getValue().getName();
                Card newObject = allCardMap.get(name);

                if (newObject != null) {
                    entry.setValue(newObject);
                }
            }
            for (Map.Entry<Integer, EquipmentSlot> entry : unit.getEquipmentSlots().entrySet()) {
                if (entry.getValue().getEquipment() == null) continue;
                String name = entry.getValue().getEquipment().getName();
                Equipment newObject = allEquipmentMap.get(name);

                if (newObject != null) {
                    entry.getValue().setEquipment(newObject);
                }
            }
            for (Map.Entry<Integer, ConditionInstance> entry : unit.getConditionInstances().entrySet()) {
                String name = entry.getValue().getCondition().getName();
                Conditions newObject = allConditionMap.get(name);

                if (newObject != null) {
                    entry.getValue().setCondition(newObject);
                }

                Unit source = allUnit.get(entry.getValue().getSourceName());
                if (source != null) {
                    entry.getValue().setSource(source);
                }
            }
            unit.calculateEverything();
        }
    }

    public void updateShopObjects() {
        if (allNormalItemMap == null) {
            System.out.println("allItem is null");
            return;
        }
        if (allShop == null) {
            System.out.println("allShop is null");
            return;
        }
        for (Shop shop : allShop.values()) {
            for (Map.Entry<Integer, ShopItem> shopItemEntry : shop.getList().entrySet()) {
                if (shopItemEntry.getValue().getItem() == null) continue;
                String item_name = shopItemEntry.getValue().getItem().getName();
                Item newItem = allNormalItemMap.get(item_name);
                Equipment newEquipment = allEquipmentMap.get(item_name);
                Dream newDream = allDreamItem.get(item_name);
                Consumable newConsumable = allConsumableMap.get(item_name);
                Rune newRune = allRuneMap.get(item_name);

                if (newItem != null) {
                    shopItemEntry.getValue().setItem(newItem);
                }
                if (newEquipment != null) {
                    shopItemEntry.getValue().setItem(newEquipment);
                }
                if (newDream != null) {
                    shopItemEntry.getValue().setItem(newDream);
                }
                if (newConsumable != null) {
                    shopItemEntry.getValue().setItem(newConsumable);
                }
                if (newRune != null) {
                    shopItemEntry.getValue().setItem(newRune);
                }
            }
        }
    }

    public void translateEverything() {
        for (PassiveNode node : allPassiveMap.values()) {
            StatTranslateUtil.translatePassiveNodeStatusDesc(node);
        }
        if (allEquipmentMap != null) for (Equipment item : allEquipmentMap.values()) {
            item.setStatusDescription(StatTranslateUtil.translateStatusDesc(item.getModifiers(), item.getSkills()));
        }
        if (allConsumableMap != null) for (Consumable item : allConsumableMap.values()) {
            StatTranslateUtil.translateConsumableStatusDesc(item);
        }
        if (allRuneMap != null) for (Rune item : getAllRuneMap().values()) {
                item.setStatusDescription(StatTranslateUtil.translateStatusDesc(item.getModifiers(), item.getSkills()));
            }
        if (allDreamItem != null)
            for (Dream item : allDreamItem.values()) {
                item.setStatusDescription(StatTranslateUtil.translateStatusDesc(item.getModifiers(), null));
                item.addStatusDescription("\n");
                item.addStatusDescription("Node : " + item.getNodeType().writeAsString());
            }
        if (allConditionMap != null) for (Conditions condition : allConditionMap.values()) {
            condition.setStatusDescription(StatTranslateUtil.translateStatusDesc(condition.getModifiers(),null));
        }
        if (allCardMap != null)
            for (Card card : allCardMap.values()) {
                for (CardType type : CardType.values()) {
                    card.getStatusDescription().put(type, StatTranslateUtil.translateStatusDesc(card.getModifiers().get(type),null));
                }
            }
    }

    public void initCounterAllUnit() {
        for (Unit unit : allUnit.values()) {
            unit.initCounter();
        }

        for (Summon summon : allSummon.values()) {
            summon.initCounter();
        }
    }

    public Unit findUnit(String key) {
        for (Map.Entry<String, Unit> entry : allUnit.entrySet()) {
            if (entry.getKey().equals(key)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public Unit findUnit(String key, UnitType type) {
        for (Map.Entry<String, Unit> entry : allUnit.entrySet()) {
            if (entry.getKey().equals(key) && entry.getValue().getUnitType().equals(type)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public List<Request> writeItemToSheet() {
        List<Request> requests = new ArrayList<>();
            try {
                GoogleSheetsUtil sheetsUtil = new GoogleSheetsUtil();
                // หา sheetId
                Integer sheetId = sheetsUtil.getSheetIdByName(GoogleSheetsUtil.databaseSheetId, "AllItemList");
                if (sheetId == null) {
                    return new ArrayList<>();
                }

                writeAllItem(requests, sheetId);
            } catch (Exception ex) {
                ex.printStackTrace(); // หรือจะ throw ต่อก็ได้
            }

        return requests;
    }

    public void writeAllItem(List<Request> requests, int sheetId) throws Exception {
        String range = "B2";
        List<List<Object>> toAppend = new ArrayList<>();

        for (Item item : allNormalItemMap.values()) {
            List<Object> row = new ArrayList<>();

            row.add(item.getName());
            row.add(item.getItemType().writeAsString());
            row.add(item.getLore());
            row.add(item.getStatusDescription()+"\n"+item.getDescription());
            row.add(item.getPrice());
            if (item instanceof Equipment) {
                Equipment equipment = (Equipment) item;
                row.add(equipment.getEquipmentType().writeAsString());
                row.add(equipment.getWeaponType().writeAsString());
            } else {
                row.add("");
                row.add("");
            }

            toAppend.add(row);
        }

        requests.add(GoogleSheetsUtil.buildUpdateCellsRequest(sheetId, range, toAppend));
    }

    public List<Request> writeCardToSheet() {
        List<Request> requests = new ArrayList<>();
        try {
            GoogleSheetsUtil sheetsUtil = new GoogleSheetsUtil();
            // หา sheetId
            Integer sheetId = sheetsUtil.getSheetIdByName(GoogleSheetsUtil.databaseSheetId, "CardList");
            if (sheetId == null) {
                return new ArrayList<>();
            }

            writeAllCard(requests, sheetId);
        } catch (Exception ex) {
            ex.printStackTrace(); // หรือจะ throw ต่อก็ได้
        }

        return requests;
    }

    public void writeAllCard(List<Request> requests, int sheetId) throws Exception {
        String range = "B2";
        List<List<Object>> toAppend = new ArrayList<>();

        for (Card card : allCardMap.values()) {
            List<Object> row = new ArrayList<>();

            row.add(card.getName());
            row.add(card.getAbilityName().get(CardType.PRIMARY));
            row.add(card.getStatusDescription().get(CardType.PRIMARY)+card.getDescription().get(CardType.PRIMARY));
            row.add(card.getAbilityName().get(CardType.SECONDARY));
            row.add(card.getStatusDescription().get(CardType.SECONDARY)+card.getDescription().get(CardType.SECONDARY));

            toAppend.add(row);
        }

        requests.add(GoogleSheetsUtil.buildUpdateCellsRequest(sheetId, range, toAppend));
    }

    public List<Request> writeBuffToSheet() {
        List<Request> requests = new ArrayList<>();
        try {
            GoogleSheetsUtil sheetsUtil = new GoogleSheetsUtil();
            // หา sheetId
            Integer sheetId = sheetsUtil.getSheetIdByName(GoogleSheetsUtil.databaseSheetId, "BuffList");
            if (sheetId == null) {
                return new ArrayList<>();
            }

            writeAllBuff(requests, sheetId);
        } catch (Exception ex) {
            ex.printStackTrace(); // หรือจะ throw ต่อก็ได้
        }

        return requests;
    }

    public void writeAllBuff(List<Request> requests, int sheetId) throws Exception {
        String range = "B2";
        List<List<Object>> toAppend = new ArrayList<>();

        for (Conditions conditions : allConditionMap.values()) {
            List<Object> row = new ArrayList<>();

            row.add(conditions.getName());
            row.add(conditions.getConditionType().writeAsString()+" / "+conditions.getConditionTierType().writeAsString());
            row.add(conditions.getStatusDescription()+conditions.getDescription());

            toAppend.add(row);
        }

        requests.add(GoogleSheetsUtil.buildUpdateCellsRequest(sheetId, range, toAppend));
    }

    public Map<String, Unit> getAllUnit() {
        return allUnit;
    }

    public Map<String, Summon> getAllSummon() {
        return allSummon;
    }

    public Map<String, Equipment> getAllEquipmentMap() {
        return allEquipmentMap;
    }

    public CombatFlow getCombatController() {
        return combatFlow;
    }

    public Map<String, Unit> getAllPlayerMap() {
        return allPlayerMap;
    }

    public Map<String, Monster> getAllMonsterMap() {
        return allMonsterMap;
    }

    public Map<String, Unit> getAllNPCMap() {
        return allNPCMap;
    }

    public Map<String, Conditions> getAllConditionMap() {
        return allConditionMap;
    }

    public Map<String, Consumable> getAllConsumableMap() {
        return allConsumableMap;
    }

    public Map<String, Card> getAllCardMap() {
        return allCardMap;
    }

    public Map<String, Shop> getAllShop() {
        return allShop;
    }

    public Map<String, Rune> getAllRuneMap() {
        return allRuneMap;
    }

    public Map<Integer, PassiveNode> getAllPassiveMap() {
        return allPassiveMap;
    }

    public Map<String, PassiveNode> getAllDream() {
        return allDream;
    }

    public Map<String, Item> getAllTypeItemMap() {
        return allTypeItemMap;
    }

    public Map<String, Item> getAllNormalItemMap() {
        return allNormalItemMap;
    }

    public Map<String, Dream> getAllDreamItem() {
        return allDreamItem;
    }
}

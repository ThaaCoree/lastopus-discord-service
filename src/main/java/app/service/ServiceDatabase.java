package app.service;

import app.servicemodel.SaveRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.entity.*;
import model.entity.items.*;
import model.entity.units.Monster;
import model.entity.units.Summon;
import model.entity.units.Unit;
import model.type.CardType;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import util.StatTranslateUtil;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ServiceDatabase {

    private MongoTemplate mongoTemplate;

    private Map<String, Item> allItemMap;
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

    public ServiceDatabase(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        loadMongo();
        mapAllUnit();
        updateEverything();
        initCounterAllUnit();
    }

    public Equipment findEquipment(String name) {
        if (allEquipmentMap.containsKey(name)) {
            return allEquipmentMap.get(name);
        }
        return null;
    }

    public void initCounterAllUnit() {
        for (Unit unit : allUnit.values()) {
            unit.initCounter();
        }

        for (Summon summon : allSummon.values()) {
            summon.initCounter();
        }
    }

    public void updateEverything() {
        updateUnitObjects();
        updateShopObjects();
    }

    public void updateShopObjects() {
        if (allItemMap == null) {
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
                Item newItem = allItemMap.get(item_name);
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

    public void updateUnitObjects() {
        if (allItemMap == null) {
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
                Item newItem = allItemMap.get(name);
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
                Item newItem = allItemMap.get(name);
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



    public void mapAllUnit() {
        if (allPlayerMap != null)
            allUnit.putAll(allPlayerMap);
        if (allNPCMap != null)
            allUnit.putAll(allNPCMap);
        if (allMonsterMap != null)
            allUnit.putAll(allMonsterMap);

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

    public Unit findUnit(String key) {
        for (Map.Entry<String, Unit> entry : allUnit.entrySet()) {
            if (entry.getKey().equals(key)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public void loadMongo() {
        try {

            SaveRequest res = load_all();
            allItemMap = res.getAllItemMap();
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

            System.out.println(allPlayerMap);
            System.out.println("--------------------------------------------------------");
            System.out.println(allItemMap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SaveRequest load_all() {
        SaveRequest saveRequest = new SaveRequest();
        Map<String, Item> allItem = mongoTemplate.findOne(new Query(), Map.class, "items");
        Map<String, Card> allCards = mongoTemplate.findOne(new Query(), Map.class, "cards");
        Map<String, Conditions> allConditions = mongoTemplate.findOne(new Query(), Map.class, "conditions");
        Map<String, Consumable> allConsumables = mongoTemplate.findOne(new Query(), Map.class, "consumables");
        Map<String, Dream> allDreams = mongoTemplate.findOne(new Query(), Map.class, "dreams");
        Map<String, Equipment> allEquipments = mongoTemplate.findOne(new Query(), Map.class, "equipments");
        Map<String, Monster> allMonsters = mongoTemplate.findOne(new Query(), Map.class, "monsters");
        Map<String, Unit> allNPCs = mongoTemplate.findOne(new Query(), Map.class, "npcs");
        Map<String, PassiveNode> allPassives = mongoTemplate.findOne(new Query(), Map.class, "passives");
        Map<String, Unit> allPlayers = mongoTemplate.findOne(new Query(), Map.class, "players");
        Map<String, Rune> allRunes = mongoTemplate.findOne(new Query(), Map.class, "runes");
        Map<String, Shop> allShops = mongoTemplate.findOne(new Query(), Map.class, "shops");

        allItem.remove("_id");
        allPlayers.remove("_id");
        allCards.remove("_id");
        allConditions.remove("_id");
        allConsumables.remove("_id");
        allDreams.remove("_id");
        allEquipments.remove("_id");
        allNPCs.remove("_id");
        allMonsters.remove("_id");
        allPassives.remove("_id");
        allRunes.remove("_id");
        allShops.remove("_id");

        Map<Integer, PassiveNode> allPassivesToPut = new LinkedHashMap<>();
        allPassives.forEach((integer, node) -> {
            allPassivesToPut.put(Integer.parseInt(integer), node);
        });

        saveRequest.setAllItemMap(allItem);
        saveRequest.setAllCardMap(allCards);
        saveRequest.setAllConditionMap(allConditions);
        saveRequest.setAllConsumableMap(allConsumables);
        saveRequest.setAllDreamItem(allDreams);
        saveRequest.setAllEquipmentMap(allEquipments);
        saveRequest.setAllMonsterMap(allMonsters);
        saveRequest.setAllNPCMap(allNPCs);
        saveRequest.setAllPassiveMap(allPassivesToPut);
        saveRequest.setAllPlayerMap(allPlayers);
        saveRequest.setAllRuneMap(allRunes);
        saveRequest.setAllShop(allShops);
        return saveRequest;
    }

    public String save_player(@RequestBody Map<String, Unit> allPlayerMap) {

        Unit unit = new Unit();
        allPlayerMap.put("_id", unit);
        mongoTemplate.save(allPlayerMap, "players");

        System.out.println("saved player");
        return "saved";
    }

    public String save_npc(@RequestBody Map<String, Unit> map) {

        Unit unit = new Unit();
        map.put("_id", unit);
        mongoTemplate.save(map, "npcs");

        System.out.println("saved npc");
        return "saved";
    }

    public String save_monster(@RequestBody Map<String, Monster> map) {

        Monster monster = new Monster();
        map.put("_id", monster);
        mongoTemplate.save(map, "monsters");

        System.out.println("saved monster");
        return "saved";
    }

    public String save_item(@RequestBody Map<String, Item> map) {

        Item item = new Item();
        map.put("_id", item);
        mongoTemplate.save(map, "items");

        System.out.println("saved item");
        return "saved";
    }

    public String save_equip(@RequestBody Map<String, Equipment> map) {

        map = map.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().replace(".", "_"),
                        Map.Entry::getValue
                ));

        Equipment equipment = new Equipment();
        map.put("_id", equipment);
        mongoTemplate.save(map, "equipments");

        System.out.println("saved equipment");
        return "saved";
    }

    public String save_consumable(@RequestBody Map<String, Consumable> map) {

        Consumable item = new Consumable();
        map.put("_id", item);
        mongoTemplate.save(map, "consumables");

        System.out.println("saved consumable");
        return "saved";
    }

    public String save_dream(@RequestBody Map<String, Dream> map) {

        Dream item = new Dream();
        map.put("_id", item);
        mongoTemplate.save(map, "dreams");

        System.out.println("saved dream");
        return "saved";
    }

    public String save_rune(@RequestBody Map<String, Rune> map) {

        Rune item = new Rune();
        map.put("_id", item);
        mongoTemplate.save(map, "runes");

        System.out.println("saved rune");
        return "saved";
    }

    public String save_card(@RequestBody Map<String, Card> map) {

        Card item = new Card();
        map.put("_id", item);
        mongoTemplate.save(map, "cards");

        System.out.println("saved card");
        return "saved";
    }

    public String save_condition(@RequestBody Map<String, Conditions> map) {

        Conditions item = new Conditions();
        map.put("_id", item);
        mongoTemplate.save(map, "conditions");

        System.out.println("saved condition");
        return "saved";
    }

    public String save_shop(@RequestBody Map<String, Shop> map) {

        Shop item = new Shop();
        map.put("_id", item);
        mongoTemplate.save(map, "shops");

        System.out.println("saved shop");
        return "saved";
    }

    public String save_summon(@RequestBody Map<String, Summon> map) {

        Summon item = new Summon();
        map.put("_id", item);
        mongoTemplate.save(map, "summons");

        System.out.println("saved summon");
        return "saved";
    }

    public String save_passive(@RequestBody Map<Integer, PassiveNode> map) {

        Map<String, PassiveNode> re_map = new LinkedHashMap<>();
        PassiveNode item = new PassiveNode();
        re_map.put("_id", item);

        map.forEach((key, node) -> {
            re_map.put(Integer.toString(key), node);
        });
        mongoTemplate.save(re_map, "passives");

        System.out.println("saved passive");
        return "saved";
    }
}

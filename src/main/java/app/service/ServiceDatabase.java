package app.service;

import app.servicemodel.SaveRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.entity.Card;
import model.entity.Conditions;
import model.entity.PassiveNode;
import model.entity.Shop;
import model.entity.items.*;
import model.entity.units.Monster;
import model.entity.units.Summon;
import model.entity.units.Unit;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
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
        // ...
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

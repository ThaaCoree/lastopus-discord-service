package app.servicecontroller;

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
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class MongoController {

    private MongoTemplate mongoTemplate;

    public MongoController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostMapping("/load_all")
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


        List<Map<String, ?>> maps = new ArrayList<>(Arrays.asList(
                allItem,
                allPlayers,
                allCards,
                allConditions,
                allConsumables,
                allDreams,
                allEquipments,
                allNPCs,
                allMonsters,
                allPassives,
                allRunes,
                allShops
        ));

        for (Map<String, ?> map : maps) {
            if (map != null) {
                map.remove("_id");
            }
        }

        allEquipments = allEquipments.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().replace("_", "."),
                        Map.Entry::getValue
                ));

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

    @PostMapping("/save_player")
    public String save_player(@RequestBody Map<String, Unit> allPlayerMap) {

        // ดึง document เดิมมาก่อน
        org.bson.Document existing = mongoTemplate.findOne(
                new Query(), org.bson.Document.class, "players"
        );

        ObjectId id = existing != null
                ? existing.getObjectId("_id")
                : new ObjectId();

        // สร้าง document ใหม่จาก map
        org.bson.Document doc = new org.bson.Document();
        doc.put("_id", id);

        ObjectMapper mapper = new ObjectMapper();
        allPlayerMap.forEach((key, unit) ->
                doc.put(key, mapper.convertValue(unit, org.bson.Document.class))
        );

        // upsert ทับ document เดิม
        mongoTemplate.getCollection("players").replaceOne(
                com.mongodb.client.model.Filters.eq("_id", id),
                doc,
                new com.mongodb.client.model.ReplaceOptions().upsert(true)
        );

        System.out.println("saved player");
        return "saved";
    }

    @PostMapping("/save_npc")
    public String save_npc(@RequestBody Map<String, Unit> map) {

        mongoTemplate.dropCollection("npcs");
        mongoTemplate.save(map, "npcs");

        System.out.println("saved npc");
        return "saved";
    }

    @PostMapping("/save_monster")
    public String save_monster(@RequestBody Map<String, Monster> map) {

        mongoTemplate.dropCollection("monsters");
        mongoTemplate.save(map, "monsters");

        System.out.println("saved monster");
        return "saved";
    }

    @PostMapping("/save_item")
    public String save_item(@RequestBody Map<String, Item> map) {

        mongoTemplate.dropCollection("items");
        mongoTemplate.save(map, "items");

        System.out.println("saved item");
        return "saved";
    }

    @PostMapping("/save_equipment")
    public String save_equip(@RequestBody Map<String, Equipment> map) {

        map = map.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().replace(".", "_"),
                        Map.Entry::getValue
                ));

        mongoTemplate.dropCollection("equipments");
        mongoTemplate.save(map, "equipments");

        System.out.println("saved equipment");
        return "saved";
    }

    @PostMapping("/save_consumable")
    public String save_consumable(@RequestBody Map<String, Consumable> map) {

        mongoTemplate.dropCollection("consumables");
        mongoTemplate.save(map, "consumables");

        System.out.println("saved consumable");
        return "saved";
    }

    @PostMapping("/save_dream")
    public String save_dream(@RequestBody Map<String, Dream> map) {

        mongoTemplate.dropCollection("dreams");
        mongoTemplate.save(map, "dreams");

        System.out.println("saved dream");
        return "saved";
    }

    @PostMapping("/save_rune")
    public String save_rune(@RequestBody Map<String, Rune> map) {

        mongoTemplate.dropCollection("runes");
        mongoTemplate.save(map, "runes");

        System.out.println("saved rune");
        return "saved";
    }

    @PostMapping("/save_card")
    public String save_card(@RequestBody Map<String, Card> map) {

        mongoTemplate.dropCollection("cards");
        mongoTemplate.save(map, "cards");

        System.out.println("saved card");
        return "saved";
    }

    @PostMapping("/save_condition")
    public String save_condition(@RequestBody Map<String, Conditions> map) {

        mongoTemplate.dropCollection("conditions");
        mongoTemplate.save(map, "conditions");

        System.out.println("saved condition");
        return "saved";
    }

    @PostMapping("/save_shop")
    public String save_shop(@RequestBody Map<String, Shop> map) {

        mongoTemplate.dropCollection("shops");
        mongoTemplate.save(map, "shops");

        System.out.println("saved shop");
        return "saved";
    }

    @PostMapping("/save_summon")
    public String save_summon(@RequestBody Map<String, Summon> map) {

        mongoTemplate.dropCollection("summons");
        mongoTemplate.save(map, "summons");

        System.out.println("saved summon");
        return "saved";
    }

    @PostMapping("/save_passive")
    public String save_passive(@RequestBody Map<Integer, PassiveNode> map) {

        mongoTemplate.dropCollection("passives");
        Map<String, PassiveNode> re_map = new LinkedHashMap<>();

        map.forEach((key, node) -> {
            re_map.put(Integer.toString(key), node);
        });
        mongoTemplate.save(re_map, "passives");

        System.out.println("saved passive");
        return "saved";
    }
}
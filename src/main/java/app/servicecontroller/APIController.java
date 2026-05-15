package app.servicecontroller;

import app.service.ServiceDatabase;
import app.servicemodel.RuneboardRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import model.entity.items.Rune;
import model.entity.units.Unit;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*")
@RestController
public class APIController {

    private MongoTemplate mongoTemplate;
    private ServiceDatabase database;

    public APIController(MongoTemplate mongoTemplate, ServiceDatabase database) {
        this.mongoTemplate = mongoTemplate;
        this.database = database;
    }

    @GetMapping("/get_unit")
    public Map<String, Unit> getAllUnit() {
        database.load_player();

        return database.allPlayerMap;
    }

    @PostMapping("/update_unit")
    public String updateUnit(@RequestBody RuneboardRequest request) {
        database.load_player();
        Unit unit = database.findPlayer(request.player_name);
//        System.out.println("socketed runes : "+request.socketed_runes);
//        System.out.println("unit's socketed runes before set "+unit.getSocketed_runes());
//        System.out.println("rune_inventory : "+request.rune_inventory);
//        System.out.println("unit's rune_inventory before set : "+request.rune_inventory);


//        System.out.println("unit's socketed runes : "+unit.getSocketed_runes());
//        System.out.println("request's socketed runes : "+request.socketed_runes);

        for (Rune socketedRune : request.socketed_runes) {
            if (socketedRune.getId() == null) {
                socketedRune.setId(Rune.generateId(socketedRune.getShapeName()));
            }
        }

        List<Rune> identical_inventory = new ArrayList<>();
        identical_inventory.addAll(request.rune_inventory.values());
        identical_inventory.addAll(unit.getRune_inventory().values());

        for (Rune socketedRune : request.rune_inventory.values()) {
            if (socketedRune == null) continue;
            for (Rune socketed_rune : unit.getRune_inventory().values()) {
                if (socketed_rune == null) continue;
                if (socketedRune.getId() == null) {
                    socketedRune.setId(Rune.generateId(socketedRune.getShapeName()));
                }
                if (socketed_rune.getId() == null) {
                    socketed_rune.setId(Rune.generateId(socketed_rune.getShapeName()));
                }
                if (socketed_rune.getId().equals(socketedRune.getId())) {
                    identical_inventory.remove(socketed_rune);
                }
            }
        }

        int index = 1;
        Map<Integer, Rune> identical_removed = new LinkedHashMap<>();
        for (Rune rune : identical_inventory) {
            identical_removed.put(index++, rune);
        }
//        System.out.println("set's socketed runes : "+set);

        unit.setRune_inventory(identical_removed);
        unit.setSocketed_runes(request.socketed_runes);
//        System.out.println("unit's rune_inventory after set : "+request.rune_inventory);
//        System.out.println("unit's socketed runes after set "+unit.getSocketed_runes());
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            String json = mapper.writeValueAsString(unit);

            database.save_player(json);
            unit.writeToSheet(database.load_credentials());
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Rune rune : request.socketed_runes) {
            identical_removed.put(index++, rune);
        }
        save_rune_data(identical_removed.values());
        return "บันทึกหน้ารูนแล้ว";
    }

    private void save_rune_data(Collection<Rune> runes) {
        BulkOperations bulk = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Rune.class);

        runes.forEach(rune -> {
            if (rune == null) return;
            Query query = Query.query(Criteria.where("_id").is(rune.getId()));
            bulk.replaceOne(query, rune, FindAndReplaceOptions.options().upsert());
        });

        bulk.execute();
    }
}

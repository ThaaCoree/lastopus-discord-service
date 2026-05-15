package app.servicecontroller;

import app.service.ServiceDatabase;
import app.servicemodel.RuneboardRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import model.entity.items.Rune;
import model.entity.units.Unit;
import org.springframework.data.mongodb.core.MongoTemplate;
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
        Map<Integer, Rune> reindexed = new LinkedHashMap<>();
        int i = 0;
        for (Rune rune : request.rune_inventory.values()) {
            reindexed.put(i++, rune);
        }
//        System.out.println("unit's socketed runes : "+unit.getSocketed_runes());
//        System.out.println("request's socketed runes : "+request.socketed_runes);


        Set<Rune> set = new LinkedHashSet<>();
        set.addAll(unit.getSocketed_runes());
        set.addAll(request.socketed_runes);

        for (Rune socketedRune : request.socketed_runes) {
            for (Rune socketed_rune : unit.getSocketed_runes()) {
                if (socketed_rune.equals(socketedRune)) {
                    System.out.println("same rune found : "+socketedRune);
                }
            }
        }

//        System.out.println("set's socketed runes : "+set);

        unit.setRune_inventory(reindexed);
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
        return "บันทึกหน้ารูนแล้ว";
    }
}

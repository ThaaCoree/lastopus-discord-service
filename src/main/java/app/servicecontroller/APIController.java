package app.servicecontroller;

import app.service.ServiceDatabase;
import app.servicemodel.RuneboardRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import model.entity.units.Unit;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
        System.out.println("socketed runes : "+request.socketed_runes);
        System.out.println("unit's socketed runes before set "+unit.getSocketed_runes());
        unit.setRune_inventory(request.rune_inventory);
        unit.setSocketed_runes(request.socketed_runes);
        System.out.println("unit's socketed runes after set "+unit.getSocketed_runes());
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            String json = mapper.writeValueAsString(unit);

            database.save_player(json);
            unit.writeToSheet(database.load_credentials());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "saved unit";
    }
}

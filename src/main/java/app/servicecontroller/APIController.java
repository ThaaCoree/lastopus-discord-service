package app.servicecontroller;

import app.service.ServiceDatabase;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    public String updateUnit(@RequestBody String json) {
        database.save_player(json);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            Unit unit = mapper.readValue(
                    json,
                    new TypeReference<Unit>() {
                    }
            );
            unit.writeToSheet(database.load_credentials());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "saved unit";
    }
}

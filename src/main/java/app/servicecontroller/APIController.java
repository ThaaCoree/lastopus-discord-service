package app.servicecontroller;

import app.service.ServiceDatabase;
import model.entity.units.Unit;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

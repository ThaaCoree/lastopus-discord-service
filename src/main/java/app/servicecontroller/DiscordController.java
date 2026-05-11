package app.servicecontroller;

import app.Database;
import app.service.ServiceDatabase;
import app.servicemodel.PlayerMessage;
import model.entity.units.Unit;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
        String name = "";
        for (String role : playerMessage.roles) {
            if (role.equals("Christ")) {
                name = "Christ";
            }
        }
        Unit unit = database.findUnit(name);
        if (unit != null) {
            return name + " : " + playerMessage.message + " [" + unit.getUnitType().writeAsString() + "]";
        } else {
            return "No Role!";
        }
    }
}
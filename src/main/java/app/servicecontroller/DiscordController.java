package app.servicecontroller;

import app.servicemodel.PlayerMessage;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiscordController {

    private MongoTemplate mongoTemplate;

    public DiscordController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostMapping("/equip")
    public String equip(@RequestBody PlayerMessage playerMessage) {
        String role = "";
        if (playerMessage.roles.equals("Christ")) {
            role = "Christ";
        }
        return role+" : "+playerMessage.message+" ...";
    }
}
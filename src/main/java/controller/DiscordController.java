package main;

import main.service_model.SaveRequest;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class DiscordController {

    private MongoTemplate mongoTemplate;

    public DiscordController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostMapping("/equip")
    public String equip() {

        return "test test";
    }
}
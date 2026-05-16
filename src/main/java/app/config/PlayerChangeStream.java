package app.config;

import app.service.ServiceDatabase;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class PlayerChangeStream {

    private final ServiceDatabase database;
    private final MongoTemplate mongoTemplate;

    public PlayerChangeStream(ServiceDatabase database, MongoTemplate mongoTemplate) {
        this.database = database;
        this.mongoTemplate = mongoTemplate;
        startListening();
    }

    private void startListening() {
        Thread thread = new Thread(() -> {
            mongoTemplate.getCollection("players")
                    .watch()
                    .forEach(change -> {
                        System.out.println("Players changed, reloading cache...");
                        database.load_player();
                    });
        });
        thread.setDaemon(true);
        thread.start();
    }
}
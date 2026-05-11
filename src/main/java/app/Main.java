import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import app.Database;
import ui.MainPane;
import model.type.CurrencyType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Database database = new Database();
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        MainPane root = new MainPane(database);

//        database.createNPC("Darren");
//        database.createNPC("AlFrost");

//        Skill test = SkillFactory.getSkill(Catastrophic_Convergence.NAME, database.getAllPlayerMap().get("Acheros Aki"));
//        test.use(database.getCombatController());

        Scene scene = new Scene(root, 1900, 1000);
        primaryStage.setScene(scene);
        primaryStage.setTitle("The Last Opus");
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Program is closing saving Json...");
            database.saveJson();
            System.out.println("Saved database to Json");
        });

        database.getAllMonsterMap().forEach((key,value) -> {
            for (CurrencyType type : CurrencyType.values()) {
                value.getPurse().put(type,0);
            }
        });

//        for (Unit player : database.getAllPlayerMap().values()) {
//            if (player.getName().equals("Acheros Aki")) {
//                player.addEquipmentSlot(9, EquipmentType.HELMET);
//                player.addEquipmentSlot(10, EquipmentType.ARMOR);
//                player.addEquipmentSlot(11, EquipmentType.BOOTS);
//            }
//        }
//        for (Unit player : database.getAllPlayerMap().values()) {
//            if (player.getName().equals("Acheros Aki")) {
//                player.removeEquipmentSlots(9);
//                player.removeEquipmentSlots(10);
//                player.removeEquipmentSlots(11);
//            }
//        }
//            if (player.getName().equals("Acheros Aki")) {
//                player.setRace(race);
//                player.getRaceManager().setHumanStrongAndWeakStatus(StatusType.DEXTERITY, StatusType.LUCK);
//                player.getRaceManager().calculateRaceBonus();
//            }
//            if (player.getName().equals("Four-Leaf Clover777")) {
//                player.setRace(race);
//                player.getRaceManager().setHumanStrongAndWeakStatus(StatusType.LUCK, StatusType.VITALITY);
//                player.getRaceManager().calculateRaceBonus();
//            }
//            if (player.getName().equals("Voahri")) {
//                player.setRace(race);
//                player.getRaceManager().setHumanStrongAndWeakStatus(StatusType.INTELLIGENCE, StatusType.DEXTERITY);
//                player.getRaceManager().calculateRaceBonus();
//            }
//            if (player.getName().equals("Esther")) {
//                player.setRace(race);
//                player.getRaceManager().setHumanStrongAndWeakStatus(StatusType.STRENGTH, StatusType.INTELLIGENCE);
//                player.getRaceManager().calculateRaceBonus();
//            }
//            if (player.getName().equals("Pumpkin'Slayerman")) {
//                player.setRace(race);
//                player.getRaceManager().setHumanStrongAndWeakStatus(StatusType.AGILITY, StatusType.LUCK);
//                player.getRaceManager().calculateRaceBonus();
//            }
//            if (player.getName().equals("Christ")) {
//                player.setRace(race);
//                player.getRaceManager().setHumanStrongAndWeakStatus(StatusType.DEXTERITY, StatusType.VITALITY);
//                player.getRaceManager().calculateRaceBonus();
//            }
//            if (player.getName().equals("Slafier")) {
//                player.setRace(race);
//                player.getRaceManager().setHumanStrongAndWeakStatus(StatusType.STRENGTH, StatusType.LUCK);
//                player.getRaceManager().calculateRaceBonus();
//            }
//        }


    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        Application.launch();

    }
}


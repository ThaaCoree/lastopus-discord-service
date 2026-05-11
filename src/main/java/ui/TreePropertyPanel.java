package ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.entity.PassiveNode;
import model.entity.items.Dream;
import model.entity.items.Item;
import model.entity.units.Unit;
import util.StatTranslateUtil;

import java.util.Map;

public class TreePropertyPanel extends VBox {

    private Label nameLabel;
    private Label descriptionLabel;
    private Label statusDescriptionLabel;
    private Label loreLabel;
    private Label remainingPointsLabel;
    private ComboBox<String> dream = new ComboBox<>();
    private Button dreamConfirm = new Button("Confirm Dream");
    private TreePane treePane;

    public TreePropertyPanel(Unit unit, TreePane treePane) {
        this.treePane = treePane;

        // กำหนดพื้นหลังสีเข้ม
        this.getStyleClass().add("custom-panel");
        this.getStylesheets().add(getClass().getResource("/styles/theme.css").toExternalForm());

        this.setPadding(new Insets(15));
        this.setPrefWidth(250);
        this.setMaxWidth(300);

        // กำหนด label พื้นฐาน
        nameLabel = new Label("Property of Node:");
        descriptionLabel = new Label("");
        statusDescriptionLabel = new Label("");
        loreLabel = new Label("");
        remainingPointsLabel = new Label("Remaining Point: ");
        updateRemainingPoints(unit);

        nameLabel.setPrefWidth(200);
        nameLabel.setMaxHeight(175);
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(200);
        loreLabel.setWrapText(true);
        loreLabel.setMaxWidth(200);
        dream.setMinWidth(200);
        dreamConfirm.setPrefWidth(150);

        for (Item item : unit.getInventoryItems().values()) {
            if (item instanceof Dream) {
                Dream toPut = (Dream) item;
                dream.getItems().add(toPut.getName());
            }
        }

        dreamConfirm.setOnAction(e-> {
            dreamConvert(unit, dream.getValue());
        });

        this.getChildren().add(remainingPointsLabel);
        this.getChildren().add(nameLabel);
        this.getChildren().add(statusDescriptionLabel);
        this.getChildren().add(descriptionLabel);
        this.getChildren().add(loreLabel);
        this.getChildren().add(dream);
        this.getChildren().add(dreamConfirm);
    }

    public void dreamConvert(Unit unit,String name) {
        PassiveNode node = new PassiveNode();
        String dreamName = null;
        for (Map.Entry<Integer, Item> entry : unit.getInventoryItems().entrySet()) {
            if (entry.getValue().getName().equals(name)) {
                Dream itemCast = (Dream) entry.getValue();

                node.setName(itemCast.getName());
                node.setModifiers(itemCast.getModifiers());
                node.setStatusDescription(StatTranslateUtil.translateStatusDesc(itemCast.getModifiers(),null));
                node.setDescription(itemCast.getDescription());
                node.setLore(itemCast.getLore());
                node.setNodeType(itemCast.getNodeType());
                node.setDream(true);
                dreamName = entry.getValue().getName();
            }
        }
        Integer recentNode = treePane.getRecentNode();

        if (recentNode != null) {
            if (node.getNodeType() != unit.getAllocatedPassives().get(recentNode).getNodeType()) return;
            unit.getAllocatedPassives().get(recentNode).setName(node.getName());
            unit.getAllocatedPassives().get(recentNode).setModifiers(node.getModifiers());
            unit.getAllocatedPassives().get(recentNode).setStatusDescription(node.getStatusDescription());
            unit.getAllocatedPassives().get(recentNode).setDescription(node.getDescription());
            unit.getAllocatedPassives().get(recentNode).setLore(node.getLore());
            unit.getAllocatedPassives().get(recentNode).setNodeType(node.getNodeType());
            unit.getAllocatedPassives().get(recentNode).setDream(true);

            if (dreamName != null) {
                unit.getInventoryManager().removeItem(dreamName);
            }
        } else {
            System.out.println("Recent Node clicked is NULL");
        }

        dream.getItems().clear();

        for (Item item : unit.getInventoryItems().values()) {
            if (item instanceof Dream) {
                Dream toPut = (Dream) item;
                dream.getItems().add(toPut.getName());
            }
        }

        dream.setValue("");
    }

    public void updateProperty(PassiveNode node) {
        nameLabel.setText("Node: " + node.getName());
        descriptionLabel.setText(node.getDescription());
        statusDescriptionLabel.setText(node.getStatusDescription());
        loreLabel.setText("\n"+node.getLore());
    }
    public void updateEmptyProperty() {
        nameLabel.setText("Node:");
        descriptionLabel.setText("");
        statusDescriptionLabel.setText("");
        loreLabel.setText("");
    }

    public void updateRemainingPoints(Unit unit) {
        remainingPointsLabel.setText("Remaining Point: " + unit.getRemainingPassiveTreePoint());
    }
}

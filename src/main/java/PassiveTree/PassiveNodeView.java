package main.PassiveTree;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import model.entity.PassiveNode;
import model.type.NodeType;

import java.util.List;

public class PassiveNodeView extends StackPane {
    private final int id;
    private boolean selected = false;
    private PassiveNode passiveNode;
    private Text nameText;
    private double offsetX;
    private double offsetY;
    private ColorAdjust darkEffect = new ColorAdjust();
    private ColorAdjust brightEffect = new ColorAdjust();
    ImageView nodeImage = new ImageView();
    private final DropShadow goldGlow = new DropShadow(50, Color.GOLD);


    public PassiveNodeView(PassiveNode passiveNode) {
        this.passiveNode = passiveNode;
        this.id = passiveNode.getId();
        darkEffect.setBrightness(-0.5);
        brightEffect.setBrightness(0.2);
        if (passiveNode.getNodeType() == NodeType.SMALL){
//            Circle nodeImage = new Circle(30);
//            nodeImage.setFill(Color.SKYBLUE);
//            nodeImage.setStroke(Color.WHITE);
            nodeImage = new ImageView(new Image("images/60.png"));
            nodeImage.setFitWidth(60);
            nodeImage.setFitHeight(60);
        } else if (passiveNode.getNodeType() == NodeType.NOTABLE){
//            Circle nodeImage = new Circle(35);
//            nodeImage.setFill(Color.PINK);
//            nodeImage.setStroke(Color.WHITE);
            nodeImage = new ImageView(new Image("images/80.png"));
            nodeImage.setFitWidth(80);
            nodeImage.setFitHeight(80);
        } else if (passiveNode.getNodeType() == NodeType.KEYSTONE){
//            Circle nodeImage = new Circle(40);
//            nodeImage.setFill(Color.PURPLE);
//            nodeImage.setStroke(Color.WHITE);
            nodeImage = new ImageView(new Image("images/100.png"));
            nodeImage.setFitWidth(100);
            nodeImage.setFitHeight(100);
        }
        nodeImage.setEffect(darkEffect);
        getChildren().addAll(nodeImage);
        setLayoutX(passiveNode.getX());
        setLayoutY(passiveNode.getY());
    }

    public void updateSelectedStyle() {
        if (selected) {
            nodeImage.setEffect(brightEffect);
            setEffect(goldGlow);
        } else {
            nodeImage.setEffect(darkEffect);
            setEffect(null);
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public Integer getNodeId() {
        return id;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        updateSelectedStyle();
    }

    public String getLabel() {
        return this.passiveNode.getName();
    }

    public PassiveNode getPassiveNode() {
        return passiveNode;
    }

    public List<Integer> getConnectedNodeIds() {
        return passiveNode.getConnectedNodes();
    }

    public void setPassiveNode(PassiveNode passiveNode) {
        this.passiveNode = passiveNode;
    }

    public Text getNameText() {
        return nameText;
    }

    public void setNameText(Text nameText) {
        this.nameText = nameText;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }
}

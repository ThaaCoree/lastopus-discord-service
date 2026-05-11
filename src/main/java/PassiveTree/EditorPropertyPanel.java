package main.PassiveTree;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.entity.PassiveNode;

public class EditorPropertyPanel extends VBox {
    private Label nameLabel;
    private Label descriptionLabel;
    private Label statusDescriptionLabel;
    private Label loreLabel;
    private EditorTreePane treePane;

    public EditorPropertyPanel() {
        // กำหนด layout ของ panel
        this.setSpacing(10);
        this.setPadding(new Insets(15));
        this.setPrefWidth(250);

        // กำหนดพื้นหลังสีเข้ม
        this.getStyleClass().add("custom-panel");
        this.getStylesheets().add(getClass().getResource("/styles/theme.css").toExternalForm());

        // กำหนด label พื้นฐาน
        nameLabel = new Label("Property of Node:");
        descriptionLabel = new Label("");
        statusDescriptionLabel = new Label("");
        loreLabel = new Label("");

        this.getChildren().add(nameLabel);
        this.getChildren().add(statusDescriptionLabel);
        this.getChildren().add(descriptionLabel);
        this.getChildren().add(loreLabel);
    }

    public void updateProperty(PassiveNode node) {
            nameLabel.setText("Node: " + node.getName());
            descriptionLabel.setText(node.getDescription());
            statusDescriptionLabel.setText(node.getStatusDescription());
        loreLabel.setText(node.getLore());
    }
    public void updateEmptyProperty() {
            nameLabel.setText("Node:");
            descriptionLabel.setText("");
            statusDescriptionLabel.setText("");
        loreLabel.setText("");
    }
}

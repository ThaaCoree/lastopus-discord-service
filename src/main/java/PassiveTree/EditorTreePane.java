package main.PassiveTree;

import com.fasterxml.jackson.core.type.TypeReference;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import model.entity.PassiveNode;
import model.type.NodeType;
import util.JsonUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EditorTreePane extends Pane {

    private Group connectionGroup;
    private Map<Integer, PassiveNode> passiveNodeList = JsonUtils.loadFromFile("passives.json", new TypeReference<Map<Integer, PassiveNode>>() {});
//    private List<PassiveNode> passiveNodeList = new ArrayList<>();
    private EditorPropertyPanel propertyPanel;
    private List<PassiveNodeView> nodeViews;
    private double mouseAnchorX, mouseAnchorY;
    private double translateAnchorX, translateAnchorY;
    private Group contentGroup;

        public EditorTreePane() {
            System.out.println(new File("passives.json").getAbsolutePath());

//            ImageView backgroundView = new ImageView(
//                    new Image(getClass().getResource("/images/constellation_tree4.png").toExternalForm())
//            );
//            backgroundView.setFitWidth(6000);
//            backgroundView.setFitHeight(6000);
//            backgroundView.setPreserveRatio(false);

            Rectangle blackBackground = new Rectangle(6000, 6000);
            blackBackground.setFill(Color.BLACK);

            this.getChildren().add(blackBackground);
//            this.getChildren().add(backgroundView);

            contentGroup = new Group();
            // สร้าง PropertyPanel และจัดตำแหน่ง
            propertyPanel = new EditorPropertyPanel();
            propertyPanel.setLayoutX(600); // กำหนดตำแหน่ง PropertyPanel
            propertyPanel.setLayoutY(50);
            // สร้าง Group สำหรับเส้นเชื่อมต่อ
            connectionGroup = new Group();
            contentGroup.getChildren().add(connectionGroup);
            contentGroup.getChildren().add(propertyPanel);

            this.getChildren().add(contentGroup);

            // สร้างรายการของโหนด
            nodeViews = new ArrayList<>();
            createNodes();
            drawConnections(nodeViews);

            enableZoomAndDrag();
        }

    public void drawConnections(List<PassiveNodeView> nodeViews) {
        connectionGroup.getChildren().clear(); //ลบเส้นจากโหนดเก่าทั้งหมด

        for (PassiveNodeView nodeView : nodeViews) {
            if (nodeView.getConnectedNodeIds() == null) {
                continue;
            }
            for (Integer connectedId : nodeView.getConnectedNodeIds()) {
                PassiveNodeView targetView = null;
                for (PassiveNodeView view : nodeViews) {
                    if (view.getNodeId() != null && connectedId != null) {
                        if (view.getNodeId().equals(connectedId)) {
                            targetView = view;
                            break;
                        }
                    }
                }
                if (targetView != null) {
                    // เพื่อไม่วาดเส้นซ้ำ เช็กว่า nodeId ของต้นทางน้อยกว่าปลายทางก่อน
                    if (nodeView.getNodeId().compareTo(targetView.getNodeId()) > 0) {
                        Line line = new Line();
                        NodeType nodeType = nodeView.getPassiveNode().getNodeType();
                        NodeType targetNodeType = targetView.getPassiveNode().getNodeType();
                        double sourceNodeWidthAndHeight = 0;
                        double targetNodeWidthAndHeight = 0;
                        if (nodeType == NodeType.SMALL) {
                            sourceNodeWidthAndHeight = 30;
                        } else if (nodeType == NodeType.NOTABLE) {
                            sourceNodeWidthAndHeight = 40;
                        } else if (nodeType == NodeType.KEYSTONE) {
                            sourceNodeWidthAndHeight = 50;
                        }
                        if (targetNodeType == NodeType.SMALL) {
                            targetNodeWidthAndHeight = 30;
                        } else if (targetNodeType == NodeType.NOTABLE) {
                            targetNodeWidthAndHeight = 40;
                        } else if (targetNodeType == NodeType.KEYSTONE) {
                            targetNodeWidthAndHeight = 50;
                        }
                            line.startXProperty().bind(nodeView.layoutXProperty().add(sourceNodeWidthAndHeight));
                        line.startYProperty().bind(nodeView.layoutYProperty().add(sourceNodeWidthAndHeight));
                        line.endXProperty().bind(targetView.layoutXProperty().add(targetNodeWidthAndHeight));
                        line.endYProperty().bind(targetView.layoutYProperty().add(targetNodeWidthAndHeight));

                        line.setStrokeWidth(2);
                        line.setStyle("-fx-stroke: white;");

                        connectionGroup.getChildren().add(line);
                    }
                }
            }
        }
    }

    private void createNodes() {
        for (PassiveNode node : passiveNodeList.values()) {
            PassiveNodeView nodeN = new PassiveNodeView(node);
            nodeN.setLayoutX(node.getX());
            nodeN.setLayoutY(node.getY());

            nodeN.setOnMouseEntered(e -> updatePropertyPanel(node));
            nodeN.setOnMouseExited(e -> updateEmptyPropertyPanel());

            nodeViews.add(nodeN);
            contentGroup.getChildren().add(nodeN);
        }
    }

    public void updatePropertyPanel(PassiveNode node) {
        // ใช้ method จาก PropertyPanel
        propertyPanel.updateProperty(node);
    }

    public void updateEmptyPropertyPanel() {
        // ใช้ method จาก PropertyPanel
        propertyPanel.updateEmptyProperty();
    }

    private void enableZoomAndDrag() {
        this.setOnScroll(event -> {
            double zoomFactor = (event.getDeltaY() > 0) ? 1.1 : 0.9;

            // Scale limit เพื่อกันหลุด
            double newScaleX = this.getScaleX() * zoomFactor;
            double newScaleY = this.getScaleY() * zoomFactor;
            if (newScaleX < 0.1 || newScaleX > 5) return;

            this.setScaleX(newScaleX);
            this.setScaleY(newScaleY);

            event.consume();
        });

        this.setOnMousePressed(event -> {
            if (event.isSecondaryButtonDown()) {
                mouseAnchorX = event.getSceneX();
                mouseAnchorY = event.getSceneY();
                translateAnchorX = this.getTranslateX();
                translateAnchorY = this.getTranslateY();
                event.consume();
            }
        });

        this.setOnMouseDragged(event -> {
            if (event.isSecondaryButtonDown()) {
                this.setTranslateX(translateAnchorX + event.getSceneX() - mouseAnchorX);
                this.setTranslateY(translateAnchorY + event.getSceneY() - mouseAnchorY);
                event.consume();
            }
        });
    }

    public Group getConnectionGroup() {
        return connectionGroup;
    }

    public Map<Integer, PassiveNode> getPassiveNodeList() {
        return passiveNodeList;
    }

    public EditorPropertyPanel getPropertyPanel() {
        return propertyPanel;
    }

    public List<PassiveNodeView> getNodeViews() {
        return nodeViews;
    }

    public Group getContentGroup() {
        return contentGroup;
    }

    public double getMouseAnchorX() {
        return mouseAnchorX;
    }

    public double getMouseAnchorY() {
        return mouseAnchorY;
    }

    public double getTranslateAnchorX() {
        return translateAnchorX;
    }

    public double getTranslateAnchorY() {
        return translateAnchorY;
    }
}
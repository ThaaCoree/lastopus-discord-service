package main.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import main.java.PassiveTree.PassiveNodeView;
import model.entity.PassiveNode;
import model.entity.units.Unit;
import model.type.NodeType;
import util.JsonUtils;

import java.util.*;

public class TreePane extends StackPane {
    private Group connectionGroup;
    private Group allocatedConnectionGroup;
    private Map<Integer, PassiveNode> passiveNodeList = new TreeMap<>(JsonUtils.loadFromFile("/json/passives.json", new TypeReference<Map<Integer, PassiveNode>>() {}));
    private TreePropertyPanel propertyPanel;
    private List<PassiveNodeView> nodeViews;
    private double mouseAnchorX, mouseAnchorY;
    private double translateAnchorX, translateAnchorY;
    private Group contentGroup;
    private Integer recentNode;

    public TreePane (Unit unit) {

        setStyle("-fx-background-color: #2c2c2c;");
        contentGroup = new Group();
        // สร้าง PropertyPanel และจัดตำแหน่ง
        propertyPanel = new TreePropertyPanel(unit, this);
        propertyPanel.setLayoutX(600); // กำหนดตำแหน่ง PropertyPanel
        propertyPanel.setLayoutY(50);
        // สร้าง Group สำหรับเส้นเชื่อมต่อ
        connectionGroup = new Group();
        allocatedConnectionGroup = new Group();
        contentGroup.getChildren().add(connectionGroup);
        contentGroup.getChildren().add(allocatedConnectionGroup);
        contentGroup.getChildren().add(propertyPanel);

        this.getChildren().add(contentGroup);

        // สร้างรายการของโหนด
        nodeViews = new ArrayList<>();
        createNodes(unit);
        drawConnections(nodeViews);
        makeAllocatable(nodeViews, unit);
        updateAllocated(unit);
        drawAllocationConnections(unit);
        updatePlayersNodeProperties(unit);

        enableZoomAndDrag();
    }

    public TreePane() {

    }

    public void makeAllocatable(List<PassiveNodeView> nodeViews, Unit unit) {
        for (PassiveNodeView nodeView : nodeViews) {
            nodeView.setOnMousePressed(event -> {
                if (event.isPrimaryButtonDown()) {
                    Set<Integer> allocatedIds = new HashSet<>();
                    for (PassiveNode node : unit.getAllocatedPassives().values()) {
                        allocatedIds.add(node.getId());
                    }
                    boolean isAllocated = allocatedIds.contains(nodeView.getPassiveNode().getId());
                    if (!isAllocated) {
                        unit.getPassiveManager().allocateNode(nodeView.getPassiveNode());
                        recentNode = nodeView.getPassiveNode().getId();
                    }
                    if (isAllocated) {
                        unit.getPassiveManager().unallocateNode(nodeView.getPassiveNode());
                    }
                    updateAllocated(unit);
                    drawAllocationConnections(unit);
                    propertyPanel.updateRemainingPoints(unit);
                }
            });
        }
    }

    public void updateAllocated(Unit unit) {
        Set<Integer> allocatedIds = new HashSet<>();
        for (PassiveNode node : unit.getAllocatedPassives().values()) {
            allocatedIds.add(node.getId());
        }
        for (PassiveNodeView nodeView : nodeViews) {
            boolean isAllocated = allocatedIds.contains(nodeView.getPassiveNode().getId());
            nodeView.setSelected(isAllocated);
        }
    }

    public void updatePlayersNodeProperties(Unit unit) {
        for (PassiveNode node : unit.getAllocatedPassives().values()) {
            PassiveNodeView nodeView = findNodeViewById(node.getId());
            if (nodeView != null) {
                PassiveNode updatedNode = nodeView.getPassiveNode();
                unit.getAllocatedPassives().put(updatedNode.getId(), updatedNode);
            }
        }
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

    public void drawAllocationConnections(Unit unit) {
        allocatedConnectionGroup.getChildren().clear();

        Map<Integer, PassiveNode> allocated = unit.getAllocatedPassives();
        Set<String> drawnConnections = new HashSet<>(); // ป้องกันเส้นซ้ำ

        for (PassiveNode node : allocated.values()) {
            PassiveNodeView sourceView = findNodeViewById(node.getId());
            if (sourceView == null) continue;

            for (Integer connectedId : node.getConnectedNodes()) {
                if (!allocated.containsKey(connectedId)) continue; // วาดเฉพาะถ้าจัดสรรทั้งคู่

                PassiveNodeView targetView = findNodeViewById(connectedId);
                if (targetView == null) continue;

                // ป้องกันเส้นซ้ำ
                int min = Math.min(node.getId(), connectedId);
                int max = Math.max(node.getId(), connectedId);
                String key = min + "-" + max;
                if (drawnConnections.contains(key)) continue;
                drawnConnections.add(key);

                // วาดเส้น
                Line line = new Line();
                DropShadow glow = new DropShadow(30, Color.GOLD);
                line.setEffect(glow);

                line.setStrokeWidth(2);
                line.setStyle("-fx-stroke: gold;");

                line.startXProperty().bind(sourceView.layoutXProperty().add(sourceView.widthProperty().divide(2)));
                line.startYProperty().bind(sourceView.layoutYProperty().add(sourceView.heightProperty().divide(2)));
                line.endXProperty().bind(targetView.layoutXProperty().add(targetView.widthProperty().divide(2)));
                line.endYProperty().bind(targetView.layoutYProperty().add(targetView.heightProperty().divide(2)));

                allocatedConnectionGroup.getChildren().add(line);
            }
        }
    }

    private PassiveNodeView findNodeViewById(int id) {
        for (PassiveNodeView view : nodeViews) {
            if (view.getNodeId() == id) return view;
        }
        return null;
    }

    private void createNodes(Unit unit) {
        for (PassiveNode node : passiveNodeList.values()) {
            PassiveNodeView nodeN = new PassiveNodeView(node);
            nodeN.setLayoutX(node.getX());
            nodeN.setLayoutY(node.getY());

            nodeN.setOnMouseEntered(e -> propertyPanel.updateProperty(node));
            nodeN.setOnMouseExited(e -> propertyPanel.updateEmptyProperty());

            nodeViews.add(nodeN);
            contentGroup.getChildren().add(nodeN);
        }

        for (PassiveNode node : unit.getAllocatedPassives().values()) {
            if (node.isDream()) {
                nodeViews.removeIf(view -> view.getNodeId() == node.getId());
                PassiveNodeView nodeN = new PassiveNodeView(node);
                nodeN.setLayoutX(node.getX());
                nodeN.setLayoutY(node.getY());

                nodeN.setOnMouseEntered(e -> propertyPanel.updateProperty(node));
                nodeN.setOnMouseExited(e -> propertyPanel.updateEmptyProperty());

                nodeViews.add(nodeN);
                contentGroup.getChildren().add(nodeN);
            }
        }
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

    public Integer getRecentNode() {
        return recentNode;
    }

    public void setRecentNode(int recentNode) {
        this.recentNode = recentNode;
    }

    public Group getConnectionGroup() {
        return connectionGroup;
    }

    public TreePropertyPanel getPropertyPanel() {
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

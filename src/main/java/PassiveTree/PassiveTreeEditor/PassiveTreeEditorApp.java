package PassiveTree.PassiveTreeEditor;

import PassiveTree.PassiveNodeView;
import PassiveTree.EditorTreePane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import util.StatTranslateUtil;
import model.entity.PassiveNode;
import util.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PassiveTreeEditorApp extends Application {

    private final EditorTreePane treePane = new EditorTreePane();
    private List<PassiveNodeView> nodeViews = treePane.getNodeViews();
    private final List<Line> connections = new ArrayList<>();
    private PassivePropertyEditorPanel editorPanel;


    @Override
    public void start(Stage primaryStage) {

        BorderPane root = new BorderPane();
        root.setCenter(treePane);

        HBox controls = new HBox(100);
        Button connectButton = new Button("Connect Selected");
        Button saveButton = new Button("Save to JSON");
        controls.getStyleClass().add("custom-panel");
        controls.getStylesheets().add(getClass().getResource("/styles/theme.css").toExternalForm());
        controls.getChildren().add(connectButton);
        controls.getChildren().add(saveButton);
        root.setTop(controls);
        editorPanel = new PassivePropertyEditorPanel(this);
        root.setRight(treePane.getPropertyPanel());
        root.setLeft(editorPanel);

        Scene scene = new Scene(root, 1000, 1000);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Passive Tree Editor");
        primaryStage.show();

        // ปุ่ม connect
        connectButton.setOnAction(e -> connectSelectedNodes());
        saveButton.setOnAction(e -> saveToJson());

        for (PassiveNodeView nodeView : nodeViews) {
            makeDraggable(nodeView);
        }

        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Editor is closing...");
            saveToJson();
        });
    }

    private void connectSelectedNodes() {
        List<PassiveNodeView> selected = new ArrayList<>();
        for (PassiveNodeView view : nodeViews) {
            if (view.isSelected()) {
                selected.add(view);
            }
        }
        if (selected.size() == 2) {
            PassiveNodeView a = selected.get(0);
            PassiveNodeView b = selected.get(1);
            for (Integer connectedNodeIdA : a.getPassiveNode().getConnectedNodes()) {
                if (Objects.equals(connectedNodeIdA, b.getNodeId())) {
                    System.out.println("Same connectedNodes found, removing connected nodes");
                    a.getPassiveNode().getConnectedNodes().remove(b.getNodeId());
                    b.getPassiveNode().getConnectedNodes().remove(a.getNodeId());
                    treePane.drawConnections(treePane.getNodeViews());
                    a.setSelected(false);
                    b.setSelected(false);
                    return;
                }
            }

            a.getPassiveNode().addConnectedNodes(b.getNodeId());
            b.getPassiveNode().addConnectedNodes(a.getNodeId());

            treePane.drawConnections(treePane.getNodeViews());

            // ล้างเลือกหลังจากเชื่อม
            a.setSelected(false);
            b.setSelected(false);
        }

    }

    private void saveToJson() {
        for (PassiveNodeView nodeView : nodeViews) {
            PassiveNode node = nodeView.getPassiveNode();
            node.setX(nodeView.getLayoutX());
            node.setY(nodeView.getLayoutY());
            StatTranslateUtil.translatePassiveNodeStatusDesc(node);
        }
        JsonUtils.saveToFile(treePane.getPassiveNodeList(), "passives.json");
        List<PassiveNode> nodeList = new ArrayList<>(treePane.getPassiveNodeList().values());
        JsonUtils.saveToFile(nodeList, "passives-display.json");
    }

    public void makeDraggable(PassiveNodeView nodeView) {
        nodeView.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                nodeView.setSelected(!nodeView.isSelected());
                nodeView.updateSelectedStyle();

                nodeView.setOffsetX(e.getX());
                nodeView.setOffsetY(e.getY());
                e.consume();
            }
        });

        nodeView.setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                double newX = nodeView.getLayoutX() + e.getX() - nodeView.getOffsetX();
                double newY = nodeView.getLayoutY() + e.getY() - nodeView.getOffsetY();

                nodeView.setLayoutX(newX);
                nodeView.setLayoutY(newY);

                e.consume();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    public EditorTreePane getTreePane() {
        return treePane;
    }

    public List<PassiveNodeView> getNodeViews() {
        return nodeViews;
    }
}

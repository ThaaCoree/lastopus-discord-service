package main.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import main.Database;
import model.entity.Conditions;

public class ConditionListPane extends StackPane {

    private final TextField searchField;
    private final ListView<Conditions> listView;
    private final ObservableList<Conditions> conditionList;
    private final FilteredList<Conditions> filteredConditions;
    private final Database database;
    private final ConditionEditPanel editPanel;

    public ConditionListPane(Database database) {
        this.database = database;
        editPanel = new ConditionEditPanel(database, this);

        getStylesheets().add(getClass().getResource("/styles/theme.css").toExternalForm());

        this.conditionList = FXCollections.observableArrayList(database.getAllConditionMap().values());
        this.filteredConditions = new FilteredList<>(conditionList, p -> true);

        searchField = new TextField();
        searchField.setPromptText("Search condition...");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal.toLowerCase().trim();
            String[] keywords = filter.split("\\s+"); // แยกคำด้วยช่องว่าง

            filteredConditions.setPredicate(item -> {
                if (item == null) return false;

                StringBuilder sb = new StringBuilder();
                sb.append(item.getName()).append(" ");
                sb.append(item.getDescription()).append(" ");
                sb.append(item.getStatusDescription()).append(" ");
                sb.append(item.getConditionTierType()).append(" ");
                sb.append(item.getConditionType()).append(" ");

                String searchable = sb.toString().toLowerCase();

                // ต้องผ่านทุก keyword ถึงจะผ่าน
                for (String keyword : keywords) {
                    if (!searchable.contains(keyword)) {
                        return false;
                    }
                }
                return true;
            });
        });

        listView = new ListView<>(filteredConditions);
        listView.setMinWidth(1300);
        listView.setMaxWidth(1300);
        listView.setMinHeight(800);
        listView.setCellFactory(lv -> new ListCell<Conditions>() {
            @Override
            protected void updateItem(Conditions con, boolean empty) {
                super.updateItem(con, empty);
                if (empty || con == null) {
                    setGraphic(null);
                } else {
                    Label nameLabel = new Label(con.getName());
                    nameLabel.setMinWidth(200);
                    nameLabel.setMaxWidth(200);
                    nameLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");

                    Label tierLabel = new Label(con.getConditionTierType().writeAsString());
                    tierLabel.setMinWidth(150);
                    tierLabel.setMaxWidth(150);
                    tierLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");

                    Label typeLabel = new Label(con.getConditionType().writeAsString());
                    typeLabel.setMinWidth(150);
                    typeLabel.setMaxWidth(150);
                    typeLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");

                    Label descLabel = new Label(con.getDescription());
                    descLabel.setWrapText(true);
                    descLabel.setMinWidth(400);
                    descLabel.setMaxWidth(400);
                    descLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");

                    Label statusLabel = new Label(con.getStatusDescription());
                    statusLabel.setWrapText(true);
                    statusLabel.setMinWidth(400);
                    statusLabel.setMaxWidth(400);
                    statusLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");

                    HBox content = new HBox(2, nameLabel, tierLabel, typeLabel, descLabel, statusLabel);
                    content.setPadding(new Insets(5));
                    setGraphic(content);
                }
            }
        });

        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && editPanel.isEditMode()) {
                editPanel.editMode();
            }
        });

        VBox content = new VBox(10, searchField, listView);
        content.setPadding(new Insets(10));
        this.getChildren().add(content);
    }

    public ConditionEditPanel getEditPanel() {
        return editPanel;
    }

    public ListView<Conditions> getListView() {
        return listView;
    }

    public ObservableList<Conditions> getConditionList() {
        return conditionList;
    }
}

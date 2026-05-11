package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import app.Database;
import model.entity.Card;
import model.type.CardType;

public class CardListPane extends StackPane {

    private final TextField searchField;
    private final ListView<Card> listView;
    private final ObservableList<Card> cardList;
    private final FilteredList<Card> filteredCard;
    private final Database database;
    private final CardEditPanel editPanel;

    public CardListPane(Database database) {
        this.database = database;
        editPanel = new CardEditPanel(database, this);

        getStylesheets().add(getClass().getResource("/styles/theme.css").toExternalForm());

        this.cardList = FXCollections.observableArrayList(database.getAllCardMap().values());
        this.filteredCard = new FilteredList<>(cardList, p -> true);

        searchField = new TextField();
        searchField.setPromptText("Search condition...");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal.toLowerCase().trim();
            String[] keywords = filter.split("\\s+"); // แยกคำด้วยช่องว่าง

            filteredCard.setPredicate(item -> {
                if (item == null) return false;

                StringBuilder sb = new StringBuilder();
                for (CardType type : CardType.values()) {
                    sb.append(item.getName()).append(" ");
                    sb.append(item.getAbilityName().get(type)).append(" ");
                    sb.append(item.getDescription().get(type)).append(" ");
                    sb.append(item.getStatusDescription().get(type)).append(" ");
                }

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

        listView = new ListView<>(filteredCard);
        listView.setMinWidth(1300);
        listView.setMaxWidth(1300);
        listView.setMinHeight(800);
        listView.setCellFactory(lv -> new ListCell<Card>() {
            @Override
            protected void updateItem(Card card, boolean empty) {
                super.updateItem(card, empty);
                if (empty || card == null) {
                    setGraphic(null);
                } else {
                    Label nameLabel = new Label(card.getName());
                    nameLabel.setMinWidth(200);
                    nameLabel.setMaxWidth(200);
                    nameLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");

                    Label primaryName = new Label(card.getAbilityName().get(CardType.PRIMARY));
                    primaryName.setMinWidth(150);
                    primaryName.setMaxWidth(150);
                    primaryName.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");

                    Label secondaryName = new Label(card.getAbilityName().get(CardType.SECONDARY));
                    secondaryName.setMinWidth(150);
                    secondaryName.setMaxWidth(150);
                    secondaryName.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");

                    Label primaryDesc = new Label(card.getStatusDescription().get(CardType.PRIMARY) + card.getDescription().get(CardType.PRIMARY));
                    primaryDesc.setWrapText(true);
                    primaryDesc.setMinWidth(400);
                    primaryDesc.setMaxWidth(400);
                    primaryDesc.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");

                    Label secondaryDesc = new Label(card.getStatusDescription().get(CardType.SECONDARY) + card.getDescription().get(CardType.SECONDARY));
                    secondaryDesc.setWrapText(true);
                    secondaryDesc.setMinWidth(400);
                    secondaryDesc.setMaxWidth(400);
                    secondaryDesc.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");

                    HBox content = new HBox(2, nameLabel, primaryName, primaryDesc, secondaryName, secondaryDesc);
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

    public CardEditPanel getEditPanel() {
        return editPanel;
    }

    public ListView<Card> getListView() {
        return listView;
    }

    public ObservableList<Card> getCardList() {
        return cardList;
    }
}

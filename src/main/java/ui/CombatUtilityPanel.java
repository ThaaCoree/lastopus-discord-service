package main.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import main.java.controller.CombatFlow;
import main.java.controller.event.events.ActionEvent;
import main.java.controller.event.events.ResourceEvent;
import manager.ConditionManager;
import model.entity.ConditionInstance;
import model.entity.Conditions;
import model.entity.PassiveNode;
import model.entity.items.EquipmentSlot;
import model.entity.units.Monster;
import model.entity.units.Unit;
import model.type.*;
import util.FormulaUtils;
import util.LogWriterUtil;
import util.SearchableListView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static model.entity.skills.Skill.calculateDamageAfterDEF;


public class CombatUtilityPanel extends ScrollPane {
    VBox contentBox = new VBox();
    Button refresh = new Button("Refresh");
    ComboBox<String> allName = new ComboBox<>();
    private final DecimalFormat df = new DecimalFormat("0.##");
    VBox requestBox = new VBox();
    CombatFlow combatFlow;

    public CombatUtilityPanel(CombatFlow combatFlow) {
        this.combatFlow = combatFlow;

        setPrefWidth(400);
        for (Unit unit : combatFlow.getAllUnit().values()) {
            allName.getItems().add(unit.getName());
        }

        refresh.setOnAction(e-> {
            refreshContent(combatFlow);
        });
        allName.setOnAction(e-> {
            contentBox.getChildren().clear();
            StringBuilder sb = new StringBuilder("Extra Abilities:\n");
            Unit unit = combatFlow.getAllUnit().get(allName.getValue());
            if (unit != null) {
                VBox countBox = new VBox();
                for (Map.Entry<CounterName, Double> entry : unit.getCounter().entrySet()) {
                    HBox count = new HBox();
                    Label label = new Label(entry.getKey().writeAsString());
                    TextField countAmount = new TextField(Double.toString(entry.getValue()));
                    countAmount.setPrefWidth(80);
                    Button plus = new Button("+");
                    Button minus = new Button("-");
                    plus.setOnAction(ev-> {
                        unit.counterIncrement(entry.getKey());
                        countAmount.setText(Double.toString(entry.getValue()));
                    });
                    minus.setOnAction(ev-> {
                        unit.counterDecrement(entry.getKey());
                        countAmount.setText(Double.toString(entry.getValue()));
                    });
                    countAmount.setOnKeyReleased(ev -> {
                        unit.counterSet(entry.getKey(), Double.parseDouble(countAmount.getText()));
                    });
                    count.getChildren().addAll(label, countAmount, plus, minus);
                    count.setAlignment(Pos.CENTER_LEFT);
                    countBox.getChildren().add(count);
                }
                for (EquipmentSlot slot : unit.getEquipmentSlots().values()) {
                    if (slot.getEquipment() == null) continue;
                    if (slot.getEquipment().getDescription().isEmpty()) continue;
                    sb.append(slot.getEquipment().getName()).append("\n");
                    sb.append(slot.getEquipment().getDescription()).append("\n\n");
                }
                for (PassiveNode node : unit.getAllocatedPassives().values()) {
                    if (node.getDescription().isEmpty()) continue;
                    sb.append(node.getName()).append("\n");
                    sb.append(node.getDescription()).append("\n\n");
                }
                for (ConditionInstance instance : unit.getConditionInstances().values()) {
                    if (instance.getCondition() == null) continue;
                    if (instance.getCondition().getDescription().isEmpty()) continue;
                    sb.append(instance.getCondition().getName()).append("\n");
                    sb.append(instance.getCondition().getDescription()).append("\n\n");
                }
                if (unit instanceof Monster) {
                    for (Map.Entry<Integer, String> move : ((Monster) unit).getOpusMove().entrySet()) {
                        if (move.getValue().isEmpty()) continue;
                        sb.append("Opus Move ").append(move.getKey()).append(":").append("\n");
                        sb.append(move.getValue()).append("\n\n");
                    }
                }
                Label display = new Label(sb.toString());
                display.setWrapText(true);
                display.setPrefWidth(375);
                Region spacer = new Region();
                spacer.setPrefHeight(50);
                contentBox.getChildren().addAll(requestBox, refresh, allName, countBox, display, spacer, createConditionZone(unit, combatFlow));
            }
        });

        registerEvent();
        combatFlow.addOnResetListener(() -> {
            registerEvent();
        });


        requestBox.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 2 0; -fx-padding: 5px;");
        requestBox.setPrefWidth(400);

        contentBox.getChildren().addAll(requestBox, refresh, allName);
        setContent(contentBox);
    }

    public Node createConditionZone(Unit unit, CombatFlow combatFlow) {
        VBox conditionBox = new VBox();

        Label conditionInd = new Label("Condition Name");
        Label conditionTurnInd = new Label("Turn");
        Region conditionDelSpacer = new Region();
        conditionDelSpacer.setPrefWidth(80);
        conditionInd.setPrefWidth(150);
        conditionTurnInd.setPrefWidth(50);
        HBox conditionIndRow = new HBox(conditionInd, conditionTurnInd);
        conditionBox.getChildren().add(conditionIndRow);
        for (ConditionInstance instance : unit.getConditionInstances().values()) {
            HBox conditionRow = new HBox();
            Label conditionName = new Label(instance.getCondition().getName());
            Label conditionTurn = new Label(df.format(instance.getDurationRemain()));
            conditionName.setPrefWidth(150);
            conditionTurn.setPrefWidth(50);
            Button removeCondition = new Button("Remove");
            removeCondition.setPrefWidth(80);
            removeCondition.setOnAction(event -> {
                ConditionManager.removeCondition(unit, instance.getCondition().getName());
                refreshContent(combatFlow);
            });
            conditionRow.getChildren().addAll(conditionName, conditionTurn, removeCondition);
            conditionBox.getChildren().add(conditionRow);
        }
        // add condition
        HBox conditionAddRow = new HBox(5);
        TextField conditionNameField = new TextField("");
        TextField turnField = new TextField("1");
        Label sourceInd = new Label("Source");
        ComboBox<String> conditionSource = new ComboBox<>();
        conditionSource.getItems().add("None");
        for (Unit toBox : combatFlow.getAllUnit().values()) {
            conditionSource.getItems().add(toBox.getName());
        }
        Button addCondition = new Button("Add");
        conditionNameField.setPrefWidth(150);
        turnField.setPrefWidth(50);
        addCondition.setPrefWidth(80);
        conditionAddRow.getChildren().addAll(conditionNameField, turnField, addCondition);
        Popup choosePopup = new Popup();
        ListView<String> itemListView = new ListView<>();
        List<String> itemNameList = new ArrayList<>();
        for (Conditions condition : combatFlow.getDatabase().getAllConditionMap().values()) {
            itemNameList.add(condition.getName());
        }

        addCondition.setOnAction(ev -> {
            String conditionName = conditionNameField.getText();
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get(conditionName);
            Unit condiSource = combatFlow.getAllUnit().get(conditionSource.getValue());
            if (condition != null && condiSource != null) {
                ConditionManager.applyCondition(condition, condiSource, unit, Integer.parseInt(turnField.getText()));
            } else if (condition != null) {
                ConditionManager.applyCondition(condition, unit, Integer.parseInt(turnField.getText()));
            } else {
                System.out.println("Condition Not Found");
            }
            refreshContent(combatFlow);
        });

        SearchableListView.makeSearchable(itemListView, FXCollections.observableArrayList(itemNameList), conditionNameField);
        itemListView.setOnMouseClicked(ev -> {
            String selectedItem = itemListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                conditionNameField.setText(selectedItem);
                choosePopup.hide();
                conditionBox.requestFocus();
            }
        });
        itemListView.setOnKeyReleased(ev -> {
            if (ev.getCode() == KeyCode.ENTER || ev.getCode() == KeyCode.ESCAPE) {
                String selectedItem = itemListView.getSelectionModel().getSelectedItem();
                conditionNameField.setText(selectedItem);
                choosePopup.hide();
                conditionBox.requestFocus();
            }
        });
        choosePopup.getContent().add(itemListView);

        conditionNameField.setOnKeyReleased(ev -> {
            if (!choosePopup.isShowing()) {
                Bounds screenBounds = conditionNameField.localToScreen(conditionNameField.getBoundsInParent());
                if (screenBounds != null) {
                    choosePopup.show(conditionNameField, screenBounds.getMaxX(), screenBounds.getMinY());
                }
            }
            if (ev.getCode() == KeyCode.ESCAPE) {
                choosePopup.hide();
                conditionBox.requestFocus();
            }
        });

        conditionBox.getChildren().addAll(conditionAddRow,sourceInd,conditionSource);
        return conditionBox;
    }

    public Node createPendingField(double amount, Unit target, ActionEffectType actionEffectType, ActionEvent event) {
        if (amount <= 0) {
            if (actionEffectType == ActionEffectType.DAMAGE_MAGICAL ||
            actionEffectType == ActionEffectType.DAMAGE_PHYSICAL ||
            actionEffectType == ActionEffectType.DAMAGE_PURE ||
            actionEffectType == ActionEffectType.DAMAGE_TRUE) {
                return new VBox();
            }
        }
        VBox pendingBox = new VBox();
        HBox row1 = new HBox();
        HBox row2 = new HBox();
        HBox row3 = new HBox();
        DecimalFormat df = new DecimalFormat("0.##");
        StringBuilder sb = new StringBuilder();

        sb.append(event.event_source).append("( ").append(event.unit_source.getName()).append(" )").append(" > ").append(target.getName());
        switch (actionEffectType) {
            case DAMAGE_PURE -> {
                sb.append(" [PURE Damage]");
                amount = calculateDamageAfterDEF(target, event.unit_source,
                        amount, DamageType.PURE, event.extra_def, event.ignore_def);
            }
            case DAMAGE_TRUE -> {
                sb.append(" [TRUE Damage]");
                amount = calculateDamageAfterDEF(target, event.unit_source,
                        amount, DamageType.TRUE, event.extra_def, event.ignore_def);
            }
            case DAMAGE_PHYSICAL -> {
                sb.append(" [Physical Damage]");
                amount = calculateDamageAfterDEF(target, event.unit_source,
                        amount, DamageType.PHYSICAL, event.extra_def, event.ignore_def);
            }
            case DAMAGE_MAGICAL -> {
                sb.append(" [Magical Damage]");
                amount = calculateDamageAfterDEF(target, event.unit_source,
                        amount, DamageType.MAGICAL, event.extra_def, event.ignore_def);
            }
            case HEALTH_RECOVER -> sb.append(" [Health Recover]");
            case MANA_RECOVER -> sb.append(" [Mana Recover]");
            case CREATE_DEBRIS -> sb.append(" [Debris]");
        }
        Label label = new Label(sb.toString());
        TextField field = new TextField();
        field.setText(df.format(amount));
        Button send = new Button("Send");
        Button cancel = new Button("Cancel");

        field.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                double toSendBack = FormulaUtils.evaluateFormula(field.getText());
                field.setText(df.format(toSendBack));
            }
        });

        send.setOnAction(e-> {

            double amount_to_send = Double.parseDouble(field.getText());

            ResourceEvent effect_event = new ResourceEvent(event.event_source, event.unit_source, target, amount_to_send, actionEffectType);

            if (actionEffectType.equals(ActionEffectType.DAMAGE_TRUE)) {
                effect_event.bypassDebris = true;
            }

            combatFlow.getEventBus().post(effect_event, EventPhase.PRE);
            combatFlow.getEventBus().post(effect_event, EventPhase.MODIFY);

            switch (actionEffectType) {
                case DAMAGE_PURE, DAMAGE_TRUE, DAMAGE_MAGICAL, DAMAGE_PHYSICAL -> calculateDamageSend(effect_event);
                case HEALTH_RECOVER -> calculateHealthRecoverySend(effect_event);
                case MANA_RECOVER -> calculateManaRecoverySend(effect_event);
                case CREATE_DEBRIS -> calculateDebrisRecoverySend(effect_event);
            }

            combatFlow.getEventBus().post(effect_event, EventPhase.POST);
            combatFlow.allUnitUpdate();
            requestBox.getChildren().remove(pendingBox);
        });

        cancel.setOnAction(e-> {
            requestBox.getChildren().remove(pendingBox);
        });

        row1.getChildren().add(label);
        row2.getChildren().add(field);
        row3.getChildren().addAll(send,cancel);
        pendingBox.getChildren().addAll(row1,row2,row3);
        return pendingBox;
    }

    public Node createPendingCondition(Conditions conditions, Unit target, int condition_number, ActionEvent event, int turn_number) {
        VBox pendingBox = new VBox();
        HBox row1 = new HBox();
        HBox row2 = new HBox();
        HBox row3 = new HBox();
        StringBuilder sb = new StringBuilder();
        int duration = event.condition_to_inflict.get(condition_number).get(conditions);
        sb.append(event.event_source).append("( ").append(event.unit_source.getName()).append(" )").append(" > ").append(target.getName());
        sb.append(" Condition ").append(conditions.getName());
        Label label = new Label(sb.toString());
        TextField field = new TextField();
        field.setPrefWidth(150);
        field.setText(Integer.toString(duration));
        Button send = new Button("Send");
        Button cancel = new Button("Cancel");
        send.setOnAction(e-> {
            if (!event.condition_number_record.isEmpty()) {
                var record = event.condition_number_record.get(condition_number);
                if (record != null) {
                    ConditionManager.applyCondition(
                            conditions,
                            event.unit_source,
                            target,
                            turn_number,
                            record
                    );
                }
            } else {
                ConditionManager.applyCondition(conditions, event.unit_source, target, turn_number);
            }

            LogWriterUtil.log(">" + target.getName() + " received condition " + conditions.getName() + " from "+event.unit_source.getName());
            combatFlow.allUnitUpdate();
            requestBox.getChildren().remove(pendingBox);
        });
        cancel.setOnAction(e-> {
            requestBox.getChildren().remove(pendingBox);
        });

        row1.getChildren().add(label);
        row2.getChildren().add(field);
        row3.getChildren().addAll(send,cancel);
        pendingBox.getChildren().addAll(row1,row2,row3);
        return pendingBox;
    }

    public void calculateDamageSend(ResourceEvent event) {
        LogWriterUtil.log(event.target.getName()+" took "+df.format(event.amount)+" damage",combatFlow.getTurnCount());
        if (event.target instanceof Monster) {
            combatFlow.getDamageTaken().merge(event.target.getName(), event.amount, Double::sum);
        }
        if (event.target.hasSecondaryCard("Venus The Fallen Star Card")) {
            event.bypassDebris = false;
        }
        if (event.bypassDebris) {
            event.target.sumRemainingHealth(-1 * event.amount);
            return;
        }
        double debris = event.target.getDebris().getRemaining();
        double remainingDamage = -1 * event.amount;
        if (debris > 0) {
            if (Math.abs(remainingDamage) > debris) {
                // ดาเมจเกิน debris → ลบ debris ทั้งหมด แล้วเหลือดาเมจ
                remainingDamage += debris;
                event.target.getDebris().setRemaining(0);
            } else {
                // ดาเมจไม่เกิน → ลบออกจาก debris แล้วจบ
                event.target.getDebris().sumRemaining(remainingDamage);
                return;
            }
        }
        if (remainingDamage < 0) {
            event.target.sumRemainingHealth(remainingDamage);
        }
    }

    public void calculateHealthRecoverySend(ResourceEvent event) {
        LogWriterUtil.log(event.target.getName()+" took "+df.format(event.amount)+" health recovery",combatFlow.getTurnCount());
        if (event.target instanceof Monster) {
            combatFlow.getDamageTaken().merge(event.target.getName(), event.amount * -1, Double::sum);
        }

        double oldHealth = event.target.getHealth().getRemaining();
        event.target.sumRemainingHealth(event.amount);
        double newHealth = event.target.getHealth().getRemaining();
//        LogWriterUtil.log("Unit: "+event.target.getName()+" | Resource: HEALTH | Remaining: "+oldHealth+" -> "+newHealth+" (Recovered: "+ (newHealth-oldHealth) + ")");
    }

    public void calculateManaRecoverySend(ResourceEvent event) {
        LogWriterUtil.log(event.target.getName()+" took "+df.format(event.amount)+" mana recovery",combatFlow.getTurnCount());
        if (event.target instanceof Monster) {
            combatFlow.getDamageTaken().merge(event.target.getName(), event.amount * -1, Double::sum);
        }

        double oldMana = event.target.getMana().getRemaining();
        event.target.sumRemainingMana(event.amount);
        double newMana = event.target.getMana().getRemaining();
//        LogWriterUtil.log("Unit: "+event.target.getName()+" | Resource: MANA | Remaining: "+oldMana+" -> "+newMana+" (Change: "+ (newMana-oldMana) + ")");
    }

    public void calculateDebrisRecoverySend(ResourceEvent event) {
        LogWriterUtil.log(event.target.getName()+" took "+df.format(event.amount)+" debris",combatFlow.getTurnCount());
        if (event.target instanceof Monster) {
            combatFlow.getDamageTaken().merge(event.target.getName(), event.amount * -1, Double::sum);
        }

        double oldDebris = event.target.getDebris().getRemaining();
        event.target.sumRemainingDebris(event.amount);
        double newDebris = event.target.getDebris().getRemaining();
//        LogWriterUtil.log("Unit: "+event.target.getName()+" | Resource: DEBRIS | Remaining: "+oldDebris+" -> "+newDebris+" (Change: "+ (newDebris-oldDebris) + ")");
    }

    public void registerEvent() {
        combatFlow.getEventBus().register(ActionEvent.class, EventPhase.POST, -99, (ActionEvent event) -> {
            for (Unit target : event.unit_target) {
                for (int loop = 1 ; loop <= event.heal_times ; loop++) {
                    requestBox.getChildren().add(createPendingField(event.getHealCritable(target, loop), target, ActionEffectType.HEALTH_RECOVER, event));
                }
                for (int loop = 1 ; loop <= event.debris_times ; loop++) {
                    requestBox.getChildren().add(createPendingField(event.getCreateDebris(target.getName()), target, ActionEffectType.CREATE_DEBRIS, event));
                }
                for (int loop = 1 ; loop <= event.damage_times ; loop++) {
                    requestBox.getChildren().add(createPendingField(event.getDamageCritable(DamageType.TRUE, target, loop), target, ActionEffectType.DAMAGE_TRUE, event));
                    requestBox.getChildren().add(createPendingField(event.getDamageCritable(DamageType.PURE, target, loop), target, ActionEffectType.DAMAGE_PURE, event));
                    requestBox.getChildren().add(createPendingField(event.getDamageCritable(DamageType.PHYSICAL, target, loop), target, ActionEffectType.DAMAGE_PHYSICAL, event));
                    requestBox.getChildren().add(createPendingField(event.getDamageCritable(DamageType.MAGICAL, target, loop), target, ActionEffectType.DAMAGE_MAGICAL, event));
                }
                for (int loop = 1 ; loop <= event.mana_recover_times ; loop++) {
                    requestBox.getChildren().add(createPendingField(event.getRecoverMana(target.getName()), target, ActionEffectType.MANA_RECOVER, event));
                }
                event.condition_to_inflict.forEach((number, map) -> {
                    map.forEach((condition, turn_number) -> {
                        requestBox.getChildren().add(createPendingCondition(condition, target, number, event, turn_number));
                    });
                });
            }
        });
    }

    public void refreshContent(CombatFlow combatFlow) {
        allName.getItems().clear();
        contentBox.getChildren().clear();
        for (Unit unit : combatFlow.getAllUnit().values()) {
            allName.getItems().add(unit.getName());
        }
        contentBox.getChildren().addAll(requestBox, refresh,allName);
    }
}

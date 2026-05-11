package main.ui;

import com.google.api.services.sheets.v4.model.Request;
import com.google.common.util.concurrent.AtomicDouble;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.Duration;
import main.java.controller.CombatFlow;
import main.java.controller.event.events.ActionEvent;
import main.java.controller.event.events.ResourceEvent;
import main.java.controller.event.events.SkillUse;
import manager.ConditionManager;
import model.entity.ConditionInstance;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillInstance;
import model.entity.units.Monster;
import model.entity.units.Unit;
import model.modifier.ModValue;
import model.type.*;
import util.*;

import java.text.DecimalFormat;
import java.math.RoundingMode;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class CombatPane extends ScrollPane {
    private final CombatUtilityPanel utilityPanel;
    private final CombatFlow combatFlow;
    private boolean confirmRemove;
    private final VBox col1 = new VBox();
    private final VBox col2 = new VBox();
    HBox col1Block = new HBox();
    ListView<Unit> turnList;
    private final DecimalFormat df = new DecimalFormat("0.##");
    private Unit selectedTurnUnit;
    private Unit strikerUnit;
    private Unit defenderUnit;
    private DamageType damageType = DamageType.PHYSICAL;
    private String damageToWrite = "";
    private String additionalDef = "";
    private double finalDmgToSend = 0;
    private boolean isBypassDebris = false;
    private String additionalCrit = "";
    private String additionalEva = "";
    private String additionalBlock = "";
    private String additionalCritMult = "";
    private String additionalEvaMult = "";
    private String additionalBlockMult = "";
    private final Map<String, Boolean> fieldSetting = new LinkedHashMap<>();
    private final ComboBox<String> skillUser = new ComboBox<>();
    private final ComboBox<String> skillName = new ComboBox<>();
    private Map<Integer, Integer> target_count = new LinkedHashMap<>();
    private SkillTarget skill_target_to_send_back = new SkillTarget();
    private String skill_user = "";
    private String skill_name = "";
    private final CheckBox bypass_calculation = new CheckBox("Bypass Calc");

    public CombatPane(CombatFlow combatFlow) {
        this.combatFlow = combatFlow;
        confirmRemove = false;
        utilityPanel = new CombatUtilityPanel(this.combatFlow);
        col1Block.getChildren().addAll(turnList(),toolBlock());
        col1Block.setStyle("-fx-background-color: #393939;");
        col1.getChildren().addAll(col1Block, calculationBlock());
        List<Unit> player = new ArrayList<>(combatFlow.getPlayerUnit().values());
        List<Unit> monster = new ArrayList<>(combatFlow.getMonsterUnit().values());
        monster.addAll(combatFlow.getSummonUnit().values());
        col2.getChildren().addAll(importBlock(player), importBlock(monster));

        HBox mainBox = new HBox();
        mainBox.getChildren().addAll(col1, col2);
        setContent(mainBox);
    }

    public Node calculationBlock() {
        HBox calcBox = new HBox();
        VBox unitBox = new VBox(10);
        unitBox.setAlignment(Pos.CENTER);
        VBox damageBox = new VBox(10);
        VBox chanceBox = new VBox(10);
        chanceBox.setAlignment(Pos.CENTER);
        VBox fieldBox = new VBox();

        //fieldBox
        CheckBox rainfall = new CheckBox("Atlantean's Rainfall");
        rainfall.setVisible(false);
        if (combatFlow.getAllUnit().get("Akivili") != null) {
            rainfall.setVisible(true);
        }
        if (fieldSetting.get("Rainfall") != null) {
            rainfall.setSelected(fieldSetting.get("Rainfall"));
        }
        rainfall.setOnAction(e-> {
            fieldSetting.put("Rainfall", rainfall.isSelected());
            calculateToWrite();
            refreshCalculationBlock();
        });
        fieldBox.getChildren().addAll(rainfall);

        //unitBox
        ComboBox<String> striker = new ComboBox<>();
        ComboBox<String> defender = new ComboBox<>();
        striker.getItems().add("None");
        CheckBox isPhysical = new CheckBox("Physical");
        CheckBox isMagical = new CheckBox("Magical");
        CheckBox isPure = new CheckBox("Pure");
        CheckBox isTrue = new CheckBox("True");
        List<CheckBox> damageTypeList = new ArrayList<>();
        damageTypeList.add(isPhysical);
        damageTypeList.add(isMagical);
        damageTypeList.add(isPure);
        damageTypeList.add(isTrue);
        if (damageType.equals(DamageType.PHYSICAL)) {
            isPhysical.setSelected(true);
        }
        if (damageType.equals(DamageType.MAGICAL)) {
            isMagical.setSelected(true);
        }
        if (damageType.equals(DamageType.PURE)) {
            isPure.setSelected(true);
        }
        if (damageType.equals(DamageType.TRUE)) {
            isTrue.setSelected(true);
        }
        if (strikerUnit != null) {
            striker.setValue(strikerUnit.getName());
        }
        if (defenderUnit != null) {
            defender.setValue(defenderUnit.getName());
        }
        HBox damageTypeBox = new HBox(isPhysical,isMagical,isPure,isTrue);
        unitBox.getChildren().addAll(striker,damageTypeBox, defender, bypass_calculation);
        for (Unit unit :  combatFlow.getAllUnit().values()) {
            striker.getItems().add(unit.getName());
            defender.getItems().add(unit.getName());
        }
        for (CheckBox box : damageTypeList) {
            box.setOnAction(e-> {
                if (box.isSelected()) {
                    for (CheckBox toUnselect : damageTypeList) {
                        toUnselect.setSelected(false);
                    }
                    box.setSelected(true);
                    isBypassDebris = false;
                    if (box == isPhysical) {
                        damageType = DamageType.PHYSICAL;
                    } else if (box == isMagical) {
                        damageType = DamageType.MAGICAL;
                    } else if (box == isPure) {
                        damageType = DamageType.PURE;
                    } else if (box == isTrue) {
                        damageType = DamageType.TRUE;
                        isBypassDebris = true;
                    }

                } else if (box == isPhysical) {
                    isMagical.setSelected(true);
                    damageType = DamageType.MAGICAL;
                    isBypassDebris = false;
                } else {
                    isPhysical.setSelected(true);
                    damageType = DamageType.PHYSICAL;
                    isBypassDebris = false;
                }
                calculateToWrite();
                refreshCalculationBlock();
            });
        }
        striker.setOnAction(e-> {
            strikerUnit = combatFlow.getAllUnit().get(striker.getValue());
            calculateToWrite();
            refreshCalculationBlock();
        });
        defender.setOnAction(e-> {
            defenderUnit = combatFlow.getAllUnit().get(defender.getValue());
            calculateToWrite();
            refreshCalculationBlock();
        });

        //damageBox
        Label toWriteLabel = new Label("Damage");
        Label addiDefLabel = new Label("Additional Def");
        Label finalLabel = new Label("Final Damage");
        TextField toWrite = new TextField(damageToWrite);
        TextField addiDef = new TextField(additionalDef);
        toWrite.setOnKeyReleased(e-> {
            damageToWrite = toWrite.getText();
            if (e.getCode() == KeyCode.ENTER) {
                double number = FormulaUtils.evaluateFormula(damageToWrite);
                damageToWrite = df.format(number);
                calculateToWrite();
                refreshCalculationBlock();
            }
        });
        addiDef.setOnKeyReleased(e-> {
            additionalDef = addiDef.getText();
            if (e.getCode() == KeyCode.ENTER) {
                double number = FormulaUtils.evaluateFormula(additionalDef);
                additionalDef = df.format(number);
                calculateToWrite();
                refreshCalculationBlock();
            }
        });
        TextField toSend = new TextField(df.format(finalDmgToSend));
        toSend.setOnKeyReleased(e-> {
            finalDmgToSend = FormulaUtils.evaluateFormula(toSend.getText());
        });
        CheckBox bypassDebris = new CheckBox("Bypass Debris");
        bypassDebris.setSelected(isBypassDebris);
        bypassDebris.setOnAction(e-> {
            isBypassDebris = bypassDebris.isSelected();
        });
        Button send = new Button("Deal Damage");
        send.setOnAction(e-> {
            ActionEffectType type = ActionEffectType.DAMAGE_PHYSICAL;
            switch (damageType) {
                case PHYSICAL -> type = ActionEffectType.DAMAGE_PHYSICAL;
                case MAGICAL -> type = ActionEffectType.DAMAGE_MAGICAL;
                case PURE -> type = ActionEffectType.DAMAGE_PURE;
                case TRUE -> type = ActionEffectType.DAMAGE_TRUE;
            }
            ResourceEvent event = new ResourceEvent("???", strikerUnit, defenderUnit, finalDmgToSend, type);
            combatFlow.getEventBus().post(event, EventPhase.PRE);
            combatFlow.getEventBus().post(event, EventPhase.MODIFY);
            calculateDamageSend(event);
            combatFlow.getEventBus().post(event, EventPhase.POST);
            refreshCol2();
            refreshCalculationBlock();
        });
        Region spacer = new Region();
        damageBox.getChildren().addAll(toWriteLabel,toWrite, addiDefLabel, addiDef, finalLabel, toSend, bypassDebris, send, spacer);


        Label event_damage_label = new Label("Send Damage");
        TextField event_damage_field = new TextField("");

        Label event_heal_label = new Label("Send Recover");
        TextField event_heal_field = new TextField("");

        Label event_debris_label = new Label("Send Debris");
        TextField event_debris_field = new TextField("");

        CheckBox attack = new CheckBox("Attack");
        CheckBox strike = new CheckBox("Strike");
        CheckBox cast = new CheckBox("Cast");
        CheckBox physical_event = new CheckBox("PHYSICAL");
        CheckBox magical_event = new CheckBox("MAGICAL");
        CheckBox pure_event = new CheckBox("PURE");
        CheckBox true_event = new CheckBox("TRUE");
        CheckBox health_recover = new CheckBox("Health Recover");
        CheckBox mana_recover = new CheckBox("Mana Recover");
        CheckBox heal = new CheckBox("Heal");
        CheckBox create_debris = new CheckBox("Create Debris");
        CheckBox skill_trigger = new CheckBox("Skill Trigger");

        Button event_send = new Button("Event Send");
        event_send.setOnAction(e-> {
            ActionEvent.Builder action_event = ActionEvent.builder("???", strikerUnit, defenderUnit);
            if (attack.isSelected()) {
                action_event.addActType(ActType.ATTACK);
            }
            if (strike.isSelected()) {
                action_event.addActType(ActType.STRIKE);
            }
            if (cast.isSelected()) {
                action_event.addActType(ActType.CAST);
            }
            if (heal.isSelected()) {
                action_event.addActType(ActType.HEAL);
            }
            if (health_recover.isSelected()) {
                action_event.addActType(ActType.HEALTH_RECOVER);
                action_event.effect(ActionEffectType.HEALTH_RECOVER, Double.parseDouble(event_heal_field.getText()) , 1);
            }
            if (mana_recover.isSelected()) {
                action_event.addActType(ActType.MANA_RECOVER);
                action_event.effect(ActionEffectType.MANA_RECOVER, Double.parseDouble(event_heal_field.getText()) , 1);
            }
            if (create_debris.isSelected()) {
                action_event.addActType(ActType.CREATE_DEBRIS);
                action_event.effect(ActionEffectType.CREATE_DEBRIS, Double.parseDouble(event_debris_field.getText()), 1);
            }
            if (physical_event.isSelected()) {
                action_event.effect(ActionEffectType.DAMAGE_PHYSICAL, Double.parseDouble(event_damage_field.getText()), 1);
            }
            if (magical_event.isSelected()) {
                action_event.effect(ActionEffectType.DAMAGE_MAGICAL, Double.parseDouble(event_damage_field.getText()), 1);
            }
            if (pure_event.isSelected()) {
                action_event.effect(ActionEffectType.DAMAGE_PURE, Double.parseDouble(event_damage_field.getText()), 1);
            }
            if (true_event.isSelected()) {
                action_event.effect(ActionEffectType.DAMAGE_TRUE, Double.parseDouble(event_damage_field.getText()), 1);
            }

            Skill.sendActionEvent(combatFlow.getEventBus(), action_event.build());
        });
        damageBox.getChildren().addAll(event_damage_label,event_damage_field, attack, strike, cast, physical_event, magical_event, pure_event, true_event,
                event_heal_label, event_heal_field, health_recover, mana_recover, heal,
                event_debris_label, event_debris_field,create_debris, skill_trigger, event_send);


        //chanceBox
        CheckBox isCrit = new CheckBox("Crit");
        Label addiCritLabel = new Label("Add Flat");
        Label addiCritMultLabel = new Label("Add Mult");
        TextField addiCrit = new TextField(additionalCrit);
        TextField addiCritMult = new TextField(additionalCritMult);
        addiCrit.setOnKeyReleased(e-> {
            additionalCrit = addiCrit.getText();
            if (e.getCode() == KeyCode.ENTER) {
                refreshCalculationBlock();
            }
        });
        addiCritMult.setOnKeyReleased(e-> {
            additionalCritMult = addiCritMult.getText();
            if (e.getCode() == KeyCode.ENTER) {
                refreshCalculationBlock();
            }
        });
        Label critChance = new Label(df.format(calculateCrit())+"%");
        VBox critBox = new VBox(isCrit,addiCritLabel, addiCrit, addiCritMultLabel, addiCritMult, critChance);
        critBox.setAlignment(Pos.CENTER);
        CheckBox isDodge = new CheckBox("Dodge");
        Label addiEvaLabel = new Label("Add Flat");
        Label addiEvaMultLabel = new Label("Add Mult");
        TextField addiEva = new TextField(additionalEva);
        TextField addiEvaMult = new TextField(additionalEvaMult);
        addiEva.setOnKeyReleased(e-> {
            additionalEva = addiEva.getText();
            if (e.getCode() == KeyCode.ENTER) {
                refreshCalculationBlock();
            }
        });
        addiEvaMult.setOnKeyReleased(e-> {
            additionalEvaMult = addiEvaMult.getText();
            if (e.getCode() == KeyCode.ENTER) {
                refreshCalculationBlock();
            }
        });
        CheckBox isGlance = new CheckBox("Glancing");
        Label dodgeChance = new Label(df.format(calculateEvasion())+"%");
        VBox dodgeBox = new VBox(isDodge, addiEvaLabel, addiEva, addiEvaMultLabel ,addiEvaMult, dodgeChance, isGlance);
        dodgeBox.setAlignment(Pos.CENTER);
        CheckBox isBlock = new CheckBox("Block");
        Label addiBlockLabel = new Label("Add Flat");
        Label addiBlockMultLabel = new Label("Add Mult");
        TextField addiBlock = new TextField(additionalBlock);
        TextField addiBlockMult = new TextField(additionalBlockMult);
        addiBlock.setOnKeyReleased(e-> {
            additionalBlock = addiBlock.getText();
            if (e.getCode() == KeyCode.ENTER) {
                refreshCalculationBlock();
            }
        });
        addiBlockMult.setOnKeyReleased(e-> {
            additionalBlockMult = addiBlockMult.getText();
            if (e.getCode() == KeyCode.ENTER) {
                refreshCalculationBlock();
            }
        });
        Label blockChance = new Label(df.format(calculateBlock())+"%");
        VBox blockBox = new VBox(isBlock,addiBlockLabel,addiBlock , addiBlockMultLabel, addiBlockMult, blockChance);
        blockBox.setAlignment(Pos.CENTER);
        addiCrit.setPrefWidth(70);
        addiCritMult.setPrefWidth(70);
        addiEva.setPrefWidth(70);
        addiEvaMult.setPrefWidth(70);
        addiBlock.setPrefWidth(70);
        addiBlockMult.setPrefWidth(70);

        Label diceLabel = new Label("Dices");
        Button randomAll = new Button("Random");
        Label extraLabel = new Label("Extra Dice");
        Button extraRandom = new Button("Random");
        Label extraDice = new Label(Integer.toString(combatFlow.getExtraDice()));
        VBox extraDiceBox = new VBox(extraLabel, extraRandom, extraDice);
        Label critLabel = new Label("Crit Dice");
        Button critRandom = new Button("Random");
        Label critDice = new Label(Integer.toString(combatFlow.getCritDice()));
        VBox critDiceBox = new VBox(critLabel, critRandom, critDice);
        Label avoidLabel = new Label("Avoid Dice");
        Button avoidRandom = new Button("Random");
        Label avoidDice = new Label(Integer.toString(combatFlow.getAvoidDice()));
        VBox avoidDiceBox = new VBox(avoidLabel, avoidRandom, avoidDice);
        randomAll.setOnAction(e-> {
            combatFlow.randAllDices();
            refreshCalculationBlock();
        });
        extraRandom.setOnAction(e-> {
            combatFlow.randExtraDice();
            refreshCalculationBlock();
        });
        critRandom.setOnAction(e-> {
            combatFlow.randCritDice();
            refreshCalculationBlock();
        });
        avoidRandom.setOnAction(e-> {
            combatFlow.randAvoidDice();
            refreshCalculationBlock();
        });
        extraDiceBox.setAlignment(Pos.CENTER);
        critDiceBox.setAlignment(Pos.CENTER);
        avoidDiceBox.setAlignment(Pos.CENTER);
        HBox diceBox = new HBox(extraDiceBox, critDiceBox, avoidDiceBox);
        HBox topChanceBox = new HBox(critBox, dodgeBox, blockBox);
        topChanceBox.setAlignment(Pos.CENTER);
        VBox bottomChanceBox = new VBox(diceLabel, randomAll, diceBox);
        bottomChanceBox.setAlignment(Pos.CENTER);
        chanceBox.getChildren().addAll(topChanceBox, bottomChanceBox);
        if (calculateCrit() >= combatFlow.getCritDice()) {
            isCrit.setSelected(true);
        }
        if (calculateEvasion()+10 >= combatFlow.getAvoidDice()) {
            isGlance.setSelected(true);
        }
        if (calculateEvasion() >= combatFlow.getAvoidDice()) {
            isDodge.setSelected(true);
            isGlance.setSelected(false);
        }
        if (calculateBlock() >= combatFlow.getAvoidDice()) {
            isBlock.setSelected(true);
        }

        calcBox.getChildren().addAll(unitBox,damageBox,chanceBox,fieldBox);
        return calcBox;
    }
    
    public void calculateDamageSend(ResourceEvent event) {
        LogWriterUtil.log(event.target.getName()+" took "+df.format(event.amount)+" damage",combatFlow.getTurnCount());
        if (event.target instanceof Monster) {
            combatFlow.getDamageTaken().merge(event.target.getName(), event.amount, Double::sum);
        }
        double oldHealth = event.target.getHealth().getRemaining();
        if (event.bypassDebris) {
            event.target.sumRemainingHealth(-1 * event.amount);
            double newHealth = event.target.getHealth().getRemaining();
            LogWriterUtil.log("Unit: "+event.target.getName()+" | Resource: HEALTH | Remaining: "+oldHealth+" -> "+newHealth+" (Lost: "+ (oldHealth-newHealth) + ")");
            refreshCol2();
            refreshCalculationBlock();
            return;
        }
        double debris = event.target.getDebris().getRemaining();
        double remainingDamage = -1 * event.amount;
        if (debris > 0) {
            if (Math.abs(remainingDamage) > debris) {
                // ดาเมจเกิน debris → ลบ debris ทั้งหมด แล้วเหลือดาเมจ
                remainingDamage += debris;
                event.target.getDebris().setRemaining(0);
                double newDebris = event.target.getDebris().getRemaining();
                LogWriterUtil.log("Unit: "+event.target.getName()+" | Resource: DEBRIS | Remaining: "+debris+" -> "+newDebris+" (Lost: "+ (debris-newDebris) + ")");
            } else {
                // ดาเมจไม่เกิน → ลบออกจาก debris แล้วจบ
                event.target.getDebris().sumRemaining(remainingDamage);
                double difference = debris + remainingDamage;
                remainingDamage = 0;
                double newDebris = event.target.getDebris().getRemaining();
                LogWriterUtil.log("Unit: "+event.target.getName()+" | Resource: DEBRIS | Remaining: "+debris+" -> "+newDebris+" (Lost: "+ (debris-newDebris) + ")");
            }
        }
        if (remainingDamage < 0) {
            event.target.sumRemainingHealth(remainingDamage);
            double newHealth = event.target.getHealth().getRemaining();
            LogWriterUtil.log("Unit: "+event.target.getName()+" | Resource: HEALTH | Remaining: "+oldHealth+" -> "+newHealth+" (Lost: "+ (oldHealth-newHealth) + ")");
        }
    }

    public double calculateCrit() {
        double critChance = 0;
        double critShield = 0;
        if (strikerUnit != null) {
            critChance = strikerUnit.getStats().get(StatType.CRITCHANCE).getFinal();
            critChance += FormulaUtils.evaluateFormula(additionalCrit);
            critChance *= (1+FormulaUtils.evaluateFormula(additionalCritMult));
        }
        if (defenderUnit != null) {
            critShield = defenderUnit.getStats().get(StatType.CRITSHIELD).getFinal();
        }
        return ( critChance - critShield )*100;
    }

    public double calculateEvasion() {
        double evasion = 0;
        double accuracy = 0;
        if (defenderUnit != null) {
            evasion = defenderUnit.getStats().get(StatType.EVASION).getFinal();
            evasion += FormulaUtils.evaluateFormula(additionalEva);
            evasion *= (1+FormulaUtils.evaluateFormula(additionalEvaMult));
        }
        if (strikerUnit != null) {
            accuracy = strikerUnit.getStats().get(StatType.ACCURACY).getFinal();
        }
        return (  1-(1.25*accuracy/(accuracy+Math.pow(evasion*0.82,0.9)))  )*100;
    }

    public double calculateBlock() {
        double block = 0;
        double damage = finalDmgToSend;
        if (defenderUnit != null) {
            if (damageType.equals(DamageType.PHYSICAL)) {
                block = defenderUnit.getStats().get(StatType.PHYSICALBLOCK).getFinal();
            } else if (damageType.equals(DamageType.MAGICAL)) {
                block = defenderUnit.getStats().get(StatType.MAGICALBLOCK).getFinal();
            } else {
                block = defenderUnit.getStats().get(StatType.MAGICALBLOCK).getFinal()/2 + defenderUnit.getStats().get(StatType.PHYSICALBLOCK).getFinal()/2;
            }
            block += FormulaUtils.evaluateFormula(additionalBlock);
            block *= (1+FormulaUtils.evaluateFormula(additionalBlockMult));
        }

        return (  0.5 + (block - damage) / (2 * (block + damage))  )*100;
    }

    public void calculateToWrite() {
        AtomicDouble level_sum = new AtomicDouble(0);
        AtomicInteger player_count = new AtomicInteger(0);
        combatFlow.getPlayerUnit().forEach((key, value) -> {
            level_sum.addAndGet(value.getLevel());
            player_count.incrementAndGet();
        });
        double level_avg = level_sum.get() / player_count.get();
        double rawDamage = FormulaUtils.evaluateFormula(damageToWrite);
        double pdef= 0;
        double mdef= 0;
        double dmgred= 0;
        double ppen= 0;
        double mpen= 0;
        double dmgamp= 0;
        if (strikerUnit != null) {
            ppen = strikerUnit.getStats().get(StatType.PHYSICALPENETRATE).getFinal();
            mpen = strikerUnit.getStats().get(StatType.MAGICALPENETRATE).getFinal();
            dmgamp = strikerUnit.getStats().get(StatType.DAMAGEAMPLIFIER).getFinal();
        }
        Boolean isRainfall = fieldSetting.get("Rainfall");
        if (isRainfall != null && isRainfall) {
            ppen = combatFlow.getAllUnit().get("Akivili").getStats().get(StatType.PHYSICALPENETRATE).getFinal();
        }
        if (defenderUnit != null) {
            pdef = defenderUnit.getStats().get(StatType.PHYSICALDEFENSE).getFinal();
            mdef = defenderUnit.getStats().get(StatType.MAGICALDEFENSE).getFinal();
            dmgred = defenderUnit.getStats().get(StatType.DAMAGEREDUCTION).getFinal();
        }
        pdef -= ppen;
        mdef -= mpen;
        if (damageType.equals(DamageType.TRUE)) {
            dmgred = 0;
        }
        rawDamage *= (1+dmgamp);
        rawDamage *= (1-dmgred);
        if (defenderUnit != null && defenderUnit.hasCondition("Tremble")) {
            rawDamage *= 1.25;
        }
        double finaldmg = 0;
        double extraDef = FormulaUtils.evaluateFormula(additionalDef);
        double k = 100+(10*level_avg);
        double clampedPDef = Math.max(pdef, -k + 1e-6);
        double clampedMDef = Math.max(mdef, -k + 1e-6);
        double multiplier_physical = k/(k+clampedPDef);
        double multiplier_magical = k/(k+clampedMDef);
        multiplier_physical = Math.min(multiplier_physical, 2.0);
        multiplier_magical = Math.min(multiplier_magical, 2.0);
        if (damageType.equals(DamageType.PHYSICAL)) {
            finaldmg = rawDamage*multiplier_physical;
        } else if (damageType.equals(DamageType.MAGICAL)) {
            finaldmg = rawDamage*multiplier_magical;
        } else if (Double.isNaN(rawDamage) || rawDamage == 0) {
            finaldmg = 0;
        } else {
            finaldmg = Math.pow(rawDamage, 2) / rawDamage;
        }
        finalDmgToSend = finaldmg;
        if (bypass_calculation.isSelected()) {
            finalDmgToSend = rawDamage;
        }
    }

    public Node toolBlock() {
        VBox toolBox = new VBox();
        VBox skillUseArea = new VBox();
        HBox skillUseBox = new HBox();
        HBox advancedBox = new HBox();
        skillUser.setPrefWidth(150);
        skillName.setPrefWidth(150);
        Button skillUse = new Button("Use");
        CheckBox bypass_cost = new CheckBox("Bypass Cost");
        CheckBox bypass_cooldown = new CheckBox("Bypass Cooldown");
        CheckBox bypass_skill_use_event = new CheckBox("Bypass SkillUse Event");
        for (Unit unit : combatFlow.getAllUnit().values()) {
            if (skillUser.getItems().contains(unit.getName())) continue;
            skillUser.getItems().add(unit.getName());
        }
        skillUser.setOnAction(e-> {
            Unit user = combatFlow.getAllUnit().get(skillUser.getValue());
            if (user == null) return;
            skillName.getItems().clear();
            for (SkillInstance instance : user.getAllSkill().values()) {
                skillName.getItems().add(instance.getSkillData().getName());
            }

            refreshCol1();
        });
        skillName.setOnAction(e -> {
            Unit user = combatFlow.getAllUnit().get(skillUser.getValue());
            skill_target_to_send_back = new SkillTarget();
            target_count = new LinkedHashMap<>();

            if (user == null) return;
            for (SkillInstance instance : user.getAllSkill().values()) {
                if (instance.getSkillData().getName().equals(skillName.getValue())) {
                    SkillInputSpec spec = instance.getSkillData().getInputSpec(combatFlow);
                    if (!spec.targets.isEmpty()) {
                        for (Map.Entry<Integer, List<String>> entry : spec.targets.entrySet()) {
                            target_count.put(entry.getKey(), 1);
                        }
                    }
                }
            }

            refreshCol1();
        });
        skillUseArea.getChildren().addAll(skillUse, bypass_cooldown, bypass_cost, bypass_skill_use_event);
        skillUseBox.getChildren().addAll(skillUser,skillName,skillUseArea);
        skillUse.setOnAction(e-> {
            Unit user = combatFlow.getAllUnit().get(skillUser.getValue());
            if (user == null) return;
            SkillInstance si = user.getAllSkill().get(skillName.getValue());
            if (si == null) return;
            if (si.isReserving()) {
                Skill skill = si.getSkillData();
                double health_cost = skill.getHealthCost();
                double mana_cost = skill.getManaCost();
                int cooldown = skill.getCooldown();
                if (bypass_cost.isSelected()) {
                    health_cost = 0;
                    mana_cost = 0;
                }
                if (bypass_cooldown.isSelected()) {
                    cooldown = 0;
                }
                skill.use(combatFlow, skill_target_to_send_back, mana_cost, health_cost, cooldown);

                if (!bypass_skill_use_event.isSelected()) {
                    combatFlow.getEventBus().post(new SkillUse(user, si, skill.getMultiplierTags(), health_cost, mana_cost));
                }
            } else {
                LogWriterUtil.log("Skill "+si.getSkillData().getName()+" is not active", combatFlow.getTurnCount());
            }
        });
        Button startTurn = new Button("Start Turn");
        startTurn.setOnAction(e-> {
            Unit selectedUnit = turnList.getSelectionModel().getSelectedItem();
            if (selectedUnit == null) return;
            double manaRegen = selectedUnit.getStats().get(StatType.MANAREGEN).getFinal();
            double healthRegen = selectedUnit.getStats().get(StatType.HEALTHREGEN).getFinal();
            LogWriterUtil.log(selectedUnit.getName()+"'s turn has started! gain "+manaRegen+" mana from ManaRegen", combatFlow.getTurnCount());
            selectedUnit.sumRemainingMana(manaRegen);
            if (healthRegen != 0) {
                LogWriterUtil.log("> and "+healthRegen+" health from HealthRegen",combatFlow.getTurnCount());
                selectedUnit.sumRemainingHealth(healthRegen);
            }
            AsyncUtil.runAsync(writeToSheet());
            combatFlow.oneTurnPassed();
        });
        Button focus = new Button("Focus");
        focus.setOnAction(e-> {
            Unit selectedUnit = turnList.getSelectionModel().getSelectedItem();
            if (selectedUnit == null) return;
            double manaRegen = selectedUnit.getStats().get(StatType.MANAREGEN).getFinal();
            LogWriterUtil.log(selectedUnit.getName()+" has used focus! gain "+manaRegen+" mana from ManaRegen", combatFlow.getTurnCount());
            selectedUnit.sumRemainingMana(manaRegen);
        });
        Button writeToSheet = new Button("Write to Sheet");
        writeToSheet.setOnAction(e-> {
            AsyncUtil.runAsync(writeToSheet());
        });
        Button resetFlow = new Button("Reset Combat Flow");
        resetFlow.setOnAction(e-> {
            combatFlow.resetCombatFlow();
            AsyncUtil.runAsync(writeToSheet());
            refreshContent(true);
        });
        Button allUnitUpdate = new Button("All Unit Update");
        allUnitUpdate.setOnAction(e-> {
            combatFlow.allUnitUpdate();
            refreshContent(false);
        });
        Button oneRoundPass = new Button("One Round Pass");
        Button oneRoundRewind = new Button("One Round <<Rewind>>");
        oneRoundPass.setOnAction(e-> {
            combatFlow.oneRoundPassed();
            AsyncUtil.runAsync(writeToSheet());
            refreshContent(true);
        });
        oneRoundRewind.setOnAction(e-> {
            combatFlow.oneRoundRewind();
            AsyncUtil.runAsync(writeToSheet());
            refreshContent(true);
        });
        Region spacer1 = new Region();
        spacer1.setPrefHeight(100);
        advancedBox.getChildren().addAll(writeToSheet, resetFlow, allUnitUpdate);

        VBox party_box = new VBox();
        combatFlow.getParties().forEach((party_number, members) -> {
            CheckBox checkBox = new CheckBox("Party "+party_number.toString());
            Unit selectedUnit = turnList.getSelectionModel().getSelectedItem();
            if (selectedUnit == null) {
                checkBox.selectedProperty().set(false);
            } else if (members.contains(selectedUnit.getName())) {
                checkBox.selectedProperty().set(true);
            } else {
                checkBox.selectedProperty().set(false);
            }
            checkBox.setOnAction(e->{
                if (selectedUnit == null) return;
                if (checkBox.selectedProperty().get()) {
                    combatFlow.getParties().get(party_number).add(selectedUnit.getName());
                } else {
                    combatFlow.getParties().get(party_number).remove(selectedUnit.getName());
                }
            });
            party_box.getChildren().add(checkBox);
        });

        Button add_party = new Button("Add a Party");
        add_party.setOnAction(e-> {
            combatFlow.getParties().remove(0);
            combatFlow.getParties().put(1, new ArrayList<>());
            combatFlow.getParties().put(2, new ArrayList<>());
        });
        party_box.getChildren().add(add_party);
        toolBox.getChildren().addAll(advancedBox,skillUseBox, createSkillInput(), skillUseArea, startTurn, focus, spacer1, oneRoundPass, oneRoundRewind, party_box);
        return toolBox;
    }

    public Node createSkillInput() {
        VBox box = new VBox();

        Unit user = combatFlow.getAllUnit().get(skillUser.getValue());
        if (user == null) return box;
        for (SkillInstance instance : user.getAllSkill().values()) {
            if (instance.getSkillData().getName().equals(skillName.getValue())) {
                for (Map.Entry<Integer, Integer> target_count_entry : target_count.entrySet()) {
                    VBox target_box = new VBox();
                    Button add_target = new Button("Add Target");
                    Integer target_key = target_count_entry.getKey();

                    add_target.setOnAction(e -> {
                        target_count.compute(target_key, (k, v) -> v == null ? 1 : v + 1);
                        refreshCol1();
                    });

                    SkillInputSpec spec = instance.getSkillData().getInputSpec(combatFlow);
                        List<String> list_of_targets = spec.getTargets(target_key);
                        if (spec.getTargets(target_key) == null) {
                            list_of_targets = new ArrayList<>();
                        }
                        for (int i=0;i<target_count_entry.getValue();i++) {
                            int target_index = i;
                            HBox line = new HBox();
                            ComboBox<String> target = new ComboBox<>();
                            target.getItems().addAll(list_of_targets);
                            target.setOnAction(e -> {
                                skill_target_to_send_back.addTarget(target.getValue(), target_key, target_index);
                            });
                            line.getChildren().add(target);
                            if (spec.getFields().get(target_key) != null) {
                                for (Map.Entry<Integer, SkillInputSpec.InputField<?>> field : spec.getFields().get(target_key).entrySet()) {
                                    int input_key = field.getKey();
                                    HBox additional_field = new HBox();
                                    if (field.getValue().getType(input_key).equals(SkillInputSpec.InputType.SELECT)) {
                                        Label label = new Label(field.getValue().getName(input_key));
                                        ComboBox<String> field_combo = new ComboBox<>();
                                        renderComboBox(field.getValue(), field_combo, input_key);
                                        additional_field.getChildren().addAll(label, field_combo);
                                        //เก็บค่ากลับ
                                        target.setOnAction(e -> {
                                            skill_target_to_send_back.addTargetAndDecision(target.getValue(), field_combo.getValue(), target_key, input_key, target_index);
                                        });
                                        field_combo.setOnAction(e -> {
                                            skill_target_to_send_back.addTargetAndDecision(target.getValue(), field_combo.getValue(), target_key, input_key, target_index);
                                        });

                                    }
                                    if (field.getValue().getType(input_key).equals(SkillInputSpec.InputType.NUMBER)) {
                                        Label label = new Label(field.getValue().getName(input_key));
                                        TextArea field_text = new TextArea();
                                        additional_field.getChildren().addAll(label, field_text);
                                        //เก็บค่ากลับ
                                        target.setOnAction(e -> {
                                            skill_target_to_send_back.addTargetAndDecision(target.getValue(), field_text.getText(), target_key, input_key, target_index);
                                        });
                                        field_text.setOnKeyReleased(e -> {
                                            skill_target_to_send_back.addTargetAndDecision(target.getValue(), field_text.getText(), target_key, input_key, target_index);
                                        });
                                    }
                                    if (field.getValue().getType(input_key).equals(SkillInputSpec.InputType.BOOLEAN)) {
                                        CheckBox field_check_box = new CheckBox(field.getValue().getName(input_key));
                                        additional_field.getChildren().addAll(field_check_box);
                                        //เก็บค่ากลับ
                                        target.setOnAction(e -> {
                                            String isSelected;
                                            if (field_check_box.isSelected()) {
                                                isSelected = "TRUE";
                                            } else {
                                                isSelected = "FALSE";
                                            }
                                            skill_target_to_send_back.addTargetAndDecision(target.getValue(), isSelected, target_key, input_key, target_index);
                                        });
                                        field_check_box.setOnAction(e -> {
                                            String isSelected;
                                            if (field_check_box.isSelected()) {
                                                isSelected = "TRUE";
                                            } else {
                                                isSelected = "FALSE";
                                            }
                                            skill_target_to_send_back.addTargetAndDecision(target.getValue(), isSelected, target_key, input_key, target_index);
                                        });
                                    }

                                    line.getChildren().add(additional_field);
                                }
                            }
                            target_box.getChildren().add(line);
                        }
                        target_box.getChildren().add(add_target);
                        box.getChildren().add(target_box);
                }
            }
        }

        return box;
    }

    public <T> void renderComboBox(SkillInputSpec.InputField<T> inputField, ComboBox<String> field_combo, int input_index) {
        for (T option : inputField.options.get(input_index)) {
            String label = inputField.labelProvider.get(input_index).apply(option);
            field_combo.getItems().add(label);
        }
    }

    public Node importBlock(List<Unit> unitList) {
        
        HBox importBox = new HBox();
        VBox nameBox = new VBox();
        nameBox.setAlignment(Pos.CENTER_LEFT);
        ComboBox<String> name = new ComboBox<>();
        name.setPrefWidth(200);
        for (Unit unit : unitList) {
            name.getItems().add(unit.getName());
        }
        VBox resourceBox = new VBox();
        HBox statBox = new HBox();
        VBox conditionBox = new VBox();
        VBox skillBox = new VBox();
        name.setOnAction(e-> {
            Unit unit = combatFlow.getAllUnit().get(name.getValue());
            nameBox.getChildren().clear();
            resourceBox.getChildren().clear();
            conditionBox.getChildren().clear();
            skillBox.getChildren().clear();
            statBox.getChildren().clear();
            if (unit == null) {
                nameBox.getChildren().add(name);
                return;
            }
            if (unit.isPlayer() || unit.isNpc()) {
                combatFlow.setSelectedPlayer(unit.getName());
            } else if (unit.isMonster() || unit.isSummon()) {
                combatFlow.setSelectedMonster(unit.getName());
            }
            nameBox.getChildren().addAll(name);
            for(ResourceType resourceType : ResourceType.values()) {
                VBox resourceRow = new VBox();
                resourceRow.setMinWidth(100);
                resourceRow.setMinHeight(100);
                double usable = unit.getResources().get(resourceType).getUsable();
                double reservedPercent = unit.getResources().get(resourceType).getReservedPercent();
                double reservedFlat = unit.getResources().get(resourceType).getReservedFlat();
                double remaining = unit.getResources().get(resourceType).getRemaining();
                Label maxLabel = new Label("Max "+ resourceType.writeAsString() + ": "+ usable);
                Label reservedLabel = new Label("Reserved: " + df.format(reservedPercent*100) + "%, Flat: "+ df.format(reservedFlat));
                reservedLabel.setStyle("-fx-font-size: 16px");
                TextField remainingField = new TextField();
                if (resourceType == ResourceType.HEALTH) {
                    maxLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: crimson;");
                }
                if (resourceType == ResourceType.MANA) {
                    maxLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: dodgerblue;");
                }
                if (resourceType == ResourceType.DEBRIS) {
                    maxLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: goldenrod;");
                }
                remainingField.setText(df.format(remaining));
                remainingField.setOnKeyReleased(ev -> {
                    if (ev.getCode() == KeyCode.ENTER) {
                        double oldRemaining = unit.getResources().get(resourceType).getRemaining();
                        double toSendBack = FormulaUtils.evaluateFormula(remainingField.getText());
                        System.out.printf("[LOG] Unit: %s | Resource: %s | Remaining: %.2f -> %.2f (Change: %.2f)%n",
                                unit.getName(),
                                resourceType.name(),
                                oldRemaining,
                                toSendBack,
                                toSendBack - oldRemaining
                        );
                        if (resourceType == ResourceType.HEALTH) {
                            unit.setRemainingHealth(toSendBack);
                        }
                        if (resourceType == ResourceType.MANA) {
                            unit.setRemainingMana(toSendBack);
                        }
                        if (resourceType == ResourceType.DEBRIS) {
                            unit.setRemainingDebris(toSendBack);
                        }
                        remainingField.setText(df.format(unit.getResources().get(resourceType).getRemaining()));
                    }
                });
                resourceRow.getChildren().addAll(maxLabel, reservedLabel, remainingField);
                resourceBox.getChildren().add(resourceRow);
            }
            Label conditionInd = new Label("Condition Name");
            Label conditionTurnInd = new Label("Turn");
            Region conditionDelSpacer = new Region();
            conditionDelSpacer.setPrefWidth(80);
            conditionInd.setPrefWidth(150);
            conditionTurnInd.setPrefWidth(50);
            HBox conditionIndRow = new HBox(conditionInd, conditionTurnInd);
            conditionBox.getChildren().add(conditionIndRow);
            for (ConditionInstance instance : unit.getConditionInstances().values()) {
                if (instance.getCondition() == null) continue;
                HBox conditionRow = new HBox();
                Label conditionName = new Label(instance.getCondition().getName());
                Label conditionTurn = new Label(df.format(instance.getDurationRemain()));
                conditionName.setPrefWidth(150);
                conditionTurn.setPrefWidth(50);
                Button removeCondition = new Button("Remove");
                removeCondition.setPrefWidth(80);
                removeCondition.setOnAction(event -> {
                    ConditionManager.removeOneCondition(unit, instance.getCondition().getName());
                    name.setValue("");
                    name.setValue(unit.getName());
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
                    name.setValue("");
                    name.setValue(unit.getName());
                } else if (condition != null) {
                    ConditionManager.applyCondition(condition, unit, Integer.parseInt(turnField.getText()));
                    name.setValue("");
                    name.setValue(unit.getName());
                } else {
                    System.out.println("Condition Not Found");
                }
            });

            SearchableListView.makeSearchable(itemListView, FXCollections.observableArrayList(itemNameList), conditionNameField);
            itemListView.setOnMouseClicked(ev -> {
                String selectedItem = itemListView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    choosePopup.hide();
                    conditionNameField.setText(selectedItem);
                    conditionBox.requestFocus();
                }
            });
            itemListView.setOnKeyReleased(ev -> {
                if (ev.getCode() == KeyCode.ENTER || ev.getCode() == KeyCode.ESCAPE) {
                    String selectedItem = itemListView.getSelectionModel().getSelectedItem();
                    choosePopup.hide();
                    conditionNameField.setText(selectedItem);
                    conditionBox.requestFocus();
                    e.consume();
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
                    e.consume();
                }
            });
            conditionBox.getChildren().addAll(conditionAddRow,sourceInd,conditionSource);

            for (Map.Entry<String, SkillInstance> entry : unit.getAllSkill().entrySet()) {
                HBox row = new HBox();
                row.setSpacing(5);
                row.setStyle("-fx-border-color: hotpink; -fx-border-width: 0 0 1 0;");
                CheckBox activate = new CheckBox();
                activate.setSelected(entry.getValue().isReserving());
                activate.setOnAction(ev -> {
                    entry.getValue().setReserving(activate.isSelected());
                    unit.calculateEverything();
                    name.setValue("");
                    name.setValue(unit.getName());
                });
                Label skillType = createWrappingLabel(entry.getValue().getSkillData().getTranslatedTag(), 100);
                Label skillName = createWrappingLabel(entry.getValue().getSkillData().getName(), 150);
                Label skillDesc = createWrappingLabel(entry.getValue().getSkillData().getTranslatedDesc(), 400);
                Label skillAct = createWrappingLabel(entry.getValue().getSkillData().getActionType(), 100);
                Label skillCost = createWrappingLabel(entry.getValue().getSkillData().getTranslatedCost(), 100);
                Label skillCooldown = createWrappingLabel(entry.getValue().getSkillData().getTranslatedCooldown(), 100);

                HBox cdBox = new HBox(2);
                Label cdLabel = createLabel(String.valueOf(entry.getValue().getOnCooldown()), 40);
                Button plus = new Button("+");
                Button minus = new Button("-");
                plus.setOnAction(ev -> {
                    entry.getValue().cooldownIncrement();
                    name.setValue("");
                    name.setValue(unit.getName());
                });
                minus.setOnAction(ev -> {
                    entry.getValue().cooldownDecrement();
                    name.setValue("");
                    name.setValue(unit.getName());
                });
                cdBox.getChildren().addAll(cdLabel, plus, minus);
                cdBox.setMinWidth(100);
                cdBox.setMaxWidth(100);
                cdBox.setAlignment(Pos.CENTER_LEFT);

                cdBox.setAlignment(Pos.CENTER_LEFT);
                HBox slotNumBox = new HBox(activate);
                slotNumBox.setAlignment(Pos.CENTER_LEFT);
                HBox skillTypeBox = new HBox(skillType);
                skillTypeBox.setAlignment(Pos.CENTER_LEFT);
                HBox skillNameBox = new HBox(skillName);
                skillNameBox.setAlignment(Pos.CENTER_LEFT);
                HBox skillDescBox = new HBox(skillDesc);
                skillDescBox.setAlignment(Pos.CENTER_LEFT);
                HBox skillActBox = new HBox(skillAct);
                skillActBox.setAlignment(Pos.CENTER_LEFT);
                HBox skillCooldownBox = new HBox(skillCooldown);
                skillCooldownBox.setAlignment(Pos.CENTER_LEFT);
                HBox skillCostBox = new HBox(skillCost);
                skillCostBox.setAlignment(Pos.CENTER_LEFT);

                cdBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: hotpink; -fx-padding: 0");
                slotNumBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: hotpink; -fx-padding: 0");
                skillTypeBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: hotpink; -fx-padding: 0");
                skillNameBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: hotpink; -fx-padding: 0");
                skillDescBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: hotpink; -fx-padding: 0");
                skillActBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: hotpink; -fx-padding: 0");
                skillCooldownBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: hotpink; -fx-padding: 0");
                skillCostBox.setStyle("-fx-border-width: 0 2 0 0; -fx-border-color: hotpink; -fx-padding: 0");

                row.getChildren().addAll(skillTypeBox, skillNameBox, skillDescBox, skillActBox, cdBox, skillCostBox, skillCooldownBox, slotNumBox);
                skillBox.getChildren().add(row);
            }
            //statBox
            VBox indicator = new VBox();
            VBox overall = new VBox();
            VBox indicator2 = new VBox();
            VBox overall2 = new VBox();

            indicator.setStyle("-fx-border-color: orange; -fx-border-width: 0 2 0 0; -fx-padding: 0; -fx-font-size: 20px;");
            overall.setStyle("-fx-border-color: orange; -fx-border-width: 0 2 0 0; -fx-padding: 0; -fx-font-size: 20px;");
            indicator2.setStyle("-fx-border-color: orange; -fx-border-width: 0 2 0 0; -fx-padding: 0; -fx-font-size: 20px;");
            overall2.setStyle("-fx-border-color: orange; -fx-border-width: 0 2 0 0; -fx-padding: 0; -fx-font-size: 20px;");

            indicator.setMinWidth(150);
            indicator.setMinHeight(500);
            overall.setMinWidth(150);
            overall.setMinHeight(500);
            indicator2.setMinWidth(150);
            indicator2.setMinHeight(500);
            overall2.setMinWidth(150);
            overall2.setMinHeight(500);

            Map<StatType, ModValue> stat = unit.getStats();

            Label statlabel = new Label("Stat\n\n\n");
            Label overallLabel = new Label("Overall\n\n\n");
            Label statlabel2 = new Label("Stat\n\n\n");
            Label overallLabel2 = new Label("Overall\n\n\n");

            statlabel.setStyle("-fx-font-size: 20px; -fx-border-width: 0 0 2 0; -fx-border-color: orange; -fx-padding: 15");
            overallLabel.setStyle("-fx-font-size: 20px; -fx-border-width: 0 0 2 0; -fx-border-color: orange; -fx-padding: 15");
            statlabel2.setStyle("-fx-font-size: 20px; -fx-border-width: 0 0 2 0; -fx-border-color: orange; -fx-padding: 15");
            overallLabel2.setStyle("-fx-font-size: 20px; -fx-border-width: 0 0 2 0; -fx-border-color: orange; -fx-padding: 15");

            indicator.getChildren().add(statlabel);
            overall.getChildren().add(overallLabel);
            indicator2.getChildren().add(statlabel2);
            overall2.getChildren().add(overallLabel2);

            statlabel.setMinWidth(150);
            overallLabel.setMinWidth(150);
            statlabel.setMaxHeight(30);
            overallLabel.setMaxHeight(30);
            statlabel2.setMinWidth(150);
            overallLabel2.setMinWidth(150);
            statlabel2.setMaxHeight(30);
            overallLabel2.setMaxHeight(30);

            StatType[] allStats = StatType.values();
                int half = allStats.length / 2;

            for (int i = 0; i < half; i++) {
                Label indicatorToShow = new Label();
                Label overallToShow = new Label();
                indicatorToShow.setText(allStats[i].writeAsString() + "\n\n");
                double statValue = stat.get(allStats[i]).getFinal();
                if (allStats[i] == StatType.CRITCHANCE || allStats[i] == StatType.CRITDAMAGE || allStats[i] == StatType.HEALAMPLIFIER || allStats[i] == StatType.BUFFAMPLIFIER ||
                        allStats[i] == StatType.DEBUFFAMPLIFIER) {
                    overallToShow.setText(String.format("%.2f", statValue*100) + "%\n\n");
                } else {
                    overallToShow.setText(String.format("%.2f", statValue) + "\n\n");
                }

                indicatorToShow.setStyle("-fx-font-size: 20px; -fx-border-width: 0 0 2 0; -fx-border-color: orange; -fx-padding: 0");
                overallToShow.setStyle("-fx-font-size: 20px; -fx-border-width: 0 0 2 0; -fx-border-color: orange; -fx-padding: 0");
                indicatorToShow.setMinWidth(150);
                overallToShow.setMinWidth(150);
                indicatorToShow.setMaxHeight(15);
                overallToShow.setMaxHeight(15);

                indicator.getChildren().add(indicatorToShow);
                overall.getChildren().add(overallToShow);
            }

            for (int i = half; i < allStats.length; i++) {
                Label indicatorToShow = new Label();
                Label overallToShow = new Label();
                indicatorToShow.setText(allStats[i].writeAsString() + "\n\n");
                double statValue = stat.get(allStats[i]).getFinal();
                if (allStats[i] == StatType.DAMAGEAMPLIFIER || allStats[i] == StatType.DAMAGEREDUCTION || allStats[i] == StatType.ATTACKSPEED || allStats[i] == StatType.CASTSPEED ||
                        allStats[i] == StatType.RESERVATION || allStats[i] == StatType.CRITSHIELD) {
                    overallToShow.setText(String.format("%.2f", statValue*100) + "%\n\n");
                } else {
                    overallToShow.setText(String.format("%.2f", statValue) + "\n\n");
                }

                indicatorToShow.setStyle("-fx-font-size: 20px; -fx-border-width: 0 0 2 0; -fx-border-color: orange; -fx-padding: 0");
                overallToShow.setStyle("-fx-font-size: 20px; -fx-border-width: 0 0 2 0; -fx-border-color: orange; -fx-padding: 0");
                indicatorToShow.setMinWidth(150);
                overallToShow.setMinWidth(150);
                indicatorToShow.setMaxHeight(15);
                overallToShow.setMaxHeight(15);

                indicator2.getChildren().add(indicatorToShow);
                overall2.getChildren().add(overallToShow);
            }
            statBox.getChildren().addAll(indicator, overall, indicator2, overall2);
        });
        if (!unitList.isEmpty()) {
            if (!combatFlow.getSelectedPlayer().isEmpty()) {
                if (unitList.get(0).isPlayer() || unitList.get(0).isNpc()) {
                    PauseTransition delay = new PauseTransition(Duration.millis(200));
                    delay.setOnFinished(event -> name.setValue(combatFlow.getSelectedPlayer()));
                    delay.play();
                }
                if (!combatFlow.getSelectedMonster().isEmpty()) {
                    if (unitList.get(0).isMonster() || unitList.get(0).isSummon()) {
                        PauseTransition delay = new PauseTransition(Duration.millis(200));
                        delay.setOnFinished(event -> name.setValue(combatFlow.getSelectedMonster()));
                        delay.play();
                    }
                }
            }
        }
        nameBox.getChildren().add(name);
        importBox.getChildren().addAll(nameBox,resourceBox,conditionBox, statBox,skillBox);
        importBox.setStyle("-fx-background-color: #393939;");
        importBox.setPrefHeight(400);
        importBox.setMinWidth(500);
        return importBox;
    }

    public Node turnList() {
        List<Unit> sorted = new ArrayList<>(combatFlow.getAllUnit().values());
        sorted.sort(Comparator.comparing(Unit::getSpeed).reversed());
        VBox turnBox = new VBox();
        turnList = new ListView<>(FXCollections.observableArrayList(sorted));

        turnList.setMinWidth(480);
        turnList.setMaxWidth(480);
        turnList.setMinHeight(400);

        if (selectedTurnUnit != null) {
            turnList.getSelectionModel().select(selectedTurnUnit);
        }

        turnList.setCellFactory(lv -> new ListCell<Unit>() {
            @Override
            protected void updateItem(Unit unit, boolean empty) {
                super.updateItem(unit, empty);
                if (empty || unit == null) {
                    setGraphic(null);
                } else {

                    // Title
                    Label nameLabel = new Label(unit.getName());
                    nameLabel.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    nameLabel.setMinWidth(150);
                    nameLabel.setMaxWidth(150);
                    nameLabel.setWrapText(true);

                    // Subtitle
                    Label speed = new Label("Speed: "+df.format(unit.getSpeed())+"\nCombined: "+df.format(Math.log(unit.getSpeed() + 1) * (2 / Math.log(4)))+"\nMSPD: "+
                            df.format(unit.getStats().get(StatType.MOVEMENTSPEED).getFinal()));
                    speed.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    speed.setMinWidth(150);
                    speed.setMaxWidth(150);

                    String sb = "HP: " + df.format(unit.getHealth().getRemaining()) + "\n" +
                            "MP: " + df.format(unit.getMana().getRemaining()) + "\n" +
                            "Debris: " + df.format(unit.getDebris().getRemaining());
                    // Description
                    Label resource = new Label(sb);
                    resource.setWrapText(true);
                    resource.setStyle("-fx-font-size: 16; -fx-border-color: #969696; -fx-border-width: 0 0 0 2; -fx-padding: 5px;");
                    resource.setMinWidth(250);
                    resource.setMaxWidth(250);

                    HBox content = new HBox(2, nameLabel, speed, resource);
                    content.setPadding(new Insets(5));

                    setGraphic(content);
                }
            }
        });

        turnList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedTurnUnit = newVal;
                refreshCol1();
            }
        });

        TextField addField = new TextField("Unit");
        HBox addBox = new HBox(5);
        Button addButton = new Button("Add");
        Button removeButton = new Button("Remove");
        CheckBox isCopy = new CheckBox("Copy?");
        isCopy.setVisible(false);
        isCopy.setSelected(false);
        addBox.getChildren().addAll(addField, addButton, isCopy);

        Popup choosePopup = new Popup();
        ListView<String> itemListView = new ListView<>();
        List<String> itemNameList = new ArrayList<>();
        for (Unit unit : combatFlow.getDatabase().getAllUnit().values()) {
            itemNameList.add(unit.getName());
        }

        addButton.setOnAction(e -> {
            String name = addField.getText();
            Unit toAdd = combatFlow.getDatabase().getAllUnit().get(name);
            combatFlow.addUnitToFlow(toAdd,isCopy.isSelected());
            refreshCol1();
        });

        SearchableListView.makeSearchable(itemListView, FXCollections.observableArrayList(itemNameList), addField);
        itemListView.setOnMouseClicked(e -> {
            String selectedItem = itemListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                choosePopup.hide();
                addField.setText(selectedItem);
                if (combatFlow.getDatabase().getAllUnit().get(selectedItem) instanceof Monster) {
                    isCopy.setVisible(true);
                } else {
                    isCopy.setVisible(false);
                    isCopy.setSelected(false);
                }
            }
        });
        itemListView.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.ESCAPE) {
                String selectedItem = itemListView.getSelectionModel().getSelectedItem();
                choosePopup.hide();
                addField.setText(selectedItem);
                if (combatFlow.getDatabase().getAllUnit().get(selectedItem) instanceof Monster) {
                    isCopy.setVisible(true);
                } else {
                    isCopy.setVisible(false);
                    isCopy.setSelected(false);
                }
                e.consume();
            }
        });
        choosePopup.getContent().add(itemListView);

        addField.setOnKeyReleased(e -> {
            if (!choosePopup.isShowing()) {
                Bounds screenBounds = addField.localToScreen(addField.getBoundsInParent());
                if (screenBounds != null) {
                    choosePopup.show(addField, screenBounds.getMaxX(), screenBounds.getMinY());
                }
            }
            if (e.getCode() == KeyCode.ESCAPE) {
                choosePopup.hide();
                e.consume();
            }
        });

        removeButton.setOnAction(e-> {
            if (confirmRemove) {
                Unit toRemove = turnList.getSelectionModel().getSelectedItem();
                combatFlow.removeUnitFromFlow(toRemove.getName());
                refreshCol1();
            } else {
                confirmRemove = true;
            }
        });
        turnBox.getChildren().addAll(turnList, addBox, removeButton);

        return turnBox;
    }

    public Runnable writeToSheet() {
        //turn panel
        return () -> {
        try {
            String allRange = "MAP!A1:F101";
            GoogleSheetsUtil sheetsUtil = new GoogleSheetsUtil();
            List<Object> turnIndicator = new ArrayList<>();
            List<List<Object>> toAppendTurn = new ArrayList<>();
            List<List<Object>> toAppendCooldownLeft = new ArrayList<>();
            List<List<Object>> toAppendCooldownRight = new ArrayList<>();
            List<List<Object>> allToAppend  = new ArrayList<>();
            turnIndicator.add("<TURN "+combatFlow.getTurnCount()+">");

            List<SkillInstance> sort = new ArrayList<>();
            DecimalFormat df2 = new DecimalFormat("0");
            df2.setRoundingMode(RoundingMode.FLOOR);

            for (Unit unit : turnList.getItems()) {
                List<Object> row1 = new ArrayList<>();
                List<Object> row2 = new ArrayList<>();
                if (!(unit instanceof Monster)) {
                    for (SkillInstance instance : unit.getAllSkill().values()) {
                        if (instance.getOnCooldown() > 0) {
                            sort.add(instance);
                        }
                    }
                }
                if (!(unit instanceof Monster)) {
                    row1.add(unit.getName());
                    row1.add("");
                    row1.add("");
                    if (unit.getDebris().getRemaining() == 0) {
                        row1.add(df.format(unit.getHealth().getRemaining()));
                    } else {
                        row1.add(df2.format(unit.getHealth().getRemaining()) + "+" + df2.format(unit.getDebris().getRemaining()));
                    }
                    row1.add(":");
                    row1.add(df.format(unit.getHealth().getUsable()));

                    row2.add("");
                    row2.add("");
                    row2.add("");
                    row2.add(df.format(unit.getMana().getRemaining()));
                    row2.add(":");
                    row2.add(df.format(unit.getMana().getUsable()));
                } else {
                    row1.add(unit.getName());
                    row1.add("");
                    row1.add("");
                    row1.add(df.format(combatFlow.getDamageTaken().get(unit.getName())));
                    row1.add("/");
                    row1.add(df.format(combatFlow.getHealTaken().get(unit.getName())));

                    row2.add("");
                    row2.add("");
                    row2.add("");
                    row2.add("");
                    row2.add("");
                    row2.add("");
                }
                toAppendTurn.add(row1);
                toAppendTurn.add(row2);
            }
            //cooldown part
            sort.sort(Comparator.comparing(SkillInstance::getOnCooldown));
            int index = 1;
            for (SkillInstance instance : sort) {
                if (index >= 13) break;
                List<Object> cd = new ArrayList<>();
                cd.add(instance.getSkillData().getName());
                cd.add(":");
                cd.add(df2.format(instance.getOnCooldown()));
                if (index <= 6) {
                    toAppendCooldownLeft.add(cd);
                } else {
                    toAppendCooldownRight.add(cd);
                }
                index++;
            }

            //add everything
            allToAppend.add(turnIndicator);
            List<List<Object>> mergedCooldown = new ArrayList<>();

            for (int i = 0; i < 6; i++) {
                List<Object> row = new ArrayList<>();

                // ซ้าย
                if (i < toAppendCooldownLeft.size()) {
                    row.addAll(toAppendCooldownLeft.get(i));
                } else {
                    // เติมช่องว่างให้เท่ากัน (3 ช่อง ตามจากตัวอย่าง: name, :, cooldown)
                    row.add(""); row.add(""); row.add("");
                }

                // ขวา
                if (i < toAppendCooldownRight.size()) {
                    row.addAll(toAppendCooldownRight.get(i));
                } else {
                    row.add(""); row.add(""); row.add("");
                }

                mergedCooldown.add(row);
            }
            allToAppend.addAll(mergedCooldown);
            allToAppend.addAll(toAppendTurn);
            sheetsUtil.clearRange(GoogleSheetsUtil.viewerSheetId, allRange);
            sheetsUtil.updateRange(GoogleSheetsUtil.viewerSheetId, allRange, allToAppend);

            //update unit sheets
            for (Unit unit : combatFlow.getAllUnit().values()) {
                if (unit.isPlayer() || unit.isNpc()) {
                    List<Request> requests = unit.buildWriteRequests();
                    sheetsUtil.takeRequests(requests);
                }
            }
            sheetsUtil.requestSet();
            sheetsUtil.processRequest(GoogleSheetsUtil.viewerSheetId);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    };
    }

    public void refreshCol1() {
        col1Block.getChildren().clear();
        col1Block.getChildren().addAll(turnList(),toolBlock());
        col1.getChildren().clear();
        col1.getChildren().addAll(col1Block, calculationBlock());
    }

    public void refreshCol2() {
        col2.getChildren().clear();
        List<Unit> player = new ArrayList<>(combatFlow.getPlayerUnit().values());
        List<Unit> monster = new ArrayList<>(combatFlow.getMonsterUnit().values());
        monster.addAll(combatFlow.getSummonUnit().values());
        col2.getChildren().addAll(importBlock(player), importBlock(monster));
    }

    public void refreshCalculationBlock() {
        col1.getChildren().clear();
        col1.getChildren().addAll(col1Block, calculationBlock());
    }

    public void refreshContent(boolean write) {
        refreshCol1();
        refreshCol2();
        if (write) {
            AsyncUtil.runAsync(writeToSheet());
        }
    }

    public CombatUtilityPanel getUtilityPanel() {
        return utilityPanel;
    }

    private Label createLabel(String text, double width) {
        Label label = new Label(text);
        label.setPrefSize(width, 40);
        label.setStyle("-fx-font-size: 16px; -fx-padding: 0");
        return label;
    }

    private Label createWrappingLabel(String text, double width) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setPrefWidth(width);
        label.setMinHeight(Region.USE_PREF_SIZE); // สำคัญ: ให้ความสูงปรับตามเนื้อหา
        label.setStyle("-fx-font-size: 16px; -fx-padding: 0");
        return label;
    }

}

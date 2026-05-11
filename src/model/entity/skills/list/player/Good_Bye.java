package model.entity.skills.list.player;

import main.controller.CombatFlow;
import main.controller.event.EventBus;
import main.controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;
import util.LogWriterUtil;

import java.util.List;

public class Good_Bye extends Skill implements SkillWithCondition {

    public static String NAME = "Good bye";

    public Good_Bye() {
        super();
        setDescription("เรียกหมอกขึ้นมาพรางตัวพร้อมกับเคลื่อนไหว XA เมตร หากการโจมตีครั้งถัดไปเป็นคริติคอล จะสร้างความเสียหายเพิ่มเติม XB หน่วย");
        setActionType("Action");
        setManaCost(4);
        setCooldown(3);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.6*MSPD"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.MOVEMENT);

        getSkillMultiplier().put("XB",new SkillMultiplier("1.2*(PATK*(CritDamage))"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.STEALTH);
        getSkillMultiplier().get("XB").getTags().add(SkillType.CRITICAL);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
//                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
        );
//        spec    .addFields(
//                new SkillInputSpec.InputField<String>("Mode", SkillInputSpec.InputType.SELECT, 0)
//                        .options(List.of("choice","choice"), 0)
//                        .labelProvider(String::toString, 0)
//        , 0, 0)
//                .addFields(
//                        new SkillInputSpec.InputField<String>("Damage", SkillInputSpec.InputType.NUMBER,1)
//                , 0, 1);
        return spec;
    }

    @Override
    public void calculateExtra() {

    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        if (!skillTarget.getTarget(0).isEmpty()) {

        } else {
            skillTarget.getTarget(0).add(getUser().getName());
        }
        int duration = 99;
        Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Good bye");
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(),getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                        .condition(condition, duration)
                        .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                        .build());
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Good bye");
        double xa = getSkillMultiplier().get("XA").getResult();
        condition.setDescription("ครั้งถัดไปที่โจมตีคริติคอลจะสร้างความเสียหายกายภาพเพิ่มเติม "+xa+" หน่วย");

        condition.setConditionType(ConditionType.NEUTRAL);
        condition.setConditionTierType(ConditionTierType.UNDISPELLABLE);

        //remove and re-add to database
        addConditionToDatabase(condition, combatFlow);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ActionEvent.class, EventPhase.MODIFY, 0, (ActionEvent event) -> {
            if (event.unit_source != getUser() || !event.hasActType(ActType.ATTACK) || !event.hasCritical()) return;
            double xa = getSkillMultiplier().get("XA").getResult();
            event.addFlatModifier(ActionEffectType.DAMAGE_PHYSICAL, xa);
            ConditionManager.removeCondition(getUser(), "Good bye");
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

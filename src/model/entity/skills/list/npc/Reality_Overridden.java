package model.entity.skills.list.npc;

import main.controller.CombatFlow;
import main.controller.event.EventBus;
import main.controller.event.events.ActionEffect;
import main.controller.event.events.ActionEvent;
import main.controller.event.events.ResourceEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

import java.util.List;

public class Reality_Overridden extends Skill implements SkillWithCondition {

    public static String NAME = "Reality, Overridden";

    public Reality_Overridden() {
        super();
        setDescription("กำหนดอนาคตของหนึ่งยูนิต จนกว่าจะถึงเทิร์นถัดไป หากเป็นพันธมิตร ยูนิตนั้นจะรับความเสียหายที่ทำให้พลังชีวิตต่ำกว่า XA หน่วยไม่ได้ ยกเว้นจากความเสียหายจริง\n" +
                "หากเป็นศัตรู ยูนิตนั้นจะไม่ได้รับการฟื้นฟูใดๆ");
        setActionType("Action");
        setManaCost(10);
        setCooldown(3);
        getSkillMultiplier().put("XA",new SkillMultiplier("1*MATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIMIT);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
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
            int duration = 1;
            Conditions condition1 = combatFlow.getDatabase().getAllConditionMap().get("Fall Unwritten");
            Conditions condition2 = combatFlow.getDatabase().getAllConditionMap().get("Rise Unremembered");

            for (String name : skillTarget.getTarget(0)) {
                Unit target = combatFlow.findUnit(name);
                if (isAlly(target, combatFlow)) {
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), target)
                                    .condition(condition1, duration)
                                    .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                                    .build());
                } else {
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), target)
                                    .condition(condition2, duration)
                                    .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                                    .build());
                }
            }
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        double xa = getSkillMultiplier().get("XA").getResult();
        Conditions condition = new Conditions("Fall Unwritten");
        condition.setDescription("เมื่อได้รับความเสียหายที่ทำให้พลังชีวิตเหลือต่ำกว่า "+xa+"หน่วย ยกเลิกความเสียหายนั้น ยกเว้นจากความเสียหายจริง");
        Conditions condition2 = new Conditions("Rise Unremembered");
        condition2.setDescription("ไม่รับผลการฟื้นฟูใดๆ");

        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.BOUND);
        condition2.setConditionType(ConditionType.DEBUFF);
        condition2.setConditionTierType(ConditionTierType.BOUND);

        //remove and re-add to database
        addConditionToDatabase(condition, combatFlow);
        addConditionToDatabase(condition2, combatFlow);
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ResourceEvent.class, EventPhase.MODIFY, -1, (ResourceEvent event) -> {
                if (event.target.hasCondition("Fall Unwritten")) {
                    if (event.effectType == ActionEffectType.DAMAGE_TRUE) return;
                    double damage = event.amount;
                    double xa = getSkillMultiplier().get("XA").getResult();
                    double current_health = event.target.getHealth().getRemaining();
                    if (current_health - damage < xa) {
                        event.amount = 0;
                        sendSkillTriggerEvent(combatFlow, "Reality Overridden triggered, reducing damage taken to 0");
                    }
                } else if (event.target.hasCondition("Rise Unremembered")) {
                    if (event.effectType != ActionEffectType.HEALTH_RECOVER) return;
                    event.amount = 0;
                    sendSkillTriggerEvent(combatFlow, "Reality Overridden triggered, reducing health recovery to 0");
                }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

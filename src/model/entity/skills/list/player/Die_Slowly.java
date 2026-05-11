package model.entity.skills.list.player;

import main.controller.CombatFlow;
import main.controller.event.EventBus;
import main.controller.event.events.ActionEvent;
import main.controller.event.events.RoundEvent;
import manager.ConditionManager;
import model.entity.ConditionInstance;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

import java.util.ArrayList;
import java.util.List;

public class Die_Slowly extends Skill implements SkillWithCondition {

    public static String NAME = "Die Slowly";

    public Die_Slowly() {
        super();
        setDescription("เลือกเป้าหมายที่มีดีบัพสูงที่สุดในบรรดาศัตรู พุ่งโจมตีใส่ยูนิตนั้น เปลี่ยนดีบัพอื่นทั้งหมดให้กลายเป็น Poison ที่สร้างความเสียหายกายภาพ XA หน่วยเป็นเวลา XB รอบเทิร์น");
        setActionType("Action");
        setManaCost(9);
        setCooldown(3);
        getSkillMultiplier().put("XA",new SkillMultiplier("1.35*PATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.POISON);
        getSkillMultiplier().get("XA").getTags().add(SkillType.DEBUFF);

        getSkillMultiplier().put("XB",new SkillMultiplier("2"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DURATION);
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
        for (String name : skillTarget.getTarget(0)) {
            Unit target = combatFlow.findUnit(name);
            List<String> debuff_to_remove = new ArrayList<>();
            int number_of_debuff = 0;

            for (ConditionInstance value : target.getConditionInstances().values()) {
                if (value.getCondition().getConditionType() == ConditionType.DEBUFF) {
                    debuff_to_remove.add(value.getCondition().getName());
                    number_of_debuff++;
                }
            }

            for (String debuff_name : debuff_to_remove) {
                ConditionManager.removeCondition(target, debuff_name);
            }

            for (int i=0;i<number_of_debuff;i++) {
                int duration = (int) getSkillMultiplier().get("XB").getResult();

                Conditions condition = new Conditions("Die Slowly");
                double xa = getSkillMultiplier().get("XA").getResult();
                condition.setDescription("ได้รับความเสียหายกายภาพ "+xa+" หน่วยเมื่อจบรอบเทิร์น");

                condition.setConditionType(ConditionType.DEBUFF);
                condition.setConditionTierType(ConditionTierType.GENERAL);

                addConditionToDatabase(condition, combatFlow);

                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), target)
                                .condition(condition, duration)
                                .addActType(ActType.HEAL, ActType.HEALTH_RECOVER, ActType.SKILL_TRIGGER)
                                .build()
                );
            }
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {

    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(RoundEvent.class, EventPhase.MODIFY, 0, (RoundEvent event) -> {
            for (Unit unit : combatFlow.getAllUnit().values()) {
                if (!unit.hasCondition("Die Slowly Poison")) continue;

                double xa = getSkillMultiplier().get("XA").getResult();
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), unit)
                                .effect(ActionEffectType.DAMAGE_PHYSICAL, xa, 1)
                                .addActType(ActType.SKILL_TRIGGER)
                                .build()
                );
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

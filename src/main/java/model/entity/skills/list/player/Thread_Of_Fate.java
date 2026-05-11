package main.java.model.entity.skills.list.player;

import main.java.controller.CombatFlow;
import main.java.controller.event.EventBus;
import main.java.controller.event.events.ActionEvent;
import main.java.controller.event.events.ResourceEvent;
import main.java.manager.ConditionManager;
import main.java.model.entity.ConditionInstance;
import main.java.model.entity.Conditions;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillMultiplier;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.type.*;

public class Thread_Of_Fate extends Skill {

    public static String NAME = "Thread of Fate";

    public Thread_Of_Fate() {
        super();
        setDescription("รับสถานะ Thread of Fate เป็นเวลา XB รอบเทิร์น ซึ่งทำให้ระหว่างนี้จะไม่สามารถรับความเสียหายที่ทำให้หมดสติได้สูงสุด XA ครั้ง");
        setActionType("Action");
        setManaCost(10);
        setCooldown(6);
        setManaReservePercent(0.15);
        getSkillMultiplier().put("XA",new SkillMultiplier("4"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIMIT);

        getSkillMultiplier().put("XB",new SkillMultiplier("3"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DURATION);
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
//        , 0, 0);
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
            int duration = (int) getSkillMultiplier().get("XB").getResult();
            int xa = (int) getSkillMultiplier().get("XA").getResult();

            Conditions condition = new Conditions("Thread of Fate");
            condition.setDescription("การได้รับความเสียหายที่ทำให้หมดสติจะถูกหยุดยั้ง ใช้งานได้อีก "+ xa + "ครั้ง");

        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.BOUND);

            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), getUser())
                            .condition(condition, duration, "Death Prevention", (double) xa)
                            .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                            .build()
            );
    }


    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ResourceEvent.class, EventPhase.MODIFY, -3, (ResourceEvent event) -> {
            if (event.target != getUser()) return;
            if (!event.isDamage()) return;
            if (!event.target.hasCondition("Thread of Fate")) return;
            double hp = getUser().getHealth().getRemaining();
            double debris = getUser().getDebris().getRemaining();
            ConditionInstance condition = event.target.findCondition("Thread of Fate");
            int count = (int) condition.getNumberRecordOrDefault("Death Prevention", 0);

            if (event.amount >= hp+debris && count >= 1) {
                event.amount = 0;
                count--;
                condition.putNumberRecord("Death Prevention", count);
                condition.getCondition().setDescription("การได้รับความเสียหายที่ทำให้หมดสติจะถูกหยุดยั้ง ใช้งานได้อีก "+ count + "ครั้ง");
                if (count <= 0) {
                    ConditionManager.removeCondition(event.target, "Thread of Fate");
                }
                sendSkillTriggerEvent(combatFlow, "Thread of Fate triggered [Undying]");
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

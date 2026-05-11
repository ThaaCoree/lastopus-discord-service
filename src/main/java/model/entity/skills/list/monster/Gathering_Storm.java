package main.java.model.entity.skills.list.monster;

import main.java.controller.CombatFlow;
import main.java.controller.event.EventBus;
import main.java.controller.event.events.ActionEvent;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.type.*;

public class Gathering_Storm extends Skill {

    public static String NAME = "Gathering Storm";

    public Gathering_Storm() {
        super();
        setDescription("ผู้ใช้จะมี Counter [Gathering Storm]\n" +
                "เมื่อร่ายเวทมนตร์จะได้รับ Gathering Storm หนึ่งสแต็คจนกว่าจะจบการต่อสู้\n" +
                "\n" +
                "Gathering Storm : เพิ่มตัวคูณสกิลประเภทลิมิต 1 ต่อสแต็ค");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
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
        if (getUser().getCounter() == null) return;
        if (!getUser().getRawCounterMap().containsKey(CounterName.GATHERING_STORM)) {
            getUser().getRawCounterMap().put(CounterName.GATHERING_STORM,0.0);
            getUser().getCounter().put(CounterName.GATHERING_STORM,0.0);
        }
        double counter = getUser().getRawCounterMap().get(CounterName.GATHERING_STORM);
        getSkillModifier().getSkillModifierSafe(SkillType.LIMIT).setFlat(1*counter);
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        getUser().counterIncrement(CounterName.GATHERING_STORM);
        calculateAll();
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ActionEvent.class, EventPhase.POST, 0, (ActionEvent event) -> {
            if (!event.hasActType(ActType.CAST) || event.unit_source != getUser()) return;
            getUser().counterIncrement(CounterName.GATHERING_STORM);
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), getUser())
                            .addActType(ActType.SKILL_TRIGGER)
                            .build()
            );
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

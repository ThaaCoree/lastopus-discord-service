package main.java.model.entity.skills.list.player;

import main.java.controller.CombatFlow;
import main.java.controller.event.EventBus;
import main.java.controller.event.events.ActionEvent;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.entity.skills.SkillMultiplier;
import main.java.model.type.*;
import util.LogWriterUtil;

public class Black_Scythe_Comet extends Skill {

    public static String NAME = "Black Scythe of the Comet";

    public Black_Scythe_Comet() {
        super();
        setDescription("ทุกๆการโจมตีครั้งที XB จะเพิ่มความเสียหายกายภาพธาตุไฟหรือแสง XA หน่วย");
        setActionType("Passive");
        setManaReservePercent(0.2);
        getSkillMultiplier().put("XA",new SkillMultiplier("1.5*PATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.ELEMENTAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.FIRE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIGHT);

        getSkillMultiplier().put("XB",new SkillMultiplier("3"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.COUNT);
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
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ActionEvent.class, EventPhase.MODIFY, 0, event -> {
            if (event.unit_source != getUser() || !event.hasActType(ActType.STRIKE)) return;
            getUser().counterIncrement(CounterName.BLACK_SCYTHE_OF_THE_COMET);

                double xa = getSkillMultiplier().get("XA").getResult();
                if (getUser().getCounter().get(CounterName.BLACK_SCYTHE_OF_THE_COMET) >= 3) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Black Scythe of the Comet has reached the count");
                    event.addFlatModifier(ActionEffectType.DAMAGE_PHYSICAL, xa);
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(),getUser(), getUser())
                                    .addActType(ActType.SKILL_TRIGGER)
                                    .build());
                    LogWriterUtil.log(sb.toString(), combatFlow.getTurnCount());
                    getUser().counterDecrement(CounterName.BLACK_SCYTHE_OF_THE_COMET);
                    getUser().counterDecrement(CounterName.BLACK_SCYTHE_OF_THE_COMET);
                    getUser().counterDecrement(CounterName.BLACK_SCYTHE_OF_THE_COMET);
                }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

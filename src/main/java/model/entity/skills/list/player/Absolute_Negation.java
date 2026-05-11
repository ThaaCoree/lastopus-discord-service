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

public class Absolute_Negation extends Skill {

    public static String NAME = "Absolute Negation";

    public Absolute_Negation() {
        super();
        setDescription("เมื่อถูกโจมตี มีโอกาส XA ที่จะไม่รับความเสียหาย โอกาสนี้จะไม่มากกว่า XB\n" +
                "หากเป็น True Damage จะมีโอกาสหลีกเลี่ยงเหลือครึ่งเดียว");
        setActionType("Passive");
        getPureTags().add(SkillType.PHYSICAL);
        setManaReservePercent(0.25);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.07+(LUK*0.001)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.CHANCE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.DEFENSE);
        getSkillMultiplier().get("XA").setPercent(true);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.25+(LUK*0.0002)"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.LIMIT);
        getSkillMultiplier().get("XB").setPercent(true);
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
        double xa = getSkillMultiplier().get("XA").getResult();
        double xb = getSkillMultiplier().get("XB").getResult();
        getSkillMultiplier().get("XA").setResult(Math.min(xa, xb));
        translateDescription();

        combatFlow.getEventBus().register(ActionEvent.class, EventPhase.MODIFY, 0, event -> {
            if (event.hasTarget(getUser().getName()) && event.canDamage(getUser().getName())) {
                EventBus eventBus = combatFlow.getEventBus();
                if (getUser().getCounter().get(CounterName.CERTAINTY_REWRITE) >= 1) {
                    LogWriterUtil.log("Absolute Negation TRIGGERED WITH CERTAINTY! negating the happening damage", combatFlow.getTurnCount());
                    event.addAllDamageOverrideModifier(0);
                    sendActionEvent(eventBus,
                            ActionEvent.builder(getName(), getUser(), getUser())
                                    .addActType(ActType.SKILL_TRIGGER)
                                    .build()
                    );
                    getUser().counterSet(CounterName.CERTAINTY_REWRITE, 0);
                    return;
                }

                int chance = (int) (getSkillMultiplier().get("XA").getResult()*100);
                if (chance >= combatFlow.getExtraDice()) {
                    LogWriterUtil.log("Absolute Negation triggered! negating the happening damage", combatFlow.getTurnCount());
                    event.addAllDamageOverrideModifier(0);
                    sendActionEvent(eventBus,
                            ActionEvent.builder(getName(), getUser(), getUser())
                                    .addActType(ActType.SKILL_TRIGGER)
                                    .build()
                    );
                }
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

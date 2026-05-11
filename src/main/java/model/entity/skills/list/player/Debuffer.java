package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.EventBus;
import controller.event.events.ActionEvent;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.entity.units.Unit;
import model.type.ActType;
import model.type.EventPhase;
import model.type.SkillType;
import util.LogWriterUtil;
import util.WeightedRandom;

public class Debuffer extends Skill {

    public static String NAME = "Debuffer";

    public Debuffer() {
        super();
        setDescription("เมื่อจู่โจมศัตรูที่อยู่ในหมอก สุ่มมอบดีบัพให้กับเป้าหมายเป็นเวลา XA เทิร์น");
        setActionType("Passive");
        setHealthReservePercent(0.4);
        setManaReservePercent(0.55);
        getSkillMultiplier().put("XA",new SkillMultiplier("2"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.DURATION);

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
        eventBus.register(ActionEvent.class, EventPhase.POST, 0, (ActionEvent event) -> {
            if (!event.hasActType(ActType.STRIKE) || event.unit_source != getUser()) return;
            WeightedRandom<String> weightedRandom = new WeightedRandom<>();
            weightedRandom.add("Tremble", 40);
            weightedRandom.add("Blindness", 60);
            weightedRandom.add("Sleep", 20);
            weightedRandom.add("Paralyzed", 10);
            weightedRandom.add("Melting Acid", 60);
            weightedRandom.add("Green Fin Poison", 200);
            weightedRandom.add("Lingering Noise", 130);
            weightedRandom.add("Rooted", 80);
            weightedRandom.add("Broken Leg", 30);

            Conditions condition = combatFlow.findCondition(weightedRandom.roll());
            if (condition == null) {
                LogWriterUtil.log("Cannot find debuff for Debuffer");
                return;
            }
            double xa = getSkillMultiplier().get("XA").getResult();

            for (Unit target : event.unit_target) {
                if (target.hasCondition(condition.getName())) {
                    String condition_name = condition.getName();
                    if (condition_name.equals("Green Fin Poison") ||
                        condition_name.equals("Lingering Noise") ||
                            condition_name.equals("Melting Acid") ||
                            condition_name.equals("Tremble")
                        )
                    {
                        //stackable (leave blank)
                    } else {
                        continue;
                    }
                }

                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), target)
                                .condition(condition, (int) xa)
                                .addActType(ActType.CONDITION_GIVEN, ActType.SKILL_TRIGGER)
                                .build()
                );
                LogWriterUtil.log(">Debuffer triggered giving "+condition.getName());
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

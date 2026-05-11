package model.entity.skills.list.item.offensive;

import main.controller.CombatFlow;
import main.controller.event.EventBus;
import main.controller.event.events.ActionEvent;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.entity.units.Unit;
import model.type.*;
import util.LogWriterUtil;

import java.util.List;
import java.util.Map;

public class Pain_Amplify extends Skill {

    public static String NAME = "Pain Amplify";

    public Pain_Amplify() {
        super();
        setDescription("เมื่อมอบดีบัพให้กับเป้าหมาย สร้างความเสียหายโดยตรง XA หน่วยด้วย");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.DEBUFF);
        getSkillMultiplier().put("XA",new SkillMultiplier("(0.33*PATK + 0.33*MATK + 0.33*RATK)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.PURE);
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
            if (!event.hasActType(ActType.CONDITION_GIVEN) || event.unit_source != getUser()) return;
            for (Map.Entry<Integer, Map<Conditions, Integer>> condition_map : event.condition_to_inflict.entrySet()) {
                for (Map.Entry<Conditions, Integer> condition_entry : condition_map.getValue().entrySet()) {
                    if (condition_entry.getKey().getConditionType().equals(ConditionType.DEBUFF)) {

                        double xa = getSkillMultiplier().get("XA").getResult();
                        sendActionEvent(combatFlow.getEventBus(),
                                ActionEvent.builder(getName(), getUser(), event.unit_target)
                                        .effect(ActionEffectType.DAMAGE_PURE, xa, 1)
                                        .addActType(ActType.SKILL_TRIGGER)
                                        .build()
                        );

                        LogWriterUtil.log(">Pain Amplify triggered");

                        return;
                    }
                }
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

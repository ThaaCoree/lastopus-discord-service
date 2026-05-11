package model.entity.skills.list.item.utility;

import controller.CombatFlow;
import controller.event.EventBus;
import controller.event.events.ActionEffect;
import controller.event.events.ActionEvent;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.units.Unit;
import model.type.ActionEffectType;
import model.type.EventPhase;
import model.type.SkillType;

public class Ethereal_Shift extends Skill {

    public static String NAME = "Ethereal Shift";

    public Ethereal_Shift() {
        super();
        setDescription("รับความเสียหายเวทมนตร์แทนกายภาพ และรับความเสียหายกายภาพแทนเวทมนตร์");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.KINETIC);
        getPureTags().add(SkillType.ETHEREAL);
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
            if (!event.unit_target.contains(getUser())) return;
            if (!isActive()) return;
            for (Unit unit : event.unit_target) {
                if (unit == getUser()) {
                    event.effects.forEach((target, effect_list) -> {
                        for (ActionEffect actionEffect : effect_list) {
                            if (actionEffect.type.equals(ActionEffectType.DAMAGE_PHYSICAL)) {
                                actionEffect.type = ActionEffectType.DAMAGE_MAGICAL;
                            }
                            if (actionEffect.type.equals(ActionEffectType.DAMAGE_MAGICAL)) {
                                actionEffect.type = ActionEffectType.DAMAGE_PHYSICAL;
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

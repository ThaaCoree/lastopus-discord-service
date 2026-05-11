package model.entity.skills.list.item.defensive;

import controller.CombatFlow;
import controller.event.EventBus;
import controller.event.events.ActionEffect;
import controller.event.events.ActionEvent;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.type.ActionEffectType;
import model.type.EventPhase;
import model.type.SkillType;

public class Light_Redirect extends Skill {

    public static String NAME = "Light Redirect";

    public Light_Redirect() {
        super();
        setDescription("รับความเสียหายเวทมนตร์แทนความเสียหายโดยตรง");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.DEFENSE);
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
        eventBus.register(ActionEvent.class, EventPhase.MODIFY, -2, (ActionEvent event) -> {
            if (!event.hasPureDamage(getUser().getName())) return;
            double pure_damage = event.getPureDamage(getUser().getName());
            ActionEffect actionEffect = new ActionEffect(ActionEffectType.DAMAGE_MAGICAL, pure_damage);
            event.effects.get(getUser().getName()).add(actionEffect);

            for (ActionEffect effect : event.effects.get(getUser().getName())) {
                if (effect.type == ActionEffectType.DAMAGE_PURE) {
                    effect.finalValue = 0;
                }
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

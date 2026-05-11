package model.entity.skills.list.item.offensive;

import main.controller.CombatFlow;
import main.controller.event.EventBus;
import main.controller.event.events.ActionEffect;
import main.controller.event.events.ActionEvent;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.units.Unit;
import model.type.ActType;
import model.type.ActionEffectType;
import model.type.EventPhase;
import model.type.SkillType;

import java.util.List;

public class Death_Sentence extends Skill {

    public static String NAME = "Death Sentence";

    public Death_Sentence() {
        super();
        setDescription("การโจมตีคริติคอลจะกลายเป็นความเสียหายจริง");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.PHYSICAL);
        getPureTags().add(SkillType.CRITICAL);
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
        eventBus.register(ActionEvent.class, EventPhase.MODIFY, 0, (ActionEvent event) -> {
            if (!event.hasActType(ActType.HEAL) || event.unit_source != getUser() || event.event_source.equals(getName())) return;
            List<Unit> targets = event.unit_target;
            for (Unit target : targets) {
                int loop = 1;
                while (loop <= event.damage_times) {
                    loop++;
                if (event.isCriticalToUnit(target, loop)) {
                    for (ActionEffect actionEffect : event.effects.get(target.getName())) {
                        if (actionEffect.type == ActionEffectType.DAMAGE_PHYSICAL) {
                            actionEffect.type = ActionEffectType.DAMAGE_TRUE;
                        }
                        if (actionEffect.type == ActionEffectType.DAMAGE_MAGICAL) {
                            actionEffect.type = ActionEffectType.DAMAGE_TRUE;
                        }
                        if (actionEffect.type == ActionEffectType.DAMAGE_PURE) {
                            actionEffect.type = ActionEffectType.DAMAGE_TRUE;
                        }
                    }
                }
                }
            }

            sendActionEvent(combatFlow.getEventBus(),
                                ActionEvent.builder(getName(), getUser(), targets)
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

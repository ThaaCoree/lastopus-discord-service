package main.java.model.entity.skills.list.item.utility;

import main.java.controller.CombatFlow;
import main.java.controller.event.EventBus;
import main.java.controller.event.events.ActionEffect;
import main.java.controller.event.events.ActionEvent;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.type.ActionEffectType;
import main.java.model.type.EventPhase;
import main.java.model.type.SkillType;

import java.util.concurrent.ThreadLocalRandom;

public class Unpredictable_Pain extends Skill {

    public static String NAME = "Unpredictable Pain";

    public Unpredictable_Pain() {
        super();
        setDescription("เมื่อรับความเสียหายที่ไม่ใช่ความเสียหายจริง สุ่มระหว่าง\n" +
                "รับเป็นความเสียหายกายภาพ\n" +
                "รับเป็นความเสียหายเวทมนตร์\n" +
                "หรือ รับเป็นความเสียหายโดยตรง");
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
        eventBus.register(ActionEvent.class, EventPhase.POST, 0, (ActionEvent event) -> {
            if (event.damage_times <= 0) return;
            if (!event.unit_target.contains(getUser())) return;

            event.effects.forEach((name, list) -> {
                if (name.equals(getUser().getName())) {
                    for (ActionEffect actionEffect : list) {
                        ActionEffectType type = actionEffect.type;
                        if (type == ActionEffectType.DAMAGE_PHYSICAL ||
                                type == ActionEffectType.DAMAGE_MAGICAL ||
                                type == ActionEffectType.DAMAGE_PURE) {

                            int random = ThreadLocalRandom.current().nextInt(3);
                            if (random == 0) {
                                actionEffect.type = ActionEffectType.DAMAGE_PHYSICAL;
                            }
                            if (random == 1) {
                                actionEffect.type = ActionEffectType.DAMAGE_MAGICAL;
                            }
                            if (random == 2) {
                                actionEffect.type = ActionEffectType.DAMAGE_PURE;
                            }
                        }
                    }
                }
            });
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

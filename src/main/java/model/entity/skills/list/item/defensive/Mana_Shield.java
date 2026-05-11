package main.java.model.entity.skills.list.item.defensive;

import main.java.controller.CombatFlow;
import main.java.controller.event.EventBus;
import main.java.controller.event.events.ResourceEvent;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.type.ActionEffectType;
import main.java.model.type.EventPhase;
import main.java.model.type.SkillType;
import util.LogWriterUtil;

public class Mana_Shield extends Skill {

    public static String NAME = "Mana Shield";

    public Mana_Shield() {
        super();
        setDescription("เมื่อได้รับความเสียหายแล้วพลังชีวิตลดเหลือน้อยกว่า 1\n" +
                "เปลี่ยนให้ความเสียหายนั้นสร้างกับมานาแทน\n" +
                "ความสามารถนี้จะไม่เกิดผลเมื่อมานาติดลบ");
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
        eventBus.register(ResourceEvent.class, EventPhase.POST, 0, (ResourceEvent event) -> {
            if (event.target != getUser()) return;
            if (event.target.getMana().getRemaining() < 0) return;
            if (event.effectType.equals(ActionEffectType.DAMAGE_MAGICAL) ||
            event.effectType.equals(ActionEffectType.DAMAGE_PHYSICAL) ||
            event.effectType.equals(ActionEffectType.DAMAGE_PURE) ||
            event.effectType.equals(ActionEffectType.DAMAGE_TRUE)) {
                double current_health = getUser().getHealth().getRemaining();
                double debris = getUser().getDebris().getRemaining();
                if (event.amount > current_health + debris) {
                    event.amount = current_health + debris - 1;
                    double overflow = event.amount - current_health;
                    getUser().getMana().sumRemaining(overflow * -1);
                    LogWriterUtil.log(">Mana Shield triggered, blocked " + overflow + " damage");
                }
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

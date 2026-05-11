package model.entity.skills.list.item.defensive;

import main.controller.CombatFlow;
import main.controller.event.EventBus;
import main.controller.event.events.ActionEvent;
import main.controller.event.events.ResourceEvent;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.ActType;
import model.type.ActionEffectType;
import model.type.EventPhase;
import model.type.SkillType;
import util.LogWriterUtil;

public class Vivid_Dream extends Skill {

    public static String NAME = "Vivid Dream";

    public Vivid_Dream() {
        super();
        setDescription("บล็อกความเสียหายหลังลดทอนที่ต่ำกว่า XA หน่วยทั้งหมดโดยอัตโนมัติ");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.1*UsableHP"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.DEFENSE);
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
            if (event.effectType.equals(ActionEffectType.DAMAGE_MAGICAL) ||
                    event.effectType.equals(ActionEffectType.DAMAGE_PHYSICAL) ||
                    event.effectType.equals(ActionEffectType.DAMAGE_PURE) ||
                    event.effectType.equals(ActionEffectType.DAMAGE_TRUE)) {
                double xa = getSkillMultiplier().get("XA").getResult();
                if (event.amount < xa) {
                    LogWriterUtil.log(">Vivid Dream triggered, blocked " + event.amount + " damage");
                    event.amount = 0;
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), getUser())
                                    .addActType(ActType.SKILL_TRIGGER)
                                    .build());
                }
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

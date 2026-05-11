package model.entity.skills.list.monster;

import main.controller.CombatFlow;
import main.controller.event.events.ActionEvent;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.type.ActType;
import model.type.ActionEffectType;
import model.type.EventPhase;
import model.type.StatType;
import util.LogWriterUtil;

public class Twin_Twice extends Skill {

    public static String NAME = "TWIN_TWICE";

    public Twin_Twice() {
        super();
        setDescription("เมื่อยูนิตที่มี Passive เดียวกันนี้โจมตี โจมตีไปพร้อมกันด้วย");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
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
//        , 0, 0);
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
        combatFlow.getEventBus().register(ActionEvent.class, EventPhase.MODIFY, 0, event -> {
            if (event.unit_source == getUser()) return;
            if (event.unit_source.hasSkill(Twin_Twice.NAME) && !event.event_source.equals(getName()) && event.hasActType(ActType.ATTACK)) {
                double patk = getUser().getStats().get(StatType.PHYSICALATTACK).getFinal();

                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), event.unit_target)
                                .effect(ActionEffectType.DAMAGE_PHYSICAL, patk, 1)
                                .addActType(ActType.ATTACK, ActType.STRIKE)
                                .build()
                );
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

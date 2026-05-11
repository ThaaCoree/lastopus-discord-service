package main.java.model.entity.skills.list.item.support;

import main.java.controller.CombatFlow;
import main.java.controller.event.events.ActionEvent;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.type.ActType;
import main.java.model.type.ActionEffectType;
import main.java.model.type.SkillType;

public class Soul_Harvest extends Skill {

    public static String NAME = "Soul Harvest";

    public Soul_Harvest() {
        super();
        setDescription("เมื่อมียูนิตศัตรูตายในสนาม นำครึ่งหนึ่งของความเสียหายที่ใช้ฆ่ายูนิตนั้นมาฮีลให้กับพันธมิตรที่เลือกหนึ่งคน");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.RECOVERY);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.ALLIES, 0)
        );
        spec    .addFields(
                new SkillInputSpec.InputField<String>("Damage that kills", SkillInputSpec.InputType.NUMBER, 0)
                , 0, 0);
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
        if (!skillTarget.getTarget(0).isEmpty()) {
            for (String name : skillTarget.getTarget(0)) {
                double xa = Double.parseDouble(skillTarget.getDecision(name, 0,0)) * 0.5;
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                                .effect(ActionEffectType.HEALTH_RECOVER, xa, 1)
                                .addActType(ActType.HEAL, ActType.HEALTH_RECOVER)
                                .build()
                );
            }
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

    }

    @Override
    public String getName() {
        return NAME;
    }
}

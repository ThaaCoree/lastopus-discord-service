package main.java.model.entity.skills.list.item.support;

import main.java.controller.CombatFlow;
import main.java.controller.event.events.ActionEvent;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.entity.units.Unit;
import main.java.model.type.ActType;
import main.java.model.type.ActionEffectType;
import main.java.model.type.SkillType;
import main.java.model.type.StatType;

import java.util.List;

public class Emperors_Command extends Skill {

    public static String NAME = "Emperor's Command";

    public Emperors_Command() {
        super();
        setDescription("สั่งให้พันธมิตรหนึ่งคนที่ได้ยินโจมตีหนึ่งครั้งโดยสร้างความเสียหายครึ่งหนึ่ง");
        setActionType("Combine");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.STRIKE);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 1)
        );
        spec    .addFields(
                new SkillInputSpec.InputField<String>("Calculation", SkillInputSpec.InputType.SELECT, 0)
                        .options(List.of("PATK","RATK"), 0)
                        .labelProvider(String::toString, 0)
        , 1, 0);
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
                Unit attacker = combatFlow.findUnit(name);

                double patk = attacker.getStats().get(StatType.PHYSICALATTACK).getFinal();
                double ratk = attacker.getStats().get(StatType.RANGEDATTACK).getFinal();
                if (skillTarget.getDecision(name,1,0).contains("PATK")) {
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), attacker, combatFlow.findUnit(skillTarget.getTarget(0)))
                                    .effect(ActionEffectType.DAMAGE_PHYSICAL, patk*0.5, 1)
                                    .addActType(ActType.ATTACK, ActType.STRIKE)
                                    .build()
                    );
                }

                if (skillTarget.getDecision(name,1,0).contains("RATK")) {
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), attacker, combatFlow.findUnit(skillTarget.getTarget(1)))
                                    .effect(ActionEffectType.DAMAGE_PHYSICAL, ratk*0.5, 1)
                                    .addActType(ActType.ATTACK, ActType.STRIKE)
                                    .build()
                    );
                }
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

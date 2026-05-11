package model.entity.skills.list.npc;

import main.controller.CombatFlow;
import main.controller.event.events.ActionEvent;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.entity.units.Unit;
import model.type.ActType;
import model.type.ActionEffectType;
import model.type.SkillType;
import util.LogWriterUtil;

public class Sanity_Switch extends Skill {

    public static String NAME = "Sanity, Switch";

    public Sanity_Switch() {
        super();
        setDescription("สลับพลังชีวิตของยูนิตพันธมิตรหนึ่งคนกับยูนิตพันธมิตรอีกหนึ่งคน จากนั้นเพิ่มพลังชีวิตปัจจุบันให้กับทั้งสองคน XA หน่วย");
        setActionType("Reaction");
        setManaCost(9);
        setCooldown(4);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.15*MATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.RECOVERY);
        getSkillMultiplier().get("XA").getTags().add(SkillType.HEALING);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.ALLIES, 0)
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.ALLIES, 1)
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
        if (!skillTarget.getTarget(0).isEmpty()) {
            double xa = getSkillMultiplier().get("XA").getResult();
            for (String name1 : skillTarget.getTarget(0)) {
                for (String name2 : skillTarget.getTarget(1)) {
                    Unit target1 = combatFlow.findUnit(name1);
                    Unit target2 = combatFlow.findUnit(name2);

                    double target1_health = target1.getHealth().getRemaining();
                    double target2_health = target2.getHealth().getRemaining();

                    target1.setRemainingHealth(0);
                    target2.setRemainingHealth(0);

                    target1.sumRemainingHealth(target2_health+xa);
                    target2.sumRemainingHealth(target1_health+xa);
                }
            }
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .addActType(ActType.CAST)
                            .build()
            );
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

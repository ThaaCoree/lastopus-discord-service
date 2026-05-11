package model.entity.skills.list.monster;

import main.controller.CombatFlow;
import main.controller.event.events.ActionEvent;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillMultiplier;
import model.entity.skills.SkillTarget;
import model.entity.units.Unit;
import model.type.ActType;
import model.type.ActionEffectType;
import model.type.SkillType;

public class Cross_Slash extends Skill {

    public static String NAME = "Cross Slash";

    public Cross_Slash() {
        super();
        setDescription("สร้างความเสียหายกายภาพ XA หน่วยให้กับเป้าหมาย เลือกพันธมิตรหนึ่งยูนิต พันธมิตรนั้นจู่โจมพร้อมกัน สร้างความเสียหายเท่ากัน");
        setActionType("Action");
        setManaCost(0);
        setCooldown(2);
        getSkillMultiplier().put("XA",new SkillMultiplier("2.4*PATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.ALLIES, 0)
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 1)
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

            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(1)))
                            .effect(ActionEffectType.DAMAGE_PHYSICAL, xa, 1)
                            .addActType(ActType.ATTACK, ActType.STRIKE)
                            .build()
            );

            for (String name : skillTarget.getTarget(0)) {
                Unit ally = combatFlow.findUnit(name);

                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), ally, combatFlow.findUnit(skillTarget.getTarget(1)))
                                .effect(ActionEffectType.DAMAGE_PHYSICAL, xa, 1)
                                .addActType(ActType.ATTACK, ActType.STRIKE)
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

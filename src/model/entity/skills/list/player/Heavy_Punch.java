package model.entity.skills.list.player;

import main.controller.CombatFlow;
import main.controller.event.events.ActionEvent;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.ActType;
import model.type.ActionEffectType;
import model.type.SkillType;
import util.LogWriterUtil;

public class Heavy_Punch extends Skill {

    public static String NAME = "Heavy Punch";

    public Heavy_Punch() {
        super();
        setDescription("พุ่งเป็นระยะสูงสุด XB เมตร จากนั้นโจมตีหนึ่งเป้าหมาย สร้างความเสียหายกายภาพ XA หน่วยและมอบสถานะ Stun ให้กับเป้าหมายเป็นเวลา XC รอบเทิร์น");
        setActionType("Action");
        setManaCost(4);
        setCooldown(3);
        getSkillMultiplier().put("XA",new SkillMultiplier("1.5*PATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);

        getSkillMultiplier().put("XB",new SkillMultiplier("1*MSPD"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.MOVEMENT);

        getSkillMultiplier().put("XC",new SkillMultiplier("1"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.DURATION);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
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
            int duration = (int) getSkillMultiplier().get("XB").getResult();
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Stun");
            double xa = getSkillMultiplier().get("XA").getResult();
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(),getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .condition(condition, duration)
                            .effect(ActionEffectType.DAMAGE_PHYSICAL, xa, 1)
                            .addActType(ActType.ATTACK, ActType.STRIKE, ActType.CONDITION_GIVEN)
                            .build());
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

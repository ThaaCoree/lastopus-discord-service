package model.entity.skills.list.monster;

import main.controller.CombatFlow;
import main.controller.event.events.ActionEvent;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.entity.units.Unit;
import model.type.ActType;
import model.type.ActionEffectType;
import model.type.SkillType;
import util.LogWriterUtil;

public class Double_Kick extends Skill {

    public static String NAME = "Double Kick";

    public Double_Kick() {
        super();
        setDescription("ยกขาขึ้นสองข้างแล้วถีบขาคู่ สร้างความเสียหายกายภาพ XA หน่วย\n" +
                "หากผู้ใช้สกิลนี้เดินด้วยสี่ขาจนคุ้นชิน สร้างความเสียหายกายภาพ XB แทน");
        setActionType("Action");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("1.6*PATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);

        getSkillMultiplier().put("XB",new SkillMultiplier("2.2*PATK"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.STRIKE);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
        );
        spec    .addFields(
                new SkillInputSpec.InputField<String>("Four-Legged", SkillInputSpec.InputType.BOOLEAN, 0)
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
        for (String name : skillTarget.getTarget(0)) {
            Unit target = combatFlow.findUnit(name);
            if (skillTarget.getDecision(name,0,0).equals("TRUE")) {
                double xb = getSkillMultiplier().get("XB").getResult();
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), target)
                                .effect(ActionEffectType.DAMAGE_PHYSICAL, xb, 1)
                                .addActType(ActType.ATTACK, ActType.STRIKE)
                                .build()
                );
            } else {
                double xa = getSkillMultiplier().get("XA").getResult();
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), target)
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

package model.entity.skills.list.player;

import main.controller.CombatFlow;
import main.controller.event.events.ActionEvent;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.ActType;
import model.type.ActionEffectType;
import model.type.SkillType;
import util.LogWriterUtil;

public class Thunderbolt extends Skill {

    public static String NAME = "Thunderbolt";

    public Thunderbolt() {
        super();
        setDescription("เคลื่อนย้ายในชั่วพริบตาไปยังพื้นที่กำหนด ระยะสูงสุด XB เมตรและสร้างความเสียหายกายภาพ XA หน่วยให้ศัตรูทั้งหมดไม่เกิน 3 ยูนิตในระยะหนึ่งเมตรรอบตัว ทั้งก่อนไปและเมื่อไปถึงแล้ว แต่ยูนิตหนึ่งไม่สามารถรับความเสียหายจากทั้งสองครั้งได้");
        setActionType("Action");
        setManaCost(6);
        setCooldown(3);
        getSkillMultiplier().put("XA",new SkillMultiplier("2.2*(PATK+(6*MSPD*((AGI+70)/70)))"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.MOVEMENT);
        getSkillMultiplier().get("XA").getTags().add(SkillType.AOE);

        getSkillMultiplier().put("XB",new SkillMultiplier("2*MSPD"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.MOVEMENT);
        getSkillMultiplier().get("XB").getTags().add(SkillType.DISTANCE);
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
        if (!skillTarget.getTarget(0).isEmpty()) {
            double xa = getSkillMultiplier().get("XA").getResult();
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .effect(ActionEffectType.DAMAGE_PHYSICAL, xa, 1)
                            .addActType(ActType.ATTACK, ActType.STRIKE, ActType.CAST)
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

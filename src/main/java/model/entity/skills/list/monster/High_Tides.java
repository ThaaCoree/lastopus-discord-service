package main.java.model.entity.skills.list.monster;

import main.java.controller.CombatFlow;
import main.java.controller.event.events.ActionEvent;
import main.java.model.entity.Conditions;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.entity.skills.SkillMultiplier;
import main.java.model.type.ActType;
import main.java.model.type.ActionEffectType;
import main.java.model.type.SkillType;

public class High_Tides extends Skill {

    public static String NAME = "High Tides";

    public High_Tides() {
        super();
        setDescription("ซัดน้ำออกไปเป็นคลื่นตรงๆ กว้าง XA เมตร สร้างความเสียหายกายภาพธาตุน้ำ XB หน่วย\n" +
                "ยูนิตที่ได้รับความเสียหายนี้ รับสถานะ Feet Hinder เป็นเวลา XC รอบเทิร์นด้วย\n" +
                "Feet Hinder : มี MSPD เป็น 2");
        setActionType("Action");
        setManaCost(0);
        setCooldown(2);
        getSkillMultiplier().put("XA",new SkillMultiplier("10"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIMIT);
        getSkillMultiplier().get("XA").getTags().add(SkillType.DISTANCE);

        getSkillMultiplier().put("XB",new SkillMultiplier("1.7*MATK"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.STRIKE);

        getSkillMultiplier().put("XC",new SkillMultiplier("2"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.DURATION);
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
        if (!skillTarget.getTarget(0).isEmpty()) {
            double xb = getSkillMultiplier().get("XB").getResult();
            int duration = (int) getSkillMultiplier().get("XC").getResult();
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Feet Hinder");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), getEnemies(combatFlow))
                            .effect(ActionEffectType.DAMAGE_PHYSICAL, xb, 1)
                            .condition(condition, duration)
                            .addActType(ActType.CAST, ActType.STRIKE)
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

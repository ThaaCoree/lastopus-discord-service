package main.java.model.entity.skills.list.item.offensive;

import main.java.controller.CombatFlow;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.entity.skills.SkillMultiplier;
import main.java.model.type.SkillType;

public class Piercing_Cold extends Skill {

    public static String NAME = "Piercing Cold";

    public Piercing_Cold() {
        super();
        setDescription("เมื่อยิงในระยะ XA เมตรหรือใกล้กว่า ลูกธนูจะเจาะทะลุเป้าหมายหากทำได้\n" +
                "เมื่อยิงเจาะทะลุเป้าหมาย เป้าหมายถัดไปที่ถูกโจมตี มีโอกาส XB ที่จะถูก Freeze\n" +
                "เมื่อผู้ใช้สวมใส่ Taily Ailligator Card ในช่อง Primary โอกาส Freeze จะกลายเป็น XC");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("3"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.DISTANCE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.REQUIREMENT);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.4"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XB").getTags().add(SkillType.WATER);
        getSkillMultiplier().get("XB").setPercent(true);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.8"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XC").getTags().add(SkillType.WATER);
        getSkillMultiplier().get("XC").setPercent(true);
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

    }

    @Override
    public String getName() {
        return NAME;
    }
}

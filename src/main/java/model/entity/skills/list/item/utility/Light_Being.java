package model.entity.skills.list.item.utility;

import controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.SkillType;

public class Light_Being extends Skill {

    public static String NAME = "Light Being";

    public Light_Being() {
        super();
        setDescription("เมื่อมียูนิตที่ได้รับบัพจากเวทมนตร์ ใช้งาน Reaction เพื่อทำให้บัพนั้นส่งผลกับตนเองด้วย\n" +
                "ใช้งานได้ไม่เกิน XA ครั้งต่อรอบเทิร์น");
        setActionType("Action");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.SPELL);
        getSkillMultiplier().put("XA",new SkillMultiplier("1"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIMIT);
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

package main.java.model.entity.skills.list.player;

import main.java.controller.CombatFlow;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.entity.skills.SkillMultiplier;
import main.java.model.type.SkillType;

public class Leda_Of_Canopus extends Skill {

    public static String NAME = "Leda of Canopus";

    public Leda_Of_Canopus() {
        super();
        setDescription("ใช้งานได้หลังจากไม่ใช้แอคชันประเภทใดเลยเป็นเวลาอย่างน้อยหนึ่งรอบเทิร์นหรือเมื่อบล็อกสำเร็จ สร้าง Starlit Waltz XB สแต็คที่จะไม่หายไปจนกว่าจะใช้งาน\n" +
                "เมื่อมี Starlit Waltz อยู่จะไม่สามารถใช้แอคชันประเภทใดๆนอกเหนือจาก Starlit Waltz หรือการบล็อกได้จนกว่าจะใช้งานแสต็คทั้งหมด เมื่อเริ่มใช้งานสแต็คครั้งแรกจะทำให้ Stack ทั้งหมดหายไปในตอนที่สิ้นสุดเทิร์น\n" +
                "Starlit Waltz : ใช้ Reaction เพื่อเคลื่อนที่เป็นเส้นตรงได้สูงสุด XA เมตรสามารถใช้เพื่อพุ่งขึ้นหรือลงในแนวดิ่งได้ สามารถใช้เพื่อหลบหลีกหรือใช้โจมตีผนวกความเร็วให้กับยูนิตที่พุ่งผ่านได้ สูงสุด XC ครั้งต่อยูนิตหนึ่งตัว คำนวนความเสียหายตอนใช้หมดครบ XB ครั้งที่การโจมตีผนวกความเร็วสูงสุด");
        setActionType("Action");
        setManaCost(10);
        setCooldown(6);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.15*MSPD"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.MOVEMENT);
        getSkillMultiplier().get("XA").getTags().add(SkillType.DISTANCE);

        getSkillMultiplier().put("XB",new SkillMultiplier("7"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.LIMIT);

        getSkillMultiplier().put("XC",new SkillMultiplier("3"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.LIMIT);
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

package model.entity.skills.list.player;

import main.controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.type.SkillType;

public class Starbound_Invoker extends Skill {

    public static String NAME = "Starbound Invoker";

    public Starbound_Invoker() {
        super();
        setDescription("The Iron Tomb ได้รับสกิลนี้เช่นกัน\n" +
                "ได้รับ Counter [Providence] (วิวรณ์)" +
                "สกิลนี้จะทำงานไม่ว่าผู้ถือครองสกิลจะอยู่ในสถานะใด (นอนหลับ, ถูกจองจำ ฯลฯ)\n" +
                "ได้รับ 2 วิวรณ์ ทุกครั้งเมื่อเริ่มเทิร์นของตนเอง เก็บสะสมได้ไม่จำกัด,\n" +
                "สามารถใช้งาน Action เพื่อภาวนา และรับอีก 2 วิวรณ์ได้\n" +
                "สามารถใช้งาน 2 Combine Action เพื่อภาวนา และรับอีก 1 วิวรณ์ได้\n" +
                "เมื่อจบรอบเทิร์น สิ่งมีชีวิตจาก The Fallen Paradise, \"Tezzeract\" จะแหวกมิติออกมาเพื่อใช้งานสกิลอื่นๆตามเงื่อนไข\n" +
                "ลำดับการออกแอคชันของ Tezzeract\n" +
                "Ω(x) Invocation\n" +
                "XYZ Invocation\n" +
                "หาก Akivili และ The Iron Tomb หมดสภาพต่อสู้, Tezzeract จะไม่สามารถใช้งานแอคชันใดๆได้\n\n" +
                "Tezzeract\n" +
                "สิ่งมีชีวิตจากสรวงสวรรค์โรยรายอีกตน มีลักษณะคล้ายกับ The Iron Tomb ขนาดมหึมา เมื่อจะแทรกแทรงมิติมันจะใช้กรงเล็บจากรยางค์ทั้งสองข้างของมันแหวกมิติออกมา ปรากฏอยู่เหนือน่านฟ้า และย้อมนภาให้เป็นสีม่วงเข้ม แต่สภาพนั้นคงอยู่ได้ไม่นาน มิติก็จะพยายามบีบรักษาตัวเองจนปิดเพื่อไล่มันกลับไปที่ที่มันควรอยู่อีกครั้ง");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.RITUAL);
        setManaReservePercent(0.6);
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

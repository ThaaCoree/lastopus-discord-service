package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.ActType;
import model.type.CounterName;
import model.type.SkillType;

public class Gate_Of_The_Fallen extends Skill {

    public static String NAME = "Gate of the Fallen Paradise";

    public Gate_Of_The_Fallen() {
        super();
        setDescription("The Iron Tomb ได้รับสกิลนี้เช่นกัน, ใช้ 4 วิวรณ์ เพื่อร่ายสกิลนี้,\n" +
                "เลือกยูนิตกี่ยูนิตก็ได้ในระยะ 1 เมตรจากผู้ร่าย วาร์ปทุกยูนิตที่เลือกไปยัง \"รอยรั่วมิติ\" ที่เลือกทันที, ทุกยูนิตที่ได้รับผลนี้จะไม่สามารถเคลื่อนที่ได้จนกว่าจะจบรอบเทิร์น\n" +
                "หากวาร์ปในระหว่างถูกจู่โจม จะถือว่าหลบหลีกการจู่โจมนั้นสำเร็จ สามารถหลบหลีกสกิลทั่วไปได้\n" +
                "\n" +
                "Divine Intervention:\n" +
                "Tezzeract จะทำการฉีกกระชากมิติ สร้าง \"รอยรั่วมิติ\" 1 จุด ที่เลือกบนแผนที่จนกว่าจะจบการต่อสู้\n" +
                "\n" +
                "Divine Invocation:\n" +
                "Tezzeract จะทำการฉีกกระชากมิติ สร้าง \"รอยรั่วมิติ\" 2 จุด ที่เลือกบนแผนที่จนกว่าจะจบการต่อสู้,\n" +
                "\n" +
                "ทุกรอยรั่วมิติ เชื่อมต่อถึงกัน สิ่งที่เข้าไปสามารถเลือกที่จะออกจากรอยรั่วมิติใดก็ได้\n" +
                "รอยรั่วมิติ คงอยู่พร้อมกันได้ XA จุด หากเกินกว่านี้ จะต้องเลือกบางจุดให้หายไป");
        setActionType("Reaction");
        setManaCost(18);
        setCooldown(2);
        setManaReservePercent(0.6);
        getSkillMultiplier().put("XA",new SkillMultiplier("4"));
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
        getUser().counterSum(CounterName.PROVIDENCE, -4);
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                        .addActType(ActType.CAST)
                        .build()
        );
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

    }

    @Override
    public String getName() {
        return NAME;
    }
}

package model.entity.skills.list.player;

import main.controller.CombatFlow;
import main.controller.event.events.ActionEvent;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.ActType;
import model.type.CounterName;
import model.type.SkillType;
import model.type.StatType;
import util.LogWriterUtil;

public class XYZ_Invocation extends Skill {

    public static String NAME = "XYZ Invocation";

    public XYZ_Invocation() {
        super();
        setDescription("เมื่อจบรอบเทิร์น Tezzeract จะพยายามฉีกกระชากมิติ สร้าง \"รอยรั่วมิติ\" หนึ่งตำแหน่งบนสนามแบบสุ่ม\n" +
                "\n" +
                "The Iron Tomb ได้รับสกิลนี้เช่นกัน, ใช้ 5 วิวรณ์ เพื่อร่ายสกิลนี้,\n" +
                "Tezzeract จะทำการฉีกกระชากมิติ สร้าง \"รอยรั่วมิติ\" สองตำแหน่งที่เลือกบนแผนที่จนกว่าจะจบการต่อสู้,\n" +
                "ทุกรอยรั่วมิติจะเชื่อมต่อถึงกัน สิ่งที่เข้าไปสามารถเลือกที่จะออกจากรอยรั่วมิติใดก็ได้,\n" +
                "ยูนิตที่ใช้งานรอยรั่วมิติจะถูก Invoke 1 Stack และได้รับผลนี้ไม่เกินรอบเทิร์นละหนึ่งครั้งต่อยูนิต,\n" +
                "รอยรั่วมิติ คงอยู่พร้อมกันได้ XA จุด หากเกินกว่านี้ จะต้องเลือกบางจุดให้หายไป");
        setActionType("Turn");
        setManaCost(11);
        setCooldown(3);
        getPureTags().add(SkillType.PHYSICAL);
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
        getUser().counterSum(CounterName.PROVIDENCE, -5);
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

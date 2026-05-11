package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

import java.util.List;

public class Over_The_Rainbow extends Skill implements SkillWithCondition {

    public static String NAME = "ABC Invocation";

    public Over_The_Rainbow() {
        super();
        setDescription(
                "The Iron Tomb ได้รับสกิลนี้เช่นกัน, ใช้ 4 วิวรณ์เพื่อร่ายสกิลนี้\n" +
                "Tezzeract ประทับร่าง 1 ครั้ง บนตัว Akivili หรือ The Iron Tomb คงอยู่จนกว่าจะจบการต่อสู้\n" +
                "การประทับร่างอื่น จะไม่ทำให้ร่างเดิมหายไป แต่จะเป็นการประทับเพิ่มในการต่อสู้ครั้งนี้\n" +
                "ทุกร่างสามารถถูกประทับซ้ำได้ไม่จำกัดจำนวนครั้ง\n" +
                "Akivili หรือ The Iron Tomb จะได้รับความสามารถตามร่างที่ประทับแล้วดังนี้\n" +
                "\n" +
                "Wicked Valkyrie\n" +
                "เมื่อประทับร่างนี้ครั้งแรก เพิ่ม 'ปีก' สองข้าง (ไม่มีผลอะไรในเชิง Combat)\n" +
                "จะได้รับความสามารถในการใช้งาน Action เพื่อฮีลตนเองหรือยูนิตที่เลือกด้วยการสัมผัสเป็นจำนวน XB หน่วยได้\n" +
                "อีกทั้งยังใช้งาน Combined Action เพื่อฮีลตนเองหรือยูนิตที่เลือกด้วยการสัมผัสเป็นจำนวน XC หน่วยได้\n" +
                "การประทับซ้ำครั้งถัด ๆ ไปจะทำให้ฮีลเพิ่มขึ้นอีก XD\n" +
                "การฮีลนี้เป็น Dark Heal ซึ่งจะส่งผลสร้างความเสียหายต่อยูนิตบางชนิดที่เกี่ยวข้องกับธาตุแสง และใช้งานฮีลอันเดดได้\n" +
                "\n" +
                "Made in Heaven\n" +
                "เมื่อประทับร่างนี้ครั้งแรก เพิ่ม 'รยางค์' สองข้าง (ไม่มีผลอะไรในเชิง Combat)\n" +
                "Akivili ได้รับ Speed XE หน่วย\n" +
                "การประทับซ้ำครั้งถัดๆไปจะทำให้ Akivili ได้รับ Speed เพิ่ม XE หน่วย\n" +
                "\n" +
                "Over the Rainbow\n" +
                "\n" +
                "เมื่อประทับร่างนี้ครั้งแรก เพิ่ม 'วงแหวน' หนึ่งวง (ไม่มีผลอะไรในเชิง Combat)\n" +
                "เพิ่มระยะการแสดงผลของ Starbound Immortal หนึ่งเมตร\n" +
                "การประทับซ้ำครั้งถัดๆไปจะทำให้เพิ่มระยะแสดงผลอีกหนึ่งเมตร");
        setActionType("Turn");
        setManaCost(8);
        setCooldown(2);
        setManaReservePercent(0.3);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.2*UsableHP"));

        getSkillMultiplier().put("XB",new SkillMultiplier("0.8*MATK"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.RECOVERY);
        getSkillMultiplier().get("XB").getTags().add(SkillType.HEALING);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.22*MATK"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.RECOVERY);
        getSkillMultiplier().get("XC").getTags().add(SkillType.HEALING);

        getSkillMultiplier().put("XD",new SkillMultiplier("0.5"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.RECOVERY);
        getSkillMultiplier().get("XD").getTags().add(SkillType.HEALING);

        getSkillMultiplier().put("XE",new SkillMultiplier("0.05*AGI"));
        getSkillMultiplier().get("XE").getTags().add(SkillType.DEFENSE);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.ALLIES, 0)
        );
        spec    .addFields(
                new SkillInputSpec.InputField<String>("Form", SkillInputSpec.InputType.SELECT, 0)
                        .options(List.of("Wicked Valkyrie","Made in Heaven", "Over the Rainbow"), 0)
                        .labelProvider(String::toString, 0)
        , 0, 0);
//                .addFields(
//                        new SkillInputSpec.InputField<String>("Damage", SkillInputSpec.InputType.NUMBER,1)
//                , 0, 1);
        return spec;
    }

    @Override
    public void calculateExtra() {
        if (!getUser().getName().equals("The Iron Tomb")) return;
        if (getUser().getCounter() == null) return;
        if (!getUser().getRawCounterMap().containsKey(CounterName.WING)) {
            getUser().getRawCounterMap().put(CounterName.WING,0.0);
            getUser().getCounter().put(CounterName.WING,0.0);
        }
        if (!getUser().getRawCounterMap().containsKey(CounterName.LIMB)) {
            getUser().getRawCounterMap().put(CounterName.LIMB,0.0);
            getUser().getCounter().put(CounterName.LIMB,0.0);
        }
        if (!getUser().getRawCounterMap().containsKey(CounterName.HALO)) {
            getUser().getRawCounterMap().put(CounterName.HALO,0.0);
            getUser().getCounter().put(CounterName.HALO,0.0);
        }
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        for (String name : skillTarget.getTarget(0)) {
            Unit target = combatFlow.findUnit(name);
            if (skillTarget.getDecision(name, 0 ,0).contains("Wicked Valkyrie")) {
                int duration = 99;
                Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Wicked Valkyrie");
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(),getUser(), target)
                                .condition(condition, duration)
                                .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                                .build());
            }

            if (skillTarget.getDecision(name, 0 ,0).contains("Made in Heaven")) {
                int duration = 99;
                Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Made in Heaven");
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(),getUser(), target)
                                .condition(condition, duration)
                                .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                                .build());
            }

            if (skillTarget.getDecision(name, 0 ,0).contains("Over the Rainbow")) {
                int duration = 99;
                Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Over the Rainbow");
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(),getUser(), target)
                                .condition(condition, duration)
                                .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                                .build());
            }
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        if (!getUser().getName().equals("The Iron Tomb")) return;
        if (getUser().getCounter() == null) return;

        Conditions condition = new Conditions("Made in Heaven");
        double xe = getSkillMultiplier().get("XE").getResult();
        condition.getStatModifiers(StatType.SPEED).setGlobalMult(xe);

        condition.setConditionType(ConditionType.NEUTRAL);
        condition.setConditionTierType(ConditionTierType.BOUND);


        Conditions condition2 = new Conditions("Over the Rainbow");
        condition2.setDescription("เพิ่มระยะแสดงผลของ Starbound Immortal; Euler's Constant หนึ่งเมตร");
        condition2.setConditionType(ConditionType.NEUTRAL);
        condition2.setConditionTierType(ConditionTierType.BOUND);

        Conditions condition3 = new Conditions("Wicked Valkyrie");
        condition3.setDescription("สามารถใช้งาน Dark Heal ได้ และเพิ่มผลฮีลมากขึ้นต่อจำนวนสถานะที่มี");
        condition3.setConditionType(ConditionType.NEUTRAL);
        condition3.setConditionTierType(ConditionTierType.BOUND);

        //remove and re-add to database
        addConditionToDatabase(condition,combatFlow);
        addConditionToDatabase(condition2,combatFlow);
        addConditionToDatabase(condition3,combatFlow);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
            ConditionManager.reapplyCondition(condition2, unit);
            ConditionManager.reapplyCondition(condition3, unit);
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

package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.EventBus;
import controller.event.events.ActionEvent;
import controller.event.events.ConditionInflictEvent;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillMultiplier;
import model.entity.skills.SkillTarget;
import model.entity.units.Unit;
import model.type.*;

import java.util.List;
import java.util.Map;

public class Starbound_Anomalus extends Skill {

    public static String NAME = "Starbound Anomalus";

    public Starbound_Anomalus() {
        super();
        setDescription("Status Check ของ Akivili และ The Iron Tomb จะใช้ WIS แทนทั้งหมด\n" +
                "เมื่อจะได้รับ Debuff ที่ระดับ Advanced หรือต่ำกว่า, เหตุการณ์ดังกล่าวหยุดการมอบ Debuff นั้น จากนั้นเสีย XA วิวรณ์\n" +
                "สามารถใช้งาน Combined Action และวิวรณ์ในจำนวนเท่ากันเพื่อลบ Debuff ของตนเองได้\n\n" +
                "Divine Intervention:\n" +
                "เลือก 1 ยูนิต วิเคราะห์มัน 1 ครั้ง\n" +
                "\n" +
                "Divine Invocation:\n" +
                "เลือก 1 ยูนิต ลบ Debuff ที่ระดับ Advanced หรือต่ำกว่าบนตัวมัน 2 อย่าง");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        setManaReservePercent(0.65);
        getSkillMultiplier().put("XA",new SkillMultiplier("2"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.REQUIREMENT);
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
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ConditionInflictEvent.class, EventPhase.MODIFY, 0, (ConditionInflictEvent event) -> {
            if (event.target != getUser()) return;
            Conditions condition = event.condition;
            if (condition.getConditionType().equals(ConditionType.DEBUFF)) {
                if (condition.getConditionTierType() == ConditionTierType.BOUND) return;
                if (condition.getConditionTierType() == ConditionTierType.UNDISPELLABLE) return;
                if (getUser().getCounter().get(CounterName.PROVIDENCE) < getSkillMultiplier().get("XA").getResult()) return;
                event.duration = 0;
                getUser().counterSum(CounterName.PROVIDENCE, -1 * getSkillMultiplier().get("XA").getResult());
                sendSkillTriggerEvent(combatFlow, "Starbound Anomalus Triggered, removing condition " + condition.getName());
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

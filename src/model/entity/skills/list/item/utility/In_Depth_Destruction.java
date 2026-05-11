package model.entity.skills.list.item.utility;

import javafx.beans.InvalidationListener;
import main.controller.CombatFlow;
import main.controller.event.events.RoundEvent;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.modifier.BasicModifier;
import model.type.CounterName;
import model.type.EventPhase;
import model.type.SkillType;
import model.type.StatType;

public class In_Depth_Destruction extends Skill {

    public static String NAME = "In Depth Destruction";

    public In_Depth_Destruction() {
        super();
        setDescription("มี Counter [In Depth]\n" +
                "ทุกรอบเทิร์นของการต่อสู้ที่ผ่านไป ผู้ใช้จะได้รับ In Depth เพิ่มขึ้นหนึ่งหน่วย\n" +
                "In Depth : เพิ่ม MATK XA ทับซ้อนสูงสุด XB หน่วย");
        setActionType("Action");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.025"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XA").setPercent(true);

        getSkillMultiplier().put("XB",new SkillMultiplier("10"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.LIMIT);
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
        if (getUser().getCounter() == null) return;
        if (!getUser().getRawCounterMap().containsKey(CounterName.IN_DEPTH)) {
            getUser().getRawCounterMap().put(CounterName.IN_DEPTH, 0.0);
            getUser().getCounter().put(CounterName.IN_DEPTH, 0.0);
        }

        if (getUser().getCounter() != null) {
            BasicModifier modifier = new BasicModifier();
            modifier.setFlat(getSkillMultiplier().get("XA").getResult() * getUser().getCounter().get(CounterName.IN_DEPTH));
            getSkillModifier().getStatModifiers().put(StatType.MAGICALATTACK, modifier);

            getUser().getCounter().addListener((InvalidationListener) change -> {
                getSkillModifier().getStatModifierSafe(StatType.MAGICALATTACK).setGlobalMult(getSkillMultiplier().get("XA").getResult() * getUser().getCounter().get(CounterName.IN_DEPTH));
                getUser().calculateStatAndStatus();
            });
        }
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        combatFlow.getEventBus().register(RoundEvent.class, EventPhase.POST, 0, event -> {
            getUser().counterIncrement(CounterName.IN_DEPTH);
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

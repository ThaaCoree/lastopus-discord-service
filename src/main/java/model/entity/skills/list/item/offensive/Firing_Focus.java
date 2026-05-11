package main.java.model.entity.skills.list.item.offensive;

import main.java.controller.CombatFlow;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.entity.skills.SkillMultiplier;
import main.java.model.type.CounterName;
import main.java.model.type.SkillType;
import main.java.model.type.StatType;

public class Firing_Focus extends Skill {

    public static String NAME = "Firing Focus";

    public Firing_Focus() {
        super();
        setDescription("ผู้ใช้จะมี Counter [Focus Fire]\n" +
                "เมื่อสร้างความเสียหายด้วยการโจมตีระยะไกล จะได้รับ Focus Fire หนึ่งสแต็คจนกว่าจะจบการต่อสู้\n" +
                "การได้รับความเสียหายจากการจู่โจมจะทำให้สูญเสีย Focus Fire ทั้งหมด\n" +
                "\n" +
                "Focus Fire : เพิ่ม XA RATK ทับซ้อนสูงสุด XB สแต็ค");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.04"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XA").setPercent(true);

        getSkillMultiplier().put("XB",new SkillMultiplier("4"));
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
        if (!getUser().getRawCounterMap().containsKey(CounterName.FOCUS_FIRE)) {
            getUser().getRawCounterMap().put(CounterName.FOCUS_FIRE,0.0);
            getUser().getCounter().put(CounterName.FOCUS_FIRE,0.0);
        }
        double xa = getSkillMultiplier().get("XA").getResult();
        double counter = getUser().getRawCounterMap().get(CounterName.FOCUS_FIRE);
        getSkillModifier().getStatModifierSafe(StatType.RANGEDATTACK).setGlobalMult(xa*counter);
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        getUser().counterIncrement(CounterName.FOCUS_FIRE);
        calculateAll();
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

    }

    @Override
    public String getName() {
        return NAME;
    }
}

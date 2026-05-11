package model.entity.skills.list.item.defensive;

import main.controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.CounterName;
import model.type.SkillType;
import model.type.StatType;

public class Lumen_Distortion extends Skill {

    public static String NAME = "Lumen Distortion";

    public Lumen_Distortion() {
        super();
        setDescription("ผู้ใช้จะมี Counter [Lumen Distort]\n" +
                "เมื่อได้รับความเสียหายเวท จะได้รับ Lumen Distort หนึ่งสแต็คจนกว่าจะจบการต่อสู้\n" +
                "\n" +
                "Lumen Distort : เพิ่ม XA MDEF ทับซ้อนสูงสุด XB สแต็ค");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.15"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XA").getTags().add(SkillType.DEFENSE);
        getSkillMultiplier().get("XA").setPercent(true);

        getSkillMultiplier().put("XB",new SkillMultiplier("5"));
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
        if (!getUser().getRawCounterMap().containsKey(CounterName.LUMEN_DISTORT)) {
            getUser().getRawCounterMap().put(CounterName.LUMEN_DISTORT,0.0);
            getUser().getCounter().put(CounterName.LUMEN_DISTORT,0.0);
        }
        double xa = getSkillMultiplier().get("XA").getResult();
        double counter = getUser().getRawCounterMap().get(CounterName.LUMEN_DISTORT);
        getSkillModifier().getStatModifierSafe(StatType.MAGICALDEFENSE).setGlobalMult(xa*counter);
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        getUser().counterIncrement(CounterName.LUMEN_DISTORT);
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

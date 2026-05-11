package model.entity.skills.list.item.utility;

import controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.CounterName;
import model.type.SkillType;
import model.type.StatType;

public class Unusual_Evolution extends Skill {

    public static String NAME = "Unusual Evolution";

    public Unusual_Evolution() {
        super();
        setDescription("ผู้ใช้จะมี Counter [Abnormal Evolution]\n" +
                "เมื่อได้รับบัพ จะได้รับ Abnormal Evolution หนึ่งสแต็คจนกว่าจะจบการต่อสู้\n" +
                "\n" +
                "Abnormal Evolution : เพิ่ม XA ATK และลด MaxHP XB ต่อสแต็ค");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.1"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XA").setPercent(true);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.05"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DRAWBACK);
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
        if (!getUser().getRawCounterMap().containsKey(CounterName.ABNORMAL_EVOLUTION)) {
            getUser().getRawCounterMap().put(CounterName.ABNORMAL_EVOLUTION,0.0);
            getUser().getCounter().put(CounterName.ABNORMAL_EVOLUTION,0.0);
        }
        double xa = getSkillMultiplier().get("XA").getResult();
        double xb = getSkillMultiplier().get("XB").getResult();
        double counter = getUser().getRawCounterMap().get(CounterName.ABNORMAL_EVOLUTION);
        getSkillModifier().getStatModifierSafe(StatType.PHYSICALATTACK).setGlobalMult(xa*counter);
        getSkillModifier().getStatModifierSafe(StatType.MAGICALATTACK).setGlobalMult(xa*counter);
        getSkillModifier().getStatModifierSafe(StatType.RANGEDATTACK).setGlobalMult(xa*counter);
        getSkillModifier().getStatModifierSafe(StatType.HEALTHPOINT).setGlobalMult(xb*counter*(-1));
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        getUser().counterIncrement(CounterName.ABNORMAL_EVOLUTION);
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

package model.entity.skills.list.item.utility;

import main.controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.CounterName;
import model.type.SkillType;
import model.type.StatType;

public class Soul_Drain extends Skill {

    public static String NAME = "Soul Drain";

    public Soul_Drain() {
        super();
        setDescription("มี Counter [Soul Drained]\n" +
                "ได้รับ Soul Drained เพิ่มขึ้นหนึ่งหน่วยเมื่อมีศพเกิดขึ้นในการต่อสู้\n" +
                "ATK เพิ่มขึ้น XA ต่อ Soul Drained ที่มี");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.07"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XA").setPercent(true);
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
        if (!getUser().getRawCounterMap().containsKey(CounterName.SOUL_DRAINED)) {
            getUser().getRawCounterMap().put(CounterName.SOUL_DRAINED,0.0);
            getUser().getCounter().put(CounterName.SOUL_DRAINED,0.0);
        }
        double xa = getSkillMultiplier().get("XA").getResult();
        double counter = getUser().getRawCounterMap().get(CounterName.SOUL_DRAINED);
        getSkillModifier().getStatModifierSafe(StatType.MAGICALATTACK).setGlobalMult(xa*counter);
        getSkillModifier().getStatModifierSafe(StatType.RANGEDATTACK).setGlobalMult(xa*counter);
        getSkillModifier().getStatModifierSafe(StatType.PHYSICALATTACK).setGlobalMult(xa*counter);
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        getUser().counterIncrement(CounterName.SOUL_DRAINED);
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

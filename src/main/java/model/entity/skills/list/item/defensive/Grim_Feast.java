package model.entity.skills.list.item.defensive;

import controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.CounterName;
import model.type.SkillType;
import model.type.StatType;

public class Grim_Feast extends Skill {

    public static String NAME = "Grim Feast";

    public Grim_Feast() {
        super();
        setDescription("มี Counter [Grim Feast]\n" +
                "ได้รับ Grim Feast เพิ่มขึ้นหนึ่งหน่วยตามจำนวนศพที่อยู่ในสนาม\n" +
                "Health Regen เพิ่มขึ้น XA หน่วยต่อ Grim Feast ที่มี");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.06*UsableHP"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.RECOVERY);
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
        if (!getUser().getRawCounterMap().containsKey(CounterName.GRIM_FEAST)) {
            getUser().getRawCounterMap().put(CounterName.GRIM_FEAST,0.0);
            getUser().getCounter().put(CounterName.GRIM_FEAST,0.0);
        }
        double xa = getSkillMultiplier().get("XA").getResult();
        double counter = getUser().getRawCounterMap().get(CounterName.GRIM_FEAST);
        getSkillModifier().getStatModifierSafe(StatType.HEALTHREGEN).setFlat(xa*counter);
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        getUser().counterIncrement(CounterName.GRIM_FEAST);
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

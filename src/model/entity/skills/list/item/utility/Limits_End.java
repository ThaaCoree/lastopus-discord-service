package model.entity.skills.list.item.utility;

import main.controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.CounterName;
import model.type.SkillType;
import model.type.StatType;

public class Limits_End extends Skill {

    public static String NAME = "Limits' End";

    public Limits_End() {
        super();
        setDescription("ได้รับ Counter [Used Mana]\n" +
                "Used Mana จะเพิ่มขึ้นตามจำนวนมานาที่ถูกใช้ไปในการต่อสู้ครั้งนี้\n" +
                "เพิ่ม MATK 1 หน่วยต่อ Used Mana ที่มี");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("1"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);
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
        if (!getUser().getRawCounterMap().containsKey(CounterName.USED_MANA)) {
            getUser().getRawCounterMap().put(CounterName.USED_MANA,0.0);
            getUser().getCounter().put(CounterName.USED_MANA,0.0);
        }
        double xa = getSkillMultiplier().get("XA").getResult();
        double counter = getUser().getRawCounterMap().get(CounterName.USED_MANA);
        getSkillModifier().getStatModifierSafe(StatType.MAGICALATTACK).setFlat(xa*counter);
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

    }

    @Override
    public String getName() {
        return NAME;
    }
}

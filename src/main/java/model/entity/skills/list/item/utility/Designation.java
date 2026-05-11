package model.entity.skills.list.item.utility;

import controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.SkillType;
import model.type.StatType;
import model.type.StatusType;

public class Designation extends Skill {

    public static String NAME = "Designation";

    public Designation() {
        super();
        setDescription("ได้รับ RATK เพิ่มขึ้นอีก XA หน่วยต่อ DEX");
        setActionType("Action");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.3"));
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
        double dex = getUser().getStatuses().get(StatusType.DEXTERITY).getFinal();
        double xa = getSkillMultiplier().get("XA").getResult();
        getSkillModifier().getStatModifierSafe(StatType.RANGEDATTACK).setFlat(xa*dex);
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

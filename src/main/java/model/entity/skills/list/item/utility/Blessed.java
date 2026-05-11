package model.entity.skills.list.item.utility;

import controller.CombatFlow;
import model.entity.ConditionInstance;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.ConditionType;
import model.type.SkillType;
import model.type.StatusType;

import java.util.Map;

public class Blessed extends Skill {

    public static String NAME = "Blessed";

    public Blessed() {
        super();
        setDescription("สเตตัสทั้งหมดเพิ่มขึ้น XA เมื่อมีบัพ\n" +
                "สเตตัสทั้งหมดลดลง XB เมื่อไม่มีบัพ");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.25"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XA").setPercent(true);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.25"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DRAWBACK);
        getSkillMultiplier().get("XB").setPercent(true);
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
        double xa = getSkillMultiplier().get("XA").getResult();
        double xb = getSkillMultiplier().get("XB").getResult()*(-1);
        boolean hasBuff = false;
        for (Map.Entry<Integer, ConditionInstance> entry : getUser().getConditionInstances().entrySet()) {
            if (entry.getValue().getCondition().getConditionType() == ConditionType.BUFF) {
                hasBuff = true;
                break;
            }
        }
        if (hasBuff) {
            getSkillModifier().getStatusModifierSafe(StatusType.INTELLIGENCE).setGlobalMult(xa);
            getSkillModifier().getStatusModifierSafe(StatusType.WISDOM).setGlobalMult(xa);
            getSkillModifier().getStatusModifierSafe(StatusType.STRENGTH).setGlobalMult(xa);
            getSkillModifier().getStatusModifierSafe(StatusType.DEXTERITY).setGlobalMult(xa);
            getSkillModifier().getStatusModifierSafe(StatusType.AGILITY).setGlobalMult(xa);
            getSkillModifier().getStatusModifierSafe(StatusType.VITALITY).setGlobalMult(xa);
            getSkillModifier().getStatusModifierSafe(StatusType.LUCK).setGlobalMult(xa);
        } else {
            getSkillModifier().getStatusModifierSafe(StatusType.INTELLIGENCE).setGlobalMult(xb);
            getSkillModifier().getStatusModifierSafe(StatusType.WISDOM).setGlobalMult(xb);
            getSkillModifier().getStatusModifierSafe(StatusType.STRENGTH).setGlobalMult(xb);
            getSkillModifier().getStatusModifierSafe(StatusType.DEXTERITY).setGlobalMult(xb);
            getSkillModifier().getStatusModifierSafe(StatusType.AGILITY).setGlobalMult(xb);
            getSkillModifier().getStatusModifierSafe(StatusType.VITALITY).setGlobalMult(xb);
            getSkillModifier().getStatusModifierSafe(StatusType.LUCK).setGlobalMult(xb);
        }
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

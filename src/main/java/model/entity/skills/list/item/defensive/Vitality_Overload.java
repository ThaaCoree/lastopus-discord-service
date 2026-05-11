package model.entity.skills.list.item.defensive;

import controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.SkillType;
import model.type.StatType;
import model.type.StatusType;

public class Vitality_Overload extends Skill {

    public static String NAME = "Vitality Overload";

    public Vitality_Overload() {
        super();
        setDescription("เพิ่ม HP XA หน่วยตามความต่างของค่า Vitality ที่มีมากกว่าสเตตัสอื่น");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("3"));
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
        double str = getUser().getStatuses().get(StatusType.STRENGTH).getFinal();
        double agi = getUser().getStatuses().get(StatusType.AGILITY).getFinal();
        double vit = getUser().getStatuses().get(StatusType.VITALITY).getFinal();
        double dex = getUser().getStatuses().get(StatusType.DEXTERITY).getFinal();
        double wis = getUser().getStatuses().get(StatusType.WISDOM).getFinal();
        double intel = getUser().getStatuses().get(StatusType.INTELLIGENCE).getFinal();
        double luk = getUser().getStatuses().get(StatusType.LUCK).getFinal();
        double flat_sum = 0;
        double xa = getSkillMultiplier().get("XA").getResult();

        if (vit > str) {
            flat_sum = vit-str;
        }
        if (vit > agi) {
            flat_sum = vit-agi;
        }
        if (vit > dex) {
            flat_sum = vit-dex;
        }
        if (vit > wis) {
            flat_sum = vit-wis;
        }
        if (vit > intel) {
            flat_sum = vit-intel;
        }
        if (vit > luk) {
            flat_sum = vit-luk;
        }
        flat_sum *= xa;

        getSkillModifier().getStatModifierSafe(StatType.HEALTHPOINT).setFlat(flat_sum);
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

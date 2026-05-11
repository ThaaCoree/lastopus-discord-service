package model.entity.skills.list.npc;

import main.controller.CombatFlow;
import model.entity.skills.*;
import model.modifier.BasicModifier;
import model.type.SkillType;
import model.type.StatType;
import util.LogWriterUtil;

public class Sorcery_Focus extends Skill {

    public static String NAME = "Sorcery Focus";

    public Sorcery_Focus() {
        super();
        setDescription("เพิ่ม MATK XA เพิ่มมานาที่ใช้ในทุกสกิลเป็น XB เท่า");
        setActionType("Passive");
        setManaReservePercent(0.5);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.6"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIMIT);
        getSkillMultiplier().get("XA").setPercent(true);

        getSkillMultiplier().put("XB",new SkillMultiplier("2"));
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
        double xa = getSkillMultiplier().get("XA").getResult();

        BasicModifier basicModifier = new BasicModifier();
        basicModifier.setGlobalMult(xa);
        getSkillModifier().getStatModifiers().put(StatType.MAGICALATTACK,basicModifier);

        if (!getIsActive()) return;

        double xb = getSkillMultiplier().get("XB").getResult();
        for (SkillInstance instance : getUser().getAllSkill().values()) {
            if (instance.getSkillData() == null) continue;
            double mp = instance.getSkillData().getManaCost();
            instance.getSkillData().setManaCost(mp*xb);
            instance.getSkillData().translateCost();
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

package main.java.model.entity.skills.list.npc;

import main.java.controller.CombatFlow;
import main.java.model.entity.skills.*;
import main.java.model.type.SkillType;

public class Radiance extends Skill {

    public static String NAME = "Radiance";

    public Radiance() {
        super();
        setDescription("Daybreak จะใช้ Combined Action และใช้ MP มากขึ้น XA ");
        setActionType("Passive");
        setManaReservePercent(0.45);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.5"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.DRAWBACK);
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
        if (getIsActive()) {
            for (SkillInstance instance : getUser().getAllSkill().values()) {
                Skill skill  = instance.getSkillData();
                if (skill == null) continue;
                if (skill.getName().equals("Daybreak")) {
                    skill.setActionType("Combine");
                    double cost = skill.getManaCost();
                    double xa = getSkillMultiplier().get("XA").getResult();
                    skill.setManaCost(cost*(1+xa));
                    skill.translateCost();
                }
            }
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

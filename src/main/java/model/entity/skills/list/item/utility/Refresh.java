package main.java.model.entity.skills.list.item.utility;

import main.java.controller.CombatFlow;
import main.java.model.entity.skills.*;
import main.java.model.type.SkillType;

import java.util.Map;

public class Refresh extends Skill {

    public static String NAME = "Refresh";

    public Refresh() {
        super();
        setDescription("ใช้งาน Action เพื่อลดคูลดาวน์ของเกือบทุกสกิลลงหนึ่งเทิร์น\n" +
                "ใช้งานได้ XA ครั้งต่อรอบเทิร์น");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.TIME);
        getSkillMultiplier().put("XA",new SkillMultiplier("1"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIMIT);
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
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        for (Map.Entry<String, SkillInstance> entry : getUser().getAllSkill().entrySet()) {
            int old_cd = entry.getValue().getOnCooldown();
            entry.getValue().setOnCooldown(old_cd-1);
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

    }

    @Override
    public String getName() {
        return NAME;
    }
}

package main.java.model.entity.skills.list.item.utility;

import main.java.controller.CombatFlow;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.entity.skills.SkillInstance;
import main.java.model.type.SkillType;

import java.util.Map;

public class Ascended_Hands extends Skill {

    public static String NAME = "Ascended Hands";

    public Ascended_Hands() {
        super();
        setDescription("ลดคูลดาวน์ของสกิลลำดับก่อนหน้าทั้งหมดลงหนึ่งเทิร์น เพิ่มการใช้ MP ของสกิลที่ถูกลดคูลดาวน์ขึ้นอีกเท่าตัว");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.RESOURCE);
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
        for (Map.Entry<String, SkillInstance> entry : getUser().getAllSkill().entrySet()) {
            if (entry.getValue().getSkillData() == null) continue;
            int cooldown = entry.getValue().getSkillData().getCooldown();
            double mp = entry.getValue().getSkillData().getManaCost();
            if (cooldown >= 1) {
                entry.getValue().getSkillData().setCooldown(cooldown - 1);
                entry.getValue().getSkillData().setManaCost(mp*2);
                entry.getValue().getSkillData().translateCooldown();
                entry.getValue().getSkillData().translateCost();
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

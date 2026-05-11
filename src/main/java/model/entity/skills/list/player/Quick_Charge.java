package main.java.model.entity.skills.list.player;

import main.java.controller.CombatFlow;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.type.CounterName;

public class Quick_Charge extends Skill {

    public static String NAME = "Quick Charge";

    public Quick_Charge() {
        super();
        setDescription("จ่าย Flicker 3 สแต็คเพื่อลดคูลดาวน์หนึ่งสกิลลงหนึ่งรอบเทิร์น หากจ่าย Flicker 6 สแต็ค จะทำให้ครั้งถัดไปที่ใช้งานสกิลนั้นไม่สูญเสียมานาด้วย");
        setActionType("-");
        setManaCost(0);
        setCooldown(0);
        setManaReservePercent(0.4);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.SKILLS, 0)
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
        getUser().counterSum(CounterName.FLICKER, 3*-1);
        for (String skill_name : skillTarget.getTarget(0)) {
            getUser().skillCooldownDecrement(skill_name);
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

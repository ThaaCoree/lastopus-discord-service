package main.java.model.entity.skills.list.monster;

import main.java.controller.CombatFlow;
import main.java.controller.event.events.ActionEvent;
import main.java.model.entity.Conditions;
import main.java.model.entity.skills.*;
import main.java.model.entity.units.Unit;
import main.java.model.type.*;

public class Sun_Blast extends Skill {

    public static String NAME = "Sun Blast";

    public Sun_Blast() {
        super();
        setDescription("ระเบิดแสงตะวันออกจากรอบตัวสร้างความเสียหายเวท XA หน่วยไปทั้งสนาม\n" +
                "ยูนิตที่ใช้งาน Reaction เพื่อหลบ จะได้รับความเสียหายแบบถากๆ แต่ได้รับสถานะ Stun เป็นเวลา XB รอบเทิร์น\n" +
                "ยูนิตที่ได้รับความเสียหายจังๆจะไม่ได้รับ Stun\n");
        setActionType("Action");
        setManaCost(0);
        setCooldown(2);
        getSkillMultiplier().put("XA",new SkillMultiplier("2.3*MATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);;
        getSkillMultiplier().get("XA").getTags().add(SkillType.AOE);

        getSkillMultiplier().put("XB",new SkillMultiplier("1"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DURATION);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
        );
        spec    .addFields(
                new SkillInputSpec.InputField<String>("Dodge?", SkillInputSpec.InputType.BOOLEAN, 0)
        , 0, 0);
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
        if (!skillTarget.getTarget(0).isEmpty()) {
            double xa = getSkillMultiplier().get("XA").getResult();
            int duration = (int) getSkillMultiplier().get("XB").getResult();
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Stun");

            for (String name : skillTarget.getTarget(0)) {
                Unit target = combatFlow.findUnit(name);
                if (skillTarget.getDecision(name, 0, 0).contains("TRUE")) {
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), target)
                                    .effect(ActionEffectType.DAMAGE_MAGICAL, xa/4, 1)
                                    .condition(condition, duration)
                                    .addActType(ActType.CAST, ActType.STRIKE)
                                    .build()
                    );
                } else {
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), target)
                                    .effect(ActionEffectType.DAMAGE_MAGICAL, xa, 1)
                                    .addActType(ActType.CAST, ActType.STRIKE)
                                    .build()
                    );
                }
            }

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

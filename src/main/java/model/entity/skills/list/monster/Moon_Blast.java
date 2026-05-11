package main.java.model.entity.skills.list.monster;

import main.java.controller.CombatFlow;
import main.java.controller.event.events.ActionEvent;
import main.java.manager.ConditionManager;
import main.java.model.entity.Conditions;
import main.java.model.entity.skills.*;
import main.java.model.entity.units.Unit;
import main.java.model.type.*;

public class Moon_Blast extends Skill implements SkillWithCondition {

    public static String NAME = "Moon Blast";

    public Moon_Blast() {
        super();
        setDescription("ระเบิดแสงจันทราออกจากรอบตัวสร้างความเสียหายเวท XA หน่วยไปทั้งสนาม\n" +
                "ยูนิตที่ใช้งาน Reaction เพื่อหลบ จะได้รับความเสียหายแบบถากๆ แต่ได้รับสถานะ Lingering Noise เป็นเวลา XB รอบเทิร์น\n" +
                "ยูนิตที่ได้รับความเสียหายจังๆจะไม่ได้รับ Lingering Noise\n" +
                "Lingering Noise : Damage Reduction ลดลง XC, Attack และ Cast Speed ลดลง XD");
        setActionType("Action");
        setManaCost(0);
        setCooldown(2);
        getSkillMultiplier().put("XA",new SkillMultiplier("2.3*MATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);;
        getSkillMultiplier().get("XA").getTags().add(SkillType.AOE);

        getSkillMultiplier().put("XB",new SkillMultiplier("2"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.3*(1+DebuffAMP)"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XC").setPercent(true);

        getSkillMultiplier().put("XD",new SkillMultiplier("0.4*(1+DebuffAMP)"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XD").setPercent(true);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
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
        if (!skillTarget.getTarget(0).isEmpty()) {
            double xa = getSkillMultiplier().get("XA").getResult();
            int duration = (int) getSkillMultiplier().get("XB").getResult();
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Lingering Noise");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .effect(ActionEffectType.DAMAGE_MAGICAL, xa, 1)
                            .condition(condition, duration)
                            .addActType(ActType.CAST, ActType.STRIKE)
                            .build()
            );
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Lingering Noise");
        condition.getStatModifiers(StatType.DAMAGEREDUCTION).setGlobalMult(getSkillMultiplier().get("XC").getResult()*(-1));
        condition.getStatModifiers(StatType.ATTACKSPEED).setGlobalMult(getSkillMultiplier().get("XD").getResult()*(-1));
        condition.getStatModifiers(StatType.CASTSPEED).setGlobalMult(getSkillMultiplier().get("XD").getResult()*(-1));

        condition.setConditionType(ConditionType.DEBUFF);
        condition.setConditionTierType(ConditionTierType.GENERAL);

        //remove and re-add to database
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(condition.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(condition.getName(), condition);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
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

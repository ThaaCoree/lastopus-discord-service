package model.entity.skills.list.monster;

import main.controller.CombatFlow;
import main.controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;
import util.LogWriterUtil;

public class Glyph_of_Aqua extends Skill implements SkillWithCondition {

    public static String NAME = "Glyph of Aqua";

    public Glyph_of_Aqua() {
        super();
        setDescription("วาดอักษรแห่งวารี ยิงกระสุนน้ำใส่ XA เป้าหมาย มันจะโค้งลงระเบิดออกที่พื้น สร้างความเสียหายเวทธาตุน้ำ XB หน่วยให้กับเป้าหมายที่อยู่ในรัศมี 0.5 เมตรของการระเบิดออก\n" +
                "ยูนิตที่ได้รับความเสียหายจากสกิลนี้ ได้รับ Glyph Slip ซึ่งลด XC ให้กับ CritChance เป็นเวลา XD เทิร์น");
        setActionType("Action");
        setManaCost(0);
        setCooldown(3);
        getSkillMultiplier().put("XA",new SkillMultiplier("3"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIMIT);

        getSkillMultiplier().put("XB",new SkillMultiplier("1.2*MATK"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XB").getTags().add(SkillType.AOE);
        getSkillMultiplier().get("XB").getTags().add(SkillType.WATER);
        getSkillMultiplier().get("XB").getTags().add(SkillType.ELEMENTAL);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.15*(1+DebuffAMP)"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XC").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XC").setPercent(true);

        getSkillMultiplier().put("XD",new SkillMultiplier("2"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.DURATION);
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
            double xb = getSkillMultiplier().get("XB").getResult();
            int duration = (int) getSkillMultiplier().get("XD").getResult();
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Glyph Slip");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .effect(ActionEffectType.DAMAGE_MAGICAL, xb, 1)
                            .condition(condition, duration)
                            .addActType(ActType.CAST, ActType.STRIKE)
                            .build()
            );
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Glyph Slip");
        condition.getStatModifiers(StatType.CRITCHANCE).setGlobalMult(getSkillMultiplier().get("XC").getResult()*(-1));

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

package model.entity.skills.list.monster;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

public class Glyph_of_Flame extends Skill implements SkillWithCondition {

    public static String NAME = "Glyph of Flame";

    public Glyph_of_Flame() {
        super();
        setDescription("วาดอักษรแห่งเพลิง ระเบิดเปลวเพลิงออกมารอบตัว สร้างความเสียหายเวทธาตุไฟ XA หน่วยให้กับยูนิตศัตรูทั้งหมดในแผนที่\n" +
                "ยูนิตที่ได้รับความเสียหายจากสกิลนี้ รับสถานะ Inferno Melt ซึ่งลด VIT XB เป็นเวลา XC เทิร์น");
        setActionType("Action");
        setManaCost(0);
        setCooldown(3);
        getSkillMultiplier().put("XA",new SkillMultiplier("1.7*MATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.FIRE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.ELEMENTAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.AOE);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.2*(1+DebuffAMP)"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XB").getTags().add(SkillType.FIRE);
        getSkillMultiplier().get("XB").getTags().add(SkillType.ELEMENTAL);
        getSkillMultiplier().get("XB").setPercent(true);

        getSkillMultiplier().put("XC",new SkillMultiplier("2"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.DURATION);
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
        double xa = getSkillMultiplier().get("XA").getResult();
        int duration = (int) getSkillMultiplier().get("XC").getResult();
        Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Inferno Melt");
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(), getUser(), getEnemies(combatFlow))
                        .effect(ActionEffectType.DAMAGE_MAGICAL, xa, 1)
                        .addActType(ActType.CAST, ActType.STRIKE)
                        .condition(condition, duration)
                        .build()
        );
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        double buffAMP = 1+getUser().getStats().get(StatType.BUFFAMPLIFIER).getFinal();
        double debuffAMP = 1+getUser().getStats().get(StatType.DEBUFFAMPLIFIER).getFinal();
        Conditions condition = new Conditions("Inferno Melt");
        condition.getStatusModifiers(StatusType.VITALITY).setGlobalMult(getSkillMultiplier().get("XB").getResult()*(-1));

        condition.setConditionType(ConditionType.DEBUFF);
        condition.setConditionTierType(ConditionTierType.ADVANCED);

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

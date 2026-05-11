package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;
import util.LogWriterUtil;

public class Shadow_Field extends Skill implements SkillWithCondition {

    public static String NAME = "Shadow Field";

    public Shadow_Field() {
        super();
        setDescription("สร้างหลักเงาซึ่งมีพลังชีวิต XA ขึ้นตรงพื้นที่เป้าหมาย ตราบเท่าที่หลักเงายังอยู่ในพื้นที่ พันธมิตรที่มีเงาทั้งหมดในสนามจะได้รับ PATK, MATK, RATK XB หน่วยต่อหลัก\n" +
                "เมื่อสร้างครบ 3 หลัก จะสร้าง Phantom Zone ทับซ้อนขึ้น เขตแดนเงาจะมอบ PATK, MATK, RATK เพิ่มให้อีก XC หน่วย และมอบ Evasion ให้ XD หน่วย\n" +
                "ยูนิตศัตรูที่อยู่ในเขตแดนเงาจะถูกลดความสามารถในการตรวจจับลง");
        setActionType("Action");
        setManaCost(6);
        setCooldown(2);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.35*UsableHP"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.FIELD);
        getSkillMultiplier().get("XA").getTags().add(SkillType.SHADOW);

        getSkillMultiplier().put("XB",new SkillMultiplier("(STR*0.25+INT*0.25+DEX*0.25)*(1+BuffAMP)"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XB").getTags().add(SkillType.AOE);
        getSkillMultiplier().get("XB").getTags().add(SkillType.SHADOW);

        getSkillMultiplier().put("XC",new SkillMultiplier("(STR*0.3+INT*0.3+DEX*0.3)*(1+BuffAMP)"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XC").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XC").getTags().add(SkillType.AOE);
        getSkillMultiplier().get("XC").getTags().add(SkillType.SHADOW);

        getSkillMultiplier().put("XD",new SkillMultiplier("AGI*5.6*(1+BuffAMP)"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XD").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XD").getTags().add(SkillType.AOE);
        getSkillMultiplier().get("XD").getTags().add(SkillType.SHADOW);
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
        int duration = 99;
        getUser().counterIncrement(CounterName.SHADOW_TOTEM);
        for (Unit ally : getAllies(combatFlow)) {
            if (ally.hasCondition("Shadow Field")) {
                ConditionManager.removeCondition(ally,"Shadow Field");
            }
        }
        Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Shadow Field");
        LogWriterUtil.log("Shadow Totem has been summoned", combatFlow.getTurnCount());
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(), getUser(), getAllies(combatFlow))
                        .condition(condition, duration)
                        .addActType(ActType.CAST)
                        .build()
        );
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        double buffAMP = 1+getUser().getStats().get(StatType.BUFFAMPLIFIER).getFinal();
        double debuffAMP = 1+getUser().getStats().get(StatType.DEBUFFAMPLIFIER).getFinal();
        double count = getUser().getCounter().get(CounterName.SHADOW_TOTEM);
        double xb = getSkillMultiplier().get("XB").getResult();
        double xc = getSkillMultiplier().get("XC").getResult();
        double xd = getSkillMultiplier().get("XD").getResult();
        Conditions condition = new Conditions("Shadow Field");
        condition.getStatModifiers(StatType.PHYSICALATTACK).setFlat(xb*count);
        condition.getStatModifiers(StatType.MAGICALATTACK).setFlat(xb*count);
        condition.getStatModifiers(StatType.RANGEDATTACK).setFlat(xb*count);
        if (count >= 3) {
            condition.getStatModifiers(StatType.PHYSICALATTACK).sumFlat(xc);
            condition.getStatModifiers(StatType.MAGICALATTACK).sumFlat(xc);
            condition.getStatModifiers(StatType.RANGEDATTACK).sumFlat(xc);
            condition.getStatModifiers(StatType.EVASION).setFlat(xd);
        }

        condition.setConditionType(ConditionType.BUFF);
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

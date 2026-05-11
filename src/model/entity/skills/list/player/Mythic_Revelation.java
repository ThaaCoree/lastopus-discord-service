package model.entity.skills.list.player;

import main.controller.CombatFlow;
import main.controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;
import util.LogWriterUtil;

public class Mythic_Revelation extends Skill implements SkillWithCondition {

    public static String NAME = "Mythic Revelation";

    public Mythic_Revelation() {
        super();
        setDescription("เลือกพื้นที่ 3x3 เมตร กระโดดขึ้นไปบนฟ้า สร้างคลื่นกระแทกไปยังพื้นที่ที่กำหนด สร้างความเสียหายเวทมนตร์ธาตุลม ธาตุน้ำ หรือธาตุแสง XA หน่วย ให้กับศัตรูทั้งหมดในพื้นที่\n" +
                "เป้าหมายที่ได้รับความเสียหายนี้จะได้รับสถานะ Elemental Drain เป็นเวลา XB รอบเทิร์น ซึ่งลด Attack Speed, Cast Speed ลง XC และ ลด Evasion XD\n" +
                "หากมี Fragment of Seidr อย่างน้อย XE สแต็ค เพิ่มระยะส่งผลเป็น 4x4 เมตรแทน\n" +
                "หลังจากสร้างความเสียหายสำเร็จ ผู้ใช้สามารถเลือกเทเลพอร์ตไปยังพื้นที่เป้าหมายตรงจุดไหนก็ได้");
        setActionType("Action");
        setManaCost(12);
        setCooldown(3);
        getSkillMultiplier().put("XA",new SkillMultiplier("1.6*MATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.AOE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.ELEMENTAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.WIND);
        getSkillMultiplier().get("XA").getTags().add(SkillType.WATER);
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIGHT);

        getSkillMultiplier().put("XB",new SkillMultiplier("2"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.15*(1+DebuffAMP)"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XC").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XC").setPercent(true);

        getSkillMultiplier().put("XD",new SkillMultiplier("0.22*(1+DebuffAMP)"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XD").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XD").setPercent(true);

        getSkillMultiplier().put("XE",new SkillMultiplier("8"));
        getSkillMultiplier().get("XE").getTags().add(SkillType.REQUIREMENT);
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
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Elemental Drain");
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
        Conditions condition = new Conditions("Elemental Drain");
        condition.getStatModifiers(StatType.CASTSPEED).setGlobalMult(getSkillMultiplier().get("XC").getResult() * (-1));
        condition.getStatModifiers(StatType.ATTACKSPEED).setGlobalMult(getSkillMultiplier().get("XC").getResult() * (-1));
        condition.getStatModifiers(StatType.EVASION).setGlobalMult(getSkillMultiplier().get("XD").getResult() * (-1));

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

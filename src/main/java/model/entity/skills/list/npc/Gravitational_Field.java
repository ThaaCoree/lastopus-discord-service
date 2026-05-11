package model.entity.skills.list.npc;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

public class Gravitational_Field extends Skill implements SkillWithCondition {

    public static String NAME = "Gravitational Field";

    public Gravitational_Field() {
        super();
        setDescription("เลือกพื้นที่ 5x5 ในสนาม สร้างความเสียหายเวท XA หน่วยให้กับทุกยูนิตในพื้นที่ มอบสถานะ Gravitational ให้กับพวกมันเป็นเวลา XB เทิร์น\n" +
                "ในระหว่างที่ยังมียูนิตรับสถานะ Gravitational อยู่ STRIKEN จะไม่มี Combined Action ตั้งต้นเมื่อเริ่มเทิร์น\n" +
                "สามารถใช้งาน Reaction เพื่อยกเลิกสถานะ Gravitational ที่หนึ่งยูนิตได้\n" +
                "สถานะ Gravitational : ลด MSPD XC และไม่สามารถใช้งาน Combined Action ได้");
        setActionType("Action");
        setManaCost(13);
        setCooldown(5);
        getSkillMultiplier().put("XA",new SkillMultiplier("2.1*MATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.AOE);

        getSkillMultiplier().put("XB",new SkillMultiplier("2"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.5*(1+DebuffAMP)"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XC").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XC").setPercent(true);
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
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Gravitational");
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
        Conditions condition = new Conditions("Gravitational");
        condition.getStatModifiers(StatType.MOVEMENTSPEED).setGlobalMult(getSkillMultiplier().get("XC").getResult()*(-1));

        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.GENERAL);
        condition.setDescription("ไม่สามารถใช้งาน Combined Action ได้");

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

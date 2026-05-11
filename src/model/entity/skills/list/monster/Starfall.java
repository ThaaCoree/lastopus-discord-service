package model.entity.skills.list.monster;

import main.controller.CombatFlow;
import main.controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;
import util.LogWriterUtil;

public class Starfall extends Skill implements SkillWithCondition {

    public static String NAME = "Starfall";

    public Starfall() {
        super();
        setDescription("พุ่งเข้าใส่เป้าหมายด้วยความเร็วสูง โจมตีในรัศมี 5 เมตร เป้าหมายที่อยู่ในรัศมีโจมตีจะโดนจังๆ\n" +
                "สร้างความเสียหายกายภาพ XA หน่วย เป้าหมายที่โดนจังๆรับความเสียหายกายภาพเพิ่มเติม XB หน่วย รับสถานะ Death Mark เป็นเวลา XD เทิร์นและถือว่าการโจมตีเป็นคริติคอล\n" +
                "สถานะ Death Mark ลด PDEF และ MDEF ลง XC");
        setActionType("Action");
        setManaCost(0);
        setCooldown(3);
        getSkillMultiplier().put("XA",new SkillMultiplier("2.2*PATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.AOE);

        getSkillMultiplier().put("XB",new SkillMultiplier("1.5*PATK"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XB").getTags().add(SkillType.AOE);
        getSkillMultiplier().get("XB").getTags().add(SkillType.CRITICAL);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.5*(1+DebuffAMP)"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.PHYSICAL);
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
            double xa = getSkillMultiplier().get("XA").getResult();
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .effect(ActionEffectType.DAMAGE_PHYSICAL, xa, 1)
                            .addActType(ActType.ATTACK, ActType.STRIKE)
                            .build()
            );
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        double debuffAMP = 1+getUser().getStats().get(StatType.DEBUFFAMPLIFIER).getFinal();
        Conditions condition = new Conditions("Death Mark");
        condition.getStatModifiers(StatType.PHYSICALDEFENSE).setGlobalMult(getSkillMultiplier().get("XC").getResult()*(-1));
        condition.getStatModifiers(StatType.MAGICALDEFENSE).setGlobalMult(getSkillMultiplier().get("XC").getResult()*(-1));

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

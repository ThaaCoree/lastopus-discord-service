package model.entity.skills.list.player;

import main.controller.CombatFlow;
import main.controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;
import util.LogWriterUtil;

public class Celestial_Moonlit_Dance extends Skill implements SkillWithCondition {

    public static String NAME = "Celestial Moonlit Dance";

    public Celestial_Moonlit_Dance() {
        super();
        setDescription("เดี่ยว : ระบำดาบต่อเนื่อง ลด PDEF ของเป้าหมายลง XA หน่วย เป็นระยะเวลา XC รอบเทิร์นแล้วสร้างความเสียหายกายภาพ XB หน่วย 3 ครั้ง\n" +
                "คู่ : สร้างความเสียหายเพิ่มอีก 3 ครั้ง และสลับตำแหน่งทั้ง 2 ร่าง");
        setActionType("Action");
        setManaCost(4);
        setCooldown(4);

        getSkillMultiplier().put("XA",new SkillMultiplier("1.4*PATK*(1+DebuffAMP)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.COMBO);
        getSkillMultiplier().get("XA").getTags().add(SkillType.DEBUFF);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.7*PATK"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XB").getTags().add(SkillType.COMBO);

        getSkillMultiplier().put("XC",new SkillMultiplier("2"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.DURATION);
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
            int duration = (int) getSkillMultiplier().get("XC").getResult();
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Celestial Moonlit Dance");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .condition(condition, duration)
                            .addActType(ActType.CONDITION_GIVEN)
                            .build());

                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                                .effect(ActionEffectType.DAMAGE_PHYSICAL, xb, 3)
                                .addActType(ActType.ATTACK, ActType.STRIKE)
                                .build()
                );
    }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Celestial Moonlit Dance");
        condition.getStatModifiers(StatType.PHYSICALDEFENSE).setFlat(getSkillMultiplier().get("XA").getResult()*(-1));

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

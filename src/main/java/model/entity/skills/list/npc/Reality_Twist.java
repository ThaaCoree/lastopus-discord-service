package model.entity.skills.list.npc;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

import java.util.List;

public class Reality_Twist extends Skill implements SkillWithCondition {

    public static String NAME = "Reality, Twist";

    public Reality_Twist() {
        super();
        setDescription("พลิกผันความเป็นจริงบางส่วน เลือกมอบสถานะหนึ่งอย่างให้กับเป้าหมายเป็นเวลา XA เทิร์น\n" +
                "Twisted Force : เพิ่ม ATK XB ทำให้ Evasion และ MSPD เป็น 0 ทำให้ ATKSPD และ CastSPD เป็น 100%\n" +
                "Twisted Wrath : เพิ่ม CritDamage XC ทำให้ CritChance เป็น 0%\n" +
                "Twisted Haste : เพิ่ม MSPD XD, ATKSPD และ CastSPD XE ทำให้ PDEF และ MDEF เป็น 0\n" +
                "Twisted Dense : เพิ่ม PBlock และ MBlock XF, PDEF และ MDEF XG ทำให้ ATK เป็น 0\n" +
                "Twisted Bless : เพิ่ม HealAMP XH, BuffAMP และ DebuffAMP XI ทำให้ ManaRegen สลับขั้ว\n" +
                "Twisted Sanity : ฟื้นฟู HP จนเต็ม ทำให้ MP ติดลบเท่ากับ MaxMP");
        setActionType("Action");
        setManaCost(12);
        setCooldown(2);
        getSkillMultiplier().put("XA",new SkillMultiplier("1"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XB",new SkillMultiplier("1*(1+BuffAMP)"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XB").setPercent(true);

        getSkillMultiplier().put("XC",new SkillMultiplier("3*(1+BuffAMP)"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XC").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XC").setPercent(true);

        getSkillMultiplier().put("XD",new SkillMultiplier("1.5*(1+BuffAMP)"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XD").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XD").setPercent(true);

        getSkillMultiplier().put("XE",new SkillMultiplier("1*(1+BuffAMP)"));
        getSkillMultiplier().get("XE").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XE").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XE").setPercent(true);

        getSkillMultiplier().put("XF",new SkillMultiplier("1.5*(1+BuffAMP)"));
        getSkillMultiplier().get("XF").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XF").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XF").setPercent(true);

        getSkillMultiplier().put("XG",new SkillMultiplier("1.5*(1+BuffAMP)"));
        getSkillMultiplier().get("XG").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XG").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XG").setPercent(true);

        getSkillMultiplier().put("XH",new SkillMultiplier("1*(1+BuffAMP)"));
        getSkillMultiplier().get("XH").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XH").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XH").setPercent(true);

        getSkillMultiplier().put("XI",new SkillMultiplier("0.5*(1+BuffAMP)"));
        getSkillMultiplier().get("XI").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XI").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XI").setPercent(true);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
        );
        spec    .addFields(
                new SkillInputSpec.InputField<String>("Condition", SkillInputSpec.InputType.SELECT, 0)
                        .options(List.of("Force","Wrath","Haste","Dense","Bless","Sanity"), 0)
                        .labelProvider(String::toString, 0)
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
        for (String name : skillTarget.getTarget(0)) {
            Unit target = combatFlow.findUnit(name);
            int duration = (int) getSkillMultiplier().get("XA").getResult();

            if (skillTarget.getDecision(name, 0,0).equals("Force")) {
                Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Twisted Force");
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(),getUser(), target)
                                .condition(condition, duration)
                                .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                                .build());
            }
            if (skillTarget.getDecision(name, 0,0).equals("Wrath")) {
                Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Twisted Wrath");
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(),getUser(), target)
                                .condition(condition, duration)
                                .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                                .build());
            }
            if (skillTarget.getDecision(name, 0,0).equals("Haste")) {
                Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Twisted Haste");
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(),getUser(), target)
                                .condition(condition, duration)
                                .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                                .build());
            }
            if (skillTarget.getDecision(name, 0,0).equals("Dense")) {
                Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Twisted Dense");
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(),getUser(), target)
                                .condition(condition, duration)
                                .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                                .build());
            }
            if (skillTarget.getDecision(name, 0,0).equals("Bless")) {
                Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Twisted Bless");
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(),getUser(), target)
                                .condition(condition, duration)
                                .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                                .build());
            }
            if (skillTarget.getDecision(name, 0,0).equals("Sanity")) {
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(),getUser(), target)
                                .effect(ActionEffectType.HEALTH_RECOVER, target.getHealth().getUsable()*99, 1)
                                .addActType(ActType.CAST)
                                .build());
                double target_max_mana = target.getMana().getUsable();
                target.setRemainingMana(target_max_mana * -1);
            }
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        double xb = getSkillMultiplier().get("XB").getResult();
        double xc = getSkillMultiplier().get("XC").getResult();
        double xd = getSkillMultiplier().get("XD").getResult();
        double xe = getSkillMultiplier().get("XE").getResult();
        double xf = getSkillMultiplier().get("XF").getResult();
        double xg = getSkillMultiplier().get("XG").getResult();
        double xh = getSkillMultiplier().get("XH").getResult();
        double xi = getSkillMultiplier().get("XI").getResult();
        Conditions force = new Conditions("Twisted Force");
        force.getStatModifiers(StatType.PHYSICALATTACK).setGlobalMult(xb);
        force.getStatModifiers(StatType.MAGICALATTACK).setGlobalMult(xb);
        force.getStatModifiers(StatType.RANGEDATTACK).setGlobalMult(xb);
        force.getStatModifiers(StatType.MOVEMENTSPEED).setOverride(0);
        force.getStatModifiers(StatType.EVASION).setOverride(0);
        force.getStatModifiers(StatType.ATTACKSPEED).setOverride(1);
        force.getStatModifiers(StatType.CASTSPEED).setOverride(1);
        force.setConditionType(ConditionType.NEUTRAL);
        force.setConditionTierType(ConditionTierType.BOUND);

        Conditions wrath = new Conditions("Twisted Wrath");
        wrath.getStatModifiers(StatType.CRITDAMAGE).setGlobalMult(xc);
        wrath.getStatModifiers(StatType.CRITCHANCE).setOverride(0);
        wrath.setConditionType(ConditionType.NEUTRAL);
        wrath.setConditionTierType(ConditionTierType.BOUND);

        Conditions haste = new Conditions("Twisted Haste");
        haste.getStatModifiers(StatType.MOVEMENTSPEED).setGlobalMult(xd);
        haste.getStatModifiers(StatType.ATTACKSPEED).setGlobalMult(xe);
        haste.getStatModifiers(StatType.CASTSPEED).setGlobalMult(xe);
        haste.getStatModifiers(StatType.PHYSICALDEFENSE).setOverride(0);
        haste.getStatModifiers(StatType.MAGICALDEFENSE).setOverride(0);
        haste.setConditionType(ConditionType.NEUTRAL);
        haste.setConditionTierType(ConditionTierType.BOUND);

        Conditions dense = new Conditions("Twisted Dense");
        dense.getStatModifiers(StatType.MAGICALBLOCK).setGlobalMult(xf);
        dense.getStatModifiers(StatType.PHYSICALBLOCK).setGlobalMult(xf);
        dense.getStatModifiers(StatType.PHYSICALDEFENSE).setGlobalMult(xg);
        dense.getStatModifiers(StatType.MAGICALDEFENSE).setGlobalMult(xg);
        dense.getStatModifiers(StatType.PHYSICALATTACK).setOverride(0);
        dense.getStatModifiers(StatType.MAGICALATTACK).setOverride(0);
        dense.getStatModifiers(StatType.RANGEDATTACK).setOverride(0);
        dense.setConditionType(ConditionType.NEUTRAL);
        dense.setConditionTierType(ConditionTierType.BOUND);

        Conditions bless = new Conditions("Twisted Bless");
        bless.getStatModifiers(StatType.HEALAMPLIFIER).setGlobalMult(xh);
        bless.getStatModifiers(StatType.BUFFAMPLIFIER).setGlobalMult(xi);
        bless.getStatModifiers(StatType.DEBUFFAMPLIFIER).setGlobalMult(xi);
        bless.getStatModifiers(StatType.MANAREGEN).setGlobalMult(-2);
        bless.setConditionType(ConditionType.NEUTRAL);
        bless.setConditionTierType(ConditionTierType.BOUND);

        //remove and re-add to database
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(force.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(force.getName(), force);
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(wrath.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(wrath.getName(), wrath);
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(haste.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(haste.getName(), haste);
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(dense.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(dense.getName(), dense);
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(bless.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(bless.getName(), bless);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(force, unit);
            ConditionManager.reapplyCondition(wrath, unit);
            ConditionManager.reapplyCondition(haste, unit);
            ConditionManager.reapplyCondition(dense, unit);
            ConditionManager.reapplyCondition(bless, unit);
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

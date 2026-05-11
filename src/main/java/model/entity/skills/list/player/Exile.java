package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Exile extends Skill implements SkillWithCondition {

    public static String NAME = "Exile";

    public Exile() {
        super();
        setDescription("ครอบครอง Counter [Impale]\n" +
                "เมื่อ Scarlet ใช้งาน Divergent ตรงตามเงื่อนไขอย่างต่อเนื่องภายในรอบเทิร์นที่ผ่านมา สกิลนี้จะสามารถใช้งานได้ เปลี่ยนแปลงลักษณะกายภาพของ Scarlet รวมถึงเปลี่ยนแปลงอาวุธกำลังใช้งาน\n" +
                "\n" +
                "ร่างและอาวุธที่ได้มาจะคงอยู่ XB รอบเทิร์น\n" +
                "\n" +
                "Melovas ( Maelstrom + Icarus ) \n" +
                "สการ์เล็ตจะกลายเป็น Ranger พร้อมกับหน้าไม้ยักษ์ เพิ่ม CritDMG XA และ Accuracy XC \n" +
                "Melovas จะสร้างความเสียหายสลับกันระหว่างกายภาพและเวทมนตร์ เมื่อจู่โจม มีโอกาส XD ที่จะโจมตีเพิ่มอีกหนึ่งชุด และเมื่อโจมตีในแต่ละครั้งจะสามารถเคลื่อนที่ไปยังพื้นที่ใกล้เคียงได้ XE เมตร \n" +
                "\n" +
                "Engai  ( Icarus + Geneva ) \n" +
                "สการ์เล็ตจะกลายเป็น Shaman พร้อมกับยันต์ที่สร้างความเสียหายเวท สามารถใช้ Combined Action ในเทเลพอร์ตไปยังยันต์ที่ปาออกได้ไกลสุด XF เมตรและสามารถใช้ Reaction ในการเคลื่อนที่กลับมายังจุดก่อนหน้าที่เคลื่อนที่ไปภายในรอบเทิร์นเดียวกัน \n" +
                "เมื่อ Engai สร้างความเสียหายสำเร็จ มอบสถานะ Rooted ให้กับเป้าหมาย XG รอบเทิร์น หยุดการร่ายสกิลของเป้าหมาย อีกทั้งยังดูดมานา XH หน่วย\n" +
                "\n" +
                "Muro ( Maelstrom + Revenos ) \n" +
                "สการ์เล็ตจะกลายเป็น Swordman พร้อมกับคาตานะที่สร้างความเสียหายกายภาพแทนเวทมนตร์ เพิ่ม AttackSPD XI และ LUK XJ หน่วย\n" +
                "หากสการ์เล็ตหลบหลีกล้มเหลว จะพุ่งเข้าไปจู่โจม XK เป้าหมายที่มองเห็นได้ทันที\n" +
                "เพิ่มโอกาสปัดป้องการจู่โจม หากปัดป้องสำเร็จ เคลื่อนที่ไปยังทิศทางที่มียูนิตพันธมิตรอยู่ได้สูงสูด XL เมตร \n" +
                "\n" +
                "Tyranovia ( Titan + Geneva )\n" +
                "สการ์เล็ตจะกลายเป็น Enchanter พร้อมกับถุงมือเวทที่สร้างม่านป้องกัน ซึ่งสามารถบิด ยืด และเปลี่ยนแปลงรูปร่างได้สูงสุดในระยะ 2x2 เมตร เพิ่ม PDEF MDEF XM และ CritShield XN\n" +
                "เมื่อใช้งาน Tyranovia เพื่อโจมตีในระยะ 3x3 เมตร จะสร้างความเสียหายเวท XO หน่วยและดึงเป้าหมายเข้ามาใกล้ตัว Scarlet 1 เมตร \n" +
                "\n" +
                "Rex ( Icarus + Revenos ) \n" +
                "สการ์เล็ตจะกลายเป็น Assassin พร้อมกับกริชที่สร้างความเสียหายโดยตรง เพิ่ม Evasion และ MSPD XP\n" +
                "Mark เป้าหมายที่สการ์เล็ตกำลังจะโจมตีเสมอโดยสุ่มทิศ บน-ล่าง-ซ้าย-ขวา หากใช้งาน Rex ในการจู่โจมทิศทางที่ไม่ได้ Mark ไว้ จะไม่สร้างความเสียหาย\n" +
                "เมื่อโจมตีไปที่ Mark สำเร็จ จะสร้างความเสียหายโดยตรงเพิ่มเติม XQ เพิ่ม MSPD XR เป็นเวลา XS รอบเทิร์น\n" +
                "ทุกครั้งที่โจมตี Mark สำเร็จ จะได้รับ Impale ซึ่งเพิ่ม ATK XT จนกว่าจะจบการต่อสู้");
        setActionType("Combine");
        setManaCost(11);
        setCooldown(2);
        setManaReservePercent(0.15);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.35*(1+BuffAMP)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.CRITICAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XA").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").setPercent(true);

        getSkillMultiplier().put("XB",new SkillMultiplier("2"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.3*(1+BuffAMP)"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XC").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XC").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XC").setPercent(true);

        getSkillMultiplier().put("XD",new SkillMultiplier("0.3*(1+LUK/250)"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.CHANCE);
        getSkillMultiplier().get("XD").setPercent(true);

        getSkillMultiplier().put("XE",new SkillMultiplier("0.2*MSPD"));
        getSkillMultiplier().get("XE").getTags().add(SkillType.MOVEMENT);
        getSkillMultiplier().get("XE").getTags().add(SkillType.DISTANCE);

        getSkillMultiplier().put("XF",new SkillMultiplier("2*MSPD"));
        getSkillMultiplier().get("XF").getTags().add(SkillType.DISTANCE);

        getSkillMultiplier().put("XG",new SkillMultiplier("1"));
        getSkillMultiplier().get("XG").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XH",new SkillMultiplier("0.01*MATK"));
        getSkillMultiplier().get("XH").getTags().add(SkillType.RECOVERY);

        getSkillMultiplier().put("XI",new SkillMultiplier("0.36*(1+BuffAMP)"));
        getSkillMultiplier().get("XI").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XI").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XI").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XI").setPercent(true);

        getSkillMultiplier().put("XJ",new SkillMultiplier("0.35*INT*(1+BuffAMP)"));
        getSkillMultiplier().get("XJ").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XJ").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XJ").getTags().add(SkillType.SPELL);

        getSkillMultiplier().put("XK",new SkillMultiplier("1"));
        getSkillMultiplier().get("XK").getTags().add(SkillType.LIMIT);

        getSkillMultiplier().put("XL",new SkillMultiplier("1.2*MSPD"));
        getSkillMultiplier().get("XL").getTags().add(SkillType.MOVEMENT);
        getSkillMultiplier().get("XL").getTags().add(SkillType.DISTANCE);

        getSkillMultiplier().put("XM",new SkillMultiplier("0.4*MATK(1+BuffAMP)"));
        getSkillMultiplier().get("XM").getTags().add(SkillType.DEFENSE);
        getSkillMultiplier().get("XM").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XM").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XM").getTags().add(SkillType.SPELL);

        getSkillMultiplier().put("XN",new SkillMultiplier("1*(1+BuffAMP)"));
        getSkillMultiplier().get("XN").getTags().add(SkillType.CRITICAL);
        getSkillMultiplier().get("XN").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XN").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XN").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XN").getTags().add(SkillType.DEFENSE);
        getSkillMultiplier().get("XN").setPercent(true);

        getSkillMultiplier().put("XO",new SkillMultiplier("1.4*MATK"));
        getSkillMultiplier().get("XO").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XO").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XO").getTags().add(SkillType.AOE);

        getSkillMultiplier().put("XP",new SkillMultiplier("0.5*(1+BuffAMP)"));
        getSkillMultiplier().get("XP").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XP").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XP").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XP").setPercent(true);

        getSkillMultiplier().put("XQ",new SkillMultiplier("0.6*(1+BuffAMP)"));
        getSkillMultiplier().get("XQ").getTags().add(SkillType.CRITICAL);
        getSkillMultiplier().get("XQ").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XQ").setPercent(true);

        getSkillMultiplier().put("XR",new SkillMultiplier("0.2*(1+BuffAMP)"));
        getSkillMultiplier().get("XR").getTags().add(SkillType.MOVEMENT);
        getSkillMultiplier().get("XR").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XR").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XR").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XR").setPercent(true);

        getSkillMultiplier().put("XS",new SkillMultiplier("1"));
        getSkillMultiplier().get("XS").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XT",new SkillMultiplier("0.11"));
        getSkillMultiplier().get("XT").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XT").setPercent(true);

        getSkillMultiplier().put("XU",new SkillMultiplier("5"));
        getSkillMultiplier().get("XU").getTags().add(SkillType.LIMIT);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        List<String> choices = List.of("Melovas", "Engai", "Muro", "Tyranovia", "Rex");
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser(), choices
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.CUSTOM, 0)
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 1)
        );
        spec    .addFields(
                new SkillInputSpec.InputField<String>("Rex [Correct Direction]", SkillInputSpec.InputType.BOOLEAN, 0)
        , 1, 0);
//                .addFields(
//                        new SkillInputSpec.InputField<String>("Damage", SkillInputSpec.InputType.NUMBER,1)
//                , 0, 1);
        return spec;
    }

    @Override
    public void calculateExtra() {
        addCounter(CounterName.IMPALE);
        double xt = getSkillMultiplier().get("XT").getResult();
        double counter = getUser().getRawCounterMap().get(CounterName.IMPALE);
        getSkillModifier().getStatModifierSafe(StatType.RANGEDATTACK).setGlobalMult(xt*counter);
        getSkillModifier().getStatModifierSafe(StatType.MAGICALATTACK).setGlobalMult(xt*counter);
        getSkillModifier().getStatModifierSafe(StatType.PHYSICALATTACK).setGlobalMult(xt*counter);
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        double matk = getUser().getStats().get(StatType.MAGICALATTACK).getFinal();
        double xd = getSkillMultiplier().get("XD").getResult();
        double xg = getSkillMultiplier().get("XG").getResult();
        double xh = getSkillMultiplier().get("XH").getResult();
        double xo = getSkillMultiplier().get("XO").getResult();
        double xq = getSkillMultiplier().get("XQ").getResult();
        double xs = getSkillMultiplier().get("XS").getResult();

        if (!skillTarget.getTarget(0).isEmpty()) {
            if (skillTarget.getTarget(0).contains("Melovas")) {
                Conditions condition = combatFlow.findCondition("Melovas");

                ConditionManager.applyCondition(condition,getUser(), getUser(), 1);

                boolean repeat = true;
                while (repeat) {
                    if (getUser().hasCondition("Melovas [Physical]")) {
                        sendActionEvent(combatFlow.getEventBus(),
                                ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(1)))
                                        .effect(ActionEffectType.DAMAGE_PHYSICAL, matk, 1)
                                        .addActType(ActType.ATTACK, ActType.STRIKE)
                                        .build()
                        );
                        ConditionManager.removeCondition(getUser(), "Melovas [Physical]");
                        ConditionManager.applyCondition(combatFlow.findCondition("Melovas [Magical]"), getUser(), getUser(), 1);
                    } else if (getUser().hasCondition("Melovas [Magical]")) {
                        sendActionEvent(combatFlow.getEventBus(),
                                ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(1)))
                                        .effect(ActionEffectType.DAMAGE_MAGICAL, matk, 1)
                                        .addActType(ActType.ATTACK, ActType.STRIKE)
                                        .build()
                        );
                        ConditionManager.removeCondition(getUser(), "Melovas [Magical]");
                        ConditionManager.applyCondition(combatFlow.findCondition("Melovas [Physical]"), getUser(), getUser(), 1);
                    } else {
                        ConditionManager.applyCondition(combatFlow.findCondition("Melovas [Magical]"), getUser(), getUser(), 1);
                        continue;
                    }
                    double chance = Math.min(0.9, xd) * 100;

                    if (ThreadLocalRandom.current().nextInt(100)+1 >= chance) {
                        repeat = false;
                        ConditionManager.removeCondition(getUser(), "Melovas [Physical]");
                        ConditionManager.removeCondition(getUser(), "Melovas [Magical]");
                    }
                }
            }
        }

        if (skillTarget.getTarget(0).contains("Engai")) {

            Conditions condition = combatFlow.findCondition("Rooted");

            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(1)))
                            .effect(ActionEffectType.DAMAGE_MAGICAL, matk, 1)
                            .condition(condition, (int) xg)
                            .effect(ActionEffectType.MANA_RECOVER, xh*-1, 1)
                            .addActType(ActType.CAST, ActType.STRIKE, ActType.CONDITION_GIVEN)
                            .build()
            );

            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), getUser())
                            .effect(ActionEffectType.MANA_RECOVER, xh, 1)
                            .addActType(ActType.MANA_RECOVER)
                            .build()
            );
        }

        if (skillTarget.getTarget(0).contains("Muro")) {

            Conditions condition = combatFlow.findCondition("Muro");
            ConditionManager.applyCondition(condition,getUser(), getUser(), 1);

            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(1)))
                            .effect(ActionEffectType.DAMAGE_PHYSICAL, matk, 1)
                            .addActType(ActType.ATTACK, ActType.STRIKE)
                            .build()
            );
        }

        if (skillTarget.getTarget(0).contains("Tyranovia")) {
            Conditions condition = combatFlow.findCondition("Tyranovia");


            ConditionManager.applyCondition(condition,getUser(), getUser(), 1);

            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(1)))
                            .effect(ActionEffectType.DAMAGE_MAGICAL, xo, 1)
                            .addActType(ActType.CAST, ActType.STRIKE)
                            .build()
            );
        }

        if (skillTarget.getTarget(0).contains("Rex")) {
            Conditions condition = combatFlow.findCondition("Rex");
            ConditionManager.applyCondition(condition,getUser(), getUser(), 1);

            for (String name : skillTarget.getTarget(1)) {
                Unit target = combatFlow.findUnit(name);
                if (skillTarget.getDecision(name,1, 0).contains("TRUE")) {
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), target)
                                    .effect(ActionEffectType.DAMAGE_PURE, matk*(1+xq), 1)
                                    .addActType(ActType.ATTACK, ActType.STRIKE)
                                    .build()
                    );

                    Conditions fade = combatFlow.findCondition("Fade");
                    ConditionManager.applyCondition(fade, getUser(),getUser(), (int) xs);
                    getUser().counterIncrement(CounterName.IMPALE);
                } else {
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), target)
                                    .effect(ActionEffectType.DAMAGE_PURE, 0, 1)
                                    .addActType(ActType.ATTACK, ActType.STRIKE)
                                    .build()
                    );
                }
            }
        }

    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        double xa = getSkillMultiplier().get("XA").getResult();
        double xc = getSkillMultiplier().get("XC").getResult();
        double xi = getSkillMultiplier().get("XI").getResult();
        double xj = getSkillMultiplier().get("XJ").getResult();
        double xm = getSkillMultiplier().get("XM").getResult();
        double xn = getSkillMultiplier().get("XN").getResult();
        double xp = getSkillMultiplier().get("XP").getResult();
        double xr = getSkillMultiplier().get("XR").getResult();
        Conditions condition = new Conditions("Melovas");
        condition.getStatModifiers(StatType.CRITDAMAGE).setGlobalMult(xa);
        condition.getStatModifiers(StatType.ACCURACY).setGlobalMult(xc);
        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.BOUND);

        Conditions condition2 = new Conditions("Muro");
        condition2.getStatModifiers(StatType.ATTACKSPEED).setGlobalMult(xi);
        condition2.getStatusModifiers(StatusType.LUCK).setFlat(xj);
        condition2.setConditionType(ConditionType.BUFF);
        condition2.setConditionTierType(ConditionTierType.BOUND);

        Conditions condition3 = new Conditions("Tyranovia");
        condition3.getStatModifiers(StatType.MAGICALDEFENSE).setFlat(xm);
        condition3.getStatModifiers(StatType.PHYSICALDEFENSE).setFlat(xm);
        condition3.getStatModifiers(StatType.CRITSHIELD).setGlobalMult(xn);
        condition3.setConditionType(ConditionType.BUFF);
        condition3.setConditionTierType(ConditionTierType.BOUND);

        Conditions condition4 = new Conditions("Rex");
        condition4.getStatModifiers(StatType.EVASION).setGlobalMult(xp);
        condition4.getStatModifiers(StatType.MOVEMENTSPEED).setGlobalMult(xp);
        condition4.setConditionType(ConditionType.BUFF);
        condition4.setConditionTierType(ConditionTierType.BOUND);

        Conditions condition5 = new Conditions("Fade");
        condition5.getStatModifiers(StatType.MOVEMENTSPEED).setGlobalMult(xr);
        condition5.setConditionType(ConditionType.BUFF);
        condition5.setConditionTierType(ConditionTierType.BOUND);

        Conditions physical = new Conditions("Melovas [Physical]");
        physical.setDescription("การโจมตีครั้งถัดไปของ Melovas จะสร้างความเสียหายกายภาพ");
        physical.setConditionTierType(ConditionTierType.UNDISPELLABLE);
        physical.setConditionType(ConditionType.NEUTRAL);

        Conditions magical = new Conditions("Melovas [Magical]");
        magical.setDescription("การโจมตีครั้งถัดไปของ Melovas จะสร้างความเสียหายเวท");
        magical.setConditionTierType(ConditionTierType.UNDISPELLABLE);
        magical.setConditionType(ConditionType.NEUTRAL);

        addConditionToDatabase(physical, combatFlow);
        addConditionToDatabase(magical, combatFlow);

        addConditionToDatabase(condition, combatFlow);
        addConditionToDatabase(condition2, combatFlow);
        addConditionToDatabase(condition3, combatFlow);
        addConditionToDatabase(condition4, combatFlow);
        addConditionToDatabase(condition5, combatFlow);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
            ConditionManager.reapplyCondition(condition2, unit);
            ConditionManager.reapplyCondition(condition3, unit);
            ConditionManager.reapplyCondition(condition4, unit);
            ConditionManager.reapplyCondition(condition5, unit);
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

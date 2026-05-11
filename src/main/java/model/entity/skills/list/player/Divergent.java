package main.java.model.entity.skills.list.player;

import main.java.controller.CombatFlow;
import main.java.controller.event.events.ActionEvent;
import main.java.manager.ConditionManager;
import main.java.model.entity.Conditions;
import main.java.model.entity.skills.*;
import main.java.model.entity.units.Unit;
import main.java.model.type.*;

import java.util.List;

public class Divergent extends Skill implements SkillWithCondition {

    public static String NAME = "Divergent";

    public Divergent() {
        super();
        setDescription("Maelstorm (ดาบใหญ่) : กระโดดไปข้างหน้าได้ไกลที่สุด 2 ช่องรอบตัว (ไม่นับช่องตัวเอง) พร้อมเหวี่ยงดาบแห่งโทสะกระแทกลงที่พื้น สร้างความเสียหายกายภาพ XA หน่วยพร้อมสตันเป้าหมายในระยะ 0.5 เมตรรอบตัว\n" +
                "Icarus (ธนู) : ชาร์จลูกศรแห่งความเย่อหยิ่ง 1 เทิร์น เพิ่มค่า Accuracy XB การโจมตีนี้จะทะลุเป้าหมายแรก และสร้างความเสียหาย 40% ใส่เป้าหมายที่สองที่อยู่ด้านหลังในระยะ 2 ช่องรอบตัวเป้าหมายแรก \n" +
                "Geneva (คฑา) : ปลดปล่อยโซ่แห่งราคะ ตรึงสูงสุด 2 เป้าหมายในระยะ 3 ช่องรอบตัวเป็นระยะเวลา 1 รอบเทิร์น ดูดมานาของเป้าหมาย ตัวละ XC หน่วย ก่อนที่จะสร้างความเสียหาย XD หน่วย\n" +
                "Revenos (จักรราม) : เพิ่ม AttackSPD XE และ LUK XF\n" +
                "Titan (โล่) : เพิ่ม PDEF และ MDEF XG กระแทกโล่แห่งเกียจคร้านลงที่จุดที่ยืนอยู่เพื่อล่อเป้าหมายและดึงดูดการโจมตีจากในระยะ XH เมตร แต่จะสูญเสีย MSPD ทั้งหมดเป็นเวลา 1 เทิร์น ");
        setActionType("Action");
        setManaCost(3);
        setCooldown(0);
        setManaReservePercent(0.4);
        getSkillMultiplier().put("XA",new SkillMultiplier("(2*STR)+(0.4*MATK)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.AOE);

        getSkillMultiplier().put("XB",new SkillMultiplier("3.5*INT*(1+BuffAMP)"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XB").getTags().add(SkillType.BUFF);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.01*MATK"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.SPELL);

        getSkillMultiplier().put("XD",new SkillMultiplier("(3*WIS)+(0.5*MATK)"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XD").getTags().add(SkillType.STRIKE);

        getSkillMultiplier().put("XE",new SkillMultiplier("0.004*INT*(1+BuffAMP)"));
        getSkillMultiplier().get("XE").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XE").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XE").setPercent(true);

        getSkillMultiplier().put("XF",new SkillMultiplier("0.25*INT*(1+BuffAMP)"));
        getSkillMultiplier().get("XF").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XF").getTags().add(SkillType.BUFF);

        getSkillMultiplier().put("XG",new SkillMultiplier("1.1*INT*(1+BuffAMP)"));
        getSkillMultiplier().get("XG").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XG").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XG").getTags().add(SkillType.DEFENSE);

        getSkillMultiplier().put("XH",new SkillMultiplier("3"));
        getSkillMultiplier().get("XH").getTags().add(SkillType.AOE);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        List<String> choices = List.of("Maelstorm", "Icarus", "Geneva", "Revenos", "Titan");
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser(), choices
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.CUSTOM, 0)
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 1)
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
        double matk = getUser().getStats().get(StatType.MAGICALATTACK).getFinal();
        double xa = getSkillMultiplier().get("XA").getResult();
        double xc = getSkillMultiplier().get("XC").getResult();
        double xd = getSkillMultiplier().get("XD").getResult();
        if (!skillTarget.getTarget(0).isEmpty()) {
            if (skillTarget.getTarget(0).contains("Maelstorm")) {
                Conditions condition = combatFlow.findCondition("Stun");
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(1)))
                                .effect(ActionEffectType.DAMAGE_PHYSICAL, xa, 1)
                                .addActType(ActType.ATTACK, ActType.STRIKE)
                                .condition(condition, 1)
                                .build()
                );
            }
        }

            if (skillTarget.getTarget(0).contains("Icarus")) {

                Conditions condition = combatFlow.findCondition("Icarus");
                ConditionManager.applyCondition(condition,getUser(), getUser(), 1);

                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(1)))
                                .effect(ActionEffectType.DAMAGE_MAGICAL, matk, 1)
                                .addActType(ActType.CAST, ActType.ATTACK, ActType.STRIKE)
                                .build()
                );
            }

            if (skillTarget.getTarget(0).contains("Geneva")) {
                for (String string : skillTarget.getTarget(1)) {
                    Unit target = combatFlow.findUnit(string);
                    target.sumRemainingMana(xc*-1);
                    Conditions condition = combatFlow.findCondition("Root");
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), target)
                                    .effect(ActionEffectType.DAMAGE_MAGICAL, xd, 1)
                                    .addActType(ActType.CAST, ActType.STRIKE)
                                    .condition(condition, 1)
                                    .build()
                    );
                }
            }

        if (skillTarget.getTarget(0).contains("Revenos")) {

            Conditions condition = combatFlow.findCondition("Revenos");
            ConditionManager.applyCondition(condition,getUser(), getUser(), 1);

            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(1)))
                            .effect(ActionEffectType.DAMAGE_MAGICAL, matk, 1)
                            .addActType(ActType.CAST, ActType.ATTACK, ActType.STRIKE)
                            .build()
            );
        }

        if (skillTarget.getTarget(0).contains("Titan")) {

            Conditions condition = combatFlow.findCondition("Titan");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), getUser())
                            .addActType(ActType.CONDITION_GIVEN)
                            .condition(condition, 1)
                            .build()
            );
        }

        }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        double buffAMP = 1+getUser().getStats().get(StatType.BUFFAMPLIFIER).getFinal();
        double debuffAMP = 1+getUser().getStats().get(StatType.DEBUFFAMPLIFIER).getFinal();
        Conditions icarus = new Conditions("Icarus");
        Conditions revenos = new Conditions("Revenos");
        Conditions titan = new Conditions("Titan");
        double xb = getSkillMultiplier().get("XB").getResult();
        double xe = getSkillMultiplier().get("XE").getResult();
        double xf = getSkillMultiplier().get("XF").getResult();
        double xg = getSkillMultiplier().get("XG").getResult();
        icarus.getStatModifiers(StatType.ACCURACY).setFlat(xb);
        revenos.getStatModifiers(StatType.ATTACKSPEED).setFlat(xe);
        revenos.getStatusModifiers(StatusType.LUCK).setFlat(xf);
        titan.getStatModifiers(StatType.PHYSICALDEFENSE).setFlat(xg);
        titan.getStatModifiers(StatType.MAGICALDEFENSE).setFlat(xg);

        icarus.setConditionType(ConditionType.BUFF);
        icarus.setConditionTierType(ConditionTierType.BOUND);
        revenos.setConditionType(ConditionType.BUFF);
        revenos.setConditionTierType(ConditionTierType.BOUND);
        titan.setConditionType(ConditionType.BUFF);
        titan.setConditionTierType(ConditionTierType.BOUND);

        //remove and re-add to database
        combatFlow.getDatabase().getAllConditionMap().put(icarus.getName(), icarus);
        combatFlow.getDatabase().getAllConditionMap().put(revenos.getName(), revenos);
        combatFlow.getDatabase().getAllConditionMap().put(titan.getName(), titan);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(icarus, unit);
            ConditionManager.reapplyCondition(revenos, unit);
            ConditionManager.reapplyCondition(titan, unit);
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

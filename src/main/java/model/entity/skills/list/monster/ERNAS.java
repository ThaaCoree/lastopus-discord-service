package model.entity.skills.list.monster;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

import java.util.List;

public class ERNAS extends Skill implements SkillWithCondition {

    public static String NAME = "ERNAS";

    public ERNAS() {
        super();
        setDescription("การใช้งานสกิลนี้อาจถูกแจ้งหรือไม่แจ้งให้ฝั่งผู้เล่นทราบหรือไม่ก็ได้\n" +
                "เมื่อใช้งาน วางกับดักหนึ่งจากห้าแบบพื้นฐานไว้ตรงพื้นที่หนึ่งซึ่งมีวิธีการทำงานที่ต่างกัน กับดักห้าแบบพื้นฐานประกอบไปด้วย\n" +
                "Elis : ทำงานเมื่อมียูนิตศัตรูเคลื่อนที่เข้ามาในพื้นที่ สร้างความเสียหายเวทให้กับศัตรูทั้งหมดในพื้นที่ XA หน่วยตามจำนวนยูนิตศัตรูที่อยู่ภายใน\n" +
                "Ronox : ทำงานเมื่อเมย์เซฟใช้งาน Reaction สร้างความเสียหายเวทให้กับศัตรูทั้งหมดในพื้นที่ XA หน่วย จากนั้นสร้างพื้นที่เวทมนตร์ในแพลตฟอร์มนั้น ยูนิตศัตรูไม่สามารถเคลื่อนที่ผ่านหรือใช้งานเวทมนตร์เคลื่อนที่ผ่านเขตแดนของโรน็อกซ์ได้\n" +
                "Nevia : ทำงานเมื่อยูนิตในรัศมี 1 แพลตฟอร์มทำการจู่โจมเมย์เซฟ สร้างความเสียหายเวทให้กับมัน XC หน่วยและมอบสถานะ Light Fatigued เป็นเวลา XB รอบเทิร์น ซึ่งลด ATK และ MSPD XD\n" +
                "Aeth : ทำงานเมื่อมียูนิตศัตรูทำการฮีลในรัศมี 2 แพลตฟอร์ม สร้างความเสียหายกายภาพให้กับมัน XC หน่วยและมอบสถานะ Light Drained เป็นเวลา XB รอบเทิร์น ซึ่งลด HealAMP XD\n" +
                "Seiros : ทำงานได้เมื่อมีหนึ่งในกับดักพื้นฐานอื่นทำงาน เปลี่ยนตำแหน่งของกับดักพื้นฐานอื่นหนึ่งอัน และสั่งเปิดใช้งานมันทันที");
        setActionType("Combine / Combine + Reaction");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("2.4*MATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);

        getSkillMultiplier().put("XB",new SkillMultiplier("2"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XC",new SkillMultiplier("1.8*MATK"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XC").getTags().add(SkillType.STRIKE);

        getSkillMultiplier().put("XD",new SkillMultiplier("0.3*(1+DebuffAMP)"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XD").setPercent(true);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
        );
        spec    .addFields(
                new SkillInputSpec.InputField<String>("Mode", SkillInputSpec.InputType.SELECT, 0)
                        .options(List.of("Elis","Ronox", "Nevia","Aeth","Seiros"), 0)
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
        if (!skillTarget.getTarget(0).isEmpty()) {
            int target_number = skillTarget.getTarget(0).size();
            for (String name : skillTarget.getTarget(0)) {
                Unit target = combatFlow.findUnit(name);

                if (skillTarget.getDecision(name, 0, 0).contains("Elis")) {
                    double xa = getSkillMultiplier().get("XA").getResult();
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), target)
                                    .effect(ActionEffectType.DAMAGE_MAGICAL, xa*target_number, 1)
                                    .addActType(ActType.CAST, ActType.STRIKE)
                                    .build());
                }

                if (skillTarget.getDecision(name, 0, 0).contains("Ronox")) {
                    double xa = getSkillMultiplier().get("XA").getResult();
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), target)
                                    .effect(ActionEffectType.DAMAGE_MAGICAL, xa, 1)
                                    .addActType(ActType.CAST, ActType.STRIKE)
                                    .build());
                }

                if (skillTarget.getDecision(name, 0, 0).contains("Nevia")) {
                    double xc = getSkillMultiplier().get("XC").getResult();
                    int duration = (int) getSkillMultiplier().get("XB").getResult();
                    Conditions condition = combatFlow.findCondition("Light Fatigued");
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), target)
                                    .effect(ActionEffectType.DAMAGE_MAGICAL, xc, 1)
                                    .condition(condition, duration)
                                    .addActType(ActType.CAST, ActType.CONDITION_GIVEN, ActType.STRIKE)
                                    .build());
                }

                if (skillTarget.getDecision(name, 0, 0).contains("Aeth")) {
                    double xc = getSkillMultiplier().get("XC").getResult();
                    int duration = (int) getSkillMultiplier().get("XB").getResult();
                    Conditions condition = combatFlow.findCondition("Light Drained");
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), target)
                                    .effect(ActionEffectType.DAMAGE_MAGICAL, xc, 1)
                                    .condition(condition, duration)
                                    .addActType(ActType.CAST, ActType.CONDITION_GIVEN, ActType.STRIKE)
                                    .build());
                }
            }
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Light Fatigued");
        double xd = getSkillMultiplier().get("XD").getResult();
        condition.getStatModifiers(StatType.MOVEMENTSPEED).setGlobalMult(xd * -1);
        condition.getStatModifiers(StatType.PHYSICALATTACK).setGlobalMult(xd * -1);
        condition.getStatModifiers(StatType.RANGEDATTACK).setGlobalMult(xd * -1);
        condition.getStatModifiers(StatType.MAGICALATTACK).setGlobalMult(xd * -1);

        condition.setConditionType(ConditionType.DEBUFF);
        condition.setConditionTierType(ConditionTierType.ADVANCED);

        Conditions condition2 = new Conditions("Light Drained");
        condition2.getStatModifiers(StatType.HEALAMPLIFIER).setGlobalMult(xd * -1);

        condition.setConditionType(ConditionType.DEBUFF);
        condition.setConditionTierType(ConditionTierType.ADVANCED);

        condition2.setConditionType(ConditionType.DEBUFF);
        condition2.setConditionTierType(ConditionTierType.ADVANCED);

        addConditionToDatabase(condition, combatFlow);
        addConditionToDatabase(condition2, combatFlow);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
            ConditionManager.reapplyCondition(condition2, unit);
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
//        EventBus eventBus = combatFlow.getEventBus();
//        eventBus.register(ActionEvent.class, EventPhase.POST, 0, (ActionEvent event) -> {
//            if (!event.hasActType(ActType.HEAL) || event.unit_source != getUser() || event.event_source.equals(getName())) return;
//            List<Unit> targets = event.unit_target;
//            double heal_amount = event.getHeal();
//
//            sendActionEvent(combatFlow.getEventBus(),
//                    ActionEvent.builder(getName(), getUser(), targets)
//                            .effect(ActionEffectType.HEALTH_RECOVER,heal_amount, 1)
//                            .addActType(ActType.HEAL, ActType.HEALTH_RECOVER, ActType.SKILL_TRIGGER)
//                            .build()
//            );
//        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

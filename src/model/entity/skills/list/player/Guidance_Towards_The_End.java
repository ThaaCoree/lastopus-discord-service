package model.entity.skills.list.player;

import main.controller.CombatFlow;
import main.controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.type.*;
import util.LogWriterUtil;

public class Guidance_Towards_The_End extends Skill implements SkillWithCondition {

    public static String NAME = "Guidance towards the end";

    public Guidance_Towards_The_End() {
        super();
        setDescription("Passive : เพิ่ม ATK XA หน่วย ต่อพลังชีวิตที่หายไป 1 หน่วย\n" +
                "เมื่อใช้งาน ลดพลังชีวิตจนเหลือ 1 หน่วย และรับสถานะ Ageless เป็นเวลา XB รอบเทิร์น ซึ่งมอบ ATK อีก XC หน่วยต่อพลังชีวิตที่หายไป 1 หน่วย\n" +
                "ดาบใหญ่ที่ถือในมือจะยิงลำแสงแช่งแข็งออกมา การโจมตีด้วยดาบหรือลำแสงจะสร้างความเสียหายเวทเพิ่มเติม XD หน่วยและแช่แข็งเป้าหมาย พลังชีวิตจะไม่ลดต่ำกว่า 1 จนกว่าผลของสกิลนี้จะหมด นอกจากได้รับความเสียหายจริง");
        setActionType("Combine");
        setManaCost(4);
        setCooldown(4);
        setManaReservePercent(0.6);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.165"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);

        getSkillMultiplier().put("XB",new SkillMultiplier("1"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.35"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.SCALING);

        getSkillMultiplier().put("XD",new SkillMultiplier("2*MATK"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XD").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XD").getTags().add(SkillType.WATER);
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
        double usable_hp = getUser().getHealth().getUsable();
        double remain_hp = getUser().getHealth().getRemaining();
        double xa = getSkillMultiplier().get("XA").getResult();
        getSkillModifier().getStatModifierSafe(StatType.PHYSICALATTACK).setFlat((usable_hp-remain_hp)*xa);
        getSkillModifier().getStatModifierSafe(StatType.MAGICALATTACK).setFlat((usable_hp-remain_hp)*xa);
        getSkillModifier().getStatModifierSafe(StatType.RANGEDATTACK).setFlat((usable_hp-remain_hp)*xa);
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        Conditions condition = new Conditions("Ageless");
        double usable_hp = getUser().getHealth().getUsable();
        double remain_hp = getUser().getHealth().getRemaining();
        double xa = getSkillMultiplier().get("XA").getResult();
        condition.getStatModifiers(StatType.PHYSICALATTACK).setFlat((usable_hp-remain_hp)*xa);
        condition.getStatModifiers(StatType.MAGICALATTACK).setFlat((usable_hp-remain_hp)*xa);
        condition.getStatModifiers(StatType.RANGEDATTACK).setFlat((usable_hp-remain_hp)*xa);

        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.ADVANCED);
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(condition.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(condition.getName(), condition);

        int duration = (int) getSkillMultiplier().get("XB").getResult();
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(),getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                        .condition(condition, duration)
                        .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                        .build());


        if (!skillTarget.getTarget(0).isEmpty()) {
            double xd = getSkillMultiplier().get("XD").getResult();
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .effect(ActionEffectType.DAMAGE_MAGICAL, xd, 1)
                            .condition(condition, duration)
                            .addActType(ActType.STRIKE)
                            .build()
            );
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

    }

    @Override
    public String getName() {
        return NAME;
    }
}

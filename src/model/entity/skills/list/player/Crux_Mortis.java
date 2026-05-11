package model.entity.skills.list.player;

import main.controller.CombatFlow;
import main.controller.event.EventBus;
import main.controller.event.events.ActionEffect;
import main.controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;
import util.LogWriterUtil;

import java.util.List;

public class Crux_Mortis extends Skill implements SkillWithCondition {

    public static String NAME = "Crux Mortis";

    public Crux_Mortis() {
        super();
        setDescription("มาร์กเป้าหมาย มันจะรับความเสียหายทุกแหล่งจากผู้ใช้มากขึ้น XA หน่วย\n" +
                "เมื่อมันรับความเสียหายจากผู้ใช้ สร้างคลื่นดาบฟันยาวเจ็ดเมตรสองเล่มฟันตัดกันที่ตัวเป้าหมาย สร้างความเสียหายกายภาพ XB หน่วย\n" +
                "หากใช้งานกับพันธมิตร คลื่นดาบที่ฟันตัดกันจะฟื้นฟูพลังชีวิตให้ XC หน่วยแทน\n" +
                "ความเสียหายจากคลื่นดาบของสกิลนี้จะไม่ทำให้เกิดคลื่นดาบ");
        setActionType("Action");
        setManaCost(6);
        setCooldown(3);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.2*PATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.MARK);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.35*PATK"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XB").getTags().add(SkillType.MARK);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.06*PATK"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.RECOVERY);
        getSkillMultiplier().get("XC").getTags().add(SkillType.MARK);
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
            int duration = (int) getSkillMultiplier().get("XB").getResult();
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Crux Mortis");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .condition(condition, duration)
                            .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                            .build()
            );
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Crux Mortis");

        condition.setConditionType(ConditionType.NEUTRAL);
        condition.setConditionTierType(ConditionTierType.GENERAL);

        double xa = getSkillMultiplier().get("XA").getResult();
        double xb = getSkillMultiplier().get("XB").getResult();
        condition.setDescription("รับความเสียหายจาก "+getUser().getName()+" มากขึ้น "+xa+" หน่วย และเมื่อรับความเสียหาย จะเกิดคลื่นดาบพุ่งตัดกันตรงตำแหน่งของยูนิตนี้ สร้างความเสียหายกายภาพอีก "+xb+" หน่วย");

        //remove and re-add to database
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(condition.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(condition.getName(), condition);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ActionEvent.class, EventPhase.POST, 0, (ActionEvent event) -> {
            if (event.unit_source != getUser() || event.hasActType(ActType.SKILL_TRIGGER)) return;
            for (Unit unit : event.unit_target) {
                if (event.canDamage(unit.getName()) && unit.hasCondition("Crux Mortis")) {
                    double xb = getSkillMultiplier().get("XB").getResult();
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName()+"'s Slash", getUser(), unit)
                                    .effect(ActionEffectType.DAMAGE_PHYSICAL,xb, 1)
                                    .addActType(ActType.SKILL_TRIGGER)
                                    .build()
                    );
                }
            }
        });

        eventBus.register(ActionEvent.class, EventPhase.POST, 1, (ActionEvent event) -> {
            if (event.unit_source != getUser()) return;
            for (Unit unit : event.unit_target) {
                if (!event.canDamage(unit.getName()) || !unit.hasCondition("Crux Mortis")) continue;
                for (ActionEffect actionEffect : event.effects.get(unit.getName())) {
                    if (actionEffect.type == ActionEffectType.DAMAGE_PHYSICAL ||
                    actionEffect.type == ActionEffectType.DAMAGE_MAGICAL ||
                    actionEffect.type == ActionEffectType.DAMAGE_PURE ||
                    actionEffect.type == ActionEffectType.DAMAGE_TRUE) {
                        double xa = getSkillMultiplier().get("XA").getResult();
                        actionEffect.finalValue += xa;
                        sendActionEvent(combatFlow.getEventBus(),
                                ActionEvent.builder(getName(), getUser(), unit)
                                        .addActType(ActType.SKILL_TRIGGER)
                                        .build()
                        );
                        LogWriterUtil.log(">Crux Mortis triggered, dealing more damage");
                    }
                }
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

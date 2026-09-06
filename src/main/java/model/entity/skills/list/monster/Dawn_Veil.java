package model.entity.skills.list.monster;

import controller.CombatFlow;
import controller.event.EventBus;
import controller.event.events.ActionEvent;
import controller.event.events.ResourceEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

import java.util.List;

public class Dawn_Veil extends Skill implements SkillWithCondition {

    public static String NAME = "Dawn Veil";

    public Dawn_Veil() {
        super();
        setDescription("ใช้งานได้เมื่อถูกจู่โจม, รับสถานะ Dawn Veil เป็นเวลา 1 รอบเทิร์น\n" +
                "ระหว่าง Dawn Veil จะได้รับความเสียหายทุกประเภทยกเว้นความเสียหายจริงน้อยลงครึ่งหนึ่ง\n" +
                "ก่อนที่สถานะจะสิ้นสุดลงในตอนจบรอบเทิร์น สามารถใช้งาน Reaction เพื่อนำความเสียหายทั้งหมดที่เคยถูกลดทอนมาสร้างความเสียหายให้กับยูนิตที่เลือกได้");
        setActionType("Reaction");
        setManaCost(0);
        setCooldown(2);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
//                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
        );
//        spec    .addFields(
//                new SkillInputSpec.InputField<String>("Mode", SkillInputSpec.InputType.SELECT, 0)
//                        .options(List.of("choice","choice"), 0)
//                        .labelProvider(String::toString, 0)
//        , 0, 0);
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
        Conditions condition = combatFlow.findCondition("Dawn Veil");
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(), getUser(), getUser())
                        .condition(condition, 1)
                        .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                        .build()
        );
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Dawn Veil");
        condition.setDescription("ความเสียหายครึ่งหนึ่งที่ได้รับจะถูกลดทอนและกักเก็บไว้สำหรับการจู่โจม");

        condition.setConditionType(ConditionType.DEBUFF);
        condition.setConditionTierType(ConditionTierType.GENERAL);

        addConditionToDatabase(condition, combatFlow);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ResourceEvent.class, EventPhase.MODIFY, 0, (ResourceEvent event) -> {
            if (event.target != getUser()) return;
            if (!event.isDamage()) return;
            if (event.effectType == ActionEffectType.DAMAGE_TRUE) return;

            event.amount /= 2;
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

package model.entity.skills.list.player;

import controller.CombatFlow;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

import java.util.List;
import java.util.Map;

public class Gift_And_Take extends Skill implements SkillWithCondition {

    public static String NAME = "Gift and Take";

    public Gift_And_Take() {
        super();
        setDescription("เลือดของราเวลจะมีผลต่อไปนี้ :\n" +
                "เมื่อใช้ดื่ม ผู้ดื่มจะได้รับบัพ Venom Sip ซึ่งเพิ่ม DEF XA หน่วยเป็นเวลา XC รอบเทิร์น\n" +
                "เมื่อใช้สัมผัส ผู้สัมผัสจะได้รับดีบัพ Soft Touch ซึ่งลด DEF XB หน่วยเป็นเวลา XC รอบเทิร์น\n" +
                "สามารถทับซ้อนได้");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        setManaReservePercent(0.3);
        addMultiplier("XA", "2*VIT");
        addTag("XA", SkillType.SCALING);
        addTag("XA", SkillType.DEFENSE);
        addTag("XA", SkillType.BUFF);

        addMultiplier("XB", "4*WIS");
        addTag("XB", SkillType.DEBUFF);
        addTag("XB", SkillType.SCALING);

        addMultiplier("XC", "2");
        addTag("XC", SkillType.DURATION);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
        );
        spec    .addFields(
                new SkillInputSpec.InputField<String>("Mode", SkillInputSpec.InputType.SELECT, 0)
                        .options(List.of("Venom Sip","Soft Touch"), 0)
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
            int duration = (int) getMultiplierResult("XA");

            dispatchByUnitDecision(combatFlow, skillTarget, 0, 0, Map.of(
                    "venom sip",  u -> sendCondition(combatFlow, u, combatFlow.findCondition("Venom Sip"), duration),
                    "soft touch", u -> sendCondition(combatFlow, u, combatFlow.findCondition("Soft Touch"), duration)
            ));
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Venom Sip");
        condition.getStatModifiers(StatType.MAGICALDEFENSE).setGlobalMult(getSkillMultiplier().get("XA").getResult());
        condition.getStatModifiers(StatType.PHYSICALDEFENSE).setGlobalMult(getSkillMultiplier().get("XA").getResult());


        Conditions condition2 = new Conditions("Soft Touch");
        condition2.getStatModifiers(StatType.MAGICALDEFENSE).setGlobalMult(getSkillMultiplier().get("XB").getResult() * -1);
        condition2.getStatModifiers(StatType.PHYSICALDEFENSE).setGlobalMult(getSkillMultiplier().get("XB").getResult() * -1);

        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.GENERAL);
        condition2.setConditionType(ConditionType.DEBUFF);
        condition2.setConditionTierType(ConditionTierType.GENERAL);

        addConditionToDatabase(condition, combatFlow);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
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

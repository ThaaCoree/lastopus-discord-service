package model.entity.skills.list.monster;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

import java.util.List;

public class Brick_Break extends Skill implements SkillWithCondition {

    public static String NAME = "Brick Break";

    public Brick_Break() {
        super();
        setDescription("ใช้มือสับลงไปยังเป้าหมาย สร้างความเสียหายกายภาพ XA หน่วยและมอบสถานะให้ตามตำแหน่งที่จู่โจมโดนเป็นเวลา XB รอบเทิร์น\n" +
                "หากจู่โจมโดนพื้นที่เหนือไหล่ ลด STR, INT, DEX ลง XC\n" +
                "หากจู่โจมโดนพื้นที่กลางตัว ลด VIT ลง XC\n" +
                "หากจู่โจมโดนพื้นที่ต่ำกว่าเอว ลด AGI ลง XC");
        setActionType("Action");
        setManaCost(0);
        setCooldown(1);
        getSkillMultiplier().put("XA",new SkillMultiplier("2.2*PATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);

        getSkillMultiplier().put("XB",new SkillMultiplier("2"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.4*1+(DebuffAMP)"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XC").setPercent(true);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
        );
        spec    .addFields(
                new SkillInputSpec.InputField<String>("Mode", SkillInputSpec.InputType.SELECT, 0)
                        .options(List.of("Head","Body","Leg"), 0)
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
            double xa = getSkillMultiplier().get("XA").getResult();
            int duration = (int) getSkillMultiplier().get("XB").getResult();

            double xc = getSkillMultiplier().get("XC").getResult();
            Conditions head = new Conditions("Brick Break : Head");
            head.getStatusModifiers(StatusType.STRENGTH).setGlobalMult(xc * -1);
            head.getStatusModifiers(StatusType.DEXTERITY).setGlobalMult(xc * -1);
            head.getStatusModifiers(StatusType.INTELLIGENCE).setGlobalMult(xc * -1);
            head.setConditionType(ConditionType.DEBUFF);
            head.setConditionTierType(ConditionTierType.GENERAL);

            Conditions body = new Conditions("Brick Break : Body");
            body.getStatusModifiers(StatusType.VITALITY).setGlobalMult(xc * -1);
            body.setConditionType(ConditionType.DEBUFF);
            body.setConditionTierType(ConditionTierType.GENERAL);

            Conditions leg = new Conditions("Brick Break : Leg");
            leg.getStatusModifiers(StatusType.AGILITY).setGlobalMult(xc * -1);
            leg.setConditionType(ConditionType.DEBUFF);
            leg.setConditionTierType(ConditionTierType.GENERAL);
            Conditions condition = null;

            for (String name : skillTarget.getTarget(0)) {
                Unit unit = combatFlow.findUnit(name);

                if (skillTarget.getDecision(name,0, 0).contains("Head")) {
                    condition = head;
                }

                if (skillTarget.getDecision(name,0, 0).contains("Body")) {
                    condition = body;
                }

                if (skillTarget.getDecision(name,0, 0).contains("Leg")) {
                    condition = leg;
                }
                if (condition != null) {
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), unit)
                                    .effect(ActionEffectType.DAMAGE_PHYSICAL, xa, 1)
                                    .condition(condition, duration)
                                    .addActType(ActType.ATTACK, ActType.STRIKE, ActType.CONDITION_GIVEN)
                                    .build()
                    );
                }
            }
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
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
//                                ActionEvent.builder(getName(), getUser(), targets)
//                                        .effect(ActionEffectType.HEALTH_RECOVER,heal_amount, 1)
//                                        .addActType(ActType.HEAL, ActType.HEALTH_RECOVER, ActType.SKILL_TRIGGER)
//                                        .build()
//                        );
//        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

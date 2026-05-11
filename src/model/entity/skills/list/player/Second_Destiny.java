package model.entity.skills.list.player;

import main.controller.CombatFlow;
import main.controller.event.EventBus;
import main.controller.event.events.ActionEvent;
import main.controller.event.events.ResourceEvent;
import manager.ConditionManager;
import model.entity.ConditionInstance;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillMultiplier;
import model.entity.skills.SkillTarget;
import model.entity.units.Unit;
import model.type.*;
import util.WeightedRandom;

import java.util.List;
import java.util.Map;

public class Second_Destiny extends Skill {

    public static String NAME = "2nd Destiny";

    public Second_Destiny() {
        super();
        setDescription("เมื่อโจมตีแล้วสร้างความเสียหายสำเร็จ มีโอกาส XA ที่จะทำให้เกิดความเสียหายขึ้นซ้ำอีกครั้ง\n" +
                "สกิลนี้ทำงานได้ไม่เกิน XB ครั้งต่อรอบเทิร์น");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        setManaReservePercent(0.6);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.15*(1+LUK*0.01)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.CHANCE);
        getSkillMultiplier().get("XA").setPercent(true);

        getSkillMultiplier().put("XB",new SkillMultiplier("2"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.LIMIT);
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
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ResourceEvent.class, EventPhase.POST, 0, (ResourceEvent event) -> {
            if (event.source != getUser()) return;
            if (!event.isDamage()) return;
            WeightedRandom<Boolean> weightedRandom = new WeightedRandom<>();
            double xa = getSkillMultiplier().get("XA").getResult()*100;
            weightedRandom.add(true, xa);
            weightedRandom.add(false, 100-xa);
            if (!weightedRandom.roll()) return;

            int condition_count = 0;
            for (Map.Entry<Integer, ConditionInstance> entry : getUser().getConditionInstances().entrySet()) {
                String name = entry.getValue().getCondition().getName();
                if (name.equals("Destiny Exhausted")) {
                    condition_count++;
                }
            }
            double xb = getSkillMultiplier().get("XB").getResult();

            if (condition_count < xb) {
                ActionEffectType actionEffectType = ActionEffectType.DAMAGE_PHYSICAL;
                if (event.effectType == ActionEffectType.DAMAGE_MAGICAL) {
                    actionEffectType = ActionEffectType.DAMAGE_MAGICAL;
                }
                if (event.effectType == ActionEffectType.DAMAGE_PURE) {
                    actionEffectType = ActionEffectType.DAMAGE_PURE;
                }
                if (event.effectType == ActionEffectType.DAMAGE_TRUE) {
                    actionEffectType = ActionEffectType.DAMAGE_TRUE;
                }

                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), event.target)
                                .effect(actionEffectType, event.amount, 1)
                                .addActType(ActType.SKILL_TRIGGER)
                                .build()
                );

                Conditions condition = new Conditions("Destiny Exhausted");
                condition.setDescription("ใช้งาน 2nd Destiny ไปแล้วหนึ่งครั้ง");

                condition.setConditionType(ConditionType.NEUTRAL);
                condition.setConditionTierType(ConditionTierType.UNDISPELLABLE);

                ConditionManager.applyCondition(condition, getUser(), getUser(), 1);
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

package model.entity.skills.list.player;

import main.controller.CombatFlow;
import main.controller.event.EventBus;
import main.controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.entity.units.Unit;
import model.type.*;

import java.util.List;

public class Scales_Of_Dragon extends Skill {

    public static String NAME = "Scales of Dragon";

    public Scales_Of_Dragon() {
        super();
        setDescription("มี Counter [Claws] และ [Fangs]\n" +
                "เมื่อสร้างความเสียหายมากกว่า XA ครั้งในการโจมตีเดียว ได้รับ Claws หนึ่งสแต็คจนกว่าจะจบการต่อสู้\n" +
                "เมื่อได้รับ Claws สองสแต็คหรือมากกว่าในเทิร์นเดียวกัน ได้รับ Fangs\n" +
                "\n" +
                "Claws : เพิ่ม Attack Speed XB สูงสุด XC สแต็ค\n" +
                "Fangs : เพิ่ม MSPD XD สูงสุด XE สแต็ค");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        setManaReservePercent(0.4);
        getSkillMultiplier().put("XA",new SkillMultiplier("6"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.REQUIREMENT);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.07"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XB").setPercent(true);

        getSkillMultiplier().put("XC",new SkillMultiplier("10"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.LIMIT);

        getSkillMultiplier().put("XD",new SkillMultiplier("0.06"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XD").setPercent(true);

        getSkillMultiplier().put("XE",new SkillMultiplier("5"));
        getSkillMultiplier().get("XE").getTags().add(SkillType.LIMIT);
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
//        , 0, 0)
//                .addFields(
//                        new SkillInputSpec.InputField<String>("Damage", SkillInputSpec.InputType.NUMBER,1)
//                , 0, 1);
        return spec;
    }

    @Override
    public void calculateExtra() {
        if (getUser().getCounter() == null) return;
        if (!getUser().getRawCounterMap().containsKey(CounterName.CLAWS)) {
            getUser().getRawCounterMap().put(CounterName.CLAWS,0.0);
            getUser().getCounter().put(CounterName.CLAWS,0.0);
        }
        if (!getUser().getRawCounterMap().containsKey(CounterName.FANGS)) {
            getUser().getRawCounterMap().put(CounterName.FANGS,0.0);
            getUser().getCounter().put(CounterName.FANGS,0.0);
        }
        double xb = getSkillMultiplier().get("XB").getResult();
        double xd = getSkillMultiplier().get("XD").getResult();
        double claws = getUser().getRawCounterMap().get(CounterName.CLAWS);
        double fangs = getUser().getRawCounterMap().get(CounterName.FANGS);
        getSkillModifier().getStatModifierSafe(StatType.ATTACKSPEED).setGlobalMult(xb*claws);
        getSkillModifier().getStatModifierSafe(StatType.MOVEMENTSPEED).setGlobalMult(xd*fangs);
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ActionEvent.class, EventPhase.POST, 0, (ActionEvent event) -> {
            if (!event.hasActType(ActType.STRIKE) || event.unit_source != getUser()) return;
            double xa = getSkillMultiplier().get("XA").getResult();
            if (event.damage_times >= xa) {
            getUser().counterIncrement(CounterName.CLAWS);
            sendSkillTriggerEvent(combatFlow,getUser().getName()+" gained 1 Claws");
            if (getUser().hasCondition("Claws")) {
                getUser().counterIncrement(CounterName.FANGS);
                ConditionManager.removeCondition(getUser(), "Claws");
            } else {
                Conditions condition = new Conditions("Claws");
                condition.setConditionType(ConditionType.NEUTRAL);
                condition.setConditionTierType(ConditionTierType.UNDISPELLABLE);
                condition.setDescription("ครั้งต่อไปที่ได้รับ Counter Claws จะได้รับ Counter Fangs ด้วย");
                ConditionManager.applyCondition(condition, getUser(), getUser(), 1);
            }
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

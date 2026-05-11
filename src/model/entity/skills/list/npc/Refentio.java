package model.entity.skills.list.npc;

import main.controller.CombatFlow;
import main.controller.event.EventBus;
import main.controller.event.events.ActionEvent;
import main.controller.event.events.RoundEvent;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.entity.units.Unit;
import model.type.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Refentio extends Skill {

    public static String NAME = "Refentio";

    public Refentio() {
        super();
        setDescription("XA ครั้งต่อเทิร์น เมื่อเกิดการฮีลมากกว่า 50% ของพลังชีวิตสูงสุด ฮีลจำนวนนั้นซ้ำอีกครั้งให้กับพันธมิตรที่มีพลังชีวิตน้อยที่สุด");
        setActionType("Passive");
        getPureTags().add(SkillType.SPELL);
        getPureTags().add(SkillType.RECOVERY);
        setManaReservePercent(0.3);
        getSkillMultiplier().put("XA",new SkillMultiplier("1"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIMIT);
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
        if (!getUser().getRawCounterMap().containsKey(CounterName.REFENTIO)) {
            getUser().getRawCounterMap().put(CounterName.REFENTIO,0.0);
            getUser().getCounter().put(CounterName.REFENTIO,0.0);
        }
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ActionEvent.class, EventPhase.POST, 0, (ActionEvent event) -> {
            if (!event.hasActType(ActType.HEAL) || event.event_source.equals(getName())) return;
            double xa = getSkillMultiplier().get("XA").getResult();
            double counter = getUser().getCounter().get(CounterName.REFENTIO);
            if (counter >= xa) return;

            List<Unit> targets = event.unit_target;
            Map<Unit, Double> applying_target = new LinkedHashMap<>();
            for (Unit target : targets) {
                double heal_amount = event.getHeal(target.getName());
                double current_hp = target.getHealth().getRemaining();
                double usable_hp = target.getHealth().getUsable();
                double actual_heal = 0;
                if (current_hp <= usable_hp*0.5 && heal_amount+current_hp > usable_hp*0.5) {
                    if (heal_amount+current_hp < usable_hp) {
                        actual_heal = heal_amount;
                    } else {
                        actual_heal = usable_hp - current_hp;
                    }
                    applying_target.put(target, actual_heal);
                }
            }

            double heal_amount = 0;
            for (Map.Entry<Unit, Double> entry : applying_target.entrySet()) {
                heal_amount += entry.getValue();
            }

            Unit heal_target = new Unit();
            double health_amount = Double.MAX_VALUE;
            for (Unit unit : getAllies(combatFlow)) {
                double hp = unit.getHealth().getRemaining();
                if (hp < health_amount) {
                    health_amount = hp;
                    heal_target = unit;
                }
            }

            if (heal_amount > 0) {
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), heal_target)
                                .effect(ActionEffectType.HEALTH_RECOVER, heal_amount, 1)
                                .addActType(ActType.HEAL, ActType.HEALTH_RECOVER, ActType.SKILL_TRIGGER)
                                .build()
                );
                getUser().counterIncrement(CounterName.REFENTIO);
            }
        });

        eventBus.register(RoundEvent.class, EventPhase.POST, 0 , e -> {
            getUser().counterSet(CounterName.REFENTIO,0);
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

package model.entity.skills.list.npc;

import main.controller.CombatFlow;
import main.controller.event.EventBus;
import main.controller.event.events.ActionEvent;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillMultiplier;
import model.entity.skills.SkillTarget;
import model.entity.units.Unit;
import model.type.ActType;
import model.type.ActionEffectType;
import model.type.EventPhase;
import model.type.SkillType;

import java.util.List;

public class Divinarius extends Skill {

    public static String NAME = "Divinarius";

    public Divinarius() {
        super();
        setDescription("เพิ่มการฟื้นฟูพลังชีวิตและการสร้าง Debris ที่เกิดขึ้นกับพันธมิตรทั้งหมด XA");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.FIGHTING_STYLE);
        setManaReservePercent(0.1);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.2*(1+INT*0.005)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XA").getTags().add(SkillType.RECOVERY);
        getSkillMultiplier().get("XA").setPercent(true);
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
        eventBus.register(ActionEvent.class, EventPhase.MODIFY, 0, (ActionEvent event) -> {
            double xa = getSkillMultiplier().get("XA").getResult();
            for (Unit unit : event.unit_target) {
                if (!isAlly(unit, combatFlow)) continue;
                if (event.heal_times >= 1) {
                    event.addMultModifier(ActionEffectType.HEALTH_RECOVER, xa);
                    sendSkillTriggerEvent(combatFlow,"Divinarius triggered");
                }
                if (event.debris_times >= 1) {
                    event.addMultModifier(ActionEffectType.CREATE_DEBRIS, xa);
                    sendSkillTriggerEvent(combatFlow,"Divinarius triggered");
                }
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

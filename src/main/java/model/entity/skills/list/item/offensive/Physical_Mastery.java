package main.java.model.entity.skills.list.item.offensive;

import main.java.controller.CombatFlow;
import main.java.controller.event.EventBus;
import main.java.controller.event.events.ActionEffect;
import main.java.controller.event.events.ActionEvent;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.entity.skills.SkillMultiplier;
import main.java.model.entity.units.Unit;
import main.java.model.type.ActType;
import main.java.model.type.ActionEffectType;
import main.java.model.type.EventPhase;
import main.java.model.type.SkillType;
import util.LogWriterUtil;

public class Physical_Mastery extends Skill {

    public static String NAME = "Physical Mastery";

    public Physical_Mastery() {
        super();
        setDescription("จะสร้างความเสียหายกายภาพไม่ต่ำกว่า XA");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("1.2*PATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
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

    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ActionEvent.class, EventPhase.MODIFY, -1, (ActionEvent event) -> {
            if (event.unit_source != getUser() || !event.hasActType(ActType.STRIKE)) return;
            double xa = getSkillMultiplier().get("XA").getResult();

            for (Unit unit : event.unit_target) {
                if (!event.hasPhysicalDamage(unit.getName())) continue;
                for (ActionEffect actionEffect : event.effects.get(unit.getName())) {
                    if (actionEffect.type == ActionEffectType.DAMAGE_PHYSICAL && actionEffect.finalValue < xa) {
                        actionEffect.finalValue = xa;
                        LogWriterUtil.log(">Physical Mastery triggered");
                    }
                }
            }

            sendActionEvent(combatFlow.getEventBus(),
                                ActionEvent.builder(getName(), getUser(), event.unit_target)
                                        .addActType(ActType.SKILL_TRIGGER)
                                        .build()
                        );
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

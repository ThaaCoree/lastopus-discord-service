package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillMultiplier;
import model.entity.skills.SkillTarget;
import model.type.ActType;
import model.type.ActionEffectType;
import model.type.SkillType;
import model.type.StatType;

public class Fairy_Lore extends Skill {

    public static String NAME = "Fairy Lore";

    public Fairy_Lore() {
        super();
        setDescription("ได้รับ MP และ MDEF เพิ่มขึ้นตาม INT ที่มี (ปัจจุบัน XA หน่วยและ XB หน่วยตามลำดับ)\n" +
                "และยังสามารถฟื้นฟู MP ได้แม้หมดสติอยู่");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.PHYSICAL);
        setManaReservePercent(0.4);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.2*INT"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.RESOURCE);

        getSkillMultiplier().put("XB",new SkillMultiplier("1.2*INT"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.RESOURCE);
        getSkillMultiplier().get("XB").getTags().add(SkillType.DEFENSE);
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
        double xa = getSkillMultiplier().get("XA").getResult();
        getSkillModifier().getStatModifierSafe(StatType.MANAPOINT).setFlat(xa);

        double xb = getSkillMultiplier().get("XB").getResult();
        getSkillModifier().getStatModifierSafe(StatType.MAGICALDEFENSE).setFlat(xb);
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
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

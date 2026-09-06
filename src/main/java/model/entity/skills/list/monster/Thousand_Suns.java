package model.entity.skills.list.monster;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillMultiplier;
import model.entity.skills.SkillTarget;
import model.type.*;

public class Thousand_Suns extends Skill {

    public static String NAME = "Thousand Suns";

    public Thousand_Suns() {
        super();
        setDescription("ครอบครอง Counter [The Suns]\n" +
                "เพิ่ม ATK 0.7 หน่วย และ DEF 1.3 หน่วยต่อ 1 The Suns ที่มี (ปัจจุบันเพิ่ม ATK XA หน่วย, DEF XB หน่วย)\n" +
                "มี The Suns ตามจำนวนมหาอัครสาวกดวงตะวันแห่งจักรวรรดิลาเรโซนิกในสนาม");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
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
        addCounter(CounterName.THE_SUNS);
        if (getUser().getCounter() == null) return;
        double counter = getUser().getCounter().get(CounterName.THE_SUNS);

        getSkillModifier().getStatModifierSafe(StatType.PHYSICALATTACK).setFlat(counter*0.7);
        getSkillModifier().getStatModifierSafe(StatType.RANGEDATTACK).setFlat(counter*0.7);
        getSkillModifier().getStatModifierSafe(StatType.MAGICALATTACK).setFlat(counter*0.7);
        getSkillModifier().getStatModifierSafe(StatType.PHYSICALDEFENSE).setFlat(counter*1.3);
        getSkillModifier().getStatModifierSafe(StatType.MAGICALDEFENSE).setFlat(counter*1.3);


        getSkillMultiplier().put("XA",new SkillMultiplier(counter*0.7));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);

        getSkillMultiplier().put("XB",new SkillMultiplier(counter*1.3));
        getSkillMultiplier().get("XB").getTags().add(SkillType.SCALING);

        translateDescription();
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

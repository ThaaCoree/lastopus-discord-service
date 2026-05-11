package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.*;

public class Over_Flowing_Wisdom extends Skill {

    public static String NAME = "Over Flowing Wisdom";

    public Over_Flowing_Wisdom() {
        super();
        setDescription("เมื่อฮีลแล้วผลฮีลเกินจาก Max HP ของยูนิตนั้น จะเปลี่ยนผล XA ของฮีลนั้นไปเป็น Debris ของยูนิตที่ได้รับผลแทน\n" +
                "หากมี The Forgotten Pages อย่างน้อย 6 ใบ จะเปลี่ยนผล XB\n" +
                "จะไม่สร้าง Debris เมื่อเป้าหมายมี Debris เท่ากับหรือมากกว่าจำนวนพลังชีวิต");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        setManaReservePercent(0.4);
        getPureTags().add(SkillType.DEBRIS);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.45"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIMIT);
        getSkillMultiplier().get("XA").setPercent(true);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.65"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.LIMIT);
        getSkillMultiplier().get("XB").setPercent(true);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
//                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
        );
//        spec.setFields(List.of(
//                new SkillInputSpec.InputField<String>("Mode", SkillInputSpec.InputType.SELECT)
//                        .options(List.of("choice","choice"))
//                        .labelProvider(String::toString)
//        ));
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
        combatFlow.getEventBus().register(ActionEvent.class, EventPhase.POST,0, event -> {
            if (!event.unit_source.equals(getUser()) || !event.hasActType(ActType.HEAL)) return;
            if (event.event_source.equals(getName())) return;
            double xa = getSkillMultiplier().get("XA").getResult();
            double xb = getSkillMultiplier().get("XB").getResult();
            double multiplier;
            if (getUser().getCounter().get(CounterName.THE_FORGOTTEN_PAGES) >= 6) {
                multiplier = xb;
            } else {
                multiplier = xa;
            }
            double finalMultiplier = multiplier;
            event.unit_target.forEach(target -> {
                double usable = target.getHealth().getUsable();
                double after_heal = target.getHealth().getRemaining()+event.getHeal(target.getName());
                double overflow = 0;
                double debris = target.getDebris().getRemaining();
                if (after_heal > usable && debris < usable) {
                    overflow = after_heal-usable;
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), target)
                                    .effect(ActionEffectType.CREATE_DEBRIS,overflow*finalMultiplier, event.heal_times)
                                    .addActType(ActType.CREATE_DEBRIS, ActType.SKILL_TRIGGER)
                                    .build()
                    );
                }
            });
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

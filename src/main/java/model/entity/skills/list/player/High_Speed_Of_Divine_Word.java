package model.entity.skills.list.player;

import controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillMultiplier;
import model.entity.skills.SkillTarget;
import model.type.*;

public class High_Speed_Of_Divine_Word extends Skill {

    public static String NAME = "High Speed of Divine Word";

    public High_Speed_Of_Divine_Word() {
        super();
        setDescription("ครอบครอง Counter [Incantation]\n" +
                "ผู้ใช้จะใช้งาน Incantation แทน Cast Speed\n" +
                "โดยจะได้รับ Incantation XA หน่วยต่อ INT 1 หน่วย\n" +
                "Incantation 100 หน่วยเทียบเท่า Cast Speed 100%");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        setHealthReservePercent(0.3);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.3*(1+DEX*0.0075)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);
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
        addCounter(CounterName.INCANTATION);
        double intel = getUser().getStatuses().get(StatusType.INTELLIGENCE).getFinal();
        double xa = getSkillMultiplier().get("XA").getResult();
        if (getUser().getCounter() != null) {
            getUser().counterSet(CounterName.INCANTATION, xa * intel);
        }
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

package model.entity.skills.list.npc;

import controller.CombatFlow;
import controller.event.EventBus;
import controller.event.events.ActionEvent;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillMultiplier;
import model.entity.skills.SkillTarget;
import model.type.EventPhase;
import model.type.SkillType;

public class Dawn_Bringer extends Skill {

    public static String NAME = "Dawn Bringer";

    public Dawn_Bringer() {
        super();
        setDescription("การสร้างความเสียหายและฮีลของ Daybreak จะเกิดขึ้นซ้ำ XA ครั้ง");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.FIGHTING_STYLE);
        setManaReservePercent(0.1);

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
        eventBus.register(ActionEvent.class, EventPhase.POST, 0, (ActionEvent event) -> {
            if (!event.event_source.equals("Daybreak")) return;
            if (!isActive()) return;

            int xa = (int) getSkillMultiplier().get("XA").getResult();
            if (event.heal_times >= 1) {
                event.heal_times += xa;
            }
            if (event.damage_times >= 1) {
                event.damage_times += xa;
            }

            sendSkillTriggerEvent(combatFlow, "Dawn Bringer triggered");
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

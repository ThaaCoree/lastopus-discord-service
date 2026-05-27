package model.entity.skills.list.monster;

import controller.CombatFlow;
import controller.event.EventBus;
import controller.event.events.ActionEvent;
import controller.event.events.RoundEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

public class Army_Commander extends Skill {

    public static String NAME = "Army Commander";

    public Army_Commander() {
        super();
        setDescription("เมื่อเริ่มต้นรอบเทิร์น มอบสถานะ Under Command ให้กับยูนิตพันธมิตรทีละยูนิต\n" +
                "Under Command : เพิ่ม Speed XA หน่วยและเพิ่มขึ้นเรื่อยๆตามลำดับที่ได้รับสถานะนี้");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("3"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.BUFF);
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
        applyBehaviour(combatFlow);
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(RoundEvent.class, EventPhase.POST, 0, (RoundEvent event) -> {
            applyBehaviour(combatFlow);
        });
    }

    private void applyBehaviour(CombatFlow combatFlow) {
        double xa = getSkillMultiplier().get("XA").getResult();
        int number = 1;
        for (Unit ally : getOtherAllies(combatFlow)) {
            ConditionManager.removeCondition(ally, "Under Command");
            Conditions command = new Conditions("Under Command");
            command.getStatModifiers(StatType.SPEED).setFlat(xa * number);
            command.setConditionType(ConditionType.BUFF);
            command.setConditionTierType(ConditionTierType.UNDISPELLABLE);

            ConditionManager.applyCondition(command, getUser(), ally, 99);
            number++;
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}

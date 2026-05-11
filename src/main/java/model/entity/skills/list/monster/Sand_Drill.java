package model.entity.skills.list.monster;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.entity.units.Unit;
import model.type.ActType;
import model.type.ActionEffectType;
import model.type.SkillType;

import java.util.List;

public class Sand_Drill extends Skill {

    public static String NAME = "Sand Drill";

    public Sand_Drill() {
        super();
        setDescription("ใช้งานได้เมื่ออยู่ในทรายเท่านั้น\n" +
                "พุ่งขึ้นจู่โจมยูนิตเป้าหมาย สร้างความเสียหายกายภาพ XA หน่วยโดยเป็นคริติคอลอย่างแน่นอน");
        setActionType("Action");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().put("XA",new SkillMultiplier("1.2*PATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.CRITICAL);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
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
        if (!skillTarget.getTarget(0).isEmpty()) {
            double xa = getSkillMultiplier().get("XA").getResult();
            List<Unit> targets = combatFlow.findUnit(skillTarget.getTarget(0));
            ActionEvent actionEvent = ActionEvent.builder(getName(), getUser(), targets)
                    .effect(ActionEffectType.DAMAGE_PHYSICAL, xa, 1)
                    .addActType(ActType.ATTACK, ActType.STRIKE)
                    .build();

            actionEvent.makeAllDamageCritical();

            sendActionEvent(combatFlow.getEventBus(), actionEvent);
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

    }

    @Override
    public String getName() {
        return NAME;
    }
}

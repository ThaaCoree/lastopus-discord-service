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
import model.type.StatusType;

public class Chomp_Chomp extends Skill {

    public static String NAME = "Chomp Chomp";

    public Chomp_Chomp() {
        super();
        setDescription("กระโจนใส่หนึ่งเป้าหมาย กัดสร้างความเสียหาย XA ให้กับมัน หากเป้าหมายมี STR น้อยกว่าผู้ใช้ มันรับความเสียหายซ้ำอีกครั้ง");
        setActionType("Action");
        setManaCost(0);
        setCooldown(3);
        getSkillMultiplier().put("XA",new SkillMultiplier("1.1*PATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
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
//        , 0, 0);
//                .addFields(
//                        new SkillInputSpec.InputField<String>("Damage", SkillInputSpec.InputType.NUMBER,1)
//                , 0, 1);
        return spec;
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        if (!skillTarget.getTarget(0).isEmpty()) {
            double xa = getSkillMultiplier().get("XA").getResult();
            skillTarget.getTarget(0).forEach(target -> {
                Unit unit = combatFlow.findUnit(target);
                double target_str = unit.getStatuses().get(StatusType.STRENGTH).getFinal();
                double user_str = getUser().getStatuses().get(StatusType.STRENGTH).getFinal();
                if (target_str < user_str) {
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), unit)
                                    .effect(ActionEffectType.DAMAGE_PHYSICAL, xa, 2)
                                    .addActType(ActType.ATTACK, ActType.STRIKE)
                                    .build()
                    );
                } else {
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), unit)
                                    .effect(ActionEffectType.DAMAGE_PHYSICAL, xa, 1)
                                    .addActType(ActType.ATTACK, ActType.STRIKE)
                                    .build()
                    );
                }
            });
        }
    }

    @Override
    public void calculateExtra() {

    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

    }

    @Override
    public String getName() {
        return NAME;
    }
}

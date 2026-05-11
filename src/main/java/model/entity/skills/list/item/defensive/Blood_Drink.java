package model.entity.skills.list.item.defensive;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.ActType;
import model.type.ActionEffectType;
import model.type.SkillType;

public class Blood_Drink extends Skill {

    public static String NAME = "Blood Drink";

    public Blood_Drink() {
        super();
        setDescription("เมื่อสร้างความเสียหายให้กับยูนิตที่มีเลือดสำเร็จ ฟื้นฟูพลังชีวิต XA ต่ำสุด 1 หน่วย");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("MissingHP/5"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.RECOVERY);
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
            double xa = getSkillMultiplier().get("XA").getResult();
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), getUser())
                            .effect(ActionEffectType.HEALTH_RECOVER, xa, 1)
                            .addActType(ActType.HEALTH_RECOVER)
                            .build()
            );
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

    }

    @Override
    public String getName() {
        return NAME;
    }
}

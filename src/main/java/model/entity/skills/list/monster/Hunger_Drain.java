package model.entity.skills.list.monster;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.ActType;
import model.type.ActionEffectType;
import model.type.SkillType;

public class Hunger_Drain extends Skill {

    public static String NAME = "Hunger Drain";

    public Hunger_Drain() {
        super();
        setDescription("ฆ่ายูนิตเป้าหมายที่หมดสภาพต่อสู้ ฟื้นฟูพลังชีวิตของตนเอง XA หน่วย\n" +
                "หากเป็นยูนิตศัตรู ทำพิธีกรรมก่อนหนึ่งเทิร์น");
        setActionType("Combine");
        setManaCost(0);
        setCooldown(1);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.25*HP"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.RECOVERY);
        getSkillMultiplier().get("XA").getTags().add(SkillType.RITUAL);

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
                            .addActType(ActType.CAST, ActType.HEALTH_RECOVER)
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

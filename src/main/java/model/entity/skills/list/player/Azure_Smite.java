package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.EventBus;
import controller.event.events.ActionEvent;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.ActType;
import model.type.ActionEffectType;
import model.type.SkillType;

public class Azure_Smite extends Skill {

    public static String NAME = "Azure Smite";

    public Azure_Smite() {
        super();
        setDescription("โจมตี เลือกระหว่างสร้างความเสียหายกายภาพไร้ธาตุ, กายภาพธาตุไฟ หรือกายภาพธาตุแสง XA หน่วยและฟื้นฟูพลังชีวิตให้ตัวเอง XB จากความเสียหายที่ทำได้");
        setActionType("Action");
        setManaCost(4);
        setCooldown(3);
        getSkillMultiplier().put("XA",new SkillMultiplier("2.2*PATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.FIRE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIGHT);
        getSkillMultiplier().get("XA").getTags().add(SkillType.ELEMENTAL);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.08*(1+HealAMP)"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.RECOVERY);
        getSkillMultiplier().get("XB").setPercent(true);
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
            double xb = getSkillMultiplier().get("XB").getResult();
            EventBus eventBus = combatFlow.getEventBus();
            combatFlow.findUnit(skillTarget.getTarget(0)).forEach(unit -> {
                sendActionEvent(eventBus,
                        ActionEvent.builder(getName(), getUser(), unit)
                                .effect(ActionEffectType.DAMAGE_PHYSICAL, xa, 1)
                                .addActType(ActType.ATTACK, ActType.STRIKE)
                                .build()
                );
                sendActionEvent(eventBus,
                        ActionEvent.builder(getName(), getUser(), getUser())
                                .effect(ActionEffectType.HEALTH_RECOVER, xb * xa, 1)
                                .addActType(ActType.HEALTH_RECOVER, ActType.ATTACK)
                                .build()
                );
            });
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

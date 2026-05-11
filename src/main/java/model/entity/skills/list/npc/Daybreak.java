package model.entity.skills.list.npc;

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

public class Daybreak extends Skill {

    public static String NAME = "Daybreak";

    public Daybreak() {
        super();
        setDescription("เรียกแสงจากท้องฟ้าเข้าหาเป้าหมาย เลือกระหว่างสร้างความเสียหายโดยตรงธาตุแสง XA หน่วย หรือฮีล XB หน่วย");
        setActionType("Action");
        setManaCost(10);
        setCooldown(3);
        getSkillMultiplier().put("XA",new SkillMultiplier("1.2*MATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIGHT);
        getSkillMultiplier().get("XA").getTags().add(SkillType.ELEMENTAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.SINGLE_TARGET);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.7*MATK*(1+HealAMP)"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.RECOVERY);
        getSkillMultiplier().get("XB").getTags().add(SkillType.HEALING);
        getSkillMultiplier().get("XB").getTags().add(SkillType.LIGHT);
        getSkillMultiplier().get("XB").getTags().add(SkillType.ELEMENTAL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.SINGLE_TARGET);
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

            for (String name : skillTarget.getTarget(0)) {
                Unit target = combatFlow.findUnit(name);
                if (isAlly(target, combatFlow)) {
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), target)
                                    .effect(ActionEffectType.HEALTH_RECOVER, xb, 1)
                                    .addActType(ActType.HEAL, ActType.HEALTH_RECOVER, ActType.CAST)
                                    .build()
                    );
                } else {
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), target)
                                    .effect(ActionEffectType.DAMAGE_PURE, xa, 1)
                                    .addActType(ActType.CAST, ActType.STRIKE)
                                    .build()
                    );
                }
            }
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

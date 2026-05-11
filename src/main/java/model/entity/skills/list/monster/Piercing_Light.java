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

public class Piercing_Light extends Skill  {

    public static String NAME = "Piercing Light";

    public Piercing_Light() {
        super();
        setDescription("ยิงลำแสงไปยังเป้าหมายทั้งหมดในสนาม สร้างความเสียหายโดยตรง XA หน่วย ไม่สามารถถูกหลบหรือบล็อกได้");
        setActionType("Action");
        setManaCost(0);
        setCooldown(2);
        getSkillMultiplier().put("XA",new SkillMultiplier("1.6*MATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIGHT);
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
                    ActionEvent.builder(getName(), getUser(), getEnemies(combatFlow))
                            .effect(ActionEffectType.DAMAGE_PURE, xa, 1)
                            .addActType(ActType.CAST, ActType.STRIKE)
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

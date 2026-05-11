package main.java.model.entity.skills.list.npc;

import main.java.controller.CombatFlow;
import main.java.controller.event.events.ActionEvent;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.entity.skills.SkillMultiplier;
import main.java.model.type.ActType;
import main.java.model.type.ActionEffectType;
import main.java.model.type.CounterName;
import main.java.model.type.SkillType;

public class Paper_Slash extends Skill {

    public static String NAME = "Paper Slash";

    public Paper_Slash() {
        super();
        setDescription("ใช้กระดาษทั้งหมดโจมตีเป้าหมาย สร้างความเสียหายเวทแผ่นละ XA หน่วย");
        setActionType("Action");
        setManaCost(12);
        setCooldown(3);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.3*MATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.SINGLE_TARGET);

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
        double count = getUser().getCounter().get(CounterName.PAPER);
        if (!skillTarget.getTarget(0).isEmpty()) {
            double xa = getSkillMultiplier().get("XA").getResult();

                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                                .effect(ActionEffectType.DAMAGE_MAGICAL, xa, (int) count)
                                .addActType(ActType.CAST, ActType.STRIKE)
                                .build()
                );
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

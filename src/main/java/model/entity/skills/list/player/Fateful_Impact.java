package main.java.model.entity.skills.list.player;

import main.java.controller.CombatFlow;
import main.java.controller.event.events.ActionEvent;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.entity.skills.SkillMultiplier;
import main.java.model.type.*;
import util.LogWriterUtil;

import java.util.List;

public class Fateful_Impact extends Skill {

    public static String NAME = "Fateful Impact";

    public Fateful_Impact() {
        super();
        setDescription("โจมตีแบบทั่วไป มีโอกาส XB ที่จะสร้างความเสียหายเพิ่มเติม XA หน่วย\n" +
                "เมื่อสร้างความเสียหายเพิ่มเติมด้วยสกิลนี้ ความเสียหายจะกลายเป็นโดยตรง\n" +
                "โอกาสสำเร็จจะไม่มากกว่า XC");
        setActionType("Action");
        setManaCost(7);
        setCooldown(2);
        setManaReservePercent(0.10);
        getSkillMultiplier().put("XA",new SkillMultiplier("1.1*LUK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.PURE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.28+(LUK*0.002)"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.CHANCE);
        getSkillMultiplier().get("XB").setPercent(true);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.56+(LUK*0.0008)"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.LIMIT);
        getSkillMultiplier().get("XC").setPercent(true);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
        );
        spec    .addFields(
                new SkillInputSpec.InputField<String>("Damage Type", SkillInputSpec.InputType.SELECT, 0)
                        .options(List.of("Physical","Ranged","Magical"), 0)
                        .labelProvider(String::toString, 0)
        , 0, 0);
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
        combatFlow.randAllDices();
        if (combatFlow.getExtraDice() <= getSkillMultiplier().get("XB").getResult()) {
            if (!skillTarget.getTarget(0).isEmpty()) {
                double xa = getSkillMultiplier().get("XA").getResult();
                double patk = getUser().getStats().get(StatType.PHYSICALATTACK).getFinal();
                double ratk = getUser().getStats().get(StatType.RANGEDATTACK).getFinal();
                double matk = getUser().getStats().get(StatType.MAGICALATTACK).getFinal();

                if (skillTarget.getTarget(1).contains("Physical")) {
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                                    .effect(ActionEffectType.DAMAGE_PURE, xa+patk, 1)
                                    .addActType(ActType.ATTACK, ActType.STRIKE)
                                    .build()
                    );
                }

                if (skillTarget.getTarget(1).contains("Ranged")) {
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                                    .effect(ActionEffectType.DAMAGE_PURE, xa+ratk, 1)
                                    .addActType(ActType.ATTACK, ActType.STRIKE)
                                    .build()
                    );
                }

                if (skillTarget.getTarget(1).contains("Magical")) {
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                                    .effect(ActionEffectType.DAMAGE_PURE, xa+matk, 1)
                                    .addActType(ActType.CAST, ActType.STRIKE)
                                    .build()
                    );
                }
            }
        } else {
            LogWriterUtil.log("Fateful Impact miss! do nothing special");
        }

    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        double xb = getSkillMultiplier().get("XB").getResult();
        double xc = getSkillMultiplier().get("XC").getResult();
        getSkillMultiplier().get("XB").setResult(Math.min(xb, xc));
        translateDescription();
    }

    @Override
    public String getName() {
        return NAME;
    }
}

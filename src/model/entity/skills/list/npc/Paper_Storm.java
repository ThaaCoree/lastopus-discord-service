package model.entity.skills.list.npc;

import main.controller.CombatFlow;
import main.controller.event.events.ActionEvent;
import model.entity.Conditions;
import model.entity.skills.*;
import model.type.*;
import util.LogWriterUtil;

public class Paper_Storm extends Skill {

    public static String NAME = "Paper Storm";

    public Paper_Storm() {
        super();
        setDescription("เปลี่ยนกระดาษทั้งหมดเข้าสู่โหมดบุก กระดาษแต่ละแผ่นจู่โจมไปยังเป้าหมายหนึ่งครั้ง สร้างความเสียหายเวท XA หน่วย\n" +
                "จากนั้นสร้างความเสียหายเวท XB หน่วยให้กับลิซ่า, ทำให้กระดาษทุกแผ่นจู่โจมอีกครั้ง ทำแบบนี้ซ้ำจนกว่าลิซ่าจะหมดสภาพต่อสู้\n" +
                "หากลิซ่าไม่สูญเสีย Debris หรือพลังชีวิตจากสกิลนี้, หยุดการทำงาน");
        setActionType("Turn");
        setManaCost(16);
        setCooldown(5);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.15*MATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.1*MP"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DRAWBACK);
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
            while (getUser().getHealth().getRemaining() > 0) {
                double count = getUser().getCounter().get(CounterName.PAPER);
                SkillInstance skillInstance = getUser().findSkill("Paper Fortress");
                skillInstance.setReserving(false);

                double xa = getSkillMultiplier().get("XA").getResult();
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                                .effect(ActionEffectType.DAMAGE_MAGICAL, xa, (int) count)
                                .addActType(ActType.CAST, ActType.STRIKE)
                                .build()
                );

                double xb = getSkillMultiplier().get("XB").getResult();
                if (getUser().getDebris().getRemaining() > 0) {
                    getUser().sumRemainingDebris(xb*-1);
                }
                if (getUser().getDebris().getRemaining() <= 0) {
                    getUser().setRemainingDebris(0);
                    getUser().sumRemainingMana(xb*-1);
                }
                if (getUser().getMana().getRemaining() <= 0) {
                    getUser().setRemainingMana(0);
                    getUser().sumRemainingHealth(xb*-1);
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

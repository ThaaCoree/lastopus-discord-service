package main.java.model.entity.skills.list.player;

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
import util.LogWriterUtil;

public class Phantom_Strike extends Skill {

    public static String NAME = "Phantom Strike";

    public Phantom_Strike() {
        super();
        setDescription("เมื่อพันธมิตรทำการจู่โจม จ่าย Illusion & Dream 4 หน่วยเพื่อสร้างหมอกขึ้นที่รอบยูนิตเป้าหมายที่พันธมิตรกำลังจู่โจมนั้นและพุ่งเข้าโจมตีจากจุดบอดในเหตุการณ์เดียวกันด้วย สร้างความเสียหายกายภาพ XA หน่วย\n" +
                "ยกเลิกการต้านทานดีบัพของเป้าหมายหนึ่งอย่างเป็นเวลา XB รอบเทิร์น");
        setActionType("Hold Action");
        setManaCost(8);
        setCooldown(2);
        getPureTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().put("XA",new SkillMultiplier("1.6*PATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);

        getSkillMultiplier().put("XB",new SkillMultiplier("1"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DURATION);
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
            if (getUser().getCounter().get(CounterName.IllusionAndDream) >= 4) {
                double xa = getSkillMultiplier().get("XA").getResult();
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                                .effect(ActionEffectType.DAMAGE_PHYSICAL, xa, 1)
                                .addActType(ActType.ATTACK, ActType.STRIKE)
                                .build()
                );
                getUser().counterSet(CounterName.IllusionAndDream, 4 * -1);
            } else {
                LogWriterUtil.log("Not enough Illusion & Dream!");
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

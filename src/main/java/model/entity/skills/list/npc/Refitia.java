package model.entity.skills.list.npc;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.ActType;
import model.type.ActionEffectType;
import model.type.SkillType;

public class Refitia extends Skill {

    public static String NAME = "Refitia";

    public Refitia() {
        super();
        setDescription("เลือกพันธมิตรที่หมดสติอย่างน้อยสองคน หรือพันธมิตรที่เพิ่งตายไปไม่เกินสามเทิร์นหนึ่งคน ปลุกให้ตื่นหรือชุบชีวิตกลับมาพร้อมพลังชีวิต XA ลบล้างเวทมนตร์อื่นที่ส่งผลกับยูนิตนั้นอยู่");
        setActionType("Turn");
        setManaCost(0);
        setCooldown(12);
        setManaReservePercent(0.3);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.5"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.RECOVERY);
        getSkillMultiplier().get("XA").getTags().add(SkillType.DISPEL);
        getSkillMultiplier().get("XA").setPercent(true);
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
        setManaCost(getUser().getMana().getUsable());
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        if (!skillTarget.getTarget(0).isEmpty()) {
            double xa = getSkillMultiplier().get("XA").getResult();
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .effect(ActionEffectType.HEALTH_RECOVER, xa, 1)
                            .addActType(ActType.HEAL, ActType.HEALTH_RECOVER)
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

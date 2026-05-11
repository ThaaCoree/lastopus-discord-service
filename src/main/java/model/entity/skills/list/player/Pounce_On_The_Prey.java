package main.java.model.entity.skills.list.player;

import main.java.controller.CombatFlow;
import main.java.controller.event.events.ActionEvent;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.entity.skills.SkillMultiplier;
import main.java.model.type.ActType;
import main.java.model.type.ActionEffectType;
import main.java.model.type.SkillType;

public class Pounce_On_The_Prey extends Skill {

    public static String NAME = "Pounce on the Prey";

    public Pounce_On_The_Prey() {
        super();
        setDescription("หลังจากที่มียูนิตได้รับความเสียหายจากสกิล Impending Stab เป้าหมายจะติดเครื่องหมายของสกิล Pounce on the Prey และสามารถเปิดใช้งานสกิลนี้ได้\n" +
                "เมื่อใช้งาน เลือกเคลื่อนที่ตนเองผ่านเงาไปยังด้านหลังหรือด้านข้างของยูนิตที่ติดเครื่องหมายไว้และจู่โจมหนึ่งชุด สร้างความเสียหายกายภาพ XA หน่วย\n" +
                "หากทำความเสียหายใส่ยูนิตเเบบถากๆ สกิลจะไม่นับคูลดาวน์และเครื่องหมายจะไม่หายไป\n" +
                "หากเป้าหมายไม่มีด้านข้างหรือด้านหลัง จะเลือกเคลื่อนที่ไปยังจุดไหนก็ได้รอบตัวเป้าหมาย");
        setActionType("Combine");
        setManaCost(4);
        setCooldown(2);
        getSkillMultiplier().put("XA",new SkillMultiplier("1.7*PATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STEALTH);
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
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .effect(ActionEffectType.DAMAGE_PHYSICAL, xa, 1)
                            .addActType(ActType.ATTACK, ActType.STRIKE)
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

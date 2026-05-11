package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.EventBus;
import controller.event.events.ActionEvent;
import controller.event.events.SkillUse;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.ActType;
import model.type.EventPhase;
import model.type.SkillType;
import util.LogWriterUtil;

public class False_Authority extends Skill {

    public static String NAME = "False Authority";

    public False_Authority() {
        super();
        setDescription("ทุกครั้งที่มีการใช้สกิลของตนเอง จะสร้างร่างแยกที่สุ่มถืออาวุธหนึ่งในห้าชนิดจาก Sanctum of Sorrow, แยกได้สูงสุดหนึ่งร่างต่อรอบเทิร์น และมีร่างแยกสูงสุด XA ร่าง \n" +
                "ร่างแยกจะมีเจตจำนงเป็นของตัวเองและไม่สามารถถูกควบคุมได้ และจะถูกทำลายเมื่อได้รับความเสียหายโดยมีเงื่อนไขดังนี้ \n" +
                "หากถูกศัตรูทำลาย Scarlet จะได้รับ XB Debris และ 1 Combine Action แต่จะสูญเสียมานา XC หน่วย \n" +
                "หากถูกเพื่อนร่วมทีมทำลาย Scarlet จะฟื้นฟูพลังชีวิต XD หน่วยและมานา XE หน่วย แต่จะไม่สามารถใช้อาวุธของร่างแยกนั้นๆได้จนกว่าจะจบรอบเทิร์น \n" +
                "หากถูก Scarlet ทำลาย จะถูกลด Movement Speed ครึ่งหนึ่ง แต่จะสามารถร่ายสกิลของอาวุธที่ร่างแยกนั้นๆถือได้ทันที");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        setManaReservePercent(0.4);
        getSkillMultiplier().put("XA",new SkillMultiplier("2"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIMIT);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.2*UsableHP"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.DEFENSE);
        getSkillMultiplier().get("XB").getTags().add(SkillType.DEBRIS);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.2*MP"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.DRAWBACK);

        getSkillMultiplier().put("XD",new SkillMultiplier("0.45*INT*(1+HealAMP)"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XD").getTags().add(SkillType.RECOVERY);

        getSkillMultiplier().put("XE",new SkillMultiplier("0.035*INT"));
        getSkillMultiplier().get("XE").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XE").getTags().add(SkillType.RECOVERY);
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
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(SkillUse.class, EventPhase.POST, 0, (SkillUse event) -> {
            if (event.source != getUser()) return;
            LogWriterUtil.log(">False Authority triggered");

            sendActionEvent(combatFlow.getEventBus(),
                                ActionEvent.builder(getName(), getUser(), getUser())
                                        .addActType(ActType.SKILL_TRIGGER)
                                        .build()
                        );
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

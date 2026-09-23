package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillMultiplier;
import model.entity.skills.SkillTarget;
import model.type.ActType;
import model.type.ActionEffectType;
import model.type.SkillType;

public class To_The_Stars_And_The_Abyss extends Skill {

    public static String NAME = "To the stars and the abyss";

    public To_The_Stars_And_The_Abyss() {
        super();
        setDescription("ครอบครอง Counter [Fuel & Cog]\n" +
                "ได้รับ Fuel & Cog 1 หน่วยเมื่อมีพันธมิตรได้รับความเสียหาย\n" +
                "เรียกรถไฟออกมา รีเซ็ท HP/MP ให้พันธมิตรอื่นทั้งหมด จากนั้นล้างดีบัพที่มีระดับ General หรือต่ำกว่าทั้งหมดและลดคูลดาวน์สกิลทั้งหมดให้ XB รอบเทิร์น, ยูนิตที่หมดสติจะถูกปลุกให้ตื่น อีกทั้งยังเลือกที่จะเคลื่อนย้ายพันธมิตรไปยังบริเวณไหนก็ได้\n" +
                "ใช้งานได้เมื่อมี Fuel & Cog อย่างน้อย 42 หน่วยเท่านั้น\n" +
                "สกิลนี้ใช้ได้เพียงครั้งเดียวต่อการต่อสู้ แล้วจะถูกเปลี่ยนเป็น Let the stars guide you\n" +
                "Let the stars guide you : เรียกรถไฟพุ่งชนเพื่อสร้างความเสียหายโดยตรง XA หน่วยเป็นเส้นตรง ทำงานทันทีทุกครั้งเมื่อสิ้นสุดเทิร์นตัวเอง");
        setActionType("Turn");
        setManaCost(25);
        setCooldown(3);
        setManaReservePercent(0.25);
        getSkillMultiplier().put("XA",new SkillMultiplier("3*PATK + 3*MATK + 3*RATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.PURE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.AOE);

        getSkillMultiplier().put("XB",new SkillMultiplier("2"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.LIMIT);
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
//        , 0, 0);
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
            int duration = (int) getSkillMultiplier().get("XB").getResult();
            Conditions condition = combatFlow.findCondition("Glyph Shocked");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .effect(ActionEffectType.DAMAGE_PHYSICAL, xa, 1)
                            .condition(condition, duration)
                            .addActType(ActType.ATTACK, ActType.STRIKE)
                            .build()
            );
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
//        EventBus eventBus = combatFlow.getEventBus();
//        eventBus.register(ActionEvent.class, EventPhase.POST, 0, (ActionEvent event) -> {
//            if (!event.hasActType(ActType.HEAL) || event.unit_source != getUser() || event.event_source.equals(getName())) return;
//            List<Unit> targets = event.unit_target;
//            double heal_amount = event.getHeal();
//
//            sendActionEvent(combatFlow.getEventBus(),
//                                ActionEvent.builder(getName(), getUser(), targets)
//                                        .effect(ActionEffectType.HEALTH_RECOVER,heal_amount, 1)
//                                        .addActType(ActType.HEAL, ActType.HEALTH_RECOVER, ActType.SKILL_TRIGGER)
//                                        .build()
//                        );
//        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

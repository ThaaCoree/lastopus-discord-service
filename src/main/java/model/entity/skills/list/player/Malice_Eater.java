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

public class Malice_Eater extends Skill {

    public static String NAME = "Malice Eater";

    public Malice_Eater() {
        super();
        setDescription("ครอบครอง Counter [Malice Point]\n" +
                "เมื่อตนเองหรือพันธมิตรในรัศมี XB เมตรตกเป็นเป้าหมายของผล Debuff ที่มีระดับ Advanced หรือต่ำกว่า, สามารถเลือกใช้สกิลนี้ในการต่อต้านผลนั้น และได้รับ XC Malice Point แทน\n" +
                "เมื่อสร้างความเสียหาย, สามารถใช้งาน Malice Point เพื่อเพิ่มความเสียหายที่สร้างได้ XA หน่วยต่อแต้ม");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(1);
        setManaReservePercent(0.1);
        setCooldown_can_change(false);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.4*PATK + 0.4*MATK + 0.4*RATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);

        getSkillMultiplier().put("XB",new SkillMultiplier("2"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DISTANCE);
        getSkillMultiplier().get("XB").getTags().add(SkillType.AOE);

        getSkillMultiplier().put("XC",new SkillMultiplier("2"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.LIMIT);
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

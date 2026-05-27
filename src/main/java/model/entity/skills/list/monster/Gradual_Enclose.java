package model.entity.skills.list.monster;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillMultiplier;
import model.entity.skills.SkillTarget;
import model.entity.units.Unit;
import model.type.ActType;
import model.type.ActionEffectType;
import model.type.SkillType;

import java.util.List;

public class Gradual_Enclose extends Skill {

    public static String NAME = "Gradual Enclose";

    public Gradual_Enclose() {
        super();
        setDescription("สร้างกำแพงขึ้นระหว่างเสาสองต้น ยูนิตศัตรูที่อยู่ในเส้นทางจะถูกกระแทก ได้รับความเสียหายกายภาพ XB หน่วยและกระเด็นออก\n" +
                "เมื่อสร้างกำแพงจนปิดล้อมได้ครบสี่ด้านและมียูนิตศัตรูอยู่ภายในไม่เกิน 2 ยูนิต สร้างเขตต้นกำเนิดแสงและเริ่มการนับถอยหลังที่จะสิ้นสุดลงเมื่อจบรอบเทิร์นนี้\n" +
                "เมื่อสิ้นสุดการนับถอยหลัง ยูนิตศัตรูทั้งหมดที่อยู่ในการปิดล้อมจะได้รับความเสียหายโดยตรง XA หน่วย แต่ถ้ามียูนิตศัตรูอยู่ในเขตต้นกำเนิดแสง มันจะได้รับความเสียหายนั้นแทน");
        setActionType("Combine");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("5*MATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);

        getSkillMultiplier().put("XB",new SkillMultiplier("1.4*MATK"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XB").getTags().add(SkillType.PHYSICAL);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
        );
        spec    .addFields(
                new SkillInputSpec.InputField<String>("Mode", SkillInputSpec.InputType.SELECT, 0)
                        .options(List.of("Wall","Enclose"), 0)
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
        if (!skillTarget.getTarget(0).isEmpty()) {
            for (String name : skillTarget.getTarget(0)) {
                Unit target = combatFlow.findUnit(name);
                if (skillTarget.getDecision(name,0,0).contains("Wall")) {
                    double xb = getSkillMultiplier().get("XB").getResult();
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), target)
                                    .effect(ActionEffectType.DAMAGE_PHYSICAL, xb, 1)
                                    .addActType(ActType.CAST, ActType.STRIKE)
                                    .build()
                    );
                }
                if (skillTarget.getDecision(name,0,0).contains("Enclose")) {
                    double xa = getSkillMultiplier().get("XA").getResult();
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), target)
                                    .effect(ActionEffectType.DAMAGE_PURE, xa, 1)
                                    .addActType(ActType.CAST, ActType.STRIKE)
                                    .build()
                    );
                }
            }
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

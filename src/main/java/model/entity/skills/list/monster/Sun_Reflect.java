package main.java.model.entity.skills.list.monster;

import main.java.controller.CombatFlow;
import main.java.controller.event.events.ActionEvent;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillMultiplier;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.entity.units.Unit;
import main.java.model.type.ActType;
import main.java.model.type.ActionEffectType;
import main.java.model.type.SkillType;

public class Sun_Reflect extends Skill {

    public static String NAME = "Sun Reflect";

    public Sun_Reflect() {
        super();
        setDescription("ยิงเวทมนตร์แสงไปยังแหล่งแสงอื่นในสนาม จากนั้นเวทมนตร์แตกออกเป็นสองเส้นทาง หนึ่งเส้นทางพุ่งเข้าจู่โจมเป้าหมาย อีกหนึ่งเส้นทาง ชิ่งไปยังแหล่งแสงถัดไป\n" +
                "ทำซ้ำจนกว่าจะไม่เหลือแหล่งแสงที่ไม่เคยชิ่งในสนามอีก แสงเส้นทางสุดท้ายพุ่งเข้าจู่โจมเป้าหมายด้วยเช่นกัน การจู่โจมแต่ละครั้งสร้างความเสียหายโดยตรง XA หน่วย\n" +
                "การจู่โจมใส่เป้าหมายเดิมตั้งแต่ครั้งที่ 6 เป็นต้นไปจะกลายเป็นความเสียหายจริง");
        setActionType("Turn");
        setManaCost(0);
        setCooldown(2);
        getSkillMultiplier().put("XA",new SkillMultiplier("1.2*MATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.ELEMENTAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIGHT);
        getSkillMultiplier().get("XA").getTags().add(SkillType.PURE);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
        );
        spec    .addFields(
                new SkillInputSpec.InputField<String>("Hits", SkillInputSpec.InputType.NUMBER, 0)
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
            double xa = getSkillMultiplier().get("XA").getResult();

            for (String name : skillTarget.getTarget(0)) {
                Unit target = combatFlow.findUnit(name);
                int hits = Integer.parseInt(skillTarget.getDecision(name, 0, 0));
                if (hits >= 6) {
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), target)
                                    .effect(ActionEffectType.DAMAGE_PURE, xa, 5)
                                    .addActType(ActType.CAST, ActType.STRIKE)
                                    .build()
                    );

                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), target)
                                    .effect(ActionEffectType.DAMAGE_TRUE, xa, hits-5)
                                    .addActType(ActType.STRIKE)
                                    .build()
                    );
                } else {
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), target)
                                    .effect(ActionEffectType.DAMAGE_PURE, xa, hits)
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

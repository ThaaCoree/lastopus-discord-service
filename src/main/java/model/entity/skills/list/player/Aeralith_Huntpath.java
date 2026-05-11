package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillMultiplier;
import model.entity.skills.SkillTarget;
import model.entity.units.Unit;
import model.type.*;

public class Aeralith_Huntpath extends Skill {

    public static String NAME = "Aeralith Huntpath";

    public Aeralith_Huntpath() {
        super();
        setDescription("เมื่อจู่โจม สามารถเคลื่อนที่แบบสามมิติในทิศทางที่ต้องการได้ XA เมตร\n" +
                "การโจมตีแต่ละครั้งจะทิ้งขนนกโปร่งใสไว้บนวัตถุที่การโจมตีเข้าปะทะ ขนนกโปร่งใสจะไม่ติดอยู่กับยูนิตและทะลุผ่านไป\n" +
                "ในการโจมตีแต่ละครั้ง สามารถดึงขนนกโปร่งใสกลับมาหาตัวเองกี่อันก็ได้ หากมันพุ่งผ่านเป้าหมายที่มี Ka'rahn อยู่ สร้างความเสียหายกายภาพ XB หน่วยต่อขนนกที่พุ่งผ่าน\n\n" +
                "หากขนนกพุ่งผ่านยูนิตที่มี Ka'rahn จากระยะอย่างน้อย XC เมตร เพิ่ม AttackSpeed +XD จนกว่าจะจบรอบเทิร์นนี้");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.PHYSICAL);
        setManaReservePercent(0.45);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.15*MSPD"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.MOVEMENT);
        getSkillMultiplier().get("XA").getTags().add(SkillType.DISTANCE);

        getSkillMultiplier().put("XB",new SkillMultiplier("1.3*RATK"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.STRIKE);

        getSkillMultiplier().put("XC",new SkillMultiplier("10"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.DISTANCE);
        getSkillMultiplier().get("XC").getTags().add(SkillType.REQUIREMENT);

        getSkillMultiplier().put("XD",new SkillMultiplier("0.2"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XD").setPercent(true);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
        );
        spec    .addFields(
                new SkillInputSpec.InputField<String>("Hits", SkillInputSpec.InputType.NUMBER, 0)
                        .labelProvider(String::toString, 0)
        , 0, 0)
                .addFields(
                        new SkillInputSpec.InputField<String>("Damage", SkillInputSpec.InputType.NUMBER,1)
                , 0, 1);
        return spec;
    }

    @Override
    public void calculateExtra() {

    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        if (!skillTarget.getTarget(0).isEmpty()) {
            double xb = getSkillMultiplier().get("XB").getResult();
            for (String name : skillTarget.getTarget(0)) {
                int hits = Integer.parseInt(skillTarget.getDecision(name, 0, 0));
                Unit target = combatFlow.findUnit(name);
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), target)
                                .effect(ActionEffectType.DAMAGE_PHYSICAL, xb, hits)
                                .addActType(ActType.ATTACK, ActType.STRIKE)
                                .build()
                );
                if (!getUser().hasCondition("Aeralith Huntpath")) {
                    if (skillTarget.getDecision(name, 0, 1).equals("TRUE")) {
                        Conditions condition = new Conditions("Aeralith Huntpath");
                        condition.getStatModifiers(StatType.ATTACKSPEED).setFlat(getSkillMultiplier().get("XD").getResult());

                        condition.setConditionType(ConditionType.NEUTRAL);
                        condition.setConditionTierType(ConditionTierType.BOUND);

                        addConditionToDatabase(condition, combatFlow);

                        ConditionManager.applyCondition(condition, getUser(), getUser(), 1);
                    }
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

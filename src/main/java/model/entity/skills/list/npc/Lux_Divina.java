package model.entity.skills.list.npc;

import controller.CombatFlow;
import controller.event.EventBus;
import controller.event.events.ActionEvent;
import controller.event.events.ResourceEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

public class Lux_Divina extends Skill implements SkillWithCondition {

    public static String NAME = "Lux Divina";

    public Lux_Divina() {
        super();
        setDescription("สร้างแสงขึ้นโอบล้อมเป้าหมาย ลดความเสียหายกายภาพจากการโจมตีที่เป้าหมายจะได้รับเหลือหนึ่งในสี่ หรือสลายเวทมนตร์จู่โจมที่เข้ามา XA ครั้ง");
        setActionType("Action");
        setManaCost(12);
        setCooldown(3);
        getPureTags().add(SkillType.SPELL);
        getPureTags().add(SkillType.DEFENSE);
        getSkillMultiplier().put("XA",new SkillMultiplier("1"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIMIT);
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

        } else {
            skillTarget.getTarget(0).add(getUser().getName());
        }
        int duration = 16;
        double xa = getSkillMultiplier().get("XA").getResult();
        Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Lux Divinia");
        for (int i=0;i<xa;i++) {
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(),getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .condition(condition, duration)
                            .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                            .build());
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Lux Divinia");
        condition.setDescription("ลดความเสียหายกายภาพที่จะได้รับครั้งถัดไปเหลือหนึ่งในสี่ หรือยกเลิกความเสียหายเวทมนตร์ที่จะได้รับ");

        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.GENERAL);

        //remove and re-add to database
        addConditionToDatabase(condition, combatFlow);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ResourceEvent.class, EventPhase.POST, 0, (ResourceEvent event) -> {
                if (event.target.hasCondition("Lux Divinia")) {
                    if (event.effectType.equals(ActionEffectType.DAMAGE_PHYSICAL)) {
                        event.amount /= 4;
                    }
                    if (event.effectType.equals(ActionEffectType.DAMAGE_MAGICAL)) {
                        event.amount = 0;
                    }
                    ConditionManager.removeOneCondition(event.target, "Lux Divinia");
                }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

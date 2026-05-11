package model.entity.skills.list.player;

import main.controller.CombatFlow;
import main.controller.event.events.*;
import manager.ConditionManager;
import model.entity.ConditionInstance;
import model.entity.Conditions;
import model.entity.PassiveNode;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;
import util.LogWriterUtil;

public class Ark_Of_The_Lost_Souls extends Skill implements SkillWithCondition {

    public static String NAME = "Ark of the lost souls";

    public Ark_Of_The_Lost_Souls() {
        super();
        setDescription("Passive : หากในเทิร์นของตนเองไม่ออกแอคชั่นใดๆ การ Block ครั้งถัดไปจะสำเร็จแน่นอน ได้รับผลนี้ในตอนเริ่มการต่อสู้ด้วย\n" +
                "เมื่อใช้งาน ตั้งท่าป้องกันโดยไม่เคลื่อนที่ การบล็อกครั้งถัดไปจะสำเร็จแน่นอน, เพิ่ม Debris ทันที XA หน่วย และได้รับสถานะ Star's Anchor เป็นเวลา XB รอบเทิร์น ซึ่งเพิ่ม Debris XA หน่วยเมื่อเริ่มต้นรอบเทิร์น\n" +
                "การใช้งานแอคชั่นใดๆยกเว้นการโจมตีจะยกเลิกสถานะ Star's Anchor");
        setActionType("Turn");
        setManaCost(8);
        setCooldown(5);
        setManaReservePercent(0.35);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.2*UsableHP"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.DEFENSE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.DEBRIS);
        getSkillMultiplier().get("XA").getTags().add(SkillType.BUFF);

        getSkillMultiplier().put("XB",new SkillMultiplier("3"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DURATION);
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
        double xa = getSkillMultiplier().get("XA").getResult();
        int duration = (int) getSkillMultiplier().get("XB").getResult();
        Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Star's Anchor");
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(),getUser(), getUser())
                        .effect(ActionEffectType.CREATE_DEBRIS, xa, 1)
                        .condition(condition, duration)
                        .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                        .build());
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Star's Anchor");

        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.ADVANCED);
        condition.setDescription("จะได้รับ Debris "+getSkillMultiplier().get("XA").getResultString()+" หน่วยเมื่อเริ่มรอบเทิร์น");

        //remove and re-add to database
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(condition.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(condition.getName(), condition);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        combatFlow.getEventBus().register(RoundEvent.class, EventPhase.POST, 0, event -> {
            for (Unit unit : combatFlow.getAllUnit().values()) {
                if (unit.hasCondition("Star's Anchor")) {
                        double debris = getSkillMultiplier().get("XA").getResult();
                        double ia = 1;
                        double ua = 1;
                        for (PassiveNode node : unit.getAllocatedPassives().values()) {
                            if (node.getName().equals("Intense Affection")) {
                                ia = 2;
                            }
                            if (node.getName().equals("Unaffected Affection")) {
                                ua = 0;
                            }
                        }
                        sendActionEvent(combatFlow.getEventBus(),
                                ActionEvent.builder(getName(), getUser(), getUser())
                                        .effect(ActionEffectType.CREATE_DEBRIS, debris*ia*ua, 1)
                                        .addActType(ActType.CREATE_DEBRIS, ActType.SKILL_TRIGGER)
                                        .build()
                        );
                }
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

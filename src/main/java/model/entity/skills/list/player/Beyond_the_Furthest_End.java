package main.java.model.entity.skills.list.player;

import main.java.controller.CombatFlow;
import main.java.controller.event.events.ActionEvent;
import main.java.manager.ConditionManager;
import main.java.model.entity.Conditions;
import main.java.model.entity.skills.*;
import main.java.model.entity.units.Unit;
import main.java.model.type.*;

import java.util.List;

public class Beyond_the_Furthest_End extends Skill implements SkillWithCondition {

    public static String NAME = "Beyond the Furthest End";

    public Beyond_the_Furthest_End() {
        super();
        setDescription("เดี่ยว - เมื่อได้รับความเสียหายที่จะทำให้หมดสภาพต่อสู้ ทนทานต่อความเสียหายนั้น ทำให้พลังชีวิตเหลือ 1 หน่วย รับสถานะ Oversoul เป็นเวลา XA รอบเทิร์น\n" +
                "คู่ - เมื่อจะได้รับความเสียหายที่จะทำให้หมดสภาพต่อสู้ เลือกระหว่างหลบหรือบล็อก จากนั้นทั้งสองร่างโจมตีสวนกลับ สร้างความเสียหายกายภาพ XB หน่วย หากหลบหรือบล็อกการโจมตีสำเร็จ สร้างความเสียหายเพิ่มเติมตามความเสียหายที่หลีกเลี่ยงสำเร็จด้วย\n" +
                "Oversoul : เพิ่ม STR และ AGI XC");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(3);
        setManaReservePercent(0.4);
        getSkillMultiplier().put("XA",new SkillMultiplier("2"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XB",new SkillMultiplier("2.4*PATK"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.STRIKE);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.25*(1+BuffAMP)"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XC").setPercent(true);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        List<String> choices = List.of("Solo", "Duo");
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser(), choices
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.CUSTOM, 0)
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 1)
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
        if (skillTarget.getTarget(0).contains("Solo")) {
            int duration = (int) getSkillMultiplier().get("XA").getResult();
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Oversoul");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(),getUser(), getUser())
                            .condition(condition, duration)
                            .addActType(ActType.CONDITION_GIVEN,ActType.SKILL_TRIGGER)
                            .build());
        }
        if (!skillTarget.getTarget(1).isEmpty()) {
            double xb = getSkillMultiplier().get("XB").getResult();
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .effect(ActionEffectType.DAMAGE_PHYSICAL, xb, 1)
                            .addActType(ActType.ATTACK, ActType.STRIKE)
                            .build()
            );
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Oversoul");
        condition.getStatusModifiers(StatusType.STRENGTH).setGlobalMult(getSkillMultiplier().get("XC").getResult());
        condition.getStatusModifiers(StatusType.AGILITY).setGlobalMult(getSkillMultiplier().get("XC").getResult());

        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.ADVANCED);

        //remove and re-add to database
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(condition.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(condition.getName(), condition);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
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

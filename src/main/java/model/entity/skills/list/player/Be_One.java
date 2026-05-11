package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.EventBus;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

public class Be_One extends Skill implements SkillWithCondition {

    public static String NAME = "Be one";

    public Be_One() {
        super();
        setDescription("เข้าสู่สถานะ Nightmare เป็นระยะเวลา XA รอบเทิร์น แล้วเคลื่อนย้ายไปยังเงาที่ใกล้ที่สุด\n" +
                "สถานะ Nightmare : เปลี่ยนตนเองเป็นเงาเเละสามารถย้ายที่ไปยังพื้นเงาที่ใดก็ได้ตามระยะการมองเห็น หากโจมตีหรือถูกโจมตีจะออกสถานะนี้");
        setActionType("Combine");
        setManaCost(3);
        setCooldown(2);
        getPureTags().add(SkillType.SPELL);
        getPureTags().add(SkillType.SHADOW);
        getPureTags().add(SkillType.MOVEMENT);
        getSkillMultiplier().put("XA",new SkillMultiplier("3"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.DURATION);
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
        EventBus eventBus = combatFlow.getEventBus();
        sendActionEvent(eventBus,
                ActionEvent.builder(getName(), getUser(), getUser())
                        .addActType(ActType.CONDITION_GIVEN, ActType.CAST)
                        .build()
        );
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Nightmare");
        condition.setDescription("กำลังอยู่ภายใต้เงา ลดโอกาสการถูกตรวจจับลง");

        condition.setConditionType(ConditionType.NEUTRAL);
        condition.setConditionTierType(ConditionTierType.GENERAL);

        //remove and re-add to database
        addConditionToDatabase(condition, combatFlow);

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

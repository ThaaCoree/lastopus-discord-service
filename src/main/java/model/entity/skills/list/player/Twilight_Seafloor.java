package main.java.model.entity.skills.list.player;

import main.java.controller.CombatFlow;
import main.java.controller.event.EventBus;
import main.java.controller.event.events.ActionEvent;
import main.java.controller.event.events.RoundEvent;
import main.java.manager.ConditionManager;
import main.java.model.entity.Conditions;
import main.java.model.entity.skills.*;
import main.java.model.entity.units.Unit;
import main.java.model.type.*;

public class Twilight_Seafloor extends Skill implements SkillWithCondition {

    public static String NAME = "Twilight Seafloor";

    public Twilight_Seafloor() {
        super();
        setDescription("กางสนาม และ Twelve จะถูกหนวดปลาหมึกกักขังเข้าสู่สถานะ Imprisoned ด้วยพลังชีวิต XA หน่วยจนกว่าจะถูกโจมตี\n" +
                "ระหว่างนี้ พันธมิตรทั้งหมดในสนามจะได้รับ Action เพิ่มขึ้น 1 หน่วย " +
                "และหากจบรอบเทิร์นโดยยังอยู่ในผลของสกิลนี้ ฟื้นฟู MP ให้กับพันธมิตรทั้งหมดในสนาม XC หน่วย\n" +
                "หาก Twelve ใช้งาน MP ครบ XB หน่วยในการต่อสู้ครั้งนี้แล้ว การใช้งานสกิลนี้จะรีเซ็ต MP ของ Twelve กลับไปเป็นครึ่งหนึ่ง");
        setActionType("Turn");
        setManaCost(0);
        setCooldown(7);
        getPureTags().add(SkillType.RESOURCE);
        setHealthReservePercent(0.2);
        getSkillMultiplier().put("XA",new SkillMultiplier("1*PATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.IMPRISON);

        getSkillMultiplier().put("XB",new SkillMultiplier("1.5*UsableMP"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.REQUIREMENT);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.07*MATK"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.RECOVERY);
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
        setManaCost(getUser().getResources().get(ResourceType.MANA).getUsable());
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        if (getUser().getCounter().get(CounterName.USED_MANA) >= getSkillMultiplier().get("XB").getResult()) {
            double usable = getUser().getResources().get(ResourceType.MANA).getUsable();
            getUser().getResources().get(ResourceType.MANA).setRemaining(usable/2);
        }

        int duration = 99;
        Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Twilight Seafloor");
        Conditions imprison = combatFlow.getDatabase().getAllConditionMap().get("Imprison");
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(),getUser(), getUser())
                        .condition(condition, duration)
                        .condition(imprison, duration)
                        .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                        .build());
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Twilight Seafloor");
        double xb = getSkillMultiplier().get("XB").getResult();
        condition.setDescription("ถูก Imprison, มอบแอคชันเพิ่มเติมหนึ่งหน่วยให้กับยูนิตพันธมิตรเมื่อเริ่มต้นเทิร์น และฟื้นฟูมานา "+xb+" หน่วยให้ยูนิตพันธมิตรทั้งหมดเมื่อเริ่มรอบเทิร์น");

        condition.setConditionType(ConditionType.NEUTRAL);
        condition.setConditionTierType(ConditionTierType.BOUND);

        //remove and re-add to database
        addConditionToDatabase(condition, combatFlow);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(RoundEvent.class, EventPhase.POST, 0, (RoundEvent event) -> {
            if (getUser().hasCondition("Twilight Seafloor")) {
                double xb = getSkillMultiplier().get("XB").getResult();
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), getAllies(combatFlow))
                                .effect(ActionEffectType.MANA_RECOVER, xb, 1)
                                .addActType(ActType.MANA_RECOVER, ActType.SKILL_TRIGGER)
                                .build()
                );
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

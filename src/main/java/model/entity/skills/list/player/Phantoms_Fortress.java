package main.java.model.entity.skills.list.player;

import main.java.controller.CombatFlow;
import main.java.controller.event.events.ActionEvent;
import main.java.manager.ConditionManager;
import main.java.model.entity.Conditions;
import main.java.model.entity.skills.*;
import main.java.model.entity.units.Unit;
import main.java.model.type.*;

import java.util.List;

public class Phantoms_Fortress extends Skill implements SkillWithCondition {

    public static String NAME = "Phantom's Fortress";

    public Phantoms_Fortress() {
        super();
        setDescription("ได้รับ Counter [Fate Charge]\n" +
                "Fate Charge จะเพิ่มขึ้นหนึ่งหน่วยเมื่อสร้างความเสียหายธาตุไฟหรือแสง\n" +
                "เมื่อใช้งาน เลือกระหว่าง ฟื้นฟูพลังชีวิตให้ตนเอง XA หน่วย หรือรับสถานะ Forged ซึ่งเพิ่ม DEF XB เป็นเวลา XC รอบเทิร์น\n" +
                "การใช้งานสกิลนี้เพื่อฮีลจะทำให้สูญเสีย 2 Fate Charge, การใช้เพื่อรับสถานะ Forged จะทำให้สูญเสีย 4 Fate Charge");
        setActionType("Combine");
        setManaCost(6);
        setCooldown(1);
        getSkillMultiplier().put("XA",new SkillMultiplier("1*PATK*(1+HealAMP)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.RECOVERY);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.35*(1+BuffAMP)"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XB").setPercent(true);

        getSkillMultiplier().put("XC",new SkillMultiplier("2"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.DURATION);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        List<String> choices = List.of("Heal", "Forged");
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser(), choices
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.CUSTOM, 0)
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
        if (!getUser().getRawCounterMap().containsKey(CounterName.FATE_CHARGE)) {
            getUser().getRawCounterMap().put(CounterName.FATE_CHARGE,0.0);
            getUser().getCounter().put(CounterName.FATE_CHARGE,0.0);
        }
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        if (skillTarget.getTarget(0).contains("Heal")) {
            double xa = getSkillMultiplier().get("XA").getResult();
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .effect(ActionEffectType.HEALTH_RECOVER, xa, 1)
                            .addActType(ActType.CAST, ActType.HEALTH_RECOVER)
                            .build());
            getUser().counterSum(CounterName.FATE_CHARGE, 2*-1);
        }

        if (skillTarget.getTarget(0).contains("Forged")) {
            int duration = (int) getSkillMultiplier().get("XC").getResult();
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Forged");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .condition(condition, duration)
                            .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                            .build());
            getUser().counterSum(CounterName.FATE_CHARGE, 4*-1);
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Forged");
        condition.getStatModifiers(StatType.MAGICALDEFENSE).setGlobalMult(getSkillMultiplier().get("XB").getResult());
        condition.getStatModifiers(StatType.PHYSICALDEFENSE).setGlobalMult(getSkillMultiplier().get("XB").getResult());

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

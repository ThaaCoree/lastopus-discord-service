package model.entity.skills.list.player;

import javafx.beans.InvalidationListener;
import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

public class Shrunken_Knowledges extends Skill implements SkillWithCondition {

    public static String NAME = "Shrunken Knowledges";

    public Shrunken_Knowledges() {
        super();
        setDescription("เมื่อเกิดผลฟื้นฟูจาก Twelve จนครบ 12 ครั้ง Twelve จะได้รับกระดาษ [The Forgotten Pages] ออกมาวนรอบตัว\n" +
                "บัพ PATK, MATK และ RATK ให้แก่พันธมิตรอื่นทุกคนในสนาม XA หน่วย เก็บได้สูงสุด XB ใบ เมื่อมีอย่างน้อย 3 ใบจะได้รับ Accuracy XC หน่วยด้วย\n" +
                "และเมื่อมีอย่างน้อย 6 ใบแล้วจะทำให้พันธมิตรทุกคนในสนามมีอย่างน้อย 2 Reaction");
        setActionType("Passive");
        setManaReservePercent(0.3);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.15*MATK*(1+BuffAMP)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XA").getTags().add(SkillType.WATER);

        getSkillMultiplier().put("XB",new SkillMultiplier("6"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.LIMIT);

        getSkillMultiplier().put("XC",new SkillMultiplier("2*MATK*(1+BuffAMP)"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XC").getTags().add(SkillType.WATER);

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
        if (getUser().getCounter() != null) {
            getUser().getCounter().addListener((InvalidationListener) event -> {
                if (getUser().getCounter().get(CounterName.SHRUNKEN_KNOWLEDGES) >= 12) {
                    getUser().counterSet(CounterName.SHRUNKEN_KNOWLEDGES, 0);
                    getUser().counterIncrement(CounterName.THE_FORGOTTEN_PAGES);
                }
            });
        }
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        int duration = 99;
        for (Unit ally : getAllies(combatFlow)) {
            if (ally.hasCondition("Forgotten Pages")) {
                ConditionManager.removeCondition(ally,"Forgotten Pages");
            }
        }
        Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Forgotten Pages");
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(), getUser(), getOtherAllies(combatFlow))
                        .condition(condition, duration)
                        .build()
        );
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        double buffAMP = 1+getUser().getStats().get(StatType.BUFFAMPLIFIER).getFinal();
        double count = getUser().getCounter().get(CounterName.THE_FORGOTTEN_PAGES);
        Conditions condition = new Conditions("Forgotten Pages");
        double xa = getSkillMultiplier().get("XA").getResult() * count;
        condition.getStatModifiers(StatType.PHYSICALATTACK).setFlat(xa);
        condition.getStatModifiers(StatType.MAGICALATTACK).setFlat(xa);
        condition.getStatModifiers(StatType.RANGEDATTACK).setFlat(xa);
        if (count >= 3) {
            condition.getStatModifiers(StatType.ACCURACY).setFlat(getSkillMultiplier().get("XC").getResult());
        }
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
        combatFlow.getEventBus().register(ActionEvent.class, EventPhase.POST, 0, event -> {
            if (event.unit_source == getUser()) {
                if (!event.hasActType(ActType.HEALTH_RECOVER) && !event.hasActType(ActType.MANA_RECOVER)) return;
                for (Unit unit : event.unit_target) {
                    int loop = 1;
                    while (loop <= event.heal_times){
                        loop++;
                        getUser().counterIncrement(CounterName.SHRUNKEN_KNOWLEDGES);
                    }
                    loop = 1;
                    while (loop <= event.mana_recover_times){
                        loop++;
                        getUser().counterIncrement(CounterName.SHRUNKEN_KNOWLEDGES);
                    }
                }
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

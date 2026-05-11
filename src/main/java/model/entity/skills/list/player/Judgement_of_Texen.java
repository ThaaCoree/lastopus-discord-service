package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

import java.util.List;

public class Judgement_of_Texen extends Skill implements SkillWithCondition {

    public static String NAME = "Judgement of Texen";

    public Judgement_of_Texen() {
        super();
        setDescription("อัญเชิญเจตจำนงของเท็กเซ่นผู้ถักทอ สร้างผลพิเศษด้วยเวทมนตร์ไปทั้งสนาม\n" +
                "สุ่มส่งผลหนึ่งอย่างให้กับพันธมิตรทั้งหมด ระหว่าง\n" +
                "ฟื้นฟูพลังชีวิตให้ XC หน่วย หรือ มอบสถานะ Divine Critical ซึ่งเพิ่ม Crit Chance XD เป็นเวลา XE รอบเทิร์น\n"+
                "จากนั้นสุ่มส่งผลอีกหนึ่งอย่างให้กับศัตรูทั้งหมด ระหว่าง\n" +
                "มอบสถานะ Fate Fracture ซึ่งลด DEF XG เป็นเวลา XH รอบเทิร์น\n" +
                "หรือ มอบสถานะ Twisted Fate ซึ่งลด ATK XI เป็นเวลา XJ รอบเทิร์น");
        setActionType("Action");
        setManaCost(11);
        setCooldown(5);
        getSkillMultiplier().put("XA",new SkillMultiplier("1"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.RESOURCE);

        getSkillMultiplier().put("XB",new SkillMultiplier("1"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XC",new SkillMultiplier("((PATK+RATK+MATK)*2)*(1+HealAMP)"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.RECOVERY);

        getSkillMultiplier().put("XD",new SkillMultiplier("0.3*(1+BuffAMP)"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XD").setPercent(true);

        getSkillMultiplier().put("XE",new SkillMultiplier("2"));
        getSkillMultiplier().get("XE").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XF",new SkillMultiplier("1"));
        getSkillMultiplier().get("XF").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XG",new SkillMultiplier("0.57*(1+DebuffAMP)"));
        getSkillMultiplier().get("XG").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XG").setPercent(true);

        getSkillMultiplier().put("XH",new SkillMultiplier("2"));
        getSkillMultiplier().get("XH").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XI",new SkillMultiplier("0.25*(1+DebuffAMP)"));
        getSkillMultiplier().get("XI").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XI").setPercent(true);

        getSkillMultiplier().put("XJ",new SkillMultiplier("2"));
        getSkillMultiplier().get("XJ").getTags().add(SkillType.DURATION);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        List<String> choices = List.of("Heal", "Divine Critical", "Fate Fracture", "Twisted Fate");
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser(), choices
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.CUSTOM, 0)
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.CUSTOM, 1)
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
        double xc = getSkillMultiplier().get("XC").getResult();
        if (!skillTarget.getTarget(0).isEmpty()) {
            if (skillTarget.getTarget(0).contains("Heal")) {
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), getAllies(combatFlow))
                                .effect(ActionEffectType.HEALTH_RECOVER, xc, 1)
                                .addActType(ActType.HEAL, ActType.HEALTH_RECOVER)
                                .build()
                );
            }
            if (skillTarget.getTarget(0).contains("Divine Critical")) {
                int duration2 = (int) getSkillMultiplier().get("XE").getResult();
                Conditions condition2 = combatFlow.getDatabase().getAllConditionMap().get("Divine Critical");
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), getAllies(combatFlow))
                                .condition(condition2, duration2)
                                .addActType(ActType.CONDITION_GIVEN)
                                .build()
                );
            }

            if (skillTarget.getTarget(1).contains("Fate Fracture")) {
                int duration3 = (int) getSkillMultiplier().get("XJ").getResult();
                Conditions condition3 = combatFlow.getDatabase().getAllConditionMap().get("Fate Fracture");
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), getEnemies(combatFlow))
                                .condition(condition3, duration3)
                                .addActType(ActType.CONDITION_GIVEN)
                                .build()
                );
            }

            if (skillTarget.getTarget(1).contains("Twisted Fate")) {
                int duration3 = (int) getSkillMultiplier().get("XE").getResult();
                Conditions condition3 = combatFlow.getDatabase().getAllConditionMap().get("Twisted Fate");
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), getEnemies(combatFlow))
                                .condition(condition3, duration3)
                                .addActType(ActType.CONDITION_GIVEN)
                                .build()
                );
            }
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions goldenBlessing = new Conditions("Golden Blessing");
        Conditions divineCritical = new Conditions("Divine Critical");
        Conditions fickleFate = new Conditions("Fickle Fate");
        Conditions fateFracture = new Conditions("Fate Fracture");
        Conditions twistedFate = new Conditions("Twisted Fate");

        goldenBlessing.setDescription("ได้รับ Action "+getSkillMultiplier().get("XA").getResult()+" หน่วย");
        goldenBlessing.setConditionType(ConditionType.BUFF);
        goldenBlessing.setConditionTierType(ConditionTierType.UNDISPELLABLE);

        divineCritical.getStatModifiers(StatType.CRITCHANCE).setGlobalMult(getSkillMultiplier().get("XD").getResult());
        divineCritical.setConditionType(ConditionType.BUFF);
        divineCritical.setConditionTierType(ConditionTierType.UNDISPELLABLE);

        fickleFate.setDescription("ไม่สามารถใช้งานแอคชันได้");
        fickleFate.setConditionType(ConditionType.DEBUFF);
        fickleFate.setConditionTierType(ConditionTierType.BOUND);

        fateFracture.getStatModifiers(StatType.PHYSICALDEFENSE).setGlobalMult(getSkillMultiplier().get("XG").getResult());
        fateFracture.getStatModifiers(StatType.MAGICALDEFENSE).setGlobalMult(getSkillMultiplier().get("XG").getResult());
        fateFracture.setConditionType(ConditionType.DEBUFF);
        fateFracture.setConditionTierType(ConditionTierType.BOUND);

        twistedFate.getStatModifiers(StatType.RANGEDATTACK).setGlobalMult(getSkillMultiplier().get("XI").getResult());
        twistedFate.getStatModifiers(StatType.PHYSICALATTACK).setGlobalMult(getSkillMultiplier().get("XI").getResult());
        twistedFate.getStatModifiers(StatType.MAGICALATTACK).setGlobalMult(getSkillMultiplier().get("XI").getResult());
        twistedFate.setConditionType(ConditionType.DEBUFF);
        twistedFate.setConditionTierType(ConditionTierType.BOUND);

        //remove and re-add to database
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(goldenBlessing.getName()));
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(divineCritical.getName()));
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(fickleFate.getName()));
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(fateFracture.getName()));
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(twistedFate.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(goldenBlessing.getName(), goldenBlessing);
        combatFlow.getDatabase().getAllConditionMap().put(divineCritical.getName(), divineCritical);
        combatFlow.getDatabase().getAllConditionMap().put(fickleFate.getName(), fickleFate);
        combatFlow.getDatabase().getAllConditionMap().put(fateFracture.getName(), fateFracture);
        combatFlow.getDatabase().getAllConditionMap().put(twistedFate.getName(), twistedFate);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(twistedFate, unit);
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

package model.entity.skills.list.player;

import main.controller.CombatFlow;
import main.controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

public class Ode_Of_The_Shooting_Star extends Skill implements SkillWithCondition {

    public static String NAME = "Ode of the Shooting Star/Last Chance";

    public Ode_Of_The_Shooting_Star() {
        super();
        setDescription("เพิ่ม DEF XA หน่วย(เพิ่มขึ้นตาม AGI, STR และ VIT) โดยจะไม่สามารถใช้งานแอคชันใดๆได้นอกจากเคลื่อนที่และหลอกล่อศัตรูพร้อมป้องกันตัวเอง และจะฮีลตัวเอง XB หน่วยทุกครั้งที่เริ่มรอบเทิร์น\n" +
                "ทุกการจู่โจมหนึ่งชุดที่ได้รับ จะเพิ่ม Fate Charge 2 แสต็คซึ่งทับซ้อนได้ไม่จำกัด\n" +
                "สามารถปิดใช้งานได้ด้วย Combined Action\n" +
                "หากหมดสภาพต่อสู้ในระหว่างใช้งาน, สเลเฟียร์จะพุ่งตัวเข้าหาศัตรูโดยใช้แสต็ค Fate Charge ทั้งหมดที่มีเป็นธาตุไฟ(1)หรือแสง(2)และระเบิดทันที สร้างความเสียหายกายภาพ XC หน่วยต่อ Fate Charge\n" +
                "จากนั้นทอยเต๋า 1d20 หากลูกเต๋าเป็น 10 หรือมากกว่าจะฟื้นตัวด้วยพลังชีวิต XD หน่วยหากเป็น 20 ฟื้นด้วยพลังชีวิต XE หน่วยหากทอยได้น้อยกว่า 10 จะหมดสภาพการต่อสู้");
        setActionType("Action");
        setManaCost(11);
        setCooldown(5);
        getSkillMultiplier().put("XA",new SkillMultiplier("(6*AGI+6*STR+6*VIT)*(1+BuffAMP)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.DEFENSE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XA").getTags().add(SkillType.BUFF);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.2*UsableHP*(1+HealAMP)"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.RECOVERY);
        getSkillMultiplier().get("XB").getTags().add(SkillType.HEALING);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.7*PATK"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XC").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XC").getTags().add(SkillType.ELEMENTAL);
        getSkillMultiplier().get("XC").getTags().add(SkillType.FIRE);
        getSkillMultiplier().get("XC").getTags().add(SkillType.LIGHT);

        getSkillMultiplier().put("XD",new SkillMultiplier("0.1*UsableHP*(1+HealAMP)"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.RECOVERY);
        getSkillMultiplier().get("XD").getTags().add(SkillType.HEALING);

        getSkillMultiplier().put("XE",new SkillMultiplier("0.7*UsableHP*(1+HealAMP)"));
        getSkillMultiplier().get("XE").getTags().add(SkillType.RECOVERY);
        getSkillMultiplier().get("XE").getTags().add(SkillType.HEALING);
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
            double xc = getSkillMultiplier().get("XC").getResult();
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .effect(ActionEffectType.DAMAGE_PHYSICAL, xc, 1)
                            .addActType(ActType.ATTACK, ActType.STRIKE)
                            .build()
            );
        } else {
            int duration = 12;
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Ode of the Shooting Star");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(),getUser(), getUser())
                            .condition(condition, duration)
                            .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                            .build());
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Ode of the Shooting Star");
        condition.getStatModifiers(StatType.MAGICALDEFENSE).setFlat(getSkillMultiplier().get("XA").getResult());
        condition.getStatModifiers(StatType.PHYSICALDEFENSE).setFlat(getSkillMultiplier().get("XA").getResult());

        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.UNDISPELLABLE);

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

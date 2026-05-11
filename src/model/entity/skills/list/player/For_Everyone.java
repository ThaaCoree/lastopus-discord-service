package model.entity.skills.list.player;

import main.controller.CombatFlow;
import main.controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

public class For_Everyone extends Skill implements SkillWithCondition {

    public static String NAME = "For Everyone";

    public For_Everyone() {
        super();
        setDescription("จะทำงานโดยอัตโนมัติเมื่อหมดสภาพต่อสู้ จะทำให้ผู้ใช้ฟื้นตื่นกลับมาเป็นจิตวิญญาณภูติที่เป็นในรูปแบบพลังเวท โดยมีค่าสถานะเท่ากับร่างต้น ใช้งานได้ครั้งเดียวต่อการต่อสู้\n" +
                "ปล่อยดวงจิตของตัวเองออกเป็น [จิตวิญญาณแห่งความมุ่งมั่นสุดท้าย] ระยะเวลาสูงสุด XB รอบเทิร์น โดยระหว่างนี้จะเปลี่ยนเอา HealAMP และ BuffAMP ไปเพิ่ม MATK แทน\n" +
                "การฟื้นฟูในสกิลทั้งหมดจะกลายเป็นความเสียหาย สกิลและพาสซีพที่ทำการฟื้นฟูจะเปลี่ยนเป้าหมายไปเป็นศัตรูแทน และไม่สามารถรับการฟื้นฟูได้จากที่อื่น\n" +
                "เมื่อ HP ลดลงเหลือน้อยกว่าครึ่งจากสูงสุดจะเข้าสู่สถานะ Guilt จะได้รับสเตตัสเพิ่มขึ้น และสามารถใช้งาน การเสียสละของภูติ ได้\n" +
                "\n" +
                "[การเสียสละของภูติ]\n" +
                "เคลื่อนย้ายตัวเองไปยังจุดหมายสละพลังชีวิตที่เหลือทั้งหมด สร้างความเสียหายเวท XA ในรัศมีสองเมตร\n" +
                "\n" +
                "ระหว่างจิตวิญญาณแห่งความมุ่งมั่นสุดท้าย จะไม่ได้รับความเสียหายกายภาพ และถ้าได้รับความเสียหายที่ทำให้หมดสภาพต่อสู้ในครั้งแรก จะทำให้ยังมีพลังชีวิตเหลือ 1 จนกว่าจะถึงเทิร์นของผู้ใช้\n" +
                "Guilt : เพิ่มสเตตัสทั้งหมด XC");
        setActionType("-");
        setManaCost(0);
        setCooldown(0);
        setManaReservePercent(0.25);
        getSkillMultiplier().put("XA",new SkillMultiplier("2.9*MATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.AOE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.REDEMPTION);

        getSkillMultiplier().put("XB",new SkillMultiplier("4"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.45*(1+BuffAMP)"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XC").setPercent(true);
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
        if (getUser().hasCondition("The One Who Last Wish")) {
            double heal_amp = getUser().getStats().get(StatType.HEALAMPLIFIER).getFinal();
            double buff_amp = getUser().getStats().get(StatType.BUFFAMPLIFIER).getFinal();

            getSkillModifier().getStatModifierSafe(StatType.MAGICALATTACK).setGlobalMult(heal_amp+buff_amp);
        }
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        if (!skillTarget.getTarget(0).isEmpty()) {

        } else {
            skillTarget.getTarget(0).add(getUser().getName());
        }
        int duration = (int) getSkillMultiplier().get("XB").getResult();
        Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("The One Who Last Wish");
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(),getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                        .condition(condition, duration)
                        .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                        .build());
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("The One Who Last Wish");
        condition.setDescription("กำลังอยู่ในสภาพร่างจิต ไม่รับความเสียหายกายภาพ");

        condition.setConditionType(ConditionType.NEUTRAL);
        condition.setConditionTierType(ConditionTierType.UNDISPELLABLE);

        Conditions condition2 = new Conditions("Guilt");
        condition2.getStatusModifiers(StatusType.STRENGTH).setGlobalMult(getSkillMultiplier().get("XC").getResult());
        condition2.getStatusModifiers(StatusType.AGILITY).setGlobalMult(getSkillMultiplier().get("XC").getResult());
        condition2.getStatusModifiers(StatusType.VITALITY).setGlobalMult(getSkillMultiplier().get("XC").getResult());
        condition2.getStatusModifiers(StatusType.DEXTERITY).setGlobalMult(getSkillMultiplier().get("XC").getResult());
        condition2.getStatusModifiers(StatusType.WISDOM).setGlobalMult(getSkillMultiplier().get("XC").getResult());
        condition2.getStatusModifiers(StatusType.INTELLIGENCE).setGlobalMult(getSkillMultiplier().get("XC").getResult());
        condition2.getStatusModifiers(StatusType.LUCK).setGlobalMult(getSkillMultiplier().get("XC").getResult());

        condition2.setConditionType(ConditionType.NEUTRAL);
        condition2.setConditionTierType(ConditionTierType.UNDISPELLABLE);

        //remove and re-add to database
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(condition.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(condition.getName(), condition);
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(condition2.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(condition2.getName(), condition2);

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

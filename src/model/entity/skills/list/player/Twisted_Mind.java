package model.entity.skills.list.player;

import main.controller.CombatFlow;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

public class Twisted_Mind extends Skill implements SkillWithCondition {

    public static String NAME = "Twisted Mind";

    public Twisted_Mind() {
        super();
        setDescription("เวทมนตร์ที่บริสุทธิ์ถูกแทนที่ด้วยมลทิน สังเวยพลังชีวิตที่เหลือทั้งหมด เปลี่ยนสนามให้กลายเป็นมิติจิตใจของ Shiranui\n" +
                "ทันทีที่ใช้งานสกิลนี้ Shiranui สามารถเปลี่ยนตำแหน่งของยูนิตทั้งหมดในสนามได้อย่างอิสระ และกลายเป็นผู้ควบคุมสนามเป็นเวลา XA รอบเทิร์น\n" +
                "ในระหว่างที่สกิลนี้ทำงานอยู่ ผลของ Imagetion จะทำงานเทิร์นละครั้งด้วย\n" +
                "เมื่อเริ่มเทิร์นของรอบ เลือกมอบบัพหนึ่งอย่างจากรายการต่อไปนี้\n" +
                "- เพิ่ม ATK ให้กับพันธมิตรทั้งหมด XB\n" +
                "- ฟื้นฟู HP ให้กับพันธมิตรทั้งหมด XC หน่วย\n" +
                "- ฟื้นฟู MP ให้กับพันธมิตรทั้งหมด XD หน่วย\n" +
                "- เพิ่ม AGI ให้กับพันธมิตรทั้งหมด XE\n" +
                "- เมื่อมีพันธมิตรหนึ่งยูนิตที่ได้รับความเสียหายเกิน HP ทำให้ HP เหลือไม่น้อยกว่าหนึ่งหน่วย XF ครั้ง\n" +
                "จากนั้นเลือกมอบดีบัพให้ศัตรูหนึ่งอย่างจากรายการต่อไปนี้\n" +
                "- ลด ATK ของศัตรูทั้งหมด XG\n" +
                "- ลด DEF ของศัตรูทั้งหมด XH\n" +
                "- ลด AGI ของศัตรูทั้งหมด XI\n" +
                "- มอบความสับสนให้กับศัตรู XJ ยูนิต ทำให้ Action ไม่สามารถออกสกิลได้\n" +
                "- สร้างความเสียหายเวท XK ให้กับศัตรูทั้งหมด ไม่สามารถถูกหลบหลีกหรือบล็อกได้ และทำการฟื้นฟูให้พันธมิตรทั้งหมดตามความเสียหายที่ทำได้สูงสุด\n\n" +
                "สกิลนี้สามารถถูกใช้งานในระหว่างที่ Shiranui หมดสติได้\n" +
                "ในระหว่างผลของสกิล Shiranui จะไม่อยู่ในสนาม ไม่สามารถรับการฟื้นฟู และไม่รับผลของสกิลอื่น หลังจากสิ้นสุดผลของสกิลนี้ Shiranui จะปรากฏบนสนามแบบสุ่ม");
        setActionType("Turn");
        setManaCost(16);
        setCooldown(9);
        getSkillMultiplier().put("XA",new SkillMultiplier("3"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.2*(1+BuffAMP)"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XB").getTags().add(SkillType.REDEMPTION);
        getSkillMultiplier().get("XB").setPercent(true);

        getSkillMultiplier().put("XC",new SkillMultiplier("1.75*MATK*(1+HealAMP)"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.HEALING);
        getSkillMultiplier().get("XC").getTags().add(SkillType.RECOVERY);
        getSkillMultiplier().get("XC").getTags().add(SkillType.REDEMPTION);

        getSkillMultiplier().put("XD",new SkillMultiplier("0.07*MATK"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.RECOVERY);
        getSkillMultiplier().get("XD").getTags().add(SkillType.REDEMPTION);

        getSkillMultiplier().put("XE",new SkillMultiplier("0.3*(1+BuffAMP)"));
        getSkillMultiplier().get("XE").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XE").getTags().add(SkillType.REDEMPTION);
        getSkillMultiplier().get("XE").setPercent(true);

        getSkillMultiplier().put("XF",new SkillMultiplier("1"));
        getSkillMultiplier().get("XF").getTags().add(SkillType.LIMIT);

        getSkillMultiplier().put("XG",new SkillMultiplier("0.25*(1+DebuffAMP)"));
        getSkillMultiplier().get("XG").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XG").getTags().add(SkillType.REDEMPTION);
        getSkillMultiplier().get("XG").setPercent(true);

        getSkillMultiplier().put("XH",new SkillMultiplier("0.4*(1+DebuffAMP)"));
        getSkillMultiplier().get("XH").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XH").getTags().add(SkillType.REDEMPTION);
        getSkillMultiplier().get("XH").setPercent(true);

        getSkillMultiplier().put("XI",new SkillMultiplier("0.32*(1+DebuffAMP)"));
        getSkillMultiplier().get("XI").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XI").getTags().add(SkillType.REDEMPTION);
        getSkillMultiplier().get("XI").setPercent(true);

        getSkillMultiplier().put("XJ",new SkillMultiplier("1"));
        getSkillMultiplier().get("XJ").getTags().add(SkillType.LIMIT);

        getSkillMultiplier().put("XK",new SkillMultiplier("1*MATK"));
        getSkillMultiplier().get("XK").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XK").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XK").getTags().add(SkillType.AOE);
        getSkillMultiplier().get("XK").getTags().add(SkillType.REDEMPTION);
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
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Power Pump");
        condition.getStatModifiers(StatType.RANGEDATTACK).setGlobalMult(getSkillMultiplier().get("XB").getResult());
        condition.getStatModifiers(StatType.MAGICALATTACK).setGlobalMult(getSkillMultiplier().get("XB").getResult());
        condition.getStatModifiers(StatType.PHYSICALATTACK).setGlobalMult(getSkillMultiplier().get("XB").getResult());

        condition.setConditionType(ConditionType.BUFF);
        condition.setConditionTierType(ConditionTierType.ADVANCED);

        Conditions condition2 = new Conditions("FORCE");
        condition2.getStatusModifiers(StatusType.AGILITY).setGlobalMult(getSkillMultiplier().get("XE").getResult());

        condition2.setConditionType(ConditionType.BUFF);
        condition2.setConditionTierType(ConditionTierType.ADVANCED);

        Conditions condition3 = new Conditions("Omit");
        condition3.getStatModifiers(StatType.RANGEDATTACK).setGlobalMult(getSkillMultiplier().get("XG").getResult()*(-1));
        condition3.getStatModifiers(StatType.MAGICALATTACK).setGlobalMult(getSkillMultiplier().get("XG").getResult()*(-1));
        condition3.getStatModifiers(StatType.PHYSICALATTACK).setGlobalMult(getSkillMultiplier().get("XG").getResult()*(-1));

        condition3.setConditionType(ConditionType.DEBUFF);
        condition3.setConditionTierType(ConditionTierType.ADVANCED);
        
        Conditions condition4 = new Conditions("Tired");
        condition4.getStatModifiers(StatType.PHYSICALDEFENSE).setGlobalMult(getSkillMultiplier().get("XH").getResult()*(-1));
        condition4.getStatModifiers(StatType.MAGICALDEFENSE).setGlobalMult(getSkillMultiplier().get("XH").getResult()*(-1));

        condition4.setConditionType(ConditionType.DEBUFF);
        condition4.setConditionTierType(ConditionTierType.ADVANCED);

        Conditions condition5 = new Conditions("Careless");
        condition5.getStatusModifiers(StatusType.AGILITY).setGlobalMult(getSkillMultiplier().get("XI").getResult()*(-1));

        condition5.setConditionType(ConditionType.DEBUFF);
        condition5.setConditionTierType(ConditionTierType.ADVANCED);

        //remove and re-add to database
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(condition.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(condition.getName(), condition);

        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(condition2.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(condition2.getName(), condition2);

        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(condition3.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(condition3.getName(), condition3);

        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(condition4.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(condition4.getName(), condition4);

        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(condition5.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(condition5.getName(), condition5);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
            ConditionManager.reapplyCondition(condition2, unit);
            ConditionManager.reapplyCondition(condition3, unit);
            ConditionManager.reapplyCondition(condition4, unit);
            ConditionManager.reapplyCondition(condition5, unit);
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

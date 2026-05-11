package model.entity.skills.list.player;

import main.controller.CombatFlow;
import main.controller.event.EventBus;
import main.controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

import java.util.List;

public class True_Essence_Regalia extends Skill implements SkillWithCondition {

    public static String NAME = "True Essence : Regalia";

    public True_Essence_Regalia() {
        super();
        setDescription("ใช้งานได้ในระหว่างผลอาฟเตอร์ช็อคของ Nebula แล้วเท่านั้น\n" +
                "การทำงานจะถูกแบ่งออกเป็นสามช่วงหลัก จะผ่านไปหนึ่งช่วงเมื่อผ่านไปหนึ่งรอบเทิร์น\n" +
                "\n" +
                "ช่วงที่หนึ่ง : [Righteous Throne]\n" +
                "เปลี่ยนร่างเป็น \"จักรพรรดินีทมิฬกาฬ\" ได้รับ Debris XA หน่วย\n" +
                "เปลี่ยนแปลง Divergent ทั้งหมดให้กลายเป็น Regalia Divergent และเลือกมอบให้กับพันธมิตรห้ายูนิต ไม่เกินหนึ่งคนต่อหนึ่งชนิด โดยส่งผลดังนี้\n" +
                "Regalia Maelstorm เพิ่ม PATK และ STR XB \n" +
                "Regalia Icarus เพิ่ม RATK และ DEX XB\n" +
                "Regalia Geneva เพิ่ม MATK และ INT XB\n" +
                "Regalia Revenos เพิ่ม CRITDMG และ MSPD XB \n" +
                "Regalia Titan เพิ่ม DEF และ VIT XB และสามารถใช้โจมตีได้ โล่จะคิดความเสียหายจากค่า DEF ของผู้ถือครอง\n" +
                "ผู้ถือครองอาวุธทั้งหมดจะได้รับ MSPD อีก XC ด้วย\n" +
                "หากมีจำนวนพันธมิตรที่มอบ Regalia Divergent ให้ได้น้อยกว่าห้ายูนิต จะสร้างร่างแยกของสการ์เล็ตขึ้นมาถือครองอาวุธที่เหลืออยู่แทน\n" +
                "หากสการ์เล็ตมอบ Regalia Divergent ให้กับยูนิตที่หมดสติอยู่ ปลุกยูนิตเป้าหมายขึ้นมาด้วยพลังชีวิต 1 หน่วยและสร้าง Debris ให้เป็นครึ่งหนึ่งของพลังชีวิตสูงสุดของผู้ถือครอง\n" +
                "ระหว่างนี้สการ์เล็ตจะไม่สามารถใช้งาน Divergent ได้\n" +
                "\n" +
                "ช่วงที่สอง : [Calamity Embrace]\n" +
                "คทาแห่งสมบัติล้ำค่า Regalia ถูกสร้างขึ้นกลางอากาศแต่ยังไม่เสร็จสิ้น หากมันถูกทำลายในขณะนี้ ในรอบเทิร์นหน้า จะเข้าสู่ช่วงที่สองซ้ำอีกครั้ง\n" +
                "สการ์เล็ตจะไม่สามารถเคลื่อนที่หรือจู่โจมได้\n" +
                "ในช่วงนี้ เมื่อผู้ถือครอง Regalia Divergent จู่โจม พวกเขาจะได้รับ Debris เป็น 15% ของพลังชีวิตสูงสุด\n" +
                "\n" +
                "ช่วงที่สาม : [Evernight Starfall]\n" +
                "คทาแห่งสมบัติล้ำค่า Regalia ถูกสร้างจนเสร็จสิ้น สการ์เล็ตร่ายพลังจากดวงดาราสีชาด พุ่งตรงใส่เป้าหมาย สร้างความเสียหายโดยตรง XD หน่วยในรัศมีหนึ่งเมตร\n" +
                "การจู่โจมนี้ไม่สามารถถูกหลบหรือบล็อกได้\n" +
                "สิ้นสุดผลอื่นๆของสกิลนี้ทั้งหมด");
        setActionType("Turn");
        setManaCost(12);
        setCooldown(7);
        getSkillMultiplier().put("XA",new SkillMultiplier("3*UsableHP"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.DEFENSE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.35*(1+BuffAMP)"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XB").setPercent(true);

        getSkillMultiplier().put("XC",new SkillMultiplier("1*(1+BuffAMP)"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XC").setPercent(true);

        getSkillMultiplier().put("XD",new SkillMultiplier("6.7*MATK"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XD").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XD").getTags().add(SkillType.AOE);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        List<String> choices = List.of("Phase 1", "Phase 2", "Phase 3");
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser(), choices
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.CUSTOM, 0)
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 1)
        );
        spec    .addFields(
                new SkillInputSpec.InputField<String>("Regalia", SkillInputSpec.InputType.SELECT, 0)
                        .options(List.of("Maelstorm","Icarus","Geneva","Revenos", "Titan"), 0)
                        .labelProvider(String::toString, 0)
        , 1, 0);
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
        int xa = (int) getSkillMultiplier().get("XA").getResult();
        int xd = (int) getSkillMultiplier().get("XD").getResult();
            if (skillTarget.getTarget(0).contains("Phase 1")) {

                for (String name : skillTarget.getTarget(1)) {
                    Unit target = combatFlow.findUnit(name);

                    Conditions condition = new Conditions();
                    if (skillTarget.getDecision(name, 1, 0).contains("Maelstorm")) {
                        condition = combatFlow.findCondition("Regalia Maelstorm");
                    }
                    if (skillTarget.getDecision(name, 1, 0).contains("Icarus")) {
                        condition = combatFlow.findCondition("Regalia Icarus");
                    }
                    if (skillTarget.getDecision(name, 1, 0).contains("Geneva")) {
                        condition = combatFlow.findCondition("Regalia Geneva");
                    }
                    if (skillTarget.getDecision(name, 1, 0).contains("Revenos")) {
                        condition = combatFlow.findCondition("Regalia Revenos");
                    }
                    if (skillTarget.getDecision(name, 1, 0).contains("Titan")) {
                        condition = combatFlow.findCondition("Regalia Titan");
                    }
                    ActionEvent actionEvent = ActionEvent.builder(getName(), getUser(), target)
                            .condition(condition, 20)
                            .addActType(ActType.CONDITION_GIVEN, ActType.CAST)
                            .build();
                    if (target.getHealth().getRemaining() <= 0) {
                        actionEvent.doCreateDebris(target.getHealth().getUsable() * 0.5);
                        actionEvent.doHeal(1);
                        actionEvent.act_type.add(ActType.HEALTH_RECOVER);
                        actionEvent.act_type.add(ActType.CREATE_DEBRIS);
                    }
                    sendActionEvent(combatFlow.getEventBus(), actionEvent);
                }

                Conditions condition2 = new Conditions("True Essence : Regalia [Phase 1]");
                condition2.setDescription("เริ่มต้นการสร้างสมบัติล้ำค่าเรกาเลีย");
                condition2.setConditionType(ConditionType.NEUTRAL);
                condition2.setConditionTierType(ConditionTierType.BOUND);
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), getUser())
                                .effect(ActionEffectType.CREATE_DEBRIS, xa, 1)
                                .condition(condition2, 20)
                                .addActType(ActType.CONDITION_GIVEN, ActType.CAST, ActType.CREATE_DEBRIS)
                                .build()
                );
            }

            if (skillTarget.getTarget(0).contains("Phase 2")) {
                ConditionManager.removeCondition(getUser(), "True Essence : Regalia [Phase 1]");
                Conditions condition = new Conditions("True Essence : Regalia [Phase 2]");
                condition.setDescription("กำลังสร้างสมบัติล้ำค่าเรกาเลีย");
                condition.setConditionType(ConditionType.NEUTRAL);
                condition.setConditionTierType(ConditionTierType.BOUND);

                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), getUser())
                                .condition(condition, 20)
                                .addActType(ActType.CONDITION_GIVEN, ActType.CAST)
                                .build()
                );
            }


            if (skillTarget.getTarget(0).contains("Phase 3")) {
                ConditionManager.removeCondition(getUser(), "True Essence : Regalia [Phase 2]");
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(1)))
                                .effect(ActionEffectType.DAMAGE_PURE, xd, 1)
                                .addActType(ActType.STRIKE, ActType.CAST)
                                .build()
                );
            }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        double xb = getSkillMultiplier().get("XB").getResult();
        double xc = getSkillMultiplier().get("XC").getResult();
        Conditions condition = new Conditions("Regalia Maelstorm");
        condition.getStatModifiers(StatType.PHYSICALATTACK).setGlobalMult(xb);
        condition.getStatModifiers(StatType.MOVEMENTSPEED).setGlobalMult(xc);
        condition.getStatusModifiers(StatusType.STRENGTH).setGlobalMult(xb);
        condition.setConditionType(ConditionType.NEUTRAL);
        condition.setConditionTierType(ConditionTierType.BOUND);

        Conditions condition2 = new Conditions("Regalia Icarus");
        condition2.getStatModifiers(StatType.RANGEDATTACK).setGlobalMult(xb);
        condition2.getStatModifiers(StatType.MOVEMENTSPEED).setGlobalMult(xc);
        condition2.getStatusModifiers(StatusType.DEXTERITY).setGlobalMult(xb);
        condition2.setConditionType(ConditionType.NEUTRAL);
        condition2.setConditionTierType(ConditionTierType.BOUND);

        Conditions condition3 = new Conditions("Regalia Geneva");
        condition3.getStatModifiers(StatType.MAGICALATTACK).setGlobalMult(xb);
        condition3.getStatModifiers(StatType.MOVEMENTSPEED).setGlobalMult(xc);
        condition3.getStatusModifiers(StatusType.INTELLIGENCE).setGlobalMult(xb);
        condition3.setConditionType(ConditionType.NEUTRAL);
        condition3.setConditionTierType(ConditionTierType.BOUND);

        Conditions condition4 = new Conditions("Regalia Revenos");
        condition4.getStatModifiers(StatType.CRITDAMAGE).setGlobalMult(xb);
        condition4.getStatModifiers(StatType.MOVEMENTSPEED).setGlobalMult((1+xc)*(1+xb));
        condition4.setConditionType(ConditionType.NEUTRAL);
        condition4.setConditionTierType(ConditionTierType.BOUND);

        Conditions condition5 = new Conditions("Regalia Titan");
        condition5.getStatModifiers(StatType.PHYSICALDEFENSE).setGlobalMult(xb);
        condition5.getStatModifiers(StatType.MAGICALDEFENSE).setGlobalMult(xb);
        condition5.getStatModifiers(StatType.MOVEMENTSPEED).setGlobalMult(xc);
        condition5.getStatusModifiers(StatusType.VITALITY).setGlobalMult(xb);
        condition5.setConditionType(ConditionType.NEUTRAL);
        condition5.setConditionTierType(ConditionTierType.BOUND);

        //remove and re-add to database
        addConditionToDatabase(condition, combatFlow);
        addConditionToDatabase(condition2, combatFlow);
        addConditionToDatabase(condition3, combatFlow);
        addConditionToDatabase(condition4, combatFlow);
        addConditionToDatabase(condition5, combatFlow);

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
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ActionEvent.class, EventPhase.POST, 0, (ActionEvent event) -> {
            if (!event.hasActType(ActType.STRIKE)) return;
            if (event.hasActType(ActType.SKILL_TRIGGER)) return;
            if (!getUser().hasCondition("True Essence : Regalia [Phase 2]")) return;
            if (event.unit_source.hasCondition("Regalia Maelstorm") ||
                    event.unit_source.hasCondition("Regalia Icarus") ||
                    event.unit_source.hasCondition("Regalia Geneva") ||
                    event.unit_source.hasCondition("Regalia Revenos") ||
                    event.unit_source.hasCondition("Regalia Titan")) {
                double max_health = event.unit_source.getHealth().getUsable();

                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), event.unit_source)
                                .effect(ActionEffectType.CREATE_DEBRIS,0.15 * max_health, 1)
                                .addActType(ActType.CREATE_DEBRIS, ActType.SKILL_TRIGGER)
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

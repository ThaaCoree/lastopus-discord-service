package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.EventBus;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.ConditionInstance;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;
import util.LogWriterUtil;

import java.util.ArrayList;
import java.util.List;

public class The_Demon_Is_Me extends Skill implements SkillWithCondition {

    public static String NAME = "The Demon is Me";

    public The_Demon_Is_Me() {
        super();
        setDescription("ครอบครอง Counter [Despair] และ [Rapid Slash]\n" +
                "เมื่อมอบดีบัพ, Bleed, Poison หรือ Ignite จะได้รับ Despair 1 สแต็ค\n" +
                "เมื่อโจมตี จะได้รับ Rapid Slash 1 แสต็ค\n" +
                "ใช้งานได้เมื่อมี Despair อย่างน้อย XA สแต็ค, มี Illusion And Dream อย่างน้อย XB สแต็ค, มี Rapid Slash อย่างน้อย XC สแต็ค, มีมานาเต็ม และเมื่อกำลังเป็นเทิร์นสุดท้ายของรอบเทิร์นอยู่เท่านั้น\n" +
                "เมื่อใช้งาน ผลของสกิลจะเริ่มทำงานในเทิร์นแรกของรอบถัดไป กางหมอกทั่วทั้งสนาม ยูนิตทั้งหมดที่อยู่ในหมอกจะได้รับสถานะ Darkness In Mind เป็นเวลา XD รอบเทิร์น ซึ่งทำให้การใช้งาน Action จะต้องจ่าย Combined Action เพิ่มหนึ่งหน่วยเสมอ\n" +
                "\n" +
                "จากนั้นเลือกอาวุธหนึ่งอย่างที่กำลังสวมใส่หรืออยู่ใน Inventory เปลี่ยนเป็นดาบคาตานะยาวจนกว่าจะจบรอบเทิร์นนี้และสั่งใช้งานพาสซีฟอาวุธดาบยาวทันที การโจมตีจะสร้างคลื่นดาบไปข้างหน้าจนสุดแผนที่ ทะลุผ่านยูนิตทุกตัว\n" +
                "\n" +
                "ผู้ใช้ได้รับ\n" +
                "Demonspeed : เพิ่ม AttackSPD XE, ต้านทานผลของการลด Speed และ AGI จากสถานะทั้งหมด\n" +
                "Dead Calling : ไม่รับความเสียหายย้อนกลับจากการโจมตีผนวกความเร็ว\n" +
                "Silentstriker : เพิ่ม CritDamage XF ,การโจมตีจากจุดบอดหรือสเตลท์จะเป็นคริติคอลเสมอ หากโจมตีไม่เป็นจุดบอดหรือสเตลท์ จะสร้างความเสียหายจริง 1 หน่วยแทน\n" +
                "ในรอบเทิร์นสามารถสั่งเปิดใช้งานการโจมตีครั้งสุดท้ายได้ เมื่อเปิดใช้งานการโจมตีครั้งสุดท้าย ลบล้างสถานะ Demonspeed และ Silentstriker จากนั้นได้รับ Last Strike\n" +
                "Last Strike : การจู่โจมครั้งถัดไปสร้างความเสียหายเพิ่มอีก XG เท่า เพิ่ม AttackSPD XH การจู่โจมจะเป็นคริติคอลเสมอ\n" +
                "หากการโจมตีในชุดถัดไปแบ่งออกเป็นหลายครั้ง รวมความเสียหายทั้งหมดเป็นครั้งเดียว\n" +
                "หลังจากจู่โจมครั้งถัดไปเสร็จสิ้น ลบสถานะ Dead Calling และ Last Strike ออกจากผู้ใช้และทำให้หมดสภาพต่อสู้");
        setActionType("Hold Action");
        setManaCost(0);
        setCooldown(11);
        getSkillMultiplier().put("XA",new SkillMultiplier("20"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.REQUIREMENT);

        getSkillMultiplier().put("XB",new SkillMultiplier("10"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.REQUIREMENT);

        getSkillMultiplier().put("XC",new SkillMultiplier("25"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.REQUIREMENT);

        getSkillMultiplier().put("XD",new SkillMultiplier("1"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XE",new SkillMultiplier("4*(1+BuffAMP)"));
        getSkillMultiplier().get("XE").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XE").setPercent(true);

        getSkillMultiplier().put("XF",new SkillMultiplier("5*(1+BuffAMP)"));
        getSkillMultiplier().get("XF").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XF").getTags().add(SkillType.CRITICAL);
        getSkillMultiplier().get("XF").setPercent(true);

        getSkillMultiplier().put("XG",new SkillMultiplier("12"));
        getSkillMultiplier().get("XG").getTags().add(SkillType.LIMIT);

        getSkillMultiplier().put("XH",new SkillMultiplier("4*(1+BuffAMP)"));
        getSkillMultiplier().get("XH").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XH").setPercent(true);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        List<String> choices = List.of("Activation", "Silentstriker", "Last Strike");
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser(), choices
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.CUSTOM, 0)
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 1)
        );
        spec    .addFields(
                new SkillInputSpec.InputField<String>("Silentstriker [Stealth]", SkillInputSpec.InputType.BOOLEAN, 0)
                , 1, 0);
//                .addFields(
//                        new SkillInputSpec.InputField<String>("Damage", SkillInputSpec.InputType.NUMBER,1)
//                , 0, 1);
        return spec;
    }

    @Override
    public void calculateExtra() {
        double mana = getUser().getMana().getRemaining();
        setManaCost(mana-1);

        if (getUser().hasCondition("Demonspeed")) {
            for (ConditionInstance instance : getUser().getConditionInstances().values()) {
                Conditions condition = instance.getCondition();
                if (condition.getName().equals("Demonspeed")) continue;

                if (condition.getStatusModifiers(StatusType.AGILITY) != null) {
                    if (condition.getStatusModifiers(StatusType.AGILITY).getGlobalMult() < 0) {
                        double global = condition.getStatusModifiers(StatusType.AGILITY).getGlobalMult();
                        double flat = condition.getStatusModifiers(StatusType.AGILITY).getFlat();
                        getUser().findCondition("Demonspeed").getCondition().getStatusModifiers(StatusType.AGILITY).setGlobalMult(global*-1);
                        getUser().findCondition("Demonspeed").getCondition().getStatusModifiers(StatusType.AGILITY).setFlat(flat*-1);
                    }
                }
                if (condition.getStatModifiers(StatType.SPEED) != null) {
                    if (condition.getStatModifiers(StatType.SPEED).getGlobalMult() < 0) {
                        double global = condition.getStatModifiers(StatType.SPEED).getGlobalMult();
                        double flat = condition.getStatModifiers(StatType.SPEED).getFlat();
                        getUser().findCondition("Demonspeed").getCondition().getStatModifiers(StatType.SPEED).setGlobalMult(global*-1);
                        getUser().findCondition("Demonspeed").getCondition().getStatModifiers(StatType.SPEED).setFlat(flat*-1);
                    }
                }
            }
        }

        addCounter(CounterName.IllusionAndDream);
        addCounter(CounterName.DESPAIR);
        addCounter(CounterName.RAPID_SLASH);
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        double xa = getSkillMultiplier().get("XA").getResult();
        double xb = getSkillMultiplier().get("XB").getResult();
        double xc = getSkillMultiplier().get("XC").getResult();
        double xd = getSkillMultiplier().get("XD").getResult();
        double xg = getSkillMultiplier().get("XG").getResult();
        double patk = getUser().getStats().get(StatType.PHYSICALATTACK).getFinal();

        double despair = getUser().getCounter().get(CounterName.DESPAIR);
        double iandd = getUser().getCounter().get(CounterName.IllusionAndDream);
        double rapid_slash = getUser().getCounter().get(CounterName.RAPID_SLASH);

//        double max_mana = getUser().getMana().getUsable();
//        double remaining_mana = getUser().getMana().getRemaining();

        if (skillTarget.getTarget(0).contains("Activation")) {
            if (despair < xa) return;
            if (iandd < xb) return;
            if (rapid_slash < xc) return;

            Conditions condition = combatFlow.findCondition("Demonspeed");
            Conditions condition2 = combatFlow.findCondition("Silentstriker");
            Conditions condition3 = combatFlow.findCondition("Dead Calling");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(),getUser(), getUser())
                            .condition(condition, 1)
                            .condition(condition2, 1)
                            .condition(condition3, 1)
                            .addActType(ActType.CONDITION_GIVEN)
                            .build());

            List<Unit> allOtherUnits = new ArrayList<>(combatFlow.getAllUnit().values());

            Conditions darkness_in_mind = new Conditions("Darkness In Mind");
            darkness_in_mind.setDescription("การใช้งาน Action จะต้องจ่าย Combined Action เพิ่มหนึ่งหน่วยด้วย");

            darkness_in_mind.setConditionType(ConditionType.DEBUFF);
            darkness_in_mind.setConditionTierType(ConditionTierType.ADVANCED);

            allOtherUnits.remove(getUser());
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(),getUser(), allOtherUnits)
                            .condition(darkness_in_mind, (int) xd)
                            .addActType(ActType.CONDITION_GIVEN)
                            .build());

            
        }


        if (skillTarget.getTarget(0).contains("Silentstriker")) {
            for (String name : skillTarget.getTarget(1)) {
                Unit target = combatFlow.findUnit(name);
                if (skillTarget.getDecision(name, 1,0).contains("TRUE")) {
                    ActionEvent actionEvent = ActionEvent.builder(getName()+" Silentstriker",getUser(), target)
                            .effect(ActionEffectType.DAMAGE_PHYSICAL, patk, 1)
                            .addActType(ActType.STRIKE, ActType.ATTACK)
                            .build();

                    sendActionEvent(combatFlow.getEventBus(), actionEvent);
                } else {
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(),getUser(), target)
                                    .effect(ActionEffectType.DAMAGE_TRUE, 1, 1)
                                    .addActType(ActType.STRIKE, ActType.ATTACK)
                                    .build());
                }
            }
        }

        if (skillTarget.getTarget(0).contains("Last Strike")) {
            ConditionManager.removeCondition(getUser(), "Demonspeed");
            ConditionManager.removeCondition(getUser(), "Silentstriker");

            Conditions condition = combatFlow.findCondition("Last Strike");
            ConditionManager.applyCondition(condition, getUser(), getUser(), 1);

            for (String name : skillTarget.getTarget(1)) {
                double attack_speed = getUser().getStats().get(StatType.ATTACKSPEED).getFinal();

                Unit target = combatFlow.findUnit(name);

                ActionEvent actionEvent = ActionEvent.builder(getName(),getUser(), target)
                        .effect(ActionEffectType.DAMAGE_PHYSICAL, patk * (int) attack_speed * xg, 1)
                        .addActType(ActType.STRIKE, ActType.ATTACK)
                        .build();

                actionEvent.makeAllDamageCritical();
                sendActionEvent(combatFlow.getEventBus(), actionEvent);

                ConditionManager.removeCondition(getUser(), "Dead Calling");
                ConditionManager.removeCondition(getUser(), "Last Strike");
                getUser().getHealth().setRemaining(0);
                LogWriterUtil.log(getUser().getName()+" fainted to Last Strike's effect");
            }
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Demonspeed");
        condition.getStatModifiers(StatType.ATTACKSPEED).setGlobalMult(getSkillMultiplier().get("XE").getResult());

        condition.setConditionType(ConditionType.NEUTRAL);
        condition.setConditionTierType(ConditionTierType.UNDISPELLABLE);

        Conditions condition2 = new Conditions("Silentstriker");
        condition2.getStatModifiers(StatType.CRITDAMAGE).setGlobalMult(getSkillMultiplier().get("XF").getResult());

        condition2.setConditionType(ConditionType.NEUTRAL);
        condition2.setConditionTierType(ConditionTierType.UNDISPELLABLE);

        Conditions condition3 = new Conditions("Dead Calling");
        condition3.setDescription("ไม่รับความเสียหายย้อนกลับจากการโจมตีผนวกความเร็ว");

        condition3.setConditionType(ConditionType.NEUTRAL);
        condition3.setConditionTierType(ConditionTierType.UNDISPELLABLE);

        Conditions condition4 = new Conditions("Last Strike");
        condition4.getStatModifiers(StatType.ATTACKSPEED).setGlobalMult(getSkillMultiplier().get("XH").getResult());

        condition4.setConditionType(ConditionType.NEUTRAL);
        condition4.setConditionTierType(ConditionTierType.UNDISPELLABLE);

        //remove and re-add to database
        combatFlow.getDatabase().getAllConditionMap().put(condition.getName(), condition);
        combatFlow.getDatabase().getAllConditionMap().put(condition2.getName(), condition2);
        combatFlow.getDatabase().getAllConditionMap().put(condition3.getName(), condition3);
        combatFlow.getDatabase().getAllConditionMap().put(condition4.getName(), condition4);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
            ConditionManager.reapplyCondition(condition2, unit);
            ConditionManager.reapplyCondition(condition3, unit);
            ConditionManager.reapplyCondition(condition4, unit);
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ActionEvent.class, EventPhase.MODIFY, 0, (ActionEvent event) -> {
            if (!event.hasActType(ActType.STRIKE) || event.unit_source != getUser()) return;
            if (event.event_source.equals(getName()+" Silentstriker") || event.event_source.equals(getName()+" Last Strike")) {
                event.makeAllDamageCritical();
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

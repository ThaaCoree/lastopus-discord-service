package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import controller.event.events.RoundEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;
import util.LogWriterUtil;

import java.util.List;

public class Nebula extends Skill implements SkillWithCondition {

    public static String NAME = "Nebula";

    public Nebula() {
        super();
        setDescription("ออร่ามงกุฎสีเขียวสว่าง ปรากฎบนศีรษะของสการ์เล็ต เปลี่ยนแปลงแขนข้างถนัดของเธอให้กลายเป็นหัตถ์ปีศาจ เสริมพลังให้กับอาวุธของ Sanctum of Sorrow เปลี่ยน เปลี่ยน Divergent ให้กลายเป็น Nebula Divergent และสกิลจะใช้ Combine Action แทน Action ปกติ \n" +
                "ลดมานาของ Scarlet เหลือ 0 และ สร้างความเสียหายจริง 25% จาก MAX HP ให้กับตนเองหรือยูนิตพันธมิตรที่ภักดี เพื่อใช้งานสกิลนี้ ร่างนี้คงอยู่ XA เทิร์น แต่ละเทิร์นจะทำความเสียหาย 10% จาก MAX HP รับความเสียหายนี้ได้มากที่สุดจนพลังชีวิตเหลือ 1 \n" +
                "ร่างนี้จะสามารถเคลื่อนที่เฉกเช่นเมื่อใช้งาน Fatima และไม่สามารถรับการฟื้นฟูจากยูนิตอื่นได้ \n" +
                "หากไม่ใช้สกิล หัตถ์ปีศาจสามารถใช้โจมตีได้ ความเสียหายคิดจาก MATK และเผาเป้าหมาย 1 เทิร์น \n" +
                "Nebula Maelstorm ( ดาบใหญ่คู่ ) : ปลดปล่อยออร่าแห่งความเดือดดาล เพิ่ม PATK XB การโจมตีจะเปลี่ยนเป็นกรวยภายใน 5 เมตรด้านหน้า สร้างความเสียหาย 3 เป้าหมายที่อยู่ภายในกรวย หากสร้างความเสียหายสำเร็จ ลดความเสียหายที่ได้รับครั้งต่อไป 50% \n" +
                "Nebula Icarus ( ธนูยักษ์ ) : ง้างธนูแห่งความหยิ่งผยอง แห่งเอาไว้ 1 เทิร์น แต่สามารถใช้ Reaction เพื่อโจมตีในเทิร์นไหนก็ได้ อีกทั้งยังสามารถปล่อยตัวธนูให้ง้างเองได้ในตำแหน่งที่ใช้งานสกิล ธนูจะยิงออกไปเป็นเส้นตรง และ ทะลุทุกเป้าหมาย ในระยะทาง 10 เมตร เพิ่มค่า Accuracy และ CritDMG XC ศัตรูที่ได้รับความเสียหายนี้จะไม่ได้รับฮีลเป็นเวลา XD เทิร์น\n" +
                "Nebula Geneva ( คฑาวิญญาณ ) : โซ่แห่งตัณหา รวมกันกลายเป็นกลุ่มก้อนพลัง เมื่อยิงใส่พื้นที่ จะทำความเสียหายเวทในรัศมี 3 เมตร XE อีกทั้งยังตรึงทุกเป้าหมายในพื้นที่นั้นเป็นเวลา 1 เทิร์น\n" +
                "Nebula Revenos ( จักรรามยักษ์ ) : เพิ่ม MoveSPD XF ATKSPD XG LUK XH และ MATK XI หน่วย พุ่งไปพร้อมกับจักรรามแห่งความละโมบใส่เป้าหมายภายในระยะ 5 เมตร หากโจมตีล้มเหลว จะถอยกลับมาที่จุดเดิมโดยอัตโนมัติ \n" +
                "Nebula Titan ( โล่ไร้รูปร่าง ) : เพิ่ม PDEF และ MDEF XJ เรียกโล่แห่งความเพิกเฉยขึ้นมาในระยะมองเห็น โล่จะคงอยู่ ในอากาศอีก 1 เทิร์น สามารถใช้ Reaction ร่วมกับ Combined Action ในการย้ายตำแหน่งได้ บล็อกความเสียหายได้อย่างแน่นอน \n" +
                "เมื่อร่าง Nebula สิ้นสุด จะไม่สามารถโจมตี ใช้งานสกิล และเคลื่อนที่ได้เป็นเวลา XK เทิร์น ( ออก Reaction กับใช้ไอเทมได้เท่านั้น )");
        setActionType("Action");
        setCooldown(7);
        setHealthReservePercent(0.2);
        getSkillMultiplier().put("XA",new SkillMultiplier("3"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XB",new SkillMultiplier("3.3*INT"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XB").getTags().add(SkillType.AOE);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.75*(1+BuffAMP)"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XC").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XC").setPercent(true);

        getSkillMultiplier().put("XD",new SkillMultiplier("2"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.DURATION);

        getSkillMultiplier().put("XE",new SkillMultiplier("(4*WIS)+(0.9*MATK)"));
        getSkillMultiplier().get("XE").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XE").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XE").getTags().add(SkillType.AOE);

        getSkillMultiplier().put("XF",new SkillMultiplier("0.008*INT*(1+BuffAMP)"));
        getSkillMultiplier().get("XF").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XF").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XF").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XF").setPercent(true);

        getSkillMultiplier().put("XG",new SkillMultiplier("0.25*(1+BuffAMP)"));
        getSkillMultiplier().get("XG").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XG").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XG").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XG").setPercent(true);

        getSkillMultiplier().put("XH",new SkillMultiplier("0.04*INT*(1+BuffAMP)"));
        getSkillMultiplier().get("XH").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XH").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XH").getTags().add(SkillType.SCALING);

        getSkillMultiplier().put("XI",new SkillMultiplier("0.25*INT"));
        getSkillMultiplier().get("XI").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XI").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XI").getTags().add(SkillType.SCALING);

        getSkillMultiplier().put("XJ",new SkillMultiplier("1.4*INT*(1+BuffAMP)"));
        getSkillMultiplier().get("XJ").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XJ").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XJ").getTags().add(SkillType.SCALING);

        getSkillMultiplier().put("XK",new SkillMultiplier("2"));
        getSkillMultiplier().get("XK").getTags().add(SkillType.DRAWBACK);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        List<String> nebula_choices = List.of("Start Nebula", "Nebula Maelstorm", "Nebula Icarus", "Nebula Geneva", "Nebula Revenos", "Nebula Titan");
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser(), nebula_choices
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.CUSTOM, 0)
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 1)
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
        if (skillTarget.getTarget(0).contains("Start Nebula")) {
            int duration = (int) getSkillMultiplier().get("XA").getResult();
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Nebula");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), getUser())
                            .condition(condition, duration)
                            .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                            .build());
            getUser().setRemainingMana(0);
            double beforeHealth = getUser().getHealth().getRemaining();
            if (beforeHealth <= 0) {
                LogWriterUtil.log("Nebula is casted, Health cannot be lost, Scarlet's health is zero", combatFlow.getTurnCount());
                return;
            }
            double toReduce = getUser().getHealth().getUsable()*0.25;
            getUser().sumRemainingHealth(toReduce*(-1));
            double afterHealth = getUser().getHealth().getRemaining();
            LogWriterUtil.log("Scarlet lost "+toReduce+" health ("+beforeHealth+" > "+afterHealth+") due to Nebula", combatFlow.getTurnCount());
            if (afterHealth <= 0) {
                LogWriterUtil.log("Nebula is casted, Scarlet's health cannot be lowered than 1 this way", combatFlow.getTurnCount());
                getUser().setRemainingHealth(1);
            }
        }

        double xe = getSkillMultiplier().get("XE").getResult();
        double matk = getUser().getStats().get(StatType.MAGICALATTACK).getFinal();

        if (skillTarget.getTarget(0).contains("Nebula Maelstorm")) {
            int duration = 1;
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Nebula Maelstorm");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(),getUser(), getUser())
                            .condition(condition, duration)
                            .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                            .build());
        }

        if (skillTarget.getTarget(0).contains("Nebula Icarus")) {
            int duration = 1;
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Nebula Icarus");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(),getUser(), getUser())
                            .condition(condition, duration)
                            .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                            .build());

            for (String name : skillTarget.getTarget(1)) {
                Unit target = combatFlow.findUnit(name);

                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), target)
                                .effect(ActionEffectType.DAMAGE_MAGICAL, matk, 1)
                                .addActType(ActType.CAST, ActType.STRIKE, ActType.ATTACK)
                                .build()
                );
            }
        }

        if (skillTarget.getTarget(0).contains("Nebula Geneva")) {
            for (String name : skillTarget.getTarget(1)) {
                Unit target = combatFlow.findUnit(name);

                int duration = 1;
                Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Root");
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), target)
                                .effect(ActionEffectType.DAMAGE_MAGICAL, xe, 1)
                                .addActType(ActType.CAST, ActType.STRIKE)
                                .condition(condition, duration)
                                .build()
                );
            }
        }

        if (skillTarget.getTarget(0).contains("Nebula Revenos")) {
            int duration = 1;
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Nebula Revenos");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(),getUser(), getUser())
                            .condition(condition, duration)
                            .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                            .build());

            for (String name : skillTarget.getTarget(1)) {
                Unit target = combatFlow.findUnit(name);

                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), target)
                                .effect(ActionEffectType.DAMAGE_MAGICAL, matk, 1)
                                .addActType(ActType.CAST, ActType.STRIKE, ActType.ATTACK)
                                .build()
                );
            }
        }

        if (skillTarget.getTarget(0).contains("Nebula Titan")) {
            int duration = 1;
            Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Nebula Titan");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(),getUser(), getUser())
                            .condition(condition, duration)
                            .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                            .build());
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        double buffAMP = 1+getUser().getStats().get(StatType.BUFFAMPLIFIER).getFinal();
        double debuffAMP = 1+getUser().getStats().get(StatType.DEBUFFAMPLIFIER).getFinal();
        Conditions nebula = new Conditions("Nebula");
        nebula.setConditionType(ConditionType.NEUTRAL);
        nebula.setConditionTierType(ConditionTierType.UNDISPELLABLE);
        nebula.setDescription("กำลังรับผลของ Nebula");

        Conditions maelstorm = new Conditions("Nebula Maelstorm");
        maelstorm.getStatModifiers(StatType.PHYSICALATTACK).setGlobalMult(getSkillMultiplier().get("XB").getResult());
        maelstorm.setConditionType(ConditionType.BUFF);
        maelstorm.setConditionTierType(ConditionTierType.ADVANCED);

        Conditions icarus = new Conditions("Nebula Icarus");
        icarus.getStatModifiers(StatType.ACCURACY).setGlobalMult(getSkillMultiplier().get("XC").getResult());
        icarus.getStatModifiers(StatType.CRITDAMAGE).setGlobalMult(getSkillMultiplier().get("XC").getResult());
        icarus.setConditionType(ConditionType.BUFF);
        icarus.setConditionTierType(ConditionTierType.ADVANCED);

        Conditions icarusWound = new Conditions("Nebula Icarus Wound");
        icarusWound.setConditionType(ConditionType.DEBUFF);
        icarusWound.setConditionTierType(ConditionTierType.ADVANCED);
        icarusWound.setDescription("ไม่สามารถรับฮีลได้ในระหว่างผลของสกิลนี้");

        Conditions revenos = new Conditions("Nebula Revenos");
        revenos.getStatModifiers(StatType.MOVEMENTSPEED).setGlobalMult(getSkillMultiplier().get("XF").getResult());
        revenos.getStatModifiers(StatType.ATTACKSPEED).setGlobalMult(getSkillMultiplier().get("XG").getResult());
        revenos.getStatusModifiers(StatusType.LUCK).setFlat(getSkillMultiplier().get("XH").getResult());
        revenos.getStatModifiers(StatType.MAGICALATTACK).setFlat(getSkillMultiplier().get("XI").getResult());
        revenos.setConditionType(ConditionType.BUFF);
        revenos.setConditionTierType(ConditionTierType.ADVANCED);

        Conditions titan = new Conditions("Nebula Titan");
        titan.getStatModifiers(StatType.PHYSICALDEFENSE).setFlat(getSkillMultiplier().get("XJ").getResult());
        titan.getStatModifiers(StatType.MAGICALDEFENSE).setFlat(getSkillMultiplier().get("XJ").getResult());
        titan.setConditionType(ConditionType.BUFF);
        titan.setConditionTierType(ConditionTierType.ADVANCED);

        //remove and re-add to database
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(nebula.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(nebula.getName(), nebula);
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(maelstorm.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(maelstorm.getName(), maelstorm);
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(icarus.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(icarus.getName(), icarus);
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(icarusWound.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(icarusWound.getName(), icarusWound);
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(revenos.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(revenos.getName(), revenos);
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(titan.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(titan.getName(), titan);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(nebula, unit);
            ConditionManager.reapplyCondition(maelstorm, unit);
            ConditionManager.reapplyCondition(icarus, unit);
            ConditionManager.reapplyCondition(icarusWound, unit);
            ConditionManager.reapplyCondition(revenos, unit);
            ConditionManager.reapplyCondition(titan, unit);
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        combatFlow.getEventBus().register(RoundEvent.class, EventPhase.POST, 0, event -> {
            if (getUser().hasCondition("Nebula")) {
                getUser().setRemainingMana(0);
                double beforeHealth = getUser().getHealth().getRemaining();
                if (beforeHealth <= 0) {
                    LogWriterUtil.log("Health cannot be lost, Scarlet's health is zero", combatFlow.getTurnCount());
                    return;
                }
                double toReduce = getUser().getHealth().getUsable()*0.1;
                getUser().sumRemainingHealth(toReduce*(-1));
                double afterHealth = getUser().getHealth().getRemaining();
                LogWriterUtil.log("Scarlet lost "+toReduce+" health ("+beforeHealth+" > "+afterHealth+") due to Nebula", combatFlow.getTurnCount());
                if (afterHealth <= 0) {
                    LogWriterUtil.log("Scarlet's health cannot be lowered than 1 this way", combatFlow.getTurnCount());
                    getUser().setRemainingHealth(1);
                }
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

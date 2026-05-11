package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

public class Galdr_Sorcery extends Skill implements SkillWithCondition {

    public static String NAME = "Galdr: \"Everlasting Moonlit\"";

    public Galdr_Sorcery() {
        super();
        setDescription("ได้รับ Counter [Galdr]\n" +
                "เมื่อใช้งาน Fragment of Seidr เพื่อลดทอนความเสียหาย จะได้รับ [Galdr] หนึ่งสแต็ค ทับซ้อนสูงสุด XB สแต็ค" +
                "ต้องมีอย่างน้อย 1 Galdr จึงจะใช้งานสกิลนี้ได้\n" +
                "เมื่อใช้งาน จะเริ่มร่าย Ceremony ที่สิ้นสุดลงเมื่อจบรอบเทิร์นหน้าของตนเอง\n" +
                "หากเสร็จสิ้นการร่าย เลือกพื้นที่ 3x3 เมตรในสนาม แปลงพลังเวทโดยรอบบางส่วนให้เป็นอนุภาค \"วงแหวนสรรพสิ่ง\" ยิงลำแสงลงมาจากฟ้าใส่พื้นที่เป้าหมาย สร้างความเสียหายเวท XA หน่วย หากมี Galdr มากกว่า 1 หน่วย สร้างความเสียหายซ้ำตามจำนวน Galdr ที่มี จากนั้นทำลาย Galdr ทั้งหมด\n" +
                "ความเสียหายนี้ไม่สามารถถูกหลบได้ หลังจากที่จู่โจมแล้ว ลบล้างผลเวทมนตร์สนามที่มีในปัจจุบันทั้งหมด\n" +
                "จากนั้น ผู้ใช้เข้าสู่สถานะ 'ตอนจบของเรื่องราว' เป็นเวลา XC รอบเทิร์น\n" +
                "หากผู้ใช้มี Galdr อย่างน้อย 5 สแต็คหรือมากกว่า สกิลนี้จะเปลี่ยนรูปแบบเป็น Turn และเมื่อสั่งใช้งาน จะร่ายเสร็จสิ้นทันทีโดยไม่ต้องร่าย Ceremony\n" +
                "สกิลนี้ใช้งาน MP และนับคูลดาวน์เมื่อร่ายเสร็จสิ้นเท่านั้น");
        setActionType("Action");
        setManaCost(22);
        setCooldown(5);
        getSkillMultiplier().put("XA",new SkillMultiplier("1.9*MATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.AOE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.CEREMONY);

        getSkillMultiplier().put("XB",new SkillMultiplier("5"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.LIMIT);

        getSkillMultiplier().put("XC",new SkillMultiplier("2"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.DURATION);
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
        double xa = getSkillMultiplier().get("XA").getResult();
        double galdr = getUser().getCounter().get(CounterName.GALDR);
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                        .addActType(ActType.CAST)
                        .build()
        );
        for (double i = 0 ; i < galdr ; i++) {
            if (!skillTarget.getTarget(0).isEmpty()) {
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                                .effect(ActionEffectType.DAMAGE_MAGICAL, xa, 1)
                                .addActType(ActType.STRIKE)
                                .build()
                );
            }
        }
        getUser().counterSet(CounterName.GALDR, 0);
        int duration = (int) getSkillMultiplier().get("XC").getResult();
        Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Stories' End");
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(),getUser(), getUser())
                        .condition(condition, duration)
                        .addActType(ActType.CONDITION_GIVEN)
                        .build());
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        double buffAMP = 1+getUser().getStats().get(StatType.BUFFAMPLIFIER).getFinal();
        double debuffAMP = 1+getUser().getStats().get(StatType.DEBUFFAMPLIFIER).getFinal();
        Conditions conditions = new Conditions("Stories' End");

        conditions.setConditionType(ConditionType.NEUTRAL);
        conditions.setConditionTierType(ConditionTierType.BOUND);

        //remove and re-add to database
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(conditions.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(conditions.getName(), conditions);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(conditions, unit);
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

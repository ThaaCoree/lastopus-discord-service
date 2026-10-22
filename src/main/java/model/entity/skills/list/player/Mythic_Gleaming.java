package model.entity.skills.list.player;

import controller.CombatFlow;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

import java.util.List;
import java.util.Map;

public class Mythic_Gleaming extends Skill implements SkillWithCondition {

    public static String NAME = "Mythic Gleaming";

    public Mythic_Gleaming() {
        super();
        setDescription("เลือกศัตรูหนึ่งเป้าหมายและพันธมิตรอื่นสองเป้าหมาย วิงวอนต่อแสงฟากฟ้า ส่งคลื่นเวทมนตร์ไปยังเป้าหมายแรก สร้างความเสียหายเวทธาตุลม ธาตุน้ำ หรือธาตุแสงให้กับศัตรูเป้าหมายแรก XA หน่วย\n" +
                "จากนั้นกระแสเวทมนตร์จะย้อนกลับมาฮีลให้กับผู้ใช้และพันธมิตรที่เลือก XB หน่วย\n" +
                "ไม่สามารถใช้งาน Mythic Flow หลังจากใช้งานสกิลนี้ได้\n" +
                "หาก Voahri อยู่ในสถานะ 'ตอนจบของเรื่องราว' เป้าหมายที่ได้รับความเสียหายจะถูกลด ATK ลง XC เป็นเวลา XD รอบเทิร์น");
        setActionType("Action");
        setManaCost(12);
        setCooldown(2);
        getSkillMultiplier().put("XA",new SkillMultiplier("1.8*MATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.ELEMENTAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.WIND);
        getSkillMultiplier().get("XA").getTags().add(SkillType.WATER);
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIGHT);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.18*MATK*(1+HealAMP)"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.HEALING);
        getSkillMultiplier().get("XB").getTags().add(SkillType.RECOVERY);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.2*(1+DebuffAMP)"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XC").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XC").setPercent(true);

        getSkillMultiplier().put("XD",new SkillMultiplier("2"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.DURATION);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
        );
        spec    .addFields(
                new SkillInputSpec.InputField<String>("Type", SkillInputSpec.InputType.SELECT, 0)
                        .options(List.of("Damage","Heal"), 0)
                        .labelProvider(String::toString, 0)
        , 0, 0);
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
        if (skillTarget.getTarget(0).isEmpty()) return;

        double xa = getSkillMultiplier().get("XA").getResult();
        double xb = getSkillMultiplier().get("XB").getResult();
        int duration = (int) getSkillMultiplier().get("XD").getResult();
        Conditions gleaming = combatFlow.getDatabase().getAllConditionMap().get("Mythic Gleaming");

        dispatchByUnitDecision(combatFlow, skillTarget, 0, 0, Map.of(
                "damage", unit -> {
                    sendEffect(combatFlow, unit, ActionEffectType.DAMAGE_MAGICAL, xa,
                            ActType.CAST, ActType.STRIKE);
                    if (getUser().hasCondition("Stories' End")) {
                        sendCondition(combatFlow, unit, gleaming, duration, ActType.CAST);
                    }
                },
                "heal", unit -> sendEffect(combatFlow, unit, ActionEffectType.HEALTH_RECOVER, xb,
                        ActType.HEAL, ActType.HEALTH_RECOVER)
        ));
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions conditions = new Conditions("Mythic Gleaming");
        conditions.getStatModifiers(StatType.HEALAMPLIFIER).setGlobalMult(getSkillMultiplier().get("XC").getResult());

        conditions.setConditionType(ConditionType.DEBUFF);
        conditions.setConditionTierType(ConditionTierType.ADVANCED);

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

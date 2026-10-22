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

import java.util.List;

public class Wrath_Of_Blaze extends Skill {

    public static String NAME = "Wrath of Blaze";

    public Wrath_Of_Blaze() {
        super();
        setDescription("สามารถเสริมเวทมนตร์เข้าไปยังอาวุธเพื่อเสริมการโจมตีครั้งต่อไปได้, ได้รับสถานะ Heavenly Wrath จนกว่าจะโจมตีครั้งถัดไป\n" +
                "การโจมตีในระหว่างที่มีสถานะนี้ จะสร้างความเสียหายมากขึ้นตามมานาที่สูญเสียไป\n" +
                "สร้างความเสียหายมากขึ้น XA หน่วยต่อมานา 1 หน่วยที่ใช้จ่าย");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        setManaReservePercent(0.4);
        getSkillMultiplier().put("XA",new SkillMultiplier("2*PATK + 2*RATK + 2*MATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser(),
                List.of("Mana Usage"),
                new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.CUSTOM, 0));
        spec    .addFields(
                new SkillInputSpec.InputField<String>("Amount", SkillInputSpec.InputType.NUMBER, 0)
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
        Conditions condition = new Conditions("Heavenly Wrath");
        double mana_spent = 0;
        if (skillTarget.getTarget(0).contains("Mana Usage")) {
            mana_spent = Double.parseDouble(skillTarget.getDecision("Mana Usage", 0, 0));
        }

        double xb = mana_spent * getMultiplierResult("XA");
        condition.setDescription("สร้างความเสียหายได้มากขึ้น "+xb+" หน่วยในการโจมตีครั้งถัดไป");

        condition.setConditionType(ConditionType.NEUTRAL);
        condition.setConditionTierType(ConditionTierType.UNDISPELLABLE);

        addConditionToDatabase(condition, combatFlow);

        int duration = 99;

        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(),getUser(), getUser())
                        .condition(condition, duration, "DamageAmount", xb)
                        .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                        .build());
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ActionEvent.class, EventPhase.POST, 1, (ActionEvent event) -> {
            if (!event.hasActType(ActType.ATTACK) || event.unit_source != getUser() || event.event_source.equals(getName())) return;
            if (getUser().hasCondition("Heavenly Wrath")) {
                ConditionInstance condition = getUser().findCondition("Heavenly Wrath");
                event.addFlatModifier(ActionEffectType.DAMAGE_MAGICAL, condition.getNumberRecordOrDefault("DamageAmount", 0));
                ConditionManager.removeOneCondition(getUser(), "Heavenly Wrath");
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

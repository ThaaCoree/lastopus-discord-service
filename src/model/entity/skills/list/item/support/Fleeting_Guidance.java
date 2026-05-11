package model.entity.skills.list.item.support;

import main.controller.CombatFlow;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.ConditionTierType;
import model.type.ConditionType;
import model.type.SkillType;
import model.type.StatType;

import java.util.ArrayList;
import java.util.List;

public class Fleeting_Guidance extends Skill implements SkillWithCondition {

    public static String NAME = "Fleeting Guidance";

    public Fleeting_Guidance() {
        super();
        setDescription("ยูนิตพันธมิตรที่มีจำนวนการเคลื่อนที่น้อยที่สุดทั้งหมดจะมีจำนวนการเคลื่อนที่เท่ากับผู้ใช้");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.MOVEMENT);
        getSkillMultiplier().put("XA", new SkillMultiplier("MSPD"));
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
    public void initializeEvent(CombatFlow combatFlow) {
        int mspd = Integer.MAX_VALUE;
        List<Unit> allies = getOtherAllies(combatFlow);
        if (allies == null) return;

        List<Unit> applyingUnits = new ArrayList<>();

        for (Unit otherAlly : allies) {
            if (otherAlly == null || otherAlly.getStats() == null) continue;

            ConditionManager.removeCondition(otherAlly, "Fleeting Guidance");

            var stat = otherAlly.getStats().get(StatType.MOVEMENTSPEED);
            if (stat == null) continue;

            int unitMSPD = (int) stat.getFinal();

            if (unitMSPD < mspd) {
                mspd = unitMSPD;
                applyingUnits.clear();
                applyingUnits.add(otherAlly);
            } else if (unitMSPD == mspd) {
                applyingUnits.add(otherAlly);
            }
        }

        Conditions condition = combatFlow.findCondition("Fleeting Guidance");
        if (condition == null || getUser() == null) return;

        for (Unit applyingUnit : applyingUnits) {
            if (applyingUnit == null) continue;
            ConditionManager.applyCondition(condition, getUser(), applyingUnit, 99);
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions condition = new Conditions("Fleeting Guidance");
        condition.getStatModifiers(StatType.MOVEMENTSPEED).setOverride(getSkillMultiplier().get("XA").getResult());

        condition.setConditionType(ConditionType.NEUTRAL);
        condition.setConditionTierType(ConditionTierType.UNDISPELLABLE);

        //remove and re-add to database
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(condition.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(condition.getName(), condition);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(condition, unit);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}

package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import controller.event.events.RoundEvent;
import manager.ConditionManager;
import model.entity.ConditionInstance;
import model.entity.Conditions;
import model.entity.PassiveNode;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

public class Open_The_Pages extends Skill implements SkillWithCondition {

    public static String NAME = "Open The Pages / Close The Wisdom";

    public Open_The_Pages() {
        super();
        setDescription("[ Open The Pages ]\n" +
                "เปิดหนังสือเลือกซัมมอน ฝูงปลาหรือฝูงแมงกระพรุน \n" +
                "ฝูงปลา : เลือกมอบบัพซึ่งฮีล HP XA หน่วยตลอดระยะเวลาสกิลให้กับ XE ยูนิต\n" +
                "ฝูงแมงกระพรุน :เลือกมอบบัพซึ่งฟื้นฟู MP XB หน่วยตลอดระยะเวลาสกิลให้กับ XE ยูนิต มอบให้ตัวเองไม่ได้\n" +
                "เมื่อบัพจบลง ทำการฮีล HP XC หน่วย หรือ MP XD หน่วยให้กับเจ้าของบัพ\n" +
                "Open The Pages คงอยู่ XF เทิร์น\n" +
                "[ Close The Wisdom ]\n" +
                "ใช้ Reaction ในการปิดหนังสือได้ทุกเมื่อ เลือกทำลายฝูงปลาหรือฝูงแมงกระพรุน ลดคูลดาวน์ของสกิลลงเท่ากับระยะเวลาที่เหลือของบัพ\n" +
                "ผลการฟื้นฟูของ Open The Pages จะลดเหลือครึ่งเดียวหากผู้ใช้หมดสติอยู่");
        setActionType("Action");
        setManaCost(9);
        setCooldown(6);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.51*MATK*(1+BuffAMP)*(1+HealAMP)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.RECOVERY);
        getSkillMultiplier().get("XA").getTags().add(SkillType.HEALING);
        getSkillMultiplier().get("XA").getTags().add(SkillType.WATER);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.07*MATK*(1+BuffAMP)"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.RECOVERY);
        getSkillMultiplier().get("XB").getTags().add(SkillType.WATER);

        getSkillMultiplier().put("XC",new SkillMultiplier("1.5*(0.51*MATK*(1+BuffAMP)*(1+HealAMP))"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XC").getTags().add(SkillType.RECOVERY);
        getSkillMultiplier().get("XC").getTags().add(SkillType.HEALING);
        getSkillMultiplier().get("XC").getTags().add(SkillType.WATER);

        getSkillMultiplier().put("XD",new SkillMultiplier("1.5*(0.07*MATK*(1+BuffAMP))"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XD").getTags().add(SkillType.RECOVERY);
        getSkillMultiplier().get("XD").getTags().add(SkillType.WATER);

        getSkillMultiplier().put("XE",new SkillMultiplier("3"));
        getSkillMultiplier().get("XE").getTags().add(SkillType.LIMIT);

        getSkillMultiplier().put("XF",new SkillMultiplier("3"));
        getSkillMultiplier().get("XF").getTags().add(SkillType.DURATION);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
        );
//        spec.addFields(List.of(
//                new SkillInputSpec.InputField<String>("Mode", SkillInputSpec.InputType.SELECT)
//                        .options(List.of("choice","choice"))
//                        .labelProvider(String::toString)
//        ), 0);
        return spec;
    }

    @Override
    public void calculateExtra() {
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        boolean removed = false;
        for (Unit unit : combatFlow.getAllUnit().values()) {
            for (ConditionInstance conditionInstance : unit.getConditionInstances().values()) {
                if (conditionInstance.getCondition().getName().equals("Open The Pages HP")) {
                    double healing = getSkillMultiplier().get("XC").getResult();
                    removed = true;
                    double ia = 1;
                    double ua = 1;
                    for (PassiveNode node : unit.getAllocatedPassives().values()) {
                        if (node.getName().equals("Intense Affection")) {
                            ia = 2;
                        }
                        if (node.getName().equals("Unaffected Affection")) {
                            ua = 0;
                        }
                    }
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), unit)
                                    .effect(ActionEffectType.HEALTH_RECOVER,healing*ia*ua, 1)
                                    .addActType(ActType.HEAL, ActType.HEALTH_RECOVER, ActType.SKILL_TRIGGER)
                                    .build()
                    );
                }
                if (conditionInstance.getCondition().getName().equals("Open The Pages MP")) {
                    double healing = getSkillMultiplier().get("XD").getResult();
                    removed = true;
                    double ia = 1;
                    double ua = 1;
                    for (PassiveNode node : unit.getAllocatedPassives().values()) {
                        if (node.getName().equals("Intense Affection")) {
                            ia = 2;
                        }
                        if (node.getName().equals("Unaffected Affection")) {
                            ua = 0;
                        }
                    }
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), unit)
                                    .effect(ActionEffectType.MANA_RECOVER,healing*ia*ua, 1)
                                    .addActType(ActType.MANA_RECOVER, ActType.SKILL_TRIGGER)
                                    .build()
                    );
                }
            }
            unit.getConditionInstances().values().removeIf(val -> val.getCondition().getName().equals("Open The Pages HP"));
            unit.getConditionInstances().values().removeIf(val -> val.getCondition().getName().equals("Open The Pages MP"));
        }
        if (removed) {
            for (SkillInstance instance : getUser().getAllSkill().values()) {
                if (instance.getSkillData().getName().equals(NAME)) {
                    int xfResult = (int) getSkillMultiplier().get("XF").getResult();
                    instance.setOnCooldown(getCooldown() - xfResult);
                }
            }
            getUser().getResources().get(ResourceType.MANA).sumRemaining(getManaCost());
        } else {
            if (!skillTarget.getTarget(0).isEmpty()) {
                int xe = (int) getSkillMultiplier().get("XE").getResult();
                Conditions condition = combatFlow.getDatabase().getAllConditionMap().get("Open The Pages HP");

                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                                .condition(condition, xe)
                                .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                                .build()
                );
            }
        }
    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        Conditions hp = new Conditions("Open The Pages HP");
        Conditions mp = new Conditions("Open The Pages MP");

        hp.setConditionType(ConditionType.BUFF);
        hp.setConditionTierType(ConditionTierType.GENERAL);
        mp.setConditionType(ConditionType.BUFF);
        mp.setConditionTierType(ConditionTierType.GENERAL);
        hp.setDescription("จะถูกฮีลเป็นจำนวน "+getSkillMultiplier().get("XA").getResultString()+" เมื่อจบรอบเทิร์น");
        mp.setDescription("จะฟื้นฟู MP เป็นจำนวน "+getSkillMultiplier().get("XB").getResultString()+" เมื่อจบรอบเทิร์น");

        //remove and re-add to database
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(hp.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(hp.getName(), hp);
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(mp.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(mp.getName(), mp);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(hp, unit);
            ConditionManager.reapplyCondition(mp, unit);
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        combatFlow.getEventBus().register(RoundEvent.class, EventPhase.POST, 0, event -> {
            for (Unit unit : combatFlow.getAllUnit().values()) {
                for (ConditionInstance conditionInstance : unit.getConditionInstances().values()) {
                    if (conditionInstance.getCondition().getName().equals("Open The Pages HP")) {
                        double healing = getSkillMultiplier().get("XA").getResult();
                        double ia = 1;
                        double ua = 1;
                        double knock_down = 1;
                        if (getUser().getHealth().getRemaining() <= 0) {
                            knock_down = 0.5;
                        }
                        for (PassiveNode node : unit.getAllocatedPassives().values()) {
                            if (node.getName().equals("Intense Affection")) {
                                ia = 2;
                            }
                            if (node.getName().equals("Unaffected Affection")) {
                                ua = 0;
                            }
                        }
                        sendActionEvent(combatFlow.getEventBus(),
                                ActionEvent.builder(getName(), getUser(), unit)
                                        .effect(ActionEffectType.HEALTH_RECOVER,healing*ia*ua*knock_down, 1)
                                        .addActType(ActType.HEAL, ActType.HEALTH_RECOVER, ActType.SKILL_TRIGGER)
                                        .build()
                        );
                    }
                    if (conditionInstance.getCondition().getName().equals("Open The Pages MP")) {
                        double healing = getSkillMultiplier().get("XB").getResult();
                        double ia = 1;
                        double ua = 1;
                        double knock_down = 1;
                        if (getUser().getHealth().getRemaining() <= 0) {
                            knock_down = 0.5;
                        }
                        for (PassiveNode node : unit.getAllocatedPassives().values()) {
                            if (node.getName().equals("Intense Affection")) {
                                ia = 2;
                            }
                            if (node.getName().equals("Unaffected Affection")) {
                                ua = 0;
                            }
                        }
                        sendActionEvent(combatFlow.getEventBus(),
                                ActionEvent.builder(getName(), getUser(), unit)
                                        .effect(ActionEffectType.MANA_RECOVER,healing*ia*ua*knock_down, 1)
                                        .addActType(ActType.MANA_RECOVER, ActType.SKILL_TRIGGER)
                                        .build()
                        );
                    }
                }
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

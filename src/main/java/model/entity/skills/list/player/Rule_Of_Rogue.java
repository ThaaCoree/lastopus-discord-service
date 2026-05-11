package model.entity.skills.list.player;

import javafx.beans.InvalidationListener;
import controller.CombatFlow;
import controller.event.EventBus;
import controller.event.events.ActionEvent;
import model.entity.ConditionInstance;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.entity.units.Unit;
import model.modifier.BasicModifier;
import model.type.*;
import util.LogWriterUtil;

import java.util.List;

public class Rule_Of_Rogue extends Skill {

    public static String NAME = "Rule of Rogue";

    public Rule_Of_Rogue() {
        super();
        setDescription("จะไม่ทำให้เกิดเสียงเมื่ออยู่ในระหว่าง [สเตลท์] และเมื่อจู่โจมระหว่าง [สเตลท์] จะสร้างความเสียหายเพิ่มเติม XA หน่วย พร้อมเคลื่อนไหวได้ XB เมตร\n" +
                "การจู่โจมเป้าหมายที่กำลังได้รับดีบัพจะเพิ่มสแต็ค Illusion & Dream 1 สแต็ค\n" +
                "Illusion & Dream จะมอบ AttackSpeed XC สะสมได้สูงสุด 10 สแต็ค");
        setActionType("Passive");
        getPureTags().add(SkillType.PHYSICAL);
        setManaReservePercent(0.1);
        getSkillMultiplier().put("XA",new SkillMultiplier("1.1*PATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STEALTH);
        getSkillMultiplier().get("XA").getTags().add(SkillType.FIGHTING_STYLE);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.4*MSPD"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.MOVEMENT);
        getSkillMultiplier().get("XB").getTags().add(SkillType.LIMIT);
        getSkillMultiplier().get("XB").getTags().add(SkillType.FIGHTING_STYLE);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.05"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XC").getTags().add(SkillType.FIGHTING_STYLE);
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
        if (getUser().getCounter() != null) {
            BasicModifier modifier = new BasicModifier();
            modifier.setFlat(getSkillMultiplier().get("XC").getResult() * getUser().getCounter().get(CounterName.IllusionAndDream));
            getSkillModifier().getStatModifiers().put(StatType.ATTACKSPEED, modifier);

            getUser().getCounter().addListener((InvalidationListener) change -> {
                getSkillModifier().getStatModifierSafe(StatType.ATTACKSPEED).setGlobalMult(getSkillMultiplier().get("XC").getResult() * getUser().getCounter().get(CounterName.IllusionAndDream));
                getUser().calculateStatAndStatus();
            });
        }
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ActionEvent.class, EventPhase.POST, 0, (ActionEvent event) -> {
            if (event.unit_source != getUser() || !event.hasActType(ActType.STRIKE)) return;
            List<Unit> targets = event.unit_target;
            int stack = 0;
            for (Unit target : targets) {
                for (ConditionInstance conditionInstance : target.getConditionInstances().values()) {
                    if (conditionInstance.getCondition().getConditionType().equals(ConditionType.DEBUFF)) {
                        stack++;
                        break;
                    }
                }
            }

            for (int i = 0; i< stack;i++) {
                double counter = getUser().getCounter().get(CounterName.IllusionAndDream);
                if (counter < 10) {
                    getUser().counterIncrement(CounterName.IllusionAndDream);
                    LogWriterUtil.log(getUser().getName()+" gained 1 Illusion & Dream");
                }
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

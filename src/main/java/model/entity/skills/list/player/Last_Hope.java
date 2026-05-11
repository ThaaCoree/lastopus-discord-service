package main.java.model.entity.skills.list.player;

import main.java.controller.CombatFlow;
import main.java.controller.event.events.ActionEvent;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.entity.skills.SkillMultiplier;
import main.java.model.entity.units.Unit;
import main.java.model.type.ActType;
import main.java.model.type.ActionEffectType;
import main.java.model.type.SkillType;

public class Last_Hope extends Skill {

    public static String NAME = "Last Hope";

    public Last_Hope() {
        super();
        setDescription("สละ HP และ MP ทั้งหมดที่เหลืออยู่ ฟื้นฟู HP XA หน่วยและ MP XB หน่วย ให้กับพันธมิตรทั้งหมด โดยการเฉลี่ยให้เท่ากัน \n" +
                "จะฟื้นฟูยูนิตที่มี HP และ MP น้อยที่สุดเป็นสองเท่า ยูนิตที่ถูกฟื้นฟูพลังชีวิตจาก 0 จะตื่นขึ้นจากการหมดสติ");
        setActionType("Turn");
        setManaCost(0);
        setCooldown(9);
        getSkillMultiplier().put("XA",new SkillMultiplier("(RemainingHP*3 + UsableHP*4)*(1+HealAMP)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.RECOVERY);
        getSkillMultiplier().get("XA").getTags().add(SkillType.HEALING);
        getSkillMultiplier().get("XA").getTags().add(SkillType.REDEMPTION);

        getSkillMultiplier().put("XB",new SkillMultiplier("RemainingMP*2 + UsableMP*3"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.RECOVERY);
        getSkillMultiplier().get("XB").getTags().add(SkillType.REDEMPTION);
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
//        , 0, 0);
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
            double xb = getSkillMultiplier().get("XB").getResult();
            int allies_number = -1;
            for (Unit unit : getAllies(combatFlow)) {
                allies_number++;
            }
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), getOtherAllies(combatFlow))
                            .effect(ActionEffectType.HEALTH_RECOVER, xa/allies_number, 1)
                            .effect(ActionEffectType.MANA_RECOVER, xb/allies_number, 1)
                            .addActType(ActType.HEAL, ActType.HEALTH_RECOVER, ActType.MANA_RECOVER, ActType.CAST)
                            .build()
            );
        getUser().getHealth().setRemaining(0);
        getUser().getMana().setRemaining(0);
        getUser().calculateEverything();
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

    }

    @Override
    public String getName() {
        return NAME;
    }
}

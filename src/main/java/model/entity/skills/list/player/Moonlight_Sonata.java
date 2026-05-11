package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.*;
import util.LogWriterUtil;

public class Moonlight_Sonata extends Skill {

    public static String NAME = "Moonlight Sonata";

    public Moonlight_Sonata() {
        super();
        setDescription("ทุกการโจมตีครั้งที่ 8 สร้างความเสียหายเพิ่มเติม XA หน่วย ฟื้นฟู HP XB หน่วย และ MP XC หน่วยให้กับตนเอง");
        setActionType("Passive");
        setManaReservePercent(0.15);
        getSkillMultiplier().put("XA",new SkillMultiplier("2*PATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.10*UsableHP*(1+HealAMP)"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.RECOVERY);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.15*UsableMP"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.RECOVERY);
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
        combatFlow.getEventBus().register(ActionEvent.class, EventPhase.POST, 0 , event -> {
            if (event.unit_source == getUser() && event.hasActType(ActType.ATTACK)) {
                getUser().counterIncrement(CounterName.MOONLIGHT_SONATA);
                double counter = getUser().getCounter().get(CounterName.MOONLIGHT_SONATA);
                if (counter >= 8) {
                    double toHeal = getSkillMultiplier().get("XB").getResult()*getUser().getHealth().getUsable();
                    double toRecover = getSkillMultiplier().get("XC").getResult()*getUser().getMana().getUsable();
                    LogWriterUtil.log("Moonlight Sonata Triggered");
                    sendActionEvent(combatFlow.getEventBus(),
                            ActionEvent.builder(getName(), getUser(), getUser())
                                    .effect(ActionEffectType.HEALTH_RECOVER, toHeal, 1)
                                    .effect(ActionEffectType.MANA_RECOVER, toRecover, 1)
                                    .addActType(ActType.SKILL_TRIGGER, ActType.HEALTH_RECOVER, ActType.MANA_RECOVER)
                                    .build()
                    );
                    getUser().counterSet(CounterName.MOONLIGHT_SONATA, 0);
                }
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

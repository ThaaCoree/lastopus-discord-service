package model.entity.skills.list.monster;

import controller.CombatFlow;
import controller.event.EventBus;
import controller.event.events.ActionEvent;
import controller.event.events.ResourceEvent;
import model.entity.Conditions;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillMultiplier;
import model.entity.skills.SkillTarget;
import model.entity.units.Unit;
import model.type.ActType;
import model.type.ActionEffectType;
import model.type.EventPhase;
import model.type.SkillType;

import java.util.List;

public class Following_Light extends Skill {

    public static String NAME = "Following Light";

    public Following_Light() {
        super();
        setDescription("เมื่อสร้างความเสียหายสำเร็จ สร้างความเสียหายอีกครั้งเป็นจำนวน XA หน่วย, ประเภทของความเสียหายขึ้นอยู่กับความเสียหายที่ทำให้สกิลนี้เกิดผล\n" +
                "ในระหว่างเปิดใช้งานสกิลนี้จะไม่สามารถจู่โจมเพิ่มตาม Cast Speed ได้แม้ร่ายเวทมนตร์จู่โจม");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("1.3*MATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
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

    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ResourceEvent.class, EventPhase.POST, 0, (ResourceEvent event) -> {
            if (event.source != getUser()) return;
            if (!event.isDamage()) return;
            if (event.event_source.equalsIgnoreCase(getName())) return;

            ActionEffectType actType = ActionEffectType.DAMAGE_PHYSICAL;
            if (event.effectType == ActionEffectType.DAMAGE_MAGICAL) {
                actType = ActionEffectType.DAMAGE_MAGICAL;
            }
            if (event.effectType == ActionEffectType.DAMAGE_PURE) {
                actType = ActionEffectType.DAMAGE_PURE;
            }
            if (event.effectType == ActionEffectType.DAMAGE_TRUE) {
                actType = ActionEffectType.DAMAGE_TRUE;
            }
            sendActionEvent(combatFlow.getEventBus(),
                                ActionEvent.builder(getName(), getUser(), event.target)
                                        .effect(actType, event.amount, 1)
                                        .addActType(ActType.STRIKE, ActType.SKILL_TRIGGER)
                                        .build()
                        );
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

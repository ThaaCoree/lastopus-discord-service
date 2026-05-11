package model.entity.skills.list.item.defensive;

import main.controller.CombatFlow;
import main.controller.event.EventBus;
import main.controller.event.events.ResourceEvent;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.entity.units.Unit;
import model.type.ActionEffectType;
import model.type.EventPhase;
import model.type.SkillType;
import util.LogWriterUtil;

public class Last_Stand extends Skill {

    public static String NAME = "Last Stand";

    public Last_Stand() {
        super();
        setDescription("เมื่อพันธมิตรทั้งหมดสิ้นสภาพต่อสู้ ตนเองฟื้นฟูพลังชีวิต XA หน่วยและถูกปลุกให้ตื่น");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.5*UsableHP"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.RECOVERY);
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
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ResourceEvent.class, EventPhase.POST, 0, (ResourceEvent event) -> {
            double concious_unit = 0;
            if (event.effectType.equals(ActionEffectType.DAMAGE_MAGICAL) ||
                    event.effectType.equals(ActionEffectType.DAMAGE_PHYSICAL) ||
                    event.effectType.equals(ActionEffectType.DAMAGE_PURE) ||
                    event.effectType.equals(ActionEffectType.DAMAGE_TRUE)) {
                for (Unit ally : getAllies(combatFlow)) {
                    if (ally.getHealth().getRemaining() > 0) {
                        concious_unit++;
                    }
                }
                if (concious_unit == 0) {
                    LogWriterUtil.log(">Last Stand triggered");
                    double xa = getSkillMultiplier().get("XA").getResult();
                    getUser().getHealth().sumRemaining(xa);
                }
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

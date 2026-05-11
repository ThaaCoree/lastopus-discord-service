package model.entity.skills.list.item.defensive;

import main.controller.CombatFlow;
import main.controller.event.EventBus;
import main.controller.event.events.ResourceEvent;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.type.ActionEffectType;
import model.type.EventPhase;
import model.type.SkillType;
import util.LogWriterUtil;

public class Critical_Point_Protection extends Skill {

    public static String NAME = "Critical Point Protection";

    public Critical_Point_Protection() {
        super();
        setDescription("เมื่อพลังชีวิตลดจนเหลือ 0 จากการจู่โจม\n" +
                "ถอดอุปกรณ์ทั้งหมดที่มอบสกิลนี้ ทำให้พลังชีวิตกลับมาเป็น 1 หน่วย ประคองสติไว้\n" +
                "เมื่อความสามารถนี้ทำงาน ครั้งถัดไปที่สวมใส่อุปกรณ์ประเภทหมวก จะใช้ Turn");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.DEFENSE);
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
        eventBus.register(ResourceEvent.class, EventPhase.MODIFY, 0, (ResourceEvent event) -> {
            if (!event.target.equals(getUser())) return;
            if (event.effectType.equals(ActionEffectType.DAMAGE_MAGICAL) ||
                    event.effectType.equals(ActionEffectType.DAMAGE_PHYSICAL) ||
                    event.effectType.equals(ActionEffectType.DAMAGE_PURE) ||
                    event.effectType.equals(ActionEffectType.DAMAGE_TRUE)) {
                double health_pool = 0;
                if (!event.bypassDebris) {
                    health_pool += getUser().getDebris().getRemaining();
                }
                health_pool += getUser().getHealth().getRemaining();
                if (event.amount > health_pool) {
                    event.amount = health_pool - 1;
                    getUser().getEquipmentManager().unequip(1);
                    LogWriterUtil.log("Critical Point Protection activated! removing helmet and reduced the damage", combatFlow.getTurnCount());
                }
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

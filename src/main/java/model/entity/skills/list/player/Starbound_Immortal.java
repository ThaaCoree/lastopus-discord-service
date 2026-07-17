package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.EventBus;
import controller.event.events.ActionEvent;
import controller.event.events.ResourceEvent;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.*;

public class Starbound_Immortal extends Skill {

    public static String NAME = "Starbound Immortal";

    public Starbound_Immortal() {
        super();
        setDescription("The Iron Tomb ได้รับสกิลนี้เช่นกัน\n" +
                "เมื่อมียูนิตอื่นที่ครอบครองสกิลนี้โดยยังไม่หมดสภาพต่อสู้ในรัศมี 5 เมตร เมื่อได้รับความเสียหายที่ทำให้พลังชีวิตเหลือต่ำกว่า 1 หน่วย, ยกเลิกการได้รับความเสียหายนั้น\n" +
                "Divine Intervention:\n" +
                "Tezzeract ทำการโจมตี Akivili ด้วยรยางค์ของมัน สร้าง True Damage 0 หน่วย\n" +
                "\n" +
                "Divine Invocation:\n" +
                "เลือก 1 ยูนิต มอบผลของ Starbound Immortal ให้กับมันจนกว่าจะจบรอบเทิร์นถัดไป\n\n" +
                "หากสกิลนี้ยกเลิกความเสียหายจริง สูญเสีย 1 วิวรณ์ หากมีวิวรณ์ไม่มากพอให้สูญเสีย รับความเสียหายตามปกติ\n" +
                "ระหว่างเปิดใช้งานสกิลนี้ รับการฟื้นฟูจากทุกแหล่งลดลง 90% และลด HealthRegen ลง 94%");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        setManaReservePercent(0.55);
        getSkillMultiplier().put("XA",new SkillMultiplier("5"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.DISTANCE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIMIT);
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
        double reserve = getUser().getStats().get(StatType.RESERVATION).getFinal();
        setHealthReservePercent(0.99/reserve);

        getSkillModifier().getStatModifierSafe(StatType.HEALTHREGEN).setGlobalMult(-0.94);
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ActionEvent.class, EventPhase.MODIFY, 0, (ActionEvent event) -> {
        if (!event.hasTarget(getUser().getName()) && !event.canDamage(getUser().getName())) return;
        if (event.hasTrueDamage(getUser().getName())) {
            double count = getUser().getCounter().get(CounterName.PROVIDENCE);
            if (count >= 1) {
                event.effects.forEach((key, value) -> {
                    if (key.equals(getUser().getName())) {
                        value.forEach(actionEffect -> {
                            if (actionEffect.type.equals(ActionEffectType.DAMAGE_TRUE)) {
                                actionEffect.finalValue = 0;
                            }
                        });
                    }
                });
                getUser().counterDecrement(CounterName.PROVIDENCE);
            }
        }
        });

        eventBus.register(ResourceEvent.class, EventPhase.MODIFY, 0, (ResourceEvent event) -> {
            if (event.effectType != ActionEffectType.HEALTH_RECOVER) return;
            if (event.target != getUser()) return;
            if (!getIsActive()) return;

            event.amount *= 0.1;
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

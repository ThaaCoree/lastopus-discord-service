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

public class Seidr_Sorcery_Verry_And_Tori extends Skill {

    public static String NAME = "Seidr Sorcery & Verry and Tori";

    public Seidr_Sorcery_Verry_And_Tori() {
        super();
        setDescription("ได้รับ Counter [Fragment of Seidr]\n" +
                "เมื่อมีพันธมิตรใช้งานเวทมนตร์หรือโอปัส จะได้รับ Fragment of Seidr หนึ่งสแต็ค ทับซ้อนได้สูงสุด XA สแต็ค\n" +
                "เมื่อมีพันธมิตรได้รับความเสียหายมากกว่า 1 ใน 5 ส่วนของพลังชีวิตสูงสุด หรือได้รับความเสียหายในขณะที่มีพลังชีวิตต่ำกว่าครึ่ง ใช้งาน XB Fragment of Seidr เพื่อลดความเสียหายนั้นหลังลดทอนเหลือครึ่งหนึ่ง\n" +
                "เมื่อมี Fragment of Seidr อย่างน้อย 8 สแต็ค หรือกำลังอยู่ในสถานะ 'ตอนจบของเรื่องราว' ได้รับ INT และ WIS เพิ่มขึ้น XC");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.FIGHTING_STYLE);
        setManaReservePercent(0.1);
        getSkillMultiplier().put("XA",new SkillMultiplier("10"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIMIT);

        getSkillMultiplier().put("XB",new SkillMultiplier("3"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.REQUIREMENT);
        getSkillMultiplier().get("XB").getTags().add(SkillType.LIMIT);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.10"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.SCALING);
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
        if (getUser().getCounter() == null) return;
        if (!getUser().getRawCounterMap().containsKey(CounterName.FRAGMENT_OF_SEIDR)) {
            getUser().getRawCounterMap().put(CounterName.FRAGMENT_OF_SEIDR,0.0);
            getUser().getCounter().put(CounterName.FRAGMENT_OF_SEIDR,0.0);
        }

        int seidr = getUser().getRawCounterMap().get(CounterName.FRAGMENT_OF_SEIDR).intValue();
        double xc = getSkillMultiplier().get("XC").getResult();
        if (seidr >= 8 || getUser().hasCondition("Stories' End")) {
            getSkillModifier().getStatusModifierSafe(StatusType.INTELLIGENCE).setGlobalMult(xc);
            getSkillModifier().getStatusModifierSafe(StatusType.WISDOM).setGlobalMult(xc);
        }
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ActionEvent.class, EventPhase.POST, 0, (ActionEvent event) -> {
            if (!event.hasActType(ActType.CAST)) return;
                if (isAlly(event.unit_source, combatFlow)) {
                    double counter = getUser().getCounter().get(CounterName.FRAGMENT_OF_SEIDR);
                    double xa = getSkillMultiplier().get("XA").getResult();
                    if (counter < xa) {
                        getUser().counterIncrement(CounterName.FRAGMENT_OF_SEIDR);

                        sendSkillTriggerEvent(combatFlow, getUser().getName()+" gained 1 Fragment of Seidr");
                    }
                }
        });

        eventBus.register(ResourceEvent.class, EventPhase.POST, 0, (ResourceEvent event) -> {
                if (isAlly(event.target, combatFlow)) {
                    if (event.effectType.equals(ActionEffectType.DAMAGE_MAGICAL) ||
                            event.effectType.equals(ActionEffectType.DAMAGE_PHYSICAL) ||
                            event.effectType.equals(ActionEffectType.DAMAGE_PURE) ||
                            event.effectType.equals(ActionEffectType.DAMAGE_TRUE)) {
                        double usable_health = event.target.getHealth().getUsable();
                        double damage = event.amount;

                        double counter = getUser().getCounter().get(CounterName.FRAGMENT_OF_SEIDR);
                        double xb = getSkillMultiplier().get("XB").getResult();
                        if (counter >= xb && damage >= usable_health * 0.2) {
                            getUser().counterSum(CounterName.FRAGMENT_OF_SEIDR, xb * -1);
                            getUser().counterIncrement(CounterName.GALDR);

                            event.amount *= 0.5;

                            sendSkillTriggerEvent(combatFlow, xb+" Fragment of Seidr triggered and shattered, gain 1 Gladr");
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

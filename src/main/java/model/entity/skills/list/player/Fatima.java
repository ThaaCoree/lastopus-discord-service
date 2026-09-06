package model.entity.skills.list.player;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import controller.event.events.RoundEvent;
import manager.ConditionManager;
import model.entity.Conditions;
import model.entity.PassiveNode;
import model.entity.skills.*;
import model.entity.units.Unit;
import model.type.*;

import java.util.List;

public class Fatima extends Skill {

    public static String NAME = "Fatima";

    public Fatima() {
        super();
        setDescription("สะบัดผ้าคลุมที่ส่องแสงราวกับดวงดาว เพื่อห่อร่างกายและเทเลพอร์ตออกจากจุดนั้น เลือกพื้นที่เฉพาะที่ศัตรูมองเห็นเท่านั้น เพื่อปรากฎตัวออกมา พื้นที่เดิมนั้นจะทิ้งร่างแยกของเธอเอาไว้ \n" +
                "หากสการ์เล็ตเลือกพื้นที่ที่มีร่างแยกของเธอ ร่างแยกนั้นจะหายไป และตัวเธอจะได้รับ Debris XA หน่วย รวมถึงทำให้ร่างแยกใหม่จะบล็อกสำเร็จอย่างแน่นอน 1 ครั้งในระหว่างเทิร์นของสการ์เล็ต\n" +
                "\n" +
                "หากใช้งาน Nebula สกิลนี้จะใช้งาน Combine แทน และร่างแยกที่ทิ้งไว้จะใช้งาน Nebula Divergent รูปแบบล่าสุดของสการ์เล็ตทันที");
        setActionType("Action");
        setManaCost(14);
        setCooldown(4);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.4*MATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.DEBRIS);

        getSkillMultiplier().put("XB",new SkillMultiplier("2"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.DURATION);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        List<String> choices = List.of("Teleport to Empty Space", "Teleport to Illusion");
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser(), choices
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.CUSTOM, 0)
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
        if (skillTarget.getTarget(0).contains("Teleport to Illusion")) {

            double xa = getSkillMultiplier().get("XA").getResult();

            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), getUser())
                            .effect(ActionEffectType.CREATE_DEBRIS, xa, 1)
                            .addActType(ActType.CAST, ActType.CREATE_DEBRIS)
                            .build()
            );
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
//        combatFlow.getEventBus().register(RoundEvent.class, EventPhase.POST, 0, event -> {
//            combatFlow.getAllUnit().forEach((name, unit) -> {
//                if (unit.hasCondition("Fatima")) {
//                    double ia = 1;
//                    double ua = 1;
//                    for (PassiveNode node : getUser().getAllocatedPassives().values()) {
//                        if (node.getName().equals("Intense Affection")) {
//                            ia = 2;
//                        }
//                        if (node.getName().equals("Unaffected Affection")) {
//                            ua = 0;
//                        }
//                    }
//                    double toRecover = getSkillMultiplier().get("XA").getResult() * ia * ua;
//                    sendActionEvent(combatFlow.getEventBus(),
//                            ActionEvent.builder(getName(), getUser(), getUser())
//                                    .effect(ActionEffectType.HEALTH_RECOVER, toRecover, 1)
//                                    .addActType(ActType.HEALTH_RECOVER, ActType.SKILL_TRIGGER)
//                                    .build()
//                    );
//                }
//            });
//        });
//
//        combatFlow.getEventBus().register(ActionEvent.class, EventPhase.MODIFY, -1, event -> {
//            combatFlow.getAllUnit().forEach((name, unit) -> {
//                if (unit.hasCondition("Fatima") && event.event_source.equals("Fatima")) {
//                    double current_health = unit.getHealth().getRemaining();
//                    double usable_health = unit.getHealth().getUsable();
//                    double after_heal = current_health + event.getHeal(name);
//                    if (after_heal > usable_health) {
//                        event.doCreateDebris(after_heal - usable_health);
//                        event.act_type.add(ActType.CREATE_DEBRIS);
//                    }
//                }
//            });
//        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

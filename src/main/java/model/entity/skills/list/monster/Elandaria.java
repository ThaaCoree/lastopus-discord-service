package main.java.model.entity.skills.list.monster;

import main.java.controller.CombatFlow;
import main.java.controller.event.events.ActionEvent;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillMultiplier;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.type.ActType;
import main.java.model.type.ActionEffectType;
import main.java.model.type.SkillType;

public class Elandaria extends Skill {

    public static String NAME = "Elandaria";

    public Elandaria() {
        super();
        setDescription("ใช้งานได้เมื่อมีวงเวทที่ประกอบด้วยอักษร ปฐพี, วารี, วายุ, อัคคี แล้วเท่านั้น\n" +
                "วงเวทต้องมีเส้นผ่านศูนย์กลางอย่างน้อย 12 เมตร, ผนวกมานาร่วมด้วยอย่างน้อย 600 หน่วย ซ้อนทับด้วยวงเวทที่ลดผลหรือเพิ่มขอบเขตการควบคุมเวทมนตร์\n" +
                "เวทมนตร์นี้ไม่สามารถถูกหลบได้เมื่อวงเวทประกอบด้วยอักษรวายุอย่างน้อย 14 ตัว, ไม่สามารถบล็อกได้เมื่อประกอบด้วยอักษรอัคคีอย่างน้อย 16 ตัว\n" +
                "ยูนิตที่ได้รับความเสียหายจากเวทมนตร์นี้จะไม่ได้รับการฟื้นฟูจนกว่าจะจบการต่อสู้เมื่อวงเวทประกอบด้วยอักษรวารี 13 ตัว และเวทมนตร์นี้ไม่เกิดผลหากไม่ประกอบด้วยอักษรปฐพีอย่างน้อย 21 ตัว\n" +
                "เมื่อซ้อนทับด้วยเวทมนตร์ที่มีอักษรเวหาอย่างน้อย 6 ตัว เวทมนตร์นี้กลายเป็นสเตลท์\n" +
                "หลังใช้งาน กระจายแสงไปยังทุกแห่งในสนาม ทะลุผ่านและทำลายเงาทั้งหมดในทุกพื้นที่ ยูนิตศัตรูทั้งหมดที่ต้องแสงนี้ได้รับความเสียหายจริงธาตุแสง XA หน่วย สร้างความเสียหายซ้ำตามจำนวนอักษรมนตราในวงเวทที่ใช้ร่าย\n" +
                "ความเสียหายของสกิลนี้ไม่สนใจความต้านทานธาตุ และการร่ายจะไม่ถูกยกเลิกด้วยเวทมนตร์ที่ระดับต่ำกว่า");
        setActionType("Reaction");
        setManaCost(0);
        setCooldown(4);
        getSkillMultiplier().put("XA",new SkillMultiplier("2*MATK"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.ELEMENTAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIGHT);
        getSkillMultiplier().get("XA").getTags().add(SkillType.AOE);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
        );
        spec    .addFields(
                new SkillInputSpec.InputField<String>("Hits", SkillInputSpec.InputType.NUMBER, 0)
                        .labelProvider(String::toString, 0)
        , 0, 0);
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
        if (!skillTarget.getTarget(0).isEmpty()) {
            double xa = getSkillMultiplier().get("XA").getResult();
            int hits = 0;
            for (String name : skillTarget.getTarget(0)) {
                hits = Integer.parseInt(skillTarget.getDecision(name, 0, 0));
                break;
            }
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), getEnemies(combatFlow))
                            .effect(ActionEffectType.DAMAGE_TRUE, xa, hits)
                            .addActType(ActType.CAST, ActType.STRIKE)
                            .build()
            );
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
//        EventBus eventBus = combatFlow.getEventBus();
//        eventBus.register(ActionEvent.class, EventPhase.POST, 0, (ActionEvent event) -> {
//            if (!event.hasActType(ActType.HEAL) || event.unit_source != getUser() || event.event_source.equals(getName())) return;
//            List<Unit> targets = event.unit_target;
//            double heal_amount = event.getHeal();
//
//            sendActionEvent(combatFlow.getEventBus(),
//                                ActionEvent.builder(getName(), getUser(), targets)
//                                        .effect(ActionEffectType.HEALTH_RECOVER,heal_amount, 1)
//                                        .addActType(ActType.HEAL, ActType.HEALTH_RECOVER, ActType.SKILL_TRIGGER)
//                                        .build()
//                        );
//        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

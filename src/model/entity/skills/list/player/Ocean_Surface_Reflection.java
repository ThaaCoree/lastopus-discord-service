package model.entity.skills.list.player;

import main.controller.CombatFlow;
import main.controller.event.events.ActionEvent;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.type.ActType;
import model.type.ActionEffectType;
import model.type.SkillType;

public class Ocean_Surface_Reflection extends Skill {

    public static String NAME = "Ocean Surface Reflection";

    public Ocean_Surface_Reflection() {
        super();
        setDescription("การโจมตีปกติด้วยเวทมนตร์ของ Twelve ใส่พันธมิตรจะเปลี่ยนจากการสร้างความเสียหายเป็นการฟื้นฟูพลังชีวิตให้กับเป้าหมาย XA หน่วย จากนั้นชิ่งไปหาเป้าหมายอื่นอีก XD ยูนิตในสนาม");
        setActionType("Passive");
        setManaReservePercent(0.4);
        getSkillMultiplier().put("XA",new SkillMultiplier("0.2*MATK*(1+HealAMP)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.RECOVERY);
        getSkillMultiplier().get("XA").getTags().add(SkillType.WATER);

        getSkillMultiplier().put("XD",new SkillMultiplier("2"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.LIMIT);

    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.ALLIES, 0)
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
        if (!skillTarget.getTarget(0).isEmpty()) {
            double xa = getSkillMultiplier().get("XA").getResult();
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                            .effect(ActionEffectType.HEALTH_RECOVER, xa, 1)
                            .addActType(ActType.HEAL, ActType.HEALTH_RECOVER)
                            .build()
            );
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
    }

    @Override
    public String getName() {
        return NAME;
    }
}

package model.entity.skills.list.item.utility;

import controller.CombatFlow;
import controller.event.events.ActionEvent;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.entity.units.Unit;
import model.type.ActType;
import model.type.ActionEffectType;
import model.type.SkillType;

import java.util.List;

public class Rain_of_Stars extends Skill {

    public static String NAME = "Rain of Stars";

    public Rain_of_Stars() {
        super();
        setDescription("ไม่สามารถสั่งใช้งานด้วยวิธีปกติได้\n" +
                "ส่งดวงดาวมนตราร่วงหล่นใส่เป้าหมาย XA ครั้ง สร้างความเสียหายเวท XB หน่วย หรือฮีล XC หน่วย");
        setActionType("Combine");
        setManaCost(3);
        setCooldown(0);
        getSkillMultiplier().put("XA",new SkillMultiplier("1+(INT/100)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIMIT);

        getSkillMultiplier().put("XB",new SkillMultiplier("1.5*MATK"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.STRIKE);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.1*MATK"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XC").getTags().add(SkillType.RECOVERY);
        getSkillMultiplier().get("XC").getTags().add(SkillType.HEALING);
    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser()
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 0)
        );
        spec    .addFields(
                new SkillInputSpec.InputField<String>("Mode", SkillInputSpec.InputType.SELECT, 0)
                        .options(List.of("Damage","Heal"), 0)
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
        for (String name : skillTarget.getTarget(0)) {
            Unit target = combatFlow.findUnit(name);
            if (skillTarget.getDecision(name, 0,0).contains("Damage")) {
                double xa = getSkillMultiplier().get("XA").getResult();
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), target)
                                .effect(ActionEffectType.DAMAGE_MAGICAL, xa, 1)
                                .addActType(ActType.SKILL_TRIGGER, ActType.STRIKE)
                                .build()
                );
            }

            if (skillTarget.getDecision(name, 0,0).contains("Heal")) {
                double xa = getSkillMultiplier().get("XB").getResult();
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), target)
                                .effect(ActionEffectType.HEALTH_RECOVER, xa, 1)
                                .addActType(ActType.SKILL_TRIGGER, ActType.HEAL, ActType.HEALTH_RECOVER)
                                .build()
                );
            }
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

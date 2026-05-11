package main.java.model.entity.skills.list.item.offensive;

import main.java.controller.CombatFlow;
import main.java.model.entity.items.EquipmentSlot;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.type.EquipmentType;
import main.java.model.type.SkillType;
import main.java.model.type.StatType;
import main.java.model.type.WeaponType;

public class Double_Slash extends Skill {

    public static String NAME = "Double Slash";

    public Double_Slash() {
        super();
        setDescription("เมื่อใช้ Katar ในมือทั้งสองข้าง CritChance จะเพิ่มขึ้นอีกเท่าตัว");
        setActionType("Passive");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.PHYSICAL);
        getPureTags().add(SkillType.CRITICAL);
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
        double katar_count = 0;
        for (EquipmentSlot slot : getUser().getEquipmentSlots().values()) {
            if (slot.getEquipmentType() != EquipmentType.WEAPON) continue;
            if (slot.getEquipment() == null) continue;
            if (slot.getEquipment().getWeaponType() != WeaponType.KATAR) continue;
            katar_count++;
        }
        if (katar_count >= 2) {
            getSkillModifier().getStatModifierSafe(StatType.CRITCHANCE).setGlobalMult(1);
        }
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {

    }

    @Override
    public String getName() {
        return NAME;
    }
}

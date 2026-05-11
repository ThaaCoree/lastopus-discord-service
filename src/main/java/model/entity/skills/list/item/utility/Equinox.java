package main.java.model.entity.skills.list.item.utility;

import main.java.controller.CombatFlow;
import main.java.model.entity.items.Equipment;
import main.java.model.entity.items.EquipmentSlot;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.type.SkillType;

import java.util.Map;

public class Equinox extends Skill {

    public static String NAME = "Equinox";

    public Equinox() {
        super();
        setDescription("เปลี่ยนให้อุปกรณ์ที่กำลังสวมใส่ทั้งหมดให้กลายเป็นขั้วตรงข้าม หากมันมีคู่ตรงข้ามของมัน");
        setActionType("Combine");
        setManaCost(0);
        setCooldown(0);
        getPureTags().add(SkillType.RESOURCE);
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
        for (Map.Entry<Integer, EquipmentSlot> entry : getUser().getEquipmentSlots().entrySet()) {
            if (entry.getValue().getEquipment().getName().equals("Clear Blue")) {
                Equipment equipment = (Equipment) combatFlow.getDatabase().getAllItemMap().get("Bright Red");
                entry.getValue().setEquipment(equipment);
            }

            if (entry.getValue().getEquipment().getName().equals("Bright Red")) {
                Equipment equipment = (Equipment) combatFlow.getDatabase().getAllItemMap().get("Clear Blue");
                entry.getValue().setEquipment(equipment);
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

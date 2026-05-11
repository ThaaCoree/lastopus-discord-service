package model.entity.skills.list.player;

import main.controller.CombatFlow;
import model.entity.skills.Skill;
import model.entity.skills.SkillInputSpec;
import model.entity.skills.SkillTarget;
import model.entity.skills.SkillMultiplier;
import model.modifier.BasicModifier;
import model.type.SkillType;
import model.type.StatType;

public class VaalGaze_of_Aerithra extends Skill {

    public static String NAME = "Vaal'Gaze of Aerithra";

    public VaalGaze_of_Aerithra() {
        super();
        setDescription("เลือกศัตรูหนึ่งเป้าหมาย ทำให้ได้รับสถานะ Ka´rahn การโจมตีเข้าใส่เป้าหมายที่ติดสถานะ Ka´rahn จะสร้างความเสียหายเพิ่มเติม XA หน่วย และเพิ่มขึ้นอีกตามระยะห่าง\n" +
                "เพิ่ม CritDamage XB โดยคิดจากค่า AGI");
        setActionType("Passive");
        setManaReservePercent(0.1);
        getSkillMultiplier().put("XA",new SkillMultiplier("(PATK+RATK)*0.3"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.MARK);
        getSkillMultiplier().get("XA").getTags().add(SkillType.FIGHTING_STYLE);

        getSkillMultiplier().put("XB",new SkillMultiplier("AGI*0.01"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.CRITICAL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XB").setPercent(true);

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
        BasicModifier modifier = new BasicModifier();
        modifier.setFlat(getSkillMultiplier().get("XB").getResult());
        getSkillModifier().getStatModifiers().put(StatType.CRITDAMAGE, modifier);
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

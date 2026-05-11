package main.java.model.entity.skills.list.player;

import main.java.controller.CombatFlow;
import main.java.controller.event.EventBus;
import main.java.controller.event.events.ActionEvent;
import main.java.model.entity.skills.Skill;
import main.java.model.entity.skills.SkillInputSpec;
import main.java.model.entity.skills.SkillTarget;
import main.java.model.entity.skills.SkillMultiplier;
import main.java.model.type.*;
import util.LogWriterUtil;

public class Mythic_Flow extends Skill {

    public static String NAME = "Mythic Flow";

    public Mythic_Flow() {
        super();
        setDescription("ได้รับ Counter [Feather Protect]\n" +
                "เมื่อใช้งานสกิลนี้ จะได้รับ Feather Protect XA สแต็ค และลดคูลดาวน์ของ Mythic Gleaming หนึ่งรอบเทิร์น\n" +
                "เมื่อพันธมิตรอื่นจู่โจม นกฮูกพลังงานสองตนจะปรากฏขึ้นแล้วพุ่งเข้าปะทะ สร้างความเสียหายเวทธาตุลม ธาตุน้ำ หรือธาตุแสงให้กับเป้าหมาย XB หน่วย\n" +
                "การใช้งานสกิลนี้ในระหว่างสถานะ 'ตอนจบของเรื่องราว' จะทำให้ได้รับ Feather Protect เพิ่มอีกหนึ่งสแต็ค");
        setActionType("Combine");
        setManaCost(8);
        setCooldown(2);
        setManaReservePercent(0.5);
        getSkillMultiplier().put("XA",new SkillMultiplier("2"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.LIMIT);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.35*MATK"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XB").getTags().add(SkillType.ELEMENTAL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.WIND);
        getSkillMultiplier().get("XB").getTags().add(SkillType.WATER);
        getSkillMultiplier().get("XB").getTags().add(SkillType.LIGHT);
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
        if (!getUser().getRawCounterMap().containsKey(CounterName.FEATHER_PROTECT)) {
            getUser().getRawCounterMap().put(CounterName.FEATHER_PROTECT,0.0);
            getUser().getCounter().put(CounterName.FEATHER_PROTECT,0.0);
        }
    }

    @Override
    public void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget) {
        double xa = getSkillMultiplier().get("XA").getResult();
        double xb = getSkillMultiplier().get("XB").getResult();
        int stories_end = 0;
        if (getUser().hasCondition("Stories' End")) {
            stories_end = 1;
        }
        int to_give = (int) xa + stories_end;
        for (int i = 0; i<to_give; i++) {
            getUser().counterIncrement(CounterName.FEATHER_PROTECT);
        }
        LogWriterUtil.log("Given "+(to_give)+" Feather Protect stacks to "+getUser().getName(), combatFlow.getTurnCount());

        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(0)))
                        .addActType(ActType.CAST)
                        .build()
        );
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ActionEvent.class, EventPhase.POST, 0, (ActionEvent event) -> {
            if (!event.hasActType(ActType.STRIKE)) return;
            if (event.unit_source == getUser()) return;

                if (isAlly(event.unit_source, combatFlow)) {
                    double counter = getUser().getCounter().get(CounterName.FEATHER_PROTECT);
                    double xb = getSkillMultiplier().get("XB").getResult();
                    if (counter >= 1) {
                        getUser().counterDecrement(CounterName.FEATHER_PROTECT);

                        sendActionEvent(combatFlow.getEventBus(),
                                ActionEvent.builder(getName(), getUser(), event.unit_target)
                                        .effect(ActionEffectType.DAMAGE_MAGICAL, xb, 1)
                                        .addActType(ActType.SKILL_TRIGGER, ActType.STRIKE)
                                        .build()
                        );
                        LogWriterUtil.log(">Mythic Flow triggered");
                    }
                }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

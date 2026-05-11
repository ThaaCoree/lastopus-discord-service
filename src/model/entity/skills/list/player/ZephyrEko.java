package model.entity.skills.list.player;

import main.controller.CombatFlow;
import main.controller.event.EventBus;
import main.controller.event.events.ActionEvent;
import main.controller.event.events.RoundEvent;
import model.entity.skills.*;
import model.type.CounterName;
import model.type.EventPhase;
import model.type.SkillType;

public class ZephyrEko extends Skill {

    public static String NAME = "Zephyr'Eko to Erytheia";

    public ZephyrEko() {
        super();
        setDescription("ไม่เกิน XB ครั้งต่อเทิร์น เมื่อสร้างความเสียหายด้วยการโจมตี จะได้รับสแต็ค Aelva´rind ในลักษณะขนนก เมื่อสะสมครบ XA สแต็ค จะลดคูลดาวน์ของสกิลทั้งหมดลงหนึ่งเทิร์น หากการโจมตีมีคุณสมบัติ Vah'ris ขณะที่มีสแต็คนี้ จะทำให้จำนวนที่ต้องสะสมสูงสุดลดลง 1 สแต็ค");
        setActionType("Passive");
        getPureTags().add(SkillType.RESOURCE);
        setManaReservePercent(0.550);

        getSkillMultiplier().put("XA",new SkillMultiplier("5"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.REQUIREMENT);

        getSkillMultiplier().put("XB",new SkillMultiplier("3"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.LIMIT);
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
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ActionEvent.class, EventPhase.POST,0 , event -> {
            int limit = (int) getSkillMultiplier().get("XB").getResult();
            if (event.unit_source == getUser() && getUser().getCounter().get(CounterName.AELVA_RIND_THIS_TURN) < limit) {
                getUser().counterIncrement(CounterName.AELVA_RIND);
                getUser().counterIncrement(CounterName.AELVA_RIND_THIS_TURN);
                int requirement = (int) getSkillMultiplier().get("XA").getResult();

                if (getUser().hasCondition("Vah'ris")) {
                    if (getUser().getCounter().get(CounterName.AELVA_RIND) >= requirement-1) {
                        for (SkillInstance instance : getUser().getAllSkill().values()) {
                            instance.cooldownDecrement();
                        }
                        getUser().counterSet(CounterName.AELVA_RIND, 0);
                    }
                }

                if (getUser().getCounter().get(CounterName.AELVA_RIND) >= requirement) {
                    for (SkillInstance instance : getUser().getAllSkill().values()) {
                        instance.cooldownDecrement();
                    }
                    getUser().counterSet(CounterName.AELVA_RIND, 0);
                }
            }
        });

        eventBus.register(RoundEvent.class, EventPhase.POST, 0, event -> {
            getUser().counterSet(CounterName.AELVA_RIND_THIS_TURN, 0);
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

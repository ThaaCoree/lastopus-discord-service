package main.java.model.entity.skills.list.player;

import main.java.controller.CombatFlow;
import main.java.controller.event.EventBus;
import main.java.controller.event.events.ActionEvent;
import main.java.controller.event.events.ResourceEvent;
import main.java.manager.ConditionManager;
import main.java.model.entity.Conditions;
import main.java.model.entity.skills.*;
import main.java.model.entity.units.Unit;
import main.java.model.type.*;
import util.LogWriterUtil;

import java.util.List;

public class Parabellum extends Skill implements SkillWithCondition {

    public static String NAME = "Parabellum";

    public Parabellum() {
        super();
        setDescription("Maelstorm ( ดาบใหญ่ ) :  ระเบิดออร่าแห่งโทสะ 2 เมตรรอบตัว สร้างความเสียหายกายภาพ XA หน่วย พร้อมผลักเป้าหมายในระยะให้กระเด็นออกไป 1 เมตร \n" +
                "Icarus ( ธนู ) : ชาร์จลูกศรแห่งความเย่อหยิ่ง เพิ่มอีก 1 เทิร์น เพิ่ม CritDamage XB เปลี่ยนความเสียหายนี้เป็นความเสียหายโดยตรง การโจมตีนี้จะทะลุเป้าหมายแรก และสร้างความเสียหาย 50% ใส่ทุกเป้าหมาย ที่อยู่ด้านหลังในระยะ 5 ช่องจากเป้าหมายแรกเป็นเส้นตรง\n" +
                "Geneva ( คฑา ) : ควบคุมโซ่แห่งราคะ ให้รัดเป้าหมาย 1 ตัว จาก 2 ตัว ลด MovementSpeed ของเป้าหมาย XC ก่อนที่จะสร้างความเสียหายเวท XD หน่วย \n" +
                "Revenos ( จักรราม ) : รวมจักรรามแห่งโลภะ ทั้งหมดที่มีในขณะนั้น สูงสุด 5 ชิ้น และโจมตีไปที่จุดเดียว เพิ่ม LUK XE ให้กับตนเอง ลด PDEF และ MDEF ของเป้าหมาย XF หน่วย\n" +
                "Titan ( โล่ ) : โล่แห่งเกียจคร้าน จะรับความเสียหายกายภาพและเวทมนตร์ลดลง XG จนกว่าจะจบรอบเทิร์น จากนั้นปลดปล่อยคลื่นกระแทกสร้างความเสียหายตามความเสียหาย XH และเพิ่มตามความเสียหายที่ได้รับ");
        setActionType("Combine");
        setManaCost(5);
        setCooldown(2);
        getSkillMultiplier().put("XA",new SkillMultiplier("(1.5*STR)+(0.5*MATK)"));
        getSkillMultiplier().get("XA").getTags().add(SkillType.PHYSICAL);
        getSkillMultiplier().get("XA").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XA").getTags().add(SkillType.AOE);

        getSkillMultiplier().put("XB",new SkillMultiplier("0.01*INT*(1+BuffAMP)"));
        getSkillMultiplier().get("XB").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XB").getTags().add(SkillType.SCALING);
        getSkillMultiplier().get("XB").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XB").setPercent(true);

        getSkillMultiplier().put("XC",new SkillMultiplier("0.15*(1+DebuffAMP)"));
        getSkillMultiplier().get("XC").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XC").getTags().add(SkillType.DEBUFF);
        getSkillMultiplier().get("XC").setPercent(true);

        getSkillMultiplier().put("XD",new SkillMultiplier("(2*WIS)+(0.55*MATK)"));
        getSkillMultiplier().get("XD").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XD").getTags().add(SkillType.STRIKE);

        getSkillMultiplier().put("XE",new SkillMultiplier("0.002*INT*(1+BuffAMP)"));
        getSkillMultiplier().get("XE").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XE").getTags().add(SkillType.BUFF);
        getSkillMultiplier().get("XE").setPercent(true);

        getSkillMultiplier().put("XF",new SkillMultiplier("1*INT*(1+DebuffAMP)"));
        getSkillMultiplier().get("XF").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XF").getTags().add(SkillType.DEBUFF);

        getSkillMultiplier().put("XG",new SkillMultiplier("0.5"));
        getSkillMultiplier().get("XG").getTags().add(SkillType.LIMIT);
        getSkillMultiplier().get("XG").setPercent(true);

        getSkillMultiplier().put("XH",new SkillMultiplier("(0.12*PDEF)+(0.12*MDEF)"));
        getSkillMultiplier().get("XH").getTags().add(SkillType.SPELL);
        getSkillMultiplier().get("XH").getTags().add(SkillType.STRIKE);
        getSkillMultiplier().get("XH").getTags().add(SkillType.DEFENSE);

    }

    @Override
    public SkillInputSpec getInputSpec(CombatFlow combatFlow) {
        List<String> divergent_choices = List.of("Maelstorm", "Icarus", "Geneva", "Revenos", "Titan");
        SkillInputSpec spec = new SkillInputSpec(combatFlow, getUser(), divergent_choices
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.CUSTOM, 0)
                , new SkillInputSpec.TargetConstruct(SkillInputSpec.TargetType.UNITS, 1)
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
        double matk = getUser().getStats().get(StatType.MAGICALATTACK).getFinal();
        double xa = getSkillMultiplier().get("XA").getResult();
        double xc = getSkillMultiplier().get("XC").getResult();
        double xd = getSkillMultiplier().get("XD").getResult();
        if (!skillTarget.getTarget(0).isEmpty()) {
            if (skillTarget.getTarget(0).contains("Maelstorm")) {
                Conditions condition = combatFlow.findCondition("Stun");
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(1)))
                                .effect(ActionEffectType.DAMAGE_PHYSICAL, xa, 1)
                                .addActType(ActType.ATTACK, ActType.STRIKE)
                                .condition(condition, 1)
                                .build()
                );
            }
        }

        if (skillTarget.getTarget(0).contains("Icarus")) {

            Conditions condition = combatFlow.findCondition("Icarus");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), getUser())
                            .addActType(ActType.CONDITION_GIVEN)
                            .condition(condition, 1)
                            .build()
            );
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(1)))
                            .effect(ActionEffectType.DAMAGE_MAGICAL, matk, 1)
                            .addActType(ActType.CAST, ActType.ATTACK, ActType.STRIKE)
                            .build()
            );
        }

        if (skillTarget.getTarget(0).contains("Geneva")) {
            for (String string : skillTarget.getTarget(1)) {
                Unit target = combatFlow.findUnit(string);
                target.sumRemainingMana(xc*-1);
                Conditions condition = combatFlow.findCondition("Geneva-Parabellum");
                sendActionEvent(combatFlow.getEventBus(),
                        ActionEvent.builder(getName(), getUser(), target)
                                .effect(ActionEffectType.DAMAGE_MAGICAL, xd, 1)
                                .addActType(ActType.CAST, ActType.STRIKE)
                                .condition(condition, 1)
                                .build()
                );
            }
        }

        if (skillTarget.getTarget(0).contains("Revenos")) {

            Conditions condition = combatFlow.findCondition("Revenos-Parabellum-Empower");
            Conditions condition_shred = combatFlow.findCondition("Revenos-Parabellum-Shred");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), getUser())
                            .addActType(ActType.CAST, ActType.CONDITION_GIVEN)
                            .condition(condition, 1)
                            .build()
            );
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), combatFlow.findUnit(skillTarget.getTarget(1)))
                            .effect(ActionEffectType.DAMAGE_MAGICAL, matk, 1)
                            .condition(condition_shred, 1)
                            .addActType(ActType.CAST, ActType.ATTACK, ActType.STRIKE)
                            .build()
            );
        }

        if (skillTarget.getTarget(0).contains("Titan")) {

            Conditions condition = combatFlow.findCondition("Titan");
            sendActionEvent(combatFlow.getEventBus(),
                    ActionEvent.builder(getName(), getUser(), getUser())
                            .addActType(ActType.CONDITION_GIVEN)
                            .condition(condition, 1)
                            .build()
            );
        }

    }

    @Override
    public void refreshCondition(CombatFlow combatFlow) {
        double buffAMP = 1+getUser().getStats().get(StatType.BUFFAMPLIFIER).getFinal();
        double debuffAMP = 1+getUser().getStats().get(StatType.DEBUFFAMPLIFIER).getFinal();
        Conditions icarus = new Conditions("Icarus-Parabellum");
        Conditions revenosEmpower = new Conditions("Revenos-Parabellum-Empower");
        Conditions geneva = new Conditions("Geneva-Parabellum");
        Conditions revenosShred = new Conditions("Revenos-Parabellum-Shred");
        Conditions titan = new Conditions("Titan-Parabellum");
        double xb = getSkillMultiplier().get("XB").getResult();
        double xc = getSkillMultiplier().get("XC").getResult();
        double xf = getSkillMultiplier().get("XF").getResult();
        double xe = getSkillMultiplier().get("XE").getResult();
        double xg = getSkillMultiplier().get("XG").getResult();
        icarus.getStatModifiers(StatType.CRITDAMAGE).setFlat(xb);
        geneva.getStatModifiers(StatType.MOVEMENTSPEED).setGlobalMult(xc*-1);
        revenosEmpower.getStatusModifiers(StatusType.LUCK).setGlobalMult(xe);
        revenosShred.getStatModifiers(StatType.PHYSICALDEFENSE).setFlat(xf*-1);
        revenosShred.getStatModifiers(StatType.MAGICALDEFENSE).setFlat(xf-1);
        titan.setDescription("รับความเสียหายกายภาพและเวทมนตร์น้อยลง "+xg*100+"%");

        icarus.setConditionType(ConditionType.BUFF);
        icarus.setConditionTierType(ConditionTierType.BOUND);
        revenosEmpower.setConditionType(ConditionType.BUFF);
        revenosEmpower.setConditionTierType(ConditionTierType.BOUND);
        revenosShred.setConditionType(ConditionType.DEBUFF);
        revenosShred.setConditionTierType(ConditionTierType.ADVANCED);
        geneva.setConditionType(ConditionType.DEBUFF);
        geneva.setConditionTierType(ConditionTierType.ADVANCED);
        titan.setConditionType(ConditionType.BUFF);
        titan.setConditionTierType(ConditionTierType.ADVANCED);

        //remove and re-add to database
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(icarus.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(icarus.getName(), icarus);
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(revenosEmpower.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(revenosEmpower.getName(), revenosEmpower);
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(revenosShred.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(revenosShred.getName(), revenosShred);
        combatFlow.getDatabase().getAllConditionMap().entrySet().removeIf(entry -> entry.getValue().getName().equals(geneva.getName()));
        combatFlow.getDatabase().getAllConditionMap().put(geneva.getName(), geneva);

        for (Unit unit : combatFlow.getAllUnit().values()) {
            ConditionManager.reapplyCondition(icarus, unit);
            ConditionManager.reapplyCondition(revenosEmpower, unit);
            ConditionManager.reapplyCondition(revenosShred, unit);
            ConditionManager.reapplyCondition(geneva, unit);
        }
    }

    @Override
    public void initializeEvent(CombatFlow combatFlow) {
        EventBus eventBus = combatFlow.getEventBus();
        eventBus.register(ResourceEvent.class, EventPhase.POST, 0, (ResourceEvent event) -> {
            if (!event.effectType.equals(ActionEffectType.DAMAGE_PHYSICAL)
                    && !event.effectType.equals(ActionEffectType.DAMAGE_MAGICAL)) return;
            if (!event.target.hasCondition("Titan-Parabellum")) return;

            double remaining = 1 - (getSkillMultiplier().get("xg").getResult());
            event.amount *= remaining;
            LogWriterUtil.log("Titan-Parabellum reduced damage taken");
        });
    }

    @Override
    public String getName() {
        return NAME;
    }
}

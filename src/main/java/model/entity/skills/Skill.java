package model.entity.skills;

import controller.CombatFlow;
import controller.event.EventBus;
import controller.event.events.ActionEvent;
import model.entity.*;
import model.entity.items.EquipmentSlot;
import model.entity.skills.list.player.Pride_Of_The_Blood_Pact;
import model.entity.units.Unit;
import model.type.*;
import util.FormulaEvaluatorUtil;
import util.LogWriterUtil;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public abstract class Skill {
    private Unit user;
    private Map<String, SkillMultiplier> skillMultiplier = new LinkedHashMap<>();
    private Set<SkillType> pureTags = new LinkedHashSet<>();
    private Set<SkillType> multiplierTags = new LinkedHashSet<>();
    private String actionType;
    private String description;
    private String translatedDesc;
    private String translatedCost;
    private String translatedTag;
    private String translatedCooldown;
    private ModifierBundle skillModifier = new ModifierBundle();
    private int cooldown;
    private double manaCost;
    private double healthCost;
    private double manaReservePercent;
    private double manaReserveFlat;
    private double healthReservePercent;
    private double healthReserveFlat;
    private boolean isActive;
    private boolean cooldown_can_change;
    private static DecimalFormat df = new DecimalFormat("#.##");

    public Skill() {
        user = null;
        manaCost = 0;
        healthCost = 0;
        description = "placeholder";
        actionType = "None";
        cooldown = 0;
        manaReserveFlat = 0;
        manaReservePercent = 0;
        healthReserveFlat = 0;
        healthReservePercent = 0;
        cooldown_can_change = true;
    }

    public void calculateAll() {
        applyMultiplierTagToSkill();
        calculateAllMultiplier();
        calculateExtra();
        calculateAllMultiplier();
        calculateCost();
        translateDescription();
        translateCost();
        translateTag();
        translateCooldown();
    }

    public void use(CombatFlow combatFlow, SkillTarget context, double mana_cost, double health_cost, int cooldown) {
        if (combatFlow != null) {
            LogWriterUtil.log("Skill "+getName()+" has been used!",combatFlow.getTurnCount());
            if (mana_cost != 0) {
                user.getMana().sumRemaining(-1 * mana_cost);
                LogWriterUtil.log("> Taking "+mana_cost+" mana", combatFlow.getTurnCount());
                if (user.getCounter().get(CounterName.USED_MANA) != null) {
                    user.counterSum(CounterName.USED_MANA ,mana_cost);
                }
            }
            if (health_cost != 0) {
                user.getHealth().sumRemaining(-1 * health_cost);
                LogWriterUtil.log("> Taking "+mana_cost+" health", combatFlow.getTurnCount());
            }
            if (cooldown > 0) {
                user.getAllSkill().get(getName()).setOnCooldown(cooldown);
            }
            calculateBehavior(combatFlow, context);
        }
    }
    public abstract void calculateBehavior(CombatFlow combatFlow, SkillTarget skillTarget);
    public abstract void calculateExtra();
    public abstract void initializeEvent(CombatFlow combatFlow);
    public abstract SkillInputSpec getInputSpec(CombatFlow combatFlow);
    public abstract String getName();

    public void calculateCost() {
        for (EquipmentSlot slot : user.getEquipmentSlots().values()) {
            if (slot.getEquipment() == null) continue;
            if (slot.getEquipment().getWeaponType() == WeaponType.MAGIC_ORB) {
                manaCost *= 2;
                healthCost *= 2;
                if (cooldown_can_change) {
                    cooldown -= 1;
                }
            }
            if (!user.hasActivatedSkill(Pride_Of_The_Blood_Pact.NAME)) {
                if (slot.getEquipment().getWeaponType() == WeaponType.GREATSWORD && cooldown_can_change) {
                    cooldown += 1;
                }
            }
        }

        for (PassiveNode node : user.getAllocatedPassives().values()) {
            if (node.getName().equals("Subtle Renovation")) {
                manaCost /= 2;
                if (cooldown_can_change) {
                    cooldown += 1;
                }
            }
        }

        for (UniqueModifier modifier : user.getUniqueModifier()) {
            if (modifier.getName() == UniqueType.FAIRY && modifier.isActive()) {
                manaCost /= 2;
            }
        }
    }

    public void translateDescription() {
        String replacedDescription = description;  // เก็บของเดิมไว้ก่อน
        for (char c = 'A'; c <= 'Z'; c++) {
            String key = "X" + c;
            if (skillMultiplier.get(key) != null) {
                replacedDescription = replacedDescription.replace(
                        key,
                        skillMultiplier.get(key).getResultString()
                );
            }
        }
        translatedDesc = replacedDescription;
    }

    public void translateCooldown() {
        if (cooldown <= 0) {
            translatedCooldown = "";
        } else {
            DecimalFormat df = new DecimalFormat("0.##");
            translatedCooldown = df.format(cooldown);
        }
    }

    public void translateTag() {
        Set<SkillType> mergedTags = new LinkedHashSet<>();
        mergedTags.addAll(pureTags);
        mergedTags.addAll(multiplierTags);

        translatedTag = mergedTags.stream()
                .map(SkillType::writeAsString)
                .collect(Collectors.joining(", "));
    }


    public void translateCost() {
        StringBuilder sb = new StringBuilder();
        if (manaCost != 0)
            sb.append(manaCost).append(" MP\n");
        if (healthCost != 0)
            sb.append(healthCost).append(" HP\n");
        if (manaReservePercent != 0)
            sb.append("Reserves ").append(manaReservePercent*100).append("% Mana");
        if (manaReserveFlat != 0)
            sb.append("Reserves ").append(manaReserveFlat).append(" Mana");
        if (healthReservePercent != 0)
            sb.append("Reserves ").append(healthReservePercent*100).append("% Health");
        if (healthReserveFlat != 0)
            sb.append("Reserves ").append(healthReserveFlat).append(" Health");

        translatedCost = sb.toString();
    }

    public void calculateAllMultiplier() {
        for (SkillMultiplier multiplier : skillMultiplier.values()) {
            if (multiplier.getFormula() != null && !Objects.equals(multiplier.getFormula(), "")) {
                multiplier.setResult(FormulaEvaluatorUtil.evaluateFormula(multiplier.getFormula(), user));
            }

            for (SkillType tag : SkillType.values()) {
                if (user.getFlatSkillModifiers().get(tag) != null) {
                    if (multiplier.isPercent()) {
                        if (multiplier.getTags().contains(tag))
                            multiplier.addResult(user.getFlatSkillModifiers().get(tag)/100);
                    } else {
                        if (multiplier.getTags().contains(tag))
                            multiplier.addResult(user.getFlatSkillModifiers().get(tag));
                    }
                }
                if (user.getMultSkillModifiers().get(tag) != null) {
                    if (multiplier.getTags().contains(tag))
                        multiplier.multResult(user.getMultSkillModifiers().get(tag)+1);
                }
            }
        }
    }

    public void applyMultiplierTagToSkill() {
        multiplierTags.clear();
        for (SkillMultiplier multiplier : skillMultiplier.values()) {
            if (multiplier.getTags() != null) {
            for (SkillType tag : multiplier.getTags()) {
                addMultiplierTag(tag);
            }
            }
        }
    }

    public void addMultiplierTag(SkillType tag) {
        multiplierTags.add(tag);
    }

    public Map<String, SkillMultiplier> getSkillMultiplier() {
        return skillMultiplier;
    }

    public void setSkillMultiplier(Map<String, SkillMultiplier> skillMultiplier) {
        this.skillMultiplier = skillMultiplier;
    }


    public void addConditionToDatabase(Conditions condition, CombatFlow combatFlow) {
        combatFlow.getDatabase().getAllConditionMap().put(condition.getName(), condition);
    }

    public static void sendActionEvent(EventBus eventBus, ActionEvent actionEvent) {
        eventBus.post(actionEvent, EventPhase.PRE);
        eventCritCalculate(actionEvent);
        eventBus.post(actionEvent, EventPhase.MODIFY);
        eventSendCalculate(actionEvent);
        eventBus.post(actionEvent, EventPhase.POST);
    }

    private static void eventCritCalculate(ActionEvent event) {
        for (Unit unit : event.unit_target) {
            int num = 0;
            double source_crit_chance = event.unit_source.getStats().get(StatType.CRITCHANCE).getFinal();
            double target_crit_shield = unit.getStats().get(StatType.CRITSHIELD).getFinal();

            if(event.canDamage(unit.getName())) {
                for (int i=1 ; i <= event.damage_times; i++) {

                    if (event.hasCritical(unit, i)) continue;

                    num = ThreadLocalRandom.current().nextInt(100)+1;
                    double after_crit_shield = source_crit_chance - target_crit_shield;
                    if (after_crit_shield * 100 >= num) {
                        event.putCritical(unit, true, i);
                    } else {
                        event.putCritical(unit, false, i);
                    }
                }
            }

            if(event.hasActType(ActType.HEAL)) {
                for (int i = 1; i <= event.heal_times; i++) {

                    if (event.hasCriticalHeal(unit, i)) continue;

                    num = ThreadLocalRandom.current().nextInt(100)+1;
                    double after_crit_shield = 0;
                    if (target_crit_shield < 0) {
                        after_crit_shield = source_crit_chance + target_crit_shield;
                    } else {
                        after_crit_shield = source_crit_chance;
                    }
                    if (after_crit_shield * 100 >= num) {
                        event.putCriticalHeal(unit, true, i);
                    } else {
                        event.putCriticalHeal(unit, false, i);
                    }
                }
            }
        }
    }

    private static void eventSendCalculate(ActionEvent event) {
        String source_event = event.event_source;
        Unit source_unit = event.unit_source;
        List<Unit> targets = event.unit_target;
        double source_unit_crit_damage = event.unit_source.getStats().get(StatType.CRITDAMAGE).getFinal()+1;

        for(Unit target : targets) {
            String name = target.getName();
            //heal
            if (event.getHeal(name) > 0) {
                double heal = 0;
                int loop = 1;
                StringBuilder display = new StringBuilder();
                display.append(target.getName())
                        .append(" took ");
                while (loop <= event.heal_times) {
                    double heal_per_loop = 0;

                    heal += event.getHealCritable(target, loop);
                    heal_per_loop += event.getHealCritable(target, loop);

                    if (event.isCriticalToUnit(target,loop)) {
                        heal += heal_per_loop - event.getHealCritable(target,loop);
                        display.append(df.format(heal_per_loop)).append(" [Critical]");

                    } else {
                        display.append(df.format(heal_per_loop));
                    }

                    loop++;
                    if (loop <= event.heal_times) {
                        display.append(", ");
                    }
                }
                display.append(" heal (sum ").append(df.format(heal)).append(" ) from ").append(source_event);
//                target.sumRemainingHealth(heal);
                LogWriterUtil.log(display.toString());
            }
            //recover mana
            if (event.getRecoverMana(name) > 0) {
                double recovery = 0;
                int loop = 1;
                StringBuilder display = new StringBuilder();
                display.append(target.getName())
                        .append(" took ");
                while (loop <= event.mana_recover_times) {
                    double recovery_per_loop = 0;

                    recovery += event.getRecoverMana(name);
                    recovery_per_loop += event.getRecoverMana(name);
                    display.append(df.format(recovery_per_loop));

                    loop++;
                    if (loop <= event.heal_times) {
                        display.append(", ");
                    }
                }
                display.append(" mana recovery (sum ").append(df.format(recovery)).append(" ) from ").append(source_event);
//                target.sumRemainingMana(recovery);
                LogWriterUtil.log(display.toString());
            }
            //damage
            for (DamageType type : DamageType.values()) {
                if (event.getDamage(type, name) > 0) {
                    double damage = 0;
                    int loop = 1;
                    StringBuilder display = new StringBuilder();
                    display.append(target.getName())
                            .append(" took ");
                    while (loop <= event.damage_times) {
                        double damage_per_loop = 0;

                        damage += calculateDamageAfterDEF(target, event.unit_source,
                                event.getDamage(type, name), type, event.extra_def, event.ignore_def);
                        damage_per_loop += calculateDamageAfterDEF(target, event.unit_source,
                                event.getDamage(type, name), type, event.extra_def, event.ignore_def);

                        if (event.isCriticalToUnit(target,loop)) {
                            damage_per_loop *= source_unit_crit_damage;
                            damage += damage_per_loop - calculateDamageAfterDEF(target, event.unit_source,
                                    event.getDamage(type, name), type, event.extra_def, event.ignore_def);
                            display.append(df.format(damage_per_loop)).append(" [Critical]");

                        } else {
                            display.append(df.format(damage_per_loop));
                        }
                        loop++;

                        if (loop <= event.damage_times) {
                            display.append(", ");
                        }
                    }
                    display.append(" ").append(type.writeAsString()).append(" damage (sum ").append(df.format(damage)).append(" ) from ").append(source_event);
//                    target.sumRemainingHealth(damage * -1);
                    LogWriterUtil.log(display.toString());

                    int num_eva = ThreadLocalRandom.current().nextInt(100) + 1; // 1 - 100
                    if (num_eva <= calculateEvasion(target, event.unit_source, 0, 0)) {
                        LogWriterUtil.log(">Dodgable");
                    } else if(num_eva <= calculateEvasion(target, event.unit_source, 0, 0)+15) {
                        LogWriterUtil.log(">Glancing");
                    }
                    int num_block = ThreadLocalRandom.current().nextInt(100) + 1; // 1 - 100
                    if (num_block <= calculateBlock(target, event.unit_source, damage, 0, 0, type)) {
                        LogWriterUtil.log(">Blockable");
                    }
                }
            }
            //debris
            if (event.getCreateDebris(name) > 0) {
                double debris = 0;
                int loop = 1;
                StringBuilder display = new StringBuilder();
                display.append(target.getName())
                        .append(" took ");
                while (loop <= event.debris_times) {
                    double debris_per_loop = 0;

                    debris += event.getCreateDebris(name);
                    debris_per_loop += event.getCreateDebris(name);
                    display.append(df.format(debris_per_loop));

                    loop++;
                    if (loop <= event.debris_times) {
                        display.append(", ");
                    }
                }
                display.append(" debris (sum ").append(df.format(debris)).append(" ) from ").append(source_event);
//                target.sumRemainingDebris(debris);
                LogWriterUtil.log(display.toString());
            }
        }
    }

    public static double calculateEvasion(Unit defenderUnit, Unit strikerUnit, double additionalEva, double additionalEvaMult) {
        double evasion = 0;
        double accuracy = 0;
        if (defenderUnit != null) {
            evasion = defenderUnit.getStats().get(StatType.EVASION).getFinal();
            evasion += additionalEva;
            evasion *= (1+additionalEvaMult);
        }
        if (strikerUnit != null) {
            accuracy = strikerUnit.getStats().get(StatType.ACCURACY).getFinal();
        }
        return (  1-(1.25*accuracy/(accuracy+Math.pow(evasion*0.82,0.9)))  )*100;
    }

    public static double calculateBlock(Unit defenderUnit, Unit strikerUnit, double final_damage, double additionalBlock, double additionalBlockMult, DamageType damageType) {
        double block = 0;
        double damage = final_damage;
        if (defenderUnit != null) {
            if (damageType.equals(DamageType.PHYSICAL)) {
                block = defenderUnit.getStats().get(StatType.PHYSICALBLOCK).getFinal();
            } else if (damageType.equals(DamageType.MAGICAL)) {
                block = defenderUnit.getStats().get(StatType.MAGICALBLOCK).getFinal();
            } else {
                block = defenderUnit.getStats().get(StatType.MAGICALBLOCK).getFinal()/2 + defenderUnit.getStats().get(StatType.PHYSICALBLOCK).getFinal()/2;
            }
            block += additionalBlock;
            block *= (1+additionalBlockMult);
        }

        return (  0.5 + (block - damage) / (2 * (block + damage))  )*100;
    }

    public static double calculateDamageAfterDEF(Unit defenderUnit, Unit strikerUnit, double damage, DamageType damageType, double extra_def, boolean ignore_def) {
//        AtomicDouble level_sum = new AtomicDouble(0);
//        AtomicInteger player_count = new AtomicInteger(0);
//        combatFlow.getPlayerUnit().forEach((key, value) -> {
//            level_sum.addAndGet(value.getLevel());
//            player_count.incrementAndGet();
//        });
        double level_avg = strikerUnit.getLevel();
        double rawDamage = damage;
        double pdef= 0;
        double mdef= 0;
        double dmgred= 0;
        double ppen= 0;
        double mpen= 0;
        double dmgamp= 0;
        if (strikerUnit != null) {
            ppen = strikerUnit.getStats().get(StatType.PHYSICALPENETRATE).getFinal();
            mpen = strikerUnit.getStats().get(StatType.MAGICALPENETRATE).getFinal();
            dmgamp = strikerUnit.getStats().get(StatType.DAMAGEAMPLIFIER).getFinal();
        }
        if (defenderUnit != null) {
            pdef = defenderUnit.getStats().get(StatType.PHYSICALDEFENSE).getFinal();
            mdef = defenderUnit.getStats().get(StatType.MAGICALDEFENSE).getFinal();
            dmgred = defenderUnit.getStats().get(StatType.DAMAGEREDUCTION).getFinal();
        }
        pdef -= ppen;
        mdef -= mpen;
        pdef += extra_def;
        mdef += extra_def;
        if (ignore_def) {
            pdef = 0;
            mdef = 0;
        }
        if (damageType.equals(DamageType.TRUE)) {
            dmgred = 0;
        }
        rawDamage *= (1+dmgamp);
        rawDamage *= (1-dmgred);
        if (defenderUnit != null && defenderUnit.hasCondition("Tremble")) {
            int x = defenderUnit.hasXCondition("Tremble");
            double extra = x * 0.25;
            rawDamage *= (1+extra);
        }
        double finaldmg = 0;
        double k = 100+(10*level_avg);
        double clampedPDef = Math.max(pdef, -k + 1e-6);
        double clampedMDef = Math.max(mdef, -k + 1e-6);
        double multiplier_physical = k/(k+clampedPDef);
        double multiplier_magical = k/(k+clampedMDef);
        multiplier_physical = Math.min(multiplier_physical, 2.0);
        multiplier_magical = Math.min(multiplier_magical, 2.0);
        if (damageType.equals(DamageType.PHYSICAL)) {
            finaldmg = rawDamage*multiplier_physical;
        } else if (damageType.equals(DamageType.MAGICAL)) {
            finaldmg = rawDamage*multiplier_magical;
        } else if (Double.isNaN(rawDamage) || rawDamage == 0) {
            finaldmg = 0;
        } else {
            finaldmg = Math.pow(rawDamage, 2) / rawDamage;
        }

        return finaldmg;
    }

    public List<Unit> getAllies(CombatFlow combatFlow) {
        List<Unit> to_return = new ArrayList<>();
        int party_number = combatFlow.findParty(getUser().getName());
        for(Unit unit : combatFlow.getAllUnit().values()) {
            if(combatFlow.isInParty(unit.getName(), party_number)) {
                to_return.add(unit);
            }
        }
        return to_return;
    }

    public List<Unit> getOtherAllies(CombatFlow combatFlow) {
        List<Unit> to_return = new ArrayList<>(getAllies(combatFlow));
        to_return.remove(getUser());
        return to_return;
    }

    public List<Unit> getEnemies(CombatFlow combatFlow) {
        List<Unit> to_return = new ArrayList<>();
        int party_number = combatFlow.findParty(getUser().getName());
        for(Unit unit : combatFlow.getAllUnit().values()) {
            if(!combatFlow.isInParty(unit.getName(), party_number) && combatFlow.hasParty(unit.getName())) {
                to_return.add(unit);
            }
        }
        return to_return;
    }
    public boolean isAlly(Unit target, CombatFlow combatFlow) {
        int target_party_number = combatFlow.findParty(target.getName());
        int user_party_number = combatFlow.findParty(user.getName());
        if (target_party_number == user_party_number) {
            return true;
        }
        return false;
    }

    public void sendSkillTriggerEvent(CombatFlow combatFlow, String log) {
        sendActionEvent(combatFlow.getEventBus(),
                ActionEvent.builder(getName(), getUser(), getUser())
                        .addActType(ActType.SKILL_TRIGGER)
                        .build()
        );
        LogWriterUtil.log(log);
    }

    public void addCounter(CounterName counterName) {
        if (getUser().getCounter() == null) return;
        if (!getUser().getRawCounterMap().containsKey(counterName)) {
            getUser().getRawCounterMap().put(counterName,0.0);
            getUser().getCounter().put(counterName,0.0);
        }
    }

    public double getManaCost() {
        return manaCost;
    }

    public void setManaCost(double manaCost) {
        this.manaCost = manaCost;
    }

    public double getHealthCost() {
        return healthCost;
    }

    public void setHealthCost(double healthCost) {
        this.healthCost = healthCost;
    }

    public double getManaReservePercent() {
        return manaReservePercent;
    }

    public void setManaReservePercent(double manaReservePercent) {
        this.manaReservePercent = manaReservePercent;
    }

    public double getManaReserveFlat() {
        return manaReserveFlat;
    }

    public void setManaReserveFlat(double manaReserveFlat) {
        this.manaReserveFlat = manaReserveFlat;
    }

    public double getHealthReservePercent() {
        return healthReservePercent;
    }

    public void setHealthReservePercent(double healthReservePercent) {
        this.healthReservePercent = healthReservePercent;
    }

    public double getHealthReserveFlat() {
        return healthReserveFlat;
    }

    public void setHealthReserveFlat(double healthReserveFlat) {
        this.healthReserveFlat = healthReserveFlat;
    }

    public Unit getUser() {
        return user;
    }

    public Set<SkillType> getMultiplierTags() {
        return multiplierTags;
    }

    public void setMultiplierTags(Set<SkillType> multiplierTags) {
        this.multiplierTags = multiplierTags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTranslatedDesc() {
        return translatedDesc;
    }

    public String getTranslatedCost() {
        return translatedCost;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public Set<SkillType> getPureTags() {
        return pureTags;
    }

    public ModifierBundle getSkillModifier() {
        return skillModifier;
    }

    public String getTranslatedTag() {
        return translatedTag;
    }

    public String getTranslatedCooldown() {
        return translatedCooldown;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setUser(Unit user) {
        this.user = user;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean cooldown_can_change() {
        return cooldown_can_change;
    }

    public void setCooldown_can_change(boolean cooldown_can_change) {
        this.cooldown_can_change = cooldown_can_change;
    }
}

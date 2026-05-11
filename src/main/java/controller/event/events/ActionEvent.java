package controller.event.events;

import model.entity.Conditions;
import model.entity.units.Unit;
import model.type.ActType;
import model.type.ActionEffectType;
import model.type.DamageType;
import model.type.StatType;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ActionEvent {
    public String event_source;
    public Unit unit_source;
    public List<Unit> unit_target = new ArrayList<>();
    public Map<String, List<ActionEffect>> effects = new LinkedHashMap<>();
    public Map<Integer, Map<Conditions, Integer>> condition_to_inflict = new LinkedHashMap<>();
    public List<ActType> act_type = new ArrayList<>();
    public Map<Integer, Map<Unit, Boolean>> critical = new LinkedHashMap<>();
    public Map<Integer, Map<Unit, Boolean>> critical_heal = new LinkedHashMap<>();
    public int damage_times = 0;
    public int heal_times = 0;
    public int debris_times = 0;
    public int mana_recover_times = 0;
    public Map<Integer, Map<String, Double>> condition_number_record = new LinkedHashMap<>();
    public boolean ignore_def = false;
    public double extra_def = 0;

    public ActionEvent(String event_source,Unit unit_source, Unit unit_target) {
        this.event_source = event_source;
        this.unit_source = unit_source;
        this.unit_target = List.of(unit_target);
    }

    public ActionEvent(String event_source,Unit unit_source, List<Unit> unit_target) {
        this.event_source = event_source;
        this.unit_source = unit_source;
        this.unit_target = unit_target;
    }

    public void doHeal(double amount) {
        for (Unit unit : unit_target) {
            effects.computeIfAbsent(unit.getName(), k -> new ArrayList<>())
                    .add(new ActionEffect(ActionEffectType.HEALTH_RECOVER, amount));
        }
    }

    public void doDamage(DamageType damageType, double amount) {
        for (Unit unit : unit_target) {
            List<ActionEffect> list = effects.computeIfAbsent(unit.getName(), k -> new ArrayList<>());

            switch (damageType) {
                case PHYSICAL -> list.add(new ActionEffect(ActionEffectType.DAMAGE_PHYSICAL, amount));
                case MAGICAL -> list.add(new ActionEffect(ActionEffectType.DAMAGE_MAGICAL, amount));
                case PURE -> list.add(new ActionEffect(ActionEffectType.DAMAGE_PURE, amount));
                case TRUE -> list.add(new ActionEffect(ActionEffectType.DAMAGE_TRUE, amount));
            }
        }
    }

    public void doCreateDebris(double amount) {
        for (Unit unit : unit_target) {
            effects.computeIfAbsent(unit.getName(), k -> new ArrayList<>())
                    .add(new ActionEffect(ActionEffectType.CREATE_DEBRIS, amount));
        }
    }

    public void doRecoverMana(double amount) {
        for (Unit unit : unit_target) {
            effects.computeIfAbsent(unit.getName(), k -> new ArrayList<>())
                    .add(new ActionEffect(ActionEffectType.MANA_RECOVER, amount));
        }
    }

    public void addFlatModifier(ActionEffectType effectType, double amount) {
        effects.forEach((name, list) -> {
            boolean found = false;

            for (ActionEffect actionEffect : list) {
                if (actionEffect.type == effectType) {
                    actionEffect.finalValue += amount;
                    found = true;
                }
            }
            // ถ้าไม่เจอ → สร้างใหม่
            if (!found) {
                ActionEffect newEffect = new ActionEffect(effectType, amount);
                list.add(newEffect);
            }
        });
    }

    public void addMultModifier(ActionEffectType effectType,double amount) {
        effects.forEach((name, list) -> {
            list.forEach(actionEffect -> {
                if (actionEffect.type == effectType) {
                    actionEffect.finalValue *= 1+amount;
                }
            });
        });
    }

    public void addOverrideModifier(ActionEffectType effectType,double amount) {
        effects.forEach((name, list) -> {
            list.forEach(actionEffect -> {
                if (actionEffect.type == effectType) {
                    actionEffect.finalValue = amount;
                }
            });
        });
    }

    public void addAllDamageMultModifier(double amount) {
        effects.forEach((name, list) -> {
            list.forEach(actionEffect -> {
                if (actionEffect.type == ActionEffectType.DAMAGE_PHYSICAL) {
                    actionEffect.finalValue *= 1+amount;
                }
                if (actionEffect.type == ActionEffectType.DAMAGE_MAGICAL) {
                    actionEffect.finalValue *= 1+amount;
                }
                if (actionEffect.type == ActionEffectType.DAMAGE_PURE) {
                    actionEffect.finalValue *= 1+amount;
                }
                if (actionEffect.type == ActionEffectType.DAMAGE_TRUE) {
                    actionEffect.finalValue *= 1+amount;
                }
            });
        });
    }

    public void addAllDamageOverrideModifier(double amount) {
        effects.forEach((name, list) -> {
            list.forEach(actionEffect -> {
                if (actionEffect.type == ActionEffectType.DAMAGE_PHYSICAL) {
                    actionEffect.finalValue = amount;
                }
                if (actionEffect.type == ActionEffectType.DAMAGE_MAGICAL) {
                    actionEffect.finalValue = amount;
                }
                if (actionEffect.type == ActionEffectType.DAMAGE_PURE) {
                    actionEffect.finalValue = amount;
                }
                if (actionEffect.type == ActionEffectType.DAMAGE_TRUE) {
                    actionEffect.finalValue = amount;
                }
            });
        });
    }

    public void addCondition(Conditions condition, int duration) {
        AtomicInteger key_to_put = new AtomicInteger(0);
        if (!condition_to_inflict.isEmpty()) {
            condition_to_inflict.forEach((key, value) -> {
                key_to_put.incrementAndGet();
            });
        }
        Map<Conditions, Integer> map = new LinkedHashMap<>();
        map.put(condition,duration);
        condition_to_inflict.put(key_to_put.get(), map);
    }

    public boolean hasTarget(String name) {
        for (Unit unit : unit_target) {
            if (unit.getName() == null) continue;
            if (unit.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasActType(ActType act) {
        for (ActType actType : act_type) {
            if (actType.equals(act)) {
                return true;
            }
        }
        return false;
    }

    public double getHeal(String name) {
        double amount = 0;
        for (ActionEffect effect : effects.get(name)) {
            if (effect.type.equals(ActionEffectType.HEALTH_RECOVER)) {
                amount += effect.finalValue;
            }
        }
        return amount;
    }

    public double getDamage(String name) {
        double amount = 0;
        if (effects.get(name) != null) {
            for (ActionEffect effect : effects.get(name)) {
                if (effect.type.equals(ActionEffectType.DAMAGE_PHYSICAL)) {
                    amount += effect.finalValue;
                }
                if (effect.type.equals(ActionEffectType.DAMAGE_MAGICAL)) {
                    amount += effect.finalValue;
                }
                if (effect.type.equals(ActionEffectType.DAMAGE_PURE)) {
                    amount += effect.finalValue;
                }
                if (effect.type.equals(ActionEffectType.DAMAGE_TRUE)) {
                    amount += effect.finalValue;
                }
            }
        }
        return amount;
    }

    public double getDamage(DamageType type, String name) {
        double amount = 0;
        for (ActionEffect effect : effects.get(name)) {
            if (type.equals(DamageType.PHYSICAL)) {
                if (effect.type.equals(ActionEffectType.DAMAGE_PHYSICAL)) {
                    amount += effect.finalValue;
                }
            }
            if (type.equals(DamageType.MAGICAL)) {
                if (effect.type.equals(ActionEffectType.DAMAGE_MAGICAL)) {
                    amount += effect.finalValue;
                }
            }
            if (type.equals(DamageType.PURE)) {
                if (effect.type.equals(ActionEffectType.DAMAGE_PURE)) {
                    amount += effect.finalValue;
                }
            }
            if (type.equals(DamageType.TRUE)) {
                if (effect.type.equals(ActionEffectType.DAMAGE_TRUE)) {
                    amount += effect.finalValue;
                }
            }
        }
        return amount;
    }

    public double getPhysicalDamage(String name) {
        return getDamage(DamageType.PHYSICAL, name);
    }

    public double getMagicalDamage(String name) {
        return getDamage(DamageType.MAGICAL, name);
    }

    public double getPureDamage(String name) {
        return getDamage(DamageType.PURE, name);
    }

    public double getTrueDamage(String name) {
        return getDamage(DamageType.TRUE, name);
    }

    public double getCreateDebris(String name) {
        double amount = 0;
        for (ActionEffect effect : effects.get(name)) {
            if (effect.type.equals(ActionEffectType.CREATE_DEBRIS)) {
                amount += effect.finalValue;
            }
        }
        return amount;
    }

    public double getRecoverMana(String name) {
        double amount = 0;
        for (ActionEffect effect : effects.get(name)) {
            if (effect.type.equals(ActionEffectType.MANA_RECOVER)) {
                amount += effect.finalValue;
            }
        }
        return amount;
    }

    public double getDamageCritable(DamageType type, Unit unit, int hit_number) {
        double damage = getDamage(type, unit.getName());
        if (critical.getOrDefault(hit_number, new LinkedHashMap<>()).getOrDefault(unit, false)) {
            damage *= unit_source.getStats().get(StatType.CRITDAMAGE).getFinal() + 1;
        }
        return damage;
    }

    public double getHealCritable(Unit unit, int hit_number) {
        double heal = getHeal(unit.getName());
        if (critical_heal.getOrDefault(hit_number, new LinkedHashMap<>()).getOrDefault(unit, false)) {
            heal *= unit_source.getStats().get(StatType.CRITDAMAGE).getFinal() + 1;
        }
        return heal;
    }

    public void putCritical(Unit unit, boolean isCrit, int hit_number) {
        Map<Unit, Boolean> map = critical.computeIfAbsent(hit_number, k -> new HashMap<>());
        map.put(unit, isCrit);
    }

    public void putCriticalHeal(Unit unit, boolean isCrit, int hit_number) {
        Map<Unit, Boolean> map = critical_heal.computeIfAbsent(hit_number, k -> new HashMap<>());
        map.put(unit, isCrit);
    }

    public boolean isCriticalToUnit(Unit unit, int hit_number) {
        return critical.getOrDefault(hit_number, Collections.emptyMap())
                .getOrDefault(unit, false);
    }

    public boolean isCriticalHealToUnit(Unit unit, int hit_number) {
        return critical_heal.getOrDefault(hit_number, Collections.emptyMap())
                .getOrDefault(unit, false);
    }

    public boolean hasCritical() {
        if (critical == null) return false;

        for (Map<?, Boolean> map : critical.values()) {
            if (map == null) continue;

            for (boolean bool : map.values()) {
                if (bool) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasCriticalHeal() {
        if (critical_heal == null) return false;

        for (Map<?, Boolean> map : critical_heal.values()) {
            if (map == null) continue;

            for (boolean bool : map.values()) {
                if (bool) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasCritical(Unit unit, int hitNumber) {
        return critical
                .getOrDefault(hitNumber, Collections.emptyMap())
                .containsKey(unit);
    }

    public boolean hasCriticalHeal(Unit unit, int hitNumber) {
        return critical_heal
                .getOrDefault(hitNumber, Collections.emptyMap())
                .containsKey(unit);
    }

    public void makeAllDamageCritical() {
        critical.forEach((key, value) -> {
            for (Unit target : value.keySet()) {
                value.put(target, true);
            }
        });
    }

    public void makeAllHealCritical() {
        critical_heal.forEach((key, value) -> {
            for (Unit target : value.keySet()) {
                value.put(target, true);
            }
        });
    }

    public boolean canDamage(String name) {
        return getDamage(name) > 0;
    }

    public boolean canRecoverMana(String name) {
        return getRecoverMana(name) > 0;
    }

    public boolean canHeal(String name) {
        return getHeal(name) > 0;
    }

    public boolean canCreateDebris(String name) {
        return getCreateDebris(name) > 0;
    }

    public boolean hasPhysicalDamage(String name) {
        if (effects.get(name) == null) return false;
        for (ActionEffect actionEffect : effects.get(name)) {
            if (actionEffect.type.equals(ActionEffectType.DAMAGE_PHYSICAL)) return true;
        }
        return false;
    }

    public boolean hasMagicalDamage(String name) {
        if (effects.get(name) == null) return false;
        for (ActionEffect actionEffect : effects.get(name)) {
            if (actionEffect.type.equals(ActionEffectType.DAMAGE_MAGICAL)) return true;
        }
        return false;
    }

    public boolean hasPureDamage(String name) {
        if (effects.get(name) == null) return false;
        for (ActionEffect actionEffect : effects.get(name)) {
            if (actionEffect.type.equals(ActionEffectType.DAMAGE_PURE)) return true;
        }
        return false;
    }

    public boolean hasTrueDamage(String name) {
        if (effects.get(name) == null) return false;
        for (ActionEffect actionEffect : effects.get(name)) {
            if (actionEffect.type.equals(ActionEffectType.DAMAGE_TRUE)) return true;
        }
        return false;
    }

    public static Builder builder(String source, Unit user, List<Unit> targets) {
        return new Builder(source, user, targets);
    }

    public static Builder builder(String source, Unit user, Unit target) {
        return new Builder(source, user, List.of(target));
    }

    public static Builder builder(String source, Unit user, Collection<Unit> targets) {
        return new Builder(source, user, targets);
    }

    public static class Builder {

        private String source;
        private Unit user;
        private List<Unit> targets;

        Map<ActionEffectType, Double> effects = new LinkedHashMap<>();
        public Map<Integer, Map<Conditions, Integer>> builders_condition = new LinkedHashMap<>();
        public List<ActType> builders_act_type = new ArrayList<>();
        public int damage_times = 0;
        public int heal_times = 0;
        public int debris_times = 0;
        public int mana_times = 0;
        public Map<Integer, Map<String, Double>> condition_number_record = new LinkedHashMap<>();

        public Builder(String source, Unit user, List<Unit> targets) {
            this.source = source;
            this.user = user;
            this.targets = targets;
        }

        public Builder(String source, Unit user, Collection<Unit> targets) {
            this.source = source;
            this.user = user;
            this.targets = new ArrayList<>(targets);
        }

        public Builder effect(ActionEffectType type, double amount, int times) {
            effects.merge(type, amount, Double::sum);
            switch (type) {
                case HEALTH_RECOVER -> this.heal_times = times;
                case DAMAGE_MAGICAL, DAMAGE_TRUE, DAMAGE_PHYSICAL, DAMAGE_PURE -> this.damage_times = times;
                case CREATE_DEBRIS -> this.debris_times = times;
                case MANA_RECOVER -> this.mana_times = times;
            }
            return this;
        }

        public Builder condition(Conditions condition, int duration, String key, Double value) {
            int index = builders_condition.size();

            Map<Conditions, Integer> conditionMap = new LinkedHashMap<>();
            conditionMap.put(condition, duration);
            builders_condition.put(index, conditionMap);

            if (key != null && value != null) {
                Map<String, Double> recordMap = new LinkedHashMap<>();
                recordMap.put(key, value);
                condition_number_record.put(index, recordMap);
            }

            return this;
        }

        public Builder condition(Conditions condition, int duration) {
            int key = builders_condition.size();

            Map<Conditions, Integer> map = new LinkedHashMap<>();
            map.put(condition, duration);

            builders_condition.put(key, map);
            return this;
        }

        public Builder conditionAddNumberRecord(int index, String key, double value) {
            Map<String, Double> map = new LinkedHashMap<>();
            map.put(key, value);
            condition_number_record.put(index, map);
            return this;
        }

        public Builder addActType(ActType... actTypes) {
            builders_act_type.addAll(Arrays.asList(actTypes));
            return this;
        }

        public ActionEvent build() {
            ActionEvent action = new ActionEvent(source, user, targets);

            for (Unit target : targets) {
                action.effects.put(target.getName(), new ArrayList<>());
            }

            for (Map.Entry<ActionEffectType, Double> entry : effects.entrySet()) {
                ActionEffectType type = entry.getKey();
                double value = entry.getValue();

                switch (type) {
                    case HEALTH_RECOVER -> action.doHeal(value);
                    case DAMAGE_PHYSICAL -> action.doDamage(DamageType.PHYSICAL, value);
                    case DAMAGE_MAGICAL -> action.doDamage(DamageType.MAGICAL, value);
                    case DAMAGE_PURE -> action.doDamage(DamageType.PURE, value);
                    case DAMAGE_TRUE -> action.doDamage(DamageType.TRUE, value);
                    case CREATE_DEBRIS -> action.doCreateDebris(value);
                    case MANA_RECOVER -> action.doRecoverMana(value);
                }
            }

            action.condition_to_inflict = this.builders_condition;
            action.act_type = this.builders_act_type;
            action.damage_times = this.damage_times;
            action.heal_times = this.heal_times;
            action.debris_times = this.debris_times;
            action.mana_recover_times = this.mana_times;
            action.condition_number_record = this.condition_number_record;

            return action;
        }
    }
}

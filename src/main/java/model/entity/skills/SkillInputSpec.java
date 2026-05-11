package model.entity.skills;

import controller.CombatFlow;
import model.entity.items.EquipmentSlot;
import model.entity.items.Item;
import model.entity.units.Unit;
import model.type.CounterName;
import model.type.StatType;
import model.type.StatusType;

import java.util.*;
import java.util.function.Function;

public class SkillInputSpec {

    private boolean perTarget;
    public Map<Integer, List<String>> targets = new LinkedHashMap<>();
    public Map<Integer, Map<Integer, InputField<?>>> fields = new LinkedHashMap<>();

    public SkillInputSpec(CombatFlow combatFlow, Unit user, TargetConstruct... targetConstructs) {
        Arrays.stream(targetConstructs).toList().forEach(construct -> {
            int index = construct.index;
            switch (construct.targetType) {
                case UNITS -> putUnitToList(index, combatFlow);
                case ALLIES -> putAllyToList(index, combatFlow, user);
                case ENEMIES -> putEnemyToList(index, combatFlow, user);
                case ITEMS -> putItemToList(index, user);
                case EQUIPMENTS -> putEquipmentToList(index, user);
                case SKILLS -> putSkillToList(index, user);
                case STATS -> putStatToList(index);
                case STATUSES -> putStatusToList(index);
                case COUNTER -> putCounterToList(index, user);
            }
        });
    }

    public SkillInputSpec(CombatFlow combatFlow, Unit user, List<String> choices, TargetConstruct... targetConstructs) {
        Arrays.stream(targetConstructs).toList().forEach(construct -> {
            int index = construct.index;
            switch (construct.targetType) {
                case UNITS -> putUnitToList(index, combatFlow);
                case ALLIES -> putAllyToList(index, combatFlow, user);
                case ENEMIES -> putEnemyToList(index, combatFlow, user);
                case ITEMS -> putItemToList(index, user);
                case EQUIPMENTS -> putEquipmentToList(index, user);
                case SKILLS -> putSkillToList(index, user);
                case STATS -> putStatToList(index);
                case STATUSES -> putStatusToList(index);
                case COUNTER -> putCounterToList(index, user);
                case CUSTOM -> putCustomToList(index, choices);
            }
        });
    }

    public boolean isPerTarget() {
        return perTarget;
    }

    public void setPerTarget(boolean perTarget) {
        this.perTarget = perTarget;
    }

    public List<String> getTargets(int index) {
        return targets.get(index);
    }

    public Map<Integer, Map<Integer, InputField<?>>> getFields() {
        return fields;
    }

    public SkillInputSpec addFields(InputField<?> field, int target_index, int input_index) {
        Map<Integer, InputField<?>> to_put = new LinkedHashMap<>();
        to_put.put(input_index, field);
        this.fields.put(target_index, to_put);
        return this;
    }

    public enum TargetType {
        UNITS,
        ALLIES,
        ENEMIES,
        ITEMS,
        EQUIPMENTS,
        SKILLS,
        STATS,
        STATUSES,
        COUNTER,
        CUSTOM
    }

    public static class TargetConstruct {
        TargetType targetType;
        int index;
        public TargetConstruct(TargetType type, int target_index) {
            targetType = type;
            this.index = target_index;
        }
    }

    private void putUnitToList(int index, CombatFlow combatFlow) {
        List<String> units = new ArrayList<>();
        combatFlow.getAllUnit().forEach((key, value) -> {
            units.add(value.getName());
        });
        targets.put(index, units);
    }

    private void putAllyToList(int index, CombatFlow combatFlow, Unit user) {
        List<String> allies = new ArrayList<>();
        int party_number = combatFlow.findParty(user.getName());
        for(Unit unit : combatFlow.getAllUnit().values()) {
            if(combatFlow.isInParty(unit.getName(), party_number)) {
                allies.add(unit.getName());
            }
        }
        targets.put(index, allies);
    }

    private void putEnemyToList(int index, CombatFlow combatFlow, Unit user) {
        List<String> enemies = new ArrayList<>();
        int party_number = combatFlow.findParty(user.getName());
        for(Unit unit : combatFlow.getAllUnit().values()) {
            if(!combatFlow.isInParty(unit.getName(), party_number) && combatFlow.hasParty(unit.getName())) {
                enemies.add(unit.getName());
            }
        }
        targets.put(index, enemies);
    }

    private void putItemToList(int index, Unit user) {
        List<String> items = new ArrayList<>();
        for(Item item : user.getInventoryItems().values()) {
            items.add(item.getName());
        }
        for(Item item : user.getBackpackItems().values()) {
            items.add(item.getName());
        }
        targets.put(index, items);
    }

    private void putEquipmentToList(int index, Unit user) {
        List<String> equipment = new ArrayList<>();
        for(EquipmentSlot slot : user.getEquipmentSlots().values()) {
            if (slot.getEquipment() == null) continue;
            equipment.add(slot.getEquipment().getName());
        }
        targets.put(index, equipment);
    }

    private void putSkillToList(int index, Unit user) {
        List<String> skills = new ArrayList<>();
        for(SkillInstance instance : user.getSkillList().values()) {
            if (instance.getSkillData() == null) continue;
            skills.add(instance.getSkillData().getName());
        }
        targets.put(index, skills);
    }

    private void putStatToList(int index) {
        List<String> stats = new ArrayList<>();
        for(StatType type : StatType.values()) {
            stats.add(type.writeAsString());
        }
        targets.put(index, stats);
    }

    private void putStatusToList(int index) {
        List<String> statuses = new ArrayList<>();
        for(StatusType type : StatusType.values()) {
            statuses.add(type.writeAsString());
        }
        targets.put(index, statuses);
    }

    private void putCounterToList(int index, Unit user) {
        List<String> counters = new ArrayList<>();
        for(CounterName counter : user.getCounter().keySet()) {
            counters.add(counter.writeAsString());
        }
        targets.put(index, counters);
    }

    private void putCustomToList(int index, Collection<String> choices) {
        List<String> customs = new ArrayList<>(choices);
        targets.put(index, customs);
    }

    public static class InputField<T> {
        Map<Integer, String> name = new LinkedHashMap<>();
        Map<Integer, InputType> type = new LinkedHashMap<>();
        public Map<Integer, Collection<T>> options = new LinkedHashMap<>();
        public Map<Integer, Function<T, String>> labelProvider = new LinkedHashMap<>();

        public InputField(String name, InputType type, int input_index) {
            this.name.put(input_index, name);
            this.type.put(input_index, type);
        }

        public InputField<T> options(Collection<T> options, int input_index) {
            this.options.put(input_index, options);
            return this;
        }

        public InputField<T> labelProvider(Function<T, String> labelProvider, int input_index) {
            this.labelProvider.put(input_index, labelProvider);
            return this;
        }

        public InputType getType(int input_index) {
            return type.get(input_index);
        }

        public void putType(InputType type, int input_index) {
            this.type.put(input_index, type);
        }

        public String getName(int input_index) {
            return name.get(input_index);
        }

        public void putName(String name, int input_index) {
            this.name.put(input_index, name);
        }

    }

    public enum InputType {
        NUMBER,
        BOOLEAN,
        SELECT
    }
}
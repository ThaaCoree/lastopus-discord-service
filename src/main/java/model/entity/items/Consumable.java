package model.entity.items;

import com.fasterxml.jackson.annotation.JsonTypeName;
import model.entity.TimedCondition;
import model.type.ItemType;
import model.type.ResourceType;

import java.util.*;

public class Consumable extends Item {
        private Map<ResourceType, Double> restoredFlat;
        private Map<ResourceType, Double> restoredPercent;
        private Map<ResourceType, Double> restoredMissingPercent;
        private List<TimedCondition> conditionsGiven;

        public Consumable(String name) {
            this.setName(name);
            setItemType(ItemType.NONE);
            setDescription("");
            setStatusDescription("");
            setLore("");
            setPrice("");
            restoredFlat = new HashMap<>();
            restoredPercent = new HashMap<>();
            restoredMissingPercent = new HashMap<>();
            for (ResourceType type : ResourceType.values()) {
                restoredFlat.put(type, 0.0);
                restoredPercent.put(type, 0.0);
                restoredMissingPercent.put(type, 0.0);
            }
            conditionsGiven = new ArrayList<>();
        }

        public Consumable() {
            setName("");
            setItemType(ItemType.NONE);
            setDescription("");
            setStatusDescription("");
            setLore("");
            setPrice("");
            restoredFlat = new LinkedHashMap<>();
            restoredPercent = new LinkedHashMap<>();
            restoredMissingPercent = new LinkedHashMap<>();
            for (ResourceType type : ResourceType.values()) {
                restoredFlat.put(type, 0.0);
                restoredPercent.put(type, 0.0);
                restoredMissingPercent.put(type, 0.0);
            }
            conditionsGiven = new ArrayList<>();
        }

        public Map<ResourceType, Double> getRestoredFlat() {
            return restoredFlat;
        }

        public void setRestoredFlat(Map<ResourceType, Double> restoredFlat) {
            this.restoredFlat = restoredFlat;
        }

        public Map<ResourceType, Double> getRestoredPercent() {
            return restoredPercent;
        }

        public void setRestoredPercent(Map<ResourceType, Double> restoredPercent) {
            this.restoredPercent = restoredPercent;
        }

        public Map<ResourceType, Double> getRestoredMissingPercent() {
            return restoredMissingPercent;
        }

        public void setRestoredMissingPercent(Map<ResourceType, Double> restoredMissingPercent) {
            this.restoredMissingPercent = restoredMissingPercent;
        }

        public List<TimedCondition> getConditionsGiven() {
            return conditionsGiven;
        }

        public void setConditionsGiven(List<TimedCondition> conditionsGiven) {
            this.conditionsGiven = conditionsGiven;
        }
    }

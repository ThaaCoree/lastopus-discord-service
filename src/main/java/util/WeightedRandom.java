package util;

import java.util.LinkedHashMap;
import java.util.Map;

public class WeightedRandom<T> {
    private Map<T, Double> weights = new LinkedHashMap<>();

    public void add(T item, double weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be > 0");
        }
        weights.put(item, weight);
    }

    public T roll() {
        if (weights.isEmpty()) {
            throw new IllegalStateException("No items to roll");
        }
        return weightedRandom(weights);
    }

    public static <T> T weightedRandom(Map<T, Double> weights) {
        double totalWeight = 0;

        for (double w : weights.values()) {
            totalWeight += w;
        }

        double random = Math.random() * totalWeight;

        double cumulative = 0;
        for (Map.Entry<T, Double> entry : weights.entrySet()) {
            cumulative += entry.getValue();

            if (random <= cumulative) {
                return entry.getKey();
            }
        }

        return null; // fallback (ปกติไม่ควรเกิด)
    }
}
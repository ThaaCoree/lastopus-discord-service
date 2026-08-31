package model.entity.items.crafted_equipments;

import model.entity.items.Item;
import model.type.StatTag;

import java.util.LinkedHashMap;
import java.util.Map;

public class CraftingMaterial extends Item {
    public int material_toughness;
    public int material_max_mod = 1;
    public boolean reveal_usage;
    public Map<String, CraftPoolEntry> pools = new LinkedHashMap<>();

    public Map<StatTag, Double> modBoost = new LinkedHashMap<>();

    //crate max mods from each material
    public boolean unique_boost;

    CraftingMaterial(int material_toughness, boolean unique_boost) {
        this.material_toughness = material_toughness;
        this.unique_boost = unique_boost;
    }

    CraftingMaterial() {

    }

    //for item mitigation
    public CraftingMaterial(Item item) {
        this.setName(item.getName());
        this.setItemType(item.getItemType());
        this.setDescription(item.getDescription());
        this.setLore(item.getLore());
        this.setPrice(item.getPrice());
        this.setPrice_in_copper(item.getPrice_in_copper());
        this.setStatusDescription(item.getStatusDescription());
        this.setWeight(item.getWeight());
        this.material_toughness = 1;
        this.unique_boost = false;
        this.material_max_mod = 1;
        this.reveal_usage = false;
    }

    public void insertModPool(CraftModPool pool, int min_tier, int max_tier, int mods_allowed) {
        //only insert pool from database
        CraftPoolEntry entry = new CraftPoolEntry(pool, min_tier, max_tier, mods_allowed);
        pools.put(pool.getPool_name(), entry);
    }

    public void removeModPool(CraftModPool pool) {
        pools.remove(pool);
    }

    public void insertBoost(StatTag tag, double multiplier) {
        modBoost.put(tag, multiplier);
    }

    public static class CraftPoolEntry {

        public CraftModPool pool;

        public int minTier;
        public int maxTier;

        public int maxModsAllowed;

        public CraftPoolEntry() {

        }

        public CraftPoolEntry(CraftModPool pool, int minTier, int maxTier, int maxModsAllowed) {
            this.pool = pool;
            this.minTier = minTier;
            this.maxTier = maxTier;
            this.maxModsAllowed = maxModsAllowed;
        }

        public CraftModPool getPool() {
            return pool;
        }

        public void setPool(CraftModPool pool) {
            this.pool = pool;
        }

        public int getMinTier() {
            return minTier;
        }

        public void setMinTier(int minTier) {
            this.minTier = minTier;
        }

        public int getMaxTier() {
            return maxTier;
        }

        public void setMaxTier(int maxTier) {
            this.maxTier = maxTier;
        }

        public int getMaxModsAllowed() {
            return maxModsAllowed;
        }

        public void setMaxModsAllowed(int maxModsAllowed) {
            this.maxModsAllowed = maxModsAllowed;
        }
    }

    public void createUsageDescription() {
        StringBuilder sb = new StringBuilder();
        if (!modBoost.isEmpty()) {
            sb.append("Boost : ");
            for (Map.Entry<StatTag, Double> entry : modBoost.entrySet()) {
                sb.append("\n").append(entry.getKey().writeAsString()).append(" (").append(entry.getValue() *100).append("%)");
            }
            sb.append("\n");
        }
        sb.append("\n").append("Material Toughness : ").append(material_toughness).append("%");
        setStatusDescription(sb.toString());
    }

    public int getMaterial_toughness() {
        return material_toughness;
    }

    public boolean isReveal_usage() {
        return reveal_usage;
    }

    public Map<String, CraftPoolEntry> getPools() {
        return pools;
    }

    public Map<StatTag, Double> getModBoost() {
        return modBoost;
    }

    public boolean isUnique_boost() {
        return unique_boost;
    }

    public void setMaterial_toughness(int material_toughness) {
        this.material_toughness = material_toughness;
    }

    public void setReveal_usage(boolean reveal_usage) {
        if (reveal_usage) {
            createUsageDescription();
        } else {
            setStatusDescription("");
        }
        this.reveal_usage = reveal_usage;
    }

    public void setPools(Map<String, CraftPoolEntry> pools) {
        this.pools = pools;
    }

    public void setModBoost(Map<StatTag, Double> modBoost) {
        this.modBoost = modBoost;
    }

    public void setUnique_boost(boolean unique_boost) {
        this.unique_boost = unique_boost;
    }

    public int getMaterial_max_mod() {
        return material_max_mod;
    }

    public void setMaterial_max_mod(int material_max_mod) {
        this.material_max_mod = material_max_mod;
    }
}

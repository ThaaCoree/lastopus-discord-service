package main.java.model.entity.units;

import main.java.model.entity.ResourceData;
import main.java.model.entity.items.Item;
import main.java.model.entity.ModifierBundle;
import main.java.model.entity.skills.SkillInstance;
import main.java.model.modifier.BasicModifier;
import main.java.model.modifier.ModValue;
import main.java.model.type.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class Monster extends Unit {

    private final ModifierBundle monsterModifier = new ModifierBundle();
    private final Map<Integer, String> opusMove;
    private MonsterType monsterType;
    private String monsterModify;
    private String behavior;
    private double soulCost;
    public Monster(String name) {
        super(name, UnitType.MONSTER);
        opusMove = new LinkedHashMap<>();
        monsterType = MonsterType.NORMAL;
        behavior = "";
        soulCost = 0;
        for (StatusType type : StatusType.values()) {
            monsterModifier.getStatusModifiers().put(type, new BasicModifier());
        }
        for (StatType type : StatType.values()) {
            monsterModifier.getStatModifiers().put(type, new BasicModifier());
        }
        for (Map.Entry<Integer, Item> entry : getInventoryItems().entrySet()) {
            entry.setValue(new Item("None"));
            getInventoryItemAmount().put(entry.getKey(), 0);
        }
    }
    public Monster() {
        super();
        opusMove = new LinkedHashMap<>();
        monsterType = MonsterType.NORMAL;
        behavior = "";
        soulCost = 0;
        for (StatusType type : StatusType.values()) {
            monsterModifier.getStatusModifiers().put(type, new BasicModifier());
        }
        for (StatType type : StatType.values()) {
            monsterModifier.getStatModifiers().put(type, new BasicModifier());
        }
        for (Map.Entry<Integer, Item> entry : getInventoryItems().entrySet()) {
            entry.setValue(new Item("None"));
            getInventoryItemAmount().put(entry.getKey(), 0);
        }
    }

    @Override
    public void calculateEverything() {
        getStatusCalculator().calculateBaseStatusFromRaisedStatuses();
        addMonsterStatusModifier();
        getStatusCalculator().applyBasicStatusModifiers();
        getStatusCalculator().applyBasicStatusWeaponPassive();
        getStatusCalculator().applyTransferStatusModifier();
        getStatusCalculator().applyConditionsStatusModifier();
        getStatusCalculator().applyOverrideStatusModifier();
        getStatusCalculator().limitHumanWeakStatus();
        getStatCalculator().calculateBaseStatsFromCurrentStatus();
        addMonsterStatModifier();
        getStatCalculator().applyBasicStatModifiers();
        getStatCalculator().applyBasicStatWeaponPassive();
        getStatCalculator().calculateManaRegen();
        getStatCalculator().applyTransferStatModifier();
        getStatCalculator().applyConditionsStatModifier();
        getStatCalculator().applyOverrideStatModifier();
        getStatCalculator().applyOverrideWeaponPassive();

        getSkillModifierManager().calculateSkillModifier();
        reloadSkill();
        calculateSkillDesc();

        getResourceManager().updateMax();
    }

    public void addMonsterStatusModifier() {
        Map<StatusType, BasicModifier> statusModify = monsterModifier.getStatusModifiers();
        for (Map.Entry<StatusType, BasicModifier> modifier : statusModify.entrySet()) {
            getStatuses().get(modifier.getKey()).addToBase(modifier.getValue().getFlat());
            double newBase = getStatuses().get(modifier.getKey()).getBase();
            getStatuses().get(modifier.getKey()).setCurrent(newBase);
        }

    }
    
    public void addMonsterStatModifier() {
        Map<StatType, BasicModifier> statModify = monsterModifier.getStatModifiers();
        for (Map.Entry<StatType, BasicModifier> modifier : statModify.entrySet()) {
            getStats().get(modifier.getKey()).addToBase(modifier.getValue().getFlat());
            double newBase = getStats().get(modifier.getKey()).getBase();
            getStats().get(modifier.getKey()).setCurrent(newBase);
        }
    }

    public Monster deepCopy() {
        Monster copy = new Monster();

        // ---------- copy จาก Unit ----------
        copy.setName(this.getName());
        copy.setInSession(this.isInSession());
        copy.setUnitType(this.getUnitType());
        copy.setLevel(this.getLevel());
        copy.setRemainingStatusPoint(this.getRemainingStatusPoint());
        copy.setRemainingPassiveTreePoint(this.getRemainingPassiveTreePoint());
        copy.setOpusName(this.getOpusName());
        copy.setOpusDescription(this.getOpusDescription());
        copy.setMixTwoHanded(this.isMixTwoHanded());
        copy.setMixTwoHandedMult(this.getMixTwoHandedMult());
        copy.setInventorySlot(this.getInventorySlot());

        copy.getUniqueModifier().addAll(this.getUniqueModifier()); // shallow
        copy.getConditionInstances().putAll(this.getConditionInstances()); // shallow
        copy.setStatuses(deepCopyStatusModValueMap(this.getStatuses()));
        copy.setStats(deepCopyStatModValueMap(this.getStats()));
        copy.setFlatSkillModifiers(new LinkedHashMap<>(this.getFlatSkillModifiers()));
        copy.setMultSkillModifiers(new LinkedHashMap<>(this.getMultSkillModifiers()));
        copy.getEquipmentSlots().putAll(this.getEquipmentSlots());
        copy.getAllocatedPassives().putAll(this.getAllocatedPassives());
        copy.getRaisedStatuses().putAll(this.getRaisedStatuses());
        copy.setRace(this.getRace()); // ถ้ามี method copy

        Map<ResourceType, ResourceData> newRes = new LinkedHashMap<>();
        for (var e : this.getResources().entrySet()) {
            newRes.put(e.getKey(), e.getValue().copy()); // 🔥 ต้องมี copy()
        }
        copy.setResources(newRes);

        copy.getCard().putAll(this.getCard());
        copy.getInventoryItems().putAll(this.getInventoryItems());
        copy.getInventoryItemAmount().putAll(this.getInventoryItemAmount());

        Map<String, SkillInstance> newSkillList = new LinkedHashMap<>();
        for (var e : this.getSkillList().entrySet()) {
            SkillInstance s = e.getValue().deepcopy();
            newSkillList.put(e.getKey(), s);
        }

        copy.setSkillList(newSkillList);

        copy.getRawCounterMap().putAll(this.getRawCounterMap());
        copy.getSummons().putAll(this.getSummons());

        // ---------- copy ของ Monster เอง ----------
        copy.monsterModifier.setAll(this.monsterModifier);
        copy.opusMove.putAll(this.opusMove);
        copy.monsterType = this.monsterType;
        copy.monsterModify = this.monsterModify;
        copy.behavior = this.behavior;
        copy.soulCost = this.soulCost;

        return copy;
    }

    private Map<StatusType, ModValue> deepCopyStatusModValueMap(Map<StatusType, ModValue> original) {
        Map<StatusType, ModValue> copy = new LinkedHashMap<>();
        for (Map.Entry<StatusType, ModValue> entry : original.entrySet()) {
            copy.put(entry.getKey(), entry.getValue().copy()); // ถ้า ModValue มี method copy()
        }
        return copy;
    }

    private Map<StatType, ModValue> deepCopyStatModValueMap(Map<StatType, ModValue> original) {
        Map<StatType, ModValue> copy = new LinkedHashMap<>();
        for (Map.Entry<StatType, ModValue> entry : original.entrySet()) {
            copy.put(entry.getKey(), entry.getValue().copy()); // ถ้า ModValue มี method copy()
        }
        return copy;
    }

    public ModifierBundle getMonsterModifier() {
        return monsterModifier;
    }

    public MonsterType getMonsterType() {
        return monsterType;
    }

    public Map<Integer, String> getOpusMove() {
        return opusMove;
    }

    public String getBehavior() {
        return behavior;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }

    public double getSoulCost() {
        return soulCost;
    }

    public void setSoulCost(double soulCost) {
        this.soulCost = soulCost;
    }

    public String getMonsterModify() {
        return monsterModify;
    }

    public void setMonsterModify(String monsterModify) {
        this.monsterModify = monsterModify;
    }

    public void setMonsterType(MonsterType monsterType) {
        this.monsterType = monsterType;
    }
}

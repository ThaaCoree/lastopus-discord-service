package app.servicemodel;

import model.entity.Card;
import model.entity.Conditions;
import model.entity.PassiveNode;
import model.entity.Shop;
import model.entity.items.*;
import model.entity.units.Monster;
import model.entity.units.Unit;

import java.util.Map;

public class SaveRequest {
    private Map<String, Item> allItemMap;
    private Map<String, Equipment> allEquipmentMap;
    private Map<String, Consumable> allConsumableMap;
    private Map<String, Dream> allDreamItem;
    private Map<String, PassiveNode> allDream;
    private Map<String, Rune> allRuneMap;
    private Map<String, Unit> allPlayerMap;
    private Map<String, Unit> allNPCMap;
    private Map<String, Monster> allMonsterMap;
    private Map<String, Conditions> allConditionMap;
    private Map<String, Card> allCardMap;
    private Map<Integer, PassiveNode> allPassiveMap;
    private Map<String, Shop> allShop;
    private Map<String, Unit> allUnit;

    public SaveRequest() {
    }

    public Map<String, Unit> getAllUnit() {
        return allUnit;
    }

    public void setAllUnit(Map<String, Unit> allUnit) {
        this.allUnit = allUnit;
    }

    public Map<String, Shop> getAllShop() {
        return allShop;
    }

    public void setAllShop(Map<String, Shop> allShop) {
        this.allShop = allShop;
    }

    public Map<Integer, PassiveNode> getAllPassiveMap() {
        return allPassiveMap;
    }

    public void setAllPassiveMap(Map<Integer, PassiveNode> allPassiveMap) {
        this.allPassiveMap = allPassiveMap;
    }

    public Map<String, Card> getAllCardMap() {
        return allCardMap;
    }

    public void setAllCardMap(Map<String, Card> allCardMap) {
        this.allCardMap = allCardMap;
    }

    public Map<String, Conditions> getAllConditionMap() {
        return allConditionMap;
    }

    public void setAllConditionMap(Map<String, Conditions> allConditionMap) {
        this.allConditionMap = allConditionMap;
    }

    public Map<String, Monster> getAllMonsterMap() {
        return allMonsterMap;
    }

    public void setAllMonsterMap(Map<String, Monster> allMonsterMap) {
        this.allMonsterMap = allMonsterMap;
    }

    public Map<String, Unit> getAllNPCMap() {
        return allNPCMap;
    }

    public void setAllNPCMap(Map<String, Unit> allNPCMap) {
        this.allNPCMap = allNPCMap;
    }

    public Map<String, Unit> getAllPlayerMap() {
        return allPlayerMap;
    }

    public void setAllPlayerMap(Map<String, Unit> allPlayerMap) {
        this.allPlayerMap = allPlayerMap;
    }

    public Map<String, Rune> getAllRuneMap() {
        return allRuneMap;
    }

    public void setAllRuneMap(Map<String, Rune> allRuneMap) {
        this.allRuneMap = allRuneMap;
    }

    public Map<String, PassiveNode> getAllDream() {
        return allDream;
    }

    public void setAllDream(Map<String, PassiveNode> allDream) {
        this.allDream = allDream;
    }

    public Map<String, Dream> getAllDreamItem() {
        return allDreamItem;
    }

    public void setAllDreamItem(Map<String, Dream> allDreamItem) {
        this.allDreamItem = allDreamItem;
    }

    public Map<String, Consumable> getAllConsumableMap() {
        return allConsumableMap;
    }

    public void setAllConsumableMap(Map<String, Consumable> allConsumableMap) {
        this.allConsumableMap = allConsumableMap;
    }

    public Map<String, Equipment> getAllEquipmentMap() {
        return allEquipmentMap;
    }

    public void setAllEquipmentMap(Map<String, Equipment> allEquipmentMap) {
        this.allEquipmentMap = allEquipmentMap;
    }

    public Map<String, Item> getAllItemMap() {
        return allItemMap;
    }

    public void setAllItemMap(Map<String, Item> allItemMap) {
        this.allItemMap = allItemMap;
    }
}

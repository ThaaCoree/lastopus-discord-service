package controller;

import app.Database;
import com.fasterxml.jackson.annotation.JsonIgnore;
import controller.event.EventBus;
import controller.event.events.RoundEvent;
import model.entity.ConditionInstance;
import model.entity.Conditions;
import model.entity.items.Item;
import model.entity.skills.SkillInstance;
import model.entity.units.Monster;
import model.entity.units.Summon;
import model.entity.units.Unit;
import model.type.EventPhase;
import model.type.UnitType;
import util.LogWriterUtil;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class CombatFlow {
    @JsonIgnore
    private Database database;
    @JsonIgnore
    private final EventBus eventBus = new EventBus();
    private final Set<String> allUnitNameList = new LinkedHashSet<>();
    private int turnCount;
    @JsonIgnore
    private final Map<String, Unit> allUnit = new LinkedHashMap<>();
    @JsonIgnore
    private final Map<String, Unit> playerUnit = new LinkedHashMap<>();
    @JsonIgnore
    private final Map<String, Summon> summonUnit = new LinkedHashMap<>();
    private final Map<Integer, List<String>> parties = new LinkedHashMap<>();
    private int extraDice;
    private int critDice;
    private int avoidDice;
    private final Map<String, Monster> monsterUnit = new LinkedHashMap<>();
    private final Map<String, Double> damageTaken = new LinkedHashMap<>();
    private final Map<String, Double> healTaken = new LinkedHashMap<>();
    private final Map<String, Boolean> isCopy = new LinkedHashMap<>();
    private String selectedPlayer = "";
    private String selectedMonster = "";
    private List<Runnable> onResetListeners = new ArrayList<>();

    public CombatFlow(Database database) {
        this.database = database;
    }

    public CombatFlow() {
    }

    public void oneTurnPassed() {
        allUnitUpdate();
    }

    public void oneRoundPassed() {
        eventBus.post(new RoundEvent(turnCount,1), EventPhase.PRE);
        eventBus.post(new RoundEvent(turnCount,1), EventPhase.MODIFY);
        turnCount++;
        LogWriterUtil.log("One Round has passed! starting round "+turnCount, turnCount);
        for (Unit unit : allUnit.values()) {
            for (SkillInstance instance : unit.getAllSkill().values()) {
                instance.cooldownDecrement();
            }
            List<Integer> conditionsToRemove = new ArrayList<>();
            for (Map.Entry<Integer, ConditionInstance> entry : unit.getConditionInstances().entrySet()) {
                ConditionInstance instance = entry.getValue();
                instance.sumAppliedTime(1);
                if (instance.isExpired()) {
                    LogWriterUtil.log("Condition "+instance.getCondition().getName()+" on "+unit.getName()+" has expired", turnCount);
                    conditionsToRemove.add(entry.getKey());
                }
            }
            for (Integer key : conditionsToRemove) {
                unit.getConditionInstances().remove(key);
            }
        }
        eventBus.post(new RoundEvent(turnCount,1), EventPhase.POST);
        allUnitUpdate();
    }

    public void oneRoundRewind() {
        turnCount--;
        LogWriterUtil.log("One Round rewinded! starting round"+turnCount);
        allUnitUpdate();
    }

    public void initCombatFlow() {
        linkAllUnitToDatabase();
        mapAllUnitToAll();
        allUnitUpdate();
        initMonsterUnitCounter();
        setSessionInfo();
    }

    public void addUnitToFlow(Unit unit, boolean copy) {
        if (copy) {
            String newName = "";
            int num = 1;
            while (monsterUnit.containsKey(unit.getName()+newName)) {
                num++;
                newName = Integer.toString(num);
            }
            String key = unit.getName()+newName;
            Monster copied = ((Monster) unit).deepCopy();
            copied.setName(key);
            monsterUnit.put(key, copied);
            damageTaken.put(key, 0.0);
            healTaken.put(key, 0.0);
            isCopy.put(key, true);
            allUnitNameList.add(key);
        } else {
            if (unit instanceof Monster) {
                monsterUnit.put(unit.getName(), (Monster) unit);
                damageTaken.put(unit.getName(), 0.0);
                healTaken.put(unit.getName(), 0.0);
                isCopy.put(unit.getName(), false);
            }
            allUnitNameList.add(unit.getName());
        }
        initCombatFlow();
    }

    public void removeUnitFromFlow(String name) {
        Unit unit = allUnit.get(name);
        if (unit == null) return;
        for (SkillInstance instance : unit.getAllSkill().values()) {
            instance.setOnCooldown(0);
        }
        allUnitNameList.remove(name);
        monsterUnit.remove(name);
        damageTaken.remove(name);
        healTaken.remove(name);
        isCopy.remove(name);
        initCombatFlow();
    }

    public void linkAllUnitToDatabase() {
        allUnit.clear();
        for (String name : allUnitNameList) {
            if (!(database.getAllUnit().get(name) instanceof Monster) && database.getAllUnit().get(name) != null) {
                allUnit.put(name, database.getAllUnit().get(name));
            } else {
                if (Boolean.TRUE.equals(isCopy.get(name))) {
                    Unit monster = monsterUnit.get(name);
                    if (monster == null) continue;
                    allUnit.put(name, monster);
                } else {
                    Unit u = database.getAllUnit().get(name);
                    allUnit.put(name, u);
                }
            }
        }
    }

    public void mapAllUnitToAll() {
        playerUnit.clear();
        summonUnit.clear();

        for (Unit unit : allUnit.values()) {
            if (unit.getUnitType() == UnitType.PLAYER || unit.getUnitType() == UnitType.NPC) {
                playerUnit.put(unit.getName(), unit);
            }
            if (unit.getUnitType() == UnitType.SUMMON) {
                summonUnit.put(unit.getName(), (Summon) unit);
            }
        }
    }

    public void allUnitUpdate() {
        eventBus.getListeners().clear();
        for (Unit unit : allUnit.values()) {
            unit.calculateEverything();
            unit.initSkill(this);
        }
        randAllDices();

        for (Runnable r : onResetListeners) {
            r.run();
        }
    }

    public void addOnResetListener(Runnable r) {
        onResetListeners.add(r);
    }

    public void resetCombatFlow() {
        turnCount = 1;
        allUnitUpdate();
        System.out.println("CombatFlow has been reset!");
    }

    public void setSessionInfo() {
        for(Unit unit : database.getAllUnit().values()) {
            if (unit.isPlayer() || unit.isNpc()) {
                unit.setInSession(false);
            }
        }
        for(Unit unit : allUnit.values()) {
            if (unit.isPlayer() || unit.isNpc()) {
                unit.setInSession(true);
            }
        }
    }

    public void initMonsterUnitCounter() {
        for (Unit unit : monsterUnit.values()) {
            unit.initCounter();
        }
    }

    public void addToParty(Unit unit, int party_number) {
        parties.get(party_number).add(unit.getName());
    }

    public void removeFromParty(Unit unit, int party_number) {
        parties.get(party_number).remove(unit.getName());
    }

    public boolean isInParty(String to_find, int party_number) {
        if (party_number == 0) {
            return false;
        }
        for (String name : parties.get(party_number)) {
            if (to_find.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public int findParty(String to_find) {
        for (Map.Entry<Integer, List<String>> entry : parties.entrySet()) {
            for (String name : entry.getValue()) {
                if (to_find.equals(name)) {
                    return entry.getKey();
                }
            }
        }
        return 0;
    }

    public boolean hasParty(String to_find) {
        for (List<String> list : parties.values()) {
            for (String name : list) {
                if (to_find.equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Unit findUnit(String name) {
        for (Unit unit : allUnit.values()) {
            if (unit.getName().equals(name)) {
                return unit;
            }
        }
        return null;
    }

    public List<Unit> findUnit(Collection<String> names) {
        List<Unit> to_return = new ArrayList<>();
        for (String name : names) {
            for (Unit unit : allUnit.values()) {
                if (unit.getName().equals(name)) {
                    to_return.add(unit);
                }
            }
        }
        return to_return;
    }

    public Item findItem(String unit_name, String item_name) {
        for (Unit unit : allUnit.values()) {
            if (unit.getName().equals(unit_name)) {
                return unit.findItem(item_name);
            }
        }
        return null;
    }

    public Conditions findCondition(String name) {
        return getDatabase().getAllConditionMap().get(name);
    }

    public SkillInstance findSkill(String unit_name, String skill_name) {
        for (Unit unit : allUnit.values()) {
            if (unit.getName().equals(unit_name)) {
                return unit.findSkill(skill_name);
            }
        }
        return null;
    }

    public void registerDatabase(Database database) {
        this.database = database;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public Map<String, Unit> getPlayerUnit() {
        return playerUnit;
    }

    public Map<String, Monster> getMonsterUnit() {
        return monsterUnit;
    }

    public Map<String, Unit> getAllUnit() {
        return allUnit;
    }

    public Database getDatabase() {
        return database;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void randAllDices() {
        randExtraDice();
        randCritDice();
        randAvoidDice();
    }

    public void randExtraDice() {
        extraDice = 1+ ThreadLocalRandom.current().nextInt(100);
    }

    public void randCritDice() {
        critDice = 1+ ThreadLocalRandom.current().nextInt(100);
    }

    public void randAvoidDice() {
        avoidDice = 1+ ThreadLocalRandom.current().nextInt(100);
    }

    public int getExtraDice() {
        return extraDice;
    }

    public int getCritDice() {
        return critDice;
    }

    public int getAvoidDice() {
        return avoidDice;
    }

    public Set<String> getAllUnitNameList() {
        return allUnitNameList;
    }

    public Map<String, Summon> getSummonUnit() {
        return summonUnit;
    }

    public Map<String, Double> getDamageTaken() {
        return damageTaken;
    }

    public Map<String, Double> getHealTaken() {
        return healTaken;
    }

    public Map<String, Boolean> getIsCopy() {
        return isCopy;
    }

    public String getSelectedPlayer() {
        return selectedPlayer;
    }

    public void setSelectedPlayer(String selectedPlayer) {
        this.selectedPlayer = selectedPlayer;
    }

    public String getSelectedMonster() {
        return selectedMonster;
    }

    public void setSelectedMonster(String selectedMonster) {
        this.selectedMonster = selectedMonster;
    }

    public Map<Integer, List<String>> getParties() {
        return parties;
    }
}

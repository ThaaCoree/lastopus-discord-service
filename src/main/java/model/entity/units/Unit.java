package model.entity.units;
import calculator.StatCalculator;
import calculator.StatusCalculator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.sheets.v4.model.Request;
import com.google.common.util.concurrent.AtomicDouble;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import controller.CombatFlow;
import factory.SkillFactory;
import manager.*;
import model.entity.*;
import model.entity.items.Equipment;
import model.entity.items.EquipmentSlot;
import model.entity.items.Item;
import model.entity.items.Rune;
import model.entity.skills.SkillInstance;
import model.entity.skills.SkillWithCondition;
import model.modifier.ModValue;
import model.type.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import util.GoogleSheetsUtil;
import util.LogWriterUtil;
import util.StatTranslateUtil;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Document(collection = "units")
public class Unit {
    @Id
    private String name;

    private boolean inSession;
    private UnitType unitType;
    private int level;
    private int remainingStatusPoint;
    private int remainingPassiveTreePoint;
    private String opusName = "";
    private String opusDescription = "";
    private boolean mixTwoHanded;
    private double mixTwoHandedMult = 0.66;
    private int inventorySlot = 9;
    private int backpackSlot = 0;
    private List<UniqueModifier> uniqueModifier = new ArrayList<>();
    private Map<Integer, ConditionInstance> conditionInstances = new LinkedHashMap<>();
    private Map<StatusType, ModValue> statuses = new LinkedHashMap<>();
    private Map<StatType, ModValue> stats = new LinkedHashMap<>();
    private Map<SkillType, Double> flatSkillModifiers = new LinkedHashMap<>();
    private Map<SkillType, Double> multSkillModifiers = new LinkedHashMap<>();
    private Map<Integer, EquipmentSlot> equipmentSlots = new LinkedHashMap<>();
    private Map<Integer, PassiveNode> allocatedPassives = new LinkedHashMap<>();
    private Map<StatusType, Integer> raisedStatuses = new LinkedHashMap<>();
    private Race race = new Race();
    private Map<ResourceType, ResourceData> resources = new LinkedHashMap<>();
    private Map<CardType, Card> card = new LinkedHashMap<>();
    private Map<Integer, Item> inventoryItems = new LinkedHashMap<>();
    private Map<Integer, Integer> inventoryItemAmount = new LinkedHashMap<>();
    private Map<Integer, Item> backpackItems = new LinkedHashMap<>();
    private Map<CurrencyType, Integer> purse = new LinkedHashMap<>();
    private Map<String, SkillInstance> skillList = new LinkedHashMap<>();
    private Map<CounterName, Double> rawCounterMap = new LinkedHashMap<>();
    private Map<String, Summon> summons = new LinkedHashMap<>();

    private boolean[][] rune_board = new boolean[6][6];
    private Map<Integer, Rune> rune_inventory = new LinkedHashMap<>();
    private List<Rune> socketed_runes = new ArrayList<>();

    @JsonIgnore
    @Transient
    private ObservableMap<CounterName, Double> counter;
    @JsonIgnore
    @Transient
    private final StatCalculator statCalculator;
    @JsonIgnore
    @Transient
    private final StatusCalculator statusCalculator;
    @JsonIgnore
    @Transient
    private final EquipmentManager equipmentManager;
    @JsonIgnore
    @Transient
    private final PassiveManager passiveManager;
    @JsonIgnore
    @Transient
    private final StatusManager statusManager;
    @JsonIgnore
    @Transient
    private final UniqueManager uniqueManager;
    @JsonIgnore
    @Transient
    private final RaceManager raceManager;
    @JsonIgnore
    @Transient
    private final ResourceManager resourceManager;
    @JsonIgnore
    @Transient
    private final CardManager cardManager;
    @JsonIgnore
    @Transient
    private final InventoryManager inventoryManager;
    @JsonIgnore
    @Transient
    private final SkillModifierManager skillModifierManager;

    public Unit(String name, UnitType unitType) {
        this.name = name;
        this.unitType = unitType;
        level = 1;
        recalculateRemainingStatusPoint();
        for (ResourceType type : ResourceType.values()) {
            ResourceData zeroData = new ResourceData();
            zeroData.setRemaining(0);
            zeroData.setReservedFlat(0);
            zeroData.setReservedPercent(0);
            zeroData.setUsable(0);
            resources.put(type, zeroData);
        }
        for (StatusType type : StatusType.values()) {
            statuses.put(type, new ModValue(1.0));
            raisedStatuses.put(type, 0);
        }
        for (StatType type : StatType.values()) {
            stats.put(type, new ModValue(0.0));
        }
        for (CurrencyType type : CurrencyType.values()) {
            purse.put(type,0);
        }

        statCalculator = new StatCalculator(this);
        statusCalculator = new StatusCalculator(this);
        equipmentManager = new EquipmentManager(this);
        passiveManager = new PassiveManager(this);
        statusManager = new StatusManager(this);
        uniqueManager = new UniqueManager(this);
        raceManager = new RaceManager(this);
        resourceManager = new ResourceManager(this);
        cardManager = new CardManager(this);
        inventoryManager = new InventoryManager(this);
        skillModifierManager = new SkillModifierManager(this);
        statCalculator.calculateBaseStatsFromCurrentStatus();
    }

    public Unit() {
        recalculateRemainingStatusPoint();
        for (ResourceType type : ResourceType.values()) {
            ResourceData zeroData = new ResourceData();
            zeroData.setRemaining(0);
            zeroData.setReservedFlat(0);
            zeroData.setReservedPercent(0);
            zeroData.setUsable(0);
            resources.put(type, zeroData);
        }
        for (StatusType type : StatusType.values()) {
            statuses.put(type, new ModValue(1.0));
            raisedStatuses.put(type, 0);
        }
        for (StatType type : StatType.values()) {
            stats.put(type, new ModValue(0.0));
        }
        for (CurrencyType type : CurrencyType.values()) {
            purse.put(type,0);
        }
        statCalculator = new StatCalculator(this);
        statusCalculator = new StatusCalculator(this);
        equipmentManager = new EquipmentManager(this);
        passiveManager = new PassiveManager(this);
        statusManager = new StatusManager(this);
        uniqueManager = new UniqueManager(this);
        raceManager = new RaceManager(this);
        resourceManager = new ResourceManager(this);
        cardManager = new CardManager(this);
        inventoryManager = new InventoryManager(this);
        skillModifierManager = new SkillModifierManager(this);
        statCalculator.calculateBaseStatsFromCurrentStatus();
    }

    public void calculateEverything() {
        for (int i = 0; i < 2; i++) {
            recalculateRemainingPassiveTreePoint();
            recalculateRemainingStatusPoint();
            uniqueManager.calculateUnique();
            calculateStatAndStatus();
            skillModifierManager.calculateSkillModifier();
            reloadSkill();
            calculateSkillDesc();
            resourceManager.updateMax();
            for (Summon summon : summons.values()) {
                summon.setOwner(this);
                summon.calculateEverything();
            }
            calculateBackpackSlot();
            calculateSoulPoint();
        }
    }

    public void calculateStatAndStatus() {
        statusCalculator.calculateBaseStatusFromRaisedStatuses();
        statusCalculator.applyBasicStatusModifiers();
        statusCalculator.applyBasicStatusWeaponPassive();
        statusCalculator.applyTransferStatusModifier();
        statusCalculator.calculateEquality();
        statusCalculator.applyConditionsStatusModifier();
        statusCalculator.applyOverrideStatusModifier();
        statusCalculator.limitHumanWeakStatus();
        statCalculator.calculateBaseStatsFromCurrentStatus();
        statCalculator.calculateManaRegen();
        statCalculator.applyBasicStatModifiers();
        statCalculator.applyBasicStatWeaponPassive();
        statCalculator.applyTransferStatModifier();
        statCalculator.applyConditionsStatModifier();
        statCalculator.applyOverrideStatModifier();
        statCalculator.applyOverrideWeaponPassive();
    }

    public void reloadSkill() {
        for (Map.Entry<String, SkillInstance> entry : skillList.entrySet()) {
            entry.getValue().setSkillData(SkillFactory.getSkill(entry.getKey(), this, entry.getValue().isReserving()));
            entry.getValue().applyModifier();
        }
        for (Map.Entry<String, SkillInstance> entry : skillList.entrySet()) {
            entry.getValue().getSkillData().calculateAll();
        }

        for (Map.Entry<Integer, EquipmentSlot> entry : equipmentSlots.entrySet()) {
            Equipment eq = entry.getValue().getEquipment();
            if (eq == null) continue;
            if (eq.getSkills().isEmpty()) continue;
            for (Map.Entry<String, SkillInstance> skillInstanceEntry : eq.getSkills().entrySet()) {
                skillInstanceEntry.getValue().setSkillData(SkillFactory.getSkill(skillInstanceEntry.getKey(), this, true));
                skillInstanceEntry.getValue().applyModifier();
                }
            for (SkillInstance skillInstance : eq.getSkills().values()) {
                if (skillInstance.getSkillData() == null) continue;
                skillInstance.getSkillData().calculateAll();
            }
        }
    }

    public void calculateSkillDesc() {
        for (SkillInstance instance : skillList.values()) {
            instance.getSkillData().translateDescription();
        }
    }

    public void initSkill(CombatFlow combatFlow) {
        for (SkillInstance skillInstance : getAllSkill().values()) {
            skillInstance.getSkillData().initializeEvent(combatFlow);
            if (skillInstance.getSkillData() instanceof SkillWithCondition) {
                SkillWithCondition skill = (SkillWithCondition) skillInstance.getSkillData();
                skill.refreshCondition(combatFlow);
            }
        }
    }

    public void skillCooldownDecrement(String name) {
        if (!hasSkill(name)) return;
        skillList.forEach((key, value) -> {
            if (key.equals(name)) {
                value.cooldownDecrement();
            }
        });
    }

    public void writeToSheet() {
        try {
            GoogleSheetsUtil sheetsUtil = new GoogleSheetsUtil();
            List<Request> requests = buildWriteRequests(sheetsUtil);
            sheetsUtil.takeRequests(requests);
            sheetsUtil.requestSet();
            String sessionId = UUID.randomUUID().toString();  // สร้าง id ใหม่สำหรับ session นี้
            long startTime = System.currentTimeMillis();
            System.out.println("Start processRequest: " + startTime + " Session: " + sessionId);

            sheetsUtil.processRequest(GoogleSheetsUtil.viewerSheetId);

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("End processRequest: took " + duration + " ms Session: " + sessionId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeToSheet(GoogleCredential googleCredential) {
        try {
            GoogleSheetsUtil sheetsUtil = new GoogleSheetsUtil(googleCredential);
            List<Request> requests = buildWriteRequests(sheetsUtil);
            sheetsUtil.takeRequests(requests);
            sheetsUtil.requestSet();
            sheetsUtil.processRequest(GoogleSheetsUtil.viewerSheetId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Request> buildWriteRequests(GoogleSheetsUtil sheetsUtil) {
        List<Request> requests = new ArrayList<>();

        if (unitType == UnitType.PLAYER || unitType == UnitType.NPC) {
            try {

                // หา sheetId
                Integer sheetId = sheetsUtil.getSheetIdByName(GoogleSheetsUtil.viewerSheetId, name);
                if (sheetId == null) {
                    return new ArrayList<>();
                }

                clearSheetContent(requests, sheetId, "A1", 45, 100);
                writeName(requests, sheetId);
                writePurse(requests, sheetId);
                writeCounter(requests, sheetId);
                writeStatuses(requests, sheetId);
                writeStats(requests, sheetId);
                writeStatsExpanded(requests, sheetId);
                writeSkills(requests, sheetId);
                writeEquipDetails(requests, sheetId);
                writeEquipBlock(requests, sheetId);
                writeUnique(requests, sheetId);
                writeInventory(requests, sheetId);
                writeBackpack(requests, sheetId);
                writeCondition(requests, sheetId);
                writeSoulCovenant(requests, sheetId);

            } catch (Exception ex) {
                ex.printStackTrace(); // หรือจะ throw ต่อก็ได้
            }
        }

        return requests;
    }

    public void writeName(List<Request> requests, int sheetId) throws Exception {
        String range = "A1";
        StringBuilder race_desc = new StringBuilder();
        StringBuilder opus_desc = new StringBuilder();
        race_desc.append(race.getName()).append(": ");
        if (race.getName().equals("Human")) {
            race_desc.append(race.getHumanDescription());
        } else {
            race_desc.append(race.getDescription());
        }
        opus_desc.append(opusName).append(": ").append(opusDescription);

        List<List<Object>> toAppend = new ArrayList<>(List.of(
                List.of(name),
                List.of(""),
                List.of("Race"),
                List.of(race_desc.toString()),
                List.of("Opus"),
                List.of(opus_desc.toString()),
                List.of("")
        ));
        if (card.get(CardType.PRIMARY) != null) {
            List<Object> row = new ArrayList<>();
            row.add(card.get(CardType.PRIMARY).getName());
            toAppend.add(row);
        } else {
            List<Object> row = new ArrayList<>();
            row.add("");
            toAppend.add(row);
        }

        if (card.get(CardType.SECONDARY) != null) {
            List<Object> row = new ArrayList<>();
            row.add(card.get(CardType.SECONDARY).getName());
            toAppend.add(row);
        } else {
            List<Object> row = new ArrayList<>();
            row.add("");
            toAppend.add(row);
        }
        requests.add(GoogleSheetsUtil.buildUpdateCellsRequest(sheetId, range, toAppend));
    }

    public void writePurse(List<Request> requests, int sheetId) throws Exception {
        String range = "A14";

        int copper_value = purse.get(CurrencyType.COPPER);
        copper_value += purse.get(CurrencyType.SILVER)*10;
        copper_value += purse.get(CurrencyType.GOLD)*10*100;
        copper_value += purse.get(CurrencyType.PLATINUM)*10*100*98;

        List<List<Object>> toAppend = new ArrayList<>(List.of(
                List.of("Platinum Coin", purse.get(CurrencyType.PLATINUM), "มูลค่ารวม"),
                List.of("Gold Coin", purse.get(CurrencyType.GOLD)),
                List.of("Silver Coin", purse.get(CurrencyType.SILVER), copper_value),
                List.of("Copper Coin", purse.get(CurrencyType.COPPER))
        ));
        requests.add(GoogleSheetsUtil.buildUpdateCellsRequest(sheetId, range, toAppend));
    }

    public void writeCounter(List<Request> requests, int sheetId) throws Exception {
        String range = "B1";
        List<List<Object>> toAppend = new ArrayList<>(List.of(
                List.of("Counter", "count")
        ));
        for (Map.Entry<CounterName, Double> counter : rawCounterMap.entrySet()) {
            List<Object> row = new ArrayList<>();
            row.add(counter.getKey().writeAsString());
            row.add(counter.getValue());
            toAppend.add(row);
        }

        requests.add(GoogleSheetsUtil.buildUpdateCellsRequest(sheetId, range, toAppend));
    }

    public void writeStatuses(List<Request> requests, int sheetId) throws Exception {
        String range = "D1";
        DecimalFormat df = new DecimalFormat("0.##");
        df.setRoundingMode(RoundingMode.FLOOR);
        List<List<Object>> toAppend = new ArrayList<>(List.of(
                List.of("Status", "Raised", "Overall")
        ));
        AtomicInteger slots = new AtomicInteger(0);
        StringBuilder backpack_name = new StringBuilder();
        equipmentSlots.forEach((key, value) -> {
            if (value.getEquipment() != null) {
                Equipment backpack = value.getEquipment();
                slots.addAndGet(backpack.getBackpackSlot());
                if (backpack.getEquipmentType().equals(EquipmentType.BACKPACK)) {
                    backpack_name.append(backpack.getName());
                }
            }
        });
        for (StatusType type : StatusType.values()) {
            List<Object> row = new ArrayList<>();
            row.add(type.writeAsString());
            if(raisedStatuses.get(type) == 0) {
                row.add("");
            } else {
                row.add(raisedStatuses.get(type));
            }
            row.add(statuses.get(type).getFinal());
            toAppend.add(row);
        }
        double debris = getDebris().getRemaining();
        if (debris <= 0) {
            toAppend.add(List.of("HP", df.format(getHealth().getRemaining()), df.format(getHealth().getUsable())));
        } else {
            toAppend.add(List.of("HP", df.format(getHealth().getRemaining())+"+"+df.format(debris), df.format(getHealth().getUsable())));
        }
        toAppend.add(List.of("MP", df.format(getMana().getRemaining()), df.format(getMana().getUsable())));
        toAppend.add(List.of("Soul Point", df.format(stats.get(StatType.SOULPOINT).getFinal()), stats.get(StatType.SOULPOINT).getBase()));
        toAppend.add(List.of("Level", level));
        toAppend.add(List.of("Status Point", remainingStatusPoint));
        toAppend.add(List.of("Starmap Point", remainingPassiveTreePoint));
        toAppend.add(List.of("Backpack"));
        toAppend.add(List.of(backpack_name.toString(), "", "Slot"));
        toAppend.add(List.of("", "", (calculateMaxBackpackSlot()-backpackSlot)+" / "+slots));

        requests.add(GoogleSheetsUtil.buildUpdateCellsRequest(sheetId, range, toAppend));
    }

    public void writeStats(List<Request> requests, int sheetId) throws Exception {
        String range = "G1";
        DecimalFormat df = new DecimalFormat("0.##");
        df.setRoundingMode(RoundingMode.FLOOR);
        List<List<Object>> toAppend = new ArrayList<>(List.of(
                List.of("Stats", "Overall")
        ));

        StatType[] allStats = StatType.values();
        int half = 18;

        for (int i = 0; i < half; i++) {
            if (allStats[i] == StatType.HEALTHPOINT || allStats[i] == StatType.MANAPOINT) continue;
            List<Object> row = new ArrayList<>();
            row.add(allStats[i].writeAsString());
            row.add(stats.get(allStats[i]).getFinal());
            toAppend.add(row);
        }

        requests.add(GoogleSheetsUtil.buildUpdateCellsRequest(sheetId, range, toAppend));
    }

    public void writeStatsExpanded(List<Request> requests, int sheetId) throws Exception {
        String range = "I1";
        DecimalFormat df = new DecimalFormat("0.##");
        df.setRoundingMode(RoundingMode.FLOOR);
        List<List<Object>> toAppend = new ArrayList<>(List.of(
                List.of("Stats Expanded", "Overall")
        ));

        StatType[] allStats = StatType.values();
        int half = 18;

        for (int i = half; i < 35; i++) {
            if (allStats[i] == StatType.SOULPOINT) continue;
            List<Object> row = new ArrayList<>();
            row.add(allStats[i].writeAsString());
            row.add(stats.get(allStats[i]).getFinal());
            toAppend.add(row);
        }

        requests.add(GoogleSheetsUtil.buildUpdateCellsRequest(sheetId, range, toAppend));
    }

    public void writeSkills(List<Request> requests, int sheetId) throws Exception {
        String range = "K1";
        DecimalFormat df = new DecimalFormat("0.##");
        df.setRoundingMode(RoundingMode.FLOOR);
        List<List<Object>> toAppend = new ArrayList<>(List.of(
                List.of("ชื่อสกิล", "ประเภทสกิล", "รูปแบบ", "คำอธิบาย", "Cooldown", "คูลดาวน์", "Cost", "Active")
        ));

        int skill_count = 0;

        for (SkillInstance instance : getAllSkill().values()) {
            List<Object> row = new ArrayList<>();
            row.add(instance.getSkillData().getName());
            row.add(instance.getSkillData().getTranslatedTag());
            row.add(instance.getSkillData().getActionType());
            row.add(instance.getSkillData().getTranslatedDesc());
            row.add(instance.getSkillData().getCooldown());
            if (instance.getOnCooldown() <= 0) {
                row.add("");
            } else {
                row.add(instance.getOnCooldown());
            }
            row.add(instance.getSkillData().getTranslatedCost());
            row.add(instance.isReserving());
            toAppend.add(row);
            skill_count++;
        }

        for (int i = 0; i<(12-skill_count); i++) {
            List<Object> row = new ArrayList<>();
            row.add("");
            row.add("");
            row.add("");
            row.add("");
            row.add("");
            row.add("");
            row.add("");
            row.add("");
            toAppend.add(row);
        }

        requests.add(GoogleSheetsUtil.buildUpdateCellsRequest(sheetId, range, toAppend));
    }

    public void writeEquipDetails(List<Request> requests, int sheetId) throws Exception {
        String range = "Z2";
        List<List<Object>> toAppend = new ArrayList<>(List.of(
                List.of("Equipped Items", "Name", "Type", "Description", "", "", "", "", "Status ที่ให้เมื่อใช้งาน")
        ));
        for (EquipmentSlot slot : equipmentSlots.values()) {
            List<Object> row = new ArrayList<>();
            row.add(slot.getEquipmentType().writeAsString());
            if (slot.getEquipment() != null) {
                row.add(slot.getEquipment().getName());
                row.add(slot.getEquipment().getItemType().writeAsString());
                row.add(slot.getEquipment().getLore());
                row.add("");
                row.add("");
                row.add("");
                row.add("");
                row.add(slot.getEquipment().getStatusDescription()+"\n"+slot.getEquipment().getDescription());
            } else {
                row.add("");
                row.add("");
                row.add("");
                row.add("");
                row.add("");
                row.add("");
                row.add("");
                row.add("");
            }
            toAppend.add(row);
        }

        for (Map.Entry<CardType, Card> entry : card.entrySet()) {
            List<Object> row = new ArrayList<>();
            row.add(entry.getKey().writeAsString());
            row.add(entry.getValue().getName());
            row.add("Card");
            row.add("-");
            row.add("");
            row.add("");
            row.add("");
            row.add("");
            row.add(entry.getValue().getStatusDescription().get(entry.getKey())+"\n"+entry.getValue().getDescription().get(entry.getKey()));

            toAppend.add(row);
        }

        requests.add(GoogleSheetsUtil.buildUpdateCellsRequest(sheetId, range, toAppend));
    }

    public void writeEquipBlock(List<Request> requests, int sheetId) throws Exception {
        String range = "A10";
        List<List<Object>> toAppend = new ArrayList<>();
        List<Equipment> equipment = new ArrayList<>();
        List<Equipment> weapon = new ArrayList<>();
        for (EquipmentSlot slot : equipmentSlots.values()) {
            equipment.add(slot.getEquipment());
            if (slot.getEquipmentType() == EquipmentType.WEAPON) {
                weapon.add(slot.getEquipment());
            }
        }
        List<Object> firstRow = new ArrayList<>();
        if (weapon.get(0) != null) {
            firstRow.add(weapon.get(0).getWeaponType().writeAsString());
        } else {
            firstRow.add("");
        }
        if (weapon.get(1) != null) {
            firstRow.add(weapon.get(1).getWeaponType().writeAsString());
        } else {
            firstRow.add("");
        }
        toAppend.add(firstRow);
        toAppend.add(List.of(
                equipment.get(0) != null ? equipment.get(0).getName() : "",
                equipment.get(1) != null ? equipment.get(1).getName() : "",
                equipment.get(2) != null ? equipment.get(2).getName() : ""
        ));

        toAppend.add(List.of(
                equipment.get(3) != null ? equipment.get(3).getName() : "",
                equipment.get(4) != null ? equipment.get(4).getName() : "",
                equipment.get(5) != null ? equipment.get(5).getName() : ""
        ));

        toAppend.add(List.of(
                equipment.get(6) != null ? equipment.get(6).getName() : "",
                equipment.get(7) != null ? equipment.get(7).getName() : "",
                mixTwoHanded // อันนี้ไม่เปลี่ยนเพราะไม่เกี่ยวกับ equipment
        ));
        requests.add(GoogleSheetsUtil.buildUpdateCellsRequest(sheetId, range, toAppend));
    }

    public void writeUnique(List<Request> requests, int sheetId) throws Exception {
        String range = "AL2";
        List<List<Object>> toAppend = new ArrayList<>(List.of(
                List.of("Unique", "Active")
        ));
        for (UniqueModifier unique : uniqueModifier) {
            List<Object> row = new ArrayList<>();
                row.add(unique.getName().writeAsString());
                row.add(unique.isActive());
            toAppend.add(row);
        }

        requests.add(GoogleSheetsUtil.buildUpdateCellsRequest(sheetId, range, toAppend));
    }

    public void writeInventory(List<Request> requests, int sheetId) throws Exception {
        String range = "A19";
        String statusRange = "I19";
        List<List<Object>> toAppend = new ArrayList<>(List.of(
                List.of("ชื่อไอเทม", "", "จำนวน [น้ำหนัก]", "คำอธิบาย")
        ));
        List<List<Object>> toAppendStatus = new ArrayList<>(List.of(
                List.of("ประเภท","Status ที่ให้เมื่อใช้งาน")
        ));
        List<Item> items = new ArrayList<>();
        List<Integer> itemAmount = new ArrayList<>();
        List<Integer> itemWeight = new ArrayList<>();
        for (Map.Entry<Integer, Item> entry : inventoryItems.entrySet()) {
            items.add(entry.getValue());
            itemAmount.add(inventoryItemAmount.get(entry.getKey()));
            itemWeight.add(entry.getValue().getWeight());
        }
        for (int i = 0; i < items.toArray().length; i++) {
            List<Object> row = new ArrayList<>();
            List<Object> statusRow = new ArrayList<>();
            row.add(items.get(i).getName());
            row.add("");
            if (itemAmount.get(i) == 0) {
                row.add("");
            } else {
                row.add(itemAmount.get(i)+"["+itemAmount.get(i)*itemWeight.get(i)+"]");
            }
            row.add(items.get(i).getLore());

            statusRow.add(items.get(i).getItemType().writeAsString());
            statusRow.add(items.get(i).getStatusDescription()+"\n"+items.get(i).getDescription());

            toAppend.add(row);
            toAppendStatus.add(statusRow);
        }

        requests.add(GoogleSheetsUtil.buildUpdateCellsRequest(sheetId, range, toAppend));
        requests.add(GoogleSheetsUtil.buildUpdateCellsRequest(sheetId, statusRange, toAppendStatus));
    }

    public void writeBackpack(List<Request> requests, int sheetId) throws Exception {
        String range = "L19";
        String statusRange = "P19";
        List<List<Object>> toAppend = new ArrayList<>(List.of(
                List.of("ชื่อไอเทม", "น้ำหนัก", "คำอธิบาย")
        ));
        List<List<Object>> toAppendStatus = new ArrayList<>(List.of(
                List.of("ประเภท","Status ที่ให้เมื่อใช้งาน")
        ));
        List<Item> items = new ArrayList<>();
        List<Integer> itemWeight = new ArrayList<>();
        for (Map.Entry<Integer, Item> entry : backpackItems.entrySet()) {
            items.add(entry.getValue());
            itemWeight.add(entry.getValue().getWeight());
        }
        for (int i = 0; i < items.toArray().length; i++) {
            List<Object> row = new ArrayList<>();
            List<Object> statusRow = new ArrayList<>();
            row.add(items.get(i).getName());
            row.add("["+itemWeight.get(i)+"]");
            row.add(items.get(i).getLore());

            statusRow.add(items.get(i).getItemType().writeAsString());
            statusRow.add(items.get(i).getStatusDescription()+"\n"+items.get(i).getDescription());

            toAppend.add(row);
            toAppendStatus.add(statusRow);
        }

        requests.add(GoogleSheetsUtil.buildUpdateCellsRequest(sheetId, range, toAppend));
        requests.add(GoogleSheetsUtil.buildUpdateCellsRequest(sheetId, statusRange, toAppendStatus));
    }

    public void writeCondition(List<Request> requests, int sheetId) throws Exception {
        String range = "T2";
        List<List<Object>> toAppend = new ArrayList<>(List.of(
                List.of("สถานะ", "ประเภท/ระดับ", "คำอธิบาย", "", "ผลกระทบสแตท", "ระยะเวลา")
        ));
        for (ConditionInstance instance : conditionInstances.values()) {
            List<Object> row = new ArrayList<>();
            Conditions conditions = instance.getCondition();
            conditions.setStatusDescription(StatTranslateUtil.translateStatusDesc(conditions.getModifiers(),null));
            double duration = instance.getDuration();
            row.add(conditions.getName());
            row.add(conditions.getConditionType().writeAsString()+" / "+conditions.getConditionTierType());
            row.add(conditions.getDescription());
            row.add("");
            row.add(conditions.getStatusDescription());
            row.add(duration);
            toAppend.add(row);
        }

        requests.add(GoogleSheetsUtil.buildUpdateCellsRequest(sheetId, range, toAppend));
    }

    public void writeSoulCovenant(List<Request> requests, int sheetId) throws Exception {
        String range = "Z19";
        List<List<Object>> toAppend = new ArrayList<>(List.of(
                List.of("Unit", "ชื่อเล่น", "ความสนิท", "เลเวล", "Soul Cost")
        ));
        for (Unit unit : summons.values()) {
            Summon summon = (Summon) unit;
            List<Object> row = new ArrayList<>();
            row.add(summon.getName());
            row.add(summon.getNick_name());
            row.add(summon.getIntimacy());
            row.add(summon.getLevel());
            row.add(summon.getSoulCost());
            toAppend.add(row);
        }

        requests.add(GoogleSheetsUtil.buildUpdateCellsRequest(sheetId, range, toAppend));
    }

    public void clearSheetContent(List<Request> requests, int sheetId, String range, int column, int row) {
        List<List<Object>> toAppend = new ArrayList<>();
        for (int i = 0; i<row ; i++) {
            List<Object> row_to_append = new ArrayList<>();
            for (int k = 0; k<column ; k++) {
                row_to_append.add("");
            }
            toAppend.add(row_to_append);
        }
        requests.add(GoogleSheetsUtil.buildUpdateCellsRequest(sheetId, range, toAppend));
    }

    public void initCounter() {
        counter = FXCollections.observableMap(new LinkedHashMap<>(rawCounterMap));
    }

    public int getLevel() {
        return level;
    }

    public int getRemainingStatusPoint() {
        return remainingStatusPoint;
    }

    public Map<StatusType, ModValue> getStatuses() {
        return this.statuses;
    }

    public Map<StatType, ModValue> getStats() {
        return this.stats;
    }

    public Map<StatusType, Integer> getRaisedStatuses() {
        return raisedStatuses;
    }

    public String getName() {
        return name;
    }

    public boolean isMixTwoHanded() {
        return this.mixTwoHanded;
    }

    public double getMixTwoHandedMult() {
        return this.mixTwoHandedMult;
    }

    public Map<Integer, EquipmentSlot> getEquipmentSlots() {
        return this.equipmentSlots;
    }

    public void addEquipmentSlot(int slot, EquipmentType type) {
        this.equipmentSlots.put(slot, new EquipmentSlot(type));
    }

    public void removeEquipmentSlots(int slot) {
        this.equipmentSlots.remove(slot);
    }

    public Map<Integer, PassiveNode> getAllocatedPassives() {
        return this.allocatedPassives;
    }

    public void recalculateRemainingStatusPoint() {
        remainingStatusPoint = (6+(level*4)+((int)Math.floor(level/5)*6));
        for (PassiveNode node : allocatedPassives.values()) {
            if (node.getName().equals("Fund Baby")) {
                remainingStatusPoint = 0;
                break;
            }
        }
        for (PassiveNode node : allocatedPassives.values()) {
            remainingStatusPoint += node.getStatusPoints();
        }
        for (int point : raisedStatuses.values()) {
            remainingStatusPoint -= point;
        }
    }

    public void recalculateRemainingPassiveTreePoint() {
        int toMinus = 0;
        for (PassiveNode node : allocatedPassives.values()) {
            toMinus++;
        }
        remainingPassiveTreePoint = (level*3) - toMinus;
    }

    public boolean hasCondition(String name) {
        for (ConditionInstance instance : conditionInstances.values()) {
            if (instance.getCondition().getName().equals(name)) return true;
        }
        return false;
    }

    public int hasXCondition(String name) {
        int amount = 0;
        for (ConditionInstance instance : conditionInstances.values()) {
            if (instance.getCondition().getName().equals(name)) {
                amount++;
            }
        }
        return amount;
    }

    public ConditionInstance findCondition(String name) {
        for (ConditionInstance instance : conditionInstances.values()) {
            if (instance.getCondition().getName().equals(name)) return instance;
        }
        return null;
    }

    public SkillInstance findSkill(String name) {
        for (SkillInstance instance : skillList.values()) {
            if (instance.getSkillData().getName().equals(name)) return instance;
        }
        return null;
    }

    public Item findItem(String name) {
        for (Item item : inventoryItems.values()) {
            if (item.getName().equals(name)) return item;
        }
        for (Item item : backpackItems.values()) {
            if (item.getName().equals(name)) return item;
        }
        return null;
    }

    public boolean hasNode(String name) {
        for (PassiveNode node : allocatedPassives.values()) {
            if (node.getName().equals(name)) return true;
        }
        return false;
    }

    public boolean hasSkill(String name) {
        for (SkillInstance skill : skillList.values()) {
            if (skill.getSkillData() == null) return false;
            if (skill.getSkillData().getName().equals(name)) return true;
        }
        return false;
    }

    public boolean hasActivatedSkill(String name) {
        for (SkillInstance skill : skillList.values()) {
            if (skill.getSkillData() == null) return false;
            if (skill.getSkillData().getName().equals(name) && skill.isReserving()) return true;
        }
        return false;
    }

    public int hasEquippedAmount(String name) {
        int count = 0;
        for (EquipmentSlot slot : equipmentSlots.values()) {
            if (slot.getEquipment() == null) continue;
            if (slot.getEquipment().getName().contains(name)) {
                count++;
            }
        }
        return count;
    }

    public boolean hasPrimaryCard(String name) {
        Card object = card.get(CardType.PRIMARY);
        if (object == null) {
            return false;
        }
        return object.getName().equals(name);
    }

    public boolean hasSecondaryCard(String name) {
        Card object = card.get(CardType.SECONDARY);
        if (object == null) {
            return false;
        }
        return object.getName().equals(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMixTwoHanded(boolean mixTwoHanded) {
        this.mixTwoHanded = mixTwoHanded;
    }

    public void setInSession(boolean inSession) {
        this.inSession = inSession;
    }

    public void setMixTwoHandedMult(double mixTwoHandedMult) {
        this.mixTwoHandedMult = mixTwoHandedMult;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setRemainingStatusPoint(int remainingStatusPoint) {
        this.remainingStatusPoint = remainingStatusPoint;
    }

    public boolean isInSession() {
        return inSession;
    }

    public Map<Integer, ConditionInstance> getConditionInstances() {
        return conditionInstances;
    }

    @JsonIgnore
    public boolean isPlayer() {
        return unitType == UnitType.PLAYER;
    }

    @JsonIgnore
    public boolean isNpc() {
        return unitType == UnitType.NPC;
    }

    @JsonIgnore
    public boolean isMonster() {
        return unitType == UnitType.MONSTER;
    }

    @JsonIgnore
    public boolean isSummon() {
        return unitType == UnitType.SUMMON;
    }

    @JsonIgnore
    public ResourceData getMana() {
        return resources.get(ResourceType.MANA);
    }

    @JsonIgnore
    public ResourceData getHealth() {
        return resources.get(ResourceType.HEALTH);
    }

    @JsonIgnore
    public ResourceData getDebris() {
        return resources.get(ResourceType.DEBRIS);
    }

    public void sumRemainingHealth(double toSum) {
        getHealth().sumRemaining(toSum);
        if (getHealth().getRemaining() >= getHealth().getUsable()) {
            getHealth().setRemaining(getHealth().getUsable());
        }
        if (getHealth().getRemaining() <= 0) {
            getHealth().setRemaining(0);
            LogWriterUtil.log("> Reached 0 Health!");
        }
    }

    public void sumRemainingMana(double toSum) {
        getMana().sumRemaining(toSum);
        if (getMana().getRemaining() >= getMana().getUsable()) {
            getMana().setRemaining(getMana().getUsable());
        }
    }

    public void sumRemainingDebris(double toSum) {
        getDebris().sumRemaining(toSum);
    }

    public void setRemainingHealth(double toSet) {
        getHealth().setRemaining(toSet);
        if (getHealth().getRemaining() >= getHealth().getUsable()) {
            getHealth().setRemaining(getHealth().getUsable());
        }
        if (getHealth().getRemaining() <= 0) {
            getHealth().setRemaining(0);
            LogWriterUtil.log("> Reached 0 Health!");
        }
    }

    public void setRemainingMana(double toSet) {
        getMana().setRemaining(toSet);
        if (getMana().getRemaining() >= getMana().getUsable()) {
            getMana().setRemaining(getMana().getUsable());
        }
    }

    public void setRemainingDebris(double toSet) {
        getDebris().setRemaining(toSet);
    }

    public Map<ResourceType, ResourceData> getResources() {
        return resources;
    }

    public void setResources(Map<ResourceType, ResourceData> resources) {
        this.resources = resources;
    }

    public Race getRace() {
        return race;
    }

    public StatCalculator getStatCalculator() {
        return statCalculator;
    }

    public StatusCalculator getStatusCalculator() {
        return statusCalculator;
    }

    public EquipmentManager getEquipmentManager() {
        return equipmentManager;
    }

    public PassiveManager getPassiveManager() {
        return passiveManager;
    }

    public StatusManager getStatusManager() {
        return statusManager;
    }

    public UniqueManager getUniqueManager() {
        return uniqueManager;
    }

    public RaceManager getRaceManager() {
        return raceManager;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public String getOpusName() {
        return opusName;
    }

    public void setOpusName(String opusName) {
        this.opusName = opusName;
    }

    public String getOpusDescription() {
        return opusDescription;
    }

    public void setOpusDescription(String opusDescription) {
        this.opusDescription = opusDescription;
    }

    public Map<CardType, Card> getCard() {
        return card;
    }

    public int getInventorySlot() {
        return inventorySlot;
    }

    public void setInventorySlot(int inventorySlot) {
        this.inventorySlot = inventorySlot;
    }

    public CardManager getCardManager() {
        return cardManager;
    }

    public int getRemainingPassiveTreePoint() {
        recalculateRemainingPassiveTreePoint();
        return remainingPassiveTreePoint;
    }

    public Map<Integer, Item> getInventoryItems() {
        return inventoryItems;
    }

    public Map<Integer, Integer> getInventoryItemAmount() {
        return inventoryItemAmount;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public void clearInventory(Item none) {
        for (Map.Entry<Integer, Item> entry : inventoryItems.entrySet()) {
            entry.setValue(none);
        }
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(UnitType unitType) {
        this.unitType = unitType;
    }

    public List<UniqueModifier> getUniqueModifier() {
        return uniqueModifier;
    }

    public void setStatuses(Map<StatusType, ModValue> statuses) {
        this.statuses = statuses;
    }

    public void setStats(Map<StatType, ModValue> stats) {
        this.stats = stats;
    }

    public void levelIncrement() {
        level += 1;
    }
    public void levelDecrement() {
        level -= 1;
    }

    public void setRemainingPassiveTreePoint(int remainingPassiveTreePoint) {
        this.remainingPassiveTreePoint = remainingPassiveTreePoint;
    }

        public Map<String, SkillInstance> getAllSkill() {
            Map<String, SkillInstance> result = new LinkedHashMap<>(skillList);

            for(Map.Entry<Integer, EquipmentSlot> entry : equipmentSlots.entrySet()) {
                Equipment eq = entry.getValue().getEquipment();
                if (eq != null) {
                    result.putAll(eq.getSkills());
                }
            }

            return result;
        }

    public Map<String, SkillInstance> getSkillList() {
        return skillList;
    }

    public void setSkillList(Map<String, SkillInstance> skillList) {
        this.skillList = skillList;
    }

    public SkillModifierManager getSkillModifierManager() {
        return skillModifierManager;
    }

    public Map<SkillType, Double> getFlatSkillModifiers() {
        return flatSkillModifiers;
    }

    public void setFlatSkillModifiers(Map<SkillType, Double> flatSkillModifiers) {
        this.flatSkillModifiers = flatSkillModifiers;
    }

    public Map<SkillType, Double> getMultSkillModifiers() {
        return multSkillModifiers;
    }

    public void setMultSkillModifiers(Map<SkillType, Double> multSkillModifiers) {
        this.multSkillModifiers = multSkillModifiers;
    }

    public void counterIncrement(CounterName counterName) {
        counter.merge(counterName, 1.0, Double::sum);
        rawCounterMap.merge(counterName, 1.0, Double::sum);
    }

    public void counterDecrement(CounterName counterName) {
        counter.merge(counterName, -1.0, Double::sum);
        rawCounterMap.merge(counterName, -1.0, Double::sum);
    }

    public void counterSet(CounterName counterName, double numberToPut) {
        counter.put(counterName, numberToPut);
        rawCounterMap.put(counterName, numberToPut);
    }

    public void counterSum(CounterName counterName, double toSum) {
        counter.merge(counterName, toSum, Double::sum);
        rawCounterMap.merge(counterName, toSum, Double::sum);
    }

    public void setCounter(ObservableMap<CounterName, Double> counter) {
        this.counter = counter;
    }

    public Map<String, Summon> getSummons() {
        return summons;
    }

    public Map<CounterName, Double> getRawCounterMap() {
        return rawCounterMap;
    }

    public int getBackpackSlot() {
        return backpackSlot;
    }

    public void setBackpackSlot(int backpackSlot) {
        this.backpackSlot = backpackSlot;
    }

    public void calculateBackpackSlot() {
        backpackSlot = 0;
        equipmentSlots.forEach((key, value) -> {
            if (value.getEquipmentType() != EquipmentType.BACKPACK) return;
            if (value.getEquipment() == null) return;
            int slot_given = value.getEquipment().getBackpackSlot();
            backpackSlot += slot_given;
        });
        backpackItems.forEach((key, value) -> {
            if (value == null) return;
            int slot_used = value.getWeight();
            backpackSlot -= slot_used;
        });
    }

    public int calculateMaxBackpackSlot() {
        AtomicInteger to_return = new AtomicInteger();
        equipmentSlots.forEach((key, value) -> {
            if (value.getEquipmentType() != EquipmentType.BACKPACK) return;
            if (value.getEquipment() == null) return;
            int slot_given = value.getEquipment().getBackpackSlot();
            to_return.addAndGet(slot_given);
        });

        return to_return.get();
    }

    public Map<Integer, Item> getBackpackItems() {
        return backpackItems;
    }

    public Map<CurrencyType, Integer> getPurse() {
        return purse;
    }

    public void calculateSoulPoint() {
        AtomicDouble soul_point = new AtomicDouble(stats.get(StatType.SOULPOINT).getFinal());
        summons.forEach((key, value) -> {
            soul_point.addAndGet(value.getSoulCost()*-1);
        });
        stats.get(StatType.SOULPOINT).setFinal(soul_point.get());
    }

    public void addCurrency(CurrencyType type, int amount) {
        if (purse.get(type) == null) return;
        purse.compute(type, (k, old_amount) -> old_amount + amount);
    }

    public void setCurrency(CurrencyType type, int amount) {
        if (purse.get(type) == null) return;
        purse.put(type, amount);
    }

    public Map<Integer, Rune> getRune_inventory() {
        return rune_inventory;
    }

    public boolean[][] getRune_board() {
        return rune_board;
    }

    public void setRune_board(boolean[][] array) {
        rune_board = array;
    }

    public List<Rune> getSocketed_runes() {
        return socketed_runes;
    }

    public void increaseRuneInventory(Rune rune) {
        int nextSlot = 0;
        for (Item item : rune_inventory.values()) {
            nextSlot++;
        }
        rune_inventory.put(nextSlot, rune);
    }

    public void increaseRuneInventory() {
        increaseRuneInventory(new Rune(""));
    }

    public void decreaseRuneInventory() {
        int lastSlot = -1;
        for (Item item : rune_inventory.values()) {
            lastSlot++;
        }
        rune_inventory.remove(lastSlot);
    }

    public void addRuneToInventory(Rune rune) {
        for (Map.Entry<Integer, Rune> entry : rune_inventory.entrySet()) {
            int slotNum = entry.getKey();

            if (entry.getValue().getName().equals("")) {
                rune_inventory.put(slotNum, rune);
                return;
            }
        }
        increaseRuneInventory(rune);
    }

    public void removeRuneFromInventory(Rune rune) {
        for (Map.Entry<Integer, Rune> entry : rune_inventory.entrySet()) {
            int slotNum = entry.getKey();

            if (entry.getValue() == rune) {
                rune_inventory.put(slotNum, new Rune(""));
                return;
            }
        }
    }

    public Rune findRune(int row, int col) {
        for (Rune socketedRune : socketed_runes) {
            int rune_row = socketedRune.getBaseRow();
            int rune_col = socketedRune.getBaseCol();

            if (rune_row == row && rune_col == col) {
                return socketedRune;
            }
        }
        return null;
    }

    public void setUniqueModifier(List<UniqueModifier> uniqueModifier) {
        this.uniqueModifier = uniqueModifier;
    }

    public void setConditionInstances(Map<Integer, ConditionInstance> conditionInstances) {
        this.conditionInstances = conditionInstances;
    }

    public void setEquipmentSlots(Map<Integer, EquipmentSlot> equipmentSlots) {
        this.equipmentSlots = equipmentSlots;
    }

    public void setAllocatedPassives(Map<Integer, PassiveNode> allocatedPassives) {
        this.allocatedPassives = allocatedPassives;
    }

    public void setRaisedStatuses(Map<StatusType, Integer> raisedStatuses) {
        this.raisedStatuses = raisedStatuses;
    }

    public void setCard(Map<CardType, Card> card) {
        this.card = card;
    }

    public void setInventoryItems(Map<Integer, Item> inventoryItems) {
        this.inventoryItems = inventoryItems;
    }

    public void setInventoryItemAmount(Map<Integer, Integer> inventoryItemAmount) {
        this.inventoryItemAmount = inventoryItemAmount;
    }

    public void setBackpackItems(Map<Integer, Item> backpackItems) {
        this.backpackItems = backpackItems;
    }

    public void setPurse(Map<CurrencyType, Integer> purse) {
        this.purse = purse;
    }

    public void setRawCounterMap(Map<CounterName, Double> rawCounterMap) {
        this.rawCounterMap = rawCounterMap;
    }

    public void setSummons(Map<String, Summon> summons) {
        this.summons = summons;
    }

    public void setRune_inventory(Map<Integer, Rune> rune_inventory) {
        this.rune_inventory = rune_inventory;
    }

    public void setSocketed_runes(List<Rune> socketed_runes) {
        this.socketed_runes = socketed_runes;
    }

    @JsonIgnore
    public double getSpeed() {
        return stats.get(StatType.SPEED).getFinal();
    }

    @JsonIgnore
    public ObservableMap<CounterName, Double> getCounter() {
        return counter;
    }

    @Override
    public String toString() {
        return "Player {\n" +
                "  name: '" + name + "',\n" +
                "  ismixTwoHanded: " + isMixTwoHanded() + ",\n" +
                "  mixTwoHandedMult: " + getMixTwoHandedMult() + ",\n" +
                "  level: " + level + ",\n" +
                "  remainingStatusPoint: " + remainingStatusPoint + ",\n" +
                "  manaReservedPercent: " + getMana().getReservedPercent() + ",\n" +
                "  manaReservedFlat: " + getMana().getReservedFlat() + ",\n" +
                "  healthReservedPercent: " + getHealth().getReservedPercent() + ",\n" +
                "  healthReservedFlat: " + getHealth().getReservedFlat() + ",\n" +
                "  usableMana: " + getMana().getUsable() + ",\n" +
                "  usableHealth: " + getHealth().getUsable() + ",\n" +
                "  remainingMana: " + getMana().getRemaining() + ",\n" +
                "  remainingHealth: " + getHealth().getRemaining() + ",\n" +
                "  statuses: " + getStatuses() + ",\n" +
                "  raisedStatuses: " + getRaisedStatuses() + ",\n" +
                "  stats: " + getStats() + ",\n" +
                "  equipmentSlots: " + getEquipmentSlots() + ",\n" +
                "  allocatedPassives: " + getAllocatedPassives() + ",\n" +
                "  activeConditions: " + conditionInstances + ",\n" +
                "  uniqueModifiers: " + uniqueModifier + ",\n" +
                '}';
    }

}
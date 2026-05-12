package manager;

import model.entity.items.Item;
import model.entity.units.Unit;

import java.util.Map;

public class InventoryManager {
    Unit unit;
    public InventoryManager(Unit unit) {
        this.unit = unit;
    }

    public void replaceItem(Integer slot, Item item) {
        if (item != null)
        unit.getInventoryItems().put(slot, item);
    }

    public void increaseQuantityByOne(Integer slot) {
        int oldAmount = unit.getInventoryItemAmount().get(slot);
        unit.getInventoryItemAmount().put(slot, oldAmount+1);
    }

    public void decreaseQuantityByOne(Integer slot) {
        int oldAmount = unit.getInventoryItemAmount().get(slot);
        unit.getInventoryItemAmount().put(slot, oldAmount-1);
    }

    public void increaseSlot() {
        int nextSlot = 0;
        for (Item item : unit.getInventoryItems().values()) {
            nextSlot++;
        }
        replaceItem(nextSlot, new Item(""));
        unit.getInventoryItemAmount().put(nextSlot,0);
    }

    public void increaseSlot(Item toPut) {
        int nextSlot = 0;
        for (Item item : unit.getInventoryItems().values()) {
            nextSlot++;
        }
        replaceItem(nextSlot,toPut);
        unit.getInventoryItemAmount().put(nextSlot,1);
    }

    public void decreaseSlot() {
        int lastSlot = -1;
        for (Item item : unit.getInventoryItems().values()) {
            lastSlot++;
        }
        unit.getInventoryItems().remove(lastSlot);
        unit.getInventoryItemAmount().remove(lastSlot);
    }

    public void addItem(Item item, int amount) {
        for (Map.Entry<Integer, Item> entry : unit.getInventoryItems().entrySet()) {
            int slotNum = entry.getKey();

            if (entry.getValue().getName().equals(item.getName())) {
                for (int i = 0; i < amount; i++) {
                    increaseQuantityByOne(slotNum);
                }
                return;
            }
        }

        for (Map.Entry<Integer, Item> entry : unit.getInventoryItems().entrySet()) {
            int slotNum = entry.getKey();

            if (entry.getValue().getName().equals("")) {
                replaceItem(slotNum, item);
                setQuantity(slotNum, amount);
                return;
            }
        }
        increaseSlot(item);
    }

    public void addItem(Item item) {
        addItem(item, 1);
    }

    public void addItemToBackpack(Item item) {
        for (Map.Entry<Integer, Item> entry : unit.getBackpackItems().entrySet()) {
            int slotNum = entry.getKey();

            if (entry.getValue().getName().equals("")) {
                replaceBackpackItem(slotNum, item);
                return;
            }
        }
        increaseBackpackSlot(item);
    }

    public void removeItemFromBackpack(int slot) {
        unit.getBackpackItems().remove(slot);
    }

    public void increaseBackpackSlot(Item toPut) {
        int nextSlot = 0;
        for (Item item : unit.getBackpackItems().values()) {
            nextSlot++;
        }
        replaceBackpackItem(nextSlot,toPut);
    }

    public void replaceBackpackItem(int slot, Item item) {
        if (item != null)
            unit.getBackpackItems().put(slot, item);
    }

    public void increaseQuantity(String name, int amount) {
        for (Map.Entry<Integer, Item> entry : unit.getInventoryItems().entrySet()) {
            if (entry.getValue().getName().equals(name)) {
                int slotNum = entry.getKey();
                int oldAmount = unit.getInventoryItemAmount().get(slotNum);
                unit.getInventoryItemAmount().put(slotNum, oldAmount + amount);
            }
        }
    }

    public void reduceItem(String name, int amount) {
        for (Map.Entry<Integer, Item> entry : unit.getInventoryItems().entrySet()) {
            int slotNum = entry.getKey();
            if (entry.getValue().getName().equals(name)) {
                for (int i = 0; i< amount; i++) {
                    decreaseQuantityByOne(slotNum);
                }
                return;
            }
        }
    }

    public void setItem(String name, int amount) {
        for (Map.Entry<Integer, Item> entry : unit.getInventoryItems().entrySet()) {
            int slotNum = entry.getKey();
            if (entry.getValue().getName().equals(name)) {
                    setQuantity(slotNum, amount);
                return;
            }
        }
    }

    public void removeItem(String name, int amount) {
        for (Map.Entry<Integer, Item> entry : unit.getInventoryItems().entrySet()) {
            if (entry.getValue().getName().equals(name)) {
                int slotNum = entry.getKey();
                int current = getQuantityFromInventory(slotNum);
                if (current <= amount) {
                    replaceItem(slotNum, new Item(""));
                    setQuantity(slotNum, 0);
                } else {
                    setQuantity(slotNum, current - amount);
                }
                return;
            }
        }
    }

    public void removeItem(String name) {
        removeItem(name, 1);
    }

    public int findItem(String name) {
        for (Map.Entry<Integer, Item> entry : unit.getInventoryItems().entrySet()) {
            int slotNum = entry.getKey();
            if (entry.getValue().getName().equals(name)) {
                return unit.getInventoryItemAmount().get(slotNum);
            }
        }
        return 0;
    }

    public void setQuantity(Integer slot, int amount) {
        unit.getInventoryItemAmount().put(slot, amount);
    }

    public Integer getQuantityFromInventory(String name) {
        for (Map.Entry<Integer, Item> entry : unit.getInventoryItems().entrySet()) {
            if (entry.getValue().getName().equals(name)) {
                int slotNum = entry.getKey();
                return unit.getInventoryItemAmount().get(slotNum);
            }
        }
        return 0;
    }

    public Integer getQuantityFromInventory(int slotNum) {
        return unit.getInventoryItemAmount().get(slotNum);
    }
}

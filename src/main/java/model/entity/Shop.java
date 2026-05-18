package model.entity;

import model.entity.items.Item;
import model.type.CityName;

import java.util.LinkedHashMap;
import java.util.Map;

public class Shop {
    private Map<Integer, ShopItem> list = new LinkedHashMap<>();
    private boolean open;
    private String ownerName;
    private String cityName;
    private String description;
    private CityName city;

    public Shop() {
        open = false;
    }

    public Shop(String ownerName, String cityName, boolean open, CityName city) {
        this.ownerName = ownerName;
        this.cityName = cityName;
        this.open = open;
        this.city = city;
    }

    public void addToShop(Item item, String price, int stock, int price_in_copper) {
        ShopItem toAdd = new ShopItem(item, price, stock, price_in_copper);
        int key = generateEmptyKey(list);
        list.put(key, toAdd);
    }

    public void addToShop(Item item, int stock) {
        ShopItem toAdd = new ShopItem(item, stock);
        int key = generateEmptyKey(list);
        list.put(key, toAdd);
    }

    public void removeFromShop(int key) {
        list.remove(key);
    }

    public void removeFromShop(String itemName) {
        list.entrySet().removeIf(entry ->
                entry.getValue().getItem().getName().equals(itemName));
    }

    public int generateEmptyKey(Map<Integer, ShopItem> list) {
        int emptyKey = 1;
        while (list.containsKey(emptyKey)) {
            emptyKey++;
        }

        return emptyKey;
    }

    public Map<Integer, ShopItem> getList() {
        return list;
    }

    public void setList(Map<Integer, ShopItem> list) {
        this.list = list;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CityName getCity() {
        return city;
    }

    public void setCity(CityName city) {
        this.city = city;
    }
}

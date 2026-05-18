package model.entity;

import model.entity.items.Item;

public class ShopItem {
    private Item item;
    private String price = "";
    private int stock;
    private int price_in_copper;

    public ShopItem() {
        this.stock = 0;
        this.item = new Item("");
        this.price = "";
        this.price_in_copper = 0;
    }

    public ShopItem(Item item, String price, int stock, int price_in_copper) {
        this.item = item;
        this.price = price;
        this.stock = stock;
        this.price_in_copper = price_in_copper;
    }

    public ShopItem(Item item, int stock) {
        this.stock = stock;
        this.item = item;
        this.price = "";
        this.price_in_copper = 0;
    }

    public void reduceStock() {
        this.stock -= 1;
    }

    public void increaseStock() {
        this.stock += 1;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getPrice_in_copper() {
        return price_in_copper;
    }

    public void setPrice_in_copper(int price_in_copper) {
        this.price_in_copper = price_in_copper;
    }
}

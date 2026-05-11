package main.java.model.entity;

import main.java.model.entity.items.Item;

public class ShopItem {
    private Item item;
    private String price = "";
    private int stock;

    public ShopItem() {
    }

    public ShopItem(Item item, String price, int stock) {
        this.item = item;
        this.price = price;
        this.stock = stock;
    }

    public ShopItem(Item item, int stock) {
        this.stock = stock;
        this.item = item;
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
}

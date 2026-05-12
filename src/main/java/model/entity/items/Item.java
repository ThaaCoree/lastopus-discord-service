package model.entity.items;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import model.type.*;

public class Item {
    private String name;
    private ItemType itemType;
    private String description;
    private String statusDescription;
    private String lore;
    private String price;
    private int weight;

    public Item(String name) {
        this.name = name;
        this.itemType = ItemType.NONE;
        this.description = "";
        this.statusDescription = "";
        this.lore = "";
        this.price = "";
        this.weight = 0;
    }
    public Item() {
        this.name = "NONAME";
        this.itemType = ItemType.NONE;
        this.description = "";
        this.statusDescription = "";
        this.lore = "";
        this.price = "";
        this.weight = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getLore() {
        return lore;
    }

    public void setLore(String lore) {
        this.lore = lore;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void addStatusDescription(String statusDescToAdd) {
        statusDescription = statusDescription + statusDescToAdd;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", itemType=" + itemType +
                ", description='" + description + '\'' +
                ", statusDescription='" + statusDescription + '\'' +
                ", lore='" + lore + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}

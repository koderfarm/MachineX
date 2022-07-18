package com.am.machinex.models;

public class PartName {
    String inventory_item_id, description;

    public PartName(String inventory_item_id, String description) {
        this.inventory_item_id = inventory_item_id;
        this.description = description;
    }

    public String getInventory_item_id() {
        return inventory_item_id;
    }

    public void setInventory_item_id(String inventory_item_id) {
        this.inventory_item_id = inventory_item_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

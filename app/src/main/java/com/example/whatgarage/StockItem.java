package com.example.whatgarage;

import java.util.Arrays;

public class StockItem {

    private int id;
    private String name;
    private int recommendedStock;
    private int existingQuantity;
    private String storedType;
    private byte[] image;

    //creating new objects in the app later
    public StockItem(String name, int recommendedStock, String storedType) {
        this.name = name;
        this.recommendedStock = recommendedStock;
        this.existingQuantity = 0;
        this.storedType = storedType;
    }

    public StockItem(int id, String name, int recommendedStock, int existingQuantity, String storedType, byte[] image) {
        this.id = id;
        this.name = name;
        this.recommendedStock = recommendedStock;
        this.existingQuantity = existingQuantity;
        this.storedType = storedType;
        this.image = image;
    }

    @Override
    public String toString() {
        return this.name + "\n" + this.recommendedStock + " (" + this.existingQuantity + ")";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRecommendedStock() {
        return recommendedStock;
    }

    public void setRecommendedStock(int recommendedStock) {
        this.recommendedStock = recommendedStock;
    }

    public int getExistingQuantity() {
        return existingQuantity;
    }

    public void setExistingQuantity(int existingQuantity) {
        this.existingQuantity = existingQuantity;
    }

    public String getStoredType() {
        return storedType;
    }

    public void setStoredType(String storedType) {
        this.storedType = storedType;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}

package com.fireblaze.foodiee.models;

public class ShoppingCartItem {
    private FoodItem foodItem;
    private Integer quantity;

    public ShoppingCartItem() {
    }

    public ShoppingCartItem(FoodItem foodItem, Integer quantity) {
        this.foodItem = foodItem;
        this.quantity = quantity;
    }

    public FoodItem getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(FoodItem foodItem) {
        this.foodItem = foodItem;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

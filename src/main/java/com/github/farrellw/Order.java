package com.github.farrellw;

import java.beans.ConstructorProperties;

public class Order {
    public String price;
    public String item;

//    @ConstructorProperties({"price", "item"})
    Order(String price, String item){
        this.price = price;
        this.item = item;
    }

    public Order() {
    }

    // Creating toString
    @Override
    public String toString()
    {
        return "Order [price="
                + price
                + ", item="
                + item
                + "]";
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}

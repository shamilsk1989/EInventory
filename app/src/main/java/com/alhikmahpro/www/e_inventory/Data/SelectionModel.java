package com.alhikmahpro.www.e_inventory.Data;

public class SelectionModel {

    String Code;
    String Name;
    String Price;

    public SelectionModel(String code, String name, String price) {
        Code = code;
        Name = name;
        Price = price;
    }

    public String getCode() {
        return Code;
    }

    public String getName() {
        return Name;
    }

    public String getPrice() {
        return Price;
    }
}

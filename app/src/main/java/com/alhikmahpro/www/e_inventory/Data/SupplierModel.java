package com.alhikmahpro.www.e_inventory.Data;

public class SupplierModel {

    String supplierCode;
    String supplierName;

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    String Price;

    public SupplierModel(String supplierCode, String supplierName) {
        this.supplierCode = supplierCode;
        this.supplierName = supplierName;

    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierCode() {
        return supplierCode;

    }

    public String getSupplierName() {
        return supplierName;
    }
}

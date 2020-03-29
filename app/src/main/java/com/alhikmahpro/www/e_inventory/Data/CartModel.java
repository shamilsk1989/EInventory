package com.alhikmahpro.www.e_inventory.Data;

public class CartModel {

    //used for invoice details
    private String barcode,productCode,productName;
    private String saleType,selectedUnit,unit1,unit2,unit3;
    private double qty;
    private double net;
    private double rate;
    private double discount;
    private int _id;
    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }



    public double getDiscPercentage() {
        return discPercentage;
    }

    public void setDiscPercentage(double discPercentage) {
        this.discPercentage = discPercentage;
    }

    private double discPercentage;
    int unit1Qty,unit2Qty,unit3Qty,unitIndex;



    public String getUnit1() {
        return unit1;
    }

    public void setUnit1(String unit1) {
        this.unit1 = unit1;
    }

    public String getUnit2() {
        return unit2;
    }

    public void setUnit2(String unit2) {
        this.unit2 = unit2;
    }

    public String getUnit3() {
        return unit3;
    }

    public void setUnit3(String unit3) {
        this.unit3 = unit3;
    }

    public int getUnit1Qty() {
        return unit1Qty;
    }

    public void setUnit1Qty(int unit1Qty) {
        this.unit1Qty = unit1Qty;
    }

    public int getUnit2Qty() {
        return unit2Qty;
    }

    public void setUnit2Qty(int unit2Qty) {
        this.unit2Qty = unit2Qty;
    }

    public int getUnit3Qty() {
        return unit3Qty;
    }

    public void setUnit3Qty(int unit3Qty) {
        this.unit3Qty = unit3Qty;
    }

    public int getUnitIndex() {
        return unitIndex;
    }

    public void setUnitIndex(int unitIndex) {
        this.unitIndex = unitIndex;
    }

    public String getSelectedUnit() {
        return selectedUnit;
    }

    public void setSelectedUnit(String selectedUnit) {
        this.selectedUnit = selectedUnit;
    }


    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSaleType() {
        return saleType;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public double getNet() {
        return net;
    }

    public void setNet(double net) {
        this.net = net;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }
}

package com.alhikmahpro.www.e_inventory.Data;

public class ItemModel {


    private String barCode,productCode,productName,stock,unit1,unit2,unit3,selectedFreePackage,selectedPackage;
    private String  date;
    private String staffName;
    private String SupplierCode;

    private String SupplierName;
    private String invoiceNo;
    private String invoiceDate;
    private String orderNo;
    private String customerName;
    private String customerCode;
    private String paymentType;

    public String getServerInvoice() {
        return serverInvoice;
    }

    public void setServerInvoice(String serverInvoice) {
        this.serverInvoice = serverInvoice;
    }

    private String serverInvoice;
    private double rate;
    private double discount;

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    private double grandTotal;

    public double getOtherAmount() {
        return otherAmount;
    }

    public void setOtherAmount(double otherAmount) {
        this.otherAmount = otherAmount;
    }

    private double otherAmount;

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    private double discountPercentage;
    private double qty;
    private double freeQty;
    private double salePrice;
    private double costPrice;
    private double total;
    private double net;
    private int is_sync ,docNo,unit1Qty,unit2Qty,unit3Qty,unitIndex, _id;

    public String getSupplierName() {
        return SupplierName;
    }

    public void setSupplierName(String supplierName) {
        SupplierName = supplierName;
    }



    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }


    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }




    public int getIs_sync() {
        return is_sync;
    }

    public void setIs_sync(int is_sync) {
        this.is_sync = is_sync;
    }



    public String getSupplierCode() {
        return SupplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        SupplierCode = supplierCode;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
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

    public double getNet() {
        return net;
    }

    public void setNet(double net) {
        this.net = net;
    }



    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }



    public String getSelectedPackage() {
        return selectedPackage;
    }

    public void setSelectedPackage(String selectedPackage) {
        this.selectedPackage = selectedPackage;
    }



    public String getSelectedFreePackage() {
        return selectedFreePackage;
    }

    public void setSelectedFreePackage(String selectedFreePackage) {
        this.selectedFreePackage = selectedFreePackage;
    }

    public double getFreeQty() {
        return freeQty;
    }

    public void setFreeQty(double freeQty) {
        this.freeQty = freeQty;
    }



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

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getDocNo() {
        return docNo;
    }

    public void setDocNo(int docNo) {
        this.docNo = docNo;
    }
}

package com.alhikmahpro.www.e_inventory.Data;

public class GoodsData {


    int docNo;
    String orderNo;
    String customerCode,customerName, invoiceNo, invoiceDate, salesmanId;
    double base_total, disc, netAmount;
    String paymentMode, mDate;
    int syncStatus;


    public GoodsData(int docNo, String orderNo, String customerCode,
                     String customerName, String invoiceNo, String invoiceDate, String salesmanId,
                     double base_total, double disc, double netAmount,
                     String paymentMode, String mDate,int syncStatus) {

        this.docNo = docNo;
        this.orderNo = orderNo;
        this.customerCode = customerCode;
        this.customerName = customerName;
        this.invoiceNo = invoiceNo;
        this.invoiceDate = invoiceDate;
        this.salesmanId = salesmanId;
        this.base_total = base_total;
        this.disc = disc;
        this.netAmount = netAmount;
        this.paymentMode = paymentMode;
        this.mDate = mDate;
        this.syncStatus=syncStatus;
    }
}

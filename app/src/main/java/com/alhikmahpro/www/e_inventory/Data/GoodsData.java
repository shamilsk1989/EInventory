package com.alhikmahpro.www.e_inventory.Data;

public class GoodsData {


    int docNo;
    String orderNo;
    String customerCode,customerName, invoiceNo, invoiceDate, salesmanId;
    double base_total, disc, netAmount,otherAmount,gTotal;
    String paymentMode, mDate,serverInvoice;
    int syncStatus;


    public GoodsData(int docNo, String orderNo, String customerCode,
                     String customerName, String invoiceNo, String invoiceDate, String salesmanId,
                     double base_total, double disc, double netAmount,double otherAmount,double gTotal,
                     String paymentMode, String mDate,String serverInvoice,int syncStatus) {

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
        this.otherAmount=otherAmount;
        this.gTotal=gTotal;
        this.paymentMode = paymentMode;
        this.mDate = mDate;
        this.serverInvoice=serverInvoice;
        this.syncStatus=syncStatus;
    }
}

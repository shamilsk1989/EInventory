package com.alhikmahpro.www.e_inventory.Data;

public class SaleData {
    String invoiceNo;
    String customerCode,customerName,invoiceDate, salesmanId;
    double base_total, disc, netAmount,discPer;
    String paymentMode, mDate;
    int syncStatus;

    public SaleData(String invoiceNo, String customerCode,
                    String customerName, String invoiceDate,
                    String salesmanId, double base_total,
                    double disc, double disPer,double netAmount, String paymentMode,
                    String mDate, int syncStatus) {
        this.invoiceNo = invoiceNo;
        this.customerCode = customerCode;
        this.customerName = customerName;
        this.invoiceDate = invoiceDate;
        this.salesmanId = salesmanId;
        this.base_total = base_total;
        this.disc = disc;
        this.discPer=disPer;
        this.netAmount = netAmount;
        this.paymentMode = paymentMode;
        this.mDate = mDate;
        this.syncStatus = syncStatus;
    }


}

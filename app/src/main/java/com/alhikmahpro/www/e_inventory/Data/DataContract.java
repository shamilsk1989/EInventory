package com.alhikmahpro.www.e_inventory.Data;

public class DataContract {
    public DataContract() { }


    public static final int SYNC_STATUS_OK=1;
    public static final int SYNC_STATUS_FAILED=0;
    public static final int SALE_ON=1;
    public static final int SALE_OFF=0;
    public static final int INV_ON=1;
    public static final int INV_OFF=0;
    public static final int GDS_ON=1;
    public static final int GDS_OFF=0;
    public static final int REC_ON=1;
    public static final int REC_OFF=0;
    public static final String DIR_RECEIPT="Receipt";
    public static final String DIR_INVOICE="Invoice";

    public static class Settings{

        public static final String TABLE_NAME="settings";
        public static final String COL_ID="_id";
        public static final String COL_DEVICE_ID="device_id";
        public static final String COL_COMPANY_NAME="company_name";
        public static final String COL_COMPANY_CODE="company_code";
        public static final String COL_BRANCH_CODE="branch_code";
        public static final String COL_LOCATION_CODE="location_code";
        public static final String COL_PERIOD_CODE="period_code";
        public static final String COL_LOGO="logo";
        public static final String COL_IS_SALE="is_sale";
        public static final String COL_IS_INV="is_inv";
        public static final String COL_IS_GDS="is_gds";
        public static final String COL_IS_REC="is_rec";




    }
    public static class Login{
        public static final String TABLE_NAME="login";
        public static final String COL_ID="_id";
        public static final String COL_PASSWORD="password";
    }
    public static class Stocks{

        public static final String TABLE_NAME="stocks";
        public static final String COL_ID="_id";
        public static final String COL_DOCUMENT_NUMBER="document_number";
        public static final String COL_DATE_TIME="date_time";
        public static final String COL_TOTAL="total";
        public static final String COL_STAFF_NAME="staff_name";
        public static final String COL_IS_SYNC="is_sync";

    }

    public static class StocksDetails{

        public static final String TABLE_NAME="stocks_details";
        public static final String COL_ID="_id";
        public static final String COL_DOCUMENT_NUMBER="document_number";
        public static final String COL_BAR_CODE="bar_code";
        public static final String COL_PRODUCT_CODE="product_code";
        public static final String COL_PRODUCT_NAME="product_name";
        public static final String COL_UNIT="unit";
        public static final String COL_QUANTITY="quantity";
        public static final String COL_SALES_PRICE="sales_price";
        public static final String COL_COST_PRICE="cost_price";
        public static final String COL_IS_SYNC="is_sync";

    }

    public static class GoodsReceive{

        public static final String TABLE_NAME="goods";
        public static final String COL_ID="_id";
        public static final String COL_DOCUMENT_NUMBER="document_number";
        public static final String COL_ORDER_NUMBER="order_no";
        public static final String COL_SUPPLIER_CODE="supplier_code";
        public static final String COL_SUPPLIER_NAME="supplier_name";
        public static final String COL_INVOICE_NUMBER="invoice_no";
        public static final String COL_INVOICE_DATE="invoice_date";
        public static final String COL_STAFF_NAME="staff_name";
        public static final String COL_TOTAL="total";
        public static final String COL_DISCOUNT_AMOUNT="discount_amount";
        public static final String COL_NET_AMOUNT="net_amount";
        public static final String COL_PAYMENT_TYPE="payment_type";
        public static final String COL_DATE_TIME="date_time";
        public static final String COL_IS_SYNC="is_sync";

    }

    public static class GoodsReceiveDetails{

        public static final String TABLE_NAME="goods_details";
        public static final String COL_ID="_id";
        public static final String COL_DOCUMENT_NUMBER="document_number";
        public static final String COL_BAR_CODE="bar_code";
        public static final String COL_PRODUCT_CODE="product_code";
        public static final String COL_PRODUCT_NAME="product_name";
        public static final String COL_UNIT="unit";
        public static final String COL_FREE_UNIT="free_unit";
        public static final String COL_QUANTITY="quantity";
        public static final String COL_FREE_QUANTITY="free_quantity";
        public static final String COL_RATE="rate";
        public static final String COL_DISCOUNT="discount";
        public static final String COL_SALES_PRICE="sales_price";
        public static final String COL_COST_PRICE="cost_price";
        public static final String COL_STOCK="stock";
        public static final String COL_NET_VALUE="net_value";
        public static final String COL_UNIT1="unit1";
        public static final String COL_UNIT2="unit2";
        public static final String COL_UNIT3="unit3";
        public static final String COL_UN_QTY1="un_qty1";
        public static final String COL_UN_QTY2="un_qty2";
        public static final String COL_UN_QTY3="un_qty3";
        public static final String COL_IS_SYNC="is_sync";

    }


    public static class Invoice{

        public static final String TABLE_NAME="invoice";
        public static final String COL_ID="_id";
        public static final String COL_INVOICE_NUMBER="invoice_number";
        public static final String COL_INVOICE_DATE="invoice_date";
        public static final String COL_SALESMAN_ID="salesman_id";
        public static final String COL_CUSTOMER_CODE="customer_code";
        public static final String COL_CUSTOMER_NAME="customer_name";
        public static final String COL_TOTAL_AMOUNT="total_amount";
        public static final String COL_DISCOUNT_AMOUNT="discount_amount";
        public static final String COL_NET_AMOUNT="net_amount";
        public static final String COL_PAYMENT_TYPE="payment_type";
        public static final String COL_DATE_TIME="date_time";
        public static final String COL_IS_SYNC="is_sync";

    }

    public static class InvoiceDetails{

        public static final String TABLE_NAME="invoice_details";
        public static final String COL_ID="_id";
        public static final String COL_INVOICE_NUMBER="invoice_number";
        public static final String COL_BAR_CODE="bar_code";
        public static final String COL_PRODUCT_CODE="product_code";
        public static final String COL_PRODUCT_NAME="product_name";
        public static final String COL_UNIT="selected_unit";
        public static final String COL_QUANTITY="quantity";
        public static final String COL_UNIT1="unit1";
        public static final String COL_UNIT2="unit2";
        public static final String COL_UNIT3="unit3";
        public static final String COL_UN_QTY1="un_qty1";
        public static final String COL_UN_QTY2="un_qty2";
        public static final String COL_UN_QTY3="un_qty3";
        public static final String COL_RATE="rate";
        public static final String COL_DISCOUNT="discount";
        public static final String COL_NET_AMOUNT="net_amount";
        public static final String COL_SALE_TYPE="sale_type";
        public static final String COL_IS_SYNC="is_sync";

    }

    public static class PaperSettings{

        public static final String TABLE_NAME="paper_settings";
        public static final String COL_ID="_id";
        public static final String COL_COMPANY_NAME="company_name";
        public static final String COL_COMPANY_ADDRESS="company_address";
        public static final String COL_COMPANY_PHONE="company_phone";
        public static final String COL_FOOTER="footer";
        public static final String COL_LOGO="logo";
        public static final String COL_PAPER_SIZE="paper_size";

    }

    public static class Receipts{
        public static final String TABLE_NAME="receipts";
        public static final String COL_ID="_id";
        public static final String COL_RECEIPT_NUMBER="receipts_number";
        public static final String COL_RECEIPT_DATE="receipts_date";
        public static final String COL_SALESMAN_ID="salesman_id";
        public static final String COL_CUSTOMER_CODE="customer_code";
        public static final String COL_CUSTOMER_NAME="customer_name";
        public static final String COL_BALANCE_AMOUNT="balance_amount";
        public static final String COL_RECEIVED_AMOUNT="received_amount";
        public static final String COL_PAYMENT_TYPE="payment_type";
        public static final String COL_CHEQUE_DATE="cheque_date";
        public static final String COL_CHEQUE_NUMBER="cheque_number";
        public static final String COL_REMARK="remark";
        public static final String COL_IS_SYNC="is_sync";
    }



}

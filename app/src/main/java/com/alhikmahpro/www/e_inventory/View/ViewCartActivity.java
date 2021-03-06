package com.alhikmahpro.www.e_inventory.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alhikmahpro.www.e_inventory.Adapter.SaleCartAdapter;
import com.alhikmahpro.www.e_inventory.Data.Cart;
import com.alhikmahpro.www.e_inventory.Data.CartModel;
import com.alhikmahpro.www.e_inventory.Data.Converter;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewCartActivity extends AppCompatActivity implements SaleCartAdapter.OnItemClickListener {

    @BindView(R.id.item_list_rv)
    RecyclerView itemListRv;
    @BindView(R.id.txtEmpty)
    TextView txtEmpty;
    @BindView(R.id.btnNext)
    Button btnNext;
    SaleCartAdapter saleCartAdapter;
    RecyclerView.LayoutManager layoutManager;
//    @BindView(R.id.txtTotal)
//    TextView txtTotal;
//    @BindView(R.id.txtTotalCount)
//    TextView txtTotalCount;
    double total;
    String customerName, invoiceNo, salesmanId, customerCode, invoiceDate, Action,type;
    private static final String TAG = "ViewCartActivity";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    MenuItem itemDelete,itemCart,itemAdd;
    private static int cart_count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cart);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Intent intent = getIntent();
        type=intent.getStringExtra("TYPE");
        Action = intent.getStringExtra("ACTION");
        customerName = intent.getStringExtra("CUS_NAME");
        customerCode = intent.getStringExtra("CUS_CODE");
        salesmanId = intent.getStringExtra("SALESMAN_ID");
        invoiceNo = intent.getStringExtra("DOC_NO");
        invoiceDate = intent.getStringExtra("DOC_DATE");
        getSupportActionBar().setTitle(customerName);
        Log.d(TAG, "view cart onCreate: invoice no: and Date" + invoiceNo + "/" + invoiceDate);
        calculate();
        loadRecyclerView();

    }

    private void loadRecyclerView() {
        layoutManager = new LinearLayoutManager(this);
        itemListRv.setLayoutManager(layoutManager);
        itemListRv.setHasFixedSize(true);
        if (Cart.mCart.size() > 0) {
            saleCartAdapter = new SaleCartAdapter(ViewCartActivity.this);
            itemListRv.setAdapter(saleCartAdapter);
            saleCartAdapter.setOnItemClickListener(ViewCartActivity.this);
            txtEmpty.setVisibility(View.GONE);
        }

//        } else {
//            txtTotal.setVisibility(View.GONE);
//        }


    }

    @OnClick(R.id.btnNext)
    public void onViewClicked() {

        Intent intent_payment = new Intent(ViewCartActivity.this, PaymentActivity.class);
        intent_payment.putExtra("ACTION", Action);
        intent_payment.putExtra("TYPE", type);
        intent_payment.putExtra("CUS_NAME", customerName);
        intent_payment.putExtra("CUS_CODE", customerCode);
        intent_payment.putExtra("SALESMAN_ID", salesmanId);
        intent_payment.putExtra("INV_NO", invoiceNo);
        intent_payment.putExtra("INV_DATE", invoiceDate);
        intent_payment.putExtra("TOTAL_ROW", Cart.mCart.size());
        intent_payment.putExtra("TOTAL", total);
        startActivity(intent_payment);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onItemClick(final int position) {

        new AlertDialog.Builder(ViewCartActivity.this)
                .setTitle("Confirm")
                .setMessage("Remove Item From Cart ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(type.equals("SAL")){
                            DeleteSale(position);
                        }else if(type.equals("ORD")){
                            DeleteOrder(position);
                        }
                        calculate();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();


    }

    private void DeleteSale(int position) {
        CartModel cartModel=Cart.mCart.get(position);
        int id = cartModel.get_id();
        dbHelper helper = new dbHelper(this);
        boolean del=helper.deleteInvoiceDetailsById(id);
        if(del){
            Cart.mCart.remove(position);
            saleCartAdapter.notifyDataSetChanged();
            saleCartAdapter.notifyItemRemoved(position);
            saleCartAdapter.notifyItemRangeChanged(position, Cart.mCart.size());
            Log.d("LastID", "Deleted: " + id);
        }

    }
    private void DeleteOrder(int position) {
        CartModel cartModel=Cart.mCart.get(position);
        int id = cartModel.get_id();
        dbHelper helper = new dbHelper(this);
        boolean del=helper.deleteOrderDetailsById(id);
        if(del){
            Cart.mCart.remove(position);
            saleCartAdapter.notifyDataSetChanged();
            saleCartAdapter.notifyItemRemoved(position);
            saleCartAdapter.notifyItemRangeChanged(position, Cart.mCart.size());
            Log.d("LastID", "Deleted: " + id);
        }

    }

    private void calculate() {
        cart_count=Cart.mCart.size();
        total = 0;
        for (CartModel cartModel : Cart.mCart) {
            total = total + cartModel.getNet();
        }
        if(Cart.mCart.size()>0){
            btnNext.setText("Pay QR:"+currencyFormatter(total));
        }else{
            txtEmpty.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.GONE);
        }

        invalidateOptionsMenu();

//        txtTotal.setText(currencyFormatter(total));
//        txtTotalCount.setText(String.valueOf(Cart.mCart.size()));


    }

    public String currencyFormatter(double val) {

        NumberFormat format = NumberFormat.getCurrencyInstance();
        String pattern = ((DecimalFormat) format).toPattern();
        String newPattern = pattern.replace("\u00A4", "").trim();
        NumberFormat newFormat = new DecimalFormat(newPattern);
        return String.valueOf(newFormat.format(val));

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.cart_toolbar, menu);
        itemCart = menu.findItem(R.id.action_cart);
        Log.d(TAG, "onCreateOptionsMenu: "+ Converter.convertLayoutToImage(ViewCartActivity.this,cart_count,R.drawable.ic_shopping_cart));
        itemCart.setIcon(Converter.convertLayoutToImage(ViewCartActivity.this,cart_count,R.drawable.ic_shopping_cart));
        itemDelete = menu.findItem(R.id.action_delete);
        itemDelete.setVisible(false);
        itemAdd = menu.findItem(R.id.action_add);
        itemAdd.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection



        return super.onOptionsItemSelected(item);
    }


}

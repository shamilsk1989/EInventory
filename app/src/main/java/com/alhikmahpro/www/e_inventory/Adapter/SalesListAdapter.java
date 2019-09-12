package com.alhikmahpro.www.e_inventory.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.ItemModel;
import com.alhikmahpro.www.e_inventory.Interface.OnListAdapterClickListener;
import com.alhikmahpro.www.e_inventory.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class SalesListAdapter extends RecyclerView.Adapter<SalesListAdapter.SaleViewHolder> {
    Context context;
    List<ItemModel>saleList;
    OnListAdapterClickListener onAdapterClickListener;
    private static final String TAG = "SalesListAdapter";

    public SalesListAdapter(Context context, List<ItemModel> saleList,OnListAdapterClickListener onAdapterClickListener) {
        this.context = context;
        this.saleList = saleList;
        this.onAdapterClickListener = onAdapterClickListener;

    }

    @Override
    public SaleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_sale_row,parent,false);
        return new SaleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SaleViewHolder holder, final int position) {

        ItemModel itemModel=saleList.get(position);
        holder.invoiceNo.setText(itemModel.getInvoiceNo());
        String date=itemModel.getInvoiceDate();
        Log.d(TAG, "onBindViewHolder:date "+date);
        int sync=itemModel.getIs_sync();
        if(sync==DataContract.SYNC_STATUS_OK){
            holder.imgSync.setImageResource(R.drawable.ic_sync_ok);
            holder.imgEdit.setEnabled(false);
            holder.imgEdit.setImageResource(R.drawable.ic_edit_disabled);
        }
        if(sync==DataContract.SYNC_STATUS_FAILED){
            holder.imgSync.setImageResource(R.drawable.ic_sync_error);
            holder.imgEdit.setEnabled(true);
            holder.imgEdit.setImageResource(R.drawable.ic_edit);
        }
        holder.invoiceDate.setText(date);
        holder.invoiceAmount.setText(currencyFormatter(itemModel.getNet()));
        holder.customerName.setText(itemModel.getCustomerName());
        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: edit");
                onAdapterClickListener.OnEditClicked(position);
            }
        });
        holder.imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");
                onAdapterClickListener.OnShareClicked(position);

            }
        });


    }

    @Override
    public int getItemCount() {
        return saleList.size();
    }

    public class SaleViewHolder extends RecyclerView.ViewHolder {

        TextView invoiceNo;
        TextView invoiceAmount;
        TextView invoiceDate;
        ImageView imgEdit,imgSync,imgShare;
        TextView customerName;



        public SaleViewHolder(View itemView) {
            super(itemView);
            invoiceNo=itemView.findViewById(R.id.rv_invoiceNo);
            invoiceAmount=itemView.findViewById(R.id.rv_amount);
            invoiceDate=itemView.findViewById(R.id.rv_date);
            imgEdit=itemView.findViewById(R.id.rv_img_edit);
            imgSync=itemView.findViewById(R.id.rv_img_sync);
            imgShare=itemView.findViewById(R.id.rv_img_share);
            customerName=itemView.findViewById(R.id.rv_customer);

        }
    }

    public String currencyFormatter(double val) {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        String pattern = ((DecimalFormat) format).toPattern();
        String newPattern = pattern.replace("\u00A4", "").trim();
        NumberFormat newFormat = new DecimalFormat(newPattern);
        return String.valueOf(newFormat.format(val));

    }
}

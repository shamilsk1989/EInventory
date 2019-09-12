package com.alhikmahpro.www.e_inventory.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.ItemModel;
import com.alhikmahpro.www.e_inventory.Data.ReceiptModel;
import com.alhikmahpro.www.e_inventory.Interface.OnAdapterClickListener;
import com.alhikmahpro.www.e_inventory.Interface.OnListAdapterClickListener;
import com.alhikmahpro.www.e_inventory.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ViewHolder> {

    OnListAdapterClickListener adapterClickListener;
    List<ReceiptModel> mList = new ArrayList<>();
    Context context;


    public ReceiptAdapter(Context context,List<ReceiptModel> mList, OnListAdapterClickListener adapterClickListener) {
        this.context=context;
        this.adapterClickListener = adapterClickListener;
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_sale_row, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        ReceiptModel itemsModel = mList.get(position);

        viewHolder.rvDocNo.setText(String.valueOf(itemsModel.getReceiptNo()));
        viewHolder.rvCustomerName.setText(itemsModel.getCustomerName());
        viewHolder.rvDate.setText(itemsModel.getReceiptDate());
        double amount=itemsModel.getReceivedAmount();
        viewHolder.rvAmount.setText(currencyFormatter(amount));
        int sync=itemsModel.getIs_sync();
        if(sync==DataContract.SYNC_STATUS_OK){
            viewHolder.rvImgSync.setImageResource(R.drawable.ic_sync_ok);
            viewHolder.rvImgEdit.setEnabled(false);
            viewHolder.rvImgEdit.setImageResource(R.drawable.ic_edit_disabled);
        }
        if(sync==DataContract.SYNC_STATUS_FAILED){
            viewHolder.rvImgSync.setImageResource(R.drawable.ic_sync_error);
            viewHolder.rvImgEdit.setEnabled(true);
            viewHolder.rvImgEdit.setImageResource(R.drawable.ic_edit);
        }

        viewHolder.rvImgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterClickListener.OnEditClicked(position);
            }
        });

        viewHolder.rvImgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterClickListener.OnShareClicked(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rv_invoiceNo)
        TextView rvDocNo;
        @BindView(R.id.rv_amount)
        TextView rvAmount;
        @BindView(R.id.rv_customer)
        TextView rvCustomerName;
        @BindView(R.id.rv_img_edit)
        ImageView rvImgEdit;
        @BindView(R.id.rv_img_share)
        ImageView rvImgShare;
        @BindView(R.id.rv_img_sync)
        ImageView rvImgSync;
        @BindView(R.id.rv_date)
        TextView rvDate;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
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

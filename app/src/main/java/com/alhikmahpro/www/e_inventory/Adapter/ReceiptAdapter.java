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
import com.alhikmahpro.www.e_inventory.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ViewHolder> {

    OnAdapterClickListener adapterClickListener;
    List<ReceiptModel> mList = new ArrayList<>();
    Context context;


    public ReceiptAdapter(Context context,List<ReceiptModel> mList, OnAdapterClickListener adapterClickListener) {
        this.context=context;
        this.adapterClickListener = adapterClickListener;
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_receipt_row, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        ReceiptModel itemsModel = mList.get(position);

        viewHolder.rvDocNo.setText(String.valueOf(itemsModel.getReceiptNo()));
        viewHolder.rvStaffName.setText(itemsModel.getSalesmanId());

        viewHolder.rvDate.setText(itemsModel.getReceiptDate());

        if(itemsModel.getIs_sync()== DataContract.SYNC_STATUS_FAILED){
            viewHolder.rvImgEdit.setVisibility(View.VISIBLE);
            viewHolder.rvImgSync.setVisibility(View.GONE);

        }
        else if(itemsModel.getIs_sync()==DataContract.SYNC_STATUS_OK){
            viewHolder.rvImgEdit.setVisibility(View.GONE);
            viewHolder.rvImgSync.setVisibility(View.VISIBLE);
        }
        viewHolder.rvImgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterClickListener.OnItemClicked(position);
            }
        });

        viewHolder.rvImgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterClickListener.OnDeleteClicked(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rv_docNo)
        TextView rvDocNo;
        @BindView(R.id.rv_staffName)
        TextView rvStaffName;
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
}

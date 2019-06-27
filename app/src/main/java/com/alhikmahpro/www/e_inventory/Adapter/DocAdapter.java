package com.alhikmahpro.www.e_inventory.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.ItemModel;
import com.alhikmahpro.www.e_inventory.Data.RuntimeData;
import com.alhikmahpro.www.e_inventory.Interface.OnAdapterClickListener;
import com.alhikmahpro.www.e_inventory.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DocAdapter extends RecyclerView.Adapter<DocAdapter.ViewHolder> {

    OnAdapterClickListener adapterClickListener;
    List<ItemModel> mList = new ArrayList<>();


    public DocAdapter(List<ItemModel> mList, OnAdapterClickListener adapterClickListener) {
        this.adapterClickListener = adapterClickListener;
        this.mList = mList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_doc_row, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ItemModel itemsModel = mList.get(position);

        holder.rvDocNo.setText(String.valueOf(itemsModel.getInvoiceNo()));
        holder.rvStaffName.setText(String.valueOf(itemsModel.getStaffName()));

        holder.rvDate.setText(itemsModel.getDate());

        if(itemsModel.getIs_sync()==DataContract.SYNC_STATUS_FAILED){
            holder.rvImgEdit.setVisibility(View.VISIBLE);
            holder.rvImgSync.setVisibility(View.GONE);

        }
        else if(itemsModel.getIs_sync()==DataContract.SYNC_STATUS_OK){
            holder.rvImgEdit.setVisibility(View.GONE);
            holder.rvImgSync.setVisibility(View.VISIBLE);
        }
        holder.rvImgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterClickListener.OnItemClicked(position);
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
        @BindView(R.id.rv_date)
        TextView rvDate;
        @BindView(R.id.rv_img_sync)
        ImageView rvImgSync;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}


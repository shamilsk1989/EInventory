package com.alhikmahpro.www.e_inventory.Adapter;

import android.content.Context;
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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    Context mContext;
    OnAdapterClickListener adapterClickListener;
    List<ItemModel>mList=new ArrayList<>();



    public CartAdapter(List<ItemModel> list,OnAdapterClickListener adapterClickListener) {
        this.adapterClickListener = adapterClickListener;
        this.mList=list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_row, parent, false);
       return  new ViewHolder(view);
        //return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ItemModel itemsModel = mList.get(position);

        holder.rvBarcode.setText(itemsModel.getBarCode());
        holder.txtQuantity.setText(String.valueOf(itemsModel.getQty()));
        holder.txtUnit.setText(String.valueOf(itemsModel.getSelectedPackage()));
        holder.rvItemName.setText(itemsModel.getProductName());

        holder.rvImgDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterClickListener.OnDeleteClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rv_barcode)
        TextView rvBarcode;
        @BindView(R.id.txt_quantity)
        TextView txtQuantity;
        @BindView(R.id.txtUnit)
        TextView txtUnit;
        @BindView(R.id.rv_img_del)
        ImageView rvImgDel;
        @BindView(R.id.rv_item_name)
        TextView rvItemName;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}

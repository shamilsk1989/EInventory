package com.alhikmahpro.www.e_inventory.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alhikmahpro.www.e_inventory.Data.SupplierModel;
import com.alhikmahpro.www.e_inventory.Interface.OnAdapterClickListener;
import com.alhikmahpro.www.e_inventory.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.ViewHolder> implements Filterable {


    List<SupplierModel> supplierList = new ArrayList<>();
    List<SupplierModel> supplierListFull = new ArrayList<>();
    OnAdapterClickListener onAdapterClickListener;
    String type;


    public SupplierAdapter(List<SupplierModel> supplierList, OnAdapterClickListener onAdapterClickListener) {

        this.supplierList = supplierList;
        this.onAdapterClickListener = onAdapterClickListener;
        supplierListFull = new ArrayList<>(supplierList);

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_rv_supplier, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {

        SupplierModel supplierModel = supplierList.get(position);
        viewHolder.textViewCode.setText(supplierModel.getSupplierCode());
        viewHolder.textViewName.setText(supplierModel.getSupplierName());
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAdapterClickListener.OnItemClicked(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return supplierList.size();
    }

    @Override
    public Filter getFilter() {
        return filterSupplierList;
    }

    private Filter filterSupplierList = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<SupplierModel> filterList = new ArrayList<>();
            if ((constraint == null) || (constraint.length() == 0)) {
                filterList.addAll(supplierList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (SupplierModel model : supplierListFull) {
                    if (model.getSupplierName().toLowerCase().contains(filterPattern)) {
                        filterList.add(model);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filterList;
            return results;


        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            supplierList.clear();
            supplierList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textViewCode)
        TextView textViewCode;
        @BindView(R.id.textViewName)
        TextView textViewName;

        @BindView(R.id.layout)
        LinearLayout layout;


        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}

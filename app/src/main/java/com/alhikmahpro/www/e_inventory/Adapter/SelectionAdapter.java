package com.alhikmahpro.www.e_inventory.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alhikmahpro.www.e_inventory.Data.SelectionModel;
import com.alhikmahpro.www.e_inventory.Interface.OnAdapterClickListener;
import com.alhikmahpro.www.e_inventory.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectionAdapter extends RecyclerView.Adapter<SelectionAdapter.SelectionViewHolder> implements Filterable {
    String selectionType;
    List<SelectionModel> selectionList = new ArrayList<>();
    List<SelectionModel> selectionListFull = new ArrayList<>();
    OnAdapterClickListener onAdapterClickListener;


    public SelectionAdapter(List<SelectionModel> selectionList,
                            OnAdapterClickListener onAdapterClickListener) {

        this.selectionList = selectionList;
        this.onAdapterClickListener = onAdapterClickListener;
        selectionListFull = new ArrayList<>(selectionList);
    }

    @Override
    public SelectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rv_item, parent, false);
        return new SelectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SelectionViewHolder holder, int position) {

        SelectionModel model = selectionList.get(position);
        holder.textViewCode.setText(model.getCode());
        holder.textViewName.setText(model.getName());
        holder.textViewPrice.setText(model.getPrice());


    }

    @Override
    public int getItemCount() {
        return selectionList.size();
    }

    @Override
    public Filter getFilter() {
        return filterList;
    }

    private Filter filterList = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<SelectionModel> filterList = new ArrayList<>();
            if ((constraint == null) || (constraint.length() == 0)) {
                filterList.addAll(selectionList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (SelectionModel model : selectionListFull) {
                    if (model.getName().toLowerCase().contains(filterPattern)) {
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

            selectionList.clear();
            selectionList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class SelectionViewHolder extends RecyclerView.ViewHolder {

//        @BindView(R.id.textViewName)
//        TextView textViewName;
//        @BindView(R.id.textViewPrice)
//        TextView textViewPrice;
//        @BindView(R.id.textViewCode)
//        TextView textViewCode;
//        @BindView(R.id.layout)
//        LinearLayout layout;

        TextView textViewCode;
        TextView textViewName;
        TextView textViewPrice;
        LinearLayout layout;

         SelectionViewHolder(View itemView) {
            super(itemView);
           // ButterKnife.bind(this, itemView);

            textViewCode=itemView.findViewById(R.id.textViewCode);
            textViewName=itemView.findViewById(R.id.textViewName);
            textViewPrice=itemView.findViewById(R.id.textViewPrice);
            layout=itemView.findViewById(R.id.layout);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position=getAdapterPosition();
                    onAdapterClickListener.OnItemClicked(position);


                }
            });
        }
    }
}

package com.alhikmahpro.www.e_inventory.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alhikmahpro.www.e_inventory.Data.Cart;
import com.alhikmahpro.www.e_inventory.Data.CartModel;
import com.alhikmahpro.www.e_inventory.Interface.OnAdapterClickListener;
import com.alhikmahpro.www.e_inventory.R;

import butterknife.BindView;

public class SaleCartAdapter extends RecyclerView.Adapter<SaleCartAdapter.SalesCartViewHolder> {
    Context mContext;
    //create separate interface for adapter click event
    OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public SaleCartAdapter(Context context) {
        this.mContext = context;
        //this.mListener = onAdapterClickListener;

    }

    @Override
    public SalesCartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_sales_cart_row, parent, false);
        return new SalesCartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SalesCartViewHolder holder, final int position) {

        CartModel model = Cart.mCart.get(position);
        holder.txtItemName.setText(model.getProductName());
        holder.txtNetAmount.setText(String.valueOf(model.getNet()));
        holder.txtUnitPrice.setText(String.valueOf(model.getQty()+ " X "+String.valueOf(model.getRate())));
        holder.imgDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener!=null){
                    mListener.onItemClick(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return Cart.mCart.size();
    }

    public class SalesCartViewHolder extends RecyclerView.ViewHolder {

        //        @BindView(R.id.rv_barcode)
//        TextView rvBarcode;
//        @BindView(R.id.txt_quantity)
//        TextView txtQuantity;
//        @BindView(R.id.txtUnit)
//        TextView txtUnit;
//        @BindView(R.id.rv_img_del)
//        ImageView rvImgDel;
//        @BindView(R.id.rv_item_name)
//        TextView rvItemName;
        TextView txtItemName;
        TextView txtNetAmount;
        ImageView imgDel;
        TextView txtUnitPrice;


        public SalesCartViewHolder(View itemView) {
            super(itemView);

            txtItemName=itemView.findViewById(R.id.rv_item_name);
            txtNetAmount=itemView.findViewById(R.id.rv_net_amount);
            imgDel=itemView.findViewById(R.id.rv_img_del);
            txtUnitPrice=itemView.findViewById(R.id.rv_unit_price);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (mListener != null) {
//                        int position = getAdapterPosition();
//                        if (position != RecyclerView.NO_POSITION) {
//                            mListener.onItemClick(position);
//                        }
//                    }
//                }
//            });


        }


    }
}

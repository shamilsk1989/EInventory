package com.alhikmahpro.www.e_inventory.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alhikmahpro.www.e_inventory.Data.Cart;
import com.alhikmahpro.www.e_inventory.Data.CartModel;
import com.alhikmahpro.www.e_inventory.R;

public class PrintViewAdapter extends RecyclerView.Adapter <PrintViewAdapter.PrintViewHolder>{
    Context context;

    public PrintViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public PrintViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_print_row,parent,false);
        return new PrintViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PrintViewHolder holder, int position) {
        CartModel cartModel=Cart.mCart.get(position);
        holder.textItem.setText(cartModel.getProductName());
        double qty=cartModel.getQty();
        double price=cartModel.getRate();
        holder.txtSubTotal.setText(String.valueOf(qty)+" X "+String.valueOf(price));
        holder.txtTotal.setText(String.valueOf(cartModel.getNet()));
       // holder.txtCode.setText(cartModel.getBarcode());

    }

    @Override
    public int getItemCount() {
        return Cart.mCart.size();
    }

    public class PrintViewHolder extends RecyclerView.ViewHolder{

        TextView textItem;
        TextView txtSubTotal;
        TextView txtTotal;
        TextView txtCode;

        public PrintViewHolder(View itemView) {
            super(itemView);

            textItem=itemView.findViewById(R.id.txtItem);
            txtTotal=itemView.findViewById(R.id.txtTotal);
            txtSubTotal=itemView.findViewById(R.id.txtSubTotal);
            txtCode=itemView.findViewById(R.id.txtCode);
        }
    }
}

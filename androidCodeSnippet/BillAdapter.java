package com.example.gunjan.clientapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gunjan.clientapp.R;
import com.example.gunjan.clientapp.indicator;
import com.example.gunjan.clientapp.order;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Gunjan on 11/27/2017.
 */

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.holder_one> {
ArrayList<order> orderList;
Context context;
    TextView Total_amount;
    ArrayList<indicator> itemcount;
    ArrayList<order> refineList;
     BillAdapter adapter ;
     Activity activity;
    public BillAdapter(Context context, ArrayList<order> orderList, TextView Total_amount,ArrayList<indicator> itemcount, ArrayList<order> refineList, Activity activity) {
     this.context= context;
     this.orderList = orderList;
        this.Total_amount = Total_amount;
        this.itemcount = itemcount;
        this.refineList = refineList;
        this.adapter = this;
        this.activity = activity;

    }

    @Override
    public holder_one onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.unit_bill,parent, false);
        return new holder_one(view);    }

    @Override
    public void onBindViewHolder(final holder_one holder, final int position){
       // utilizing the same layout file for showing the order and showing the headers of the table.
        // for position 0 headed and for other a flexible bill system
        if(position!=0){
            Picasso.with(context).load("http://questimpexcom.ipage.com/Qresturant/dishimg/"+orderList.get(position-1).getImage()).into(holder.Bill_image);
            holder.Bill_name.setText(orderList.get(position-1).getItem_name());
            holder.bill_unitPrice.setText(orderList.get(position-1).getAmount()+" ");
            holder.Bill_quantity.setText(orderList.get(position-1).getQuantity()+" ");
            holder.rev_minus.setVisibility(View.VISIBLE);
            holder.rev_plus.setVisibility(View.VISIBLE);
            holder.Bill_total.setText(orderList.get(position-1).getAmount()*orderList.get(position-1).getQuantity()+" ");
        }else{
            //
        }
        // event makes final increment  in the selected order
        holder.rev_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
int Count = orderList.get(position-1).getQuantity();
            if(Count>1){
                Count--;
                holder.Bill_quantity.setText(Count+" ");
                orderList.get(position-1).setQuantity(Count);
                holder.Bill_total.setText(orderList.get(position-1).getAmount()*orderList.get(position-1).getQuantity()+" ");
                int billamt = Integer.parseInt(Total_amount.getText().toString());
                billamt= billamt - orderList.get(position-1).getAmount();
                Total_amount.setText(String.valueOf(billamt));
               for(int p =0;p<itemcount.size();p++){
                   if(itemcount.get(p).getItem_name().equals(orderList.get(position-1).getItem_name())){
                       itemcount.get(p).setOrdernumber(Count);
                       refineList.remove(p);
                       Log.e("removed", "1");
                           break;
                   }
               }
                 }
            }
        });
// event makes final  decrement in the selected order
        holder.rev_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int Count = orderList.get(position-1).getQuantity();
                    Count++;
                    holder.Bill_quantity.setText(Count+" ");
                    orderList.get(position-1).setQuantity(Count);
                    holder.Bill_total.setText(orderList.get(position-1).getAmount()*orderList.get(position-1).getQuantity()+" ");
                int billamt = Integer.parseInt(Total_amount.getText().toString());
                billamt= billamt + orderList.get(position-1).getAmount();
                Total_amount.setText(String.valueOf(billamt));
                for(int p =0;p<itemcount.size();p++){
                    if(itemcount.get(p).getItem_name().equals(orderList.get(position-1).getItem_name())){
                        itemcount.get(p).setOrdernumber(Count);
                        refineList.add(new order("","","",orderList.get(position-1).getItem_name(),orderList.get(position-1).getAmount(),orderList.get(position-1).getImage(),1));
                        Log.e("added", "1");
                        break;
                    }
                }


            }
        });
// event makes an item remove from the final list
holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
    @Override
    public boolean onLongClick(View view) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(100);
        createDialog(position);

        return false;
    }
});

    }

    private void createDialog(final int position) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
        builder1.setMessage("Will remove the item from list");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        int amount = Integer.parseInt(Total_amount.getText().toString());
                      // adjustment in the total bill
                        amount = amount - orderList.get(position-1).getAmount()*orderList.get(position-1).getQuantity();
                        Total_amount.setText(String.valueOf(amount));
                        orderList.remove(position-1);

                        adapter.notifyDataSetChanged();
                    if(orderList.size()==0){
                        activity.finish();
                    }
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public int getItemCount() {
        return orderList.size()+1;
    }
// holder for elements in the view
    public class holder_one extends RecyclerView.ViewHolder{
        ImageView Bill_image;
        TextView Bill_name,bill_unitPrice,Bill_quantity,Bill_total;
        CircleImageView rev_minus, rev_plus;
        public holder_one(View itemView) {
            super(itemView);
            Bill_image =(ImageView) itemView.findViewById(R.id.Bill_image);
         Bill_name = (TextView) itemView.findViewById(R.id.Bill_name);
            bill_unitPrice =(TextView) itemView.findViewById(R.id.bill_unitPrice);
            rev_minus =(CircleImageView) itemView.findViewById(R.id.rev_minus);
            rev_plus =(CircleImageView) itemView.findViewById(R.id.rev_plus);
            Bill_quantity =(TextView) itemView.findViewById(R.id.Bill_quantity);
            Bill_total =(TextView) itemView.findViewById(R.id.Bill_total);


        }
    }
}

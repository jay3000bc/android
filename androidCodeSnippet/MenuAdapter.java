package com.example.gunjan.clientapp.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.gunjan.clientapp.Menu;
import com.example.gunjan.clientapp.R;
import com.example.gunjan.clientapp.ViewDishActivity;
import com.example.gunjan.clientapp.indicator;
import com.example.gunjan.clientapp.order;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Gunjan on 11/16/2017.
 */

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.Viewholder> {
    ArrayList<Menu> menuList;
    Context context, con;
      int count= 0;
    String phoneno;
    String name;
    String orderno;
    ArrayList<order> orderList;
ArrayList<indicator> itemcount;
    RequestQueue requestQueue;

    public MenuAdapter(ArrayList<Menu> menuList,Context context, String phoneno,String name,ArrayList<indicator> itemcount,ArrayList<order> orderList,Context con,RequestQueue requestQueue) {
        this.menuList = menuList;
        this.context =context;
        this.phoneno =phoneno;
        this.name = name;
        this.orderno = "12345";
        this.itemcount= itemcount;
        this.orderList= orderList;
        this.con =con;
        this.requestQueue=requestQueue;
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.unit_dish,parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(final Viewholder holder, final int position) {

        Picasso.with(context).load("http://questimpexcom.ipage.com/Qresturant/dishimg/"+menuList.get(position).getImage()).resize(100,100).placeholder(R.drawable.ctandoor).into(holder.imageView);
        holder.dish_description.setText(menuList.get(position).getName());
       final String itemname = menuList.get(position).getName();
       // for(int i =0;i<itemcount.size();i++){
         //   if(itemcount.get(i).getItem_name().equals(menuList.get(position).getName())){

                holder.view_count.setText(String.valueOf(itemcount.get(position).getOrdernumber()));
           //     break;
            //}
        //}
        final float i = Float.parseFloat(menuList.get(position).getTotalrate());
        final float j = Integer.parseInt(menuList.get(position).getNumber());
        holder.dish_review.setText(String.valueOf(i/j).substring(0,3));
        holder.dish_detail.setText(menuList.get(position).getDescription());
        final int amount = Integer.parseInt(menuList.get(position).getPrice());
        holder.dish_price.setText("Rs. "+menuList.get(position).getPrice()+"/-");
       // increasing the number of dish to order
        holder.increase.setOnClickListener(new View.OnClickListener() {
            @Override
             public void onClick(View view) {
                count = Integer.parseInt(holder.view_count.getText().toString().trim().toString());
                count =count+1;
                itemcount.get(position).setOrdernumber(count);
                holder.view_count.setText(count+" ");
                orderList.add(new order(phoneno,name,orderno,menuList.get(position).getName(),Integer.parseInt( menuList.get(position).getPrice()),menuList.get(position).getImage(),1));
                Log.e("name",menuList.get(position).getName());
                Log.e("price", menuList.get(position).getPrice());

                Toast.makeText(context,"Size of order`` "+orderList.size(),Toast.LENGTH_SHORT).show();
            }
        });
        // decreasing the number of dish to order
holder.decrease.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        count = Integer.parseInt(holder.view_count.getText().toString().trim().toString());

        if(count>0){
            count --;

            itemcount.get(position).setOrdernumber(count);

            holder.view_count.setText(count+" ");
            for(int i=0;i<orderList.size();i++){
                Log.e(orderList.get(i).getItem_name(),itemname);
                if(orderList.get(i).getItem_name().equals(itemname)){
                    orderList.remove(i);
                    break;
                }
            }
            Toast.makeText(context,"Size of order "+orderList.size(),Toast.LENGTH_SHORT).show();
        }
    }
});
        // Shows the detail of dish onclick in the view
holder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(con);
        alertDialog.setTitle("DETAIL");
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View ve = inflater.inflate(R.layout.review, null, false);
        alertDialog.setView(ve);
        final RatingBar ratingBar = (RatingBar) ve.findViewById(R.id.ratingBar);
        ImageView imv= (ImageView) ve.findViewById(R.id.image_dishh);
        TextView dish_rating_name = ve.findViewById(R.id.dish_rating_name);
        dish_rating_name.setText(menuList.get(position).getName());
        TextView dish_rating_description =(TextView) ve.findViewById(R.id.dish_rating_description);
        dish_rating_description.setText(menuList.get(position).getDescription());
        Picasso.with(context).load("http://questimpexcom.ipage.com/Qresturant/dishimg/"+menuList.get(position).getImage()).into(imv);
        ratingBar.setEnabled(false);
        ratingBar.setRating(i/j);
        TextView dish_price = (TextView) ve.findViewById(R.id.dish_price);
        CircleImageView decreaseone =(CircleImageView) ve.findViewById(R.id.decreaseone);
        CircleImageView increaseone =(CircleImageView) ve.findViewById(R.id.increaseone);
        final TextView view_countone =(TextView) ve.findViewById(R.id.view_countone);
        view_countone.setText(String.valueOf(itemcount.get(position).getOrdernumber()));


        // on click listners
        decreaseone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = Integer.parseInt(holder.view_count.getText().toString().trim().toString());

                if(count>0){
                    count --;

                    itemcount.get(position).setOrdernumber(count);

                    holder.view_count.setText(count+" ");
                    view_countone.setText(count+" ");
                    for(int i=0;i<orderList.size();i++){
                        Log.e(orderList.get(i).getItem_name(),itemname);
                        if(orderList.get(i).getItem_name().equals(itemname)){
                            orderList.remove(i);
                            break;
                        }
                    }
                    Toast.makeText(context,"Size of order "+orderList.size(),Toast.LENGTH_SHORT).show();
                }

            }
        });

        increaseone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = Integer.parseInt(holder.view_count.getText().toString().trim().toString());
                count =count+1;
                itemcount.get(position).setOrdernumber(count);
                holder.view_count.setText(count+" ");
                view_countone.setText(count+" ");
                orderList.add(new order(phoneno,name,orderno,menuList.get(position).getName(), Integer.parseInt(menuList.get(position).getPrice()), menuList.get(position).getImage(),1));
                Log.e("name",menuList.get(position).getName());
                Log.e("price", menuList.get(position).getPrice());

                Toast.makeText(context,"Size of order`` "+orderList.size(),Toast.LENGTH_SHORT).show();

            }
        });

        dish_price.setText("Rs. "+menuList.get(position).getPrice()+"/-");
        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton("ORDER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.create().show();

    }
});

    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }
    // Class to hold the view items
public class Viewholder extends RecyclerView.ViewHolder{
ImageView imageView;
TextView dish_description,dish_review,dish_detail,view_count,dish_price;
CircleImageView decrease,increase;
        public Viewholder(View itemView) {
            super(itemView);
            imageView =(ImageView) itemView.findViewById(R.id.dish_image);
            dish_description =(TextView) itemView.findViewById(R.id.dish_description);
            dish_review =(TextView) itemView.findViewById(R.id.dish_review);
            dish_detail =(TextView) itemView.findViewById(R.id.dish_detail);
            decrease =(CircleImageView) itemView.findViewById(R.id.decrease);
            increase =(CircleImageView) itemView.findViewById(R.id.increase);
            view_count =(TextView) itemView.findViewById(R.id.view_count);
            dish_price =(TextView) itemView.findViewById(R.id.dish_price);


        }
    }
}

package com.example.gunjan.clientapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gunjan.clientapp.Adapter.BillAdapter;

import java.util.ArrayList;

public class BillingActivity extends AppCompatActivity {
private  ArrayList<order> orderList;
private RecyclerView recyclerView;
private ArrayList<Menu> menuList;
private TextView Total_amount;
private ArrayList<order> revisedOrder = new ArrayList<>();
private Button Cancel_order,Place_order;
    ArrayList<order> fucList = new ArrayList<>();
    private ArrayList<indicator> itemCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);
        recyclerView =(RecyclerView) findViewById(R.id.Bill);
        Total_amount =(TextView) findViewById(R.id.Total_amount);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderList = (ArrayList<order>) getIntent().getSerializableExtra("order");
        menuList = (ArrayList<Menu>) getIntent().getSerializableExtra("menu");
        itemCount =(ArrayList<indicator>) getIntent().getSerializableExtra("itemc");
        for(int i =0;i<orderList.size();i++){
            fucList.add(orderList.get(i));
        }
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        final ArrayList<order> refineList = ExtractBill();
        recyclerView.setAdapter(new BillAdapter(getApplicationContext(),refineList,Total_amount,itemCount,fucList,this));
        Cancel_order =(Button) findViewById(R.id.Cancel_order);
        Place_order =(Button) findViewById(R.id.Place_order);
        Toast.makeText(getApplicationContext(),"size "+ ExtractBill().size(),Toast.LENGTH_SHORT).show();
        Cancel_order.setText("CONTINUE ORDERING");
// event finishes the current activity to contitue the ordering
        Cancel_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
// event continues to send the order to admin
        Place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             Intent intent = new Intent(BillingActivity.this,PlaceOrderActivity.class);
                intent.putExtra("finalorder",refineList);
            //    intent.putExtra("phone",PHONE);
                startActivity(intent);
            }
        });
        int sum =0;
// code computes the total amount of the order till now
        for (int i =0;i<refineList.size();i++){
    sum = sum + refineList.get(i).getQuantity()* refineList.get(i).getAmount();
}
Total_amount.setText(String.valueOf(sum));
    }
// method extracts from an arraylist of items selected for order, a bill
// like arrangement
    private ArrayList<order> ExtractBill() {

        ArrayList<order> refine = new ArrayList<>();
        refine.add(orderList.get(0));
        for(int i=0;i<orderList.size();i++){
            for(int j=0;j<refine.size();j++){
                    if(orderList.get(i).getItem_name().equals(refine.get(j).getItem_name())){
                    Log.e("stat", "break");
                  //  Log.e("stat",orderList.get(i).getName());
                    //Log.e("stat",refine.get(i).getName());
                    break;
                }else {
                    if(j==refine.size()-1){
                        refine.add(orderList.get(i));
                        Log.e("stat", "add");
                    }else{
                        continue;
                    }
                }
            }
        }
int count =0;
        for(int k=0;k<refine.size();k++){
            count=0;
            for (int l=0;l<orderList.size();l++){
                if(refine.get(k).getItem_name().equals(orderList.get(l).getItem_name())){
                 count++;
                }
            }

            refine.get(k).setQuantity(count);
            Log.e("quantity",String.valueOf(refine.get(k).getQuantity()));
        }

      //  Toast.makeText(getApplicationContext(),"size "+ refine.size(),Toast.LENGTH_SHORT).show();
        return refine;
    }


}

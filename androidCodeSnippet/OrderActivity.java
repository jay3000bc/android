package com.example.gunjan.clientapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gunjan.clientapp.Adapter.MenuAdapter;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

public class OrderActivity extends AppCompatActivity {
private StringRequest stringRequest;//Volley Network code
private RequestQueue requestQueue;//
private RecyclerView recyclerView;// recyclerview for menu
private Button Proceed_cart;    // proceed with the selection
private ArrayList<Menu> MenuList ;// holds the menu
    ArrayList<indicator> itemcount;
    ArrayList <String> category = new ArrayList<>();
ArrayList<order> orderList;
String NAME, PHONE;
    String check = null ;
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        FirebaseMessaging.getInstance().subscribeToTopic("client");

        requestQueue = Volley.newRequestQueue(getApplicationContext());
         NAME = getIntent().getStringExtra("name");
        PHONE = getIntent().getStringExtra("phone");
         check = getIntent().getStringExtra("haha");
        if(TextUtils.isEmpty(check)){
            itemcount= new ArrayList<>();
            MenuList = new ArrayList<>();
            orderList = new ArrayList<order>();
        }else{
            itemcount = (ArrayList<indicator>)getIntent().getSerializableExtra("itemcount");
            MenuList =(ArrayList<Menu>)getIntent().getSerializableExtra("menu");
            orderList =(ArrayList<order>) getIntent().getSerializableExtra("order");
        }
        recyclerView= (RecyclerView) findViewById(R.id.Menu_recycler);
        Proceed_cart =(Button) findViewById(R.id.Proceed_button);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        Proceed_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(orderList.size()==0){
        Toast.makeText(getApplicationContext(),"You have selected nothing yet!",Toast.LENGTH_SHORT).show();
               }else{
                   Intent intent = new Intent(OrderActivity.this,BillingActivity.class);
                   intent.putExtra("order",orderList);
                   intent.putExtra("menu", MenuList);
                   intent.putExtra("itemc", itemcount);
                   intent.putExtra("phone",PHONE);
                   startActivity(intent);
               }
            }
        });
    if(TextUtils.isEmpty(check)){
        GetMenu();
    }else {
        MenuAdapter adapter = new MenuAdapter(MenuList, getApplicationContext(),PHONE,NAME,itemcount,orderList,OrderActivity.this, requestQueue);
        recyclerView.setAdapter(adapter);
    }

    }
// retrive a json file contatining the menu items in the list
    private void GetMenu() {
    stringRequest = new StringRequest(Request.Method.GET, "http://questimpexcom.ipage.com/Qresturant/menu.php", new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {

            JsonDecode(response);


        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    });

    requestQueue.add(stringRequest);

    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.order_menu,menu);
        // Search bar in ActionBAr
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_searchOne));

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        editText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       if(item.getItemId()==R.id.men_category){
         ShowCategory();

       }else if(item.getItemId()==R.id.my_account){
           Intent intent = new Intent(OrderActivity.this,MyAccountActivity.class);
           intent.putExtra("phone",PHONE);
           intent.putExtra("name", NAME);
           startActivity(intent);
       }else if(item.getItemId() == R.id.my_message){
        startActivity(new Intent(OrderActivity.this,NoticeActivity.class));

       }
       else if(item.getItemId()==R.id.log_out){
          // Toast.makeText(getApplicationContext(),"not configured yet",Toast.LENGTH_SHORT).show();
           SharedPreferences sharedPreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
           SharedPreferences.Editor editor = sharedPreferences.edit();
           editor.clear().commit();
           startActivity(new Intent(OrderActivity.this,MainActivity.class));

       }else if(item.getItemId() == R.id.action_searchOne){
           editText.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

               }

               @Override
               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

               }

               @Override
               public void afterTextChanged(Editable editable) {
                    // Search for particular dish
               ArrayList<Menu> searchedMenu = new ArrayList<Menu>();
               ArrayList<indicator> newInd = new ArrayList<indicator>();

                   for(int i =0;i<MenuList.size();i++){
                   if(MenuList.get(i).getName().toLowerCase().contains(editable.toString().toLowerCase())){
                       searchedMenu.add(MenuList.get(i));
                       newInd.add(itemcount.get(i));
                   }
               }
                   MenuAdapter menuAdapter1 = new MenuAdapter(searchedMenu,getApplicationContext(),PHONE,NAME,newInd,orderList,OrderActivity.this,requestQueue);
                   recyclerView.setAdapter(menuAdapter1);

               }
           });
       }
        return super.onOptionsItemSelected(item);
    }
    private void ShowCategory() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        //alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setTitle("Categories");
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View ve = inflater.inflate(R.layout.category_list, null, false);
        alertDialog.setView(ve);
        alertDialog.setCancelable(true);
        ListView lv =(ListView) ve.findViewById(R.id.list_category);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1,refine());
        lv.setAdapter(adapter);
        lv.setBackgroundColor(Color.CYAN);
        final AlertDialog builder = alertDialog.create();
        builder.show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               builder.dismiss();
                ArrayList<Menu> newMenuList = new ArrayList<Menu>();
               ArrayList<indicator> newInd = new ArrayList<indicator>();
                for(int p=0;p<MenuList.size();p++){
                    if(MenuList.get(p).getType().equals(refine().get(i))){
                        newMenuList.add(MenuList.get(p));
                         newInd.add(itemcount.get(p));
                    }
                }
                MenuAdapter menuAdapter1 = new MenuAdapter(newMenuList,getApplicationContext(),PHONE,NAME,newInd,orderList,OrderActivity.this,requestQueue);
                recyclerView.setAdapter(menuAdapter1);

            }
        });
        //alertDialog.create().show();



    }

    // Method extracts the category from the given menu items.
    private ArrayList<String> refine() {
    int size =category.size();
    ArrayList<String> refine = new ArrayList<>();
      refine.add(category.get(0));
        for(int i=0;i<category.size();i++){
            for(int j=0;j<refine.size();j++){
                if(category.get(i).equals(refine.get(j))){
                    break;
                }else {
                    if(j==refine.size()-1){
                       refine.add(category.get(i));
                    }
                }
            }
        }
    return refine;
    }

// Decode the json and sends it to the adapter.
    private void JsonDecode(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray("resultone");
            for (int i = 0; i < result.length(); i++) {
                // bitarray.add(collegeData.getString("image").toString());
                JSONObject collegeData = result.getJSONObject(i);
                Menu menu = new Menu();
                menu.setName(collegeData.getString("name"));
                menu.setDescription(collegeData.getString("description"));
                menu.setImage(collegeData.getString("image"));
                menu.setNumber(collegeData.getString("number"));
                menu.setTotalrate(collegeData.getString("totalrate"));
                menu.setType(collegeData.getString("trpe"));
                menu.setPrice(collegeData.getString("price"));
                MenuList.add(menu);
                category.add(collegeData.getString("trpe"));
            }

            for(int j =0;j<MenuList.size();j++){
                itemcount.add(new indicator(MenuList.get(j).getName(),0));
            }
            MenuAdapter adapter = new MenuAdapter(MenuList, getApplicationContext(),PHONE,NAME,itemcount,orderList,OrderActivity.this, requestQueue);
            recyclerView.setAdapter(adapter);
        Toast.makeText(getApplicationContext(),"Total size"+ MenuList.size(),Toast.LENGTH_SHORT).show();
        }catch (JSONException e) {
            e.printStackTrace();
        }


    }
}

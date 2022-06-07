package com.example.tricoins.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tricoins.AllItems;
import com.example.tricoins.AllWinItems;
import com.example.tricoins.CustomListViewPerItem;
import com.example.tricoins.DashItems;
import com.example.tricoins.ItemDetails;
import com.example.tricoins.MainActivity;
import com.example.tricoins.R;
import com.example.tricoins.sqlitehelpers.DatabaseHelper;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;

public class AllWinningItems extends AppCompatActivity {

    ArrayList<DashItems> Items=null;
    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView txtcount;
    Toolbar backbtn;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_winning_items2);

        listView=findViewById(R.id.l3view);
        backbtn=findViewById(R.id.backbtn);
        sharedPreferences=this.getSharedPreferences("MyRef", Context.MODE_PRIVATE);

        HashMap<String, String> params = new HashMap<>();
        params.put("TimeCap", sharedPreferences.getString("1TimeCap", "0"));
        params.put("Digits", sharedPreferences.getString("1Digits", "0"));
        backbtn.setTitle(sharedPreferences.getString("1Digits", "0")+" digits");

        swipeRefreshLayout=findViewById(R.id.swiperefresh1);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                refreshItemList( sharedPreferences.getString("1TimeCap", "0"),sharedPreferences.getString("1Digits", "0"),sharedPreferences.getString("1Sysddate", "0"));

            }
        });

        /*PerformNetworkRequest request =new PerformNetworkRequest(Api.URL_LIST_ITEMS_BY_TIMECAP_AND_DIGITS, params, CODE_POST_REQUEST);
        request.execute();*/

        refreshItemList( sharedPreferences.getString("1TimeCap", "0"),sharedPreferences.getString("1Digits", "0"),sharedPreferences.getString("1Sysddate", "0"));
        listView.setOnItemClickListener(this::onItemClick);


        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("1detailsback1","0");
        editor.apply();
    }
    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);

        final DashItems iteme = (DashItems) l.getAdapter().getItem(position);

        SharedPreferences sharedPreferences=getSharedPreferences("MyRef", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Digits",""+iteme.getDigits());
        editor.putString("Amount",""+iteme.getAmount());
        editor.putString("TimeCap",iteme.getTimeCap());
        editor.putString("Sysddate",iteme.getSysddate());
        editor.putString("qrcode",iteme.getqrcode());
        editor.putString("Types",iteme.getMatchType());
        editor.putString("1detailsback1","1");
        editor.apply();

        Intent i=new Intent(getApplicationContext(), ItemDetails.class);
        startActivity(i);
        this.overridePendingTransition(R.anim.slide_right, R.anim.slide_up);

    }

    public void onBackEmail(View view)
    {

        if(sharedPreferences.getString("1intent", "0").equals("3"))
        {
            Intent i = new Intent(getApplicationContext(), AdminMainActivity.class);
            startActivity(i);
            ((Activity) this).overridePendingTransition(R.anim.slide_left, R.anim.slide_down);
            finish();
        }
        else
        {
            Intent i = new Intent(getApplicationContext(), AllWinItems.class);
            startActivity(i);
            ((Activity) this).overridePendingTransition(R.anim.slide_left, R.anim.slide_down);
            finish();
        }
    }

    private void refreshItemList(String TimeCap,String Digit,String Sysddate){

        Items=new ArrayList<DashItems>();
        DatabaseHelper mydb=new DatabaseHelper(this);

        Cursor res=mydb.getWinningNumbersList(TimeCap,Digit,Sysddate);

        if(res.getCount()>0) {

            while(res.moveToNext())
            {
                Items.add(new DashItems(
                        Integer.parseInt(res.getString(0)),
                        Integer.parseInt(res.getString(1)),
                        res.getString(2),
                        res.getString(3),
                        res.getString(4),
                        "",
                        res.getString(5),
                        res.getString(6),
                        res.getString(7),
                        res.getString(8),
                        res.getString(9),
                        0
                ));
            }

            CustomListViewPerItem customListView=new CustomListViewPerItem(this,R.layout.customlvperitems,Items);

            listView.setAdapter(customListView);
        }
        else
        {
            Snackbar.make( findViewById(android.R.id.content), "Time schedule is currently empty", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }


        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
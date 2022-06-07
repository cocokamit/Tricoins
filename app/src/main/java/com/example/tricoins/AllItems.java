package com.example.tricoins;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tricoins.admin.AdminMainActivity;
import com.example.tricoins.admin.DailyEntriesFragment;
import com.example.tricoins.sqlitehelpers.DatabaseHelper;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AllItems extends AppCompatActivity {
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    ArrayList<DashItems> Items=null;
    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView txtcount;
    Toolbar backbtn;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_items);

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
                refreshItemList( sharedPreferences.getString("1TimeCap", "0"),sharedPreferences.getString("1Digits", "0"));
               /* PerformNetworkRequest request =new PerformNetworkRequest(Api.URL_LIST_ITEMS_BY_TIMECAP_AND_DIGITS, params, CODE_POST_REQUEST);
                request.execute();*/
            }
        });

      /*  PerformNetworkRequest request =new PerformNetworkRequest(Api.URL_LIST_ITEMS_BY_TIMECAP_AND_DIGITS, params, CODE_POST_REQUEST);
        request.execute();*/
        refreshItemList( sharedPreferences.getString("1TimeCap", "0"),sharedPreferences.getString("1Digits", "0"));
        listView.setOnItemClickListener(this::onItemClick);
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
        editor.apply();

        Intent i=new Intent(getApplicationContext(), ItemDetails.class);
        startActivity(i);
        this.overridePendingTransition(R.anim.slide_right, R.anim.slide_up);

    }



    //ListGetter

   /* private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;

        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {

                    refreshItemList(object.getJSONArray("items"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);


            if (requestCode == CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }
*/
    //on press back button
    public void onBackEmail(View view)
    {
        if( sharedPreferences.getString("1intent", "0").equals("0")){
        Intent i=new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        ((Activity)this).overridePendingTransition(R.anim.slide_left, R.anim.slide_down);
        finish();
        }
        else
        {
            Intent i=new Intent(getApplicationContext(), AdminMainActivity.class);
            startActivity(i);
            ((Activity)this).overridePendingTransition(R.anim.slide_left, R.anim.slide_down);
            finish();
        }
    }

    //ListRefresher
   /* private void refreshItemList(JSONArray heroes) throws JSONException {

        Items=new ArrayList<DashItems>();

        for (int i = 0; i < heroes.length(); i++) {
            JSONObject obj = heroes.getJSONObject(i);

            Items.add(new DashItems(
                    obj.getInt("Id"),
                    obj.getInt("AgentId"),
                    obj.getString("Agent"),
                    obj.getInt("Digits"),
                    obj.getString("Amount"),
                    "",
                    obj.getString("qrcode"),
                    obj.getString("Description"),
                    obj.getString("Type"),
                    obj.getString("Sysddate"),
                    obj.getString("TimeCap")
            ));
        }

        *//*txtcount.setText(""+heroes.length());*//*

        CustomListViewPerItem customListView=new CustomListViewPerItem(this,R.layout.customlvperitems,Items);

        listView.setAdapter(customListView);

        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
*/


    private void refreshItemList(String TimeCap,String Digit){

        Items=new ArrayList<DashItems>();
        DatabaseHelper mydb=new DatabaseHelper(AllItems.this);


        SharedPreferences sharedPreferences = getSharedPreferences("MyRef", Context.MODE_PRIVATE);
        String dd=sharedPreferences.getString("1tabintent", "0");

        Cursor res=mydb.getAllDataByTimeCapAndDigits(TimeCap,Digit);
        if(dd.equals("0"))
        {
            res=mydb.getAllDataByTimeCapAndDigits(TimeCap,Digit);
        }
        else
        {
            res=mydb.getAllDataByTimeCapAndDigitsRam(TimeCap,Digit);
        }

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







package com.example.tricoins;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tricoins.sqlitehelpers.DatabaseHelper;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class StraightviewFragment extends Fragment {


    public StraightviewFragment() {
        // Required empty public constructor
    }

    ArrayList<DashItems> Items=null;
    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView txttitle,txtcount;
    static String matchchecker, tempmatchecker="";
     View view;

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        setHasOptionsMenu(true);
         view = inflater.inflate(R.layout.fragment_straightview, container, false);

        listView=view.findViewById(R.id.l3view);
        txttitle=view.findViewById(R.id.list_title);
        txtcount=view.findViewById(R.id.list_count);
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("MyRef", Context.MODE_PRIVATE);
        String checker=sharedPreferences.getString("1machchecker", "0");

       if(!checker.equals("")){

            matchchecker=checker;
        }
        else {
           Date currentTime = Calendar.getInstance().getTime();
           int hh = currentTime.getHours();
           if (hh < 21) {
               matchchecker = "9 P.M.";
               if (hh < 17) {
                   matchchecker = "5 P.M.";

                   if (hh < 14) {
                       matchchecker = "2 P.M.";
                   }
               }
           }
       }
        txttitle.setText(matchchecker);
        swipeRefreshLayout=view.findViewById(R.id.swiperefresh1);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                PerformNetworkRequest request =new PerformNetworkRequest(Api.URL_LIST_ITEMS_BY_TIMECAP+matchchecker+"&Type=Straight", null, CODE_GET_REQUEST);
                request.execute();

               // refreshItemList(matchchecker);
            }
        });
String sds=Api.URL_LIST_ITEMS_BY_TIMECAP+matchchecker+"&Type=Straight";
        PerformNetworkRequest request =new PerformNetworkRequest(Api.URL_LIST_ITEMS_BY_TIMECAP+matchchecker+"&Type=Straight", null, CODE_GET_REQUEST);
        request.execute();
        //refreshItemList(matchchecker);
        listView.setOnItemClickListener(this::onItemClick);

        return view;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        PerformNetworkRequest request;
        switch (item.getItemId()){
            case R.id.eleven:
                tempmatchecker="2 P.M.";
                matchchecker=tempmatchecker;
                request = new PerformNetworkRequest(Api.URL_LIST_ITEMS_BY_TIMECAP+"2 P.M."+"&Type=Straight", null, CODE_GET_REQUEST);
                request.execute();
                //refreshItemList(tempmatchecker);
                break;

            case R.id.four:
                tempmatchecker="5 P.M.";
                matchchecker=tempmatchecker;
                request = new PerformNetworkRequest(Api.URL_LIST_ITEMS_BY_TIMECAP+"5 P.M."+"&Type=Straight", null, CODE_GET_REQUEST);
                request.execute();
                //refreshItemList(tempmatchecker);
                break;

            case R.id.nine:
                tempmatchecker="9 P.M.";
                matchchecker=tempmatchecker;
                request = new PerformNetworkRequest(Api.URL_LIST_ITEMS_BY_TIMECAP+"9 P.M."+"&Type=Straight", null, CODE_GET_REQUEST);
                request.execute();
                //refreshItemList(tempmatchecker);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {

        final DashItems iteme = (DashItems) l.getAdapter().getItem(position);

        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("MyRef", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("1Digits",""+iteme.getDigits());
        editor.putString("1TimeCap",iteme.getTimeCap());
        editor.putString("1tabintent","0");
        editor.apply();

        Intent i=new Intent(getActivity(), AllItems.class);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.slide_right, R.anim.slide_up);
    }


  /*  //ListRefresher
    private void refreshItemList(String match){

        Items=new ArrayList<DashItems>();
        DatabaseHelper mydb=new DatabaseHelper(getActivity());

        Cursor res=mydb.getAllDataByDateAndType(match,"Straight");
        if(res.getCount()>0) {

            while(res.moveToNext())
            {
                Items.add(new DashItems(
                        Integer.parseInt(res.getString(0)),
                        Integer.parseInt(res.getString(1)),
                        res.getString(2),
                        res.getString(3),
                        res.getString(4),
                        res.getString(5),
                        res.getString(6),
                        res.getString(7),
                        res.getString(8),
                        res.getString(9),
                        res.getString(10),
                        Integer.parseInt(res.getString(11))
                ));
            }
            SharedPreferences sharedPreferences=getActivity().getSharedPreferences("MyRef", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("1machchecker",match);
            editor.apply();

            txttitle.setText(match);
            txtcount.setText(""+res.getCount());

            CustomListView customListView=new CustomListView(getActivity(),R.layout.customlv,Items);
            listView.setAdapter(customListView);
        }
        else
        {
           *//* Snackbar.make( getActivity().findViewById(android.R.id.content), "Time schedule is currently empty", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();*//*
        }


        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }*/


    //ListRefresher
    private void refreshItemList(JSONArray heroes) throws JSONException {

        Items=new ArrayList<DashItems>();

        for (int i = 0; i < heroes.length(); i++) {
            JSONObject obj = heroes.getJSONObject(i);

            Items.add(new DashItems(
                    obj.getInt("Id"),
                    obj.getInt("AgentId"),
                    obj.getString("Agent"),
                    obj.getString("Digits"),
                    obj.getString("Amount"),
                    obj.getString("Typecount"),
                    obj.getString("qrcode"),
                    obj.getString("Description"),
                    obj.getString("Type"),
                    obj.getString("Sysddate"),
                    obj.getString("TimeCap"),
                    obj.getInt("Limits")
            ));
        }


        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("MyRef", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("1machchecker",matchchecker);
        editor.apply();

        txttitle.setText(matchchecker);
        txtcount.setText(""+ heroes.length());

        CustomListView customListView=new CustomListView(getActivity(),R.layout.customlv,Items);
        listView.setAdapter(customListView);

        /*if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }*/
    }


    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
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
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
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

}
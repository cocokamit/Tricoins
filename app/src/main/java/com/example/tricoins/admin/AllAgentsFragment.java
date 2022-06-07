package com.example.tricoins.admin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tricoins.Accounts;
import com.example.tricoins.AllItems;
import com.example.tricoins.Api;
import com.example.tricoins.CustomListView;
import com.example.tricoins.CustomListViewPerItem;
import com.example.tricoins.DashItems;
import com.example.tricoins.MainActivity;
import com.example.tricoins.R;
import com.example.tricoins.RequestHandler;
import com.example.tricoins.sqlitehelpers.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AllAgentsFragment extends Fragment {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    ArrayList<Accounts> Items=null;
    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View listViewItem = inflater.inflate(R.layout.fragment_allagents,container,false);
        listView=listViewItem.findViewById(R.id.l3view);

        swipeRefreshLayout=listViewItem.findViewById(R.id.swiperefresh1);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
               /* PerformNetworkRequest request =new PerformNetworkRequest(Api.URL_READ_ACOUNT, null, CODE_GET_REQUEST);
                request.execute();*/
                refreshItemList();
            }
        });

        /*PerformNetworkRequest request =new PerformNetworkRequest(Api.URL_READ_ACOUNT, null, CODE_GET_REQUEST);
        request.execute();
        */
        refreshItemList();
        listView.setOnItemClickListener(this::onItemClick);

        return listViewItem;

    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {

        final Accounts iteme = (Accounts) l.getAdapter().getItem(position);

        SharedPreferences sharedPreferences=this.getActivity().getSharedPreferences("MyRef", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("AgentName",""+iteme.getFullName());
        editor.putString("AgentId",""+iteme.getId());
        editor.apply();

        Intent i=new Intent(this.getActivity(), AgentDetails.class);
        startActivity(i);
        this.getActivity().overridePendingTransition(R.anim.slide_right, R.anim.slide_up);

    }

    private void refreshItemList(){

        Items=new ArrayList<Accounts>();
        DatabaseHelper mydb=new DatabaseHelper(getActivity());

        Cursor res=mydb.getAllAccData();
        if(res.getCount()>0) {

            while(res.moveToNext())
            {
                Items.add(new Accounts(
                        Integer.parseInt(res.getString(0)),
                        res.getString(1),
                        res.getString(2),
                        res.getString(3),
                        res.getString(4),
                        res.getString(5),
                        res.getString(6),
                        res.getString(7)
                ));
            }


            CustomListViewAgentList customListView=new CustomListViewAgentList(this.getActivity(),R.layout.admincustomagentlist,Items);

            listView.setAdapter(customListView);

        }

        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }




    //ListGetter

 /*   private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
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
                    refreshItemList(object.getJSONArray("users"));
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


    //ListRefresher
    private void refreshItemList(JSONArray heroes) throws JSONException {

        Items=new ArrayList<Accounts>();

        for (int i = 0; i < heroes.length(); i++) {
            JSONObject obj = heroes.getJSONObject(i);

            Items.add(new Accounts(
                    obj.getInt("Id"),
                    obj.getString("Fullname"),
                    obj.getString("Username"),
                    obj.getString("Password"),
                    obj.getString("Status"),
                    obj.getString("Type"),
                    obj.getString("Collectedentry"),
                    obj.getString("Collectedamount")
                    ));
        }


        CustomListViewAgentList customListView=new CustomListViewAgentList(this.getActivity(),R.layout.admincustomagentlist,Items);

        listView.setAdapter(customListView);

        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }*/


}

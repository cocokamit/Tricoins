package com.example.tricoins.admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tricoins.Accounts;
import com.example.tricoins.Api;
import com.example.tricoins.CreateTicket;
import com.example.tricoins.R;
import com.example.tricoins.RequestHandler;
import com.example.tricoins.networkchecker.isNetworkConnected;
import com.example.tricoins.sqlitehelpers.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class CreateUserFragment extends Fragment {


    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    public boolean connection;
    ArrayList<Accounts> Items=null;
    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View listViewItem = inflater.inflate(R.layout.fragment_createuser,container,false);

        fab = listViewItem.findViewById(R.id.fab);
        listView=listViewItem.findViewById(R.id.l3view);
        swipeRefreshLayout=listViewItem.findViewById(R.id.swiperefresh1);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                PerformNetworkRequest request =new PerformNetworkRequest(Api.URL_READ_ACOUNT, null, CODE_GET_REQUEST);
                request.execute();
               /* refreshItemList();*/
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        openDialog();
            }
        });

        PerformNetworkRequest request =new PerformNetworkRequest(Api.URL_READ_ACOUNT, null, CODE_GET_REQUEST);
        request.execute();

        /*refreshItemList();*/

        listView.setOnItemClickListener(this::onItemClick);


        return listViewItem;
    }



    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static boolean connectGoogle() {
        try {
            HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
            urlc.setRequestProperty("User-Agent", "Test");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(10000);
            urlc.connect();
            return (urlc.getResponseCode() == 200);

        } catch (IOException e) {

            Log.d("GooglePing","IOEXCEPTION");
            e.printStackTrace();
            return false;
        }
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


            CustomListCreateUser customListView=new CustomListCreateUser(this.getActivity(),R.layout.admincustomcreateuser,Items);

            listView.setAdapter(customListView);

        }

        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    public void openDialog()
    {
        isNetworkConnected isnetwork=new isNetworkConnected(getActivity());
        if(isnetwork.Connected()) {

        CreateUserDialog createUserDialog = new CreateUserDialog();
        createUserDialog.show(getActivity().getSupportFragmentManager(), "Account");
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("MyRef", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("AccNo", "0");
        editor.apply();

        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_ACOUNT, null, CODE_GET_REQUEST);
        request.execute();
        }

    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        isNetworkConnected isnetwork=new isNetworkConnected(getActivity());
        if(isnetwork.Connected()) {
            final Accounts iteme = (Accounts) l.getAdapter().getItem(position);

            SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("MyRef", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("AccName", "" + iteme.getFullName());
            editor.putString("AccUsername", "" + iteme.getUsername());
            editor.putString("AccPassword", "" + iteme.getPassword());
            editor.putString("AccType", "" + iteme.getType());
            editor.putString("AccStatus", "" + iteme.getStatus());
            editor.putString("AccNo", "1");
            editor.apply();

            CreateUserDialog createUserDialog = new CreateUserDialog();
            createUserDialog.show(getActivity().getSupportFragmentManager(), "Account");

            PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_ACOUNT, null, CODE_GET_REQUEST);
            request.execute();
        }

    }

    //ListGetter

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


        CustomListCreateUser customListView=new CustomListCreateUser(this.getActivity(),R.layout.admincustomcreateuser,Items);

        listView.setAdapter(customListView);

        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }


}

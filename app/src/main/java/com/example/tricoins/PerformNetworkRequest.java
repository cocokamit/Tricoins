package com.example.tricoins;


import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tricoins.admin.AdminMainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;

public class PerformNetworkRequest extends AsyncTask<Void, Void, String> {


    public boolean connection;
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    //the url where we need to send the request
    String url;

    //the parameters
    HashMap<String, String> params;

    //the request code to define whether it is a GET or POST
    int requestCode;
    ProgressBar progressBar;
    Context context;

    //constructor to initialize values
    PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode,ProgressBar progressBar,Context context) {
        this.url = url;
        this.params = params;
        this.requestCode = requestCode;
        this.progressBar=progressBar;
        this.context=context;
    }

    //when the task started displaying a progressbar
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }


    //this method will give the response from the request
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressBar.setVisibility(GONE);


        try {
            JSONObject object = new JSONObject(s);
            if (!object.getBoolean("error")) {

                if(object.getString("message").equals("Login successfully"))
                {
                    JSONArray jsonArray=object.getJSONArray("user");

                    if(jsonArray.getJSONObject(0).getString("Status").equals("0")){

                        SharedPreferences sharedPreferences=context.getSharedPreferences("MyRef",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("Id",jsonArray.getJSONObject(0).getString("Id"));
                        editor.putString("Fullname",jsonArray.getJSONObject(0).getString("Fullname"));

                        editor.apply();

                        if(jsonArray.getJSONObject(0).getString("Type").equals("0"))
                        {
                             context.startActivity(new Intent(context,MainActivity.class));
                        }
                        else
                        {
                            context.startActivity(new Intent(context, AdminMainActivity.class));
                        }
                        Toast.makeText(context.getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(context.getApplicationContext(), "Account is currently suspended", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(context.getApplicationContext(), "Account does not exist", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(context.getApplicationContext(), "Wrong username or password.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //the network operation will be performed in background
    @Override
    protected String doInBackground(Void... voids) {
        RequestHandler requestHandler = new RequestHandler();

        if(isNetworkAvailable(this.context))
        {
            Log.d("NetworkAvailable","TRUE");
            if(connectGoogle())
            {
                if (requestCode == CODE_POST_REQUEST)
                    return requestHandler.sendPostRequest(url, params);


                if (requestCode == CODE_GET_REQUEST)
                    return requestHandler.sendGetRequest(url);

                Log.d("GooglePing","TRUE");
                connection=true;
            }
            else
            {
                Toast.makeText(context.getApplicationContext(), "Network error.", Toast.LENGTH_SHORT).show();
                Log.d("GooglePing","FALSE");
                connection=false;
            }
        }
        else {
            Toast.makeText(context.getApplicationContext(), "Network error.", Toast.LENGTH_SHORT).show();
            connection=false;
        }

        return null;
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

}
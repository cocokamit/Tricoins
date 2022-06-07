package com.example.tricoins.networkchecker;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class isNetworkConnected {


    public boolean connection;
    Context context;
    public isNetworkConnected(Context context){
        this.context=context;
    }

   public boolean Connected() {
       if (isNetworkAvailable(context)) {
           Log.d("NetworkAvailable", "TRUE");
          /* if (connectGoogle()) {*/
               return connection = true;
       /*    } else {
               Toast.makeText(context, "Network error. Internet connection is required to access.", Toast.LENGTH_SHORT).show();
               Log.d("GooglePing", "FALSE");
               return connection = false;
           }*/
       } else {
           Toast.makeText(context, "Network error. Internet connection is required to access.", Toast.LENGTH_SHORT).show();
          return connection = false;
       }
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


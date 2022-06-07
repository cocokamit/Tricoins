package com.example.tricoins;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.Toast;

import com.example.tricoins.networkchecker.isNetworkConnected;
import com.example.tricoins.sqlitehelpers.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.view.View.GONE;

public class LoginActivity extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    private static String itemId="";
    ArrayList<DashItems> Items=null;
    TabLayout tabLayout;
    ViewPager viewPager;
    FloatingActionButton fb,google;
    float v=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tabLayout=findViewById(R.id.tab_layout);
        viewPager=findViewById(R.id.view_pager);
        fb=findViewById(R.id.fab_fb);
        google=findViewById(R.id.fab_google);

        tabLayout.addTab(tabLayout.newTab().setText("Login"));
        //tabLayout.addTab(tabLayout.newTab().setText("SignUp"));

        final LoginAdapter adapter=new LoginAdapter(getSupportFragmentManager(),this,tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        fb.setTranslationY(300);
        google.setTranslationY(300);
        tabLayout.setTranslationY(300);

        fb.setAlpha(v);
        google.setAlpha(v);
        tabLayout.setAlpha(v);

        fb.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();
        google.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(600).start();
        tabLayout.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(100).start();

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNetworkConnected isnetwork=new isNetworkConnected(LoginActivity.this);
                if(isnetwork.Connected()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setCancelable(true);
                    builder.setTitle("Confirmation Message");
                    builder.setMessage("Are you sure you want to download data?");
                    builder.setPositiveButton("Confirm",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    DatabaseHelper mydb = new DatabaseHelper(LoginActivity.this);

                                    if (mydb.ImportData().equals("0")) {
                                        Cursor res = mydb.getAllData();

                                        if (res.getCount() > 0) {
                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                                            builder1.setCancelable(true);
                                            builder1.setTitle("List inserted");
                                            builder1.setMessage("Data has been updated");
                                            builder1.setPositiveButton("OK",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    });
                                            builder1.show();
                                        }
                                    } else {
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                                        builder1.setCancelable(true);
                                        builder1.setTitle("Failed to Update");
                                        builder1.setMessage("!!!");
                                        builder1.show();
                                    }

                                }
                            });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNetworkConnected isnetwork=new isNetworkConnected(LoginActivity.this);

                if(isnetwork.Connected()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setCancelable(true);
                    builder.setTitle("Confirmation Message");
                    builder.setMessage("Are you sure you want to upload your data?");
                    builder.setPositiveButton("Confirm",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    DatabaseHelper mydb = new DatabaseHelper(LoginActivity.this);

                                    Cursor res = mydb.ExportData();
                                    if (res.getCount() > 0) {
                                        while (res.moveToNext()) {
                                            HashMap<String, String> params = new HashMap<>();
                                            params.put("AgentId", res.getString(1));
                                            params.put("Agent", res.getString(2));
                                            params.put("Digits", res.getString(3));
                                            params.put("Amount", res.getString(4));
                                            params.put("qrcode", res.getString(5));
                                            params.put("Description", "N/A");
                                            params.put("Type", res.getString(7));
                                            params.put("TimeCap", res.getString(8));
                                            params.put("Sysddate", res.getString(9));
                                            itemId=res.getString(0);
                                            mydb.Statuschanger(itemId);
                                            PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_ITEM, params, CODE_POST_REQUEST);
                                            request.execute();
                                        }

                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                                        builder1.setCancelable(true);
                                        builder1.setTitle("List inserted");
                                        builder1.setMessage("Data has been uploaded");
                                        builder1.setPositiveButton("OK",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                });
                                        builder1.show();
                                    } else {
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                                        builder1.setCancelable(true);
                                        builder1.setTitle("There is nothing to update");
                                        builder1.setMessage("!!!");
                                        builder1.show();
                                    }

                                }
                            });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

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

                }
                else
                {

                    Snackbar.make( findViewById(android.R.id.content), "error", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
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



}
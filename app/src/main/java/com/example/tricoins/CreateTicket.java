package com.example.tricoins;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tricoins.sqlitehelpers.DatabaseHelper;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class CreateTicket extends AppCompatActivity {

    DatabaseHelper mydb;
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    Spinner spinnerTimeCap,spinnerMatchType;
    EditText digits,amount,description;
    Button btn_go;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ticket);

        digits=findViewById(R.id.user_digit);
        amount=findViewById(R.id.user_amount);
       /* description=findViewById(R.id.user_desc);*/
        spinnerTimeCap=findViewById(R.id.spinnerTimeCap);
        spinnerMatchType=findViewById(R.id.spinnerMatch);
        btn_go=findViewById(R.id.btn_go);
        Date currentTime = Calendar.getInstance().getTime();

        int hh=currentTime.getHours();

        if(hh<21)
        {
            spinnerTimeCap.setSelection(2);

            if(hh<17)
            {
                spinnerTimeCap.setSelection(1);

                if(hh<14)
                {
                    spinnerTimeCap.setSelection(0);
                }
            }
        }
        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGo(v);
            }
        });
        spinnerMatchType.setSelection(0);

        mydb=new DatabaseHelper(this);
    }

    //on press back button
    public void onBackEmail(View view)
    {
        Intent i=new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        ((Activity)this).overridePendingTransition(R.anim.slide_left, R.anim.slide_down);
        finish();
    }

    public void onGo(View view)
    {
        try {
            SharedPreferences sharedPreferences = this.getSharedPreferences("MyRef", Context.MODE_PRIVATE);
            String digitss = digits.getText().toString();
            String amounts = amount.getText().toString().trim();
            //String descriptions = description.getText().toString().trim();
            String agentid = sharedPreferences.getString("Id", "0");
            String agentname = sharedPreferences.getString("Fullname", "0");
            String timecap = spinnerTimeCap.getSelectedItem().toString();
            String matchtype=spinnerMatchType.getSelectedItem().toString();

            /* if(descriptions.isEmpty())
            {
                descriptions="N/A";
            }*/

            if(digitss.length()==3) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setTitle("Confirmation Message");
                builder.setMessage("Are you sure you want to save this entry?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                        Date currentTime = Calendar.getInstance().getTime();
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        HashMap<String, String> params = new HashMap<>();
                                        params.put("AgentId", agentid);
                                        params.put("Agent", agentname);
                                        params.put("Digits", digitss);
                                        params.put("Amount", amounts);
                                        try {
                                            params.put("qrcode", "" + AESCrypt.encrypt(getRandomString(10)));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        params.put("Description", "N/A");
                                        params.put("Type", matchtype);
                                        params.put("TimeCap", timecap);
                                        params.put("Sysddate", dateFormat.format(currentTime));

                                        int hh = currentTime.getHours();
                                        int mm = currentTime.getMinutes();
                                        boolean flag = true;

                                       /* if (timecap.equals("2 P.M.") && hh < 14) {
                                            if (hh == 13 && mm > 45) {
                                                flag = false;
                                            } else {
                                                flag = true;
                                            }
                                        }
                                        if (timecap.equals("5 P.M.") && hh < 17) {
                                            if (hh == 16 && mm > 45) {
                                                flag = false;
                                            } else {
                                                flag = true;
                                            }
                                        }

                                        if (timecap.equals("9 P.M.") && hh < 21) {
                                            if (hh == 20 && mm > 45) {
                                                flag = false;
                                            } else {
                                                flag = true;
                                            }
                                        }*/

                                        if (flag) {
                                           /* PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_ITEM, params, CODE_POST_REQUEST);
                                            request.execute();*/

                                            try {

                                               /*mydb.deleteAllData();*/
                                                boolean isInserted = mydb.insertData(agentid,agentname,digitss,amounts,AESCrypt.encrypt(getRandomString(10)),"N/A",matchtype,timecap);
                                                if(isInserted)
                                                {
                                                    PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_ITEM, params, CODE_POST_REQUEST);
                                                    request.execute();

                                                    SharedPreferences sharedPreferences=getSharedPreferences("MyRef", Context.MODE_PRIVATE);

                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    if(matchtype.equals("Straight")) {
                                                        editor.putString("1tabintent", "0");
                                                    }
                                                    else
                                                    {
                                                        editor.putString("1tabintent", "1");
                                                    }
                                                    editor.apply();

                                                    Intent i=new Intent(getApplicationContext(), MainActivity.class);
                                                    startActivity(i);
                                                    overridePendingTransition(R.anim.slide_left, R.anim.slide_down);
                                                    finish();
                                                }
                                                else
                                                {
                                                    Snackbar.make(view, "An error has occurred**", Snackbar.LENGTH_LONG)
                                                     .setAction("Action", null).show();
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                Snackbar.make(view, "An error has occurred*", Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                            }

                                        } else {
                                            Snackbar.make(view, "Time Cap is currently unavailable.", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
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
            else
            {
                digits.setError("Please enter three digits");
                digits.requestFocus();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";
    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
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
package com.example.tricoins.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import androidx.fragment.app.DialogFragment;

import com.example.tricoins.Api;
import com.example.tricoins.R;
import com.example.tricoins.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PadaugEditDialog extends DialogFragment {
    private EditText fullname,username,password;
    Spinner spinner;
    ToggleButton toggleButton;
    static String toggles="0";
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater= getActivity().getLayoutInflater();
        View view =inflater.inflate(R.layout.dialog_edit_winnumber,null);

        fullname=view.findViewById(R.id.acc_fullname);
        username=view.findViewById(R.id.acc_username);
        password=view.findViewById(R.id.acc_password);
        toggleButton=view.findViewById(R.id.toggleButton);
        spinner=view.findViewById(R.id.acctype);

        toggleButton.setBackgroundColor(Color.GRAY);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    toggles="0";
                    toggleButton.setBackgroundColor(Color.GREEN);
                }
                else
                {
                    toggles="1";
                    toggleButton.setBackgroundColor(Color.GRAY);
                }
            }
        });

        builder.setView(view)
                .setTitle("Account")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ongo();
                    }
                });


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyRef", Context.MODE_PRIVATE);
        String acNo = sharedPreferences.getString("AccNo", "0");
        if(acNo.equals("1")) {
            fullname.setText(sharedPreferences.getString("AccName", "0"));
            username.setText(sharedPreferences.getString("AccUsername", "0"));
            password.setText(sharedPreferences.getString("AccPassword", "0"));
            username.setEnabled(false);
            if(sharedPreferences.getString("AccStatus", "0").equals("0")){
                toggleButton.setChecked(true);
            }

            if(sharedPreferences.getString("AccType", "0").equals("1"))
            {
                spinner.setSelection(1);
            }
        }
        return builder.create();
    }



    public void ongo()
    {
        String accname=fullname.getText().toString();
        String accusername=username.getText().toString();
        String accpassword=password.getText().toString();
        String accstatus=toggles;
        String type=spinner.getSelectedItem().toString();
        String acctype="0";
        if(type.equals("Admin"))
        {
            acctype="1";
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("Fullname",accname);
        params.put("Username",accusername);
        params.put("Password",accpassword);
        params.put("Type",acctype);
        params.put("Status",accstatus);

        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_ACCOUNT, params, CODE_POST_REQUEST);
        request.execute();
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
    }}
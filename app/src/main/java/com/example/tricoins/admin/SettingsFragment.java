package com.example.tricoins.admin;

import android.content.DialogInterface;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tricoins.Api;
import com.example.tricoins.R;
import com.example.tricoins.sqlitehelpers.DatabaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SettingsFragment extends Fragment implements View.OnClickListener{

    private static final int CODE_POST_REQUEST = 1025;
    private static String itemId="";

    DatabaseHelper mydb;
    EditText txtLimiter;
    Button bnt_go;
    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View listViewItem = inflater.inflate(R.layout.fragment_settings,container,false);
        txtLimiter=listViewItem.findViewById(R.id.txtLimiter);
        bnt_go=listViewItem.findViewById(R.id.bnt_go);

        mydb=new DatabaseHelper(getActivity());
        bnt_go.setOnClickListener(this);
        refreshItemList();
        return listViewItem;
    }

    @Override
    public void onClick(View v) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(true);
            builder.setTitle("Confirmation Message");
            builder.setMessage("Are you sure you want to change the amount limit?");
            builder.setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (txtLimiter.length() > 0) {
                                String sd = txtLimiter.getText().toString();
                                boolean isInserted = mydb.insertlimiter(sd);
                                if (isInserted) {
                                    HashMap<String, String> params = new HashMap<>();
                                    params.put("Limiter", txtLimiter.getText().toString());

                                    WinningNumNetworkRequest request = new WinningNumNetworkRequest(Api.URL_UPDATE_LIMITER, params, CODE_POST_REQUEST, getActivity());
                                    request.execute();

                                    refreshItemList();
                                }
                            } else {
                                txtLimiter.setError("Please enter the limit amount.");
                                txtLimiter.requestFocus();
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

    //ListRefresher
    private void refreshItemList(){
         DatabaseHelper mydb=new DatabaseHelper(getActivity());

        Cursor res=mydb.getsettinglimiter();

        if(res.getCount()>0) {
            while (res.moveToNext()) {
                txtLimiter.setText(res.getString(0));
            }
        }
    }
}
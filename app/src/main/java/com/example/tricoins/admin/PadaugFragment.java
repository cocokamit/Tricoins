package com.example.tricoins.admin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tricoins.AESCrypt;
import com.example.tricoins.AllItems;
import com.example.tricoins.Api;
import com.example.tricoins.CustomListView;
import com.example.tricoins.DashItems;
import com.example.tricoins.R;
import com.example.tricoins.networkchecker.isNetworkConnected;
import com.example.tricoins.sqlitehelpers.DatabaseHelper;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class PadaugFragment extends Fragment implements View.OnClickListener {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    private static String itemId="";

    DatabaseHelper mydb;
    ArrayList<WinNumber> Items=null;
    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    EditText txtdigit;
    Button bnt_go;
    TextView txtTimeCap,txtwindigit;
    int counter=0;
    Spinner spinnerTimeCap;
    public PadaugFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View listViewItem = inflater.inflate(R.layout.fragment_padaug,container,false);
        listView=listViewItem.findViewById(R.id.l3view);
        txtdigit=listViewItem.findViewById(R.id.txtdigit);
        bnt_go=listViewItem.findViewById(R.id.bnt_go);
        txtTimeCap=listViewItem.findViewById(R.id.txtTimeCap);
        txtwindigit=listViewItem.findViewById(R.id.txtwindigit);
        Date currentTime = Calendar.getInstance().getTime();
        spinnerTimeCap=listViewItem.findViewById(R.id.spinnerMatch);
        int hh=currentTime.getHours();

        if(hh<20)
        {
            txtTimeCap.setText("9 P.M.");

            if(hh<17)
            {
                txtTimeCap.setText("5 P.M.");

                if(hh<14)
                {
                    txtTimeCap.setText("2 P.M.");
                }
            }
        }
        swipeRefreshLayout=listViewItem.findViewById(R.id.swiperefresh1);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
               refreshItemList();
            }
        });

        mydb=new DatabaseHelper(getActivity());
        bnt_go.setOnClickListener(this);

        /*PerformNetworkRequest request =new PerformNetworkRequest(Api.URL_READ_ACOUNT, null, CODE_GET_REQUEST);
        request.execute();
*/
        refreshItemList();
        listView.setOnItemClickListener(this::onItemClick);


        txtwindigit.setOnClickListener(this::onWinNumberClick);



        return listViewItem;

    }

    public void onWinNumberClick( View v) {

        openDialog();
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

        }

    }

        @Override
    public void onClick(View v) {

        if(counter==0 && txtTimeCap.getText().toString().equals(spinnerTimeCap.getSelectedItem().toString())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(true);
            builder.setTitle("Confirmation Message");
            builder.setMessage("Are you sure you want to 'DECLARE' this entry?");
            builder.setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (txtdigit.length() == 3) {
                                String sd = txtdigit.getText().toString();
                                String ss = txtTimeCap.getText().toString();
                                boolean isInserted = mydb.insertwinnumber(sd, ss);
                                if (isInserted) {
                                    HashMap<String, String> params = new HashMap<>();
                                    params.put("Digits", txtdigit.getText().toString());
                                    params.put("TimeCap", txtTimeCap.getText().toString());

                                    WinningNumNetworkRequest request = new WinningNumNetworkRequest(Api.URL_CREATE_WINNINGNUMBER, params, CODE_POST_REQUEST, getActivity());
                                    request.execute();

                                    refreshItemList();
                                }
                            } else {
                                txtdigit.setError("Please enter three digits");
                                txtdigit.requestFocus();
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
            /*txtdigit.setError("Winning Digit has already been declared!");
            txtdigit.requestFocus();*/
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(true);
            builder.setTitle("Confirmation Message");
            builder.setMessage("Are you sure you want to 'UPDATE' this entry?");
            builder.setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (txtdigit.length() == 3) {
                                String sd = txtdigit.getText().toString();
                                String ss = spinnerTimeCap.getSelectedItem().toString();
                                if(mydb.gettimecapwinnumbereexist(ss).getCount()>0) {
                                    String timecap = spinnerTimeCap.getSelectedItem().toString();
                                    mydb.updatewinnumber(sd, ss);

                                    HashMap<String, String> params = new HashMap<>();
                                    params.put("Digits", txtdigit.getText().toString());
                                    params.put("TimeCap", timecap);

                                    WinningNumNetworkRequest request = new WinningNumNetworkRequest(Api.URL_UPDATE_WIN_NUMBERS, params, CODE_POST_REQUEST, getActivity());
                                    request.execute();

                                }
                                else
                                {
                                    boolean isInserted = mydb.insertwinnumber(sd, ss);
                                    if (isInserted) {
                                        HashMap<String, String> params = new HashMap<>();
                                        params.put("Digits", txtdigit.getText().toString());
                                        params.put("TimeCap",spinnerTimeCap.getSelectedItem().toString());

                                        WinningNumNetworkRequest request = new WinningNumNetworkRequest(Api.URL_CREATE_WINNINGNUMBER, params, CODE_POST_REQUEST, getActivity());
                                        request.execute();
                                    }
                                }
                                refreshItemList();
                            } else {
                                txtdigit.setError("Please enter three digits");
                                txtdigit.requestFocus();
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



    public void onItemClick(AdapterView<?> l, View v, int position, long id) {

        final WinNumber iteme = (WinNumber) l.getAdapter().getItem(position);

        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("MyRef", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("1Digits",""+iteme.getDigits());
        editor.putString("1TimeCap",iteme.getTimeCap());
        editor.putString("1Sysddate",iteme.getSysddate());
        editor.putString("1intent","3");
        editor.apply();

        Intent i=new Intent(getActivity(), AllWinningItems.class);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.slide_right, R.anim.slide_up);
    }


    //ListRefresher
    private void refreshItemList(){

        Items=new ArrayList<WinNumber>();
        DatabaseHelper mydb=new DatabaseHelper(getActivity());

        Cursor res=mydb.getWinningNumbers();
        String now="";
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        SimpleDateFormat sidate=new SimpleDateFormat("yyyy-MM-dd");
        counter=0;
        if(res.getCount()>0) {

            while(res.moveToNext())
            {
                Cursor aa=mydb.getWinningAmountList(res.getString(1),res.getString(2),res.getString(3));
                double amount=0;

                if(aa.getCount()>0)
                {
                   while(aa.moveToNext())
                   {
                       if(aa.getString(1).equals("Straight")){
                         amount+=((Double.parseDouble(aa.getString(0))/10)*4500);}
                       else if(aa.getString(1).equals("Rambolito"))
                       {
                           if(uniqueCharacters(res.getString(1)).equals("6"))
                           {
                               amount+=((Double.parseDouble(aa.getString(0))/10)*750);
                           }
                           else
                           {
                               amount+=((Double.parseDouble(aa.getString(0))/10)*1500);
                           }
                       }
                   }
                }


                Items.add(new WinNumber(
                        Integer.parseInt(res.getString(0)),
                        Integer.parseInt(res.getString(1)),
                        res.getString(2),
                        res.getString(3),
                        res.getString(4),
                        Double.toString(amount)
                ));

                try {
                Date Dsa=sidate.parse(res.getString(3));
                String lastnani=new SimpleDateFormat("yyyy-MM-dd").format(Dsa);

                    if(lastnani.equals(currentDate) && res.getString(2).equals(txtTimeCap.getText().toString()))
                    {
                        txtwindigit.setText(res.getString(1));
                        if(res.getString(2).equals("2 P.M.")) {
                            counter = 1;
                        }
                        else if(res.getString(2).equals("5 P.M."))
                        {
                            counter = 2;
                        }
                        else if(res.getString(2).equals("9 P.M."))
                        {
                            counter = 3;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }


            CustomListWinNumber customListView=new CustomListWinNumber(getActivity(),R.layout.admincustomwinnumber,Items);
            listView.setAdapter(customListView);
        }
        else
        {
        }


        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    String uniqueCharacters(String str)
    {
        int count=0;
        for (int i = 0; i < str.length(); i++)
            for (int j = i + 1; j < str.length(); j++)
                if (str.charAt(i) == str.charAt(j))
                {
                    count+=1;
                }

        if(count==0)
        {
            return "6";
        }
        else
        {
            return "3";
        }
    }
}
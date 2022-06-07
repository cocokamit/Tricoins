package com.example.tricoins;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tricoins.admin.AdminMainActivity;
import com.example.tricoins.admin.AllWinningItems;
import com.example.tricoins.admin.CustomListWinNumber;
import com.example.tricoins.admin.WinNumber;
import com.example.tricoins.sqlitehelpers.DatabaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AllWinItems extends AppCompatActivity {
    DatabaseHelper mydb;
    ArrayList<WinNumber> Items=null;
    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView txtTimeCap,txtwindigit;
    int counter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_win_items);
        listView=findViewById(R.id.l3view);
        txtTimeCap=findViewById(R.id.txtTimeCap);
        txtwindigit=findViewById(R.id.txtwindigit);

        Date currentTime = Calendar.getInstance().getTime();

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
        swipeRefreshLayout=findViewById(R.id.swiperefresh1);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                refreshItemList();
            }
        });

        mydb=new DatabaseHelper(this);
        refreshItemList();
        listView.setOnItemClickListener(this::onItemClick);

    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {

        final WinNumber iteme = (WinNumber) l.getAdapter().getItem(position);

        SharedPreferences sharedPreferences=getSharedPreferences("MyRef", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("1Digits",""+iteme.getDigits());
        editor.putString("1TimeCap",iteme.getTimeCap());
        editor.putString("1Sysddate",iteme.getSysddate());
        editor.putString("1intent","4");
        editor.apply();

        Intent i=new Intent(this, AllWinningItems.class);
        startActivity(i);
        this.overridePendingTransition(R.anim.slide_right, R.anim.slide_up);
    }


    public void onBackEmail(View view)
    {
        Intent i=new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        ((Activity)this).overridePendingTransition(R.anim.slide_left, R.anim.slide_down);
        finish();
    }


    //ListRefresher
    private void refreshItemList(){
        Items=new ArrayList<WinNumber>();
        DatabaseHelper mydb=new DatabaseHelper(this);

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
                        counter=1;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }


            CustomListWinNumber customListView=new CustomListWinNumber(this,R.layout.admincustomwinnumber,Items);
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
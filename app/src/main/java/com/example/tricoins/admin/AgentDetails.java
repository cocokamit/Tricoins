package com.example.tricoins.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tricoins.Accounts;
import com.example.tricoins.Api;
import com.example.tricoins.CustomListViewPerItem;
import com.example.tricoins.MainActivity;
import com.example.tricoins.PerformNetworkRequest;
import com.example.tricoins.R;
import com.example.tricoins.RequestHandler;
import com.example.tricoins.sqlitehelpers.DatabaseHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class AgentDetails extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    BarChart barChart;
    LineChart barChart2,barChart3;
    ArrayList<AgentStatDetails> Items=null;
    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView agentname,recentdate,dailydate;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_details);

        barChart=findViewById(R.id.bar_chart);
        barChart2=findViewById(R.id.bar_chart2);
        barChart3=findViewById(R.id.bar_chart3);
        sharedPreferences = this.getSharedPreferences("MyRef", Context.MODE_PRIVATE);
        String Id = sharedPreferences.getString("AgentId", "0");

        agentname=findViewById(R.id.agent_name);
        recentdate=findViewById(R.id.recent_date);
        dailydate=findViewById(R.id.daily_date);
        agentname.setText(sharedPreferences.getString("AgentName", "0"));

        listView=findViewById(R.id.l3view);

        HashMap<String, String> params = new HashMap<>();
        params.put("Id", Id);

        swipeRefreshLayout=findViewById(R.id.swiperefresh1);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                AgentDetails.PerformNetworkRequest request =new AgentDetails.PerformNetworkRequest(Api.URL_LIST_AGENT_STAT_DETAILS, params, CODE_POST_REQUEST);
                request.execute();
            }
        });

        PerformNetworkRequest request =new PerformNetworkRequest(Api.URL_LIST_AGENT_STAT_DETAILS, params, CODE_POST_REQUEST);
        request.execute();

        refreshItemList(Id);
        refreshItemList1(Id);
        //refreshItemList3(Id);
    }

    //on press back button
    public void onBackEmail(View view)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("1intent","2");
        editor.apply();

        Intent i=new Intent(getApplicationContext(), AdminMainActivity.class);
        startActivity(i);
        ((Activity)this).overridePendingTransition(R.anim.slide_left, R.anim.slide_down);
        finish();
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
                    //refreshItemList(object.getJSONArray("items"));
                    //refreshItemList1(object.getJSONArray("itemsinmonth"));
                    refreshItemList3(object.getJSONArray("itemrecent"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
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

    private void refreshItemList(String Id){

        Items=new ArrayList<AgentStatDetails>();
        DatabaseHelper mydb=new DatabaseHelper(AgentDetails.this);

        Cursor res=mydb.getAgentStatsDetails(Id);
        if(res.getCount()>0) {

            Date currentTime = Calendar.getInstance().getTime();
            ArrayList<BarEntry> daily=new ArrayList<>();
            while(res.moveToNext())
            {
                Items.add(new AgentStatDetails(
                        res.getString(0),
                        res.getString(1),
                        res.getString(2)
                ));

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                if(res.getString(2)!=null) {
                    try {
                        String d = (String) DateFormat.format("d", sdf.parse(res.getString(2)));
                        int a = Integer.parseInt(d);
                        int b = (int) Double.parseDouble(res.getString(0));
                        int dd = currentTime.getMonth() + 1;
                        int ss = Integer.parseInt((String) DateFormat.format("M", sdf.parse(res.getString(2))));
                        dailydate.setText((String) DateFormat.format("MMMM", sdf.parse(res.getString(2))));
                        if (dd == ss) {
                            daily.add(new BarEntry(a, b));
                        }
                        recentdate.setText((String) DateFormat.format("MM/dd/yyyy HH:mm", sdf.parse(res.getString(2))));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            if(!daily.isEmpty()) {
                YearMonth yearMonthObject = YearMonth.of(currentTime.getYear(), currentTime.getMonth());
                int daysInMonth = yearMonthObject.lengthOfMonth()+1;
                String[] arraydays = new String[daysInMonth];

                for (int i = 0; i < daysInMonth; i++) {
                    arraydays[i] = Integer.toString(i);
                }
                BarDataSet barDataSet = new BarDataSet(daily, "Daily Entries");
                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                barDataSet.setValueTextColor(Color.BLACK);
                BarData barData = new BarData(barDataSet);

                /*barChart.setFitBars(true);*/
                barChart.setData(barData);
                barChart.getDescription().setText("Daily Entriess");
                barChart.animateY(2000);
                barChart.setDoubleTapToZoomEnabled(false);
                barChart.setScaleYEnabled(false);
                XAxis xAxis = barChart.getXAxis();
                xAxis.setValueFormatter(new MyXAxisValueFormatter(arraydays));

                barChart.getViewPortHandler().setMaximumScaleX(3f);

                Collections.reverse(Items);
                CustomListAgentPerItem customListView=new CustomListAgentPerItem(this,R.layout.customlvperitems,Items);

                listView.setAdapter(customListView);
            }

        }

     /*   if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }*/
    }


    private void refreshItemList1(String Id){

        Items=new ArrayList<AgentStatDetails>();
        DatabaseHelper mydb=new DatabaseHelper(this);

        Cursor res=mydb.getAgentStatsDetailsInMonths(Id);
        ArrayList<Entry> monthly=new ArrayList<>();
        ArrayList<Entry> entries=new ArrayList<>();
        if(res.getCount()>0) {

            while(res.moveToNext())
            {
                Items.add(new AgentStatDetails(
                        res.getString(0),
                        res.getString(1),
                        res.getString(2)
                ));

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                if(res.getString(2)!=null) {

                    try {
                        String d = (String) DateFormat.format("M", sdf.parse(res.getString(2)));
                        int a = Integer.parseInt(d) - 1;
                        int b = (int) Double.parseDouble(res.getString(1));
                        int c = (int) Double.parseDouble(res.getString(0));
                        monthly.add(new Entry(a, b));
                        entries.add(new Entry(a, c));

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(!monthly.isEmpty()) {
                String[] monthsinyear=new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};

                LineDataSet dataSets1=new LineDataSet(monthly,"Entries");
                dataSets1.setColor(Color.BLUE);
                dataSets1.setValueTextColor(Color.BLACK);

                LineDataSet dataSets2=new LineDataSet(entries,"Amount");
                dataSets2.setColor(Color.RED);
                dataSets2.setValueTextColor(Color.BLACK);

                LineData barData1 = new LineData(dataSets2);

                barChart2.setData(barData1);
                barChart2.invalidate();
                //barChart2.animateY(2000);
                barChart2.setDoubleTapToZoomEnabled(false);
                barChart2.setScaleYEnabled(false);
                XAxis xAxis1=barChart2.getXAxis();
                xAxis1.setValueFormatter(new MyXAxisValueFormatter(monthsinyear));
                xAxis1.setGranularity(1);


                LineData barData = new LineData(dataSets1);

                barChart3.setData(barData);
                barChart3.invalidate();
                barChart3.setDoubleTapToZoomEnabled(false);
                barChart3.setScaleYEnabled(false);
                XAxis xAxis=barChart3.getXAxis();
                xAxis.setValueFormatter(new MyXAxisValueFormatter(monthsinyear));
                xAxis.setGranularity(1);
            }

        }

    }


    /*private void refreshItemList3(String Id){

            Items=new ArrayList<AgentStatDetails>();
            DatabaseHelper mydb=new DatabaseHelper(this);

            Cursor res=mydb.getAgentStatsDetailsRecent(Id);
            if(res.getCount()>0) {

                while(res.moveToNext())
                {
                    Items.add(new AgentStatDetails(
                            res.getString(0),
                            res.getString(1),
                            res.getString(2)
                    ));

                    if(res.getString(2)!=null) {

                        Collections.reverse(Items);
                        CustomListAgentPerItem customListView = new CustomListAgentPerItem(this, R.layout.customlvperitems, Items);

                        listView.setAdapter(customListView);
                    }
                }

            }


        }
    */

    private void refreshItemList3(JSONArray heroes) throws JSONException {

        Items=new ArrayList<AgentStatDetails>();

        for (int i = 0; i < heroes.length(); i++) {
            JSONObject obj = heroes.getJSONObject(i);

            Items.add(new AgentStatDetails(
                    obj.getString("Amount"),
                    obj.getString("Entries"),
                    obj.getString("Sysddate")
            ));
        }

            Collections.reverse(Items);
            CustomListAgentPerItem customListView=new CustomListAgentPerItem(this,R.layout.customlvperitems,Items);

            listView.setAdapter(customListView);

    }
    public class MyXAxisValueFormatter implements IAxisValueFormatter{

        private String[] mValues;
        public MyXAxisValueFormatter(String[] values)
        {
            this.mValues=values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {

            return mValues[(int)value];
        }
    }
}
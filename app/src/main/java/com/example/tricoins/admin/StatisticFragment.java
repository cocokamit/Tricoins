package com.example.tricoins.admin;

import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tricoins.Accounts;
import com.example.tricoins.Api;
import com.example.tricoins.R;
import com.example.tricoins.RequestHandler;
import com.example.tricoins.sqlitehelpers.DatabaseHelper;
import com.github.mikephil.charting.charts.CombinedChart;
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
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class StatisticFragment extends Fragment {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    ArrayList<AgentStatDetails> Items1=null;
    ArrayList<Accounts> Items=null;
    SwipeRefreshLayout swipeRefreshLayout;
    LineChart lineChart;
    HorizontalBarChart horizontalBarChart;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View listViewItem = inflater.inflate(R.layout.fragment_statistic,container,false);

        lineChart=listViewItem.findViewById(R.id.line_chart);
        horizontalBarChart=listViewItem.findViewById(R.id.bar_chart);
      /*  swipeRefreshLayout=listViewItem.findViewById(R.id.swiperefresh1);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                PerformNetworkRequest request =new PerformNetworkRequest(Api.URL_LIST_ALL_AGENT_STAT_DETAILS, null, CODE_GET_REQUEST);
                request.execute();
            }
        });*/

     /*   PerformNetworkRequest request =new PerformNetworkRequest(Api.URL_LIST_ALL_AGENT_STAT_DETAILS, null, CODE_GET_REQUEST);
        request.execute();*/

        refreshItemList();
        refreshItemList1();
        return listViewItem;
    }
/*

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
                    refreshItemList(object.getJSONArray("items"));
                    refreshItemList1(object.getJSONArray("itemsinmonth"));
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
*/

     //ListRefresher
    private void refreshItemList() {

        Items=new ArrayList<Accounts>();
        DatabaseHelper mydb=new DatabaseHelper(getActivity());

        Cursor res=mydb.getAllAgentStatsDetails();
        if(res.getCount()>0) {

            String[] agents=new String[res.getCount()];
            ArrayList<BarEntry> entries=new ArrayList<>();
            int count=0;
            while(res.moveToNext())
            {
                Items.add(new Accounts(
                        Integer.parseInt(res.getString(1)),
                        res.getString(2),
                        res.getString(3),
                        res.getString(4),
                        res.getString(5),
                        res.getString(6),
                        res.getString(7),
                        res.getString(8)
                ));
                agents[count]=res.getString(2);
                int c=0;
                if(res.getString(8)!=null){
                    c=(int)Double.parseDouble(res.getString(8));
                }
                entries.add(new BarEntry(count,c));

                ++count;
            }

            if(!entries.isEmpty()) {
                BarDataSet dataSets2=new BarDataSet(entries,"Amount");
                dataSets2.setColors(ColorTemplate.MATERIAL_COLORS);
                dataSets2.setValueTextColor(Color.BLACK);

                BarData barData1 = new BarData(dataSets2);

                horizontalBarChart.setData(barData1);
                horizontalBarChart.invalidate();
                horizontalBarChart.setDoubleTapToZoomEnabled(false);
                horizontalBarChart.setScaleYEnabled(false);
                XAxis xAxis1=horizontalBarChart.getXAxis();
                xAxis1.setValueFormatter(new MyXAxisValueFormatter(agents));
                xAxis1.setGranularity(1);
            }
        }


       /* if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }*/
    }

   /* //ListRefresher
    private void refreshItemList(JSONArray heroes) throws JSONException {

        Items=new ArrayList<Accounts>();
        String[] agents=new String[heroes.length()];
        ArrayList<BarEntry> entries=new ArrayList<>();
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

            agents[i]=obj.getString("Fullname");

            int c=0;
            if(!obj.getString("Collectedamount").equals("null")){
            c=(int)Double.parseDouble(obj.getString("Collectedamount"));
            }
            entries.add(new BarEntry(i,c));
        }
        if(!entries.isEmpty()) {
            BarDataSet dataSets2=new BarDataSet(entries,"Amount");
            dataSets2.setColors(ColorTemplate.MATERIAL_COLORS);
            dataSets2.setValueTextColor(Color.BLACK);

            BarData barData1 = new BarData(dataSets2);

            horizontalBarChart.setData(barData1);
            horizontalBarChart.invalidate();
            horizontalBarChart.setDoubleTapToZoomEnabled(false);
            horizontalBarChart.setScaleYEnabled(false);
            XAxis xAxis1=horizontalBarChart.getXAxis();
            xAxis1.setValueFormatter(new MyXAxisValueFormatter(agents));
            xAxis1.setGranularity(1);
        }

      *//*  if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }*//*
    }*/


    private void refreshItemList1(){

        Items1=new ArrayList<AgentStatDetails>();
        DatabaseHelper mydb=new DatabaseHelper(getActivity());

        Cursor res=mydb.getAllAgentStatsDetailsInMonths();
        ArrayList<Entry> entries=new ArrayList<>();
        if(res.getCount()>0) {

            while(res.moveToNext())
            {
                Items1.add(new AgentStatDetails(
                        res.getString(0),
                        res.getString(1),
                        res.getString(2)
                ));

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                if(res.getString(2)!=null) {
                try {

                    String d=(String) DateFormat.format("M",sdf.parse(res.getString(2)));
                    int a=Integer.parseInt(d)-1;
                    int c=0;
                    if(!res.getString(0).equals("null")){
                        c=(int)Double.parseDouble(res.getString(0));}

                    entries.add(new Entry(a,c));

                } catch (ParseException e) {
                    e.printStackTrace();
                }}

            }

            if(!entries.isEmpty()) {
                String[] monthsinyear=new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};


                LineDataSet dataSets2=new LineDataSet(entries,"Amount");
                dataSets2.setColor(Color.RED);
                dataSets2.setValueTextColor(Color.BLACK);

                LineData barData1 = new LineData(dataSets2);

                lineChart.setData(barData1);
                lineChart.invalidate();
                lineChart.setDoubleTapToZoomEnabled(false);
                lineChart.setScaleYEnabled(false);
                XAxis xAxis1=lineChart.getXAxis();
                xAxis1.setValueFormatter(new MyXAxisValueFormatter(monthsinyear));
                xAxis1.setGranularity(1);
            }
        }

      /*  if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }*/
    }

    /*
    private void refreshItemList1(JSONArray heroes) throws JSONException {

        Items1=new ArrayList<AgentStatDetails>();

        ArrayList<Entry> entries=new ArrayList<>();
        for (int i = 0; i < heroes.length(); i++) {
            JSONObject obj = heroes.getJSONObject(i);

            Items1.add(new AgentStatDetails(
                    obj.getString("Amount"),
                    obj.getString("Entries"),
                    obj.getString("Sysddate")
            ));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            try {
                String d=(String) DateFormat.format("M",sdf.parse(obj.getString("Sysddate")));
                int a=Integer.parseInt(d)-1;
                int c=0;
                if(!obj.getString("AmeDouble(obj.getString("ount").equals("null")){
                    c=(int)Double.parsAmount"));}

                entries.add(new Entry(a,c));

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        if(!entries.isEmpty()) {
            String[] monthsinyear=new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};


            LineDataSet dataSets2=new LineDataSet(entries,"Amount");
            dataSets2.setColor(Color.RED);
            dataSets2.setValueTextColor(Color.BLACK);

            LineData barData1 = new LineData(dataSets2);

            lineChart.setData(barData1);
            lineChart.invalidate();
            lineChart.setDoubleTapToZoomEnabled(false);
            lineChart.setScaleYEnabled(false);
            XAxis xAxis1=lineChart.getXAxis();
            xAxis1.setValueFormatter(new MyXAxisValueFormatter(monthsinyear));
            xAxis1.setGranularity(1);
        }

       */
/* if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }*//*

    }
*/


    public class MyXAxisValueFormatter implements IAxisValueFormatter {

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
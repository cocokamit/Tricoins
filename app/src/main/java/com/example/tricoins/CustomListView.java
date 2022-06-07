package com.example.tricoins;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DatabaseErrorHandler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CustomListView extends ArrayAdapter<DashItems> {
    List<DashItems> itemsList;
    Context context;
    public CustomListView(Context context,int LayoutId, List<DashItems> itemsList) {
        super(context, LayoutId, itemsList);
        this.itemsList = itemsList;
        this.context=context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View listViewItem = inflater.inflate(R.layout.customlv, null, true);


        TextView textViewName = listViewItem.findViewById(R.id.tvprofilename);
        TextView textDate = listViewItem.findViewById(R.id.txtdate);
        TextView textAmount = listViewItem.findViewById(R.id.txtamount);
        TextView textTimecap = listViewItem.findViewById(R.id.timecap);
        TextView textentrycount = listViewItem.findViewById(R.id.entrycount2);
        ConstraintLayout conlayout=listViewItem.findViewById(R.id.backpanel);
        final DashItems iteme = itemsList.get(position);

        textViewName.setText("#. "+iteme.getDigits()+"");
        textDate.setText(""+iteme.getSysddate());
        textAmount.setText("₱"+currencyFormatter(iteme.getAmount()));
        textTimecap.setText(iteme.getTimeCap());
        textentrycount.setText(iteme.getTypecount());

        if(Double.parseDouble(iteme.getAmount())>=iteme.getLimiters())
        {
            conlayout.setBackgroundColor(context.getResources().getColor(R.color.red));
        }


        return listViewItem;
    }
    public String currencyFormatter(String num) {
        double m = Double.parseDouble(num);
        DecimalFormat formatter = new DecimalFormat("###,###,###.00");
        return formatter.format(m);
    }

}
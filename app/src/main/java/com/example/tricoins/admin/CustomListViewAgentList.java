package com.example.tricoins.admin;

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

import androidx.recyclerview.widget.RecyclerView;

import com.example.tricoins.Accounts;
import com.example.tricoins.DashItems;
import com.example.tricoins.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CustomListViewAgentList extends ArrayAdapter<Accounts> {
    List<Accounts> itemsList;
    Context context;
    public CustomListViewAgentList(Context context,int LayoutId, List<Accounts> itemsList) {
        super(context, LayoutId, itemsList);
        this.itemsList = itemsList;
        this.context=context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View listViewItem = inflater.inflate(R.layout.admincustomagentlist, null, true);

        TextView textamountcollected=listViewItem.findViewById(R.id.collected2);
        TextView textentrycollected = listViewItem.findViewById(R.id.collected);
        TextView textDate = listViewItem.findViewById(R.id.txtdate);
        TextView textagentname = listViewItem.findViewById(R.id.agentname);

        final Accounts iteme = itemsList.get(position);

        textentrycollected.setText(iteme.getCollectedentry());
        textDate.setText(iteme.getStatus());
        textagentname.setText(iteme.getFullName());

        try {
            textamountcollected.setText(currencyFormatter(iteme.getCollectedamount()));
        }
        catch(Exception e)
        {
            textamountcollected.setText("0");
        }
        return listViewItem;
    }
    public String currencyFormatter(String num) {
        double m = Double.parseDouble(num);
        DecimalFormat formatter = new DecimalFormat("###,###,###.00");
        return formatter.format(m);
    }

}
package com.example.tricoins.admin;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.tricoins.Accounts;
import com.example.tricoins.DashItems;
import com.example.tricoins.R;

import java.text.DecimalFormat;
import java.util.List;

public class CustomListCreateUser extends ArrayAdapter<Accounts>{
    List<Accounts> itemsList;
    Context context;

    public CustomListCreateUser(Context context,int LayoutId, List<Accounts> itemsList) {
        super(context, LayoutId, itemsList);
        this.itemsList = itemsList;
        this.context=context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View listViewItem = inflater.inflate(R.layout.admincustomcreateuser, null, true);

        TextView accname = listViewItem.findViewById(R.id.agentname);
        TextView accstatus = listViewItem.findViewById(R.id.txtstatus);
        TextView accrole = listViewItem.findViewById(R.id.agentrole);

        final Accounts iteme = itemsList.get(position);
        String s="";
        if(iteme.getStatus().equals("0"))
        {
            s="Active";
            accstatus.setText(s);
            accstatus.setTextColor(Color.GREEN);
        }
        else
        {
            s="Inactive";
            accstatus.setText(s);
            accstatus.setTextColor(Color.GRAY);
        }

        if(iteme.getType().equals("0"))
        {
            s="Agent";
            accrole.setText(s);
        }
        else
        {
            s="Admin";
            accrole.setText(s);
        }

        accname.setText(iteme.getFullName());


        return listViewItem;
    }
    public String currencyFormatter(String num) {
        double m = Double.parseDouble(num);
        DecimalFormat formatter = new DecimalFormat("###,###,###.00");
        return formatter.format(m);
    }

}
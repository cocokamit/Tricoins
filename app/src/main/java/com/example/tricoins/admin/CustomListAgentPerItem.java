package com.example.tricoins.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.tricoins.DashItems;
import com.example.tricoins.R;

import java.text.DecimalFormat;
import java.util.List;

public class CustomListAgentPerItem extends ArrayAdapter<AgentStatDetails>{
        List<AgentStatDetails> itemsList;
        Context context;

    public CustomListAgentPerItem(Context context,int LayoutId, List<AgentStatDetails> itemsList) {
        super(context, LayoutId, itemsList);
        this.itemsList = itemsList;
        this.context=context;
    }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View listViewItem = inflater.inflate(R.layout.customlvperitems, null, true);

        TextView textDate = listViewItem.findViewById(R.id.txtdate);
        TextView textAmount = listViewItem.findViewById(R.id.txtamount);
        TextView txttype=listViewItem.findViewById(R.id.txttype);

        final AgentStatDetails iteme = itemsList.get(position);

        textDate.setText(""+iteme.getSysddate());
        textAmount.setText("₱"+currencyFormatter(iteme.getAmount()));
            txttype.setText(iteme.getEntries());


        return listViewItem;
    }
        public String currencyFormatter(String num) {
        double m = Double.parseDouble(num);
        DecimalFormat formatter = new DecimalFormat("###,###,###.00");
        return formatter.format(m);
    }

    }
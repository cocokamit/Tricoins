package com.example.tricoins;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

public class CustomListViewPerItem extends ArrayAdapter<DashItems> {
    List<DashItems> itemsList;
    Context context;
    public CustomListViewPerItem(Context context,int LayoutId, List<DashItems> itemsList) {
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
        TextView txtdigit=listViewItem.findViewById(R.id.txtdigit);
        TextView txtAgent=listViewItem.findViewById(R.id.txtAgent);
        TextView textwinamount=listViewItem.findViewById(R.id.txtwinamount);

        final DashItems iteme = itemsList.get(position);

        textDate.setText(""+iteme.getSysddate());
        textAmount.setText("₱"+currencyFormatter(iteme.getAmount()));
        txttype.setText(iteme.getMatchType());
        txtdigit.setText(""+iteme.getDigits());
        txtAgent.setText(iteme.getAgent());
        double amount=0;
        if(iteme.getMatchType().equals("Straight")){
            amount+=((Double.parseDouble(iteme.getAmount())/10)*4500);}
        else if(iteme.getMatchType().equals("Rambolito"))
        {
            if(uniqueCharacters(""+iteme.getDigits()).equals("6"))
            {
                amount+=((Double.parseDouble(iteme.getAmount())/10)*750);
            }
            else
            {
                amount+=((Double.parseDouble(iteme.getAmount())/10)*1500);
            }
        }

        textwinamount.setText("₱"+currencyFormatter(Double.toString(amount)));

        return listViewItem;
    }
    public String currencyFormatter(String num) {
        double m = Double.parseDouble(num);
        DecimalFormat formatter = new DecimalFormat("###,###,###.00");
        return formatter.format(m);
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
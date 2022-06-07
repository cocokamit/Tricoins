package com.example.tricoins.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.tricoins.Accounts;
import com.example.tricoins.R;

import java.text.DecimalFormat;
import java.util.List;

public class CustomListWinNumber extends ArrayAdapter<WinNumber> {
    List<WinNumber> itemsList;
    Context context;
    public CustomListWinNumber(Context context,int LayoutId, List<WinNumber> itemsList) {
        super(context, LayoutId, itemsList);
        this.itemsList = itemsList;
        this.context=context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View listViewItem = inflater.inflate(R.layout.admincustomwinnumber, null, true);

        TextView txtentrywin=listViewItem.findViewById(R.id.txtentrywin);
        TextView txtdigit = listViewItem.findViewById(R.id.txtDigit);
        TextView textDate = listViewItem.findViewById(R.id.txtdate);
        TextView txttimecap = listViewItem.findViewById(R.id.txtTimeCap);
        TextView txttwinamount=listViewItem.findViewById(R.id.txttwinamount);

        final WinNumber iteme = itemsList.get(position);

        txtentrywin.setText(""+iteme.getWinCount());
        txtdigit.setText(""+iteme.getDigits());
        textDate.setText(iteme.getSysddate());
        txttimecap.setText(iteme.getTimeCap());
        txttwinamount.setText("₱"+currencyFormatter(iteme.getAmount()));

        return listViewItem;
    }
    public String currencyFormatter(String num) {
        double m = Double.parseDouble(num);
        DecimalFormat formatter = new DecimalFormat("###,###,###.00");
        return formatter.format(m);
    }

}

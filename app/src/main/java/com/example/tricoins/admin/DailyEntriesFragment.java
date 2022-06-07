package com.example.tricoins.admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.tricoins.Accounts;
import com.example.tricoins.AllItems;
import com.example.tricoins.Api;
import com.example.tricoins.CustomListView;
import com.example.tricoins.DashItems;
import com.example.tricoins.LoginActivity;
import com.example.tricoins.MainActivity;
import com.example.tricoins.QRScanner;
import com.example.tricoins.R;
import com.example.tricoins.RambolitoFragment;
import com.example.tricoins.RequestHandler;
import com.example.tricoins.StraightviewFragment;
import com.example.tricoins.sqlitehelpers.DatabaseHelper;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class DailyEntriesFragment extends Fragment {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View listViewItem = inflater.inflate(R.layout.fragment_dailyentries,container,false);

        view=listViewItem;
        setupViewPager1();
        return listViewItem;

    }


    //Sliding navigate *******************************************************************************************************************
    private void setupViewPager1(){
        ArrayList<Fragment> mf=new ArrayList<>();

        mf.add(new StraightviewFragment());
        mf.add(new RambolitoFragment());

        SectionsPagerAdapter adapter=new SectionsPagerAdapter(getActivity().getSupportFragmentManager(),mf);

        adapter.notifyDataSetChanged();
        ViewPager vp=(ViewPager) view.findViewById(R.id.viewpager);
        vp.setSaveFromParentEnabled(false);
        vp.setAdapter(adapter);
        adapter.getItem(0);
        final TabLayout tabLayout=(TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(vp);
        tabLayout.setTabTextColors(getResources().getColor(R.color.white),getResources().getColor(R.color.black));
        tabLayout.setTabIconTint(getResources().getColorStateList(R.color.black));

        tabLayout.getTabAt(0).setText("Straight").setIcon(R.drawable.ic_baseline_arrow_right_alt_24);
        tabLayout.getTabAt(1).setText("Rambolito").setIcon(R.drawable.ic_baseline_shuffle_24);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyRef", Context.MODE_PRIVATE);
        String dd=sharedPreferences.getString("1tabintent", "0");
        if(!sharedPreferences.getString("1tabintent", "0").equals(""))
        {
            if(dd.equals("0")) {
                tabLayout.getTabAt(0).select();
            }
            else
            {
                tabLayout.getTabAt(1).select();
            }
        }
    }

    class SectionsPagerAdapter extends FragmentStatePagerAdapter {
        private ArrayList<Fragment> mFragmentList;

        public SectionsPagerAdapter(FragmentManager fm, ArrayList<Fragment> mFragmentList)
        {
            super(fm);
            this.mFragmentList=mFragmentList;
        }
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }
        @Override
        public int getCount() {
            return mFragmentList.size();
        }
        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        @Override
        public int getItemPosition(Object object)
        {
            return PagerAdapter.POSITION_NONE;
        }

    }




}

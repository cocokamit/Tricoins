package com.example.tricoins;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tricoins.sqlitehelpers.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.generateViewId;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    boolean isUpdating = false;
    TextView uName,uEmail;
    DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             /*   Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            */
                Intent i=new Intent(getApplicationContext(), CreateTicket.class);
                startActivity(i);
            }
        });
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this, drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        uName=navigationView.getHeaderView(0).findViewById(R.id.uname);

        SharedPreferences sharedPreferences=this.getSharedPreferences("MyRef", Context.MODE_PRIVATE);
        String name=sharedPreferences.getString("Fullname", "0");
        uName.setText(name);

        setupViewPager1();
      /*  PerformNetworkRequest request =new PerformNetworkRequest(Api.URL_LIST_ITEMS_BY_TIMECAP+matchchecker, null, CODE_GET_REQUEST);
        request.execute();*/
    }

    //Sliding navigate *******************************************************************************************************************
    private void setupViewPager1(){
        ArrayList<Fragment> mf=new ArrayList<>();

        mf.add(new StraightviewFragment());
        mf.add(new RambolitoFragment());

        SectionsPagerAdapter adapter=new SectionsPagerAdapter(getSupportFragmentManager(),mf);

        adapter.notifyDataSetChanged();
        ViewPager vp=(ViewPager) findViewById(R.id.viewpager);
        vp.setSaveFromParentEnabled(false);
        vp.setAdapter(adapter);
        adapter.getItem(0);
        final TabLayout tabLayout=(TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(vp);
        tabLayout.setTabTextColors(getResources().getColor(R.color.white),getResources().getColor(R.color.black));
        tabLayout.setTabIconTint(getResources().getColorStateList(R.color.black));

            tabLayout.getTabAt(0).setText("Straight").setIcon(R.drawable.ic_baseline_arrow_right_alt_24);
            tabLayout.getTabAt(1).setText("Rambolito").setIcon(R.drawable.ic_baseline_shuffle_24);

        SharedPreferences sharedPreferences = this.getSharedPreferences("MyRef", Context.MODE_PRIVATE);
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

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("1intent","0");
        editor.apply();

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


    //Navigation
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    /*@Override
     public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.swiperefresh1);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }*/

    //OnClickback
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {

            finishAffinity();
            System.exit(0);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.nav_Logout: {

                Intent i=new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finishAffinity();
                break;
            }


            case R.id.nav_winnumber: {

                Intent i=new Intent(MainActivity.this, AllWinItems.class);
                startActivity(i);
                this.overridePendingTransition(R.anim.slide_right, R.anim.slide_up);
                break;
            }
           /* case R.id.nav_Alltickets:
            {
                Intent i=new Intent(MainActivity.this, QRScanner.class);
                startActivity(i);
                break;
            }*/
        }
        //close navigation drawer
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    //ListGetter
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
                        if(!tempmatchecker.equals("")){
                        matchchecker=tempmatchecker;
                        }
                       refreshItemList(object.getJSONArray("items"));
                }
                else
                {
                    Snackbar.make( findViewById(android.R.id.content), "Time schedule is currently empty", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
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
    }*/

 /*   //ListRefresher
    private void refreshItemList(JSONArray heroes) throws JSONException {

       Items=new ArrayList<DashItems>();

        for (int i = 0; i < heroes.length(); i++) {
            JSONObject obj = heroes.getJSONObject(i);

            Items.add(new DashItems(
                    obj.getInt("Id"),
                    obj.getInt("AgentId"),
                    obj.getString("Agent"),
                    obj.getInt("Digits"),
                    obj.getString("Amount"),
                    obj.getString("Typecount"),
                    obj.getString("qrcode"),
                    obj.getString("Description"),
                    obj.getString("Type"),
                    obj.getString("Sysddate"),
                    obj.getString("TimeCap")
            ));
        }

        txttitle.setText(matchchecker);
        txtcount.setText(""+heroes.length());

        CustomListView customListView=new CustomListView(this,R.layout.customlv,Items);

        listView.setAdapter(customListView);

        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }*/




}
package com.example.tricoins.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tricoins.Api;
import com.example.tricoins.CreateTicket;
import com.example.tricoins.CustomListView;
import com.example.tricoins.DashItems;
import com.example.tricoins.ItemDetails;
import com.example.tricoins.LoginActivity;
import com.example.tricoins.MainActivity;
import com.example.tricoins.R;
import com.example.tricoins.RequestHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    ArrayList<DashItems> Items=null;
    static String matchchecker,tempmatchecker="";

    ListView listView;
    DrawerLayout drawer;
    TextView uName,txttitle,txtcount;
    static String counts;
    Toolbar toolbar;
    Menu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Admin");
        setSupportActionBar(toolbar);
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

        if(sharedPreferences.getString("1intent", "0").equals("2")){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AllAgentsFragment()).commit();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("1intent","1");
            editor.apply();
        }
        else if(sharedPreferences.getString("1intent", "0").equals("3")){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PadaugFragment()).commit();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("1intent","1");
            editor.apply();
        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DailyEntriesFragment()).commit();
        }
    }


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
            case R.id.nav_DailyEntries: {
                startActivity(new Intent(this, AdminMainActivity.class));
                this.overridePendingTransition(0, 0);
                break;
            }
            case R.id.nav_WinningNumber: {

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PadaugFragment()).commit();
                break;
            }
            case R.id.nav_CreateUSer: {

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new CreateUserFragment()).commit();
                break;
            }
            case R.id.nav_Users: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AllAgentsFragment()).commit();
                break;
            }
            case R.id.nav_Statistic: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new StatisticFragment()).commit();
                break;
            }
            case R.id.nav_Settings: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SettingsFragment()).commit();
                break;
            }
            case R.id.nav_Logout:
            {
                Intent i=new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finishAffinity();
            }
        }
        //close navigation drawer
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }


}
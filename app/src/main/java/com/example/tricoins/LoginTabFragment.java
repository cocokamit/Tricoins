package com.example.tricoins;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.tricoins.admin.AdminMainActivity;
import com.example.tricoins.sqlitehelpers.DatabaseHelper;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;

public class LoginTabFragment extends Fragment {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    EditText username,password;
    TextInputLayout textInputLayout1,textInputLayout2;
    Button button;
    float v=0;
    ProgressBar progressBar;
    boolean isUpdating = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup root=(ViewGroup) inflater.inflate(R.layout.login_tab_fragment, container,false);

        username=root.findViewById(R.id.username);
        password=root.findViewById(R.id.password);
        button =root.findViewById(R.id.button);
        progressBar=root.findViewById(R.id.progressBar1);
        textInputLayout1=root.findViewById(R.id.textInputLayout1);
        textInputLayout2=root.findViewById(R.id.textInputLayout2);

        textInputLayout1.setTranslationX(800);
        textInputLayout2.setTranslationX(800);
        button.setTranslationX(800);

        textInputLayout1.setAlpha(v);
        textInputLayout2.setAlpha(v);
        button.setAlpha(v);

        textInputLayout1.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(100).start();
        textInputLayout2.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(200).start();
        button.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if it is updating
                if (isUpdating) {
                } else {
                    UserById();
                }
            }
        });
        return root;
    }
    private void UserById() {
        String Username = username.getText().toString().trim();
        String Password = password.getText().toString().trim();

        //validating the inputs

        if (TextUtils.isEmpty(Username)) {
            username.setError("Please enter username");
            username.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(Password)) {
            password.setError("Please enter password");
            password.requestFocus();
            return;
        }

        //if validation passes

        /*HashMap<String, String> params = new HashMap<>();
        params.put("Username", Username);
        params.put("Password", Password);
*/
        //Calling the create hero API
      /*  PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_ACCOUNT_BY_LOGIN, params, CODE_POST_REQUEST,progressBar,getActivity());
        request.execute();*/


        DatabaseHelper mydb=new DatabaseHelper(getActivity());
        Cursor res=mydb.Loginer(Username,Password);

        if(res.getCount()>0)
        {
            String role ="";
            String Status="";
            SharedPreferences sharedPreferences=getActivity().getSharedPreferences("MyRef", Context.MODE_PRIVATE);

            while(res.moveToNext()) {
                 SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Id",res.getString(0));
                editor.putString("Fullname",res.getString(1));
                editor.putString("1machchecker","");
                editor.apply();
                role=res.getString(4);
                Status=res.getString(5);

            }
            if(Status.equals("0")) {
                if (role.equals("0")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("1intent","0");
                    editor.apply();
                    getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
                } else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("1intent","1");
                    editor.apply();
                    getActivity().startActivity(new Intent(getActivity(), AdminMainActivity.class));
                }
                Toast.makeText(getActivity().getApplicationContext(), "Login Successfully", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getActivity().getApplicationContext(), "Account is currently suspended", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(getActivity().getApplicationContext(), "Account does not exist", Toast.LENGTH_SHORT).show();
        }

        username.getText().clear();
        password.getText().clear();
    }


}

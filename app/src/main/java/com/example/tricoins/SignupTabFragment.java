package com.example.tricoins;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;

public class SignupTabFragment extends Fragment {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    EditText username,password,fullname;
    Button button;
    float v=0;
    boolean isUpdating = false;
    ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup root=(ViewGroup) inflater.inflate(R.layout.signup_tab_fragment, container,false);

        username=root.findViewById(R.id.username);
        password=root.findViewById(R.id.password);
        fullname=root.findViewById(R.id.firstname);
        button=root.findViewById(R.id.button);
        progressBar=root.findViewById(R.id.progressBar1);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if it is updating
                if (isUpdating) {
                    //calling the method update hero
                    //method is commented becuase it is not yet created
                    //updateHero();
                } else {
                    //if it is not updating
                    //that means it is creating
                    //so calling the method create hero
                    createHero();
                }
            }
        });

        return root;
    }

    private void createHero() {
        String Username = username.getText().toString().trim();
        String Password = password.getText().toString().trim();
        String Fullname = fullname.getText().toString().trim();

        //validating the inputs
        if (TextUtils.isEmpty(Fullname)) {
            fullname.setError("Please enter name");
            fullname.requestFocus();
            return;
        }

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

        HashMap<String, String> params = new HashMap<>();
        params.put("Fullname", Fullname);
        params.put("Username", Username);
        params.put("Password", Password);


        //Calling the create hero API
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_ACCOUNT, params, CODE_POST_REQUEST,progressBar,getActivity());
        request.execute();

    }



}

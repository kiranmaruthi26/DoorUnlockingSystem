package com.example.doorunlockingsystem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class ShowRegistrations extends AppCompatActivity {

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_registrations);

        tv = findViewById(R.id.registerData);

        makeRequest();

    }



    private void makeRequest() {


        StringRequest request = new StringRequest(Request.Method.POST, "https://doorunlocking.getcleared.in/getUser.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        tv.setText("Server Response  : \n"+response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tv.setText("Error  : "+error.toString());
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() {

                HashMap<String, String> map = new HashMap<>();
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ShowRegistrations.this);
        requestQueue.add(request);

    }
}
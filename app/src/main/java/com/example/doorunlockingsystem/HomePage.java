package com.example.doorunlockingsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
    }

    public void registerUser(View view) {

        startActivity(new Intent(this, RegisterUser.class));
    }

    public void showRegistrations(View view) {
        startActivity(new Intent(this, ShowRegistrations.class));
    }

    public void control_rapi(View view) {
        startActivity(new Intent(this, Control_Server.class));
    }
}
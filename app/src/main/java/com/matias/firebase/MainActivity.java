package com.matias.firebase;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    @Override
    protected void onStart() {
        super.onStart();
        findViewById(R.id.fragment_container).postDelayed(new Runnable() {
            @Override
            public void run() {
                //loginActivity
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        }, 2000);
    }
}
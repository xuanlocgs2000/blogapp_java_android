package com.example.blogapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //pause app 1.5s chay onborading
        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            public void run() {
                SharedPreferences userPref = getApplicationContext().getSharedPreferences("user",Context.MODE_PRIVATE);
                boolean isLoggedIn = userPref.getBoolean("isLoggedIn",false);
                if(isLoggedIn){
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    finish();
                }
                else{isFirstTime();}
            }
        }, 1500);
    }
    private void isFirstTime() {
        //kiem tra lan dau tien run app dung sharedprefereces
            SharedPreferences preferences = getApplication().getSharedPreferences("onBoard", Context.MODE_PRIVATE);
            boolean isFirstTime = preferences.getBoolean("isFirstTime",true);
            if (isFirstTime){
                //
                SharedPreferences.Editor editor  = preferences.edit();//cho phep luu
                editor.putBoolean("isFirstTime",false);
                editor.apply();
                 //start Onboarding activity
                startActivity(new Intent(MainActivity.this,OnBoardActivity.class));
                finish();


            }
            else {
                //start Auth
                startActivity(new Intent(MainActivity.this, AuthActivity.class));
            }


    }

}
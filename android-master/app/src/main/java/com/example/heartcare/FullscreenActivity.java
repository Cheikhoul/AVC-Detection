package com.example.heartcare;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activité permettant d'avoir un écran de chargement au lancement de l'application (pour le moment décoratif mais pourra être utile pour charger des choses par la suite
 */
public class FullscreenActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT=1000;
    private SharedPref_Manager sharedPref_manager =new SharedPref_Manager();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if(sharedPref_manager.getMdpChoisi(getApplicationContext())==false){
                    intent = new Intent(FullscreenActivity.this,NewPassword.class);
                }else {
                    intent = new Intent(FullscreenActivity.this, HomePage.class);
                }
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
package com.example.heartcare;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class HomePage extends AppCompatActivity {
    Button okButton, forgotmdp;
    TextView password;
    SharedPref_Manager sharedPref_manager;
    static String passwordString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        sharedPref_manager=new SharedPref_Manager();


        password = (EditText) findViewById(R.id.mdp);
        password.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction()== KeyEvent.ACTION_DOWN)&&(keyCode==KeyEvent.KEYCODE_ENTER)){
                    final ProgressBar progressBar = (ProgressBar) findViewById(R.id.ProgressBar2);
                    progressBar.setVisibility(View.VISIBLE);
                    if(getHash(password.getText().toString()).equals(sharedPref_manager.getHashCode(v.getContext()))){
                        Intent intent = new Intent(HomePage.this,choice_screen.class);
                        passwordString=password.getText().toString();
                        startActivity(intent);
                        progressBar.setVisibility(View.INVISIBLE);
                    }else {
                        Toast.makeText(v.getContext(), "Mot de passe incorect.", Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
                return false;
            }
        });
        okButton = findViewById(R.id.Ok);
        forgotmdp = findViewById(R.id.forgotPass);
        forgotmdp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(HomePage.this,NewPassword.class);
                startActivity(intent);
            }
        });
        buttonEffect(okButton);
        buttonEffect(forgotmdp);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressBar progressBar = (ProgressBar) findViewById(R.id.ProgressBar2);
                progressBar.setVisibility(View.VISIBLE);
                String hashmdp=getHash(password.getText().toString());
                String hash=sharedPref_manager.getHashCode(v.getContext());
              //  Log.d("hash",sharedPref_manager.getHashCode(v.getContext()));
                //Log.d("hash",getHash(password.getText().toString()));
                if(getHash(password.getText().toString()).equals(sharedPref_manager.getHashCode(v.getContext()))){
                    Intent intent = new Intent(HomePage.this,choice_screen.class);
                    passwordString=password.getText().toString();
                    startActivity(intent);
                    progressBar.setVisibility(View.INVISIBLE);

            }else{
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(v.getContext(),"Mot de passe incorect.",Toast.LENGTH_LONG).show();
                }
        }
    });
}

    public static String getHash(String plainText) {
        String hashValue="";
        MessageDigest digest=null;
        try{
            digest = MessageDigest.getInstance("SHA-256");
            digest.update(plainText.getBytes());
            hashValue=bytesToHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashValue;
    }
    private static String bytesToHexString(byte[] bytes) {

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            //String hex = Integer.toHexString(0xFF & bytes[i]).toUpperCase();
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
    public static void buttonEffect(View button){
        button.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }

}

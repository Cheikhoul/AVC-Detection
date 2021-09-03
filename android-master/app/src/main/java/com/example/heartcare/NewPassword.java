package com.example.heartcare;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import static com.example.heartcare.HomePage.buttonEffect;
import static com.example.heartcare.HomePage.getHash;

public class NewPassword extends AppCompatActivity {
    private Button okNewMdp;
    private EditText newMdp, newMdpConf;
    SharedPref_Manager sharedPref_manager = new SharedPref_Manager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        okNewMdp= (Button) findViewById(R.id.OkNewMdp);
        buttonEffect(okNewMdp);
        newMdp= (EditText) findViewById(R.id.NewMdp);
        newMdpConf= (EditText) findViewById(R.id.NewMdpConf);
        okNewMdp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message;
                if(sharedPref_manager.getMdpChoisi(getApplicationContext())==false){
                    message="Attention, si vous perdez ce mot de passe vos données seront également perdues.\n Si vous avez retenu ce mot de passe confirmez.";
                }else{
                    message="Attention! En changeant de mot de passe, vous allez perdre les données déjà existantes.\n Voulez vous vraiment changer de mot de passe?";
                }
                if(newMdp.getText().toString().equals(newMdpConf.getText().toString())){
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setCancelable(true);
                    builder.setTitle("Confirmer le nouveau mot de passe");
                    builder.setMessage(message);
                    builder.setPositiveButton("Confirmer",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteDatabase(MyDatabaseHelper.Constants.DATABASE_NAME); //Supprime la BD
                                    String hash=getHash(newMdp.getText().toString());
                                    sharedPref_manager.saveHashCode(hash,NewPassword.this);
                                    Intent intent = new Intent(NewPassword.this,HomePage.class);
                                    startActivity(intent);
                                }
                            });
                    builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else{
                    Toast.makeText(v.getContext(),"Les 2 mots de passe ne coïncident pas!",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}

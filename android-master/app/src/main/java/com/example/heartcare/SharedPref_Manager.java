package com.example.heartcare;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref_Manager {

    public final String SHARED_PREFS= "sharePrefs";
    public final String HASH="HASH";
    public final String MDP_CHOISI="MDP_CHOISI";

    public SharedPref_Manager(){
        super();
    }

    public void saveHashCode(String hash_code, Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE); //Creation de la shared preference en mode private
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(HASH,hash_code);
        editor.putBoolean(MDP_CHOISI,true); //Pour vérifié que c'est un changement de mot de passe et pas une initialisation
        editor.apply();

    }
    public boolean getMdpChoisi(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(MDP_CHOISI,false);
    }
    public String getHashCode(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        String res=sharedPreferences.getString(HASH,"");
        return res;
    }


}

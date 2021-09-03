package com.example.heartcare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class MainActivity extends AppCompatActivity {
    EditText email, password;
    Button bLog;
    private ApiInterface apiInterface;
    Button bReg;
    static final String BASE_URL = "http://192.168.1.12:45000";
    public OkHttpClient client = null;
    private static MainActivity instance;


    //1313
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;
        bLog = (Button) findViewById(R.id.login);
        bLog.setOnClickListener(ClickOnLogin);
        email = (EditText) findViewById(R.id.identifiant);
        password = (EditText) findViewById(R.id.mdp);



    }



        /**
         * Connection to the server on click
         */
        private final View.OnClickListener ClickOnLogin = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverLogIn();
            }

        };

    private void serverLogIn(){
        // Login interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.level(HttpLoggingInterceptor.Level.BASIC);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.ProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        // Cookie container
        ClearableCookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(this));
        client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build();



        OkHttpRequest req = new OkHttpRequest(BASE_URL, client);
        okhttp3.Call call = req.basicLogin(email.getText().toString(),
                password.getText().toString(),
                "loginMobile");

        if(call == null)
           return;

        call.enqueue(new okhttp3.Callback() {

            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        //vibrator.vibrate(1000);
                        Toast.makeText(getApplicationContext(), "Error connecting -  "
                                + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull final okhttp3.Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!response.isSuccessful()) {
                            System.out.println(response.body());
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), "email ou mdp incorrect",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(),
                                    "Vous êtes maintenant connecté",
                                    Toast.LENGTH_SHORT)
                                    .show();
                            Intent intent = new Intent();
                            intent.putExtra("result", "result");
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                });
            }


        });
    }

    /**
     * Intent call on sign up in the applications. Passing server information.
     */

    public void goToChoiceScreen(View view) {
        Intent intent = new Intent(MainActivity.this, choice_screen.class);

        this.startActivity(intent);
    }

    /**
     *
     * @return - the client associated with the activity
     */
    public OkHttpClient getClient(){ return this.client;}

    public static MainActivity getInstance(){ return instance;}

    /**
     * Actualising the  state current client for further requests
     * @param client the new client
     */
    public void setClient(OkHttpClient client){
        this.client = client;
    }
}


package com.example.heartcare;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.heartcare.BlunoLibrary.connectionStateEnum;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Response;

import static com.example.heartcare.HomePage.buttonEffect;



public class choice_screen extends BlunoLibrary implements View.OnClickListener {
    public static final String FILE_NAME="Data.txt";    //Nom du fichier où sont enregistrées les données
    private static final int INTENT_CALL_ID = 1;
    private ImageButton buttonScan;
    private TextView serialReceivedText, state_txt;
    private HashMap<String, String> webServer;
    protected static int hourDeb, minDeb, dayDeb, yearDeb, monthDeb, hourFin, minFin, dayFin, yearFin, monthFin;
    ImageButton btnTimeDebPicker, btnDayDebPicker, btnTimeFinPicker, btnDayFinPicker,btnFrequencyCurve,btnSendData;
    EditText timeDeb, dayDebTxt, timeFin, dayFinTxt;
    private ApiInterface apiInterface;
    static final String BASE_URL = "http://192.168.1.12:45000";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION=456;

    protected static AccessLocal accessLocal;

    protected Uri internal;

    private SimpleCrypto simpleCrypto;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_screen);
        simpleCrypto=new SimpleCrypto();
        accessLocal=new AccessLocal(this);

        onCreateProcess();

        serialBegin(115200);													//set the Uart Baudrate on BLE chip to 115200

        serialReceivedText=(TextView) findViewById(R.id.serialReveicedText);	//initial the EditText of the received data


        buttonScan = (ImageButton) findViewById(R.id.buttonScan);					//initial the button for scanning the BLE device
        buttonScan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                buttonScanOnClickProcess();										//Alert Dialog for selecting the BLE device
            }
        });
         buttonEffect(buttonScan);



        /*String test2="  12/03/2020/14:02,69";

        Pattern pattern2 = Pattern.compile("([0-9]+)/(.+)/(.+)/(.+):(.+),(.+)");
        Matcher matcher2 = pattern2.matcher(test2);
        if(matcher2.find()) {
            String cipherHeart_rate=simpleCrypto.encrypt(HomePage.passwordString+"qssfFs32fFwada",matcher2.group(6));
            accessLocal.ajout(matcher2.group(3),matcher2.group(2),matcher2.group(1),matcher2.group(4),matcher2.group(5),cipherHeart_rate);
        }*/
        //Recuperation de tout les "modules" du layout associé



        state_txt= (TextView)findViewById(R.id.state_txt);
        timeDeb = (EditText) findViewById(R.id.hourTxt);
        dayDebTxt = (EditText) findViewById(R.id.dateTxt);
        timeFin = (EditText) findViewById(R.id.hourFinTxt);
        dayFinTxt = (EditText) findViewById(R.id.dateFinTxt);

        btnTimeDebPicker = (ImageButton) findViewById(R.id.buttonHourDeb);
        btnTimeDebPicker.setOnClickListener(this);
        buttonEffect(btnTimeDebPicker);

        btnDayDebPicker = (ImageButton) findViewById(R.id.buttonDayDeb);
        btnDayDebPicker.setOnClickListener(this);
         buttonEffect(btnDayDebPicker);

        btnTimeFinPicker = (ImageButton) findViewById(R.id.buttonHourFin);
        btnTimeFinPicker.setOnClickListener(this);
         buttonEffect(btnTimeFinPicker);

        btnDayFinPicker = (ImageButton) findViewById(R.id.buttonDayFin);
        btnDayFinPicker.setOnClickListener(this);
         buttonEffect(btnDayFinPicker);

        btnFrequencyCurve =(ImageButton)findViewById(R.id.buttonFrequencyCurve);

        btnFrequencyCurve.setOnClickListener(this);
         buttonEffect(btnFrequencyCurve);


        btnSendData=(ImageButton)findViewById(R.id.buttonSendData);


        btnSendData.setOnClickListener(this);
         buttonEffect(btnSendData);



    //Demande d'accès a la position GPS du téléphone pour pouvoir scanner les appareils des environs
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_REQUEST_COARSE_LOCATION);
        }


    }


    //Ici on définit tous ce que fait les différents cliques sur les différents boutons
    @Override
    public void onClick(View v) {
        //Définition des pickers:
        if (v == btnTimeDebPicker) {
            final Calendar c = Calendar.getInstance();
            hourDeb = c.get(Calendar.HOUR_OF_DAY);
            minDeb = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                    timeDeb.setText(hourOfDay + ":" + minute);
                    hourDeb=hourOfDay;
                    minDeb=minute;
                }
            }, hourDeb, minDeb, true);
            timePickerDialog.show();


        }
        if (v == btnDayDebPicker) {
            final Calendar c = Calendar.getInstance();
            dayDeb = c.get(Calendar.DAY_OF_MONTH);
            yearDeb = c.get(Calendar.YEAR);
            monthDeb = c.get(Calendar.MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    dayDebTxt.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                    dayDeb=dayOfMonth;
                    yearDeb=year;
                    monthDeb=month+1;
                }
            }, yearDeb, monthDeb, dayDeb);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();

        }
        if (v == btnTimeFinPicker) {
            final Calendar c = Calendar.getInstance();
            hourFin = c.get(Calendar.HOUR_OF_DAY);
            minFin = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                    timeFin.setText(hourOfDay + ":" + minute);
                    hourFin=hourOfDay;
                    minFin=minute;
                }
            }, hourFin, minFin, true);
            timePickerDialog.show();

        }
        if (v == btnDayFinPicker) {
            final Calendar c = Calendar.getInstance();
            dayFin = c.get(Calendar.DAY_OF_MONTH);
            yearFin = c.get(Calendar.YEAR);
            monthFin = c.get(Calendar.MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    dayFinTxt.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                    dayFin=dayOfMonth;
                    yearFin=year;
                    monthFin=month+1;
                }
            }, yearFin, monthFin, dayFin);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();

        }
        //Boutons pour lancer les activitées associées aux graphiques que l'on souhaite observer
        if(v==btnFrequencyCurve){
            if( dayDeb==0|| yearDeb==0|| monthDeb==0||dayFin==0|| yearFin==0|| monthFin==0){
                Toast.makeText(this,"Tout les paramètres doivent être remplis.",Toast.LENGTH_SHORT).show();
            }else if(yearDeb>yearFin || ((yearDeb==yearFin) && (monthDeb>monthFin)) ||((yearDeb==yearFin) && (monthDeb==monthFin)&&(dayDeb>dayFin))){
                Toast.makeText(this,"Impossible: Le jour/année/jour de début est supérieur au jour/année/jour de fin.",Toast.LENGTH_SHORT).show();
            }else if(((yearDeb==yearFin) && (monthDeb==monthFin)&&(dayDeb==dayFin)&&(hourDeb>hourFin))){
                Toast.makeText(this,"Impossible: L'heure de début est supérieure à l'heure de fin.",Toast.LENGTH_SHORT).show();
            }else if(((yearDeb==yearFin) && (monthDeb==monthFin)&&(dayDeb==dayFin)&&(hourDeb==hourFin)&&(minDeb>minFin)) ){      //Gère le cas ou les pickers ne sont pas bien remplis
                Toast.makeText(this,"Impossible: Les minutes de début sont supérieures aux minutes de fin.",Toast.LENGTH_SHORT).show();
            }else if(((yearDeb==yearFin) && (monthDeb==monthFin)&&(dayDeb==dayFin)&&(hourDeb==hourFin)&&(minDeb==minFin)) ){
                Toast.makeText(this,"Impossible: L'intervalle n'est pas valide.",Toast.LENGTH_SHORT).show();
            }else{
                Intent intent = new Intent(this, Chart1.class);
                timeDeb.setText("Heure début");
                dayDebTxt.setText("Date début");
                timeFin.setText("Heure fin");
                dayFinTxt.setText("Date fin");
                startActivity(intent);
            }
        }
        if(v==btnSendData){
            File internalFile= getFileStreamPath(FILE_NAME);
            File internalDatabase=getDatabasePath(MyDatabaseHelper.Constants.DATABASE_NAME);
            // Essayer d'envoyer les données tout simplement en envoyant le fichier de la database

           // internal=Uri.fromFile(internalDatabase);
            internal =Uri.fromFile(internalFile);
            if(internal!=null) {
                Intent intent = new Intent(choice_screen.this,MainActivity.class);
                startActivityForResult(intent,INTENT_CALL_ID);

            }
        }


    }
    //Méthode pour écrire dans le fichier a partir des données recues par l'intent venant du onSerialReceived
    public void writeFile(String data){
        System.out.println(getFilesDir());
        FileOutputStream fileOutputStream=null;
        try {
            fileOutputStream = openFileOutput(FILE_NAME,MODE_APPEND);
            fileOutputStream.write(data.getBytes());
            fileOutputStream.close();

           // Toast.makeText(getApplicationContext(), "Text Saved", Toast.LENGTH_SHORT).show(); //ici on montre un toast pour chaque truc enregistré on enlevera cette fonctionnalité après c'est juste pour voir si ca enregistre bien au début
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    public void writeFileForDB() {
        List<String> list =accessLocal.recupAll();
        for(String data: list){
            writeFile(data);
        }
    }

    /**
     *
     * @param fileUri  file path
     *                 Method sends database content written in a file to the web server
     */

    public void uploadFile(@NotNull Uri fileUri){


        final okhttp3.OkHttpClient client = MainActivity.getInstance().getClient();
        if(client == null){
            Log.d("error", "Error instantiating client from login request");
            return;
        }
        final OkHttpRequest req = new OkHttpRequest(BASE_URL, client);
        okhttp3.Call call = req.uploadData(fileUri, "set_data", this );

        call.enqueue(new okhttp3.Callback(){

            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull final okhttp3.Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(response.isSuccessful()){
                            Toast.makeText(choice_screen.this, "Toutes vos données ont été transférées", Toast.LENGTH_SHORT).show(); //prévoir qq chose pour supprimer les données après transfert sur le serveur
                            deleteDatabase(MyDatabaseHelper.Constants.DATABASE_NAME); //Supprime la BD une fois le transfert des données au serveur effectué (que si la réponse du serveur est positive)
                            MainActivity.getInstance().setClient(client);
                            logoutFromServer();
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(choice_screen.this,"Send unsuccessful.",
                                Toast.LENGTH_SHORT).show();
                        Log.d("error", e.toString());
                        MainActivity.getInstance().setClient(client);
                        logoutFromServer();
                    }
                });
            }
        });
    }
    /**
     * Sending logout request to the server at the end of the activity usage
     */
    private void logoutFromServer(){

        final okhttp3.OkHttpClient client = MainActivity.getInstance().getClient();
        if(client == null){
            Log.d("error", "Error instantiating client from login request");
            return;
        }
        final OkHttpRequest req = new OkHttpRequest(BASE_URL, client);
        okhttp3.Call call = req.logout("logout");
        call.enqueue(new okhttp3.Callback() {


            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
                Log.d("msg", "Logout successful");
                deleteFile(FILE_NAME);
                Log.d("msg", "file deleted");

            }

            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                Log.d("msg", "Logout error" + e.toString());
                deleteFile(FILE_NAME);
                Log.d("msg", "file deleted");
            }
        });
    }

    /**
     * Méthodes liées a la connection bluetooth avec le bluno
     */
    protected void onResume(){
        super.onResume();
        onResumeProcess();														//onResume Process by BlunoLibrary
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if(requestCode==INTENT_CALL_ID){
           if(data!=null){
               writeFileForDB();//Ecrit toutes les données de la base de donnée dans un fichier texte pour l'envoyer ensuite
               this.uploadFile(internal);
           }

       }else {

           onActivityResultProcess(requestCode, resultCode, data);                    //onActivityResult Process by BlunoLibrary
           super.onActivityResult(requestCode, resultCode, data);
       }
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPauseProcess();														//onPause Process by BlunoLibrary
    }

    protected void onStop() {
        super.onStop();
        onStopProcess();														//onStop Process by BlunoLibrary
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyProcess();														//onDestroy Process by BlunoLibrary
    }

    @Override
    public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
        switch (theConnectionState) {											//Four connection state
            case isConnected:
                state_txt.setText("Connected");
                break;
            case isConnecting:
                state_txt.setText("Connecting");
                break;
            case isToScan:
                state_txt.setText("Scan");
                break;
            case isScanning:
                state_txt.setText("Scanning");
                break;
            case isDisconnecting:
                state_txt.setText("isDisconnecting");
                break;
            default:
                break;
        }
    }

    @Override
    public void onSerialReceived(String theString) {							//Once connection data received, this function will be called
        serialReceivedText.append(theString);//append the text into the EditText
        //The Serial data from the BLUNO may be sub-packaged, so using a buffer to hold the String is a good choice.
        //Ajout a la base de donnée ligne par ligne recues
        Pattern pattern = Pattern.compile("(.+)/(.+)/(.+)/(.+):(.+),(.+)");
        Matcher matcher = pattern.matcher(theString);
        if(matcher.find()) {
            String cipherHeart_rate=simpleCrypto.encrypt(HomePage.passwordString+"qssfFs32fFwada",matcher.group(6)); //"qssfFs32fFwada" est un ajout de "sel" car 4 caractère pour le mdp c'est pas assez
            accessLocal.ajout(matcher.group(3),matcher.group(2),matcher.group(1),matcher.group(4),matcher.group(5),cipherHeart_rate);
        }

        ((ScrollView)serialReceivedText.getParent()).fullScroll(View.FOCUS_DOWN);
    }

}



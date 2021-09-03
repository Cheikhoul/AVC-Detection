package com.example.heartcare;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccessLocal {

    private MyDatabaseHelper myDatabaseHelper;
    private SQLiteDatabase db;
    private SimpleCrypto simpleCrypto;

    public AccessLocal(Context context) {
        myDatabaseHelper = new MyDatabaseHelper(context, MyDatabaseHelper.Constants.DATABASE_NAME, null, MyDatabaseHelper.Constants.DATABASE_VERSION);
        simpleCrypto=new SimpleCrypto();
    }

    /**
     * ajout des données dans la base de donnée
     *
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @param heartRate
     */
    public void ajout(String year, String month, String day, String hour, String minute, String heartRate) {  //ajouter float bloodPress plus tard
        db = myDatabaseHelper.getWritableDatabase();
        String heure_SQLFormat = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":00"; //Ici les secondes sont initialisées a 0 car on considere la précision de la mesure de l'ordre de la minute

        String request = "insert into " + MyDatabaseHelper.Constants.MY_TABLE + " (" + MyDatabaseHelper.Constants.KEY_COL_DATE_TIME + "," + MyDatabaseHelper.Constants.KEY_COL_HEART_RATE + ")" + " values(\""
                + heure_SQLFormat + "\",\"" + heartRate + "\")";
        db.execSQL(request);
    }

    /**
     * @param yearDeb
     * @param monthDeb
     * @param dayDeb
     * @param hourDeb
     * @param minuteDeb
     * @param yearFin
     * @param monthFin
     * @param dayFin
     * @param hourFin
     * @param minuteFin
     * @return La liste des éléments sélectionnés par les pickers
     */
    public List<String> recup(int yearDeb, int monthDeb, int dayDeb, int hourDeb, int minuteDeb, int yearFin, int monthFin, int dayFin, int hourFin, int minuteFin) {
        List<String> list = new ArrayList<>();
        //Transformation des int en String et ajout de 0 si besoin
        String monthdeb, daydeb, hourdeb, minutedeb, monthfin, dayfin, hourfin, minutefin;

        if (monthDeb < 10) {
            monthdeb = "0" + monthDeb;
        } else {
            monthdeb = "" + monthDeb;
        }
        if (dayDeb < 10) {
            daydeb = "0" + dayDeb;
        } else {
            daydeb = "" + dayDeb;
        }
        if (hourDeb < 10) {
            hourdeb = "0" + hourDeb;
        } else {
            hourdeb = "" + hourDeb;
        }
        if (minuteDeb < 10) {
            minutedeb = "0" + minuteDeb;
        } else {
            minutedeb = "" + minuteDeb;
        }

        if (monthFin < 10) {
            monthfin = "0" + monthFin;
        } else {
            monthfin = "" + monthFin;
        }
        if (dayFin < 10) {
            dayfin = "0" + dayFin;
        } else {
            dayfin = "" + dayFin;
        }
        if (hourFin < 10) {
            hourfin = "0" + hourFin;
        } else {
            hourfin = "" + hourFin;
        }
        if (minuteFin < 10) {
            minutefin = "0" + minuteFin;
        } else {
            minutefin = "" + minuteFin;
        }

        String heure_SQLFormatDeb = yearDeb + "-" + monthdeb + "-" + daydeb + " " + hourdeb + ":" + minutedeb + ":00";
        String heure_SQLFormatFin = yearFin + "-" + monthfin + "-" + dayfin + " " + hourfin + ":" + minutefin + ":00";


        String SQLrequest = "SELECT * FROM " + MyDatabaseHelper.Constants.MY_TABLE + " WHERE "
                + MyDatabaseHelper.Constants.KEY_COL_DATE_TIME + " BETWEEN \"" + heure_SQLFormatDeb + "\" AND \"" + heure_SQLFormatFin + "\";";
        /*Cursor cursor=myDatabaseHelper.getReadableDatabase().rawQuery(SQLrequest,new String[]{String.valueOf(yearDeb),String.valueOf(yearFin),String.valueOf(monthDeb),String.valueOf(monthFin),
                String.valueOf(dayDeb),String.valueOf(dayFin),String.valueOf(hourDeb),String.valueOf(hourFin),String.valueOf(minuteDeb),String.valueOf(minuteFin)});*/
        Cursor cursor = myDatabaseHelper.getReadableDatabase().rawQuery(SQLrequest, null);


        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String date_time = cursor.getString(MyDatabaseHelper.Constants.DATE_TIME_COLUMN);
            String cipherHeart_rate = cursor.getString(MyDatabaseHelper.Constants.HEART_RATE_COLUMN);
            String plainHeart_rate = simpleCrypto.decrypt(HomePage.passwordString + "qssfFs32fFwada", cipherHeart_rate);
            Pattern pattern = Pattern.compile("(.+)-(.+)-(.+) (.+):(.+):00");
            Matcher matcher = pattern.matcher(date_time);
            String res = "";
            if (matcher.find()) {
                res = matcher.group(3) + "/" + matcher.group(2) + "/" + matcher.group(1) + "/" + matcher.group(4) + ":" + matcher.group(5) + "," + plainHeart_rate;
            }
            list.add(res);
            }
            cursor.close();
            return list;
        }


        public List<String> recupAll(){

            List<String> list = new ArrayList<>();

            String SQLrequest = "SELECT * FROM " + MyDatabaseHelper.Constants.MY_TABLE +";";
            Cursor cursor = myDatabaseHelper.getReadableDatabase().rawQuery(SQLrequest, null);

            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                String date_time = cursor.getString(MyDatabaseHelper.Constants.DATE_TIME_COLUMN);
                String cipherHeart_rate = cursor.getString(MyDatabaseHelper.Constants.HEART_RATE_COLUMN);

                String plainHeart_rate = simpleCrypto.decrypt(HomePage.passwordString+"qssfFs32fFwada",cipherHeart_rate);

                Pattern pattern = Pattern.compile("(.+)-(.+)-(.+) (.+):(.+):00");
                Matcher matcher = pattern.matcher(date_time);
                String res = "";
                if (matcher.find()) {
                    res = matcher.group(3) + "/" + matcher.group(2) + "/" + matcher.group(1) + "/" + matcher.group(4) + ":" + matcher.group(5) + "," + plainHeart_rate+"\n";
                }
                list.add(res);
            }
            cursor.close();
            return list;
        }
}

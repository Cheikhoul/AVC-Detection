package com.example.heartcare;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {


    public static class Constants implements BaseColumns{
        // The database name
        public static final String DATABASE_NAME="HeartCare.db";

        // The database version
        public static final int DATABASE_VERSION =1;

        // The table Name
        public static final String MY_TABLE="Frequences";

        // ## Column name ##

        // My Column Blood pressure and the associated explanation for end-users
        public static final String KEY_COL_DATE_TIME = "date_time";

        // My Column Heart rate and the associated explanation for end-users
        public static final String KEY_COL_HEART_RATE = "heart_rate";

        // My Column Blood pressure and the associated explanation for end-users
       // public static final String KEY_COL_BLOOD_PRESS= "blood_pressure";



        // Indexes des colonnes
        public static final int DATE_TIME_COLUMN = 0;

        public static final int HEART_RATE_COLUMN = 1;

        //public static final int BLOOD_PRESS_COLUMN = 2;


    }

    /**
     * The static string to create the database.
     */
    private static final String DATABASE_CREATE = "CREATE TABLE "
            + Constants.MY_TABLE + "("
            + Constants.KEY_COL_DATE_TIME+ " DATETIME NOT NULL PRIMARY KEY, "
            + Constants.KEY_COL_HEART_RATE + " STRING NOT NULL "
            //+ Constants.KEY_COL_BLOOD_PRESS+ " FLOAT NOT NULL, "
            +")";

    public MyDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the new database using the SQL string Database_create
        db.execSQL(DATABASE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w("DBOpenHelper", "Mise à jour de la version " + oldVersion
                + " vers la version " + newVersion
                + ", les anciennes données seront détruites ");
        // Drop the old database
        db.execSQL("DROP TABLE IF EXISTS " + Constants.MY_TABLE);
        // Create the new one
        onCreate(db);
        // or do a smartest stuff
    }
}

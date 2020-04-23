package com.example.coronatracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

import com.example.coronatracker.Patient;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Covid19.db";
    public static final String TABLE_NAME = "Patients";
    public static final String COL1 = "id";
    public static final String COL2 = "firstName";
    public static final String COL3 = "lastName";
    public static final String COL4 = "age";
    public static final String COL5 = "streetAddress";
    public static final String COL6 = "city";
    public static final String COL7 = "province";
    public static final String COL8 = "country";
    public static final String COL9 = "postalCode";
    public static final String COL10 = "latitude";
    public static final String COL11 = "longitude";
    public static final String COL12 = "dateOfInfection";
    public static final String COL13 = "alive";
    public static final String COL14 = "recovered";
    public static final String COL15 = "gender";
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME +
                    "("
                    + COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COL2 + " TEXT NOT NULL, "
                    + COL3 + " TEXT NOT NULL, "
                    + COL4 + " INTEGER NOT NULL, "
                    + COL5 + " TEXT NOT NULL, "
                    + COL6 + " TEXT NOT NULL, "
                    + COL7 + " TEXT NOT NULL, "
                    + COL8 + " TEXT NOT NULL, "
                    + COL9 + " TEXT NOT NULL, "
                    + COL10 + " REAL NOT NULL, "
                    + COL11 + " REAL NOT NULL, "
                    + COL12 + " TEXT NOT NULL, "
                    + COL13 + " INTEGER NOT NULL, "
                    + COL14 + " INTEGER NOT NULL, "
                    + COL15 + " INTEGER NOT NULL);";

    public static final String DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    //Used for inserting patient record. Return true if insert success else false
    public boolean insertPatient(Patient patient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL2, patient.getFirstName());
        cv.put(COL3, patient.getLastName());
        cv.put(COL4, patient.getAge());
        cv.put(COL5, patient.getStreetAddress());
        cv.put(COL6, patient.getCity());
        cv.put(COL7, patient.getProvince());
        cv.put(COL8, patient.getCountry());
        cv.put(COL9, patient.getPostalCode());
        cv.put(COL10, patient.getLatitude());
        cv.put(COL11, patient.getLongitude());
        cv.put(COL12, patient.getDateOfInfection());
        cv.put(COL13, patient.getAlive());
        cv.put(COL14, patient.getRecovered());
        cv.put(COL15, patient.getGender());


        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    //Gets all records from the student table and stores in a cursor
    public Cursor viewData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor;
        String query = "SELECT * FROM " + TABLE_NAME;
        cursor = db.rawQuery(query, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    //Fetching record with respect to patient id
    public Cursor viewRecord(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor;
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE id=" + id;
        cursor = db.rawQuery(query, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
    //Getting data of patients by city
    public Cursor getInfectedPatientsByCity()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor;
        String query = "SELECT COUNT(id) AS patientCount,city FROM Patients GROUP BY(city) HAVING COUNT(id)>0";
        cursor = db.rawQuery(query, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
    //function to retrieve count of patient on age basis
    public int[] getCount()
    {
        int count[]=new int[3];
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor1,cursor2,cursor3;
        String query1 = "SELECT COUNT(age) AS ageCount from "+TABLE_NAME+" WHERE age BETWEEN 1 AND 20";
        String query2 = "SELECT COUNT(age) AS ageCount from "+TABLE_NAME+" WHERE age BETWEEN 20 AND 45";
        String query3 = "SELECT COUNT(age) AS ageCount from "+TABLE_NAME+" WHERE age BETWEEN 45 AND 80";
        cursor1=db.rawQuery(query1,null);
        cursor2=db.rawQuery(query2,null);
        cursor3=db.rawQuery(query3,null);
        if (cursor1 != null) {
            cursor1.moveToFirst();
            count[0] =  cursor1.getInt(cursor1.getColumnIndex("ageCount"));
        }
        if (cursor2!= null) {
            cursor2.moveToFirst();
            count[1] =  cursor2.getInt(cursor2.getColumnIndex("ageCount"));
        }
        if(cursor3!=null)
        {
            cursor3.moveToFirst();
            count[2] = cursor3.getInt(cursor3.getColumnIndex("ageCount"));
        }
        return count;
    }
    //Updates student record
    public int updatePatient(Patient patient) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COL2, patient.getFirstName());
        cv.put(COL3, patient.getLastName());
        cv.put(COL4, patient.getAge());
        cv.put(COL5, patient.getStreetAddress());
        cv.put(COL6, patient.getCity());
        cv.put(COL7, patient.getProvince());
        cv.put(COL8, patient.getCountry());
        cv.put(COL9, patient.getPostalCode());
        cv.put(COL10, patient.getLatitude());
        cv.put(COL11, patient.getLongitude());
        cv.put(COL12, patient.getDateOfInfection());
        cv.put(COL13, patient.getAlive());
        cv.put(COL14, patient.getRecovered());
        cv.put(COL15, patient.getGender());


        int numOfRows = db.update(TABLE_NAME, cv, "id=" + patient.getId(), null);
        return numOfRows;
    }

    //Delete the student record by id
    public int deleteRecord(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "id=" + id, null);
    }
}

package com.delaroystudios.locationgeo.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SqlDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "location.db";
    private static final String TABLE_NAME = "places";
    private static final String KEY_ID = "placeId";
    private static final String KEY_NAME = "placeName";
    private static final String KEY_LAT = "placeLatitude";
    private static final String KEY_LANG = "placeLongitude";
    private static final String KEY_ISDELETE = "placeIsDelete";
    private static final String[] COLUMNS = {KEY_ID, KEY_NAME, KEY_LAT, KEY_LANG, KEY_ISDELETE};
    public SqlDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE "+TABLE_NAME+"(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                KEY_NAME +" TEXT,"+
                KEY_LAT+" TEXT,"+
                KEY_LANG+" TEXT,"+
                KEY_ISDELETE+" INTEGER DEFAULT 0)";
        db.execSQL(CREATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void deleteOne(PlaceDetails placeDetails) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID +" = ?", new String[] { String.valueOf(placeDetails.getPlaceId()) });
        db.close();
    }

    public PlaceDetails getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, // a. table
                COLUMNS, // b. column names
                KEY_ID +" = ?", // c. selections
                new String[] { String.valueOf(id) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor != null)
            cursor.moveToFirst();

        PlaceDetails details = new PlaceDetails(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getInt(4));
        return details;
    }

    public List<PlaceDetails> allPlace() {

        List<PlaceDetails> places = new ArrayList<PlaceDetails>();
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        PlaceDetails placeDetails = null;
        if(cursor!=null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    placeDetails = new PlaceDetails(
                            cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getInt(4)
                    );
                    places.add(placeDetails);
                } while (cursor.moveToNext());
            }
        }

        return places;
    }

    public void addLocation(PlaceDetails placeDetails) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, placeDetails.getPlaceName());
        values.put(KEY_LAT, placeDetails.getPlaceLatitude());
        values.put(KEY_LANG, placeDetails.getPlaceLongitude());
        values.put(KEY_ISDELETE, placeDetails.getPlaceIsDelete());
        // insert
        db.insert(TABLE_NAME,null, values);
        db.close();
    }

    public boolean updateLocation(int id, int isDeleted) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("UPDATE "+TABLE_NAME+" SET "+KEY_ISDELETE+" = "+"'"+isDeleted+"' "+ "WHERE "+KEY_ID+"="+"'"+id+"'");
        return true;
    }
}

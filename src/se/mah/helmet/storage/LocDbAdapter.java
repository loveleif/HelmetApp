package se.mah.helmet.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

public class LocDbAdapter {
	// TODO Implement trip table
	
    private static final String DB_NAME = "loc";
    private static final int DB_VERSION = 1;
    
    private static final String TAG = LocDbAdapter.class.getSimpleName();
    
    private final Context mCtx;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    private static final String TABLE_COORD = "coord";
    
    private static final String KEY_ROWID = "_id";
    private static final String KEY_TIME = "time";
    private static final String KEY_LAT = "latitude";
    private static final String KEY_LONG = "longitude";
    private static final String KEY_ALT = "altitude";
    private static final String KEY_ACCURACY = "accuracy";
    private static final String KEY_SPEED = "speed";
    private static final String KEY_BEARING = "bearing";
    private static final String TABLE_COORD_CREATE =
    		"CREATE TABLE " + TABLE_COORD + "(" +
    		KEY_ROWID + " integer primary key autoincrement, " +
    		KEY_TIME + " text not null," +
    		KEY_LAT + " real," +
    		KEY_LONG + " real," +
    		KEY_ALT + " real," +
    		KEY_ACCURACY + " real," +
    		KEY_SPEED + " real," +
    		KEY_BEARING + " real)";
    
    /**
     * SQLiteOpenHelper for the LocDbAdapter
     *
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
    	
        DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }
  	
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(TABLE_COORD_CREATE);
			Log.i(TAG, "Created database " + DB_NAME + ".");
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data.");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_COORD);
            onCreate(db);
		}
    }
    
    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public LocDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }
    
    /**
     * Open the database. If it cannot be opened, try to create a new instance 
     * of the database. If it cannot be created, throw SQLException.
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public LocDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
    	mDb.close();
    }
    
    /**
     * Insert location to database.
     * 
     * @param location Location to insert
     * @return the row ID of the newly inserted row, or -1 if an error occurred (see android.database.sqlite.SQLiteDatabase.insert(...))
     */
    public long insertLocation(Location location) {
    	ContentValues values = new ContentValues(7);
    	values.put(KEY_TIME, location.getTime());
    	values.put(KEY_LAT, location.getLatitude());
    	values.put(KEY_LONG, location.getLongitude());
    	values.put(KEY_ALT, location.hasAltitude());
    	values.put(KEY_ACCURACY, location.getAccuracy());
    	values.put(KEY_SPEED, location.getSpeed());
    	values.put(KEY_BEARING, location.getBearing());
    	
    	return mDb.insert(TABLE_COORD, null, values);
    }
   
}

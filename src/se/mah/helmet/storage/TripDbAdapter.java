package se.mah.helmet.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TripDbAdapter {
	public static final String DB_NAME = "helmet_db";
	private static final int DB_VERSION = 1;

	private static final String TAG = TripDbAdapter.class.getSimpleName();

	private final Context context;
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;

	public static final String TABLE_TRIP = "trips";
	public static final String KEY_ROWID = "_id";
	public static final String KEY_NAME = "name";
	static final String TABLE_TRIP_CREATE = "CREATE TABLE " + TABLE_TRIP
			+ "(" + KEY_ROWID + " integer primary key autoincrement, "
			+ KEY_NAME + " text not null," + ")";

	/**
	 * SQLiteOpenHelper for the AccDbAdapter
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(TABLE_TRIP_CREATE);
			Log.i(TAG, "Created database " + DB_NAME + ".");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data.");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIP);
			onCreate(db);
		}
	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx
	 *            the Context within which to work
	 */
	public TripDbAdapter(Context ctx) {
		this.context = ctx;
	}

	/**
	 * Open the database. If it cannot be opened, try to create a new instance
	 * of the database. If it cannot be created, throw SQLException.
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	public TripDbAdapter open() throws SQLException {
		dbHelper = new DatabaseHelper(context);
		db = dbHelper.getWritableDatabase();
		return this;
	}

	/**
	 * Close the database.
	 */
	public void close() {
		db.close();
	}

	public long insertTrip(String name) {
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, name);
		return db.insert(TABLE_TRIP, null, values);
	}

	/**
	 * Return a Cursor over the list of all trips in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchAllTripsCursor() {
		return db.query(TABLE_TRIP, new String[] { KEY_ROWID, KEY_NAME }, null, null, null, null, null);
	}
	
	public Cursor fetchTrip(Long rowId) {
		Cursor cursor = db.query(true, TABLE_TRIP, new String[] {
				KEY_ROWID, KEY_NAME }, KEY_ROWID + "=" + rowId,
				null, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		return cursor;
	}
}
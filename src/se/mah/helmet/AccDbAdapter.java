package se.mah.helmet;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Preliminary database adapter for storing accelerometer data in a SQLite
 * database.
 */
public class AccDbAdapter {
	// TODO Implement syncing features with server.

	private static final String DB_NAME = "helmet_db";
	private static final int DB_VERSION = 1;

	private static final String TAG = AccDbAdapter.class.getSimpleName();

	private final Context mCtx;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private static final String TABLE_ACC = "acc";
	private static final String KEY_ROWID = "_id";
	private static final String KEY_TIME = "time";
	private static final String KEY_ACCX = "acc_x";
	private static final String KEY_ACCY = "acc_y";
	private static final String KEY_ACCZ = "acc_z";
	private static final String TABLE_ACC_CREATE = 
			"CREATE TABLE " + TABLE_ACC	+ "(" 
			+ KEY_ROWID + " integer primary key autoincrement, "
			+ KEY_TIME + " text not null," 
			+ KEY_ACCX + " real," 
			+ KEY_ACCY + " real," 
			+ KEY_ACCZ + " real"
			+ ")";

	/**
	 * SQLiteOpenHelper for the AccDbAdapter
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(TABLE_ACC_CREATE);
			Log.i(TAG, "Created database " + DB_NAME + ".");

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data.");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACC);
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
	public AccDbAdapter(Context ctx) {
		this.mCtx = ctx;
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
	public AccDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	/**
	 * Close the database.
	 */
	public void close() {
		mDb.close();
	}

	/**
	 * Insert accelerometer data to database.
	 * 
	 * @param time
	 * @param accX
	 * @param accY
	 * @param accZ
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 *         (see android.database.sqlite.SQLiteDatabase.insert(...))
	 */
	public long insertData(String time, double accX, double accY,
			double accZ) {
		ContentValues values = new ContentValues(4);
		values.put(KEY_TIME, time);
		values.put(KEY_ACCX, accX);
		values.put(KEY_ACCY, accY);
		values.put(KEY_ACCZ, accZ);

		return mDb.insert(TABLE_ACC, null, values);
	}
}

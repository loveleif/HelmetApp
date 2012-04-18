package se.mah.helmet.storage;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

/**
 * Provides a common SQLiteOpenHelper for all database adapters using the
 * helmet_db database.
 * 
 */
public abstract class DbAdapter {
	private static final String TAG = AccDbAdapter.class.getSimpleName();

	private static final String DB_NAME = "helmet_db";
	private static final int DB_VERSION = 1;

	private final Context context;
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;

	/**
	 * SQLiteOpenHelper for helmet db
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(TripDbAdapter.TABLE_TRIP_CREATE);
			Log.i(TAG, "Created table " + DB_NAME + "."
					+ TripDbAdapter.TABLE_TRIP + ".");
			db.execSQL(ContactDbAdapter.TABLE_CONTACT_CREATE);
			Log.i(TAG, "Created database " + DB_NAME + "."
					+ ContactDbAdapter.TABLE_CONTACT + ".");
			db.execSQL(AccDbAdapter.TABLE_ACC_CREATE);
			Log.i(TAG, "Created database " + DB_NAME + "."
					+ AccDbAdapter.TABLE_ACC + ".");
			db.execSQL(LocDbAdapter.TABLE_LOC_CREATE);
			Log.i(TAG, "Created database " + DB_NAME + "."
					+ LocDbAdapter.TABLE_LOC + ".");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data.");
			db.execSQL("DROP TABLE IF EXISTS " + TripDbAdapter.TABLE_TRIP);
			db.execSQL("DROP TABLE IF EXISTS " + ContactDbAdapter.TABLE_CONTACT);
			db.execSQL("DROP TABLE IF EXISTS " + AccDbAdapter.TABLE_ACC);
			db.execSQL("DROP TABLE IF EXISTS " + LocDbAdapter.TABLE_LOC);
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
	public DbAdapter(Context context) {
		this.context = context;
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
	public DbAdapter open() throws SQLException {
		dbHelper = new DatabaseHelper(context);
		db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		db.close();
	}

	protected SQLiteDatabase getDb() {
		return db;
	}

	public abstract String getTableName();

	public abstract String getPrimaryKeyColumnName();

	/**
	 * Returns the last id from the database.
	 * 
	 * @return last location
	 */
	public long getLastId() {
		Cursor cursor = getDb().query(getTableName(), null,
				getPrimaryKeyColumnName(), null, null, null,
				getPrimaryKeyColumnName() + " desc", "1");
		cursor.moveToFirst();
		return cursor.getLong(0);
	}
}

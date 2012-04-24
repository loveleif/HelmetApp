package se.mah.helmet.storage;

import java.util.ArrayList;
import java.util.List;

import se.mah.helmet.entity.Contact;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Provides a common SQLiteOpenHelper and some common methods for all database 
 * adapters using the helmet_db database.
 * 
 */
public abstract class DbAdapter<T> {
	private static final String TAG = AccDbAdapter.class.getSimpleName();

	private static final String DB_NAME = "helmet_db";
	private static final int DB_VERSION = 3;

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
			db.execSQL("PRAGMA foreign_keys = ON;");
			
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
			db.execSQL(AlarmDbAdapter.TABLE_ALARM_CREATE);
			Log.i(TAG, "Created database " + DB_NAME + "."
					+ LocDbAdapter.TABLE_LOC + ".");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data.");
			db.execSQL("DROP TABLE IF EXISTS " + ContactDbAdapter.TABLE_CONTACT);
			db.execSQL("DROP TABLE IF EXISTS " + AccDbAdapter.TABLE_ACC);
			db.execSQL("DROP TABLE IF EXISTS " + LocDbAdapter.TABLE_LOC);
			db.execSQL("DROP TABLE IF EXISTS " + TripDbAdapter.TABLE_TRIP);
			db.execSQL("DROP TABLE IF EXISTS " + AlarmDbAdapter.TABLE_ALARM);
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

	/**
	 * Close database.
	 */
	public void close() {
		db.close();
	}

	/**
	 * Returns the instance of SQLiteDatabase
	 */
	protected SQLiteDatabase getDb() {
		return db;
	}

	/**
	 * Returns the name of this adapters table.
	 * 
	 * @return table name
	 */
	public abstract String getTableName();

	/**
	 * Returns the name of this adapters primary key column.
	 * 
	 * @return primary key column name
	 */
	public abstract String getPrimaryKeyColumnName();

	/**
	 * Returns the last id from the table.
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

	/**
	 * Returns object representation of the row that the specified Cursor points
	 * at. The Cursor must carry all columns.
	 * 
	 * @param cursor Cursor to extract object from
	 * @return extracted object
	 */
	public abstract T getObject(Cursor cursor);
	
	/**
	 * Extracts ContentValues from a given object.
	 * 
	 * @param object object to extract ContentValues from
	 * @return extracted ContentValues
	 */
	public abstract ContentValues getContentValues(T object);

	public T getObject(long id) {
		return getObject(get(id));
	}

	/**
	 * Returns a Cursor pointing at (not before!) the record with the specified row id.
	 *
	 * @param id record row id
	 * @return Cursor pointing at the record with the specified row id
	 */
	public Cursor get(long id) {
		Cursor cursor = getDb().query(true, getTableName(), null, null, null,
									  null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		
		return cursor;
	}

	/**
	 * Returns Cursor with all rows since the specified id. The Cursor carries
	 * all columns.
	 * 
	 * @param sinceThisId
	 *            row id
	 * @param limit
	 *            limit number of records
	 * @return all rows since the specified id
	 */
	public Cursor getAllSinceId(long sinceThisId, String limit) {
		return getDb().query(getTableName(), null,
				getPrimaryKeyColumnName() + ">" + sinceThisId, null, null,
				null, null, limit);
	}

	/**
	 * Returns a List of all objects in database.
	 * 
	 * @return all objects in database
	 */
	public List<T> getAllObjects() {
		Cursor cursor = getAll();
		List<T> list = new ArrayList<T>(cursor.getCount());
		while (cursor.moveToNext())
			list.add(getObject(cursor));
		return list;
	}

	/**
	 * Delete the row with the specified id. Returns true if a record was deleted.
	 * 
	 * @param id row id of row to delete
	 * @return true if a row was deleted
	 */
	public boolean delete(long id) {
		return getDb().delete(getTableName(),
				getPrimaryKeyColumnName() + "=" + id, null) > 0;
	}

	/**
	 * Returns Cursor with all rows. The Cursor carries all columns.
	 * 
	 * @return all rows
	 */
	public Cursor getAll() {
		return getDb()
				.query(getTableName(), null, null, null, null, null, null);
	}
	
	/**
	 * Insert the specified object to the database. Returns the row id of the inserted
	 * data
	 * 
	 * @param object object to insert
	 * @return row id of the inserted data
	 */
	public long insert(T object) {
		return getDb().insert(getTableName(), null, getContentValues(object));
	}
	
	/**
	 * Returns the last row of data in it's object form.
	 * 
	 * @return object representation of the last row
	 */
	public T getLastObject() {
		Cursor cursor = getDb().query(getTableName(), null,
				null, null, null, null,
				getPrimaryKeyColumnName() + " desc", "1");
		cursor.moveToFirst();
		return getObject(cursor);
	}
}

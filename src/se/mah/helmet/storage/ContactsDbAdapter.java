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

// TODO Ändra kommentarerna
public class ContactsDbAdapter {
	public static final String DB_NAME = "helmet_db";
	private static final int DB_VERSION = 1;

	private static final String TAG = ContactsDbAdapter.class.getSimpleName();

	private final Context mCtx;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	public static final String TABLE_CONTACTS = "contacts";
	public static final String KEY_ROWID = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_PHONE = "phone_nbr";
	static final String TABLE_ACC_CREATE = "CREATE TABLE " + TABLE_CONTACTS
			+ "(" + KEY_ROWID + " integer primary key autoincrement, "
			+ KEY_NAME + " text not null," + KEY_PHONE + " text" + ")";

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
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
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
	public ContactsDbAdapter(Context ctx) {
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
	public ContactsDbAdapter open() throws SQLException {
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

	public long insertContact(String name, String phoneNbr) {
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, name);
		values.put(KEY_PHONE, phoneNbr);

		return mDb.insert(TABLE_CONTACTS, null, values);
	}

	/**
	 * Return a Cursor over the list of all contacts in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchAllContactsCursor() {
		return mDb.query(TABLE_CONTACTS, new String[] { KEY_ROWID, KEY_NAME,
				KEY_PHONE }, null, null, null, null, null);
	}
	
	public List<Contact> fetchAllContacts() {
		List<Contact> contacts = new ArrayList<Contact>();
		Cursor cursor = fetchAllContactsCursor();
		int colIdxName = cursor.getColumnIndex(KEY_NAME);
		int colIdxPhone = cursor.getColumnIndex(KEY_PHONE);
		Log.d("-", "cursor size " + cursor.getCount());
		
		while (cursor.moveToNext()) {
			contacts.add(new Contact(
					cursor.getString(colIdxName),
					cursor.getString(colIdxPhone)));
		}
		return contacts;
	}

	public boolean deleteContact(long id) {
		return mDb.delete(TABLE_CONTACTS, KEY_ROWID + "=" + id, null) > 0;
	}

	public Cursor fetchContact(Long rowId) {
		Cursor mCursor = mDb.query(true, TABLE_CONTACTS, new String[] {
				KEY_ROWID, KEY_NAME, KEY_PHONE }, KEY_ROWID + "=" + rowId,
				null, null, null, null, null);
		if (mCursor != null)
			mCursor.moveToFirst();
		return mCursor;
	}

	public boolean updateContact(Long rowId, String name, String phoneNbr) {
		ContentValues args = new ContentValues();
		args.put(KEY_NAME, name);
		args.put(KEY_PHONE, phoneNbr);

		return mDb.update(TABLE_CONTACTS, args, KEY_ROWID + "=" + rowId, null) > 0;
	}
}

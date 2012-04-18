package se.mah.helmet.storage;

import java.util.ArrayList;
import java.util.List;

import se.mah.helmet.entity.Contact;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

// TODO Ändra kommentarerna
public class ContactDbAdapter extends DbAdapter {
	private static final String TAG = ContactDbAdapter.class.getSimpleName();

	// Remember to increment versionnumber in DbAdapter when you make changes
	public static final String TABLE_CONTACT = "contact";
	public static final String KEY_ROWID = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_PHONE = "phone_nbr";
	static final String TABLE_CONTACT_CREATE = "CREATE TABLE " + TABLE_CONTACT
			+ "(" + KEY_ROWID + " integer primary key autoincrement, "
			+ KEY_NAME + " text not null," + KEY_PHONE + " text" + ")";

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx
	 *            the Context within which to work
	 */
	public ContactDbAdapter(Context context) {
		super(context);
	}

	public long insertContact(String name, String phoneNbr) {
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, name);
		values.put(KEY_PHONE, phoneNbr);

		return getDb().insert(TABLE_CONTACT, null, values);
	}

	/**
	 * Return a Cursor over the list of all contacts in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchAllContactsCursor() {
		return getDb().query(TABLE_CONTACT,
				new String[] { KEY_ROWID, KEY_NAME, KEY_PHONE }, null, null,
				null, null, null);
	}

	public List<Contact> fetchAllContacts() {
		List<Contact> contacts = new ArrayList<Contact>();
		Cursor cursor = fetchAllContactsCursor();
		int colIdxName = cursor.getColumnIndex(KEY_NAME);
		int colIdxPhone = cursor.getColumnIndex(KEY_PHONE);

		while (cursor.moveToNext()) {
			contacts.add(new Contact(cursor.getString(colIdxName), cursor
					.getString(colIdxPhone)));
		}
		return contacts;
	}

	public boolean deleteContact(long id) {
		return getDb().delete(TABLE_CONTACT, KEY_ROWID + "=" + id, null) > 0;
	}

	public Cursor fetchContact(Long rowId) {
		Cursor mCursor = getDb().query(true, TABLE_CONTACT,
				new String[] { KEY_ROWID, KEY_NAME, KEY_PHONE },
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null)
			mCursor.moveToFirst();
		return mCursor;
	}

	public boolean updateContact(Long rowId, String name, String phoneNbr) {
		ContentValues args = new ContentValues();
		args.put(KEY_NAME, name);
		args.put(KEY_PHONE, phoneNbr);

		return getDb().update(TABLE_CONTACT, args, KEY_ROWID + "=" + rowId,
				null) > 0;
	}
}

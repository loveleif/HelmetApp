package se.mah.helmet.storage;

import java.util.ArrayList;
import java.util.List;

import se.mah.helmet.entity.Contact;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

// TODO Ändra kommentarerna
public class ContactDbAdapter extends DbAdapter<Contact> {
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

	public boolean updateContact(Long rowId, String name, String phoneNbr) {
		ContentValues args = new ContentValues();
		args.put(KEY_NAME, name);
		args.put(KEY_PHONE, phoneNbr);

		return getDb().update(TABLE_CONTACT, args, KEY_ROWID + "=" + rowId,
				null) > 0;
	}

	@Override
	public String getTableName() {
		return TABLE_CONTACT;
	}

	@Override
	public String getPrimaryKeyColumnName() {
		return KEY_ROWID;
	}

	@Override
	public Contact getObject(Cursor cursor) {
		return new Contact(
			cursor.getLong(cursor.getColumnIndex(KEY_ROWID)),
			cursor.getString(cursor.getColumnIndex(KEY_NAME)),
			cursor.getString(cursor.getColumnIndex(KEY_PHONE)));
	}
}

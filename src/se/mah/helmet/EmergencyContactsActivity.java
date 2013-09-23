package se.mah.helmet;

import se.mah.helmet.storage.ContactDbAdapter;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * Activity for adding, editing and deleting emergency contacts.
 *
 */
/**
 * @author toffe
 *
 */
public class EmergencyContactsActivity extends ListActivity {
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;

	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;

	private ContactDbAdapter dbHelper;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_list);
		dbHelper = new ContactDbAdapter(this);
		dbHelper.open();
		fillData();
		registerForContextMenu(getListView());
	}

	/**
	 * Fill the list of contacts.
	 */
	private void fillData() {
		Cursor cursor = dbHelper.getAll();
		startManagingCursor(cursor);

		// Create an array to specify the fields we want to display in the list
		// (only TITLE)
		String[] from = new String[] { ContactDbAdapter.KEY_NAME };

		// and an array of the fields we want to bind those fields to (in this
		// case just text1)
		int[] to = new int[] { R.id.txtvContactRow };

		// Now create a simple cursor adapter and set it to display
		SimpleCursorAdapter contacts = new SimpleCursorAdapter(this,
				R.layout.contacts_row, cursor, from, to);
		setListAdapter(contacts);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, R.string.contacts_menu_insert);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case INSERT_ID:
			createContact();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.contacts_menu_delete);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			dbHelper.delete(info.id);
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * Create contact.
	 */
	private void createContact() {
		Intent i = new Intent(this, ContactEdit.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, ContactEdit.class);
		i.putExtra(ContactDbAdapter.KEY_ROWID, id);
		startActivityForResult(i, ACTIVITY_EDIT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();
	}

}

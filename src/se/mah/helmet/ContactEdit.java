package se.mah.helmet;

import se.mah.helmet.entity.Contact;
import se.mah.helmet.storage.ContactDbAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Class for adding, editing and removing emergency contacts.
 */
/**
 * @author toffe
 *
 */
public class ContactEdit extends Activity {
	private ContactDbAdapter dbHelper;
	private EditText name;
	private EditText phoneNbr;
	private Long rowId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbHelper = new ContactDbAdapter(this);
		dbHelper.open();

		setContentView(R.layout.contact_edit);

		name = (EditText) findViewById(R.id.editContactName);
		phoneNbr = (EditText) findViewById(R.id.editContactPhoneNbr);

		// Get row id from saved state
		if (savedInstanceState == null)
			rowId = null;
		else
			rowId = (Long) savedInstanceState
					.getSerializable(ContactDbAdapter.KEY_ROWID);

		// Get row id from Intent
		if (rowId == null) {
			Bundle extras = getIntent().getExtras();
			if (extras != null)
				rowId = extras.getLong(ContactDbAdapter.KEY_ROWID);
		}

		
		populateFields();

		Button confirmButton = (Button) findViewById(R.id.btnConfirmContactEdit);
		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				setResult(RESULT_OK);
				finish();
			}
		});

	}

	/**
	 * Updates the GUI fields
	 */
	private void populateFields() {
		if (rowId == null)
			return;

		Contact contact = dbHelper.getObject(rowId);
		name.setText(contact.getName());
		phoneNbr.setText(contact.getPhoneNbr());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putSerializable(ContactDbAdapter.KEY_ROWID, rowId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	}

	/**
	 * Save state.
	 */
	private void saveState() {
		String name = this.name.getText().toString();
		String phoneNbr = this.phoneNbr.getText().toString();

		if (rowId == null) {
			long id = dbHelper.insertContact(name, phoneNbr);
			if (id > 0)
				rowId = id;
		} else {
			dbHelper.updateContact(rowId, name, phoneNbr);
		}
	}

}

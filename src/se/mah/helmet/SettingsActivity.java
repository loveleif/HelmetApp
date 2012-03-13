package se.mah.helmet;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import at.bartinger.list.item.EntryAdapter;
import at.bartinger.list.item.EntryItem;
import at.bartinger.list.item.Item;
import at.bartinger.list.item.SectionItem;

public class SettingsActivity extends ListActivity {
	ArrayList<Item> items = new ArrayList<Item>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		items.add(new SectionItem("Olyckslarm"));
		items.add(new EntryItem("Kvittering", "30 sekunder"));
		items.add(new EntryItem("Profil", "Ridning"));

		items.add(new SectionItem("Accelerometer"));
		items.add(new EntryItem("Samplingsfrekvens", "Sampla 1 värde/sek"));

		items.add(new SectionItem("Position"));
		items.add(new EntryItem("Min avstånd", "25 m"));
		items.add(new EntryItem("Min tid", "60 sekunder"));

		EntryAdapter adapter = new EntryAdapter(this, items);

		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		if (!items.get(position).isSection()) {

			EntryItem item = (EntryItem) items.get(position);

			Toast.makeText(this, "You clicked " + item.title,
					Toast.LENGTH_SHORT).show();

		}

		super.onListItemClick(l, v, position, id);
	}
}

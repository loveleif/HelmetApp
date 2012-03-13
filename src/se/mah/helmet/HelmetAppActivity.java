package se.mah.helmet;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class HelmetAppActivity extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, StartStopActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("Start").setIndicator("Start",
                          res.getDrawable(R.drawable.ic_tab_start))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tab
        intent = new Intent().setClass(this, SettingsActivity.class);
        spec = tabHost.newTabSpec("Settings").setIndicator("Settings",
                          res.getDrawable(R.drawable.ic_tab_settings))
                      .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(1);
        
    }
}
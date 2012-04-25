package se.mah.helmet;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Application settings.
 *
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
    }
}

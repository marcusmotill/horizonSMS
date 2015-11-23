package eventhorizon.horizonsms;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;

/**
 * Created by marcusmotill on 11/22/15.
 */
public class MyPreferenceActivity extends android.preference.PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("darkMode");
        checkBoxPreference.setChecked(sharedPreferences.getBoolean("darkMode", false));

        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                sharedPreferences.edit().putBoolean("darkMode", (boolean) newValue);
                sharedPreferences.edit().apply();
                return true;
            }
        });
    }

}

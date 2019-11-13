package com.alhikmahpro.www.e_inventory.View;


import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alhikmahpro.www.e_inventory.R;
import com.mocoo.hang.rtprinter.main.MainPrintSettings;

/**
 * A simple {@link Fragment} subclass.
 */
public class PrefSettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_main);

        Preference printerPref = findPreference(getString(R.string.key_printer));
        printerPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), MainPrintSettings.class);
                startActivity(intent);
                return true;
            }
        });
    }


}

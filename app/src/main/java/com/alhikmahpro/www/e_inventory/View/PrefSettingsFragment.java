package com.alhikmahpro.www.e_inventory.View;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.R;
import com.mocoo.hang.rtprinter.main.MainPrintSettings;

/**
 * A simple {@link Fragment} subclass.
 */
public class PrefSettingsFragment extends PreferenceFragment {

    public static String PREF_KEY_BASE_URL="key_server_url";
    public static String PREF_KEY_CNAME="key_company_name";
    public static String PREF_KEY_CCODE="key_company_code";
    public static String PREF_KEY_LCODE="key_location";

    public static String PREF_KEY_BCODE="key_branch_code";
    public static String PREF_KEY_PCODE="key_period";

    public static String PREF_KEY_EMPID="key_employee";
    public static String PREF_KEY_HEADER1="key_header_1";
    public static String PREF_KEY_HEADER2="key_header_2";
    public static String PREF_KEY_HEADER3="key_header_3";
    public static String PREF_KEY_FOOTER="key_footer";
    public static String PREF_KEY_GOODS="key_module_goods";
    public static String PREF_KEY_SALE="key_module_sales";
    public static String PREF_KEY_RECEIPT="key_module_receipts";
    public static String PREF_KEY_INVENTORY="key_module_inventory";

    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    private static final String TAG = "PrefSettingsFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_main);
        preferenceChangeListener=new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.d(TAG, "onSharedPreferenceChanged"+key);
                if (key.equals(PREF_KEY_BASE_URL)) {
                    Preference urlPref = findPreference(key);
                    urlPref.setSummary(sharedPreferences.getString(key, ""));
                } else if (key.equals(PREF_KEY_CNAME)) {
                    Preference cNamePref = findPreference(key);
                    cNamePref.setSummary(sharedPreferences.getString(key, ""));
                } else if (key.equals(PREF_KEY_CCODE)) {
                    Preference cCodePref = findPreference(key);
                    cCodePref.setSummary(sharedPreferences.getString(key, ""));
                } else if (key.equals(PREF_KEY_LCODE)) {
                    Preference locationPref = findPreference(key);
                    locationPref.setSummary(sharedPreferences.getString(key, ""));
                } else if (key.equals(PREF_KEY_BCODE)) {
                    Preference branchPref = findPreference(key);
                    branchPref.setSummary(sharedPreferences.getString(key, ""));
                } else if (key.equals(PREF_KEY_EMPID)) {
                    Preference empPref = findPreference(key);
                    empPref.setSummary(sharedPreferences.getString(key, ""));
                } else if (key.equals(PREF_KEY_PCODE)) {
                    Preference periodPref = findPreference(key);
                    periodPref.setSummary(sharedPreferences.getString(key, ""));
                } else if (key.equals(PREF_KEY_HEADER1)) {
                    Preference header1Pref = findPreference(key);
                    header1Pref.setSummary(sharedPreferences.getString(key, ""));
                } else if (key.equals(PREF_KEY_HEADER2)) {
                    Preference header2Pref = findPreference(key);
                    header2Pref.setSummary(sharedPreferences.getString(key, ""));
                } else if (key.equals(PREF_KEY_HEADER3)) {
                    Preference header3Pref = findPreference(key);
                    header3Pref.setSummary(sharedPreferences.getString(key, ""));
                } else if (key.equals(PREF_KEY_FOOTER)) {
                    Preference footerPref = findPreference(key);
                    footerPref.setSummary(sharedPreferences.getString(key, ""));
                }

            }

        };

//        final CheckBoxPreference inventoryPref = (CheckBoxPreference) getPreferenceManager().findPreference(PREF_KEY_INVENTORY);
//        final CheckBoxPreference salePref = (CheckBoxPreference) getPreferenceManager().findPreference(PREF_KEY_SALE);
//        final CheckBoxPreference goodsPref = (CheckBoxPreference) getPreferenceManager().findPreference(PREF_KEY_GOODS);
//        final CheckBoxPreference receiptPref = (CheckBoxPreference) getPreferenceManager().findPreference(PREF_KEY_RECEIPT);
//
//        inventoryPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            public boolean onPreferenceChange(Preference preference, Object newValue) {
//                Log.d(TAG, "Pref " + preference.getKey() + " changed to " + newValue.toString());
//                return true;
//            }
//        });
//        salePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            public boolean onPreferenceChange(Preference preference, Object newValue) {
//                Log.d(TAG, "Pref " + preference.getKey() + " changed to " + newValue.toString());
//                return true;
//            }
//        });
//        goodsPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            public boolean onPreferenceChange(Preference preference, Object newValue) {
//                Log.d(TAG, "Pref " + preference.getKey() + " changed to " + newValue.toString());
//
//                return true;
//            }
//        });
//        receiptPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            public boolean onPreferenceChange(Preference preference, Object newValue) {
//                Log.d(TAG, "Pref " + preference.getKey() + " changed to " + newValue.toString());
//                return true;
//            }
//        });

        Preference printerPref = findPreference(getString(R.string.key_printer));
        printerPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent_printer = new Intent(getActivity(), MainPrintSettings.class);
                startActivity(intent_printer);
                return true;
            }
        });






    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        Preference urlPref=findPreference(PREF_KEY_BASE_URL);
        urlPref.setSummary(getPreferenceScreen().getSharedPreferences().getString(PREF_KEY_BASE_URL,""));

        Preference cNamePref = findPreference(PREF_KEY_CNAME);
        cNamePref.setSummary(getPreferenceScreen().getSharedPreferences().getString(PREF_KEY_CNAME,""));

        Preference cCodePref = findPreference(PREF_KEY_CCODE);
        cCodePref.setSummary(getPreferenceScreen().getSharedPreferences().getString(PREF_KEY_CCODE,""));

        Preference locationPref = findPreference(PREF_KEY_LCODE);
        locationPref.setSummary(getPreferenceScreen().getSharedPreferences().getString(PREF_KEY_LCODE,""));

        Preference branchPref = findPreference(PREF_KEY_BCODE);
        branchPref.setSummary(getPreferenceScreen().getSharedPreferences().getString(PREF_KEY_BCODE,""));

        Preference periodPref = findPreference(PREF_KEY_PCODE);
        periodPref.setSummary(getPreferenceScreen().getSharedPreferences().getString(PREF_KEY_PCODE,""));

        Preference empPref = findPreference(PREF_KEY_EMPID);
        empPref.setSummary(getPreferenceScreen().getSharedPreferences().getString(PREF_KEY_EMPID,""));

        Preference header1Pref = findPreference(PREF_KEY_HEADER1);
        header1Pref.setSummary(getPreferenceScreen().getSharedPreferences().getString(PREF_KEY_HEADER1,""));

        Preference header2Pref = findPreference(PREF_KEY_HEADER2);
        header2Pref.setSummary(getPreferenceScreen().getSharedPreferences().getString(PREF_KEY_HEADER2,""));

        Preference header3Pref = findPreference(PREF_KEY_HEADER3);
        header3Pref.setSummary(getPreferenceScreen().getSharedPreferences().getString(PREF_KEY_HEADER3,""));

        Preference footerPref = findPreference(PREF_KEY_FOOTER);
        footerPref.setSummary(getPreferenceScreen().getSharedPreferences().getString(PREF_KEY_FOOTER,""));


    }


    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }
}

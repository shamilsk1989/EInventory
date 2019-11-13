package com.alhikmahpro.www.e_inventory.View;

import android.preference.Preference;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import com.alhikmahpro.www.e_inventory.R;

import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;

public class PrefSettingsActivity extends AppCompatActivity {

    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pref_settings);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        getSupportActionBar().setTitle("Settings");
        PrefSettingsFragment fragment=new PrefSettingsFragment();

        if(findViewById(R.id.fragment_container)!=null){
            if(savedInstanceState==null){
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PrefSettingsFragment())
                        .commit();
            }
        }


    }
}

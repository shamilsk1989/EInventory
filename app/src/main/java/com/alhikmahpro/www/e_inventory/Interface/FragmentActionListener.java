package com.alhikmahpro.www.e_inventory.Interface;

import android.os.Bundle;

public interface FragmentActionListener {

    int ACTION_VALUE_FRAGMENT_SELECTED = 0;
    String KEY_SELECTED_FRAGMENT="KEY_SELECTED_FRAGMENT";
    //tring CURRENT_FRAGMENT="current_fragment";
    void onNextInterface(Bundle bundle);
    void onBackInterface();
    void onScannerInterface();
    void onSetDrawerInterface(boolean set);
}

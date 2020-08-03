package com.alhikmahpro.www.e_inventory.View;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.SessionHandler;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.Interface.FragmentActionListener;
import com.alhikmahpro.www.e_inventory.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class SetupFragment extends Fragment {


    @BindView(R.id.txtCompanyCode)
    EditText txtCompanyCode;
    @BindView(R.id.txtCompanyName)
    EditText txtCompanyName;
    @BindView(R.id.txtBranchCode)
    EditText txtBranchCode;
    @BindView(R.id.txtHost)
    EditText txtHost;
    @BindView(R.id.btnSearch)
    Button btnSearch;
    Unbinder unbinder;
    @BindView(R.id.txtDeviceId)
    EditText txtDeviceId;
    private static final String TAG = "SetupFragment";


    FragmentActionListener fragmentActionListener;
    @BindView(R.id.txtLocationCode)
    EditText txtLocationCode;
    @BindView(R.id.txtPeriodCode)
    EditText txtPeriodCode;
    @BindView(R.id.txt_inventory)
    TextView txtInventory;
    @BindView(R.id.switch_inventory)
    Switch switchInventory;
    @BindView(R.id.txt_goods)
    TextView txtGoods;
    @BindView(R.id.switch_goods)
    Switch switchGoods;
    Toolbar toolbar;

    public SetupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setup, container, false);
        unbinder = ButterKnife.bind(this, view);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Setup");



        if(SessionHandler.getInstance(getContext()).isSetInventory()){
            switchInventory.setChecked(true);

        }


        switchInventory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(switchInventory.isChecked()){
                    Toast.makeText(getContext(), "inventory activated", Toast.LENGTH_SHORT).show();
                    SessionHandler.getInstance(getContext()).setInventory(true);

                }
                else {
                    SessionHandler.getInstance(getContext()).resetInventory();
                    Toast.makeText(getContext(), "inventory disabled", Toast.LENGTH_SHORT).show();
                }
            }
        });


        initValues();
        return view;
    }

    private void initValues() {

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnSearch)
    public void onViewClicked() {
        closeSoftKeyboard();

//tring CCode, String CCName,String BCode, String deviceI
        Log.d(TAG, "onSaveClicked: ");
        String c_name = txtCompanyName.getText().toString();
        String c_code = txtCompanyCode.getText().toString();
        String b_code = txtBranchCode.getText().toString();
        String l_code=  txtLocationCode.getText().toString();
        String p_code=  txtPeriodCode.getText().toString();
        String device = txtDeviceId.getText().toString();
        String ip = txtHost.getText().toString();

//        if (validate(c_code, ip)) {
//
//            helper.saveSettings(c_code, c_name, b_code,l_code,p_code,device);
//            SessionHandler.getInstance(getContext()).setHost(ip);
//            Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();
//            fragmentActionListener.onBackInterface();
//
//
//        }


    }


    private boolean validate(String code, String ip) {


        if (TextUtils.isEmpty(code)) {
            txtBranchCode.setError("Invalid name");
            return false;

        } else if (TextUtils.isEmpty(ip)) {
            txtHost.setError("Invalid ip");
            return false;

        }
        return true;

    }
    public void closeSoftKeyboard() {
        Log.d(TAG, "closeSoftKeyboard: ");
        View view = getActivity().getCurrentFocus();
        if (view!=null) {
            InputMethodManager imm = (InputMethodManager) getActivity(). getSystemService(Context.INPUT_METHOD_SERVICE);
            //imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity=(Activity)context;
        try {
            fragmentActionListener=(FragmentActionListener)activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString()+"Must override FragmentActionListener");
        }
    }
}

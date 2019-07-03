package com.alhikmahpro.www.e_inventory.View;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.Adapter.SupplierAdapter;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.SessionHandler;
import com.alhikmahpro.www.e_inventory.Data.SupplierModel;
import com.alhikmahpro.www.e_inventory.Interface.volleyListener;
import com.alhikmahpro.www.e_inventory.Network.VolleyServiceGateway;
import com.alhikmahpro.www.e_inventory.Network.VolleySingleton;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.Interface.OnAdapterClickListener;
import com.alhikmahpro.www.e_inventory.R;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListSupplierFragment extends DialogFragment implements SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.editTextSearch)
    EditText editTextSearch;
    @BindView(R.id.rvSupplierList)
    RecyclerView rvSupplierList;
    Unbinder unbinder;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;

    SwipeRefreshLayout mSwipeRefreshLayout;
    private static final String TAG = "ListSupplierFragment";
    String companyCode, companyName, deviceId, branchCode, periodCode, locationCode;
    private String BASE_URL = "";
    private ConnectivityManager connectivityManager;
    dbHelper helper;
    ArrayList<SupplierModel> supplierArrayList;
    SupplierAdapter adapter;
    // RecyclerView.Adapter adapter;
    volleyListener mVolleyListener;
    VolleyServiceGateway serviceGateway;
    public static final String DIALOG_FRAGMENT_TYPE="Supplier";

    public ListSupplierFragment() {
        // Required empty public constructor
    }

    public static interface OnCompleteListener {
        public abstract void onComplete(String time);
    }

    private OnCompleteListener mListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_supllier, container, false);
        unbinder = ButterKnife.bind(this, view);

        toolbar.setNavigationIcon(R.drawable.ic_close_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = getDialog();
                dialog.dismiss();
            }
        });
        toolbar.setTitle("Supplier List");
        initVolleyCallBack();
        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        helper = new dbHelper(getActivity());
        supplierArrayList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Cursor cursor = helper.getSettings(sqLiteDatabase);
        if (cursor.moveToFirst()) {
            companyCode = cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_COMPANY_CODE));
            companyName = cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_COMPANY_NAME));
            deviceId = cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_DEVICE_ID));
            branchCode = cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_BRANCH_CODE));
            periodCode = cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_PERIOD_CODE));
            locationCode = cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_LOCATION_CODE));
            BASE_URL = SessionHandler.getInstance(getContext()).getHost();
        }
        cursor.close();
        sqLiteDatabase.close();
        rvSupplierList.setHasFixedSize(true);
        rvSupplierList.setLayoutManager(new LinearLayoutManager(getContext()));
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark, android.R.color.holo_blue_dark);

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                getVolleyData();
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                adapter.getFilter().filter(s.toString());

            }
        });
        return view;
    }

    private void initVolleyCallBack() {

        mVolleyListener = new volleyListener() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {

                if (response.length() > 0) {
                    // in json array format
                    loadRecyclerView(response);

                } else {
                    Toast.makeText(getActivity(), "No data found !", Toast.LENGTH_SHORT).show();
                }
                mSwipeRefreshLayout.setRefreshing(false);


            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "Network Error ", Toast.LENGTH_SHORT).show();

            }
        };
    }

    private void loadRecyclerView(JSONObject response) {
        JSONArray jsonArray;
        try {
            jsonArray = response.getJSONArray("response");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String code = object.getString("SUBS_CODE");
                String name = object.getString("SUBS_NAME");
                supplierArrayList.add(new SupplierModel(code, name));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "onResponse: " + supplierArrayList.size());
        if(supplierArrayList.size()>0){

            adapter = new SupplierAdapter(supplierArrayList, new OnAdapterClickListener() {
                @Override
                public void OnItemClicked(int position) {
                    SupplierModel model = supplierArrayList.get(position);
                    String code = model.getSupplierCode();
                    Toast.makeText(getActivity(), "code is" + code, Toast.LENGTH_SHORT).show();
                    mListener.onComplete(code);
                    Dialog dialog = getDialog();
                    dialog.dismiss();
                }

                @Override
                public void OnDeleteClicked(int position) {

                }
            });
            rvSupplierList.setAdapter(adapter);
        }
        else {
            Toast.makeText(getActivity(), "Parsing Error..", Toast.LENGTH_SHORT).show();
        }

    }

    private void getVolleyData() {

        Toast.makeText(getActivity(), "loading RecyclerView", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "loadRecyclerView: ");
        mSwipeRefreshLayout.setRefreshing(true);
        supplierArrayList.clear();

        serviceGateway = new VolleyServiceGateway(mVolleyListener, getContext());
        serviceGateway.getDataVolley("POSTCALL", "PriceChecker/supplier_list.php");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onRefresh() {
        getVolleyData();

    }


    private void filter(String s) {
        ArrayList<SupplierModel> arrayList = new ArrayList<>();
        for (SupplierModel model : supplierArrayList) {
            if (model.getSupplierName().toLowerCase().contains(s.toLowerCase())) {
                arrayList.add(model);
            }
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        try {
            this.mListener = (OnCompleteListener) activity;
        } catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }
}

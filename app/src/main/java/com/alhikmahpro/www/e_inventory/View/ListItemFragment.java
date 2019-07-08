package com.alhikmahpro.www.e_inventory.View;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.alhikmahpro.www.e_inventory.Adapter.SelectionAdapter;
import com.alhikmahpro.www.e_inventory.AppUtils;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.SelectionModel;
import com.alhikmahpro.www.e_inventory.Data.SupplierModel;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.Interface.OnAdapterClickListener;
import com.alhikmahpro.www.e_inventory.Interface.volleyListener;
import com.alhikmahpro.www.e_inventory.Network.VolleyServiceGateway;
import com.alhikmahpro.www.e_inventory.R;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListItemFragment extends DialogFragment implements SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.editTextSearch)
    EditText editTextSearch;
    @BindView(R.id.rvCustomerList)
    RecyclerView rvCustomerList;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;

    SwipeRefreshLayout mSwipeRefreshLayout;
    Unbinder unbinder;


    dbHelper helper;
    ArrayList<SelectionModel> itemArrayList;
    SelectionAdapter adapter;
    // RecyclerView.Adapter adapter;
    volleyListener mVolleyListener;
    VolleyServiceGateway serviceGateway;
    String companyCode, companyName, deviceId, branchCode, periodCode, locationCode;
    String itemName;
    private static final String TAG = "ListItemFragment";
    public static final String DIALOG_FRAGMENT_TYPE="Items";
    public ListItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onRefresh() {
        loadRecyclerView();
    }

    public static interface OnCompleteListener {
        public abstract void onComplete(String time);
    }

    private ListItemFragment.OnCompleteListener mListener;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dialog, container, false);
        unbinder = ButterKnife.bind(this, view);
        toolbar.setNavigationIcon(R.drawable.ic_close_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = getDialog();
                dialog.dismiss();
            }
        });
        toolbar.setTitle("Choose Items..");

        Bundle bundle=getArguments();
        itemName=bundle.getString("ITEM_NAME").toUpperCase();


        helper = new dbHelper(getActivity());
        itemArrayList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Cursor cursor = helper.getSettings(sqLiteDatabase);
        if (cursor.moveToFirst()) {
            companyCode = cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_COMPANY_CODE));
            companyName = cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_COMPANY_NAME));
            deviceId = cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_DEVICE_ID));
            branchCode = cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_BRANCH_CODE));
            periodCode = cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_PERIOD_CODE));
            locationCode = cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_LOCATION_CODE));

        }
        cursor.close();
        sqLiteDatabase.close();
        rvCustomerList.setHasFixedSize(true);
        rvCustomerList.setLayoutManager(new LinearLayoutManager(getContext()));
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark, android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                loadRecyclerView();
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                adapter.getFilter().filter(s.toString());
            }
        });
        initVolleyCallBack();
        return view;


    }

    private void initVolleyCallBack() {
        mVolleyListener=new volleyListener() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                if(response.length()>0){

                    JSONArray jsonArray = null;
                    try {
                        jsonArray = response.getJSONArray("response");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object = jsonArray.getJSONObject(i);
                            String code = object.getString("PROD_CODE");
                            String name = object.getString("PROD_NAME");
                            String price=object.getString("SALE_PRICE00");
                            price=currencyFormatter(Double.valueOf(price));
                            itemArrayList.add(new SelectionModel(code, name,price));


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    adapter=new SelectionAdapter(itemArrayList, new OnAdapterClickListener() {
                        @Override
                        public void OnItemClicked(int position) {

                            SelectionModel model = itemArrayList.get(position);
                            String code = model.getCode();
                            Toast.makeText(getActivity(), "code is" + code, Toast.LENGTH_SHORT).show();
                            mListener.onComplete(code);
                            Dialog dialog = getDialog();
                            dialog.dismiss();
                        }

                        @Override
                        public void OnDeleteClicked(int position) {

                        }
                    });
                    if(itemArrayList.size()>0){
                        rvCustomerList.setAdapter(adapter);
                    }

                }else {
                    Toast.makeText(getActivity(), "No data found !", Toast.LENGTH_SHORT).show();
                }
                mSwipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "No data found ", Toast.LENGTH_SHORT).show();
            }
        };
    }
    public String currencyFormatter(double val){
        NumberFormat format=NumberFormat.getCurrencyInstance();
        String pattern=((DecimalFormat) format).toPattern();
        String newPattern=pattern.replace("\u00A4","").trim();
        NumberFormat newFormat=new DecimalFormat(newPattern);
        return String.valueOf(newFormat.format(val));

    }
    private void loadRecyclerView() {
        Log.d(TAG, "loadRecyclerView: "+itemName);
        if(!AppUtils.isNetworkAvailable(getContext())){
            Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
        }else {
            mSwipeRefreshLayout.setRefreshing(true);
            itemArrayList.clear();
            JSONObject postParam = new JSONObject();
            try {
                postParam.put("Code", itemName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            serviceGateway = new VolleyServiceGateway(mVolleyListener, getContext());
            serviceGateway.postDataVolley("POSTCALL", "PriceChecker/item_list.php", postParam);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    private void filter(String s) {
        ArrayList<SelectionModel> arrayList = new ArrayList<>();
        for (SelectionModel model : itemArrayList) {
            if (model.getName().toLowerCase().contains(s.toLowerCase())) {
                arrayList.add(model);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        try {
            this.mListener = (ListItemFragment.OnCompleteListener) activity;
        } catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }

}

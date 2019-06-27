package com.alhikmahpro.www.e_inventory.View;


import android.app.Activity;
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
import com.alhikmahpro.www.e_inventory.Adapter.SupplierAdapter;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.SelectionModel;
import com.alhikmahpro.www.e_inventory.Data.SupplierModel;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.Interface.volleyListener;
import com.alhikmahpro.www.e_inventory.Network.VolleyServiceGateway;
import com.alhikmahpro.www.e_inventory.R;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectionFragment extends DialogFragment implements SwipeRefreshLayout.OnRefreshListener{


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.editTextSearch)
    EditText editTextSearch;
    @BindView(R.id.rvCustomerList)
    RecyclerView rvSelectionList;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;
    Unbinder unbinder;
    SwipeRefreshLayout mSwipeRefreshLayout;

    dbHelper helper;
    ArrayList<SelectionModel> itemArrayList;
    SelectionAdapter adapter;
    volleyListener mVolleyListener;
    VolleyServiceGateway serviceGateway;
    String companyCode, companyName, deviceId, branchCode, periodCode, locationCode;
    String selectionType;
    private static final String TAG = "SelectionFragment";
    private SelectionFragment.onItemClickListener mListener;
    public SelectionFragment() {
        // Required empty public constructor
    }
    public interface onItemClickListener{
        public abstract void onItemClick(String item);

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
        Bundle args=getArguments();
        selectionType=args.getString("SEL_TYPE");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_selection, container, false);
        unbinder = ButterKnife.bind(this, view);

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
        rvSelectionList.setHasFixedSize(true);
        rvSelectionList.setLayoutManager(new LinearLayoutManager(getContext()));

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark, android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                loadRecycler();
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
            public void afterTextChanged(Editable editable) {
                adapter.getFilter().filter(editable.toString());
            }
        });

        initVolleyCallBack();
        return view;
    }

    private void initVolleyCallBack() {
        mVolleyListener=new volleyListener() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {

            }

            @Override
            public void notifyError(String requestType, VolleyError error) {

            }
        };
    }

    private void loadRecycler() {
        Toast.makeText(getActivity(), "loading RecyclerView", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "loadRecyclerView: ");
        mSwipeRefreshLayout.setRefreshing(true);
        itemArrayList.clear();

        serviceGateway = new VolleyServiceGateway(mVolleyListener, getContext());
        serviceGateway.getDataVolley("POSTCALL", "PriceChecker/item_list.php");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity=(Activity)context;
        try {
            mListener=(SelectionFragment.onItemClickListener)activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement onClickListener");
        }




    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onRefresh() {
        
    }
}

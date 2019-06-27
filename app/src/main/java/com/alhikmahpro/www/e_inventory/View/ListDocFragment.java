package com.alhikmahpro.www.e_inventory.View;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alhikmahpro.www.e_inventory.Adapter.DocAdapter;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.ItemModel;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.Interface.FragmentActionListener;
import com.alhikmahpro.www.e_inventory.Interface.OnAdapterClickListener;
import com.alhikmahpro.www.e_inventory.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListDocFragment extends Fragment {


    @BindView(R.id.scroll)
    NestedScrollView scroll;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    Unbinder unbinder;

    FragmentActionListener fragmentActionListener;
    @BindView(R.id.doc_list_rv)
    RecyclerView docListRv;

    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    private static final String TAG = "ListDocFragment";
    Toolbar toolbar;

    public ListDocFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_doc, container, false);
        unbinder = ButterKnife.bind(this, view);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Inventory");
        initView();
        return view;
    }

    private void initView() {
        dbHelper helper = new dbHelper(getContext());
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = helper.getStocks(database);
        Log.d(TAG, "initView: cursor size"+cursor.getCount());
        final List<ItemModel> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                ItemModel model = new ItemModel();
                Log.d(TAG, "initView: "+cursor.getInt(cursor.getColumnIndex(DataContract.Stocks.COL_DOCUMENT_NUMBER)));
                model.setDocNo(cursor.getInt(cursor.getColumnIndex(DataContract.Stocks.COL_DOCUMENT_NUMBER)));
                model.setStaffName(cursor.getString(cursor.getColumnIndex(DataContract.Stocks.COL_STAFF_NAME)));
                model.setTotal(cursor.getDouble(cursor.getColumnIndex(DataContract.Stocks.COL_TOTAL)));
                model.setDate(cursor.getString(cursor.getColumnIndex(DataContract.Stocks.COL_DATE_TIME)));
                list.add(model);
            } while (cursor.moveToNext());


        }

        if(list.size()>0){

            layoutManager = new LinearLayoutManager(getContext());
            docListRv.setLayoutManager(layoutManager);
            docListRv.setItemAnimator(new DefaultItemAnimator());
            docListRv.setHasFixedSize(true);
            adapter=new DocAdapter(list, new OnAdapterClickListener() {
                @Override
                public void OnItemClicked(int position) {
                    ItemModel itemModel=list.get(position);
                    int no=itemModel.getDocNo();
                    Bundle bundle=new Bundle();
                    bundle.putString(FragmentActionListener.KEY_SELECTED_FRAGMENT,"ItemFr");
                    bundle.putString("Action","Edit");
                    bundle.putInt("DocNo",no);
                    fragmentActionListener.onNextInterface(bundle);

                }

                @Override
                public void OnDeleteClicked(int position) {



                }
            });
            docListRv.setAdapter(adapter);
        }


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentActionListener.onSetDrawerInterface(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.fab)
    public void onViewClicked() {

        Bundle bundle = new Bundle();
        bundle.putString(FragmentActionListener.KEY_SELECTED_FRAGMENT, "InvFr");
        fragmentActionListener.onNextInterface(bundle);


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        try {
            fragmentActionListener = (FragmentActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "Must override FragmentActionListener");
        }
    }
}

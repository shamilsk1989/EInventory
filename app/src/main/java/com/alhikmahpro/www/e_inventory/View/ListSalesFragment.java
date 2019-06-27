package com.alhikmahpro.www.e_inventory.View;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alhikmahpro.www.e_inventory.Interface.FragmentActionListener;
import com.alhikmahpro.www.e_inventory.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListSalesFragment extends Fragment {


    @BindView(R.id.sales_list_rv)
    RecyclerView salesListRv;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    Toolbar toolbar;
    Unbinder unbinder;
    private static final String TAG = "ListSalesFragment";
    @BindView(R.id.salesTextView)
    TextView salesTextView;

    FragmentActionListener fragmentActionListener;

    public ListSalesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_sales, container, false);
        unbinder = ButterKnife.bind(this, view);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Sales");


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.fab)
    public void onViewClicked() {

        Bundle bundle = new Bundle();
        bundle.putString(FragmentActionListener.KEY_SELECTED_FRAGMENT, "New");
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

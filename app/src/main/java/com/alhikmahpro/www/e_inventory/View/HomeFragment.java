package com.alhikmahpro.www.e_inventory.View;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    @BindView(R.id.imgLogo)
    ImageView imgLogo;
    @BindView(R.id.txtCompanyName)
    TextView txtCompanyName;
    Unbinder unbinder;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
//
//
//        dbHelper helper=new dbHelper(getContext());
//        SQLiteDatabase database = helper.getReadableDatabase();
//        Cursor cursor = helper.getSettings(database);
//        if (cursor.moveToFirst()) {
//            txtCompanyName.setText(cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_COMPANY_NAME)));
//            byte[] img = cursor.getBlob(cursor.getColumnIndex(DataContract.Settings.COL_LOGO));
//            try {
//                Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
//                imgLogo.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 400, 400, false));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//        cursor.close();
//        database.close();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

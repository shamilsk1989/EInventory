package com.alhikmahpro.www.e_inventory.View;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.RuntimeData;
import com.alhikmahpro.www.e_inventory.Data.SessionHandler;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.Interface.FragmentActionListener;
import com.alhikmahpro.www.e_inventory.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import butterknife.BindView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentActionListener {
    Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    TextView navShopName, navUser;
    ActionBarDrawerToggle toggle;
    private static final String TAG = "HomeActivity";
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Bundle bundle;
    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private String scanData = "", user;
    Menu navMenu;
    MenuItem admin, inventory, goods, sale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        View headerView = navigationView.getHeaderView(0);
        navShopName = (TextView) headerView.findViewById(R.id.textShop);
        navUser = (TextView) headerView.findViewById(R.id.textUser);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        navMenu = navigationView.getMenu();
        admin = navMenu.findItem(R.id.nav_admin);
        inventory = navMenu.findItem(R.id.nav_inventory);
        goods = navMenu.findItem(R.id.nav_goods_receive);
        sale = navMenu.findItem(R.id.nav_sale);


        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            fragmentTransaction = fragmentManager.beginTransaction();
            HomeFragment homeFragment = new HomeFragment();
            homeFragment.setArguments(bundle);

//            ListSalesFragment salesFragment = new ListSalesFragment();
            fragmentTransaction.replace(R.id.fragment_container, homeFragment, null);
            fragmentTransaction.commit();
        }
        initView();
        hideMenu();
    }

    private void hideMenu() {
        Log.d(TAG, "hideMenu: " + SessionHandler.getInstance(this).isSetInventory());
        //hide sales menu
        //sale.setVisible(false);


        if (user.equals("User")) {
            admin.setVisible(false);

            if (!SessionHandler.getInstance(this).isSetInventory()) {
                Log.d(TAG, "Inventory false");
                inventory.setVisible(false);
            }
            if (!SessionHandler.getInstance(this).isSetGoodsReceive()) {
                Log.d(TAG, "goods false");
                goods.setVisible(false);
            }
        }
    }


    private void initView() {

        dbHelper helper = new dbHelper(this);
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = helper.getSettings(database);
        if (cursor.moveToFirst()) {
            navShopName.setText(cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_COMPANY_NAME)));
        }
        user = SessionHandler.getInstance(HomeActivity.this).getUser();
        navUser.setText(user);
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            int index = fragmentManager.getBackStackEntryCount();

            if (index > 2) {
                clearStack();
            }

            super.onBackPressed();
        }

    }

    private void clearStack() {

        RuntimeData.mCartData.clear();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


        switch (menuItem.getItemId()) {

            case R.id.nav_check:


//                fragmentTransaction = fragmentManager.beginTransaction();
//                CheckerFragment fr = new CheckerFragment();
//                fr.setArguments(bundle);
//                fragmentTransaction.replace(R.id.fragment_container, fr, null);
//                fragmentTransaction.commit();

                Intent checker = new Intent(HomeActivity.this, PriceCheckerActivity.class);
                startActivity(checker);
                break;
            case R.id.nav_sale:
//
                //  Intent intent_sale = new Intent(this, ListDocActivity.class);
                Intent intent_sale = new Intent(this, ListSalesActivity.class);
                intent_sale.putExtra("Type", "SAL");
                startActivity(intent_sale);
                break;


//                fragmentTransaction = fragmentManager.beginTransaction();
//                ListSalesFragment salesFragment = new ListSalesFragment();
//                fragmentTransaction.replace(R.id.fragment_container, salesFragment, null);
//                fragmentTransaction.commit();


            case R.id.nav_inventory:
                Intent intent_inv = new Intent(this, ListDocActivity.class);
                intent_inv.putExtra("Type", "INV");
                startActivity(intent_inv);

                //startActivity(new Intent(HomeActivity.this, ListDocActivity.class));
                break;
            case R.id.nav_goods_receive:
                Intent intent_gds = new Intent(this, ListDocActivity.class);
                intent_gds.putExtra("Type", "GDS");
                startActivity(intent_gds);
                //startActivity(new Intent(HomeActivity.this, InvoiceActivity.class));
                break;
            case R.id.nav_logout:
                logout();
                break;

            case R.id.nav_setup:
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
                break;
            case R.id.nav_print:
                startActivity(new Intent(HomeActivity.this, PrinterSettingsActivity.class));
                break;
            default:
//                fragment = new CheckerFragment();
//                bundle.putString("ScanData",scanData);
                break;
        }
//        if (fragment != null) {
//                        fragment.setArguments(bundle);
//                        fragmentTransaction = fragmentManager.beginTransaction();
//                        fragmentTransaction.replace(R.id.fragment_container, fragment, null).addToBackStack(null).commit();
//                        fragmentTransaction.commit();
//        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        SessionHandler.getInstance(HomeActivity.this).resetUser();
        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onNextInterface(Bundle bundle) {


//        String targetFr = bundle.getString(FragmentActionListener.KEY_SELECTED_FRAGMENT);
//        //Fragment nextFragment = null;
//        switch (targetFr) {
//
//            case "New":
//                startActivity(new Intent(HomeActivity.this, CheckCustomerActivity.class));
//                break;
//            case "Edit":
//                break;
//                default:
//                    break;


//            case "InvFr":
//                nextFragment = new InventoryFragment();
//                break;
//            case "DocFr":
//                nextFragment = new ListDocFragment();
//                break;
//            case "ItemFr":
//                nextFragment = new ListItemFragment();
//                break;
//
//            default:
//                nextFragment = new CheckerFragment();
//                break;

        //  }

//        Fragment nextFragment=fragmentManager.findFragmentById(R.id.fragment_container);
//        if(nextFragment instanceof ListDocFragment){
//            Log.d(TAG, "Current fragment is ListDoc ");
//            nextFragment=new InventoryFragment();
//        }else if(nextFragment instanceof InventoryFragment ){
//            nextFragment=new ListItemFragment();
//
//        }
//        nextFragment.setArguments(bundle);
//        fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.fragment_container, nextFragment, null).addToBackStack(null).commit();


    }

    @Override
    public void onBackInterface() {

        Log.d(TAG, "onBackInterface: ");
        clearStack();


    }

    @Override
    public void onScannerInterface() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission already granted: ");

                scanBarcode();
            } else {
                requestStoragePermission();
            }
        } else {
            scanBarcode();
        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, Manifest.permission.CAMERA)) {

            new AlertDialog.Builder(HomeActivity.this)
                    .setTitle("Permission needed")
                    .setMessage("To continue please allow the permission ")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).create().show();


        } else {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    private void scanBarcode() {

        Log.d(TAG, "onScannerPressed: ");
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setCameraId(0);
        intentIntegrator.setPrompt("Scan code");
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setCaptureActivity(CaptureActivity.class);
        intentIntegrator.initiateScan();
    }

    @Override
    public void onSetDrawerInterface(boolean set) {

//        if(!set){
//            drawer.setDrawerLockMode(drawer.LOCK_MODE_LOCKED_CLOSED);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: permission granted ");
                scanBarcode();
            } else {
                Log.d(TAG, "onRequestPermissionsResult: permission not granted");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {


        } else {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }

    }


}

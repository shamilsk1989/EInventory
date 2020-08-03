package com.alhikmahpro.www.e_inventory.View

import android.database.Cursor
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.alhikmahpro.www.e_inventory.Data.AutoIdModel
import com.alhikmahpro.www.e_inventory.Data.ClearData
import com.alhikmahpro.www.e_inventory.Data.DataContract
import com.alhikmahpro.www.e_inventory.Data.dbHelper
import com.alhikmahpro.www.e_inventory.R
import kotlinx.android.synthetic.main.activity_auto_id_settings.*
import java.util.ArrayList

class AutoIdSettingsActivity : AppCompatActivity() {
    internal lateinit var db:dbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_id_settings)



        db= dbHelper(this)
        loadData()
        btnSave.setOnClickListener {

            when {
                TextUtils.isEmpty(editTextInventory.text) -> editTextInventory.error = "Invalid number"
                TextUtils.isEmpty(editTextGoods.text) -> editTextInventory.setError("Invalid Number")
                TextUtils.isEmpty(editTextSales.text) -> editTextSales.setError("Invalid Number")
                TextUtils.isEmpty(editTextOrder.text) -> editTextOrder.setError("Invalid Number")
                else -> {
                    if(db.deleteAllAutoId()){
                        var id=db.saveAutoGeneratorId(editTextSales.text.toString().toInt(),
                                editTextOrder.text.toString().toInt(),
                                editTextGoods.text.toString().toInt(),
                                editTextInventory.text.toString().toInt())
                        if(id>0){
                            Toast.makeText(this,"Saved ",Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

    }

    fun clearData(){
        editTextSales?.text?.clear()
        editTextGoods?.text?.clear()
        editTextOrder?.text?.clear()
        editTextInventory?.text?.clear()
    }

    fun loadData(){

        var database=db.readableDatabase

        var cursor=db.getAllAutoId(database)
        if(cursor.moveToFirst()){
            var invId= cursor.getInt(cursor.getColumnIndex(DataContract.AutoIdGenerator.COL_INVOICE_TABLE))
            var ordId= cursor.getInt(cursor.getColumnIndex(DataContract.AutoIdGenerator.COL_ORDER_TABLE))
            var stkId= cursor.getInt(cursor.getColumnIndex(DataContract.AutoIdGenerator.COL_STOCK_TABLE))
            var gdsId= cursor.getInt(cursor.getColumnIndex(DataContract.AutoIdGenerator.COL_GOODS_TABLE))

            editTextInventory.setText(stkId.toString())
            editTextOrder.setText(ordId.toString())
            editTextSales.setText(invId.toString())
            editTextGoods.setText(gdsId.toString())

        }
        cursor.close()
        database.close()
    }




}

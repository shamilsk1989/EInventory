

package com.alhikmahpro.www.e_inventory.App
import android.content.Context
import android.content.res.Configuration
import android.support.multidex.MultiDexApplication
import android.util.Log
import com.mocoo.hang.rtprinter.main.RTApplication


class AppController : RTApplication() {

    private val TAG = javaClass.simpleName



    override fun onCreate() {
        super.onCreate()
        instance = this
//        Instabug.Builder(this, "98278477215a165451d7447b684e644c")
//                .setInvocationEvents(InstabugInvocationEvent.SHAKE, InstabugInvocationEvent.SCREENSHOT)
//                .build()
    }

    // This is used to prevent language reset on configuration change


    override fun attachBaseContext(base: Context?) {
       // var lang=MCPreferences.getLanguageCode(base!!)
      //  Log.d("language",lang)
        super.attachBaseContext(base)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        //var lang= MCPreferences.getLanguageCode(this)
       //Log.d("language",lang)
       // MCUtils.setAppLanguage(this,lang)
    }


    companion object {
        @get:Synchronized
        var instance: AppController? = null


    }

}


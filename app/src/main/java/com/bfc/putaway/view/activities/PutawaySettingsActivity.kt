package com.bfc.putaway.view.activities

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.bfc.putaway.R
import com.bfc.putaway.util.DEVICE_ID
import com.bfc.putaway.util.Methods.openAlertDialog
import com.bfc.putaway.util.Methods.setStringPreference
import com.bfc.putaway.util.Methods.writeToXml
import com.bfc.putaway.util.baseUrl
import java.io.File


class PutawaySettingsActivity : AppCompatActivity() {

    private var iv_back: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        iv_back = findViewById(R.id.iv_back)

        iv_back?.setOnClickListener(View.OnClickListener {

            var prefs = PreferenceManager.getDefaultSharedPreferences(this)
            Log.d(
                com.bfc.putaway.util.TAG,
                " Settings changed to IP/Port/Distance " + prefs.getString("ip", "") + "/ " + prefs.getString(
                    "port",
                    ""
                ) + "/ " + prefs.getString("distance", "")
            )
//            DEVICE_ID = prefs.getString("deviceId","").toString()
            saveUrl(prefs.getString("url", "").toString(), prefs.getString("deviceId","").toString())

        })

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }

    fun saveUrl(api_url: String, deviceId: String) {
        if (api_url.equals("")) {
            openAlertDialog("Please Fill URL and Port.",this)
        } else {

//            val data = api_timeout?.text.toString()
//            if(!data.isNullOrEmpty() ){
//                apiTIMEOUT = data.toLong()
//            }

            if (!deviceId.isNullOrEmpty()){
                DEVICE_ID = deviceId
            }

            val url = api_url
            baseUrl = url
            setStringPreference("api_url", url,this)
            val baseDir = Environment.getExternalStorageDirectory().absolutePath
            val pathDir =
                "$baseDir/Android/data/com.bfcassociates.putaway/appConfig"
            val fileName = "config.xml"
            val mydir = File(pathDir)
            var myInternalFile = File(mydir, fileName)
            writeToXml(myInternalFile)

            openAlertDialog("URL Updated Successfully.",this)
            val intent = Intent(this, PutAwayLoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
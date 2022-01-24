package com.bfc.putaway.view.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.bfc.putaway.R
import com.bfc.putaway.util.*
import com.bfc.putaway.util.Methods.openAlertDialog
import com.bfc.putaway.util.Methods.writeToXml
import kotlinx.android.synthetic.main.activity_admin_api_control.*
import java.io.File

class PutAwayAdminApiControlActivity : AppCompatActivity()
//    EMDKScannerWithDoubleClick.DoubleClickScannerListener,
//    EMDKScannerWithDoubleClick.ScannedBarcodeListener,
//    EMDKScannerWithDoubleClick.ConnectionScannerListener
{
    private var mPrefs: SharedPreferences? = null

    var local_api_url: String = ""
    var local_deviceId: String = ""
    lateinit var data: Array<Any>
    private var inputPassword: Boolean = true
    var login_btn: AppCompatButton? = null
    var url_password: EditText? = null

    var save_btn: AppCompatButton? = null
    var port_url: EditText? = null
    var api_url: EditText? = null
    var iv_logo: ImageView? = null

    var api_timeout: EditText? = null
    var deviceId: EditText? = null
    var adminLogin: Boolean = true
//    var EMDKWrapper: EMDKScannerWithDoubleClick? = null
    var rfid_reader_ip: EditText? = null
    var rfid_reader_port: EditText? = null
    var distance: EditText? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_api_control)
//        if (Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.contains("Motorola Solutions")) {
//            EMDKWrapper = EMDKScannerWithDoubleClick()
//        }
        initUi()
        mPrefs = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        local_api_url = getStringPrefernce("api_url")
        local_deviceId = getStringPrefernce("deviceId")
        data = local_api_url.split(":").toTypedArray()
//        Log.d("xx", "data is:- " + data)

    }

    private fun initUi() {

        api_url = findViewById<EditText>(R.id.api_url)
        url_password = findViewById<EditText>(R.id.url_password)
        save_btn = findViewById<AppCompatButton>(R.id.save_btn)
        login_btn = findViewById<AppCompatButton>(R.id.login_url_btn)
        port_url = findViewById<EditText>(R.id.port_url)
        api_timeout = findViewById<EditText>(R.id.api_timeout)
        iv_logo = findViewById<ImageView>(R.id.iv_logo)
        deviceId = findViewById<EditText>(R.id.deviceId)
        rfid_reader_ip = findViewById<EditText>(R.id.rfid_reader_ip)
        rfid_reader_port = findViewById<EditText>(R.id.rfid_reader_port)
        distance = findViewById<EditText>(R.id.distance)

        setFieldData()


        url_password?.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_UP -> {
//                        Log.d("xx", "Hello therennn")
                        val DRAWABLE_RIGHT: Int = 2
                        if (event.rawX >= (url_password!!.right - url_password!!.compoundDrawables[DRAWABLE_RIGHT].bounds
                                .width())
                        ) {
                            // your action here
//                            Log.d("xx", "Hello Sir")
                            inputPassword = !inputPassword
                            if (inputPassword) {
                                url_password?.inputType =
                                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                                url_password?.setCompoundDrawablesWithIntrinsicBounds(
                                    null,
                                    null,
                                    ContextCompat.getDrawable(
                                        applicationContext,
                                        R.drawable.ic_show_password
                                    ),
                                    null
                                )
                            } else {
                                url_password?.inputType = InputType.TYPE_CLASS_TEXT
                                url_password?.setCompoundDrawablesWithIntrinsicBounds(
                                    null,
                                    null,
                                    ContextCompat.getDrawable(
                                        applicationContext,
                                        R.drawable.ic_hide_password
                                    ),
                                    null
                                )
                            }
                            return true
                        }
                    }//Do Something
                }
                return v?.onTouchEvent(event) ?: true
            }
        })

        login_btn?.setOnClickListener {
            loginUser()
        }

        save_btn?.setOnClickListener {
            saveUrl()
        }

    }


    fun loginUser() {
        if (url_password?.text.toString().equals("") || url_password?.text == null) {
            openAlertDialog("Enter Password.",this)
        } else {
            if (url_password?.text.toString().equals("PutAwayURL123!")) {

//                val mIntent: Intent = Intent(this, SettingsActivity::class.java)
//                startActivity(mIntent)
//                finish()
                save_btn?.visibility = View.VISIBLE
                login_btn?.visibility = View.GONE

                url_password?.visibility = View.GONE
                iv_logo?.visibility = View.GONE
                api_url?.visibility = View.VISIBLE
                port_url?.visibility = View.VISIBLE
                api_timeout?.visibility = View.VISIBLE
                deviceId?.visibility = View.VISIBLE
                rfid_reader_ip?.visibility = View.VISIBLE
                rfid_reader_port?.visibility = View.VISIBLE
                distance?.visibility = View.VISIBLE


                api_url?.setText(data[0].toString() + ":" + data[1].toString())
                port_url?.setText(data[2].toString().dropLast(1))
                api_timeout?.setText("" + apiTIMEOUT)
                adminLogin = false
                deviceId?.setText("$DEVICE_ID")

            } else {
                url_password?.setText("")
                openAlertDialog("Wrong Password.",this)
            }
        }
    }

    fun setStringPrefernce(key: String, value: String) {
        getmPrefs()!!.edit().putString(key, value).apply()
    }

    fun getmPrefs(): SharedPreferences? {
        return mPrefs
    }

    fun getStringPrefernce(key: String): String {
        return getmPrefs()!!.getString(key, "").toString()
    }

    fun saveUrl() {
        if (api_url?.text.toString().equals("") || port_url?.text.toString().equals("")) {
            openAlertDialog("Please Fill URL and Port.",this)
        } else {

            val data = api_timeout?.text.toString()
            if(!data.isNullOrEmpty() ){
                apiTIMEOUT = data.toLong()
            }

            val deviceId = deviceId?.text.toString()
            if (!deviceId.isNullOrEmpty()){
                DEVICE_ID = deviceId
            }

            val url = api_url?.text.toString() + ":" + port_url?.text.toString() + "/"
            setStringPrefernce("api_url", url)
            setStringPrefernce("ip",rfid_reader_ip?.text.toString())
            setStringPrefernce("port",rfid_reader_port?.text.toString())
            setStringPrefernce("distance",distance?.text.toString())
            baseUrl = url
            val baseDir = Environment.getExternalStorageDirectory().absolutePath
            val pathDir =
                "$baseDir/Android/data/com.bfcassociates.putaway/appConfig"
            val fileName = "config.xml"
            val mydir = File(pathDir)
            var myInternalFile = File(mydir, fileName)
            writeToXml(myInternalFile)

            openAlertDialog("URL Updated Successfully.",this)

        }
    }
    override fun onResume() {
//        if (EMDKWrapper != null) {
//            EMDKWrapper?.onCreate(this)
//            EMDKWrapper?.setDoubleClickOffset(600)
//            EMDKWrapper?.setDoubleClickScannerListener(this)
//            EMDKWrapper?.setScannedBarcodeListener(this)
//            EMDKWrapper?.setConnectionScannerListener(this)
//            EMDKWrapper?.startScanning()
//        }
        super.onResume()
    }

    override fun onPause() {
//        if (EMDKWrapper != null) {
//            EMDKWrapper?.releaseDoubleClickScannerListener()
//            EMDKWrapper?.releaseScannedBarcodeListener()
//            EMDKWrapper?.releaseConnectionScannerListener()
//            EMDKWrapper?.onClosed()
//        }
        super.onPause()
    }

    override fun onStop() {
//        if (EMDKWrapper != null) {
//            EMDKWrapper?.releaseDoubleClickScannerListener()
//            EMDKWrapper?.releaseScannedBarcodeListener()
//            EMDKWrapper?.releaseConnectionScannerListener()
//            EMDKWrapper?.onClosed()
//        }
        super.onStop()
    }

    override fun onDestroy() {
//        if (EMDKWrapper != null) {
//            EMDKWrapper?.releaseDoubleClickScannerListener()
//            EMDKWrapper?.releaseScannedBarcodeListener()
//            EMDKWrapper?.releaseConnectionScannerListener()
//            EMDKWrapper?.onClosed()
//        }
        super.onDestroy()
    }
//    override fun onScannerDoubleClick() {
//        Log.d("xx", "Double click Called")
//    }
//
//    override fun onScannedBarcode(barcode: String?, barcodeType: String?) {
//        Log.d("xx", "barcode scanned: " + barcode)
//        if (barcode != null) {
//            runOnUiThread {
//                seScanValues(barcode)
//            }
//        }
//    }
//
//    override fun onScannerConnection(status: String?) {
//        if (status.equals("CONNECTED")) {
//            Log.d("xx", "Scanned CONNECTED!")
//            if (Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.contains("Motorola Solutions")) {
//                EMDKWrapper = EMDKScannerWithDoubleClick()
//            }
//            onResume()
//        } else if (status.equals("DISCONNECTED")) {
//            Log.d("xx", "Scanned DISCONNECTED!")
//        }
//    }

    fun seScanValues(decodedData: String) {
        if (adminLogin) {
            url_password?.setText(decodedData)
            loginUser()
        } else {
            Log.d("xx", "inside split method. ")
            val data = decodedData.split("~").toTypedArray()
            var url: String = ""
            var port: String = ""
            try {
                if (data.size > 1) {
                    url = data[0]
                    port = data[1]
                }
                Log.d("xx", "barcode url: " + url + " Port: " + port)
                api_url?.setText(url)
                port_url?.setText(port)
                api_timeout?.setText("" + apiTIMEOUT)
                saveUrl()

            } catch (e: IndexOutOfBoundsException) {
                openAlertDialog("Something Wrong with Username and Password!",this)
            }
        }
    }

    private fun setFieldData() {
        if (!Methods.getStringPreference("ip",this).equals("",true))
        rfid_reader_ip?.setText(Methods.getStringPreference("ip",this))
        if (!Methods.getStringPreference("port",this).equals("",true))
        rfid_reader_port?.setText(Methods.getStringPreference("port",this))
        if (!Methods.getStringPreference("distance",this).equals("",true))
        distance?.setText(Methods.getStringPreference("distance",this))

    }




}
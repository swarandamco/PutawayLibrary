package com.bfc.putaway.view.activities

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.bfc.putaway.R
import com.bfc.putaway.apirequests.PutawayRestServiceHandler
import com.bfc.putaway.databinding.ActivityPutawayLoginBinding
import com.bfc.putaway.models.AppData
import com.bfc.putaway.util.*
import com.bfc.putaway.util.Methods.getDate
import com.bfc.putaway.util.Methods.writeLogsInFile
import com.bfc.putaway.util.Methods.writeToXml
import com.bfc.putaway.util.ResponseMapper.errorHandler
import com.bfcassociates.rfselection.models.response.PutawayOneModelRes
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import java.io.FileInputStream
import java.util.*
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PutAwayLoginActivity : AppCompatActivity(), TextToSpeech.OnInitListener
//    EMDKScannerWithDoubleClick.DoubleClickScannerListener,
//    EMDKScannerWithDoubleClick.ScannedBarcodeListener,
//    EMDKScannerWithDoubleClick.ConnectionScannerListener
{

    //    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var loadingDialog: AlertDialog? = null
    private var mPrefs: SharedPreferences? = null

    //    val appApiServe by lazy {
//        RestServiceHandler.create()
//    }

    private var touchCount: Int = 0


    private val appApiServe by lazy {
        PutawayRestServiceHandler.create()
    }

    var disposable: Disposable? = null
    var user: String = ""

    //    var mLIbName: String = ""
    private var tts: TextToSpeech? = null
    private var inputPassword: Boolean = true
    val isConnectedToInternet: Boolean
        get() {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork != null
        }

    private lateinit var binding: ActivityPutawayLoginBinding
//    var EMDKWrapper: EMDKScannerWithDoubleClick? = null
    var DOUBLE_CLICK_ENABLE_LOGIN: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        if (Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.contains("Motorola Solutions")) {
//            EMDKWrapper = EMDKScannerWithDoubleClick()
//        }

        binding = ActivityPutawayLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        setContentView(R.layout.activity_login)
        // Obtain the FirebaseAnalytics instance.
//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mPrefs = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        Log.d("xx", "Device Name is " + Build.MODEL)
        initUI()
    }

    private fun loginButtonClick() {
        val mUserName: String? = binding.loginUserId.text.toString().toUpperCase()
        val mPAssword: String? = binding.loginPassword.text.toString()
        if (!mUserName.isNullOrEmpty() && !mPAssword.isNullOrEmpty()) {
            loginUser(mUserName, mPAssword)
        } else {
            Toast.makeText(this, "Please Fill Username & Password.", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun initUI() {
        binding.ivLogo.setOnClickListener {
            touchCount = touchCount + 1
            Log.d("xx", "Count is:- " + touchCount)
            if (touchCount == 1) {
                checkInTimeAction()
            }
        }


        binding.loginBtn.setOnClickListener {
            Methods.avoidDoubleClicks(binding.loginBtn)
            loginButtonClick();
        }

        // fab settings button
//        val btn_fabSettings: FloatingActionButton =
//            findViewById<FloatingActionButton>(R.id.btn_fabSettings)
//        btn_fabSettings.setOnClickListener(View.OnClickListener {
//            Methods.avoidDoubleClicks(btn_fabSettings)
//            val mIntent: Intent = Intent(this, SettingsActivity::class.java)
//            startActivity(mIntent)
//        })


        if (checkAndRequestPermissions()) {
            val myApp = MyPersonalApp()
            myApp.writeLogsToFile()
            readFileFromDownloads()
        }


        binding.loginPassword.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_UP -> {
//                        Log.d("xx", "Hello therennn")
                        val DRAWABLE_LEFT: Int = 0;
                        val DRAWABLE_TOP: Int = 1;
                        val DRAWABLE_RIGHT: Int = 2;
                        val DRAWABLE_BOTTOM: Int = 3;
                        if (event.getRawX() >= (binding.loginPassword.getRight() - binding.loginPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds()
                                .width())
                        ) {
                            // your action here
//                            Log.d("xx", "Hello Sir")
                            inputPassword = !inputPassword
                            if (inputPassword) {
                                binding.loginPassword.inputType =
                                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                                binding.loginPassword.setCompoundDrawablesWithIntrinsicBounds(
                                    null,
                                    null,
                                    ContextCompat.getDrawable(
                                        applicationContext,
                                        R.drawable.ic_show_password
                                    ),
                                    null
                                )
                            } else {
                                binding.loginPassword.inputType = InputType.TYPE_CLASS_TEXT
                                binding.loginPassword.setCompoundDrawablesWithIntrinsicBounds(
                                    null,
                                    null,
                                    ContextCompat.getDrawable(
                                        applicationContext,
                                        R.drawable.ic_hide_password
                                    ),
                                    null
                                )
                            }
                            return true;
                        }
                    }//Do Something
                }
                return v?.onTouchEvent(event) ?: true
            }
        })

    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result = tts!!.setLanguage(Locale.US)
            tts!!.setPitch(1.3f)
            tts!!.setSpeechRate(1f)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language specified is not supported!")
            } else {
                //                buttonSpeak!!.isEnabled = true
            }
        } else {
            Log.e("TTS", "Initilization Failed!")
        }
    }

    /*This method takes the inputs from user like username and password then call to method of
     RestServiceHandler interface for consume the api and receives the response with ovservable of
      BaseResponseModel class.*/

    // Login Api call. prog. name JSCVALD
    private fun loginUser(userName: String, password: String) {

        if (isConnectedToInternet) {

            Log.d("xx", "Base Url Is " + baseUrl);
            showLoading()

            val jObj = JsonObject()
            val jObj1 = JsonObject()
            val jObj2 = JsonObject()

            jObj1.addProperty("app", APP_NAME)
//        jObj1.addProperty("apkver", "" + APP_VER)
            jObj1.addProperty("user", "" + userName)
            jObj1.addProperty("pwd", "" + password)
            jObj1.addProperty("uid", "")
            jObj1.addProperty("whereFrom", "LOGIN")
            jObj1.addProperty("whouse", "")

            jObj2.addProperty("f1f24flags", "00000000000000000000000") // level 1
            jObj2.addProperty("btnflags", "0000000000")

            jObj.add("Std", jObj1) // level 0
            jObj.add("Input", jObj2)
            val json = Gson().toJson(jObj)
            Log.d("xx", "login json is: " + json)
            if (myLogFile != null) {
                writeLogsInFile(myLogFile!!, getDate() + ": Req: " + json)
            }
//        loginReqTime = getCurrentTimeInMiliSec()
            disposable =
                appApiServe.loginApiCall(jObj)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ result ->

                        Log.d("xx", "login res is: " + result)

                        var gson = Gson()
                        var mMineUserEntity =
                            gson.fromJson(result, PutawayOneModelRes::class.java)
                        Log.d("xx", "mMineUserEntity is: " + mMineUserEntity.Std.uid)
                        if(mMineUserEntity.Output.screen_bc != null){
                            AppColors.screen_bc = mMineUserEntity.Output.screen_bc
                        }

                        if(mMineUserEntity.Output.errmsg_bc != null){
                            AppColors.error_bc = mMineUserEntity.Output.errmsg_bc
                        }

                        if(mMineUserEntity.Output.errmsg_fc != null){
                            AppColors.error_fc = mMineUserEntity.Output.errmsg_fc
                        }
                        if (mMineUserEntity.Output.errmsg.equals("")) {
                            setStringPrefernce("userName", mMineUserEntity.Std.user)
//                        hideLoading()
//                        speakOut("Logged in, Scan the License")
                            AppData.setData("" + result)
                            setStringPrefernce("wareHouse", mMineUserEntity.Std.whouse)
                            setStringPrefernce("uId", mMineUserEntity.Std.uid)
                            WHOUSE = mMineUserEntity.Std.whouse
                            intent = Intent(applicationContext, MainActivityPutAway::class.java)
                            hideLoading()
                            startActivity(intent)
                        } else {
                            hideLoading()
                            clearInputFields()
                            if (myLogFile != null) {
                                writeLogsInFile(myLogFile!!, getDate() + ": Invalid Username Or Password" + json)
                            }
                            Toast.makeText(this, "Invalid Username Or Password", Toast.LENGTH_LONG)
                                .show()

                        }
                    }, { error ->
                        hideLoading()
                        errorHandler(applicationContext, error)

                    })

        } else {
            Toast.makeText(this, "Check Internet Connection.", Toast.LENGTH_LONG).show()
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

    // api fun for get init data of logged in user
//    private fun initApi(userName: String, lib: String) {
//        showLoading()
//        var mMap = ArrayMap<String, String>().apply {
//            put("lib", "" + lib)
//            put("programname", "PTAINIT")
//            put("input", userName)
//        }
//        disposable =
//            appApiServe.postApiCall(mMap)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ result ->
//                    //                    {"output": "1,RC200.01S,PTA01PR,5F11400E50401A3B88B90004AC1D17AF,"}
//
//                    var data = result.output.split(",").toTypedArray()
//                    if (!data[4].equals("")) {
//                        hideLoading()
//                        Toast.makeText(this, data[4], Toast.LENGTH_LONG).show()
//                        return@subscribe
//                    }
//
//                    speakOut("Logged in, Scan the License")
//                    setStringPrefernce("wareHouse", data[0])
//                    setStringPrefernce("uId", data[3])
//
//                    // store data in Singlton class
//                    AppData.setData(result.output)
//                    Log.d("xx", "Logged in, scan license")
//                    Log.d("xx", "Logged in as " + userName)
//
////                    val bundle = Bundle()
////                    bundle.putString("logged_in", "Logged in as " + userName)
////                    mFirebaseAnalytics?.logEvent("show_selected", bundle)
//
////                    HyperLog.d("xx", "Logged in as " + userName)
//
//                    Handler().postDelayed({
//                        /* Create an Intent that will start the Menu-Activity. */
//                        intent = Intent(applicationContext, MainActivity::class.java)
//                        hideLoading()
//                        startActivity(intent)
//                    }, 1500)
//                },
//                    { error ->
//                        hideLoading()
//                        errorHandler(applicationContext, error)
//
//                    })
//    }

    /**
     * For Displaying the loading when a service is hit
     */
    fun showLoading() {
        try {
            if (loadingDialog == null) {
                val factory = LayoutInflater.from(this)
                val progressDialogView = factory.inflate(R.layout.loading_dialog, null)
                val progressBar =
                    progressDialogView.findViewById<View>(R.id.progress_bar) as ProgressBar
                progressBar.indeterminateDrawable.setColorFilter(
                    ContextCompat.getColor(this, R.color.colorAccent),
                    PorterDuff.Mode.SRC_ATOP
                )
                loadingDialog =
                    AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle).create()
                loadingDialog!!.setView(progressDialogView)
                loadingDialog!!.setCanceledOnTouchOutside(false)
                loadingDialog!!.setCancelable(false)
            }
            loadingDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * For hiding the loading
     */
    fun hideLoading() {
        try {
            if (loadingDialog != null) {
                loadingDialog!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /*private val isExternalStorageReadOnly: Boolean get() {
        val extStorageState = Environment.getExternalStorageState()
        return if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            true
        } else {
            false
        }
    }
    private val isExternalStorageAvailable: Boolean get() {
        val extStorageState = Environment.getExternalStorageState()
        return if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            true
        } else{
            false
        }
    }*/


    private fun checkAndRequestPermissions(): Boolean {
        val writepermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readpermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        val listPermissionsNeeded = ArrayList<String>()

        if (writepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (readpermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        Log.d("xx", "Permission callback called-------")
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {

                val perms = HashMap<String, Int>()
                // Initialize the map with both permissions

                perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] =
                    PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.READ_EXTERNAL_STORAGE] =
                    PackageManager.PERMISSION_GRANTED
//                perms[Manifest.permission.RECORD_AUDIO] = PackageManager.PERMISSION_GRANTED
                // Fill with actual results from user
                if (grantResults.size > 0) {
                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]
                    // Check for both permissions
                    if (perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.READ_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                    ) {
                        Log.d("xx", "sms & location services permission granted")
                        // process the normal flow

                        /*val appDirectory = File(Environment.getExternalStorageDirectory().toString() + "/Putaway_Data")
                        Log.d("xx", "appDirectory is;- " + appDirectory)
                        var logFile = File(appDirectory, "logcat" + System.currentTimeMillis() + ".txt")

                        // create app folder
                        if (!appDirectory.exists()) {
                            appDirectory.mkdir()
                        }

                        if (!logFile.exists()) {
                            logFile.createNewFile()
                        }


                        var myAppLog: Logger = LoggerFactory.getLogger(LoginActivity.javaClass)

                        myAppLog.debug("Hello SWARAN")


                        // clear the previous logcat and then write the new one to the file
                        try {
                            var process = Runtime.getRuntime().exec("logcat -c")
                            process = Runtime.getRuntime().exec("logcat -f $logFile")
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                        mLogFile = logFile*/

                        val myApp = MyPersonalApp()
                        myApp.writeLogsToFile()
                        readFileFromDownloads()

                        //else any one or both the permissions are not granted
                    } else {
                        Log.d("xx", "Some permissions are not granted ask again ")
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                        //                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        ) {
                            showDialogOK("Service Permissions are required for this app",
                                DialogInterface.OnClickListener { dialog, which ->
                                    when (which) {
                                        DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions()
                                        DialogInterface.BUTTON_NEGATIVE ->
                                            // proceed with logic by disabling the related features or quit the app.
                                            finish()
                                    }
                                })
                        } else {
                            explain("You need to give some mandatory permissions to continue. Do you want to go to app settings?")
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }//permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                    }
                }
            }
        }

    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", okListener)
            .create()
            .show()
    }

    private fun explain(msg: String) {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage(msg)
            .setPositiveButton("Yes") { paramDialogInterface, paramInt ->
                //  permissionsclass.requestPermission(type,code);
                startActivity(
                    Intent(
                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:com.example.parsaniahardik.kotlin_marshmallowpermission")
                    )
                )
            }
            .setNegativeButton("Cancel") { paramDialogInterface, paramInt -> finish() }
        dialog.show()
    }

    companion object {

        val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
        private val SPLASH_TIME_OUT = 2000

        var mLogFile: Any? = null

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
        tts = TextToSpeech(this, this)

        Log.d("xx", "Inside On resume");
        // Settings elements setup
        var prefs = PreferenceManager.getDefaultSharedPreferences(this)
        // then you use
        val ip = prefs.getString("ip", "")
        val port = prefs.getString("port", "")
        val distanse = prefs.getString("distance", "")
        val appLib = prefs.getString("libName", "")
        val url = prefs.getString("url", "")
        val editor = prefs.edit()

        if (ip.equals("")) {
            editor.putString("ip", "")
        }

        if (port.equals("")) {
            editor.putString("port", "")
        }

        if (distanse.equals("")) {
            editor.putString("distance", "")
        }
        if (appLib.equals("")) {
            editor.putString("libName", "")
        }
        if (url.equals("")) {
            editor.putString("url", "http://bfc.demo.damcogroup.com:8880/")
        }
        editor.commit()

        var mURL = prefs.getString("url", "")
        baseUrl = "" + mURL
        Log.d("xx", "baseUrl is: " + baseUrl)

//        mLIbName = prefs.getString("libName", "").toString()
//        Log.d("xx", "lib name is: " + mLIbName)

//        appApiServe by lazy {
//            RestServiceHandler.create()
//        }
//        appApiServe = RestServiceHandler.create()
    }

    override fun onPause() {
//        if (EMDKWrapper != null) {
//            EMDKWrapper?.releaseDoubleClickScannerListener()
//            EMDKWrapper?.releaseScannedBarcodeListener()
//            EMDKWrapper?.releaseConnectionScannerListener()
//            EMDKWrapper?.onClosed()
//        }
        hideLoading()
        disposable?.dispose()
        super.onPause()
    }

    override fun onStop() {
//        if (EMDKWrapper != null) {
//            EMDKWrapper?.releaseDoubleClickScannerListener()
//            EMDKWrapper?.releaseScannedBarcodeListener()
//            EMDKWrapper?.releaseConnectionScannerListener()
//            EMDKWrapper?.onClosed()
//        }

        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }

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

    fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    fun getVersion(): String {
        val pInfo = packageManager
            .getPackageInfo(packageName, 0)
        val current = pInfo.versionName
        return current.toString()
    }

    private fun checkInTimeAction() {
        Handler().postDelayed({
            /* Create an Intent that will start the Menu-Activity. */
            if (touchCount > 6) {
                touchCount = 0
                val mIntent = Intent(this, PutAwayAdminApiControlActivity::class.java)
//                val mIntent: Intent = Intent(this, SettingsActivity::class.java)
                startActivity(mIntent)
            } else {
                touchCount = 0
            }
        }, 5000)
    }

    fun seScanValues(decodedData: String) {
        Log.d("xx", "inside split method. ")
        var data = decodedData.split("~").toTypedArray()
        var userName: String = ""
        var password: String = ""
        try {
            if (data.size > 1) {
                userName = data[0]
                password = data[1]
            }
            Log.d("xx", "barcode userName: " + userName + " Pwd: " + password)

            binding.loginUserId?.setText(userName.toString().toUpperCase())
            binding.loginPassword?.setText(password.toString())
            loginButtonClick()

        } catch (e: IndexOutOfBoundsException) {
//            openAlerDialog("Something Wrong with Username and Password!")
        }
    }

//    override fun onScannerDoubleClick() {
//        Log.d("xx", "Double click Called")
//        runOnUiThread {
//            if (DOUBLE_CLICK_ENABLE_LOGIN) {
//                Log.d("xx", "Inside Double click Called")
//                loginButtonClick()
//            }
//        }
//    }
//
//    override fun onScannerConnection(status: String?) {
//        Log.d("xx", "Scanned status $status")
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
//
//    override fun onScannedBarcode(barcode: String?, barcodeType: String?) {
//        Log.d("xx", "barcode scanned: $barcode")
//
//        if (barcode != null) {
//            runOnUiThread {
//                if (DOUBLE_CLICK_ENABLE_LOGIN) {
//                    Log.d("xx", "barcode scanned: " + barcode)
//                    seScanValues(barcode)
//                }
//            }
//        }
//
//    }

    private fun readFileFromDownloads() {
        val baseDir = Environment.getExternalStorageDirectory().absolutePath
        val pathDir =
            "$baseDir/Android/data/com.bfcassociates.putaway/appConfig"
        val fileName = "config.xml"
        val fileNameLog = "PutAway_AppLog_" + getDate() + ".txt"
        val mydir = File(pathDir)
//        val mydirLog = File(fileNameLog)
        if (mydir.exists()) {
            var file = File(mydir, fileName)
            if (file.exists()) {
                Log.d("xx", "File exist!!")
                try {
                    var fis = FileInputStream(file)
                    Log.d("xx", "File reading!!")
                    var dbFactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
                    var dBuilder: DocumentBuilder = dbFactory.newDocumentBuilder()
                    var doc: Document = dBuilder.parse(fis)
                    var element: Element = doc.documentElement
                    element.normalize()
                    val nList = doc.getElementsByTagName("putaway")
                    for (i in 0 until nList.length) {
                        val node = nList.item(i)
                        if (node.nodeType === Node.ELEMENT_NODE) {
                            val element2 = node as Element
                            val url = getValue("url", element2)
//                            val apiTimeout = getValue("apiTimeout", element2)
                            DEVICE_ID = getValue("deviceId", element2)
                            Log.d("xx", "url from Settings.xml $url and deviceId: $DEVICE_ID")
                            baseUrl = url
//                            if(!apiTimeout.isNullOrEmpty()) {
//                                apiTIMEOUT = apiTimeout.toLong()
//                            }
                            setStringPrefernce("api_url", url)
                        }
                    }
                } catch (e: java.lang.Exception) {
                    Log.e("xx", "File contains something wrong data")
                    e.printStackTrace()
                }

            } else {
                Log.d("xx", "File not exist")
                var myInternalFile = File(mydir, fileName)
                myInternalFile.createNewFile()
                if (myInternalFile != null) {
                    writeToXml(myInternalFile)
                }
            }
        } else {
            Log.d("xx", "Dir not exist")
            mydir.mkdirs()
            var myInternalFile = File(mydir, fileName)
            myInternalFile.createNewFile()
            if (myInternalFile != null) {
                writeToXml(myInternalFile)
            }
        }


        val logfile = getStringPrefernce("fileNameLog")


        if (logfile != null && !logfile.equals("")) {
            val mLogFile = File(mydir, logfile)
            uploadDataFromLogfiles(mLogFile, "/bfcLogger/PutAwayLogs")
        }


        if (mydir.exists()) {
            Log.d("xx", "log Dir exist")
            myLogFile = File(mydir, fileNameLog)
            if (!myLogFile!!.exists()) {
                myLogFile?.createNewFile()
            }
            if (myLogFile != null) {
                writeLogsInFile(myLogFile!!, "User on Login screen")
            }
        } else {
            Log.d("xx", "log Dir not exist")
            mydir.mkdirs()
            myLogFile = File(mydir, fileNameLog)
            myLogFile?.createNewFile()
            if (myLogFile != null) {
                writeLogsInFile(myLogFile!!, "User on Login screen")
            }
        }

        setStringPrefernce("fileNameLog", fileNameLog)
    }

    private fun getValue(tag: String, element: Element): String {
        val nodeList = element.getElementsByTagName(tag).item(0).childNodes
        val node = nodeList.item(0)
        return node.nodeValue
    }
    private fun uploadDataFromLogfiles(file1: File, path: String) {
        var bodyString: StringBuilder = java.lang.StringBuilder("")
        file1.useLines { lines ->
            lines.forEach {
                bodyString.append(it)
                bodyString.append(System.getProperty("line.separator"))
//            lineList.add(it)
            }
        }

//        val text = "plain text request body"
        val body = RequestBody.create(MediaType.parse("application/json"), bodyString.toString())

//        val body: RequestBody = RequestBody.create(
//            MediaType.parse("application/json"),
//            bodyString.toString()
//        )

        val jObj = JsonObject()

        jObj.addProperty("device", "" + Build.DEVICE) // level 0
        jObj.addProperty("deviceId", "$DEVICE_ID") // level 0
        jObj.addProperty("fileName", "PutAway")
        jObj.addProperty("filePath", path)
        jObj.addProperty("body", "" + bodyString)
        val json = Gson().toJson(jObj)
        Log.d("xx", "upload log file json is: " + json)


        disposable =
            appApiServe.uploadCall(
                DEVICE_ID,
                "PutAway",
                path,
                "Y",
                body
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    Log.d("xx", "res of Log file is: " + result)
                    file1.delete()

                    val baseDir = Environment.getExternalStorageDirectory().absolutePath
                    val pathDir =
                        "$baseDir/Android/data/com.bfcassociates.putAway/appConfig"
                    val mydir = File(pathDir)

                    var errorfileName = "PutAway_Err.txt"
                    val mErrorFile = File(mydir, errorfileName)
                    if (mErrorFile.exists()) {
                        Log.d("xx", "Error file exists.")
                        uploadDataFromErrorfiles(mErrorFile, "/bfcLogger/PutAwayExceptions")
                    } else {
                        Log.d("xx", "Error file NOT exists.")
                    }
                },
                    { error ->
                        Log.e("xx", "error is: " + error.message)
                    })
    }

    private fun uploadDataFromErrorfiles(file1: File, path: String) {
        var bodyString: StringBuilder = java.lang.StringBuilder("")
        file1.useLines { lines ->
            lines.forEach {
                bodyString.append(it)
                bodyString.append(System.getProperty("line.separator"))
//            lineList.add(it)
            }
        }

//        val text = "plain text request body"
        val body = RequestBody.create(MediaType.parse("application/json"), bodyString.toString())

//        val body: RequestBody = RequestBody.create(
//            MediaType.parse("application/json"),
//            bodyString.toString()
//        )

        val jObj = JsonObject()

        jObj.addProperty("device", "" + Build.DEVICE) // level 0
        jObj.addProperty("fileName", "PutAway_Err")
        jObj.addProperty("filePath", path)
        jObj.addProperty("body", "" + bodyString)
        val json = Gson().toJson(jObj)
        Log.d("xx", "upload errror json is: " + json)



        disposable =
            appApiServe.uploadCall(
                DEVICE_ID,
                "PutAway_Err",
                path,
                "Y",
                body
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    Log.d("xx", "res of error file is: " + result)
                    file1.delete()
                },
                    { error ->
                        Log.e("xx", "error is: " + error.message)
                    })
    }
     private fun clearInputFields(){
         binding.loginUserId.setText("")
         binding.loginPassword.setText("")
     }





}

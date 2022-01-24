package com.bfc.putaway.view.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bfc.putaway.R
import com.bfc.putaway.apirequests.PutawayRestServiceHandler
import com.bfc.putaway.databinding.ActivityBaseBinding
import com.bfc.putaway.util.MyPersonalApp
import com.bfc.putaway.util.MyPersonalApp.*
import com.bfc.putaway.util.RFIDDataReceiver
import com.bfc.putaway.util.SHARED_PREF_NAME
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.BuildConfig
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*


/**
 * Created by shivam on 7/19/2016.
 */
open class PutAwayBaseActivity : AppCompatActivity() {

    //    lateinit var mTextViewScreenTitle: TextView
//    lateinit var mTextViewScreenTitleSub: TextView
//    lateinit var mTextViewScreenDetail: TextView
//    lateinit var mTextViewScreenDetailSub: TextView
    var mPrefs: SharedPreferences? = null
    private val mRemeberPref: SharedPreferences? = null
    private var snackbar: Snackbar? = null
    private var loadingDialog: AlertDialog? = null

    var mFragment: Fragment? = null
    private var isHomeActivityVisible: Boolean? = null

    internal var myLocale: Locale? = null

    var mdisposable: Disposable? = null
    val mappApiServe by lazy {
        PutawayRestServiceHandler.create()
    }
    lateinit var activityBaseBinding: ActivityBaseBinding

//    internal var currentLanguage = "en"
//    internal var currentLang: String? = null
//    var drawerLayout: Any? = null
    /**
     * function to check if internet connection is active or not.
     *
     * @return true if connected to internet else false
     */
    val isConnectedToInternet: Boolean
        @SuppressLint("MissingPermission")
        get() {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork != null
        }

    val backStackCount: Int
        get() {
            val fm = supportFragmentManager
            return fm.backStackEntryCount
        }

    fun getmPrefs(): SharedPreferences? {
        return mPrefs
    }

    fun getmRemberedPrefs(): SharedPreferences? {
        return mRemeberPref
    }

    /*fun checkFragment() {
        if (isHomeActivityVisible!!) {
            if (mFragment is OrderPickOneFragment) {
                Log.d("xx", "click on Logout...")
                logoutApp(mFragment!!)
            } else if (mFragment is OrderPickTenFragment) {
                logoutApp(mFragment!!)
            } else {
                Log.d("xx", "Logout not Working.. ")
//                speekError()
//                logoutApp(currentFragment!!)
                Toast.makeText(baseContext, resources.getString(R.string.logout_error), Toast.LENGTH_SHORT).show()

            }
        }
    }*/

//    private fun speekError() {
//        var obj = HomeActivity()
//        obj.speakOut(resources.getString(R.string.error_logout))
//    }

    fun setCurrentFragmment(fragment: Fragment) {
        currentFragment = fragment
        mFragment = fragment
    }

    fun getCurrentFragment(): Fragment {
        return currentFragment as Fragment
    }

    /*fun logoutApp(currentFragment: Fragment) {
        var obj = HomeActivity()
        obj.clickCancel(currentFragment)
    }*/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPrefs = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        /*var mLan = getStringPrefernce("lang_switch")
        if (mLan.equals("true")) {
            setLocale("es")
        } else {
            setLocale("en")
        }*/
    }

    override fun setContentView(layoutResID: View) {
        activityBaseBinding = ActivityBaseBinding.inflate(layoutInflater)
        var drawerLayout: DrawerLayout =
            layoutInflater.inflate(R.layout.activity_base, null) as DrawerLayout
        drawerLayout.setScrimColor(resources.getColor(android.R.color.transparent))
        var navigationView: NavigationView = drawerLayout.findViewById(R.id.navigation_view)
        val drawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar as Toolbar?,
            R.string.open,
            R.string.close
        ) {
        }

        // Configure the drawer layout to add listener and show icon on toolbar
        drawerToggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        // Set navigation view navigation item selected listener
        activityBaseBinding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_logout -> {
                    finish()
                }
                R.id.action_share -> {

                    val recipient = "arpitg@damcogroup.com"
                    val subject = "LOG_FILE_PUTAWAY || APP_LOG_DETAILS"
                    val message = "Dear Admin, please find the attachment"
//                    var filePath = FILE_DESTINATION
                    var file = File(FILE_DESTINATION, FILE_NAME)
                    /* var uri = Uri.fromFile(file)
                     val mFile = File(uri.path)*/

//                    sendEmail(recipient, subject, message, file)


                    shareFileOnServer()

                }
                R.id.action_rfid_reader -> {
                    // connect to rfid reader
//                    MainActivity.connectToRFIDServer(this)
                   val intent = Intent(applicationContext, MainActivityPutAway::class.java)
                    startActivity(intent)
                    finish()

//                    Handler().postDelayed({
//                        /* Create an Intent that will start the Menu-Activity. */
////                        appResponseMapper(AppData.getData())
//                        intent = Intent(applicationContext, MainActivity::class.java)
////                        hideLoading()
//                        startActivity(intent)
//                    }, 2000)
                }
            }
            // Close the drawer
            drawer_layout.closeDrawer(GravityCompat.START)
            true
        }

//        var activityContainer: FrameLayout = drawerLayout.findViewById(R.id.layout_container)
//        mTextViewScreenTitle = drawerLayout.findViewById(R.id.text_screen_title) as TextView
//        mTextViewScreenTitleSub = drawerLayout.findViewById(R.id.text_screen_title_sub) as TextView
//        mTextViewScreenDetail = drawerLayout.findViewById(R.id.text_screen_detail) as TextView
//        mTextViewScreenDetailSub = drawerLayout.findViewById(R.id.text_screen_detail_sub) as TextView

//        var hamburgernIcon: ImageView = drawerLayout.findViewById(R.id.btn_sidemenu)
//        var sideMenuBack: ImageView = drawerLayout.findViewById(R.id.side_menu_back)
//        var tv_appVer: TextView = drawerLayout.findViewById(R.id.tv_appVer)
        activityBaseBinding.tvAppVer.text = "App Version: " + getVersion()
        activityBaseBinding.sideMenuBack.setOnClickListener {
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        activityBaseBinding.toolbar.btn_sidemenu.setOnClickListener {
            if (!RFIDDataReceiver.isConnectedToRFID) {
                Log.d("xx", "Inside isConnectedToRFID false")
                activityBaseBinding.navigationView.menu.findItem(R.id.action_rfid_reader)
                    .setVisible(true)
            } else {
                Log.d("xx", "Inside isConnectedToRFID true")
                activityBaseBinding.navigationView.menu.findItem(R.id.action_rfid_reader)
                    .setVisible(false)
            }

            drawer_layout.openDrawer(GravityCompat.START)
        }

        layoutInflater.inflate(R.layout.activity_main, activityBaseBinding.layoutContainer, true)
        super.setContentView(activityBaseBinding.root)
    }

    fun setLocale(localeName: String) {
//        if (localeName != currentLanguage) {
        myLocale = Locale(localeName)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
//        val refresh = Intent(this, HomeActivity::class.java)
//        refresh.putExtra(currentLang, localeName)
//        startActivity(refresh)
//        } else {
//            Toast.makeText(this, "Language already selected!", Toast.LENGTH_SHORT).show()
//        }
    }

    fun getVersion(): String {
        val pInfo = packageManager
            .getPackageInfo(packageName, 0)
        val current = pInfo.versionName
        return current.toString()
    }

    fun setScreenTitle(resId: Int) {
        activityBaseBinding.toolbar.text_screen_title.text = getString(resId)
    }

    fun setScreenTitleBG(colorCode: String) {
        activityBaseBinding.toolbar.text_screen_title.setBackgroundColor(Color.parseColor(colorCode))
    }

    fun setScreenTitle(title: String) {
        activityBaseBinding.toolbar.text_screen_title.text = title
    }

    fun setScreenTitleSubBG(colorCode: String) {
        activityBaseBinding.toolbar.text_screen_title_sub.setBackgroundColor(
            Color.parseColor(
                colorCode
            )
        )
    }

    fun setScreenTitleSub(title: String) {
        activityBaseBinding.toolbar.text_screen_title_sub.text = title
    }

    fun setScreenDetailSubBG(colorCode: String) {
        activityBaseBinding.toolbar.text_screen_detail_sub.setBackgroundColor(
            Color.parseColor(
                colorCode
            )
        )
    }

    fun setScreenDetailSub(title: String) {
        activityBaseBinding.toolbar.text_screen_detail_sub.text = title
    }

    fun setScreenDetailBG(colorCode: String) {
        activityBaseBinding.toolbar.text_screen_detail.setBackgroundColor(Color.parseColor(colorCode))
    }

    fun setScreenDetail(title: String) {
        activityBaseBinding.toolbar.text_screen_detail.text = title
    }

    /**
     * function for clearing shared SharedPreferences
     */
    fun clearPreferences() {
        mPrefs!!.edit().clear().apply()
    }


    /**
     * Function to display Toast
     *
     * @param message  Message of the Toast
     * @param duration Duration of the Toast 0 for Short and 1 for Long Toast
     */
    fun showToast(message: String?) {
        if (message != null && message.length > 0) {
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }

        /* val li = layoutInflater
         val layout = li.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_layout_id) as? ViewGroup)

         var txtview = layout.findViewById<TextView>(R.id.text)
         txtview.setText(message)

         val toast = Toast(applicationContext)
         toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
         toast.duration = Toast.LENGTH_SHORT
         toast.view = layout//setting the view of custom toast layout
         toast.show()*/
    }


    /**
     * For Displaying Output in LogCat
     *
     * @param message Message to display
     */
    fun showOutput(message: String) {
        println("------------------------")
        println(message)
        println("------------------------")
    }


    /**
     * For Displaying the Log Output in Logcat
     *
     * @param tag     Tag of the Log
     * @param message Message of the Log
     */
    fun showLog(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            println("--------------------")
            Log.d(tag, message)
            println("--------------------")
        }
    }

    // Extension function to show toast message easily
    private fun Context.toast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Clearing the Fragment backStack
     */
    fun clearBackStack() {
        val fm = supportFragmentManager
        for (i in 0 until fm.backStackEntryCount) {
            fm.popBackStack()
        }
    }

    /**
     * Clearing a Fragment from backStack
     */
    fun popFragment() {
        val fm = supportFragmentManager
        fm.popBackStack()
    }

    /**
     * For Hiding the keyboard
     */
    fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    /**
     * For Displaying the snackBar
     *
     * @param msg message to display in the snackBar.
     */
    fun showSnack(msg: String) {
        try {
            snackbar = Snackbar.make(currentFocus!!, msg, Snackbar.LENGTH_LONG)
            snackbar!!.show()
        } catch (e: Exception) {
            showToast(msg)
            e.printStackTrace()
        }

    }


    /**
     * For Displaying the snackBar
     */
    fun showInternetSnack() {
        try {
            snackbar = Snackbar.make(
                currentFocus!!,
                "Please check your internet connection.",
                Snackbar.LENGTH_LONG
            )
            snackbar!!.show()
        } catch (e: Exception) {
            showToast("Please check your internet connection.")
            e.printStackTrace()
        }

    }

    /**
     * For Displaying the snackBar
     */
    fun showServerSnack() {
        try {
            snackbar = Snackbar.make(
                currentFocus!!,
                "Something went wrong. Please try again.",
                Snackbar.LENGTH_LONG
            )
            snackbar!!.show()
        } catch (e: Exception) {
            showToast("Something went wrong. Please try again.")
            e.printStackTrace()
        }

    }


    /**
     * For Dismissing the snackBar
     */
    fun dismissSnack() {
        snackbar!!.dismiss()
    }

    /**
     * For Displaying the message at snackBar
     *
     * @param msg      message to display in the snackBar.
     * @param listener setting the action when button is clicked
     */
    fun showMsgSnack(msg: String, listener: View.OnClickListener) {
        var msg = msg
        if (!isConnectedToInternet)
            msg = "No Internet Connection"

        try {
            snackbar = Snackbar.make(currentFocus!!, msg, Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", listener)
            snackbar!!.show()
        } catch (e: Exception) {
            showToast(msg)
            e.printStackTrace()
        }

    }

    /*public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        hideKeyboard();
        this.fragment = fragment;
        if (addToBackStack) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(fragment.getClass().getName()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }
    }*/

    /*public void addFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(fragment.getClass().getName()).commit();
        this.fragment = fragment;
    }*/


    /*fun replaceFragment(fragment: Fragment, addToBackStack: Boolean) {
        hideKeyboard()
        this.fragment = fragment
        if (addToBackStack) {
            supportFragmentManager.beginTransaction().replace(R.id.container, fragment)
                .addToBackStack(fragment.javaClass.name).commit()
        } else {
            supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
        }
    }*/

    fun makeTransparentStatusBar() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

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

    fun setStringPrefernce(key: String, value: String) {
        getmPrefs()!!.edit().putString(key, value).apply()
    }

    infix fun getStringPrefernce(key: String): String {
        return getmPrefs()!!.getString(key, "").toString()
    }

    companion object {
        var currentFragment: Fragment? = null

        /**
         * Check the GPS state.
         * @param context
         * @return Return true if GPS is on.
         */
        fun checkLocationProviderIsEnabled(context: Context): Boolean {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            var gpsEnabled = java.lang.Boolean.FALSE
            var networkEnabled = java.lang.Boolean.FALSE

            try {
                gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            } catch (ex: Exception) {
            }

            try {
                networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            } catch (ex: Exception) {
            }

            return !(!gpsEnabled && !networkEnabled)
        }

        /**
         * Get the bluetooth state.
         * @return Return true if bluetooth is on.
         */
        /*val isBluetoothAvailable: Boolean
            get() {
                val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                return bluetoothAdapter != null && bluetoothAdapter.isEnabled
            }*/
    }

    /*// custom toast for errors.
    fun Toast.createToast(context: Context, message: String, gravity: Int, duration: Int) {
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        *//*first parameter is the layout you made
        second parameter is the root view in that xml
         *//*
        val layout = inflater.inflate(
            R.layout.custom_toast,
            (context as Activity).findViewById<ViewGroup>(R.id.custom_toast_layout_id)
        )

        layout.findViewById<TextView>(R.id.text).text = message
        setGravity(gravity, 0, 0)
        setDuration(Toast.LENGTH_LONG)
        view = layout
        show()
    }*/

    fun setIsHomeActivityVisible(isVisible: Boolean) {
        isHomeActivityVisible = isVisible
    }


    // fun for Create and share file over the Email.
    /*fun createLogFile(data: String) {
        val mFileWriter: FileWriter
        val lComma = ','
        val lNewLine = '\n'

        var file: File = Environment.getDataDirectory()
        file = File(file.path + "Backup BFC.csv")

        if (!file.exists()) {
            file.createNewFile()
        }

        mFileWriter = FileWriter(file)
        mFileWriter.append(resources.getString(R.string.timestamp))
        mFileWriter.append(lComma)

        mFileWriter.append(resources.getString(R.string.loggeduser))
        mFileWriter.append(lComma)

        mFileWriter.append(resources.getString(R.string.screen_name))
        mFileWriter.append(lComma)

        mFileWriter.append(resources.getString(R.string.actionperformed))
        mFileWriter.append(lComma)

        mFileWriter.append(resources.getString(R.string.ip))
        mFileWriter.append(lComma)

        mFileWriter.append(resources.getString(R.string.op))
        mFileWriter.append(lComma)

        mFileWriter.append(resources.getString(R.string.error))
        mFileWriter.append(lNewLine)

        var mLogList = logList

        if (mLogList != null) {

            for (mData in mLogList) {
                var timeStamp = mData.timeStamp
                var loggedUser = mData.loggedUser
                var screenName: String = mData.screenName
                var actionPerformed: String = mData.actionPerformed
                var ip: String = mData.ip
                var op: String = mData.op
                var mError: String = mData.mError

                mFileWriter.append(timeStamp)
                mFileWriter.append(lComma)

                mFileWriter.append(loggedUser)
                mFileWriter.append(lComma)

                mFileWriter.append(screenName)
                mFileWriter.append(lComma)

                mFileWriter.append(actionPerformed)
                mFileWriter.append(lComma)

                mFileWriter.append(ip)
                mFileWriter.append(lComma)

                mFileWriter.append(op)
                mFileWriter.append(lComma)

                mFileWriter.append(mError)
                mFileWriter.append(lNewLine)
            }

            mFileWriter.flush()
            mFileWriter.close()

        } else {
            Toast.makeText(this, "No Log Data.", Toast.LENGTH_SHORT).show()
        }


    }*/


    /**
     * E-mail intent for sending attachment
     */
    private fun sendEmail(recipient: String, subject: String, message: String, file: File) {
//        Log.d("xx", "Sending Log ...###### ")
        val emailIntent: Intent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "application/pdf"
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
//        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))

        var fileURI: Uri = FileProvider.getUriForFile(
            this, this.getApplicationContext()
                .getPackageName() + ".provider", file
        )
//        Log.d("xx", "fileURI is:- " + fileURI)

        emailIntent.setDataAndType(fileURI, "application/pdf")
        emailIntent.putExtra(Intent.EXTRA_STREAM, fileURI)
//        emailIntent.setData(fileURI)
        emailIntent.putExtra(Intent.EXTRA_TEXT, message)
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(Intent.createChooser(emailIntent, "Choose Email Client..."))
        } catch (ex: ActivityNotFoundException) {
            Log.d("xx", "No Intent matcher found")
        }

    }


    private fun shareFileOnServer() {
        showLoading()
        val file = File(MyPersonalApp.FILE_DESTINATION, MyPersonalApp.FILE_NAME)

        /*val file2 = File(
            MyPersonalApp.FILE_DESTINATION,
            MyPersonalApp.FILE_NAME + getStringPrefernce("userName") + System.currentTimeMillis() + "_logout.txt"
        )

        if (file.renameTo(file2)) {
            Log.d("xx", "File renamed:" + file2)
        } else {
            Log.e("xx", "File not renamed:")
        }*/

        if (process != null) {
            process.destroy()
        }

        val fileURI: Uri = FileProvider.getUriForFile(
            this, this.getApplicationContext()
                .getPackageName() + ".provider", file
        )

//        val fileURI =Uri.fromFile(file)

        // create RequestBody instance from file
        val requestFile: RequestBody =
            RequestBody.create(
                MediaType.parse(getContentResolver().getType(fileURI)),
                file
            )

        // MultipartBody.Part is used to send also the actual file name
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.getName(), requestFile)


//        val filename: RequestBody = RequestBody.create(MultipartBody.FORM, file.getName());

        // create part for file (photo, video, ...)
// val body:MultipartBody.Part = prepareFilePart("photo", fileURI)

//        mdisposable = mappApiServe.uploadFile(body)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ result ->
//                hideLoading()
//                Log.d("xx", "response is:- " + result.output)
////                finish()
//                process = Runtime.getRuntime().exec("logcat -c")
//                process = Runtime.getRuntime().exec("logcat -f $logFile")
//                Toast.makeText(applicationContext, result.output, Toast.LENGTH_SHORT).show()
//            }, { error ->
//                hideLoading()
//                process = Runtime.getRuntime().exec("logcat -c")
//                process = Runtime.getRuntime().exec("logcat -f $logFile")
//                Toast.makeText(applicationContext, "Server Connection Error!", Toast.LENGTH_SHORT)
//                    .show()
//            })
//        cdv
    }


}

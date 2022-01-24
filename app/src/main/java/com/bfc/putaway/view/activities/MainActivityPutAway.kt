package com.bfc.putaway.view.activities

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.*
import android.speech.tts.TextToSpeech
import android.util.ArrayMap
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bfc.putaway.R
import com.bfc.putaway.apirequests.PutawayRestServiceHandler
import com.bfc.putaway.customviews.PutawayCustomDialogFragment
import com.bfc.putaway.databinding.ActivityMainBinding
import com.bfc.putaway.interfaces.FabInterface
import com.bfc.putaway.interfaces.OnFragmentInteractionListener
import com.bfc.putaway.models.AppData
import com.bfc.putaway.util.*
import com.bfc.putaway.util.Methods.avoidDoubleClicks
import com.bfc.putaway.util.Methods.dataParsing
import com.bfc.putaway.util.Methods.getDate
import com.bfc.putaway.view.fragments.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.view.*
import java.util.*


class MainActivityPutAway : PutAwayBaseActivity(), OnFragmentInteractionListener, View.OnClickListener,
    TextToSpeech.OnInitListener, FabInterface,
//    EMDKScannerWithDoubleClick.DoubleClickScannerListener,
//    EMDKScannerWithDoubleClick.ScannedBarcodeListener,
//    EMDKScannerWithDoubleClick.ConnectionScannerListener,
    RFIDDataReceiver.SendRFIDScannedData {

    private var uId: String = ""
    private var userName: String = ""

    var currebtFragment: Fragment? = null
    var paramMap: ArrayMap<String, String>? = null
    var disposable: Disposable? = null
    val appApiServe by lazy {
        PutawayRestServiceHandler.create()
    }

    //    var mCurrentScreen: String? = null
//    var tv_cancel: TextView? = null
//    var tv_next: TextView? = null
//    var tv_skip: TextView? = null
//    var tv_errorMsg: TextView? = null
    val TAG = "xx"
    private var tts: TextToSpeech? = null

    //    var rl_root: RelativeLayout? = null
    lateinit var dialog: Any

    //    var fab: FloatingActionButton? = null
    lateinit var binding: ActivityMainBinding
//    var EMDKWrapper: EMDKScannerWithDoubleClick? = null

    //    var rfidDataReceiver: RFIDDataReceiver? = null
    var isFABOpen: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        if (Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.contains("Motorola Solutions")) {
//            EMDKWrapper = EMDKScannerWithDoubleClick()
//        }
//        rfidDataReceiver = RFIDDataReceiver(this);
        connectToRFIDServer(this)

        tts = TextToSpeech(this, this)

        /* var prefs = PreferenceManager.getDefaultSharedPreferences(this)
         // then you use
         val ip = prefs.getString("ip", "")
         val port = prefs.getString("port", "")
         val distanse = prefs.getString("distance", "")
         val editor = prefs.edit()

         if (ip.equals("")) {
             editor.putString("ip", "172.29.18.151")
         }

         if (port.equals("")) {
             editor.putString("port", "2020")
         }

         if (distanse.equals("")) {
             editor.putString("distance", "300")
         }
         editor.commit()*/


        initUi()
        checkForConnection()
        activityBaseBinding.layoutContainer.putawayfab!!.setOnClickListener(View.OnClickListener {
//            activityBaseBinding.layoutContainer.fab?.isEnabled = false
//            Handler().postDelayed({
//                activityBaseBinding.layoutContainer.fab?.isEnabled = true
//            }, 1500)

            // fun for perform action on p3 press
            closeFABMenu()
            p3Pressed()

        })
        activityBaseBinding.layoutContainer.putawayfab_exp_close.setOnClickListener(View.OnClickListener {
//            activityBaseBinding.layoutContainer.fab_exp_close?.isEnabled = false
//            Handler().postDelayed({
//                activityBaseBinding.layoutContainer.fab_exp_close?.isEnabled = true
//            }, 1500)

            if (!isFABOpen) {
                showFABMenu()
            } else {
                closeFABMenu()
            }
            checkForConnection()
        })
        activityBaseBinding.layoutContainer.putawayfab1.setOnClickListener(View.OnClickListener {
            closeFABMenu()
            switchToManual(false) // for manual Slot
            checkForConnection()

        })

        //updateSwitch()
    }

    // fun for check the key pressed from device
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        if (keyCode == KeyEvent.KEYCODE_BUTTON_L2) {
            // fun for perform action on click of p3 button
            p3Pressed()
            return true
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d("xx", "nononono")
            return true
        }

        return false
    }


    private fun p3Pressed() {
        if (myLogFile != null) {
            Methods.writeLogsInFile(myLogFile!!, getDate() + " : P3 Button Press")
        }
        if (currebtFragment is OnePutawayFragment) {
            (currebtFragment as OnePutawayFragment).fabClickedData()
        } else if (currebtFragment is TwoPutawayFragment) {
            (currebtFragment as TwoPutawayFragment).fabClickedData()
        } else if (currebtFragment is ThreeS_PutawayFragment) {
            (currebtFragment as ThreeS_PutawayFragment).fabClickedData()
        } else if (currebtFragment is FourPutawayFragment) {
            (currebtFragment as FourPutawayFragment).fabClickedData()
        } else if (currebtFragment is FivePutawayFragment) {
            (currebtFragment as FivePutawayFragment).fabClickedData()
        } else if (currebtFragment is SixPutawayFragment) {
            (currebtFragment as SixPutawayFragment).fabClickedData()
        } else if (currebtFragment is SevenPutawayFragment) {
            (currebtFragment as SevenPutawayFragment).fabClickedData()
        } else if (currebtFragment is EightPutawayFragment) {
            (currebtFragment as EightPutawayFragment).fabClickedData()
        } else if (currebtFragment is NinePutawayFragment) {
            (currebtFragment as NinePutawayFragment).fabClickedData()
        } else if (currebtFragment is NineAPutawayFragment) {
            (currebtFragment as NineAPutawayFragment).fabClickedData()
        } else if (currebtFragment is TenPutawayFragment) {
            (currebtFragment as TenPutawayFragment).fabClickedData()
        } else if (currebtFragment is ElevenPutawayFragment) {
            (currebtFragment as ElevenPutawayFragment).fabClickedData()
        } else if (currebtFragment is TwelvePutawayFragment) {
            (currebtFragment as TwelvePutawayFragment).fabClickedData()
        } else if (currebtFragment is OneRfidPutawayFragment) {
            (currebtFragment as OneRfidPutawayFragment).fabClickedData()
        } else if (currebtFragment is TwoRfidPutawayFragment) {
            (currebtFragment as TwoRfidPutawayFragment).fabClickedData()
        } else if (currebtFragment is ThreeRfidPutawayFragment) {
            (currebtFragment as ThreeRfidPutawayFragment).fabClickedData()
        }
    }

    public fun initUi() {

//        var btn_next = findViewById<LinearLayout>(R.id.ll_next)
//        var btn_cancel = findViewById<LinearLayout>(R.id.ll_cancel)
//        var btn_skip = findViewById<LinearLayout>(R.id.ll_skip)
//        fab = findViewById<FloatingActionButton>(R.id.fab)

//        tv_cancel = findViewById<TextView>(R.id.tv_cancel)
//        tv_next = findViewById<TextView>(R.id.tv_next)
//        tv_skip = findViewById<TextView>(R.id.tv_skip)
//        tv_errorMsg = findViewById<TextView>(R.id.tv_errorMsg)
//        rl_root = findViewById<RelativeLayout>(R.id.rl_root)


        activityBaseBinding.layoutContainer.putawayll_next.setOnClickListener(this)
        activityBaseBinding.layoutContainer.putawayll_cancel.setOnClickListener(this)
        activityBaseBinding.layoutContainer.putawayll_skip.setOnClickListener(this)

        activityBaseBinding.layoutContainer.rl_root?.setOnClickListener {
            activityBaseBinding.layoutContainer.tv_errorMsg?.setText("")
        }

//        var data = AppData.getData()
//        mCurrentScreen = data[1]

        /*moveToNextFragment(
            OnePutawayFragment.newInstance(AppData.getData(), "")
        )*/

        /* // clear the previous logcat and then write the new one to the file
         try {
             var process = Runtime.getRuntime().exec("logcat -c")
             process = Runtime.getRuntime().exec("logcat -f $mLogFile")
         } catch (e: IOException) {
             e.printStackTrace()
         }*/
        AppColors.setErrorMsgBackgroundColor(
            activityBaseBinding.layoutContainer.tv_errorMsg,
            AppColors.error_bc
        )
        AppColors.setErrorMsgTextColor(
            activityBaseBinding.layoutContainer.tv_errorMsg,
            AppColors.error_fc
        )

        showLoading()
        Handler().postDelayed({
            /* Create an Intent that will start the Menu-Activity. */
            appResponseMapper(AppData.getData())
        }, 2000)

    }

    override fun onFragmentInteraction(uri: Uri) {

    }

    override fun onFabInteraction(
        tag: Int,
        datalist: ArrayList<String>,
        mapData: ArrayMap<String, String>?
    ) {
        paramMap = mapData
        if (tag == 404) {
            showSnack("Please Fill All The Fields!")
            return
        } else if (tag == 400) {
            showSnack(resources.getString(R.string.wrong_scan))
            return
        }

        when (tag) {
            0 -> showTaskDialog(datalist)
            1 -> showOneDialog(datalist)
            2 -> showTwoDialog(datalist)
            3 -> showThreeDialog(datalist)
            3118 -> showThreeVDialog(datalist)
            4 -> showFourDialog(datalist)
            5 -> showFiveDialog(datalist)
            6 -> showSixDialog(datalist)
            665 -> showSixADialog(datalist)
            666 -> showSixBDialog(datalist)
            667 -> showSixCDialog(datalist)
            668 -> showSixDDialog(datalist)
            669 -> showSixEDialog(datalist)
            670 -> showSixFDialog(datalist)
            7 -> showSevenDialog(datalist)
            766 -> showSevenBDialog(datalist)
            767 -> showSevenCDialog(datalist)
            8 -> showEightDialog(datalist)
            9 -> showNineDialog(datalist)
            965 -> showNineADialog(datalist)
            10 -> showTenDialog(datalist)
            11 -> showElevenDialog(datalist)
            12 -> showTwelveDialog(datalist)
            1265 -> showTwelveADialog(datalist)
            1266 -> showTwelveBDialog(datalist)
            12118 -> showTwelveVDialog(datalist)
            13 -> showThirteenDialog(datalist)
            14 -> showFourteenDialog(datalist)
            15 -> showFifteenDialog(datalist)
            16 -> showSixteenDialog(datalist)
            17 -> showSeventeenDialog(datalist)
            18 -> showEighteenDialog(datalist)
            19 -> showNineteenDialog(datalist)
            20 -> showTwentyDialog(datalist)
            21 -> showTwentyOneDialog(datalist)
            22 -> showTwentyTwoDialog(datalist)
            23 -> showTwentyThreeDialog(datalist)
            401 -> showChangeSlotDialog(datalist)
            681 -> showListSlotDialog(datalist)
            82 -> showEightyTwoDialog(datalist)
            else -> {
                Log.d("xx", "Something Wrong")
            }
        }
    }

    // Dialogs for all screens
    private fun showElevenDialog(list: ArrayList<String>) {
        showDialog(list, 11)
    }

    private fun showSixDialog(list: ArrayList<String>) {
        showDialog(list, 6)
    }

    private fun showSixADialog(list: ArrayList<String>) {
        showDialog(list, 665)
    }

    private fun showSixBDialog(list: ArrayList<String>) {
        showDialog(list, 666)
    }

    private fun showSixCDialog(list: ArrayList<String>) {
        showDialog(list, 667)
    }

    private fun showSixDDialog(list: ArrayList<String>) {
        showDialog(list, 668)
    }

    private fun showSixEDialog(list: ArrayList<String>) {
        showDialog(list, 669)
    }

    private fun showSixFDialog(list: ArrayList<String>) {
        showDialog(list, 670)
    }

    private fun showFourDialog(list: ArrayList<String>) {
        showDialog(list, 4)
    }

    private fun showFiveDialog(list: ArrayList<String>) {
        showDialog(list, 5)
    }

    private fun showThreeDialog(list: ArrayList<String>) {
        showDialog(list, 3)
    }

    private fun showThreeVDialog(list: ArrayList<String>) {
        showDialog(list, 3118)
    }

    private fun showTwoDialog(datalist: ArrayList<String>) {
        showDialog(datalist, 2)
    }

    private fun showOneDialog(datalist: ArrayList<String>) {
        showDialog(datalist, 1)
    }

    private fun showTaskDialog(datalist: ArrayList<String>) {
        showDialog(datalist, 0)
    }

    private fun showTwelveDialog(list: ArrayList<String>) {
        showDialog(list, 12)
    }

    private fun showTwelveADialog(list: ArrayList<String>) {
        showDialog(list, 1265)
    }

    private fun showTwelveBDialog(list: ArrayList<String>) {
        showDialog(list, 1266)
    }

    private fun showTwelveVDialog(list: ArrayList<String>) {
        showDialog(list, 12118)
    }

    private fun showSixteenDialog(list: ArrayList<String>) {
        showDialog(list, 16)
    }

    private fun showEightDialog(list: ArrayList<String>) {
        showDialog(list, 8)
    }

    private fun showSevenDialog(list: ArrayList<String>) {
        showDialog(list, 7)
    }

    private fun showNineDialog(list: ArrayList<String>) {
        showDialog(list, 9)
    }

    private fun showNineADialog(list: ArrayList<String>) {
        showDialog(list, 965)
    }

    private fun showTenDialog(list: ArrayList<String>) {
        showDialog(list, 10)
    }

    private fun showSevenBDialog(list: ArrayList<String>) {
        showDialog(list, 766)
    }

    private fun showSevenCDialog(list: ArrayList<String>) {
        showDialog(list, 767)
    }

    private fun showChangeSlotDialog(list: ArrayList<String>) {
        showDialog(list, 401)
    }

    private fun showListSlotDialog(list: ArrayList<String>) {
        showDialog(list, 681)
    }

    private fun showSeventeenDialog(list: ArrayList<String>) {
        showDialog(list, 17)
    }

    private fun showEighteenDialog(list: ArrayList<String>) {
        showDialog(list, 18)
    }

    private fun showNineteenDialog(list: ArrayList<String>) {
        showDialog(list, 19)
    }

    private fun showThirteenDialog(list: ArrayList<String>) {
        showDialog(list, 13)
    }

    private fun showTwentyThreeDialog(list: ArrayList<String>) {
        showDialog(list, 23)
    }

    private fun showFourteenDialog(list: ArrayList<String>) {
        showDialog(list, 14)
    }

    private fun showFifteenDialog(list: ArrayList<String>) {
        showDialog(list, 15)
    }

    private fun showTwentyDialog(list: ArrayList<String>) {
        showDialog(list, 20)
    }

    private fun showTwentyOneDialog(list: ArrayList<String>) {
        showDialog(list, 21)
    }

    private fun showEightyTwoDialog(list: ArrayList<String>) {
        showDialog(list, 82)
    }

    private fun showTwentyTwoDialog(list: ArrayList<String>) {
        showDialog(list, 22)
    }

    private fun showDialog(arrayList: ArrayList<String>, tag: Int) {
        dialog = PutawayCustomDialogFragment(context, arrayList, tag)
        (dialog as PutawayCustomDialogFragment).show(supportFragmentManager, "Test dialog")
        isDialogOpen = true
    }

    private fun updateSwitch() {
        val value = getStringPrefernce("drawer_switch")
        updateSwitchFab(value)
    }

    fun moveToNextFragment(fragment: Fragment) {
        // Get the support fragment manager instance
        currebtFragment = fragment
        setCurrentFragmment(currebtFragment!!)

        val manager = supportFragmentManager
        // Begin the fragment transition using support fragment manager
        val transaction = manager.beginTransaction()
        // Replace the fragment on container
        transaction.replace(R.id.putawaycontainer, fragment)
        // add with tag fragment Name
        transaction.addToBackStack(fragment.javaClass.name)
        // Finishing the transition
        transaction.commit()
    }

    override fun onNextInteraction(tag: Int, value: String, data: ArrayMap<String, String>?) {
        paramMap = data
        when (tag) {
            1 -> oneFragmentTransaction(paramMap!!, value)
            2 -> twoFragmentTransaction(paramMap!!, value)
            3 -> threeSFragmentTransaction(paramMap!!, value)
            4 -> fourFragmentTransaction(paramMap!!, value)
            5 -> fiveFragmentTransaction(paramMap!!, value)
            6 -> sixFragmentTransaction(paramMap!!, value)
            7 -> sevenFragmentTransaction(paramMap!!, value)
            8 -> eightFragmentTransaction(paramMap!!, value)
            965 -> nineAFragmentTransaction(paramMap!!, value)
            9 -> nineFragmentTransaction(paramMap!!, value)
            10 -> tenFragmentTransaction(paramMap!!, value)
            11 -> elevenFragmentTransaction(paramMap!!, value)
            12 -> twelveFragmentTransaction(paramMap!!, value)
        }
    }

    override fun onClickSwitch(data: String) {
        Log.d("xx", "data is " + data)
        updateSwitchFab(data)
    }

    private fun fiveFragmentTransaction(paramMap: ArrayMap<String, String>, value: String) {
        when (value) {
            resources.getString(R.string.next) -> callProgramForFiveFragment(
                paramMap,
                "00000000000000000000000",
                ""
            )
            resources.getString(R.string.cancel) -> callProgramForFiveFragment(
                paramMap,
                "00000000000100000000000",
                ""
            )
            /* resources.getString(R.string.cancel) -> moveToNextFragment(
                 ThreeS_PutawayFragment.newInstance(
                     "",
                     ""
                 )
             )*/
            else -> {
                showSnack(resources.getString(R.string.wrong_scan))
            }
        }
    }

    private fun threeSFragmentTransaction(paramMap: ArrayMap<String, String>, value: String) {
        when (value) {
            resources.getString(R.string.next) -> callProgramForThreeFragment(
                paramMap,
                "000000000000000000000000",
                ""
            )
            resources.getString(R.string.cancel) -> callProgramForThreeFragment(
                paramMap,
                "000000000001000000000000",
                ""
            )
            resources.getString(R.string.stage) -> callProgramForThreeFragment(
                paramMap,
                "000000001000000000000000", ""
            )
            resources.getString(R.string.nextpck) -> callProgramForThreeFragment(
                paramMap,
                "000000000010000000000000", ""
            )
            resources.getString(R.string.slots) -> callProgramForThreeFragment(
                paramMap,
                "000000010000000000000000", ""
            )
            resources.getString(R.string.ovr_warn) -> callProgramForThreeFragment(
                paramMap,
                "000000000000000000010000", ""
            )
            else -> {
                showSnack(resources.getString(R.string.wrong_scan))
            }
        }
    }

    private fun oneFragmentTransaction(paramMap: ArrayMap<String, String>, value: String) {
        when (value) {
            resources.getString(R.string.next) -> callProgramForOneFragment(
                paramMap,
                "00000000000000000000000",
                ""
            )
            resources.getString(R.string.cancel) -> callProgramForOneFragment(
                paramMap,
                "00100000000000000000000",
                ""
            )
            else -> {
                showSnack(resources.getString(R.string.wrong_scan))
            }
        }
    }

    private fun twoFragmentTransaction(paramMap: ArrayMap<String, String>, value: String) {
        when (value) {
            resources.getString(R.string.next) -> callProgramForTwoFragment(
                paramMap,
                "00000000000000000000000",
                ""
            )
            resources.getString(R.string.cancel) -> /*finish()*/callProgramForTwoFragment(
                paramMap,
                "00000000000100000000000",
                ""
            )
            else -> {
                showSnack(resources.getString(R.string.wrong_scan))
            }
        }
    }

    private fun fourFragmentTransaction(paramMap: ArrayMap<String, String>, value: String) {
        when (value) {
            resources.getString(R.string.next) -> callProgramForFourFragment(
                paramMap, "00000000000000000000000",
                ""
            )
            resources.getString(R.string.cancel) -> callProgramForSixFragment(
                paramMap,
                "0000000000010000000000",
                ""
            )
            else -> {
                showSnack(resources.getString(R.string.wrong_scan))
            }
        }
    }

    private fun sixFragmentTransaction(paramMap: ArrayMap<String, String>, value: String) {
        when (value) {
            resources.getString(R.string.next) -> callProgramForSixFragment(
                paramMap,
                "00000000000000000000000",
                ""
            )
            resources.getString(R.string.skip) -> callProgramForSixFragment(
                paramMap,
                "00000000100000000000000",
                ""
            )
            resources.getString(R.string.cancel) -> callProgramForSixFragment(
                paramMap,
                "0000000000010000000000",
                ""
            )
            resources.getString(R.string.exit) -> callProgramForSixFragment(
                paramMap,
                "0010000000000000000000",
                ""
            )
            else -> {
                showSnack(resources.getString(R.string.wrong_scan))
            }
        }
    }

    private fun sevenFragmentTransaction(paramMap: ArrayMap<String, String>, value: String) {
        when (value) {
            resources.getString(R.string.next) -> callProgramForSevenFragment(
                paramMap,
                "00000000000000000000000",
                ""
            )
            resources.getString(R.string.cancel) -> callProgramForSevenFragment(
                paramMap,
                "00000000000100000000000",
                ""
            )
            else -> {
                showSnack(resources.getString(R.string.wrong_scan))
            }
        }
    }

    private fun eightFragmentTransaction(paramMap: ArrayMap<String, String>, value: String) {
        when (value) {
            resources.getString(R.string.next) -> callProgramForEightFragment(
                paramMap,
                "00000000000000000000000",
                ""
            )
            resources.getString(R.string.cancel) -> callProgramForEightFragment(
                paramMap,
                "00000000000100000000000",
                ""
            )
            else -> {
                showSnack(resources.getString(R.string.wrong_scan))
            }
        }
    }

    private fun nineFragmentTransaction(paramMap: ArrayMap<String, String>, value: String) {
        when (value) {
            resources.getString(R.string.next) -> callProgramForNineFragment(
                paramMap,
                "00000000000000000000000",
                ""
            )
            resources.getString(R.string.cancel) -> callProgramForNineFragment(
                paramMap,
                "00000000000100000000000",
                ""
            )
            resources.getString(R.string.exit) -> callProgramForNineFragment(
                paramMap,
                "00100000000000000000000",
                ""
            )
            else -> {
                showSnack(resources.getString(R.string.wrong_scan))
            }
        }
    }

    private fun nineAFragmentTransaction(paramMap: ArrayMap<String, String>, value: String) {
        when (value) {
            resources.getString(R.string.next) -> callProgramForNineAFragment(
                paramMap,
                "00000000000000000000000",
                ""
            )
            resources.getString(R.string.cancel) -> callProgramForNineAFragment(
                paramMap,
                "00000000000100000000000",
                ""
            )
            else -> {
                showSnack(resources.getString(R.string.wrong_scan))
            }
        }
    }

    private fun tenFragmentTransaction(paramMap: ArrayMap<String, String>, value: String) {
        when (value) {
            resources.getString(R.string.next) -> callProgramForTenFragment(
                paramMap,
                "00000000000000000000000",
                ""
            )
            resources.getString(R.string.cancel) -> callProgramForTenFragment(
                paramMap,
                "00000000000100000000000",
                ""
            )
            else -> {
                showSnack(resources.getString(R.string.wrong_scan))
            }
        }
    }

    private fun elevenFragmentTransaction(paramMap: ArrayMap<String, String>, value: String) {
        when (value) {
            resources.getString(R.string.next) -> callProgramForElevenFragment(
                paramMap,
                "00000000000000000000000",
                ""
            )
            resources.getString(R.string.cancel) -> callProgramForElevenFragment(
                paramMap,
                "00000000000100000000000",
                ""
            )
            else -> {
                showSnack(resources.getString(R.string.wrong_scan))
            }
        }
    }

    private fun twelveFragmentTransaction(paramMap: ArrayMap<String, String>, value: String) {
        when (value) {
            resources.getString(R.string.next) -> ""
            resources.getString(R.string.task) -> ""
            resources.getString(R.string.cancel) -> ""
            else -> {
                showSnack(resources.getString(R.string.wrong_scan))
            }
        }
    }

    private fun callProgramForOneFragment(
        paramMap: ArrayMap<String, String>?,
        btnValue: String,
        inputDialog: String
    ) {

        Log.d("xx", "Base Url Is " + baseUrl);
        showLoading()
        val jObj = JsonObject()
        val jObj1 = JsonObject()
        val jObj2 = JsonObject()

        jObj1.addProperty("app", APP_NAME)
//        jObj1.addProperty("apkver", "" + APP_VER)
        jObj1.addProperty("user", "" + userName)
        jObj1.addProperty("uid", "" + uId)
        jObj1.addProperty("whereFrom", "RC200.01")
        jObj1.addProperty("whouse", "" + WHOUSE)

        jObj2.addProperty("w1lcns", "" + paramMap?.get("w1lcns").toString())
        jObj2.addProperty("w1whse", "" + WHOUSE)
        jObj2.addProperty("w1lbl#b", "" + paramMap?.get("w1lbl#b").toString())
        jObj2.addProperty("height", "" + paramMap?.get("height").toString())
        jObj2.addProperty("distance", "" + paramMap?.get("distance").toString())
        jObj2.addProperty("rfId", "" + paramMap?.get("Rfid_Tag").toString())

        jObj2.addProperty("f1f24flags", btnValue) // level 1
        jObj2.addProperty("btnflags", "0000000000")


        jObj.add("Std", jObj1) // level 0
        jObj.add("Input", jObj2)
        val json = Gson().toJson(jObj)
        Log.d("xx", "One api json is: " + json)

        if (myLogFile != null) {
            Methods.writeLogsInFile(myLogFile!!, getDate() + ": Req :" + json)
        }
        disposable = appApiServe.genericApiCall(jObj)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                Log.d(TAG, "PTA01PR data is:- " + result)
                appResponseMapper(result.toString())

            }, { error ->
                hideLoading()
                ResponseMapper.errorHandler(applicationContext, error)

            })
    }

    private fun callProgramForTwoFragment(
        paramMap: ArrayMap<String, String>?,
        btnValue: String,
        inputDialog: String
    ) {

//        Log.d("xx", "Base Url Is " + baseUrl);
        showLoading()
        val jObj = JsonObject()
        val jObj1 = JsonObject()
        val jObj2 = JsonObject()

        jObj1.addProperty("app", APP_NAME)
//        jObj1.addProperty("apkver", "" + APP_VER)
        jObj1.addProperty("user", "" + userName)
        jObj1.addProperty("uid", "" + uId)
        jObj1.addProperty("whereFrom", "RC200.02")
        jObj1.addProperty("whouse", "" + WHOUSE)

        jObj2.addProperty("w2scan", "" + paramMap?.get("scanSlot").toString())
        jObj2.addProperty("height", "" + paramMap?.get("height").toString())
        jObj2.addProperty("distance", "" + paramMap?.get("distance").toString())
        jObj2.addProperty("rfId", "" + paramMap?.get("Rfid_Tag").toString())

        jObj2.addProperty("f1f24flags", btnValue) // level 1
        jObj2.addProperty("btnflags", "0000000000")

        jObj.add("Std", jObj1) // level 0
        jObj.add("Input", jObj2)
        val json = Gson().toJson(jObj)
        Log.d("xx", "2 api json is: " + json)
        if (myLogFile != null) {
            Methods.writeLogsInFile(myLogFile!!, getDate() + ": Req: " + json)
        }


        disposable = appApiServe.genericApiCall(jObj)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                Log.d(TAG, "2 api data is:- " + result)
                appResponseMapper(result.toString())

            }, { error ->
                hideLoading()
                ResponseMapper.errorHandler(applicationContext, error)

            })
    }


    private fun callProgramForThreeFragment(
        paramMap: ArrayMap<String, String>?,
        btnValue: String,
        inputDialog: String
    ) {
        showLoading()
        val jObj = JsonObject()
        val jObj1 = JsonObject()
        val jObj2 = JsonObject()

        jObj1.addProperty("app", APP_NAME)
//        jObj1.addProperty("apkver", "" + APP_VER)
        jObj1.addProperty("user", "" + userName)
        jObj1.addProperty("uid", "" + uId)
        jObj1.addProperty("whereFrom", "RC200.03")
        jObj1.addProperty("whouse", "" + WHOUSE)

        jObj2.addProperty("w3qty", "" + paramMap?.get("quantity").toString())
        jObj2.addProperty("w3scan", "" + paramMap?.get("scanSlot").toString())
        jObj2.addProperty("height", "" + paramMap?.get("height").toString())
        jObj2.addProperty("distance", "" + paramMap?.get("distance").toString())
        jObj2.addProperty("rfId", "" + paramMap?.get("Rfid_Tag").toString())

        jObj2.addProperty("f1f24flags", btnValue) // level 1
        jObj2.addProperty("btnflags", "0000000000")

        jObj.add("Std", jObj1) // level 0
        jObj.add("Input", jObj2)
        val json = Gson().toJson(jObj)
        Log.d("xx", "3 api json is: " + json)
        if (myLogFile != null) {
            Methods.writeLogsInFile(myLogFile!!, getDate() + ": Req: " + json)
        }

        disposable = appApiServe.genericApiCall(jObj)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                Log.d(TAG, "3 api data is:- " + result)

                appResponseMapper(result.toString())
            }, { error ->
                hideLoading()
                ResponseMapper.errorHandler(applicationContext, error)

            })
    }


    private fun callProgramForFourFragment(
        paramMap: ArrayMap<String, String>?,
        btnValue: String,
        inputDialog: String
    ) {
        showLoading()
        val jObj = JsonObject()
        val jObj1 = JsonObject()
        val jObj2 = JsonObject()

        jObj1.addProperty("app", APP_NAME)
//        jObj1.addProperty("apkver", "" + APP_VER)
        jObj1.addProperty("user", "" + userName)
        jObj1.addProperty("uid", "" + uId)
        jObj1.addProperty("whereFrom", "RC200.04")
        jObj1.addProperty("whouse", "" + WHOUSE)

        jObj2.addProperty("w4qty", "" + paramMap?.get("quantity").toString())
        jObj2.addProperty("f1f24flags", "00000000000000000000000") // level 1
        jObj2.addProperty("btnflags", "0000000000")

        jObj.add("Std", jObj1) // level 0
        jObj.add("Input", jObj2)
        val json = Gson().toJson(jObj)
        Log.d("xx", "4 api json is: " + json)
        if (myLogFile != null) {
            Methods.writeLogsInFile(myLogFile!!, getDate() + ": Req: " + json)
        }

        disposable = appApiServe.genericApiCall(jObj)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                Log.d(TAG, "4 api data is:- " + result)
                appResponseMapper(result.toString())

            }, { error ->
                hideLoading()
                ResponseMapper.errorHandler(applicationContext, error)

            })
    }


    private fun callProgramForFiveFragment(
        paramMap: ArrayMap<String, String>?,
        btnValue: String,
        inputDialog: String
    ) {

        showLoading()
        val jObj = JsonObject()
        val jObj1 = JsonObject()
        val jObj2 = JsonObject()

        jObj1.addProperty("app", APP_NAME)
//        jObj1.addProperty("apkver", "" + APP_VER)
        jObj1.addProperty("user", "" + userName)
        jObj1.addProperty("uid", "" + uId)
        jObj1.addProperty("whereFrom", "RC200.05")
        jObj1.addProperty("whouse", "" + WHOUSE)

        jObj2.addProperty("w5lcns", "" + paramMap?.get("w5lcns").toString())
        jObj2.addProperty("w5lbl#b", "" + paramMap?.get("w5lbl#b").toString())

        jObj2.addProperty("f1f24flags", btnValue) // level 1
        jObj2.addProperty("btnflags", "0000000000")

        jObj.add("Std", jObj1) // level 0
        jObj.add("Input", jObj2)
        val json = Gson().toJson(jObj)
        Log.d("xx", "5 api json is: " + json)
        if (myLogFile != null) {
            Methods.writeLogsInFile(myLogFile!!, getDate() + ": Req: " + json)
        }


        disposable = appApiServe.genericApiCall(jObj)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                Log.d(TAG, "5 api data is:- " + result)
                appResponseMapper(result.toString())

            }, { error ->
                hideLoading()
                ResponseMapper.errorHandler(applicationContext, error)

            })
    }

    private fun callProgramForSixFragment(
        paramMap: ArrayMap<String, String>?,
        btnValue: String,
        inputDialog: String
    ) {

        showLoading()
        val jObj = JsonObject()
        val jObj1 = JsonObject()
        val jObj2 = JsonObject()

        jObj1.addProperty("app", APP_NAME)
//        jObj1.addProperty("apkver", "" + APP_VER)
        jObj1.addProperty("user", "" + userName)
        jObj1.addProperty("uid", "" + uId)
        jObj1.addProperty("whereFrom", "RC200.06")
        jObj1.addProperty("whouse", "" + WHOUSE)

        jObj2.addProperty("w6nxt#", "" + paramMap?.get("w6nxt#").toString())

        jObj2.addProperty("f1f24flags", btnValue) // level 1
        jObj2.addProperty("btnflags", "0000000000")

        jObj.add("Std", jObj1) // level 0
        jObj.add("Input", jObj2)
        val json = Gson().toJson(jObj)
        Log.d("xx", "6 api json is: " + json)
        if (myLogFile != null) {
            Methods.writeLogsInFile(myLogFile!!, getDate() + ": Req: " + json)
        }

        disposable = appApiServe.genericApiCall(jObj)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                Log.d(TAG, "6 api data is:- " + result)
                appResponseMapper(result.toString())

            }, { error ->
                hideLoading()
                ResponseMapper.errorHandler(applicationContext, error)

            })
    }

    private fun callProgramForSevenFragment(
        paramMap: ArrayMap<String, String>?,
        btnValue: String,
        inputDialog: String
    ) {
        showLoading()
        val jObj = JsonObject()
        val jObj1 = JsonObject()
        val jObj2 = JsonObject()

        jObj1.addProperty("app", APP_NAME)
//        jObj1.addProperty("apkver", "" + APP_VER)
        jObj1.addProperty("user", "" + userName)
        jObj1.addProperty("uid", "" + uId)
        jObj1.addProperty("whereFrom", "RC200.07")
        jObj1.addProperty("whouse", "" + WHOUSE)

        jObj2.addProperty("w7scan", "" + paramMap?.get("scanSlot").toString())
//        jObj2.addProperty("w1lbl#b", "" + paramMap?.get("rfId").toString())

        jObj2.addProperty("f1f24flags", btnValue) // level 1
        jObj2.addProperty("btnflags", "0000000000")

        jObj.add("Std", jObj1) // level 0
        jObj.add("Input", jObj2)
        val json = Gson().toJson(jObj)
        Log.d("xx", "7 api json is: " + json)
        if (myLogFile != null) {
            Methods.writeLogsInFile(myLogFile!!, getDate() + ": Req: " + json)
        }


        disposable = appApiServe.genericApiCall(jObj)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                Log.d(TAG, "7 api data is:- " + result)
                appResponseMapper(result.toString())

            }, { error ->
                hideLoading()
                ResponseMapper.errorHandler(applicationContext, error)

            })
    }

    private fun callProgramForEightFragment(
        paramMap: ArrayMap<String, String>?,
        btnValue: String,
        inputDialog: String
    ) {
        showLoading()
        val jObj = JsonObject()
        val jObj1 = JsonObject()
        val jObj2 = JsonObject()

        jObj1.addProperty("app", APP_NAME)
//        jObj1.addProperty("apkver", "" + APP_VER)
        jObj1.addProperty("user", "" + userName)
        jObj1.addProperty("uid", "" + uId)
        jObj1.addProperty("whereFrom", "RC200.08")
        jObj1.addProperty("whouse", "" + WHOUSE)

        jObj2.addProperty("w8scan", "" + paramMap?.get("scanSlot").toString())

        jObj2.addProperty("f1f24flags", btnValue) // level 1
        jObj2.addProperty("btnflags", "0000000000")

        jObj.add("Std", jObj1) // level 0
        jObj.add("Input", jObj2)
        val json = Gson().toJson(jObj)
        Log.d("xx", "8 api json is: " + json)
        if (myLogFile != null) {
            Methods.writeLogsInFile(myLogFile!!, getDate() + ": Req: " + json)
        }


        disposable = appApiServe.genericApiCall(jObj)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                Log.d(TAG, "8 api data is:- " + result)
                appResponseMapper(result.toString())

            }, { error ->
                hideLoading()
                ResponseMapper.errorHandler(applicationContext, error)

            })
    }

    private fun callProgramForNineFragment(
        paramMap: ArrayMap<String, String>?,
        btnValue: String,
        inputDialog: String
    ) {

        showLoading()
        val jObj = JsonObject()
        val jObj1 = JsonObject()
        val jObj2 = JsonObject()

        jObj1.addProperty("app", APP_NAME)
//        jObj1.addProperty("apkver", "" + APP_VER)
        jObj1.addProperty("user", "" + userName)
        jObj1.addProperty("uid", "" + uId)
        jObj1.addProperty("whereFrom", "RC200.09")
        jObj1.addProperty("whouse", "" + WHOUSE)

//        jObj2.addProperty("w1lcns", "" + inputDialog)

        jObj2.addProperty("f1f24flags", "00000000000000000000000") // level 1
        jObj2.addProperty("btnflags", "0000000000")

        jObj.add("Std", jObj1) // level 0
        jObj.add("Input", jObj2)
        val json = Gson().toJson(jObj)
        Log.d("xx", "9 api json is: " + json)
        if (myLogFile != null) {
            Methods.writeLogsInFile(myLogFile!!, getDate() + ": Req: " + json)
        }


        disposable = appApiServe.genericApiCall(jObj)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                Log.d(TAG, "9 api data is:- " + result)
                appResponseMapper(result.toString())

            }, { error ->
                hideLoading()
                ResponseMapper.errorHandler(applicationContext, error)

            })
    }

    private fun callProgramForNineAFragment(
        paramMap: ArrayMap<String, String>?,
        btnValue: String,
        inputDialog: String
    ) {

        showLoading()
        val jObj = JsonObject()
        val jObj1 = JsonObject()
        val jObj2 = JsonObject()

        jObj1.addProperty("app", APP_NAME)
//        jObj1.addProperty("apkver", "" + APP_VER)
        jObj1.addProperty("user", "" + userName)
        jObj1.addProperty("uid", "" + uId)
        jObj1.addProperty("whereFrom", "RC200.09A")
        jObj1.addProperty("whouse", "" + WHOUSE)

        jObj2.addProperty("w9afrom", "" + paramMap?.get("w9afrom").toString())
        jObj2.addProperty("w9atype", "" + paramMap?.get("w9atype").toString())

        jObj2.addProperty("f1f24flags", btnValue) // level 1
        jObj2.addProperty("btnflags", "0000000000")

        jObj.add("Std", jObj1) // level 0
        jObj.add("Input", jObj2)
        val json = Gson().toJson(jObj)
        Log.d("xx", "9A api json is: " + json)
        if (myLogFile != null) {
            Methods.writeLogsInFile(myLogFile!!, getDate() + ": Req: " + json)
        }


        disposable = appApiServe.genericApiCall(jObj)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                Log.d(TAG, "9A api data is:- " + result)
                appResponseMapper(result.toString())

            }, { error ->
                hideLoading()
                ResponseMapper.errorHandler(applicationContext, error)

            })
    }

    private fun callProgramForTenFragment(
        paramMap: ArrayMap<String, String>?,
        btnValue: String,
        inputDialog: String
    ) {
        showLoading()
        val jObj = JsonObject()
        val jObj1 = JsonObject()
        val jObj2 = JsonObject()

        jObj1.addProperty("app", APP_NAME)
//        jObj1.addProperty("apkver", "" + APP_VER)
        jObj1.addProperty("user", "" + userName)
        jObj1.addProperty("uid", "" + uId)
        jObj1.addProperty("whereFrom", "RC200.10")
        jObj1.addProperty("whouse", "" + WHOUSE)

        jObj2.addProperty("w10aisle", "" + paramMap?.get("w10aisle").toString())
//        jObj2.addProperty("w1lbl#b", "" + paramMap?.get("rfId").toString())

        jObj2.addProperty("f1f24flags", btnValue) // level 1
        jObj2.addProperty("btnflags", "0000000000")

        jObj.add("Std", jObj1) // level 0
        jObj.add("Input", jObj2)
        val json = Gson().toJson(jObj)
        Log.d("xx", "10 api json is: " + json)
        if (myLogFile != null) {
            Methods.writeLogsInFile(myLogFile!!, getDate() + ": Req: " + json)
        }


        disposable = appApiServe.genericApiCall(jObj)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                Log.d(TAG, "10 api data is:- " + result)
                appResponseMapper(result.toString())

            }, { error ->
                hideLoading()
                ResponseMapper.errorHandler(applicationContext, error)

            })
    }

    private fun callProgramForElevenFragment(
        paramMap: ArrayMap<String, String>?,
        btnValue: String,
        inputDialog: String
    ) {
        showLoading()
        val jObj = JsonObject()
        val jObj1 = JsonObject()
        val jObj2 = JsonObject()

        jObj1.addProperty("app", APP_NAME)
//        jObj1.addProperty("apkver", "" + APP_VER)
        jObj1.addProperty("user", "" + userName)
        jObj1.addProperty("uid", "" + uId)
        jObj1.addProperty("whereFrom", "RC200.11")
        jObj1.addProperty("whouse", "" + WHOUSE)

        jObj2.addProperty("w11slot1s", "" + paramMap?.get("w11slot1s").toString())
        jObj2.addProperty("w11slot1", "" + paramMap?.get("w11slot1").toString())
        jObj2.addProperty("w11slot1q", "" + paramMap?.get("w11slot1q").toString())
        jObj2.addProperty("w11slot2s", "" + paramMap?.get("w11slot2s").toString())
        jObj2.addProperty("w11slot2", "" + paramMap?.get("w11slot2").toString())
        jObj2.addProperty("w11slot2q", "" + paramMap?.get("w11slot2q").toString())
        jObj2.addProperty("w11slot3s", "" + paramMap?.get("w11slot3s").toString())
        jObj2.addProperty("w11slot3", "" + paramMap?.get("w11slot3").toString())
        jObj2.addProperty("w11slot3q", "" + paramMap?.get("w11slot3q").toString())
        jObj2.addProperty("w11slot4s", "" + paramMap?.get("w11slot4s").toString())
        jObj2.addProperty("w11slot4", "" + paramMap?.get("w11slot4").toString())
        jObj2.addProperty("w11slot4q", "" + paramMap?.get("w11slot4q").toString())


        jObj2.addProperty("f1f24flags", btnValue) // level 1
        jObj2.addProperty("btnflags", "0000000000")

        jObj.add("Std", jObj1) // level 0
        jObj.add("Input", jObj2)
        val json = Gson().toJson(jObj)
        Log.d("xx", "11 api json is: " + json)
        if (myLogFile != null) {
            Methods.writeLogsInFile(myLogFile!!, getDate() + ": Req: " + json)
        }


        disposable = appApiServe.genericApiCall(jObj)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                Log.d(TAG, "11 api data is:- " + result)
                appResponseMapper(result.toString())

            }, { error ->
                hideLoading()
                ResponseMapper.errorHandler(applicationContext, error)

            })
    }

    private fun setButtonsVisibilty(
        toShowCancelButton: Boolean,
        toShowNextButton: Boolean,
        toShowSkipButton: Boolean,
    ) {
        if (toShowCancelButton) {
            binding.putawayllCancel.visibility = View.VISIBLE
        } else {
            binding.putawayllCancel.visibility = View.GONE
        }

        if (toShowSkipButton) {
            binding.putawayllSkip.visibility = View.VISIBLE
        } else {
            binding.putawayllSkip.visibility = View.GONE
        }

        if (toShowNextButton) {
            binding.putawayllNext.visibility = View.VISIBLE
        } else {
            binding.putawayllNext.visibility = View.GONE
        }
    }

    private fun appResponseMapper(output: String) {
        AppData.setData(output)
        if (myLogFile != null) {
            Methods.writeLogsInFile(myLogFile!!, getDate() + ": Res: " + output)
        }
        val mMineUserEntity = dataParsing(output)
        if (mMineUserEntity != null && !mMineUserEntity.equals("")) {
            userName = mMineUserEntity.Std.user
            uId = mMineUserEntity.Std.uid

            if (mMineUserEntity.Output != null) {
                var screenName = mMineUserEntity.Output.nxtscr

                if (!mMineUserEntity.Output.errmsg.isNullOrEmpty()) {
                    activityBaseBinding.layoutContainer.tv_errorMsg?.text =
                        mMineUserEntity.Output.errmsg
                } else {
                    activityBaseBinding.layoutContainer.tv_errorMsg?.text = ""
                }

                hideLoading()
                if (screenName == "RC200.01S") {
                    WHOUSE = mMineUserEntity.Output.w1whse
                    /* tv_cancel?.text = resources.getString(R.string.exit)
                     tv_next?.text = resources.getString(R.string.next)*/
                    setButtonsVisibilty(true, true, false)
                    if (RFIDDataReceiver.isConnectedToRFID) {
                        moveToNextFragment(OneRfidPutawayFragment.newInstance("", ""))
                    } else {
                        moveToNextFragment(OnePutawayFragment.newInstance("", ""))
                    }
                } else if (screenName == "RC200.02S") {
                    /*    tv_cancel?.text = resources.getString(R.string.cancel)
                        tv_next?.text = resources.getString(R.string.next)*/
                    setButtonsVisibilty(true, true, false)
                    if (RFIDDataReceiver.isConnectedToRFID) {
                        moveToNextFragment(TwoRfidPutawayFragment.newInstance("", ""))
                    } else {
                        moveToNextFragment(TwoPutawayFragment.newInstance("", ""))
                    }
                } else if (screenName == "RC200.03S") {
                    /*  tv_cancel?.text = resources.getString(R.string.cancel)
                      tv_next?.text = resources.getString(R.string.next)
                      tv_skip?.text = resources.getString(R.string.nextpck)*/
                    setButtonsVisibilty(true, true, false)
                    if (RFIDDataReceiver.isConnectedToRFID) {
                        moveToNextFragment(ThreeRfidPutawayFragment.newInstance("", ""))
                    } else {
                        moveToNextFragment(ThreeS_PutawayFragment.newInstance("", ""))
                    }
                } else if (screenName == "RC200.04S") {
                    /*    tv_cancel?.text = resources.getString(R.string.cancel)
                        tv_next?.text = resources.getString(R.string.next)*/
                    setButtonsVisibilty(true, true, false)
                    moveToNextFragment(FourPutawayFragment.newInstance("", ""))
                } else if (screenName == "RC200.05S") {
                    /* tv_cancel?.text = resources.getString(R.string.cancel)
                     tv_next?.text = resources.getString(R.string.next)*/
                    setButtonsVisibilty(true, true, false)
                    moveToNextFragment(FivePutawayFragment.newInstance("", ""))
                } else if (screenName == "RC200.06S") {
                    /* tv_cancel?.text = resources.getString(R.string.cancel)
                     tv_next?.text = resources.getString(R.string.next)*/
                    setButtonsVisibilty(true, true, false)
                    moveToNextFragment(SixPutawayFragment.newInstance("", ""))
                } else if (screenName == "RC200.07S") {
                    /*     tv_cancel?.text = resources.getString(R.string.cancel)
                         tv_next?.text = resources.getString(R.string.next)*/
                    setButtonsVisibilty(true, true, false)
                    moveToNextFragment(SevenPutawayFragment.newInstance("", ""))
                } else if (screenName == "RC200.08S") {
                    /*     tv_cancel?.text = resources.getString(R.string.cancel)
                         tv_next?.text = resources.getString(R.string.next)*/
                    setButtonsVisibilty(true, true, false)
                    moveToNextFragment(EightPutawayFragment.newInstance("", ""))
                } else if (screenName == "RC200.09S") {
                    /*tv_cancel?.text = resources.getString(R.string.exit)
                    tv_next?.text = resources.getString(R.string.accept)*/
                    setButtonsVisibilty(true, true, false)
                    moveToNextFragment(NinePutawayFragment.newInstance("", ""))
                } else if (screenName == "RC200.09AS") {
                    /*  tv_cancel?.text = resources.getString(R.string.cancel)
                      tv_next?.text = resources.getString(R.string.next)*/
                    setButtonsVisibilty(true, true, false)
                    moveToNextFragment(NineAPutawayFragment.newInstance("", ""))
                } else if (screenName == "RC200.10S") {
                    /*  tv_cancel?.text = resources.getString(R.string.cancel)
                      tv_next?.text = resources.getString(R.string.next)*/
                    setButtonsVisibilty(true, true, false)
                    moveToNextFragment(TenPutawayFragment.newInstance("", ""))
                } else if (screenName == "RC200.11S") {
                    /*   tv_cancel?.text = resources.getString(R.string.cancel)
                       tv_next?.text = resources.getString(R.string.next)*/
                    setButtonsVisibilty(true, true, false)
                    moveToNextFragment(ElevenPutawayFragment.newInstance("", ""))
                } else if (screenName == "LOGOUT") {
                    finish()
                }
            }
        }
    }

    override fun onClick(v: View?) {
        v?.let { avoidDoubleClicks(it) }
        when (v?.id) {
            R.id.putawayll_next -> {
                onNextButtonPress()
                closeFABMenu()
            }
            R.id.putawayll_cancel -> {
                onCancelButtonPress()
                closeFABMenu()
            }
            R.id.putawayll_skip -> {
                onSkipButtonPress()
                closeFABMenu()
            }
        }
    }


    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result = tts?.setLanguage(Locale.US)
            tts?.setPitch(1.3f)
            tts?.setSpeechRate(1f)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language specified is not supported!")
            } else {
                //                buttonSpeak!!.isEnabled = true
                Log.e("TTS", "Initilization Done TTS!")
            }
        } else {
            Log.e("TTS", "Initilization Failed!")
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
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
//            unregisterReceiver(receiver)
//            unregisterReceiver(mReceiver)
//            printer.disconnectBluetoothConnection()
    }

    fun speakOut(text: String) {
        if (text != null) {
            if (!text.equals("")) {
                tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
            }
        }
    }

    fun speakOutString(text: String) {
        val data = text.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (i in data.indices) {
            if (data[i].equals("")) {
                continue
            }
            val isNumber = data[i].substring(0, 1).matches("\\d".toRegex())
            if (isNumber) {
                val charSequence = data[i].toCharArray()
                for (k in charSequence.indices) {
                    tts!!.speak(charSequence[k].toString(), TextToSpeech.QUEUE_ADD, null)
                }
            } else {
                tts!!.speak(data[i], TextToSpeech.QUEUE_ADD, null)
            }
        }
    }

//    private fun openInputDialog(titleName: String) {
//        //Inflate the dialog with custom view
//        val mDialogView = LayoutInflater.from(this).inflate(R.layout.prompts_dialog, null)
//        var textView1: TextView = mDialogView.findViewById(R.id.textView1) as TextView
//        textView1.setText(titleName)
//        //AlertDialogBuilder
//        val mBuilder = AlertDialog.Builder(this)
//            .setView(mDialogView)
//            // if the dialog is cancelable
//            .setCancelable(false)
//            // positive button text and action
//            .setPositiveButton("Proceed", DialogInterface.OnClickListener { dialog, id ->
//
//                val inputDialog = mDialogView.editTextDialogUserInput.text.toString()
//                Log.d(TAG, "License number is:- " + inputDialog)
//
//                if (currebtFragment is OnePutawayFragment) {
//                    Log.d(TAG, "Manual Putaway mode for " + inputDialog + " started")
//                    var mMap = ArrayMap<String, String>().apply {
//                        put("rfId", "" + "")
//                    }
//                    callProgramForOneFragment(mMap, "", inputDialog)
//                } else if (currebtFragment is ThreeS_PutawayFragment) {
//                    Log.d(TAG, "Manual Putaway mode for " + inputDialog + " started")
//                    var mMap = ArrayMap<String, String>().apply {
//                        put("rfId", "" + "")
//                        put("quantity", "" + ThreeS_PutawayFragment.quantity)
//                        put("scanSlot", "")
//                    }
//
//                    callProgramForThreeFragment(mMap, "", inputDialog)
//                }
//
//                dialog.cancel()
//            })
//            // negative button text and action
//            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
//                dialog.cancel()
//            })
//
//        // create dialog box
//        val alert = mBuilder.create()
//        // set title for alert dialog box
//        alert.setTitle("Manual Mode")
//        // show alert dialog
//        alert.show()
//    }

//    private fun uploadFileOnServer() {
//        showLoading()
//        val file = File(MyPersonalApp.FILE_DESTINATION, MyPersonalApp.FILE_NAME)
//
//
//        val fileURI: Uri = FileProvider.getUriForFile(
//            this, this.getApplicationContext()
//                .getPackageName() + ".provider", file
//        )
//
//        if (MyPersonalApp.process != null) {
//            MyPersonalApp.process.destroy()
//        }
//
//        // create RequestBody instance from file
//        val requestFile: RequestBody =
//            RequestBody.create(
//                MediaType.parse(getContentResolver().getType(fileURI)),
//                file
//            )
//
//        // MultipartBody.Part is used to send also the actual file name
//        val body: MultipartBody.Part =
//            MultipartBody.Part.createFormData("file", file.getName(), requestFile)
//
//        disposable = appApiServe.uploadFile(body)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ result ->
//                hideLoading()
//                MyPersonalApp.process = Runtime.getRuntime().exec("logcat -c")
//                MyPersonalApp.process =
//                    Runtime.getRuntime().exec("logcat -f ${MyPersonalApp.logFile}")
//                Log.d("xx", "response is:- " + result.output)
//                //finish()
//            }, { error ->
//                hideLoading()
//                MyPersonalApp.process = Runtime.getRuntime().exec("logcat -c")
//                MyPersonalApp.process =
//                    Runtime.getRuntime().exec("logcat -f ${MyPersonalApp.logFile}")
//                //finish()
//                ResponseMapper.errorHandler(applicationContext, error)
//
//            })
//    }

    private fun onNextButtonPress() {
        if (myLogFile != null) {
            Methods.writeLogsInFile(myLogFile!!, getDate() + " : Next Button Press")
        }
        if (currebtFragment is OnePutawayFragment) {
            (currebtFragment as OnePutawayFragment).onNextClick(resources.getString(R.string.next))
            //openInputDialog("Enter License number :")
        } else if (currebtFragment is ThreeS_PutawayFragment) {
            (currebtFragment as ThreeS_PutawayFragment).cancelAsyncTask()
            //openInputDialog("Enter Slot number :")
            (currebtFragment as ThreeS_PutawayFragment).onNextClick(resources.getString(R.string.next))
        } else if (currebtFragment is FivePutawayFragment) {
            (currebtFragment as FivePutawayFragment).onNextClick(resources.getString(R.string.next))
        } else if (currebtFragment is TwoPutawayFragment) {
            (currebtFragment as TwoPutawayFragment).onNextClick(resources.getString(R.string.next))
        } else if (currebtFragment is FourPutawayFragment) {
            (currebtFragment as FourPutawayFragment).onNextClick(resources.getString(R.string.next))
        } else if (currebtFragment is SixPutawayFragment) {
            (currebtFragment as SixPutawayFragment).onNextClick(resources.getString(R.string.next))
        } else if (currebtFragment is SevenPutawayFragment) {
            (currebtFragment as SevenPutawayFragment).onNextClick(resources.getString(R.string.next))
        } else if (currebtFragment is EightPutawayFragment) {
            (currebtFragment as EightPutawayFragment).onNextClick(resources.getString(R.string.next))
        } else if (currebtFragment is NinePutawayFragment) {
            (currebtFragment as NinePutawayFragment).onNextClick(resources.getString(R.string.next))
        } else if (currebtFragment is NineAPutawayFragment) {
            (currebtFragment as NineAPutawayFragment).onNextClick(resources.getString(R.string.next))
        } else if (currebtFragment is TenPutawayFragment) {
            (currebtFragment as TenPutawayFragment).onNextClick(resources.getString(R.string.next))
        } else if (currebtFragment is ElevenPutawayFragment) {
            (currebtFragment as ElevenPutawayFragment).onNextClick(resources.getString(R.string.next))
        } else if (currebtFragment is TwelvePutawayFragment) {
            (currebtFragment as TwelvePutawayFragment).onNextClick(resources.getString(R.string.next))
        } else if (currebtFragment is OneRfidPutawayFragment) {
            (currebtFragment as OneRfidPutawayFragment).onNextClick(resources.getString(R.string.next))
        } else if (currebtFragment is TwoRfidPutawayFragment) {
            (currebtFragment as TwoRfidPutawayFragment).onNextClick(resources.getString(R.string.next))
        } else if (currebtFragment is ThreeRfidPutawayFragment) {
            (currebtFragment as ThreeRfidPutawayFragment).onNextClick(resources.getString(R.string.next))
        }
    }

    private fun onSkipButtonPress() {
        if (myLogFile != null) {
            Methods.writeLogsInFile(myLogFile!!, getDate() + " : Skip Button Press")
        }
        if (currebtFragment is SixPutawayFragment) {
            (currebtFragment as SixPutawayFragment).onNextClick(resources.getString(R.string.skip))
        } else if (currebtFragment is ThreeS_PutawayFragment) {
            (currebtFragment as ThreeS_PutawayFragment).onNextClick(resources.getString(R.string.skip))
        }
    }

    private fun onCancelButtonPress() {
        if (myLogFile != null) {
            Methods.writeLogsInFile(myLogFile!!, getDate() + " : Cancel Button Press")
        }
        if (currebtFragment is OnePutawayFragment) {
            //uploadFileOnServer()
            (currebtFragment as OnePutawayFragment).onNextClick(resources.getString(R.string.cancel))
        } else if (currebtFragment is ThreeS_PutawayFragment) {
            (currebtFragment as ThreeS_PutawayFragment).onNextClick(resources.getString(R.string.cancel))
        } else if (currebtFragment is FivePutawayFragment) {
            (currebtFragment as FivePutawayFragment).onNextClick(resources.getString(R.string.cancel))
        } else if (currebtFragment is TwoPutawayFragment) {
            (currebtFragment as TwoPutawayFragment).onNextClick(resources.getString(R.string.cancel))
        } else if (currebtFragment is FourPutawayFragment) {
            (currebtFragment as FourPutawayFragment).onNextClick(resources.getString(R.string.cancel))
        } else if (currebtFragment is SixPutawayFragment) {
            (currebtFragment as SixPutawayFragment).onNextClick(resources.getString(R.string.cancel))
        } else if (currebtFragment is SevenPutawayFragment) {
            (currebtFragment as SevenPutawayFragment).onNextClick(resources.getString(R.string.cancel))
        } else if (currebtFragment is EightPutawayFragment) {
            (currebtFragment as EightPutawayFragment).onNextClick(resources.getString(R.string.cancel))
        } else if (currebtFragment is NinePutawayFragment) {
            (currebtFragment as NinePutawayFragment).onNextClick(resources.getString(R.string.cancel))
        } else if (currebtFragment is NineAPutawayFragment) {
            (currebtFragment as NineAPutawayFragment).onNextClick(resources.getString(R.string.cancel))
        } else if (currebtFragment is TenPutawayFragment) {
            (currebtFragment as TenPutawayFragment).onNextClick(resources.getString(R.string.cancel))
        } else if (currebtFragment is ElevenPutawayFragment) {
            (currebtFragment as ElevenPutawayFragment).onNextClick(resources.getString(R.string.cancel))
        } else if (currebtFragment is TwelvePutawayFragment) {
            (currebtFragment as TwelvePutawayFragment).onNextClick(resources.getString(R.string.cancel))
        } else if (currebtFragment is OneRfidPutawayFragment) {
            (currebtFragment as OneRfidPutawayFragment).onNextClick(resources.getString(R.string.cancel))
        } else if (currebtFragment is TwoRfidPutawayFragment) {
            (currebtFragment as TwoRfidPutawayFragment).onNextClick(resources.getString(R.string.cancel))
        } else if (currebtFragment is ThreeRfidPutawayFragment) {
            (currebtFragment as ThreeRfidPutawayFragment).onNextClick(resources.getString(R.string.cancel))
        }
    }

    // fun for update the status of Drawer's Switch on p3 enable/disable
    @SuppressLint("RestrictedApi")
    fun updateSwitchFab(value: String) {
        if (value.equals("true")) {
            activityBaseBinding.layoutContainer.putawayfab?.visibility = View.VISIBLE
        } else {
            activityBaseBinding.layoutContainer.putawayfab?.visibility = View.INVISIBLE
        }
    }

    override val context: Context
        get() = applicationContext

    override fun clickDialogFragment(value: String, tag: Int) {
        if (paramMap == null) {
            paramMap = ArrayMap<String, String>().apply {
                put("printerId", "")
            }
        }

        when (tag) {
            1 -> oneFragmentTransaction(paramMap!!, value)
            2 -> twoFragmentTransaction(paramMap!!, value)
            3 -> threeSFragmentTransaction(paramMap!!, value)
            4 -> fourFragmentTransaction(paramMap!!, value)
            5 -> fiveFragmentTransaction(paramMap!!, value)
            6 -> sixFragmentTransaction(paramMap!!, value)
            7 -> sevenFragmentTransaction(paramMap!!, value)
            8 -> eightFragmentTransaction(paramMap!!, value)
            965 -> nineAFragmentTransaction(paramMap!!, value)
            9 -> nineFragmentTransaction(paramMap!!, value)
            10 -> tenFragmentTransaction(paramMap!!, value)
            11 -> elevenFragmentTransaction(paramMap!!, value)
            12 -> twelveFragmentTransaction(paramMap!!, value)
            else -> {
                Log.e("xx", "Something Wrong")
            }
        }
    }

//    override fun onScannerDoubleClick() {
//        runOnUiThread {
//            if (DOUBLE_CLICK_ENABLE) {
//                Log.d("xx", "Double click Called")
//                if (myLogFile != null) {
//                    Methods.writeLogsInFile(myLogFile!!, getDate() + ": Double Click Pressed.")
//                }
//                onNextButtonPress()
//                if (myLogFile != null) {
//                    Methods.writeLogsInFile(myLogFile!!, getDate() + ": Exiting Double Click.")
//                }
//            }
//        }
//
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
//
//    override fun onScannedBarcode(barcode: String?, barcodeType: String?) {
//        if (barcode != null) {
//            runOnUiThread {
//                if (DOUBLE_CLICK_ENABLE) {
//                    Log.d("xx", "barcode scanned: " + barcode)
//                    if (myLogFile != null) {
//                        Methods.writeLogsInFile(myLogFile!!, getDate() + ":  Inside Scan Function")
//                    }
//                    if (myLogFile != null) {
//                        Methods.writeLogsInFile(
//                            myLogFile!!,
//                            getDate() + ":  Barcode scanned value is : " + barcode
//                        )
//                    }
//                    if (myLogFile != null) {
//                        Methods.writeLogsInFile(
//                            myLogFile!!,
//                            getDate() + ":  Exiting Scan Function."
//                        )
//                    }
//                    onReceiveScanningData(barcode)
//
//                } else {
//                    Log.d("xx", "Outside beep block else of double click")
//                    beepPlay()
//                }
//            }
//        }
//
//    }

    private fun onReceiveScanningData(decodedData: String) {
        if (isDialogOpen) {
            Log.d("xx", "Inside Dialog open.." + intent)
            Log.d("xx", "data is:- " + decodedData)
            // 12369347435  or {Next /
            (dialog as PutawayCustomDialogFragment).updateScanData(decodedData)
        } else {
            val scanned = decodedData.drop(1)
            when (scanned) {
                resources.getString(R.string.next) -> onNextButtonPress()
                resources.getString(R.string.cancel) -> onCancelButtonPress()
                resources.getString(R.string.skip) -> onSkipButtonPress()
                else -> {
                    displayScanResult(decodedData)
                }
            }
        }
    }

    fun beepPlay() {
        val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 500)
        //  viebrate the phone now
        vibrate()
    }

    // Extension method to vibrate a phone programmatically
    fun Context.vibrate(milliseconds: Long = 500) {
        val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // Check whether device/hardware has a vibrator
        val canVibrate: Boolean = vibrator.hasVibrator()

        if (canVibrate) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // void vibrate (VibrationEffect vibe)
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        milliseconds,
                        // The default vibration strength of the device.
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                // This method was deprecated in API level 26
                vibrator.vibrate(milliseconds)
            }
        }
    }

    // function for dispatch Scan Result Data to fragment
    private fun displayScanResult(decodedData: String) {
        Log.d("xx", "inside display")
        if (currebtFragment is OnePutawayFragment) {
            (currebtFragment as OnePutawayFragment).updateUiOnScan(decodedData)
        } else if (currebtFragment is TwoPutawayFragment) {
            (currebtFragment as TwoPutawayFragment).updateUiOnScan(decodedData)
        } else if (currebtFragment is ThreeS_PutawayFragment) {
            (currebtFragment as ThreeS_PutawayFragment).updateUiOnScan(decodedData)
        } else if (currebtFragment is FivePutawayFragment) {
            (currebtFragment as FivePutawayFragment).updateUiOnScan(decodedData)
        } else if (currebtFragment is FourPutawayFragment) {
            (currebtFragment as FourPutawayFragment).updateUiOnScan(decodedData)
        } else if (currebtFragment is SixPutawayFragment) {
            (currebtFragment as SixPutawayFragment).updateUiOnScan(decodedData)
        } else if (currebtFragment is SevenPutawayFragment) {
            (currebtFragment as SevenPutawayFragment).updateUiOnScan(decodedData)
        } else if (currebtFragment is EightPutawayFragment) {
            (currebtFragment as EightPutawayFragment).updateUiOnScan(decodedData)
        } else if (currebtFragment is NinePutawayFragment) {
            (currebtFragment as NinePutawayFragment).updateUiOnScan(decodedData)
        } else if (currebtFragment is NineAPutawayFragment) {
            (currebtFragment as NineAPutawayFragment).updateUiOnScan(decodedData)
        } else if (currebtFragment is TenPutawayFragment) {
            (currebtFragment as TenPutawayFragment).updateUiOnScan(decodedData)
        } else if (currebtFragment is ElevenPutawayFragment) {
            (currebtFragment as ElevenPutawayFragment).updateUiOnScan(decodedData)
        } else if (currebtFragment is TwelvePutawayFragment) {
            (currebtFragment as TwelvePutawayFragment).updateUiOnScan(decodedData)
        }
    }

    override fun onDataReceived(data: String) {
        Log.d("xx", "onDataReceived RFID App: $data")
        showToast("Scanning For RFiDs.")
        transferRFIDData(data)
    }

    private fun transferRFIDData(data: String) {
        if (currebtFragment is OneRfidPutawayFragment) {
            (currebtFragment as OneRfidPutawayFragment).scanFromRFID(data)
        } else if (currebtFragment is TwoRfidPutawayFragment) {
            (currebtFragment as TwoRfidPutawayFragment).scanFromRFID(data)
        } else if (currebtFragment is ThreeRfidPutawayFragment) {
            (currebtFragment as ThreeRfidPutawayFragment).scanFromRFID(data)
        }
    }

    fun openAlerDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        var alertDialog: AlertDialog? = null
        //set title for alert dialog
        builder.setTitle("Alert!")
        //set message for alert dialog
        builder.setMessage(message)
        builder.setIcon(android.R.drawable.stat_sys_warning)

        //performing positive action
        builder.setPositiveButton("Ok") { dialogInterface, which ->
            alertDialog?.dismiss()
        }
        // Create the AlertDialog
        alertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }


    private fun showFABMenu() {
        isFABOpen = true
        activityBaseBinding.layoutContainer.putawayfab.animate()
            .translationY(-resources.getDimension(R.dimen.standard_55))
        activityBaseBinding.layoutContainer.putawayfab1.animate()
            .translationY(-resources.getDimension(R.dimen.standard_105))
        activityBaseBinding.layoutContainer.putawayfab_exp_close.setImageResource(R.drawable.ic_close)
    }

    private fun closeFABMenu() {
        isFABOpen = false
        activityBaseBinding.layoutContainer.putawayfab.animate().translationY(0F)
        activityBaseBinding.layoutContainer.putawayfab1.animate().translationY(0F)
        activityBaseBinding.layoutContainer.putawayfab_exp_close.setImageResource(R.drawable.ic_expend)

    }

    companion object {
        var rfidDataReceiver: RFIDDataReceiver? = null

        fun connectToRFIDServer(ctx: Context) {
            rfidDataReceiver = RFIDDataReceiver(ctx)
        }
    }

    fun switchToManual(flag: Boolean) {
        RFIDDataReceiver.isConnectedToRFID = flag
        appResponseMapper(AppData.getData())
    }

    @SuppressLint("RestrictedApi")
    fun checkForConnection() {
        if (RFIDDataReceiver.isConnectedToRFID) {
            activityBaseBinding.layoutContainer.putawayfab1?.visibility = View.VISIBLE
        } else {
            activityBaseBinding.layoutContainer.putawayfab1.visibility = View.GONE
        }
    }


}

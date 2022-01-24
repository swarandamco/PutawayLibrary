package com.bfc.putaway.view.fragments

import android.content.Context
import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bfc.putaway.R
import com.bfc.putaway.databinding.FragmentThreeSPutawayBinding
import com.bfc.putaway.interfaces.OnFragmentInteractionListener
import com.bfc.putaway.models.AppData
import com.bfc.putaway.util.*
import com.bfc.putaway.view.activities.PutAwayBaseActivity
import com.bfc.putaway.view.activities.MainActivityPutAway
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_three_s__putaway.*
import org.json.JSONObject
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.regex.Pattern


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

var dataInputStream: DataInputStream? = null
var dataOutputStream: DataOutputStream? = null


class ThreeS_PutawayFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    public var listener: OnFragmentInteractionListener? = null
//    private var tv_dataAny: TextView? = null
//    private var tv_To: TextView? = null
//    private var tv_expDl: TextView? = null
//    private var edit_scan: EditText? = null
//    private var tvExpDlLayout: LinearLayout? = null

    //    private var tv_slotName: TextView? = null
//    private var tv_subSlot: TextView? = null
//    private var tv_OverFlow: TextView? = null
//    private var tv_y: TextView? = null
//    private var tv_xdk: TextView? = null
//    private var tv_n: TextView? = null
//    private var edit_qty: EditText? = null
//    var scanningTask: MyScanningTask? = null

    var slotRfId: String? = null
//    private var slotLevel: Int? = null
//    var slotHeight: Int? = null
//    private var heightFromFloor: Int? = null
//    var disposable: Disposable? = null
//    val appApiServe by lazy {
//        RestServiceHandler.create()
//    }
//    val mContext = activity
//
//    var mData = ""

    private lateinit var binding: FragmentThreeSPutawayBinding

    //    var dataInputStream: DataInputStream? = null
//    var dataOutputStream: DataOutputStream? = null
//    var SERVER_IP: String? = null
//    var SERVER_PORT = 0
//    var thread1: Thread? = null
//    var DISTANCE: Int = 0
    var SCANNED_DISTANCE: String? = ""
    var HEIGHT: String? = ""
    var RFID: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentThreeSPutawayBinding.inflate(inflater, container, false)

//        val rootView = inflater.inflate(R.layout.fragment_three_s__putaway, container, false)
//        tv_dataAny = rootView.findViewById<TextView>(R.id.tv_dataAny)
//        tv_To = rootView.findViewById<TextView>(R.id.tv_To)
//        tv_expDl = rootView.findViewById<TextView>(R.id.tv_expDl)
//        edit_scan = rootView.findViewById<EditText>(R.id.edit_scan)
//        tvExpDlLayout = rootView.findViewById<LinearLayout>(R.id.expDlLayout)
//        /*tv_slotName = rootView.findViewById<TextView>(R.id.tv_slotName)
//        tv_subSlot = rootView.findViewById<TextView>(R.id.tv_subSlot)
//        tv_OverFlow = rootView.findViewById<TextView>(R.id.tv_OverFlow)
//        tv_y = rootView.findViewById<TextView>(R.id.tv_y)
//        tv_xdk = rootView.findViewById<TextView>(R.id.tv_xdk)
//        tv_n = rootView.findViewById<TextView>(R.id.tv_n)*/
//
//        edit_qty = rootView.findViewById<EditText>(R.id.edit_qty)

//        scanningTask = MyScanningTask(this)
//        scanningTask?.execute()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val data = param1!!.split(",").toTypedArray()
        val appData = AppData.getData()
        val mMineUserEntity = Methods.dataParsing(appData)
        (activity as MainActivityPutAway).setScreenTitle(mMineUserEntity.Output.nxtscr + " RF Putaway")
        AppColors.setBackgroundColor(binding.root, AppColors.screen_bc, activity)
        AppColors.setErrorMsgBackgroundColor(
            (context as MainActivityPutAway).activityBaseBinding.layoutContainer.tv_errorMsg,
            AppColors.error_bc
        )
        AppColors.setErrorMsgTextColor(
            (context as MainActivityPutAway).activityBaseBinding.layoutContainer.tv_errorMsg,
            AppColors.error_fc
        )

        if (mMineUserEntity != null && mMineUserEntity.Output != null) {
            if (!mMineUserEntity.Output.w3item.isNullOrEmpty()) {
                binding.tvDataAny?.text = mMineUserEntity.Output.w3item
            }

            if (!mMineUserEntity.Output.des01.isNullOrEmpty()) {
                binding.tvTo?.text = mMineUserEntity.Output.des01
            }

            if (!mMineUserEntity.Output.des02.isNullOrEmpty()) {
                binding.editQty?.setText(mMineUserEntity.Output.des02)
                quantity = getNumber(binding.editQty?.text.toString())
            }

//            if (!mMineUserEntity.Output.w3odsg.isNullOrEmpty()) {
//                binding.tvExpDl?.text = mMineUserEntity.Output.w3odsg
//            } else {
//                  binding.expDlLayout?.visibility = View.GONE
//            }

            if (!mMineUserEntity.Output.des04.isNullOrEmpty()) {
                binding.editScan?.setText(mMineUserEntity.Output.des04)
                scan = mMineUserEntity.Output.des04
            }

            if (mMineUserEntity.Output.spoken != null) {
                (activity as MainActivityPutAway).speakOut(mMineUserEntity.Output.spoken)
            }

            if (mMineUserEntity.Output.head1 != null) {
                (activity as MainActivityPutAway).setScreenDetail(mMineUserEntity.Output.head1)
            } else {
                (activity as MainActivityPutAway).setScreenDetail("")
            }

            if (mMineUserEntity.Output.head2 != null) {
                (activity as MainActivityPutAway).setScreenDetailSub(mMineUserEntity.Output.head2)
            }

            if (mMineUserEntity.Output.head3 != null) {
                (activity as MainActivityPutAway).setScreenTitleSub(mMineUserEntity.Output.head3)
            }
            if (mMineUserEntity.Output.screen_bc != null) {
                AppColors.setBackgroundColor(
                    binding.root,
                    mMineUserEntity.Output.screen_bc,
                    activity
                )
            }
            if (mMineUserEntity.Output.errmsg_bc != null) {
                AppColors.setErrorMsgBackgroundColor(
                    (context as MainActivityPutAway).activityBaseBinding.layoutContainer.tv_errorMsg,
                    mMineUserEntity.Output.errmsg_bc
                )
            }
            if (mMineUserEntity.Output.errmsg_fc != null) {
                AppColors.setErrorMsgTextColor(
                    (context as MainActivityPutAway).activityBaseBinding.layoutContainer.tv_errorMsg,
                    mMineUserEntity.Output.errmsg_fc
                )
            }
            if (!mMineUserEntity.Output.errmsg.isNullOrEmpty()) {
                SCANNED_DISTANCE = ""
                HEIGHT = ""
                RFID = ""
            }
        }
        Log.d(TAG, "Slot details for license is " + slotRfId)
//        startSocket()
    }

    /* // TODO: Rename method, update argument and hook method into UI event
     fun onButtonPressed(uri: Uri) {
         listener?.onFragmentInteraction(uri)
     }*/


//    fun startSocket() {
//        var prefs = PreferenceManager.getDefaultSharedPreferences(context)
////        var ip = prefs.getString("ip", "")
//        SERVER_IP = activity?.let { getStringPreference("ip", it) }
////        var port = prefs.getString("port", "")
//        var port = activity?.let { getStringPreference("port", it) }
//        SERVER_PORT = port?.toInt() ?: 0
////        var distanse = prefs.getString("distance", "")
//        var distanse = activity?.let { getStringPreference("distance", it) }
//        DISTANCE = distanse?.toInt() ?: 0
//        if (!SERVER_IP.isNullOrEmpty() && SERVER_PORT != 0) {
//            val th1 = context?.let { Thread1(SERVER_IP!!, SERVER_PORT, it) }
//            Thread(th1).start()
////            scanningTask = MyScanningTask(this, SERVER_IP, SERVER_PORT, distanse)
////            scanningTask?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
//        } else {
//            Log.d("xx", "Reader ip not available.");
//        }
//    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }

    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    fun onNextClick(btnValue: String) {
        quantity = getNumber(binding.editQty?.text.toString())
        scan = binding.editScan?.text.toString()
        val mMap = ArrayMap<String, String>().apply {
            put("quantity", "" + quantity)
            put("scanSlot", "" + scan)
            put("distance", SCANNED_DISTANCE)
            put("height", HEIGHT)
            put("Rfid_Tag", RFID)
        }
        listener?.onNextInteraction(3, btnValue, mMap)
    }

    fun showMyToast(msg: String) {
        Toast.makeText(activity as MainActivityPutAway, msg, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        AppData.setData(param1!!)
        PutAwayBaseActivity.currentFragment = ThreeS_PutawayFragment()
//        scanningTask?.cancel(true)
    }

    fun getNumber(str: String): String? {
        val p = Pattern.compile("\\d+")
        val m = p.matcher(str)
        if (m.find()) {
//           System.out.println(m.group())
            return m.group(0)
        } else {
            return null
        }
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ThreeS_PutawayFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        var quantity: String? = null
        var scan: String? = null
//        var listener: OnFragmentInteractionListener? = null

    }


//    class Thread1 internal constructor(ip: String, SERVER_PORT: Int, context: Context) : Runnable {
//        val ip = ip
//        val SERVER_PORT = SERVER_PORT
//        val context = context;
//        lateinit var handler: Handler
//
//        override fun run() {
//            val socket: Socket
//            try {
////                val serverSocket = ServerSocket(SERVER_PORT)
////                socket = serverSocket.accept()
//                socket = Socket(ip, SERVER_PORT)
//                dataInputStream = DataInputStream(
//                    socket.getInputStream()
//                )
//                dataOutputStream = DataOutputStream(
//                    socket.getOutputStream()
//                )
//
//                handler = Handler(Looper.getMainLooper())
//                handler.post(Runnable {
//                    //update the ui
//                    Log.d("xx", "Connected with IP: $ip Port: $SERVER_PORT")
//                    Toast.makeText(context, "Connected", Toast.LENGTH_LONG).show()
//                })
//
//
////                output = new PrintWriter(socket.getOutputStream());
////                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
////                runOnUiThread(Runnable { tvMessages.setText("Connected\n") })
//                Thread(
//                    Thread2(
//                        ip,
//                        SERVER_PORT,
//                        dataInputStream!!,
//                        dataOutputStream!!,
//                        context
//                    )
//                ).start()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    class Thread2 internal constructor(
//        ip: String,
//        SERVER_PORT: Int,
//        dataInputStream: DataInputStream,
//        dataOutputStream: DataOutputStream,
//        context: Context
//    ) : Runnable {
//        val ip = ip
//        val SERVER_PORT = SERVER_PORT
//        val context = context
//        lateinit var handler: Handler
//        override fun run() {
//            while (true) {
//                try {
//                    var message: String = dataInputStream!!.readUTF()
//                    if (message != null) {
//                        handler = Handler(Looper.getMainLooper())
//                        handler.post(Runnable {
//                            //update the ui
//                            Log.d("xx", "RFID Server Data: $message")
//                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
//                        })
//                        val jsonObject = JSONObject(message)
//                        val distance = jsonObject.getString("distance")
//                        val height = jsonObject.getString("height")
//                        val Rfid_Tag = jsonObject.getString("Rfid_Tag")
//
//                        if (!distance.isNullOrEmpty()) {
//                            val mDistance: Int = distance?.toInt() ?: 0
//                            if (mDistance > DISTANCE) {
//                                // Here we fire event of distance event
//                                if (!Rfid_Tag.isNullOrEmpty() && !height.isNullOrEmpty()) {
//                                    // call api for putaway
//                                    SCANNED_DISTANCE = mDistance.toString()
//                                    HEIGHT = height
//                                    RFID = Rfid_Tag
//
//                                    handler = Handler(Looper.getMainLooper())
//                                    handler.post(Runnable {
//                                        if ((context as MainActivity).currebtFragment is ThreeS_PutawayFragment) {
//                                            val mMap = ArrayMap<String, String>().apply {
//                                                put("quantity", "")
//                                                put("scanSlot", "")
//                                                put("distance", SCANNED_DISTANCE)
//                                                put("height", HEIGHT)
//                                                put("Rfid_Tag", RFID)
//                                            }
//                                            listener?.onNextInteraction(3, "Next", mMap)
//
//                                        }
//
//                                    })
//                                }
//                            }
//                        }
//
//
//                    } else {
//                        val th1 = Thread1(ip, SERVER_PORT, context)
//                        Thread(th1).start()
//                        return
//                    }
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//            }
//        }
//    }

//    internal class Thread3(private val message: String) : Runnable {
//        override fun run() {
//            try {
//                dataOutputStream.writeUTF(message)
//                dataOutputStream.flush()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//            //            output.write(message);
////            output.flush();
//            runOnUiThread(Runnable {
//                tvMessages.append("client: $message\n")
//                etMessage.setText("")
//            })
//        }
//    }


    fun cancelAsyncTask() {
//        scanningTask?.cancel(true)
    }

    // method called from Home Activity.
    fun fabClickedData() {
        Log.d("xx", "Clicked data")
        val appData = AppData.getData()
        val mMineUserEntity = JsonParsingUtil.dataParsing(appData)
        var listofPosition = arrayListOf<Int>()
        if (mMineUserEntity.Output.f1f24flags != null) {
            listofPosition = Methods.getButtonPosition(mMineUserEntity.Output.f1f24flags)
        }

        val listofButton = arrayListOf<String>()
        if (listofPosition != null) {
            for (listitem in listofPosition) {
                when (listitem) {
                    /*   12 -> listofButton.add(resources.getString(R.string.cancel))*/
                    9 -> listofButton.add(resources.getString(R.string.stage))
                    8 -> listofButton.add(resources.getString(R.string.slots))
                    20 -> listofButton.add(resources.getString(R.string.ovr_warn))
                    11 -> listofButton.add(resources.getString(R.string.nextpck))
                    12 -> listofButton.add(resources.getString(R.string.cancel))
                }
            }
            listofButton.add(resources.getString(R.string.next))
        }

        quantity = getNumber(edit_qty?.text.toString())
        scan = edit_scan?.text.toString()

        val mMap = ArrayMap<String, String>().apply {
            put("quantity", "" + quantity)
            put("scanSlot", scan)
            put("distance", SCANNED_DISTANCE)
            put("height", HEIGHT)
            put("Rfid_Tag", RFID)
        }

        listener?.onFabInteraction(3, listofButton, mMap)
    }

    fun updateUiOnScan(decodedData: String) {
        binding.editScan.setText(decodedData)
        onNextClick(resources.getString(R.string.next))
    }

    fun scanFromRFID(decodedData: String) {
        val jsonObject = JSONObject(decodedData)
        val distance = jsonObject.getString("distance")
        val height = jsonObject.getString("height")
        val Rfid_Tag = jsonObject.getString("Rfid_Tag")

        if (!distance.isNullOrEmpty()) {
            val mDistance: Int = distance?.toInt() ?: 0
            if (mDistance > DISTANCE) {
                // Here we fire event of distance event
                if (!Rfid_Tag.isNullOrEmpty() && !height.isNullOrEmpty()) {
                    // call api for putaway
                    SCANNED_DISTANCE = mDistance.toString()
                    HEIGHT = height
                    RFID = Rfid_Tag

                    val mMap = ArrayMap<String, String>().apply {
                        put("quantity", "")
                        put("scanSlot", "")
                        put("distance", SCANNED_DISTANCE)
                        put("height", HEIGHT)
                        put("Rfid_Tag", RFID)
                    }
                    listener?.onNextInteraction(3, resources.getString(R.string.next), mMap)
                }
            }
        }

    }

}

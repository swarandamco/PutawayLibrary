package com.bfc.putaway.view.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.ArrayMap
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bfc.putaway.R
import com.bfc.putaway.databinding.FragmentOneRfidPutawayBinding
import com.bfc.putaway.interfaces.OnFragmentInteractionListener
import com.bfc.putaway.models.AppData
import com.bfc.putaway.util.AppColors
import com.bfc.putaway.util.DISTANCE
import com.bfc.putaway.util.JsonParsingUtil
import com.bfc.putaway.util.Methods
import com.bfc.putaway.view.activities.PutAwayBaseActivity
import com.bfc.putaway.view.activities.MainActivityPutAway
import kotlinx.android.synthetic.main.activity_main.view.*
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private var listener: OnFragmentInteractionListener? = null

/**
 * A simple [Fragment] subclass.
 * Use the [OneRfidPutawayFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OneRfidPutawayFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentOneRfidPutawayBinding
    var SCANNED_DISTANCE: String? = ""
    var HEIGHT: String? = ""
    var RFID: String? = ""
    private var wareHouse: String? = null


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
        binding = FragmentOneRfidPutawayBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var appData = AppData.getData()
        var mMineUserEntity = Methods.dataParsing(appData.toString())
//        var prefs = PreferenceManager.getDefaultSharedPreferences(context)
        (activity as MainActivityPutAway).setScreenTitle("RC200.01S RF Putaway")
        AppColors.setBackgroundColor(binding.root, AppColors.screen_bc, activity)
        AppColors.setErrorMsgBackgroundColor((context as MainActivityPutAway).activityBaseBinding.layoutContainer.tv_errorMsg,
            AppColors.error_bc)
        AppColors.setErrorMsgTextColor((context as MainActivityPutAway).activityBaseBinding.layoutContainer.tv_errorMsg,
            AppColors.error_fc)

//        if (!mMineUserEntity.Output.head1.isNullOrEmpty()) {
            (activity as MainActivityPutAway).setScreenDetail("Looking For License/Label")
//        }
        if (!mMineUserEntity.Output.head2.isNullOrEmpty()) {
            (activity as MainActivityPutAway).setScreenDetailSub(mMineUserEntity.Output.head2)
        }
        if (!mMineUserEntity.Output.head3.isNullOrEmpty()) {
            (activity as MainActivityPutAway).setScreenTitleSub(mMineUserEntity.Output.head3)
        }
        if (!mMineUserEntity.Output.screen_bc.isNullOrEmpty()) {
            AppColors.setBackgroundColor(binding.root, mMineUserEntity.Output.screen_bc, activity)
        }
        if (!mMineUserEntity.Output.errmsg_bc.isNullOrEmpty()) {
            AppColors.setErrorMsgBackgroundColor((context as MainActivityPutAway).activityBaseBinding.layoutContainer.tv_errorMsg,mMineUserEntity.Output.errmsg_bc)
        }
        if (!mMineUserEntity.Output.errmsg_fc.isNullOrEmpty()) {
            AppColors.setErrorMsgTextColor((context as MainActivityPutAway).activityBaseBinding.layoutContainer.tv_errorMsg,mMineUserEntity.Output.errmsg_fc)
        }
        if (!mMineUserEntity.Output.spoken.isNullOrEmpty()) {
            Handler().postDelayed({
                (activity as MainActivityPutAway).speakOut(mMineUserEntity.Output.spoken)
            }, 1500)
        }

        if(!mMineUserEntity.Output.w1whse.isNullOrEmpty()) {
            wareHouse = mMineUserEntity.Output.w1whse
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        PutAwayBaseActivity.currentFragment = OneRfidPutawayFragment()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    // method called from Home Activity.
    fun onNextClick(btnValue: String) {
        val mMap = ArrayMap<String, String>().apply {
            put("w1lcns", "" )
            put("w1lbl#b", "")
            put("w1whse", wareHouse)
            put("distance", SCANNED_DISTANCE)
            put("height", HEIGHT)
            put("Rfid_Tag", RFID)
        }
        listener?.onNextInteraction(1, btnValue, mMap)
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
                    12 -> listofButton.add(resources.getString(R.string.cancel))
                }
            }
            listofButton.add(resources.getString(R.string.next))
        }

        val mMap = ArrayMap<String, String>().apply {
            put("w1lcns", "")
            put("w1lbl#b", "")
            put("w1whse", wareHouse)
            put("distance", SCANNED_DISTANCE)
            put("height", HEIGHT)
            put("Rfid_Tag", RFID)
        }

        listener?.onFabInteraction(1, listofButton, mMap)
    }

    // method called from Home Activity.
    fun scanFromRFID(decodedData: String) {
        val jsonObject = JSONObject(decodedData)
        val distance = jsonObject.getString("distance")
        val height = jsonObject.getString("height")
        val Rfid_Tag = jsonObject.getString("Rfid_Tag")

        if (!distance.isNullOrEmpty()) {
            val mDistance: Int = distance?.toInt() ?: 0
            //10 > 9
            if (mDistance < DISTANCE) {
                // Here we fire event of distance event
                if (!Rfid_Tag.isNullOrEmpty() && !height.isNullOrEmpty()) {
                    // call api for putaway
                    SCANNED_DISTANCE = mDistance.toString()
                    HEIGHT = height
                    RFID = Rfid_Tag

                    val mMap = ArrayMap<String, String>().apply {
                        put("w1lcns", "")
                        put("w1lbl#b", "")
                        put("w1whse", wareHouse)
                        put("distance", SCANNED_DISTANCE)
                        put("height", HEIGHT)
                        put("Rfid_Tag", RFID)
                    }
                    listener?.onNextInteraction(1, resources.getString(R.string.next), mMap)
                }
            }
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OneRfidPutawayFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OneRfidPutawayFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
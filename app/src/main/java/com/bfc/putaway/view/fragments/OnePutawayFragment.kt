package com.bfc.putaway.view.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.ArrayMap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.bfc.putaway.R
import com.bfc.putaway.databinding.FragmentOnePutawayBinding
import com.bfc.putaway.interfaces.OnFragmentInteractionListener
import com.bfc.putaway.models.AppData
import com.bfc.putaway.util.*
import com.bfc.putaway.util.Methods.dataParsing
import com.bfc.putaway.view.activities.PutAwayBaseActivity
import com.bfc.putaway.view.activities.MainActivityPutAway
import kotlinx.android.synthetic.main.activity_main.view.*
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [OnePutawayFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [OnePutawayFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class OnePutawayFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

//    private var tv_warehouse: TextView? = null
//    private var tv_licence: TextView? = null
//    private var tv_label: TextView? = null
//    private var edit_label: EditText? = null
//    private var edit_license: EditText? = null

    private var wareHouse: String? = null

    //    private var tv_errorMsg: TextView? = null
    private var ll_root: ConstraintLayout? = null

    //    private var et_warehouse: EditText? = null
//    private var programName: String? = null
    private var rfId: String? = null

    //    private var iv_scanning: AppCompatImageView? = null
//    private var task: MyAsyncTask? = null
    lateinit var mTextViewScreenTitle: TextView
    lateinit var mTextViewScreenTitleSub: TextView
    lateinit var mTextViewScreenDetail: TextView
    lateinit var mTextViewScreenDetailSub: TextView

//    private var lateinit fragmentFirstBinding: FragmentOnePutawayBinding

    private lateinit var binding: FragmentOnePutawayBinding
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
        binding = FragmentOnePutawayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var appData = AppData.getData()
        var mMineUserEntity = dataParsing(appData.toString())
//        var prefs = PreferenceManager.getDefaultSharedPreferences(context)
        (activity as MainActivityPutAway).setScreenTitle("RC200.01S RF Putaway")
        AppColors.setBackgroundColor(binding.root, AppColors.screen_bc, activity)
        AppColors.setErrorMsgBackgroundColor((context as MainActivityPutAway).activityBaseBinding.layoutContainer.tv_errorMsg,AppColors.error_bc)
        AppColors.setErrorMsgTextColor((context as MainActivityPutAway).activityBaseBinding.layoutContainer.tv_errorMsg,AppColors.error_fc)

        if (mMineUserEntity != null && mMineUserEntity.Output != null) {
            if (!mMineUserEntity.Output.des01.isNullOrEmpty()) {
                binding.tvWarehouse?.text = mMineUserEntity.Output.des01
            }

            if (!mMineUserEntity.Output.des02.isNullOrEmpty()) {
                binding.tvLicence?.text = mMineUserEntity.Output.des02
            }

            if (!mMineUserEntity.Output.des03.isNullOrEmpty()) {
                binding.tvLabel?.text = mMineUserEntity.Output.des03
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
                AppColors.setBackgroundColor(binding.root, mMineUserEntity.Output.screen_bc, activity)
            }
            if (mMineUserEntity.Output.errmsg_bc != null) {
                AppColors.setErrorMsgBackgroundColor((context as MainActivityPutAway).activityBaseBinding.layoutContainer.tv_errorMsg,mMineUserEntity.Output.errmsg_bc)
            }
            if (mMineUserEntity.Output.errmsg_fc != null) {
                AppColors.setErrorMsgTextColor((context as MainActivityPutAway).activityBaseBinding.layoutContainer.tv_errorMsg,mMineUserEntity.Output.errmsg_fc)
            }

            if(!mMineUserEntity.Output.w1whse.isNullOrEmpty()) {
                wareHouse = mMineUserEntity.Output.w1whse
            }
        }

        if (mMineUserEntity.Output.spoken != null) {
            Handler().postDelayed({
                (activity as MainActivityPutAway).speakOut(mMineUserEntity.Output.spoken)
            }, 1500)
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
//        AppData.setData(param1!!)
        PutAwayBaseActivity.currentFragment = OnePutawayFragment()
//        task?.cancel(true)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
//        task?.cancel(true)
    }

    fun onNextClick(btnValue: String) {
        val mMap = ArrayMap<String, String>().apply {
            put("w1lcns", "" + binding.editLicense?.text.toString())
            put("w1lbl#b", "" + binding.editLabel?.text.toString())
            put("w1whse", wareHouse)
            put("distance", SCANNED_DISTANCE)
            put("height", HEIGHT)
            put("Rfid_Tag", RFID)
        }
        listener?.onNextInteraction(1, btnValue, mMap)
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) = OnePutawayFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
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
            put("w1lcns", "" + binding.editLicense.text.toString())
            put("w1lbl#b", "" + binding.editLabel?.text.toString())
            put("w1whse", wareHouse)
            put("distance", SCANNED_DISTANCE)
            put("height", HEIGHT)
            put("Rfid_Tag", RFID)
        }

        listener?.onFabInteraction(1, listofButton, mMap)
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

    fun updateUiOnScan(decodedData: String) {
        binding.editLicense.setText(decodedData)
        onNextClick(resources.getString(R.string.next))
    }
}

package com.bfc.putaway.view.fragments

import android.content.Context
import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bfc.putaway.R
import com.bfc.putaway.databinding.FragmentSixPutawayBinding
import com.bfc.putaway.interfaces.OnFragmentInteractionListener
import com.bfc.putaway.models.AppData
import com.bfc.putaway.util.AppColors
import com.bfc.putaway.util.JsonParsingUtil
import com.bfc.putaway.util.Methods
import com.bfc.putaway.view.activities.PutAwayBaseActivity
import com.bfc.putaway.view.activities.MainActivityPutAway
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_six_putaway.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private var listener: OnFragmentInteractionListener? = null

/**
 * A simple [Fragment] subclass.
 * Use the [SixPutawayFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SixPutawayFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
//    private var tv_dataAny: TextView? = null
//    private var tv_scanData: TextView? = null
//    private var tv_desc1: TextView? = null
//    private var tv_desc2: TextView? = null
//    private var tv_desc3: TextView? = null
//    private var tv_lic: EditText? = null
    private lateinit var binding: FragmentSixPutawayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
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
                    3 -> listofButton.add(resources.getString(R.string.exit))
                    9 -> listofButton.add(resources.getString(R.string.skip))
                }
            }
            listofButton.add(resources.getString(R.string.next))
        }

        val mMap = ArrayMap<String, String>().apply {
            put("whouse", "")
            put("transaction", "")
            put("eod", "")
            put("w6nxt#", edit_lic.text.toString())
        }

        listener?.onFabInteraction(6, listofButton, mMap)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSixPutawayBinding.inflate(inflater,container,false)
//        val rootView = inflater.inflate(R.layout.fragment_six_putaway, container, false)
//        tv_scanData = rootView.findViewById<TextView>(R.id.tv_scanData)
//        tv_dataAny = rootView.findViewById<TextView>(R.id.tv_dataAny)
//        tv_desc1 = rootView.findViewById<EditText>(R.id.tv_desc1)
//        tv_desc2 = rootView.findViewById<EditText>(R.id.tv_desc2)
//        tv_desc3 = rootView.findViewById<EditText>(R.id.tv_desc3)
//        tv_lic = rootView.findViewById<EditText>(R.id.edit_lic)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val appData = AppData.getData()
        val mMineUserEntity = Methods.dataParsing(appData)
        (activity as MainActivityPutAway).setScreenTitle(mMineUserEntity.Output.nxtscr + " RF Putaway")

        AppColors.setBackgroundColor(binding.root, AppColors.screen_bc, activity)
        AppColors.setErrorMsgBackgroundColor((context as MainActivityPutAway).activityBaseBinding.layoutContainer.tv_errorMsg,AppColors.error_bc)
        AppColors.setErrorMsgTextColor((context as MainActivityPutAway).activityBaseBinding.layoutContainer.tv_errorMsg,AppColors.error_fc)
        if (mMineUserEntity != null && mMineUserEntity.Output != null) {
            if (!mMineUserEntity.Output.w6item.isNullOrEmpty()) {
                binding.tvDataAny?.text = mMineUserEntity.Output.w6item
            }

            if (!mMineUserEntity.Output.des01.isNullOrEmpty()) {
                binding.tvScanData?.text = mMineUserEntity.Output.des01
            }

            if (!mMineUserEntity.Output.w6dsp.isNullOrEmpty()) {
                binding.tvDesc1?.text = mMineUserEntity.Output.w6dsp
            }

            if (!mMineUserEntity.Output.des02.isNullOrEmpty()) {
                binding.tvDesc2?.text = mMineUserEntity.Output.des02
            }

            if (!mMineUserEntity.Output.des03.isNullOrEmpty()) {
                binding.tvDesc3?.text = mMineUserEntity.Output.des03
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
                AppColors.setBackgroundColor(binding.root, mMineUserEntity.Output.screen_bc, activity)
            }
            if (mMineUserEntity.Output.errmsg_bc != null) {
                AppColors.setErrorMsgBackgroundColor((context as MainActivityPutAway).activityBaseBinding.layoutContainer.tv_errorMsg,mMineUserEntity.Output.errmsg_bc)
            }
            if (mMineUserEntity.Output.errmsg_fc != null) {
                AppColors.setErrorMsgTextColor((context as MainActivityPutAway).activityBaseBinding.layoutContainer.tv_errorMsg,mMineUserEntity.Output.errmsg_fc)
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
         * @return A new instance of fragment SixPutawayFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SixPutawayFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        PutAwayBaseActivity.currentFragment = SixPutawayFragment()
    }

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
        val mMap = ArrayMap<String, String>().apply {
            put("whouse", "")
            put("transaction", "")
            put("eod", "")
            put("w6nxt#", binding.editLic.text.toString())
        }
        listener?.onNextInteraction(6, btnValue, mMap)
    }

    fun updateUiOnScan(decodedData: String) {
        binding.editLic?.setText(decodedData)
        onNextClick(resources.getString(R.string.next))
    }
}
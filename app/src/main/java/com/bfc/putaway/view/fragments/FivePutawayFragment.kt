package com.bfc.putaway.view.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bfc.putaway.R
import com.bfc.putaway.databinding.FragmentFivePutawayBinding
import com.bfc.putaway.interfaces.OnFragmentInteractionListener
import com.bfc.putaway.models.AppData
import com.bfc.putaway.util.AppColors
import com.bfc.putaway.util.JsonParsingUtil
import com.bfc.putaway.util.Methods
import com.bfc.putaway.util.RFIDDataReceiver
import com.bfc.putaway.view.activities.PutAwayBaseActivity
import com.bfc.putaway.view.activities.MainActivityPutAway
import kotlinx.android.synthetic.main.activity_main.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FivePutawayFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FivePutawayFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FivePutawayFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    //    private var tv_scanned: TextView? = null
//    private var tv_dataAny: TextView? = null
//    private var edit_nextLic: EditText? = null
//    private var edit_nextLbl: EditText? = null
    private lateinit var binding: FragmentFivePutawayBinding


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
                    12 -> listofButton.add(resources.getString(R.string.cancel))
                }
            }
            listofButton.add(resources.getString(R.string.next))
        }

        val mMap = ArrayMap<String, String>().apply {
            put("w5lcns", binding.editNextLic?.text.toString())
            put("w5lbl#b", binding.editNextLbl?.text.toString())
        }

        listener?.onFabInteraction(5, listofButton, mMap)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFivePutawayBinding.inflate(inflater, container, false)
//        val rootView = inflater.inflate(R.layout.fragment_five_putaway, container, false)
//        tv_scanned = rootView.findViewById<TextView>(R.id.tv_scanned)
//        tv_dataAny = rootView.findViewById<TextView>(R.id.tv_dataAny)
//        edit_nextLic = rootView.findViewById<EditText>(R.id.edit_next_lic)
//        edit_nextLbl = rootView.findViewById<EditText>(R.id.edit_next_lbl)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            if (!mMineUserEntity.Output.des01.isNullOrEmpty()) {
                binding.tvScanned?.text = mMineUserEntity.Output.des01
            }

            if (!mMineUserEntity.Output.des02.isNullOrEmpty()) {
                binding.tvDataAny?.text = mMineUserEntity.Output.des02
            }

            if (!mMineUserEntity.Output.des03.isNullOrEmpty()) {
                //edit_nextLic?.setText(mMineUserEntity.Output.des03)
            }

            if (!mMineUserEntity.Output.des04.isNullOrEmpty()) {
                // edit_nextLbl?.setText(mMineUserEntity.Output.des04)
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

            if(RFIDDataReceiver.isConnectedToRFID){
                updateUiOnScan("0")
            }

        }
        /*  val data = param1!!.split(",").toTypedArray()
          Log.d("xx", "data of five frag:- " + data)*/
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
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

    override fun onDestroyView() {
        super.onDestroyView()
//        AppData.setData(param1!!)
        PutAwayBaseActivity.currentFragment = FivePutawayFragment()
    }

    fun onNextClick(btnValue: String) {
        val mMap = ArrayMap<String, String>().apply {
            put("w5lcns", binding.editNextLic?.text.toString())
            put("w5lbl#b", binding.editNextLbl?.text.toString())
        }
        listener?.onNextInteraction(5, btnValue, mMap)
    }

    fun updateUiOnScan(decodedData: String) {
        binding.editNextLic.setText(decodedData)
        onNextClick(resources.getString(R.string.next))

    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FivePutawayFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

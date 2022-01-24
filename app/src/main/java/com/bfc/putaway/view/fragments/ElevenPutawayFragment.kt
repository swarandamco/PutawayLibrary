package com.bfc.putaway.view.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.ArrayMap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bfc.putaway.R
import com.bfc.putaway.databinding.FragmentElevenPutawayBinding
import com.bfc.putaway.interfaces.OnFragmentInteractionListener
import com.bfc.putaway.models.AppData
import com.bfc.putaway.util.AppColors
import com.bfc.putaway.util.JsonParsingUtil
import com.bfc.putaway.util.Methods
import com.bfc.putaway.view.activities.PutAwayBaseActivity
import com.bfc.putaway.view.activities.MainActivityPutAway
import kotlinx.android.synthetic.main.activity_main.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private var listener: OnFragmentInteractionListener? = null

/**
 * A simple [Fragment] subclass.
 * Use the [ElevenPutawayFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ElevenPutawayFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //    private var tv_desc1: TextView? = null
//    private var tv_desc2: TextView? = null
//    private var tv_desc3: TextView? = null
//    private var tv_desc4: TextView? = null
//    private var tv_desc5: TextView? = null
//    private var tv_desc6: TextView? = null
//    private var tv_desc7: TextView? = null
//    private var tv_desc8: TextView? = null
//    private var tv_desc9: TextView? = null
//    private var tv_desc10: TextView? = null
//    private var tv_desc11: TextView? = null
//    private var tv_desc12: TextView? = null
    private lateinit var binding: FragmentElevenPutawayBinding

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
            put("w11slot1s", binding.tvDesc1?.text.toString())
            put("w11slot2s", binding.tvDesc2?.text.toString())
            put("w11slot3s", binding.tvDesc3?.text.toString())
            put("w11slot4s", binding.tvDesc4?.text.toString())
            put("w11slot1", binding.tvDesc5?.text.toString())
            put("w11slot2", binding.tvDesc6?.text.toString())
            put("w11slot3", binding.tvDesc7?.text.toString())
            put("w11slot4", binding.tvDesc8?.text.toString())
            put("w11slot1q", binding.tvDesc9?.text.toString())
            put("w11slot2q", binding.tvDesc10?.text.toString())
            put("w11slot3q", binding.tvDesc11?.text.toString())
            put("w11slot4q", binding.tvDesc12?.text.toString())
        }

        listener?.onFabInteraction(11, listofButton, mMap)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentElevenPutawayBinding.inflate(inflater, container, false)
//        val rootView = inflater.inflate(R.layout.fragment_eleven_putaway, container, false)
//        tv_desc1 = rootView.findViewById<TextView>(R.id.tv_desc1)
//        tv_desc2 = rootView.findViewById<EditText>(R.id.tv_desc2)
//        tv_desc3 = rootView.findViewById<EditText>(R.id.tv_desc3)
//        tv_desc4 = rootView.findViewById<EditText>(R.id.tv_desc4)
//        tv_desc5 = rootView.findViewById<EditText>(R.id.tv_desc5)
//        tv_desc6 = rootView.findViewById<EditText>(R.id.tv_desc6)
//        tv_desc7 = rootView.findViewById<EditText>(R.id.tv_desc7)
//        tv_desc8 = rootView.findViewById<EditText>(R.id.tv_desc8)
//        tv_desc9 = rootView.findViewById<EditText>(R.id.tv_desc9)
//        tv_desc10 = rootView.findViewById<EditText>(R.id.tv_desc10)
//        tv_desc11 = rootView.findViewById<EditText>(R.id.tv_desc11)
//        tv_desc12 = rootView.findViewById<EditText>(R.id.tv_desc12)
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
            if (!mMineUserEntity.Output.w11slot1s.isNullOrEmpty()) {

                binding.tvDesc1?.text = mMineUserEntity.Output.w11slot1s.toEditable()
            }

            if (!mMineUserEntity.Output.w11slot2s.isNullOrEmpty()) {
                binding.tvDesc2?.text = mMineUserEntity.Output.w11slot2s.toEditable()
            }

            if (!mMineUserEntity.Output.w11slot3s.isNullOrEmpty()) {
                binding.tvDesc3?.text = mMineUserEntity.Output.w11slot3s.toEditable()
            }

            if (!mMineUserEntity.Output.w11slot4s.isNullOrEmpty()) {
                binding.tvDesc4?.text = mMineUserEntity.Output.w11slot4s.toEditable()
            }

            if (!mMineUserEntity.Output.w11slot1.isNullOrEmpty()) {
                binding.tvDesc5?.text = mMineUserEntity.Output.w11slot1.toEditable()
            }

            if (!mMineUserEntity.Output.w11slot2.isNullOrEmpty()) {
                binding.tvDesc6?.text = mMineUserEntity.Output.w11slot2.toEditable()
            }

            if (!mMineUserEntity.Output.w11slot3.isNullOrEmpty()) {
                binding.tvDesc4?.text = mMineUserEntity.Output.w11slot2.toEditable()
            }
            if (!mMineUserEntity.Output.w11slot4.isNullOrEmpty()) {
                binding.tvDesc4?.text = mMineUserEntity.Output.w11slot4.toEditable()
            }

            if (!mMineUserEntity.Output.w11slot1q.isNullOrEmpty()) {
                binding.tvDesc4?.text = mMineUserEntity.Output.w11slot1q.toEditable()
            }

            if (!mMineUserEntity.Output.w11slot2q.isNullOrEmpty()) {
                binding.tvDesc4?.text = mMineUserEntity.Output.w11slot1q.toEditable()
            }

            if (!mMineUserEntity.Output.w11slot3q.isNullOrEmpty()) {
                binding.tvDesc4?.text = mMineUserEntity.Output.w11slot3q.toEditable()
            }

            if (!mMineUserEntity.Output.w11slot4q.isNullOrEmpty()) {
                binding.tvDesc4?.text = mMineUserEntity.Output.w11slot4q.toEditable()
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
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ElevenPutawayFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ElevenPutawayFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        PutAwayBaseActivity.currentFragment = ElevenPutawayFragment()
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
            put("w11slot1s", binding.tvDesc1?.text.toString())
            put("w11slot2s", binding.tvDesc2?.text.toString())
            put("w11slot3s", binding.tvDesc3?.text.toString())
            put("w11slot4s", binding.tvDesc4?.text.toString())
            put("w11slot1", binding.tvDesc5?.text.toString())
            put("w11slot2", binding.tvDesc6?.text.toString())
            put("w11slot3", binding.tvDesc7?.text.toString())
            put("w11slot4", binding.tvDesc8?.text.toString())
            put("w11slot1q", binding.tvDesc9?.text.toString())
            put("w11slot2q", binding.tvDesc10?.text.toString())
            put("w11slot3q", binding.tvDesc11?.text.toString())
            put("w11slot4q", binding.tvDesc12?.text.toString())
        }
        listener?.onNextInteraction(11, btnValue, mMap)
    }

    fun String.toEditable(): Editable {
        return Editable.Factory.getInstance().newEditable(this)
    }

    fun updateUiOnScan(decodedData: String) {
        binding.tvDesc1?.setText(decodedData)
        onNextClick(resources.getString(R.string.next))
    }
}
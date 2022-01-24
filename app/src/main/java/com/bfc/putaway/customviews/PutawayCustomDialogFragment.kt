package com.bfc.putaway.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bfc.putaway.R
import com.bfc.putaway.interfaces.FabInterface
import com.bfc.putaway.util.isDialogOpen
import com.nex3z.flowlayout.FlowLayout


@SuppressLint("ValidFragment")
class PutawayCustomDialogFragment constructor(context: Context, var data: ArrayList<String>, tag: Int) : DialogFragment() {
    private var listener: FabInterface? = null
    //    private var et_dilog_level: EditText? = null
    //    private var mBaseActivity: BaseActivity? = null
    var tag = tag

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.dialog_fragment_layout, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        var root = activity!!.layoutInflater.inflate(R.layout.dialog_fragment_layout, container)

        var iv_cancel = root.findViewById(R.id.iv_cancel) as ImageView
        iv_cancel.setOnClickListener(View.OnClickListener {
            isDialogOpen = false
            dismiss()
        })
//        et_dilog_level = root.findViewById(R.id.et_dilog_level) as EditText

        return root
    }

    override fun onStart() {
        super.onStart()

        val window = dialog?.window
        val windowParams = window!!.attributes
        windowParams.dimAmount = 0.90f
        windowParams.flags = windowParams.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        window.attributes = windowParams
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        listener = context
        tag = this.tag
        fillAutoSpacingLayout(view)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FabInterface) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    private fun fillAutoSpacingLayout(view: View) {
        val flowLayout = view.findViewById(R.id.fl_dialog) as FlowLayout
        val dummyTexts = this.data
        for (text in dummyTexts) {
            val textView = buildLabel(text)
            flowLayout.addView(textView)
            textView.setOnClickListener(View.OnClickListener {
                dialogClicked(text)
                isDialogOpen = false
                dismiss()
            })
        }
    }

    private fun dialogClicked(text: String?) {
        when (text) {
            resources.getString(R.string.next) -> listener?.clickDialogFragment(text, tag)
            resources.getString(R.string.task) -> listener?.clickDialogFragment(text, tag)
            resources.getString(R.string.cancel) -> listener?.clickDialogFragment(text, tag)
            resources.getString(R.string.skip) -> listener?.clickDialogFragment(text, tag)
            resources.getString(R.string.exit) -> listener?.clickDialogFragment(text, tag)
            resources.getString(R.string.nextpck) -> listener?.clickDialogFragment(text, tag)
            resources.getString(R.string.stage) -> listener?.clickDialogFragment(text, tag)
            resources.getString(R.string.slots) -> listener?.clickDialogFragment(text, tag)
            resources.getString(R.string.ovr_warn) -> listener?.clickDialogFragment(text, tag)
            resources.getString(R.string.ovr_warn) -> listener?.clickDialogFragment(text, tag)
            else -> {
                Toast.makeText(context, resources.getString(R.string.wrong_scan), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun buildLabel(text: String): TextView {
        val textView = TextView(context)
        textView.text = text
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
        textView.setTextColor(ContextCompat.getColor(listener?.context!!, R.color.colorAccent))
        textView.setPadding(dpToPx(16f).toInt(), dpToPx(8f).toInt(), dpToPx(16f).toInt(), dpToPx(8f).toInt())
        textView.setBackgroundResource(R.drawable.label_bg)
        val buttonLayoutParams =
            ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        buttonLayoutParams.rightMargin = dpToPx(16f).toInt()
        textView.layoutParams = buttonLayoutParams
        return textView
    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics
        )
    }

    fun updateScanData(decodedData: String) {
//        Log.d("xx", "decodedData: " + decodedData)
        when (decodedData) {
            resources.getString(R.string.next) -> {
                dialogClicked(decodedData)
                isDialogOpen = false
                dismiss()
            }
            resources.getString(R.string.task) -> {
                dialogClicked(decodedData)
                isDialogOpen = false
                dismiss()
            }
            resources.getString(R.string.cancel) -> {
                dialogClicked(decodedData)
                isDialogOpen = false
                dismiss()
            }
            else -> {
                Toast.makeText(context, resources.getString(R.string.wrong_scan), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
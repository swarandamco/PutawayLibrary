package com.bfc.putaway.util

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.bfc.putaway.view.activities.MainActivityPutAway

object AppColors {

    var screen_bc: String = "#FAFAFA"
    var error_bc: String = "#FAFAFA"
    var error_fc: String = "#F33F40"


    fun setBackgroundColor(view: View, color: String, context: FragmentActivity?){
        view?.setBackgroundColor(Color.parseColor(color))
        Methods.updateHeader((context as MainActivityPutAway), color)
    }

    fun setErrorMsgBackgroundColor(tv:TextView, errBC: String){
        tv?.setBackgroundColor(Color.parseColor(errBC))
    }
    fun setErrorMsgTextColor(tv:TextView, errFC: String){
        tv?.setTextColor(Color.parseColor(errFC))
    }

}
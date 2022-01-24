package com.bfc.putaway.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bfc.putaway.interfaces.OnFragmentInteractionListener


class DrawerSwitchReceiver : BroadcastReceiver() {
    private var listner: OnFragmentInteractionListener? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action

        if (context is OnFragmentInteractionListener) {
            listner = context
        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }

        if (context != null) {
            if (action == "com.rfselection.SwitchCompactBroadcast") {
                listner?.onClickSwitch("" + intent.getBooleanExtra("isChecked", false))
            }
        }
    }
}
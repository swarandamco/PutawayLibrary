package com.bfc.putaway.interfaces

import android.net.Uri
import android.util.ArrayMap

interface OnFragmentInteractionListener {
    fun onFragmentInteraction(uri: Uri)
    fun onFabInteraction(tag: Int, data: ArrayList<String> , mapData: ArrayMap<String, String>?)
    fun onNextInteraction(tag: Int, value: String, data: ArrayMap<String, String>?)
    fun onClickSwitch(data: String)
}
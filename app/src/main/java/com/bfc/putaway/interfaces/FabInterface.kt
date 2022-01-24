package com.bfc.putaway.interfaces

import android.content.Context

interface FabInterface {
    val context: Context
    fun clickDialogFragment(value: String, tag: Int)
}
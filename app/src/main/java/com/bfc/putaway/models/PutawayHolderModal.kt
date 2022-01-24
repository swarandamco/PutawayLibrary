package com.bfc.putaway.models

var resData: String? = null

object AppData {

     fun setData(data: String) {
        resData = data
    }

     fun getData(): String {
        if (resData != null) {
            return resData!!
        } else {
            return ""
        }
    }
}
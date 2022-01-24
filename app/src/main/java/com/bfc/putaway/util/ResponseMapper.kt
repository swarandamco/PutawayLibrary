package com.bfc.putaway.util

import android.content.Context
import android.widget.Toast
import retrofit2.HttpException

object ResponseMapper {

    fun errorHandler(applicationContext: Context, error: Throwable) {
        if(error is HttpException){
            var errorCode = error.code()
            when(errorCode){
                400 -> showToast(applicationContext,"Wrong Request sent to Server. Please contact support")
                401 -> showToast(applicationContext,"Can't Authenticate the Credentials")
                404 -> showToast(applicationContext,"Can't find data on Server")
                500 -> showToast(applicationContext,"Can't fulfill the request. Please check connection & try again")
                else -> showToast(applicationContext,"Can't fulfill the request. Please check connection & try again")
            }
        }else{
            showToast(applicationContext,"Can't fulfill the request. Please check connection & try again")
        }
    }

    fun showToast(context: Context,msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        if (myLogFile != null) {
            Methods.writeLogsInFile(myLogFile!!, Methods.getDate() + ": Error: " + msg)
        }
    }

}
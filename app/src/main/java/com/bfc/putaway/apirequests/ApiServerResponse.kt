package com.bfc.putaway.apirequests

import retrofit2.Response

interface ApiServerResponse {

    fun onSuccess(tag: Int, response: Response<*>)
    fun onError(tag: Int, throwable: Throwable)
}
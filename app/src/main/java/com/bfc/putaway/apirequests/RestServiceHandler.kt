package com.bfc.putaway.apirequests

import android.util.ArrayMap
import com.bfc.putaway.models.BaseResponseModel
import com.bfc.putaway.util.baseUrl
import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.Dispatcher
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit


interface RestServiceHandler {

    @Headers(
        "Content-type:application/json"
    )
    @POST("putawayService")
    fun postApiCall(
        @Body data: ArrayMap<String, String>?
    ): Observable<BaseResponseModel.BaseResponse>


    @Multipart
    @POST("upload")
    fun uploadFile(
        @Part file: MultipartBody.Part
//        @Part("file") name: RequestBody
    ): Observable<BaseResponseModel.BaseResponse>


    @POST("selectprimedev/bfc")
    fun loginApiCall(@Body data: JsonObject): Observable<JsonObject>

    @POST("selectprimedev/bfc")
    fun genericApiCall(@Body data: JsonObject): Observable<JsonObject>

    //https://futurestud.io/tutorials/retrofit-2-passing-multiple-parts-along-a-file-with-partmap

    @POST("bfclogger/log")
    fun uploadCall(
        @Query("device") device: String,
        @Query("fileName") fileName: String,
        @Query("pathName") pathName: String,
        @Query("minInfo") minInfo: String,
        @Body body: RequestBody
    ): Observable<JsonObject>


    companion object {
        fun create(): RestServiceHandler {

            val dispatcher = Dispatcher()
            dispatcher.maxRequests = 1

            val okHttpClient = OkHttpClient.Builder().dispatcher(dispatcher)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create()
                )
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .build()
            return retrofit.create(
                RestServiceHandler::class.java
            )
        }
    }
}
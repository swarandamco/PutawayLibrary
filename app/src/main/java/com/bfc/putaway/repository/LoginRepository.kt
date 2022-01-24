package com.bfc.putaway.repository

import com.bfcassociates.rfselection.models.response.LoginPutawayModelRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

class LoginRepository() {
    private val loginUrl = "https://example.com/login"

    // Function that makes the network request, blocking the current thread
   suspend fun makeLoginRequest(
        jsonBody: String
    ): Result<LoginPutawayModelRes> {
//        val url = URL(loginUrl)
//        (url.openConnection() as? HttpURLConnection)?.run {
//            requestMethod = "POST"
//            setRequestProperty("Content-Type", "application/json; utf-8")
//            setRequestProperty("Accept", "application/json")
//            doOutput = true
//            outputStream.write(jsonBody.toByteArray())
//            return Result.Success(responseParser.parse(inputStream))
//        }
//        return Result.Error(Exception("Cannot open HttpURLConnection"))

        return withContext(Dispatchers.IO){
            val url = URL(loginUrl)
            (url.openConnection() as? HttpURLConnection)?.run {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json; utf-8")
            setRequestProperty("Accept", "application/json")
            doOutput = true
            outputStream.write(jsonBody.toByteArray())
                Result.Success(inputStream)
        }
             Result.Error(Exception("Cannot open HttpURLConnection"))

        }

    }
}
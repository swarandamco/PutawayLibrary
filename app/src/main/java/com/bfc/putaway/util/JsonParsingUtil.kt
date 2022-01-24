package com.bfc.putaway.util

import com.bfcassociates.rfselection.models.response.PutawayOneModelRes
import com.google.gson.Gson

object JsonParsingUtil {
    fun dataParsing(result: String): PutawayOneModelRes {
        val gson = Gson()
        val mMineUserEntity = gson.fromJson(result, PutawayOneModelRes::class.java)
        return mMineUserEntity
    }
}

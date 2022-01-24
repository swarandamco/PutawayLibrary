package com.bfcassociates.rfselection.models.response

import PutAwayOutput
import PutAwayStd
import com.google.gson.annotations.SerializedName

data class LoginPutawayModelRes(
//    val std: Std,
//    val output: Output

    @SerializedName("Std") var Std: PutAwayStd,
    @SerializedName("Output") var Output: PutAwayOutput

)
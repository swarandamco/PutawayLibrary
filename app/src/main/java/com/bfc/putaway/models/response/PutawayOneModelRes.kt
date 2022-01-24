package com.bfcassociates.rfselection.models.response

import PutAwayOutput
import PutAwayStd
import com.google.gson.annotations.SerializedName

data class PutawayOneModelRes(
    @SerializedName("Std") var Std: PutAwayStd,
    @SerializedName("Output") var Output: PutAwayOutput

)
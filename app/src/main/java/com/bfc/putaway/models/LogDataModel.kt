package com.bfc.putaway.models

data class LogDataModel(
    val timeStamp: String,
    val loggedUser: String,
    val screenName: String,
    val actionPerformed: String,
    val ip: String,
    val op: String,
    val mError: String
)
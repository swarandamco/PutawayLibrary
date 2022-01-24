package com.bfc.putaway.util

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bfc.putaway.view.activities.PutAwayAdminApiControlActivity
import com.bfc.putaway.view.activities.PutAwayLoginActivity
import com.bfc.putaway.view.activities.MainActivityPutAway
import com.bfcassociates.rfselection.models.response.PutawayOneModelRes
import com.google.gson.Gson
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

// http://172.29.15.72:8089/putaway/putawayService
//val baseUrl: String = "http://172.29.15.72:8089/putaway/"
//var baseUrl: String = "http://bfc.demo.damcogroup.com:8880/putaway/"
var baseUrl: String = "http://bfc.demo.damcogroup.com:8880/"
var apiTIMEOUT: Long = 60
val SHARED_PREF_NAME: String = "local_pref"
var isDialogOpen: Boolean = false
var DIR_NAME: String = "selectprimeqa/bfc" // Need to update the Service Handelar as well.
//var logList: MutableList<LogDataModel>? = mutableListOf<LogDataModel>()
var TAG: String = "xx"
//var FILE_PATH: String = ""
var tfmArray = mutableListOf<Int>()
var APP_VER: String = ""
var APP_NAME: String = "PutAway"
var WHOUSE: String = ""
var DOUBLE_CLICK_ENABLE: Boolean = true
var myLogFile: File? = null
var mPrefs: SharedPreferences? = null
var DEVICE_ID: String = "UNKNOWN"
var DISTANCE: Int = 0



object Methods {
    var appUser: String? = null

    @JvmStatic
    fun getDate(): String {
        val currentTime = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val formattedDate = df.format(currentTime.time)
        return formattedDate
    }

    fun avoidDoubleClicks(view: View) {
        val DELAY_IN_MS: Long = 900
        if (!view.isClickable) {
            return
        }
        view.isClickable = false
        view.postDelayed({ view.isClickable = true }, DELAY_IN_MS)
    }

    fun dataParsing(result: String): PutawayOneModelRes {
        var gson = Gson()
        var mMineUserEntity = gson.fromJson(result, PutawayOneModelRes::class.java)
        return mMineUserEntity
    }

    fun writeToXml(myInternalFile: File) {
        val docBuilder: DocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document = docBuilder.newDocument()
        // Create the root node
        val rootElement: Element = doc.createElement("records")

        // Set person-id attribute that represents a person id from the datasource
//        rootElement.setAttribute("person-id", "1001")
        // Create an element for person's last name
        val putaway: Element = doc.createElement("putaway")
        val url: Element = doc.createElement("url")

        val api_timeout: Element = doc.createElement("apiTimeout")
        val deviceId: Element = doc.createElement("deviceId")

        // Append text to the lastName element (give that person a last name)
        url.appendChild(doc.createTextNode(baseUrl))
        api_timeout.appendChild(doc.createTextNode(apiTIMEOUT.toString()))
        deviceId.appendChild(doc.createTextNode(DEVICE_ID))

        // Finally, append lastName to person element
        putaway.appendChild(url)
        putaway.appendChild(api_timeout)
        putaway.appendChild(deviceId)

        // Finally, append firstName to the person element right after the lastName element
        rootElement.appendChild(putaway)

        // Now, "add" the root node to the XML document in memory
        doc.appendChild(rootElement)

        val transformer: Transformer = TransformerFactory.newInstance().newTransformer()

        // ==== Start: Pretty print
        // https://stackoverflow.com/questions/139076/how-to-pretty-print-xml-from-java
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
        // ==== End: Pretty print

        transformer.transform(DOMSource(doc), StreamResult(myInternalFile))
    }

    fun getCurrentTimeInMiliSec(): Long {
        return System.currentTimeMillis()
    }

    fun getButtonPosition(f1f24flags: String):ArrayList<Int>{

        val listofPosition = arrayListOf<Int>()

        for ((index, value) in f1f24flags.withIndex()) {
            if (value.equals('1')) {
                listofPosition.add(index + 1)
            }
        }

        return listofPosition
    }

    fun writeLogsInFile(logFile: File, content: String) {
        val mcontent = content + "\n"
        try {
            val fw = FileWriter(logFile, true)
            fw.write(mcontent)
            fw.close()
        } catch (e: IOException) {
        }
    }

    fun updateHeader(activity: MainActivityPutAway, colorCode: String){
        (activity as MainActivityPutAway).setScreenDetailBG(colorCode)
        (activity as MainActivityPutAway).setScreenDetailSubBG(colorCode)
        (activity as MainActivityPutAway).setScreenTitleBG(colorCode)
        (activity as MainActivityPutAway).setScreenTitleSubBG(colorCode)
    }

    fun openAlertDialog(message: String, context: Context) {
        val builder = AlertDialog.Builder(context)
        var alertDialog: AlertDialog? = null
        //set title for alert dialog
        builder.setTitle("Alert!")
        //set message for alert dialog
        builder.setMessage(message)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Ok") { dialogInterface, which ->
            if (message.equals("URL Updated Successfully.",true)){
                val intent = Intent(context, PutAwayLoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                if (context is PutAwayAdminApiControlActivity)
                    (context as PutAwayAdminApiControlActivity).finish()
            }
            alertDialog?.dismiss()
        }
        // Create the AlertDialog
        alertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun setStringPreference(key: String, value: String,context: Context) {
        mPrefs = context.getSharedPreferences(SHARED_PREF_NAME, AppCompatActivity.MODE_PRIVATE)
        mPrefs!!.edit().putString(key, value).apply()
    }

    fun getStringPreference(key: String,context: Context): String {
        mPrefs = context.getSharedPreferences(SHARED_PREF_NAME, AppCompatActivity.MODE_PRIVATE)
        return mPrefs!!.getString(key, "").toString()
    }




}
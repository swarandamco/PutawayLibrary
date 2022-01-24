package com.bfc.putaway.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import kotlin.properties.Delegates

var dataInputStream: DataInputStream? = null
var dataOutputStream: DataOutputStream? = null


class RFIDDataReceiver(activity: Context) {
    var activity:Context = activity
    lateinit var SERVER_IP:String
    var SERVER_PORT by Delegates.notNull<Int>()
    var sendRFIDScannedData:SendRFIDScannedData?= null

    init {
        sendRFIDScannedData = activity as SendRFIDScannedData
        startSocket()
    }


    fun startSocket() {
//        var prefs = PreferenceManager.getDefaultSharedPreferences(context)
//        var ip = prefs.getString("ip", "")
        SERVER_IP = activity?.let { Methods.getStringPreference("ip", it) }
//        var port = prefs.getString("port", "")
        var port = activity?.let { Methods.getStringPreference("port", it) }
        if (port.equals("")) port = "0"
        SERVER_PORT = port?.toInt() ?: 0
       var distanse = activity?.let { Methods.getStringPreference("distance", it) }
        if (distanse.equals("")) distanse = "0"
        DISTANCE = distanse?.toInt() ?: 0
        if (!SERVER_IP.isNullOrEmpty() && SERVER_PORT != 0) {
            val th1 = activity?.let { Thread1(SERVER_IP!!, SERVER_PORT, sendRFIDScannedData,it) }
            Thread(th1).start()

//            scanningTask = MyScanningTask(this, SERVER_IP, SERVER_PORT, distanse)
//            scanningTask?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        } else {

            Log.d("xx", "Reader ip not available.");
        }
    }

    class Thread1 internal constructor(
        ip: String, SERVER_PORT: Int,
        sendRFIDScannedData: SendRFIDScannedData?, context: Context) : Runnable {
        val ip = ip
        val SERVER_PORT = SERVER_PORT
        val context = context;
        lateinit var handler: Handler
        val sendRFIDScannedData = sendRFIDScannedData

        override fun run() {
            val socket: Socket
            try {
//                val serverSocket = ServerSocket(SERVER_PORT)
//                socket = serverSocket.accept()
                socket = Socket(ip, SERVER_PORT)
                dataInputStream = DataInputStream(
                    socket.getInputStream()
                )
                dataOutputStream = DataOutputStream(
                    socket.getOutputStream()
                )

                handler = Handler(Looper.getMainLooper())
                handler.post(Runnable {
                    //update the ui
                    isConnectedToRFID = true
                    Toast.makeText(context, "Connected", Toast.LENGTH_LONG).show()
                })


//                output = new PrintWriter(socket.getOutputStream());
//                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                runOnUiThread(Runnable { tvMessages.setText("Connected\n") })
                Thread(
                    Thread2(
                        ip,
                        SERVER_PORT,
                        dataInputStream!!,
                        dataOutputStream!!,
                        sendRFIDScannedData,
                        context
                    )
                ).start()
            } catch (e: IOException) {
                isConnectedToRFID = false
                e.printStackTrace()
            }
        }
    }

    class Thread2 internal constructor(
        ip: String,
        SERVER_PORT: Int,
        dataInputStream: DataInputStream,
        dataOutputStream: DataOutputStream,
        sendRFIDScannedData: SendRFIDScannedData?,
        context: Context
    ) : Runnable {
        val ip = ip
        val SERVER_PORT = SERVER_PORT
        val sendRFIDScannedData = sendRFIDScannedData

        //        var dataInputStream = dataInputStream
//        var dataOutputStream = dataOutputStream
        val context = context
        lateinit var handler: Handler

        override fun run() {
            while (true) {
                try {
                    var message: String = dataInputStream!!.readUTF()
                    if (message != null) {
                        handler = Handler(Looper.getMainLooper())
                        handler.post(Runnable {
                            //update the ui
                            Log.d("xx", "In lib RFIDScannedData: $message")
//                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            sendRFIDScannedData?.onDataReceived(message)
                        })

                    } else {
                        val th1 = Thread1(ip, SERVER_PORT,sendRFIDScannedData, context)
                        Thread(th1).start()
                        return
                    }
                } catch (e: IOException) {
                    isConnectedToRFID = false
                    e.printStackTrace()
                }
            }
        }
    }

//    fun setSendRFIDScannedDataListener(sendRFIDScannedData1: SendRFIDScannedData) {
//        sendRFIDScannedData = sendRFIDScannedData1
//    }
//
//
//    fun releaseSendRFIDScannedDataListener() {
////        sendRFIDScannedData = null
//    }


    // interface for sending RFID scanned data
    interface SendRFIDScannedData {
        fun onDataReceived(data: String)
    }

    companion object{
        var isConnectedToRFID: Boolean = false
    }

}
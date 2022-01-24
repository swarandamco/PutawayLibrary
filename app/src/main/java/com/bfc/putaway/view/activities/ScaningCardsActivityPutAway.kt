package com.bfc.putaway.view.activities

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import com.bfc.putaway.R
import com.bfc.putaway.apirequests.PutawayRestServiceHandler
import io.reactivex.disposables.Disposable
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.lang.ref.WeakReference
import java.net.Socket

class ScaningCardsActivityPutAway : PutAwayBaseActivity() {

    var disposable: Disposable? = null
    val appApiServe by lazy {
        PutawayRestServiceHandler.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scaning_cards)

        val task = MyAsyncTask(this)
        task.execute()
    }

    class MyAsyncTask internal constructor(context: ScaningCardsActivityPutAway) : AsyncTask<Int, String, String?>() {

        private var resp: String? = null
        private val activityReference: WeakReference<ScaningCardsActivityPutAway> = WeakReference(context)

        override fun onPreExecute() {
            val activity = activityReference.get()
            if (activity == null) return
        }

        override fun doInBackground(vararg params: Int?): String? {

            var pingSocket: Socket? = null
            var out: PrintWriter? = null
            var br: BufferedReader? = null
            val host = "172.29.18.151"
            val port = 2020

            var palet_Id: String? = null
            var id_found: Boolean = false
            var tfmArray = mutableListOf<Int>()
            var id_Array = mutableListOf<String>()
            var distanceFromReader = 0

            try {
                pingSocket = Socket(host, port)
                out = PrintWriter(pingSocket.getOutputStream(), true)
                br = BufferedReader(InputStreamReader(pingSocket.getInputStream()))
//                br.ready()
            } catch (e: IOException) {
                return null
            }

            while (br.readLine() != null) {
                Log.d("xx", "data is :- " + br.readLine())
                resp = br.readLine()

//                id_Array.add(resp.toString())

                Log.d("xx", "id_found is: " + id_found)
                if (!id_found && resp!!.startsWith('@')) {

                    /*out.close()
                    br.close()
                    pingSocket.close()*/

                    id_found = true
                    var paletArray = resp.toString().split(",").toTypedArray()
                    palet_Id = paletArray[0]
                    Log.d("xx", "palet_Id is :- " + palet_Id)
//                    return resp
                }

                if (id_found && resp!!.contains("TFM")) {
                    var tfm_data: String = resp.toString()
                    Log.d("xx", "TMF data is :- " + tfm_data)
//                    var tfm_val = tfm_data.substring(10)
                    var tfm_array = tfm_data.split(",").toTypedArray()
                    var tfm_val = tfm_array[tfm_array.size - 1]
                    Log.d("xx", "TMF val is :- " + tfm_val)
//                    tfmArray.plus(tfm_val.toInt())
                    tfmArray.add(tfm_val.toInt())

                    if (tfmArray.size > 1) {
                        val largestElement = tfmArray.maxOrNull()
                        Log.d("xx", "max distance of tfm is: " + largestElement)
                        Log.d("xx", "distance at scan time: " + tfmArray.first())

                        if (largestElement != null) {
                            if (largestElement > (tfmArray.first() + 50)) {
                                Log.d("xx", "Item Droped on Slot Completed.. Yeah")
                                Log.d(
                                    "xx",
                                    "Pallet Id is:- " + palet_Id + "  Initial distance was:- "
                                            + tfmArray.first() + " Current Distance is:- " + largestElement
                                )

                                try {
                                    out.println("RFID TAGTABLE ENABLED" + host)
                                    Log.d("xx", "data after ENABLED: " + br.readLine())

                                    out.println("RFID TAGTABLE CLEAR" + host)
                                    Log.d("xx", "data after CLEAR:  " + br.readLine())

                                    out.println("RFID READ" + host)
                                    Log.d("xx", "data after READ:  " + br.readLine())

                                    /*out.println("RFID TAGTABLE" + host)
                                    Log.d("xx", "data after TAGTABLE:  " + br.readLine())*/

                                    id_found = false
                                    tfmArray.clear()

                                } catch (e: Exception) {
                                    Log.e("xx", "Error is:- " + e.printStackTrace())
                                }

                            }
                        }
                    }
                }

            }

            return resp
        }

        override fun onPostExecute(result: String?) {
            val activity = activityReference.get()
//            activity?.tv_errorMsg?.text = result
        }

        override fun onProgressUpdate(vararg values: String?) {
            super.onProgressUpdate(*values)
            Log.d("xx", "on progress data " + values)
        }
    }


//    private fun callProgramPTA01PR() {
//        showLoading()
//        var mMap = ArrayMap<String, String>().apply {
//            put("lib", "")
//            put("programname", "PTA01PR")
//            put("input", "")
//            put("whouse", "")
//            put("license", "")
//            put("licenseRfid", "")
//            put("uid", "")
//            put("whereFrom", "")
//            put("buttonClick", "")
//        }
//
//        disposable = appApiServe.postApiCall(mMap)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ result ->
//                hideLoading()
//                Log.d("xx", "PTA01PR data is:- " + result)
//            }, { error ->
//                hideLoading()
//                Toast.makeText(applicationContext, "Server Connection Error!", Toast.LENGTH_SHORT).show()
//            })
//
//    }

}

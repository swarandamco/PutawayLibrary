package com.bfc.putaway.util;

import android.app.Application;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

public class MyPersonalApp extends Application {
//    static private final Logger myAppLog =
//            LoggerFactory.getLogger(MyPersonalApp.class);

    public static String FILE_DESTINATION = "";
    public static String FILE_NAME = "";
    public static File logFile;
    public static Process process;
//    public static String FILE_NAME_DUMMY = "";

    /**
     * Called when the application is starting, before any activity, service, or receiver objects (excluding content providers) have been created.
     */
    public void onCreate() {
        super.onCreate();

//        HyperLog.initialize(this);
//        HyperLog.setLogLevel(Log.VERBOSE);
//
//        writeLogsToFile();
    }

    public void writeLogsToFile() {

        if (isExternalStorageWritable()) {
            File appDirectory = new File(Environment.getExternalStorageDirectory() + "/Putaway_Data");
            File logDirectory = new File(appDirectory + "/log");
            FILE_NAME = "log_"+ System.currentTimeMillis() + ".txt";
//            FILE_NAME_DUMMY = "logcat1577689957988.txt";
            logFile = new File(logDirectory, FILE_NAME);
//            File logFile = new File(logDirectory, "logcat.txt");

            FILE_DESTINATION = logDirectory.toString();


            // create app folder
            if (!appDirectory.exists()) {
                appDirectory.mkdir();
            }

            // create log folder
            if (!logDirectory.exists()) {
                logDirectory.mkdir();
            }

            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

           /* myAppLog.debug("rajesh says");
            Log.d("xx", "appDirectory is:- " + appDirectory + " and logDirectory is:- " + logDirectory);
            myAppLog.debug("appDirectory is:- ");

            Log.d("xx", "getName " + myAppLog.getName());*/

            // clear the previous logcat and then write the new one to the file
            try {
                process = Runtime.getRuntime().exec("logcat -c");
                process = Runtime.getRuntime().exec("logcat -f " + logFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (isExternalStorageReadable()) {
            // only readable
        } else {
            // not accessible
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }








}

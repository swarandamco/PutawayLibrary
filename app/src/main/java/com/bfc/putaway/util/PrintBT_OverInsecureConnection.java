//package com.bfc.putaway.util;
//
//import android.os.Looper;
//import android.util.Log;
//
//import com.zebra.sdk.comm.BluetoothConnectionInsecure;
//import com.zebra.sdk.comm.Connection;
//import com.zebra.sdk.comm.ConnectionException;
//
//public class PrintBT_OverInsecureConnection {
//
//    public interface PrintBT_OverInsecureConnectionListener {
//        void printingDone(String macAddress);
//
//        void printingError(String errorMassage, String zpl, String mac);
//    }
//
//    private PrintBT_OverInsecureConnectionListener listener;
//    private boolean isPrinting = false;
//    public static Connection thePrinterConn;
//    public static Boolean iSMacOkay = false;
//
//    // static variable single_instance of type PrintBT_OverInsecureConnection
//    private static PrintBT_OverInsecureConnection single_instance = null;
//
//    private PrintBT_OverInsecureConnection() {
//        this.listener = null;
//        isPrinting = false;
//    }
//
//    public static PrintBT_OverInsecureConnection getInstance() {
//        if (single_instance == null)
//            single_instance = new PrintBT_OverInsecureConnection();
//
//        return single_instance;
//    }
//
//
//    public void connectToBluetooth(String macAddress) {
//        if (thePrinterConn != null) {
//            if (!thePrinterConn.isConnected()) {
//                thePrinterConn = new BluetoothConnectionInsecure(macAddress);
////                new Thread(new Runnable() {
////                    public void run() {
//                try {
//                    thePrinterConn.open();
//                    iSMacOkay = true;
//                    Log.d("xx", "connect to bluetooth printer" + macAddress);
//                } catch (ConnectionException e) {
//                    iSMacOkay = false;
//                    if (listener != null) {
//                        listener.printingError(e.getMessage(), "", "");
//                    } else {
//                        e.printStackTrace();
//                    }
//                    Log.e("xx", "error on connect to bluetooth printer");
//                }
////                    }}).start();
//
//            } else {
//                Log.d("xx", "already connected to bluetooth printer" + macAddress);
//                disconnectBluetoothConnection();
//                connectToBluetooth(macAddress);
//            }
//        } else {
//            thePrinterConn = new BluetoothConnectionInsecure(macAddress);
////            new Thread(new Runnable() {
////                public void run() {
//            try {
//                thePrinterConn.open();
//                iSMacOkay = true;
//                Log.d("xx", "connect to bluetooth printer" + macAddress);
//            } catch (ConnectionException e) {
//                iSMacOkay = false;
//                if (listener != null) {
//                    listener.printingError(e.getMessage(), "", "");
//                } else {
//                    e.printStackTrace();
//                }
//                Log.e("xx", "error on connect to bluetooth printer");
//            }
////                }}).start();
//        }
//    }
//
//    public void disconnectBluetoothConnection() {
////        new Thread(new Runnable() {
////            public void run() {
//        try {
//            if(thePrinterConn != null){
//                thePrinterConn.close();
//            }
//        } catch (ConnectionException e) {
//            if (listener != null) {
//                listener.printingError(e.getMessage(), "", "");
//            } else {
//                e.printStackTrace();
//            }
//        }
//        Log.d("xx", "disconnect to bluetooth printer");
//    }
////        }).start();
////
////    }
//
//    public void setListener(PrintBT_OverInsecureConnectionListener listener) {
//        this.listener = listener;
//    }
//
//    public void print(final String printerMacAddress, final String zplData) {
//        if (isPrinting) {
//            listener.printingError("Printer busy", zplData, printerMacAddress);
//            return;
//        }
//
//        isPrinting = true;
//        new Thread(new Runnable() {
//            public void run() {
//                try {
//                    // Instantiate insecure connection for given Bluetooth&reg; MAC Address.
////                    Connection thePrinterConn = new BluetoothConnectionInsecure(printerMacAddress);
//
//                    // Initialize
//                    Looper.prepare();
//
//                    // Open the connection - physical connection is established here.
////                    thePrinterConn.open();
//                    if (thePrinterConn != null) {
//                        if (thePrinterConn.isConnected()) {
//                            // Send the data to printer as a byte array.
//                            thePrinterConn.write(zplData.getBytes());
//                            if (listener != null) {
//                                listener.printingDone(printerMacAddress);
//                            }
//                        } else {
//                            Log.d("xx", "not connected with btl");
//                        }
//                    } else {
//                        Log.d("xx", "not connected with btl");
//                    }
//                    // Make sure the data got to the printer before closing the connection
////                    Thread.sleep(300);
//
//                    // Close the insecure connection to release resources.
////                    thePrinterConn.close();
//
//                    Looper.myLooper().quit();
////                    if (listener != null) {
////                        listener.printingDone(printerMacAddress);
////                    }
//                    isPrinting = false;
//                } catch (Exception e) {
//                    if (listener != null) {
//                        listener.printingError(e.getMessage(), "", "");
//                    } else {
//                        e.printStackTrace();
//                    }
//                    isPrinting = false;
//                }
//            }
//        }).start();
//    }
//}

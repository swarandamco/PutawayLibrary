//package com.bfc.putaway.util;
//
//import android.content.Context;
//import android.util.Log;
//
//import com.symbol.emdk.EMDKManager;
//import com.symbol.emdk.EMDKManager.EMDKListener;
//import com.symbol.emdk.EMDKResults;
//import com.symbol.emdk.barcode.BarcodeManager;
//import com.symbol.emdk.barcode.ScanDataCollection;
//import com.symbol.emdk.barcode.Scanner;
//import com.symbol.emdk.barcode.Scanner.DataListener;
//import com.symbol.emdk.barcode.Scanner.StatusListener;
//import com.symbol.emdk.barcode.ScannerConfig;
//import com.symbol.emdk.barcode.ScannerException;
//import com.symbol.emdk.barcode.ScannerInfo;
//import com.symbol.emdk.barcode.ScannerResults;
//import com.symbol.emdk.barcode.StatusData;
//
//import java.util.ArrayList;
//
//public class EMDKScannerWithDoubleClick implements EMDKListener, StatusListener, DataListener, BarcodeManager.ScannerConnectionListener {
//    // Application Context
//    private static Context context;
//    // Declare a variable to store EMDKManager object
//    private EMDKManager emdkManager = null;
//    // Declare a variable to store Barcode Manager object
//    private BarcodeManager barcodeManager = null;
//    // Declare a variable to hold scanner device to scan
//    private Scanner scanner = null;
//
//    // boolean flag to start scanning after scanner initialization
//    // Used in OnStatus callback to ensure scanner is idle before read() method is called
//    private boolean startRead = false;
//
//    // Double CLick COntrol Variables
//    private final StringBuffer statusDataOut = new StringBuffer();
//    private long doubleClickFirstClick = 0;
//    private long doubleClickSecondClick = 0;
//    private long betweenDoubleClickSpace = 0;
//
//    private boolean isGettingData = false;
//    private long doubleClickOffset = 600; // Default DoubleClick Offset
//
//    private boolean setScannerSupportedLabels = true;
//
//// SECTION:
//// 1) PUBLIC METHODS
//// 2) SETTING LISTENERS IMPLEMENTATIONS
//// 3) EMDK LISTENERS IMPLEMENTATIONS
//// 4) PRIVATE METHODS
//
//
//    // 1) PUBLIC METHODS
//// BEGINING
//    public void setDoubleClickOffset(long doubleClickOffset) {
//        this.doubleClickOffset = doubleClickOffset;
//    }
//
//    public void startScanning() {
//        setScannerSupportedLabels = true;
//
//        if (scanner != null) {
//            try {
//                if (scanner.isEnabled() && !scanner.isReadPending()) {
//                    startRead = true;
//                    scanner.read();
//                }
//            } catch (ScannerException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public void stopScanning() {
//        if (scanner != null) {
//            try {
//                startRead = false;
//                scanner.cancelRead();
//            } catch (ScannerException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//// END
//// 1) PRIVATE METHODS
//// ---------------------------------------------
//
//    // 2) SETTING LISTENERS IMPLEMENTATIONS
//// BEGINING
//    // DOUBLE CLICK EVENT/LISTENER IMPLEMENTATION
//    // add a private listener variable for the double click listener
//    private DoubleClickScannerListener dblClkListener = null;
//
//    // provide a way for another class to set the double click listener
//    public void setDoubleClickScannerListener(DoubleClickScannerListener dblClkListener) {
//        this.dblClkListener = dblClkListener;
//    }
//
//    // provide a way for another class to release the double click listener
//    public void releaseDoubleClickScannerListener() {
//        this.dblClkListener = null;
//    }
//
//
//    private ConnectionScannerListener scannerListener = null;
//
//    // provide a way for another class to set the double click listener
//    public void setConnectionScannerListener(ConnectionScannerListener scannerListener) {
//        this.scannerListener = scannerListener;
//
//        if (emdkManager != null) {
//            barcodeManager = (BarcodeManager) emdkManager.getInstance(EMDKManager.FEATURE_TYPE.BARCODE);
//            // Add connection listener
//            if (barcodeManager != null) {
//                barcodeManager.addConnectionListener(this);
//            }
//        }
//    }
//
//    // provide a way for another class to release the double click listener
//    public void releaseConnectionScannerListener() {
//        this.scannerListener = null;
//        // Remove connection listener
//        if (barcodeManager != null) {
//            barcodeManager.removeConnectionListener(this);
//            barcodeManager = null;
//        }
//    }
//
//
//    @Override
//    public void onConnectionChange(ScannerInfo scannerInfo, BarcodeManager.ConnectionState connectionState) {
//        Log.d("xx", "inside sdk listner...");
//        switch (connectionState) {
//            case CONNECTED:
//                try {
//                    deInitializeScanner();
////                    initializeScanner();
//                } catch (ScannerException e) {
//                    e.printStackTrace();
//                }
//
//                scannerListener.onScannerConnection("CONNECTED");
//                break;
//            case DISCONNECTED:
//                try {
//                    deInitializeScanner();
//                } catch (ScannerException e) {
//                    e.printStackTrace();
//                }
//                scannerListener.onScannerConnection("DISCONNECTED");
//                break;
//            default:
//                break;
//        }
//    }
//
//    // interface for double click listener
//    public interface DoubleClickScannerListener {
//        void onScannerDoubleClick();
//    }
//
//    // interface for double click listener
//    public interface ConnectionScannerListener {
//        void onScannerConnection(String status);
//    }
//
//    // SCANNED BARCODE EVENT/LISTENER IMPLEMENTATION
//    // add a private listener variable for the scanned barcode listener
//    private ScannedBarcodeListener barcodeScannedListener = null;
//
//    // provide a way for another class to set the scanned barcode listener
//    public void setScannedBarcodeListener(ScannedBarcodeListener barcodeScannedListener) {
//        this.barcodeScannedListener = barcodeScannedListener;
//    }
//
//    // provide a way for another class to release the scanned barcode listener
//    public void releaseScannedBarcodeListener() {
//        this.barcodeScannedListener = null;
//    }
//
//    // interface for scanned barcode listener
//    public interface ScannedBarcodeListener {
//        void onScannedBarcode(String barcode, String barcodeType);
//    }
//// END
//// SETTING LISTENERS IMPLEMENTATIONS
//// ---------------------------------------------
//
//    // 3) EMDK LISTENERS IMPLEMENTATIONS
//// BEGINING
//    public void onCreate(Context context) {
//        // The EMDKManager object will be created and returned in the callback.
//        EMDKResults results = EMDKManager.getEMDKManager(context.getApplicationContext(), this);
//    }
//
//    @Override
//    public void onOpened(EMDKManager emdkManager) {
//        this.emdkManager = emdkManager;
//        try {
//            // Call this method to enable Scanner and its listeners
//            initializeScanner();
//            startScanning();
//
//            // Acquire the barcode manager resources
//            barcodeManager = (BarcodeManager) emdkManager.getInstance(EMDKManager.FEATURE_TYPE.BARCODE);
//            // Add connection listener
//            if (barcodeManager != null) {
//                barcodeManager.addConnectionListener(this);
//            }
//
//        } catch (ScannerException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onClosed() {
//        try {
//            stopScanning();
//            deInitializeScanner();
//            if (emdkManager != null) {
//                // Remove connection listener
//                if (barcodeManager != null) {
//                    barcodeManager.removeConnectionListener(this);
//                    barcodeManager = null;
//                }
//            }
//        } catch (ScannerException e) {
//            e.printStackTrace();
//        }
//        // The EMDK closed abruptly. // Clean up the objects created by EMDK manager
//        if (this.emdkManager != null) {
//            this.emdkManager.release();
//            this.emdkManager = null;
//        }
//    }
//
//    @Override
//    public void onData(ScanDataCollection scanDataCollection) {
//        isGettingData = true;
//        resetDoubleClickAttr();
//        Log.d("xs", "onScan - doble clk reset");
//
//        String barcodeData = "";
//        String labelType = "";
//
//        // The ScanDataCollection object gives scanning result and the
//        // collection of ScanData. So check the data and its status
//        if (scanDataCollection != null && scanDataCollection.getResult() == ScannerResults.SUCCESS) {
//            ArrayList<ScanDataCollection.ScanData> scanData = scanDataCollection.getScanData();
//
//            // Iterate through scanned data and prepare the statusStr
//            for (ScanDataCollection.ScanData data : scanData) {
//                // cancel loop if value is captured on Scanning
//                if (!barcodeData.equals(""))
//                    break;
//
//                // Get the scanned data
//                barcodeData = data.getData();
//                // Get the type of label being scanned
//                ScanDataCollection.LabelType labelTypeInfo = data.getLabelType();
//                labelType = labelTypeInfo.toString();
//                Log.d("xs", "onScan ->:" + barcodeData + " - " + labelType);
//            }
//        }
//        isGettingData = false;
//
//        // Sending event
//        barcodeScannedListener.onScannedBarcode(barcodeData, labelType);
//    }
//
//    @Override
//    public void onStatus(StatusData statusData) {
//        // Get the current state of scanner in background
//        StatusData.ScannerStates state = statusData.getState();
//        Log.d("xs", "iscanner state :" + state +
//                " doubleClickFirstClick :" + doubleClickFirstClick +
//                " doubleClickSecondClick :" + doubleClickSecondClick
//        );
//
//        // Different states of Scanner
//        switch (state) {
//            case SCANNING: // Scanner is SCANNING
//
//                if (doubleClickFirstClick == 0) {
//                    doubleClickFirstClick = System.currentTimeMillis();
//                    Log.d("xx", "inside doubleClickFirstClick :" + doubleClickFirstClick);
//                } else {
//                    Log.d("xx", "inside doubleClickSecondClick :" + doubleClickSecondClick);
//                    if (doubleClickSecondClick == 0) {
//                        doubleClickSecondClick = System.currentTimeMillis();
//                    }
//                    betweenDoubleClickSpace = doubleClickSecondClick - doubleClickFirstClick;
//                    Log.d("xx", " doubleClickFirstClick :" + doubleClickFirstClick +
//                            " doubleClickSecondClick :" + doubleClickSecondClick +
//                            " click gap. " + betweenDoubleClickSpace +
//                            " click offset. " + doubleClickOffset);
//
//                    if (betweenDoubleClickSpace <= (doubleClickOffset + 200)) {
//                        Log.d("xx", "inside if..  " + betweenDoubleClickSpace);
//                        resetDoubleClickAttr();
//                        dblClkListener.onScannerDoubleClick();
//                    } else {
//                        Log.d("xx", "inside Else.. " + betweenDoubleClickSpace);
//
//                        doubleClickFirstClick = doubleClickSecondClick;
//                        doubleClickSecondClick = 0;
//                    }
////                    try {
////                        scanner.cancelRead();
////                    } catch (ScannerException e) {
////                        e.printStackTrace();
////                    }
//                }
//                Log.d("xx", " doubleClickFirstClick :" + doubleClickFirstClick +
//                        " doubleClickSecondClick :" + doubleClickSecondClick +
//                        " click gap. " + betweenDoubleClickSpace +
//                        " click offset. " + doubleClickOffset);
//                break;
//            case IDLE: // Scanner is IDLE
//
//                try {
//
//                    if (setScannerSupportedLabels) {
//                        if (!scanner.isReadPending()) {
//                            Log.d("xx", "setting scanner barcodes ..");
//                            setDecoders();
//                        }
//                        setScannerSupportedLabels = false;
//                        try {
//                            Thread.sleep(300);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    if (scanner.isEnabled() && !scanner.isReadPending() && startRead) {
//                        Log.d("xx", "scanner reading..");
//                        scanner.read(); // Prepare scanner for next read
//                    }
//
//                } catch (ScannerException e) {
//                    e.printStackTrace();
//                }
//
//                break;
//            case WAITING: // Scanner is waiting for trigger press
//                break;
//            case DISABLED: // Scanner is not enabled
//                break;
//            case ERROR: // Error on scanner
//                break;
//            default:
//                break;
//        }
//
//    }
//
//
//    private void setDecoders() {
//        if ((scanner != null) && (scanner.isEnabled())) {
//            try {
//                ScannerConfig config = scanner.getConfig();
////                config.skipOnUnsupported = ScannerConfig.SkipOnUnSupported.NONE;
////                config.scanParams.decodeLEDFeedback = true;
////                config.readerParams.readerSpecific.imagerSpecific.picklistEx = ScannerConfig.PicklistEx.SOFTWARE;
//                config.decoderParams.ean8.enabled = true;
//                config.decoderParams.ean13.enabled = true;
//                config.decoderParams.code39.enabled = true;
//                config.decoderParams.code93.enabled = true;
//                config.decoderParams.code128.enabled = true;
//                config.decoderParams.gs1Databar.enabled = true;
//                config.decoderParams.gs1DatabarExp.enabled = true;
//                config.decoderParams.dataMatrix.enabled = true;
//                config.decoderParams.qrCode.enabled = true;
//                config.decoderParams.upca.enabled = true;
//                config.decoderParams.upce0.enabled = true;
//                config.decoderParams.upce1.enabled = true;
//                //config.decoderParams.chinese2of5.enabled = true;
//                //config.decoderParams.matrix2of5.enabled = true;
//                //config.decoderParams.d2of5.enabled = true;
//
//                // i2of5 configuration parameters
//                config.decoderParams.i2of5.enabled = true;
//                config.decoderParams.i2of5.convertToEan13 = false;
//                config.decoderParams.i2of5.length1 = 0;
//                config.decoderParams.i2of5.length2 = 55;
//                config.decoderParams.i2of5.reducedQuietZone = false;
//                config.decoderParams.i2of5.redundancy = true;
//                config.decoderParams.i2of5.reportCheckDigit = false;
//                config.decoderParams.i2of5.verifyCheckDigit = ScannerConfig.CheckDigitType.NO;
//                config.decoderParams.i2of5.securityLevel = ScannerConfig.SecurityLevel.LEVEL_1;
//
//                scanner.setConfig(config);
//                Log.d("xx", "decoder setup complete");
//            } catch (ScannerException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//// END
//// EMDK LISTENERS IMPLEMENTATIONS
//// ---------------------------------------------
//
//// 4) PRIVATE METHODS
//// BEGINING
//
//    // Method to initialize and enable Scanner and its listeners
//    private void initializeScanner() throws ScannerException {
//        if (scanner == null) {
//            setScannerSupportedLabels = true;
//
//            // Get the Barcode Manager object
//            barcodeManager = (BarcodeManager) this.emdkManager.getInstance(EMDKManager.FEATURE_TYPE.BARCODE);
//            // Get default scanner defined on the device
//            scanner = barcodeManager.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT);
//            if (scanner != null) {
//                // Add data and status listeners
//                scanner.addStatusListener(this::onStatus);
//                scanner.addDataListener(this::onData);
//                // Hard trigger. When this mode is set, the user has to manually
//                // press the trigger on the device after issuing the read call.
//                scanner.triggerType = Scanner.TriggerType.HARD;
//                // Enable the scanner
//                scanner.enable();
//                //set startRead flag to true. this flag will be used in the OnStatus callback to insure
//                //the scanner is at an IDLE state and a read is not pending before calling scanner.read()
//                startRead = true;
//                setDecoders();
//            }
//        }
//
//    }
//
//    private void deInitializeScanner() throws ScannerException {
//        // If there is a scanner attached
//        if (scanner != null) {
//            try {
//                // Cancel reading if the scanner is trying to read a barcode
//                if (scanner.isReadPending()) {
//                    scanner.cancelRead();
//                }
//                scanner.disable();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            try {
//                //Un-link scanner listeners
//                scanner.removeDataListener(this);
//                scanner.removeStatusListener(this);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            try {
//                // Release scanner
//                scanner.release();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            scanner = null;
//        }
//    }
//
//    private void resetDoubleClickAttr() {
//        doubleClickFirstClick = 0;
//        doubleClickSecondClick = 0;
//    }
//
//// END
//// PRIVATE METHODS
////
//
//}
//
//
//

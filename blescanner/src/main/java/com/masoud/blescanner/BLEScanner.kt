package com.masoud.blescanner

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import java.util.*

/**
 * Created by Masoud Darzi, Email : masouddarzi@gmail.com
 * make sure to pass context and make the UUID and also the time to stop scanning optional
 */

//TODO: create singleTone  pattern for initializing
//TODO: create builder pattern for passing UUID for filtering for example Sensoro or Kontakt

private const val SCAN_PERIOD: Long = 10000

private var isScanning: Boolean = false

class BLEScanner(private var applicationContext: Context) {
    private val TAG = BLEScanner::class.java.simpleName


    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager =
            applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val handler: Handler by lazy {
        Handler()
    }

    private val BluetoothAdapter.isDisabled: Boolean
        get() = !isEnabled

    fun startScan() {
        if (bluetoothAdapter?.isDisabled!!) {
            //Throws Exception or return a call back for enabling the bluetooth
            Log.d(TAG, "make sure you have the bluetoot permission and it's turned on")
        } else {
            Log.d(TAG, "staring the BLE scanning")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                scanBleVersionAboveLollipop()
            } else {
                scanBleVersionBelowLollipop()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun scanBleVersionAboveLollipop() {

        val filters: List<ScanFilter> = ArrayList()
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        val leScanner = bluetoothAdapter?.bluetoothLeScanner

        val leScanCallBaclAboveLollipop = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                //TODO: stop after finding the desired device!
                if (getDevice(result?.device)) {
                    leScanner?.stopScan(this)
                    isScanning = false
                }
                Log.d(TAG, "device is : ${result?.device?.address}")

            }

            override fun onScanFailed(errorCode: Int) {
                Log.d(TAG, "failed to scan: make sure you grant the location permission")
            }
        }

        leScanner?.startScan(filters, scanSettings, leScanCallBaclAboveLollipop)
        isScanning = true
        handler.postDelayed({
            if (isScanning) {
                leScanner?.stopScan(leScanCallBaclAboveLollipop)
                isScanning = false
            }
        }, SCAN_PERIOD)

    }

    private fun scanBleVersionBelowLollipop() {

        val leScanCallbackBelowLollipop = object : BluetoothAdapter.LeScanCallback {
            override fun onLeScan(device: BluetoothDevice?, rssi: Int, scanRecord: ByteArray?) {
                //TODO: stop after finding the desired device!
                if (getDevice(device)) {
                    bluetoothAdapter?.stopLeScan(this)
                    isScanning = false
                }
                Log.d(TAG, "device is : ${device?.address} and rssi is : $rssi")
            }
        }

        bluetoothAdapter?.startLeScan(leScanCallbackBelowLollipop)
        isScanning = true
        handler.postDelayed({
            if (isScanning) {
                bluetoothAdapter?.stopLeScan(leScanCallbackBelowLollipop)
                isScanning = false
            }
        }, SCAN_PERIOD)
    }


    private fun getDevice(device: BluetoothDevice?): Boolean {
        //if found return true
        return false
    }
}
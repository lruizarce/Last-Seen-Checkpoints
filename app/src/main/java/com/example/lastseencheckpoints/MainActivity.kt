package com.example.lastseencheckpoints

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private lateinit var mTextArea: TextView

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.i("Bluetooth", "BroadcastReceiver onReceive()")
            handleBTDevice(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        mTextArea = findViewById(R.id.discovered_devices)

        setUpBroadcastReceiver()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    Log.i("Bluetooth", "Fine_Location Permission granted")
                    setUpDiscovery()
                } else {
                    Log.i("Bluetooth", "Fine_Location Permission refused")
                }
                return
            }
        }
    }

    private fun setUpBroadcastReceiver() {
        if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                ACCESS_FINE_LOCATION)
            Log.i("Bluetooth", "Getting Permission")
            return
        }

        setUpDiscovery()
    }

    private fun setUpDiscovery() {
        Log.i("Bluetooth", "Activating Discovery")
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mReceiver, filter)
        mBluetoothAdapter.startDiscovery()
    }

    private fun handleBTDevice(intent: Intent) {
        Log.i("Bluetooth", "handleBTDevice() -- starting")
        val action = intent.action

        Log.i("Bluetooth", action!!)
        Log.i("Bluetooth", BluetoothDevice.ACTION_FOUND)
        if (BluetoothDevice.ACTION_FOUND == action) {

            Log.i("Bluetooth", "CHECK")

            val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            val deviceName =
                if (device.name != null) {
                    device.name.toString()
                } else {
                    "--no name--"
                }

            Log.i("Bluetooth", deviceName + "\n" + device)
            mTextArea.text = mTextArea.text.toString() + ("$deviceName, $device \n\n")
        }
    }

    companion object {
        private const val ACCESS_FINE_LOCATION = 1
    }
}

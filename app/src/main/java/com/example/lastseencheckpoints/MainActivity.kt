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
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.core.app.ActivityCompat
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.lang.Exception
import java.time.LocalDateTime
import kotlin.text.StringBuilder

class MainActivity : AppCompatActivity() {
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private lateinit var inputFilter: EditText
    private lateinit var listOfDevicesDisplay: TextView

    private val fileName = "lastSeenLog.txt"
    private val listOfDevices = ArrayList<String>()
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
        inputFilter = findViewById(R.id.mac_address_filter_input)
        listOfDevicesDisplay = findViewById(R.id.discovered_devices)

        inputFilter.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s : Editable) {

            }

            override fun beforeTextChanged(s : CharSequence, start : Int, count : Int, after : Int) {

            }

            override fun onTextChanged(s : CharSequence, start : Int, before : Int, count : Int) {
                displayDevices()
            }
        })

        setUpBroadcastReceiver()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.i("Bluetooth", "PERMISSION RESULT")

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

        val thread = object: Thread() {
            override fun run() {
                while (true) {
                    clearDevicesAndDisplay()
                    setUpDiscovery()
                    sleep(15000)
                    logDevicesFound()
                }
            }
        }

        thread.start()
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
            val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            val deviceName =
                if (device.name != null) {
                    device.name.toString()
                } else {
                    "--no name--"
                }

            Log.i("Bluetooth", deviceName + "\n" + device)

            listOfDevices.add("$deviceName\n$device")

            displayDevices()
        }
    }

    private fun displayDevices() {
        val listOfDevicesStringBuilder = StringBuilder()

        for (device in listOfDevices) {
            if (inputFilter.text.isEmpty()) {
                listOfDevicesStringBuilder.append(device).append("\n\n")
            } else if (device.contains(inputFilter.text.toString())) {
                listOfDevicesStringBuilder.append(device).append("\n\n")
            }

            listOfDevicesDisplay.text = listOfDevicesStringBuilder.toString()
        }
    }

    private fun clearDevicesAndDisplay() {
        listOfDevices.clear()
        listOfDevicesDisplay.text = ""
    }

    fun logDevicesFound() {
        val currentTime = LocalDateTime.now()
        val logBatchStringBuilder = StringBuilder()
        val fileOutputStream : FileOutputStream

        for (device in listOfDevices) {
            logBatchStringBuilder.append(device.split("\n")[1] + " " + currentTime).append("\n")
        }

        try {
            fileOutputStream = openFileOutput(fileName, Context.MODE_APPEND)
            fileOutputStream.write(logBatchStringBuilder.toString().toByteArray())
            fileOutputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Log.i("Bluetooth_Logging", "SUCCESSFULLY LOGGED")
        Log.i("Bluetooth_Logging", logBatchStringBuilder.toString())
    }

    companion object {
        private const val ACCESS_FINE_LOCATION = 1
    }
}

package com.example.last_seen_checkpoints

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
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
import android.widget.Toast
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mTextarea: TextView? = null
    private val mReceiver = object : BroadcastReceiver(){
        override  fun onReceive(context: Context, intent: Intent){
            Log.i("BT", "BroadcastReceiver onReceive()")
            handleBTDevice(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mTextarea = findViewById(R.id.discovered_devices)
        setUpBroadcastReceiver()
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
//        Log.i("BT", "onActivityResult(): requestCode = $requestCode")
//        if (requestCode == REQUEST_ENABLE_BT){
//            if (resultCode == Activity.RESULT_OK){
//                Log.i("BT", "-- Bluetooth is enabled")
//                setUpBroadcastReceiver()
//            }
//        }
//    }

    private fun setUpBroadcastReceiver() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                ACCESS_FINE_LOCATION)
            Log.i("BT", "Getting Permission")
            return
        }

        setupDiscovery()

    }

    private fun setupDiscovery (){
        Log.i("BT", "Activating Discovery")
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mReceiver, filter)
        mBluetoothAdapter!!.startDiscovery()
    }

    private fun handleBTDevice(intent:Intent){
        Log.i ("BT", "HandleBRDevice() -- starting <<<< -----------")
        val action = intent.action
        if (BluetoothDevice.ACTION_FOUND == action){
            val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            val deviceName =
                if (device.name != null){
                    device.name.toString()

                } else {
                    "--no name--"
                }
            Log.i("BT", deviceName + "\n" + device)
            mTextarea!!.append("$deviceName, $device \n")
        }
    }

//    public override fun onResume(){
//        super.onResume()
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//        Log.i("BT", "onResume()")
//        if (mBluetoothAdapter == null){
//            Log.i ("BT", "No Bluetooth on this device")
//            Toast.makeText(baseContext,
//                "No Bluetooth on this device", Toast.LENGTH_LONG).show()
//
//        }else if (!mBluetoothAdapter!!.isEnabled){
//            Log.i("BT", "Enabling Bluetooth" )
//            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
//        }
//        Log.i("BT", "End of onResume()")
//    }



    companion object{
        private const val ACCESS_FINE_LOCATION = 1
        private const val REQUEST_ENABLE_BT = 3313
    }


}

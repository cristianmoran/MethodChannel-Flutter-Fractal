package com.example.app_mc

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.view.FlutterMain

class MainActivity: FlutterActivity(),MethodChannel.MethodCallHandler, EventChannel.StreamHandler {
//    private val CHANNEL = "samples.flutter.dev/battery"
//    private var methodChannel :MethodChannel?=null
//
//    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
//        super.configureFlutterEngine(flutterEngine)
//        methodChannel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger,CHANNEL)
//        methodChannel?.setMethodCallHandler(this)
//    }
//
//    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
//        if (call.method == "getBatteryLevel") {
//            val batteryLevel = getBatteryLevel()
//
//            if (batteryLevel != -1) {
//                result.success(batteryLevel)
//                result.success(batteryLevel)
//            } else {
//                result.error("UNAVAILABLE", "Battery level not available.", null)
//            }
//        } else {
//            result.notImplemented()
//        }
//    }
//
//    private fun getBatteryLevel(): Int {
//        val batteryLevel: Int
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
//            batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
//        } else {
//            val intent = ContextWrapper(applicationContext).registerReceiver(null, IntentFilter(
//                Intent.ACTION_BATTERY_CHANGED))
//            batteryLevel = intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100 / intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
//        }
//
//        return batteryLevel
//    }





    private val CHANNEL = "com/background_location"

    private var broadCastLocation: BroadcastReceiver? = null
    private var eventSink: EventChannel.EventSink? = null
    private lateinit var methodChannel: MethodChannel

    private var serviceIsRunning = false


    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

//        methodChannel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
//        methodChannel.setMethodCallHandler(this)

        EventChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setStreamHandler(this)
    }

    private fun iniciarServicio() {
        val intent = Intent(this, LocationUpdatesService::class.java)
        startService(intent)
    }

//    fun createChangeReceiver(): BroadcastReceiver {
//        return object : BroadcastReceiver() {
//            override fun onReceive(context: Context, intent: Intent) {
//                FlutterMain.startInitialization(context)
//                FlutterMain.ensureInitializationComplete(context, null)
//                val location =
//                    intent.getParcelableExtra<Location>(LocationUpdatesService.EXTRA_LOCATION)
//                if (location != null) {
////                    Toast.makeText(context, "ejecutando", Toast.LENGTH_SHORT)
////                        .show()
//                    val locationMap = HashMap<String, Any>()
//                    //Coordenadas
//                    serviceIsRunning = true
//                    locationMap["latitude"] = location.latitude
//                    locationMap["longitude"] = location.longitude
//
//                    Toast.makeText(context,"Las coordenadas son : ${location.latitude}  ${location.longitude}",Toast.LENGTH_LONG).show()
//                    methodChannel.invokeMethod("callTestResuls", locationMap)
//                }
//            }
//        }
//    }

    fun createChangeReceiver(events: EventChannel.EventSink?): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val location =
                    intent.getParcelableExtra<Location>(LocationUpdatesService.EXTRA_LOCATION)
                if (location != null) {
                    Toast.makeText(context, "ejecutando", Toast.LENGTH_LONG)
                        .show()
                    val locationMap = HashMap<String, Any>()
                    //Coordenadas
                    serviceIsRunning = true
                    locationMap["latitude"] = location.latitude
                    locationMap["longitude"] = location.longitude
                    events?.success(locationMap)
                }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() -> Log.i(this.javaClass.name, "Interacción cancelada")
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> initLocationUpdates()
                else -> Toast.makeText(context,"Permiso denegado", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun requestLocation() {
        if (!checkPermissions()) {
            requestPermissions()
        } else {
            initLocationUpdates()
        }
    }


    private fun checkPermissions(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
            Manifest.permission.ACCESS_FINE_LOCATION)
        if (shouldProvideRationale) {
            Toast.makeText(context,"Permiso necesario para compartir su localización", Toast.LENGTH_LONG).show()
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    private fun initLocationUpdates(){
        serviceIsRunning = true
        iniciarServicio()
    }

    companion object {
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
//        if (call.method == "startLocation") {
//            if (serviceIsRunning) {
//                Toast.makeText(
//                    context,
//                    "Es servicio ya se encuentra en ejecución",
//                    Toast.LENGTH_LONG
//                ).show()
//            } else {
//                broadCastLocation = createChangeReceiver()
//                LocalBroadcastManager.getInstance(this@MainActivity).registerReceiver(
//                    broadCastLocation!!,
//                    IntentFilter(LocationUpdatesService.ACTION_BROADCAST)
//                )
//                requestLocation()
//            }
//        }
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        eventSink = events
        if (serviceIsRunning) {
            Toast.makeText(context, "Es servicio ya se encuentra en ejecución", Toast.LENGTH_LONG)
                .show()
        } else {
            broadCastLocation = createChangeReceiver(events)
            LocalBroadcastManager.getInstance(this@MainActivity).registerReceiver(
                broadCastLocation!!,
                IntentFilter(LocationUpdatesService.ACTION_BROADCAST)
            )
            requestLocation()
        }

    }

    override fun onCancel(arguments: Any?) {
        TODO("Not yet implemented")
    }

}

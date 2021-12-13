package com.example.app_mc

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.*
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import mdp.altoke.Utils
import java.util.concurrent.TimeUnit


class LocationUpdatesService : Service() {

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    private val mBinder = LocalBinder()
    private var mNotificationManager: NotificationManager? = null
    private var mLocationRequest: LocationRequest? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationCallback: LocationCallback? = null
    private var mLocation: Location? = null
    private var isStarted: Boolean = false

    companion object {
        var NOTIFICATION_TITLE = "Pedido en reparto"
        var NOTIFICATION_MESSAGE = "Se están registrando las coordenadas hacia el destino"
        var NOTIFICATION_ICON = "@mipmap/ic_launcher"

        private val PACKAGE_NAME =
            "com.google.android.gms.location.sample.locationupdatesforegroundservice"
        private val TAG = "LocationUpdatesService"
        private val CHANNEL_ID = "channel_01"
        internal val ACTION_BROADCAST = "$PACKAGE_NAME.broadcast"
        internal val EXTRA_LOCATION = "$PACKAGE_NAME.location"
        private val EXTRA_STARTED_FROM_NOTIFICATION = "$PACKAGE_NAME.started_from_notification"
        var UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000
        val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2
        private val NOTIFICATION_ID = 12345678

        private val STOP_SERVICE = "stop_service"
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent != null) {
//            idPedido = intent.getIntExtra(CONST_ID_PEDIDO, 0)
//            baseUrl = intent.getStringExtra(CONST_BASE_URL) ?: ""
//            pathRest = intent.getStringExtra(CONST_PATH_REST) ?: ""

        } else {
            Utils.setRequestingLocationUpdates(this@LocationUpdatesService, false)
        }
        return START_STICKY
    }

    private val notification: NotificationCompat.Builder
        get() {

            val intent = Intent(this, getMainActivityClass(this))
            intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true)
            intent.action = "Localization"
            intent.setClass(this, getMainActivityClass(this)!!)
            val pendingIntent =
                PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            val builder = NotificationCompat.Builder(this, "BackgroundLocation")
                .setContentTitle(NOTIFICATION_TITLE)
                .setOngoing(true)
                .setSound(null)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(resources.getIdentifier(NOTIFICATION_ICON, "mipmap", packageName))
                .setWhen(System.currentTimeMillis())
                .setStyle(NotificationCompat.BigTextStyle().bigText(NOTIFICATION_MESSAGE))
                .setContentIntent(pendingIntent)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setChannelId(CHANNEL_ID)
            }

            return builder
        }

    private var mServiceHandler: Handler? = null

    override fun onCreate() {
        super.onCreate()
        createLocationRequest(0.0)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                onNewLocation(locationResult!!.lastLocation)
                if (Utils.requestingLocationUpdates(baseContext)) {
                } else {
                   // this@LocationUpdatesService.removeLocationUpdates()
                }
            }
        }

        getLastLocation()

        val handlerThread = HandlerThread(TAG)
        handlerThread.start()
        mServiceHandler = Handler(handlerThread.looper)

        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Altoke"
            val mChannel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_LOW)
            mChannel.setSound(null, null)
            mNotificationManager?.createNotificationChannel(mChannel)
        }

//        val filter = IntentFilter()
//        filter.addAction(STOP_SERVICE)

        requestLocationUpdates()
    }

    private fun requestLocationUpdates() {
        try {
            mFusedLocationClient?.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback!!, Looper.myLooper()
            )
            updateNotification()
        } catch (unlikely: SecurityException) {
            println("Exception 2: ${unlikely.message}")
        }
    }

    private fun updateNotification() {
        if (!isStarted) {
            isStarted = true
            startForeground(NOTIFICATION_ID, notification.build())
        } else {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, notification.build())
        }
    }

    fun removeLocationUpdates() {
        stopForeground(true)
        stopSelf()
    }


    private fun getLastLocation() {
        try {
            mFusedLocationClient?.lastLocation
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        mLocation = task.result
                    }
                }
        } catch (unlikely: SecurityException) {
        }

    }

    private fun onNewLocation(location: Location) {
        mLocation = location
        //initStartService(latitud = location.latitude,longitud = location.longitude)
        val intent = Intent(ACTION_BROADCAST)
        intent.putExtra(EXTRA_LOCATION, location)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }


    private fun createLocationRequest(distanceFilter: Double) {
        mLocationRequest = LocationRequest()
        mLocationRequest?.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest?.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest?.smallestDisplacement = distanceFilter.toFloat()
    }


    inner class LocalBinder : Binder() {
        internal val service: LocationUpdatesService
            get() = this@LocationUpdatesService
    }


    override fun onDestroy() {
        super.onDestroy()
        isStarted = false
        try {
            mFusedLocationClient?.removeLocationUpdates(mLocationCallback!!)
            Utils.setRequestingLocationUpdates(this, false)
            mNotificationManager?.cancel(NOTIFICATION_ID)
        } catch (unlikely: SecurityException) {
            Utils.setRequestingLocationUpdates(this, true)
        }
    }

    private fun getMainActivityClass(context: Context): Class<*>? {
        val packageName = context.packageName
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        val className = launchIntent?.component?.className ?: return null

        return try {
            Class.forName(className)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            null
        }
    }


}

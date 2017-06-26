package com.jcraw.locationupdates

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.jaybirdsport.virtualrunpartner.location.LocationNotification

class LocationService : Service() {

    val TAG = "LocationService"

    companion object {
        val GPS_UPDATE_INTERVAL: Long = 5000
        val GPS_FASTEST_INTERVAL: Long = 3000

    }

    val binder = LocalBinder()
    lateinit var googleApiClient: GoogleApiClient
    lateinit var locationNotification: LocationNotification

    override fun onCreate() {
        super.onCreate()

        googleApiClient = GoogleApiClient.Builder(applicationContext)
                .addApi(LocationServices.API)
                .build()

        locationNotification = LocationNotification(applicationContext)

        googleApiClient.connect()
        googleApiClient.registerConnectionCallbacks(connectionCallbacks)
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind(): ")

        locationNotification.display()
        startForeground(locationNotification.ID, locationNotification.notification)

        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "LocationService: onStartCommand()")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.d(TAG, "LocationService: onDestroy()")

        locationNotification.remove()
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    googleApiClient, locationListener)
        } catch(throwable: Throwable) {
            Log.e(TAG, "Error removing location updates: ${throwable.message}")
        }

        googleApiClient.disconnect()
        googleApiClient.unregisterConnectionCallbacks(connectionCallbacks)
        super.onDestroy()
    }

    internal var connectionCallbacks: GoogleApiClient.ConnectionCallbacks = object : GoogleApiClient.ConnectionCallbacks {
        override fun onConnected(bundle: Bundle?){
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                        createLocationRequest(), locationListener)
            } catch (ex: SecurityException) {
                Log.w(TAG, "Location permissions not enabled: ", ex)
            }
        }

        override fun onConnectionSuspended(i: Int) {
            Log.d(TAG, "Connection suspended")
        }
    }

    internal var locationListener: LocationListener = LocationListener {
        location ->
        Log.d(TAG, "Location: " + location.toString())
    }

    fun createLocationRequest(): LocationRequest {
        val locationRequest: LocationRequest = LocationRequest()
        locationRequest.interval = GPS_UPDATE_INTERVAL
        locationRequest.fastestInterval = GPS_FASTEST_INTERVAL
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        return locationRequest
    }

    inner class LocalBinder : Binder() {
        internal val service: LocationService
            get() = this@LocationService
    }
}
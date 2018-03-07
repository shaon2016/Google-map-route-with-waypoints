package com.durbinlabs.googlemaproute

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.directions.route.Route
import com.directions.route.RouteException
import com.directions.route.Routing
import com.directions.route.RoutingListener

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.util.ArrayList

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, RoutingListener {

    private val TAG = MapsActivity::class.simpleName
    private lateinit var mMap: GoogleMap
    //Pahartali station road
    private val startLatLng = LatLng(22.335513, 91.828135)
    //Chawkbazar
    private val endLatLng = LatLng(22.351774, 91.833053)
    private  var waypoints: ArrayList<LatLng>? = null
    //Tigerpass, kajerdewri, jamalkhan
   private val waypointsBetweenStartToEnd = arrayListOf(LatLng(22.337096, 91.820521),
            LatLng(22.347521, 91.825631), LatLng(22.345970, 91.834250))


    private val COLORS = intArrayOf(R.color.colorPrimary,
            R.color.blue, R.color.articlecolor,
            R.color.lightGreen, R.color.lightRed, R.color.lime)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        waypoints = ArrayList()

        waypoints?.add(startLatLng)
        waypoints?.addAll(waypointsBetweenStartToEnd)
        waypoints?.add(endLatLng)

        val routing = Routing.Builder()
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(waypoints)
                .build()
        routing.execute()
    }

    override fun onRoutingCancelled() {
        Log.d(TAG, "Routing canceled")
    }

    override fun onRoutingStart() {
        Log.d(TAG, "Routing started")
    }

    override fun onRoutingFailure(e: RouteException?) {
        Log.d(TAG, "Routing failed")

        if (e != null)
            e.printStackTrace()
        else Toast.makeText(this, resources.getText(R.string.something_wrong), Toast.LENGTH_SHORT)
                .show()
    }

    override fun onRoutingSuccess(routes: ArrayList<Route>?, p1: Int) {
        val center = CameraUpdateFactory.newLatLngZoom(startLatLng, 14f)

        mMap.moveCamera(center)


        val polylines = java.util.ArrayList<Polyline>()
        //add route(s) to the map.
        for (i in routes!!.indices) {

            //In case of more than 5 alternative routes
            val colorIndex = i % COLORS.size

            val polyOptions = PolylineOptions()
            polyOptions.color(ContextCompat.getColor(this, COLORS[colorIndex]))
            polyOptions.width((10 + i * 3).toFloat())
            polyOptions.addAll(routes[i].points)
            val polyline = mMap.addPolyline(polyOptions)
            polylines.add(polyline!!)

//            Toast.makeText(applicationContext, "Route " + (i + 1) + ": distance - "
//                    + routes[i].distanceValue + ": duration - " +
//                    routes[i].durationValue, Toast.LENGTH_SHORT).show()
        }

        // Start marker
        var options = MarkerOptions()
        options.position(startLatLng)
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_marker))
        mMap.addMarker(options)

        // Marker for the waypoints
        for (w in waypointsBetweenStartToEnd) {
            options = MarkerOptions()
            options.position(w)
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.defualt_marker))
            mMap.addMarker(options)
        }

        // End marker
        options = MarkerOptions()
        options.position(endLatLng)
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_marker))
        mMap.addMarker(options)
    }
}

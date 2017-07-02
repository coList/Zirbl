package hsaugsburg.zirbl001.TourActivities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadNutLocation;
import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadStationLocation;
import hsaugsburg.zirbl001.Models.MapModels.NutModel;
import hsaugsburg.zirbl001.Models.MapModels.Route;
import hsaugsburg.zirbl001.Models.MapModels.StationModel;
import hsaugsburg.zirbl001.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private double latNut;
    private double lngNut;
    private int nutID;

    private double latStation;
    private double lngStation;

    private double latTarget;
    private double lngTarget;

    private double lat;
    private double lng;

    private Location location;

    private String stationName;
    LocationManager locationManager;
    private final Integer MIN_DISTANCE_METERS = 0;
    private final Integer LOCATION_UPDATE_INTERVALL_MSEC = 2000;
    private final String DIRECTIONS_API_JSON = "https://maps.googleapis.com/maps/api/directions/json?";

    private LatLng latLngMyTarget;
    private LatLng latLngMyPos;
    private LatLng latLngNut;

    private final String API_KEY = "AIzaSyAZKY3-8A2pA2VHyEjXcCuV5aqA1mhB_Q0";

    private List<Polyline> polylinePaths = new ArrayList<>();

    private int selectedTour;
    public static final String TOUR_VALUES = "tourValuesFile";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        SharedPreferences tourValues = getSharedPreferences(TOUR_VALUES, 0);
        selectedTour = Integer.parseInt(tourValues.getString("tourID", null));
        ArrayList<NutModel> nuts = new LoadNutLocation(this, selectedTour).readFile();

        int stationID = 0;


        StationModel result = new LoadStationLocation(this, selectedTour, stationID).readFile();

    }




    public void setIntentExtras(){
        Intent intent = getIntent();
        latTarget = intent.getDoubleExtra("lat", 0);
        lngTarget = intent.getDoubleExtra("lng", 0);
        stationName = intent.getStringExtra("stationName");
        latNut = intent.getDoubleExtra("latNut", 0);
        lngNut = intent.getDoubleExtra("lngNut", 0);
        nutID = intent.getIntExtra("nutID", 0);


    }


    private String createURL(String origin, String destination) {
        return DIRECTIONS_API_JSON // => "maps.googleapis.com/maps/api/directions/json?" + ...
                + "origin=" + origin
                + "&destination=" + destination
                + "&mode=walking"
                + "&key=" + API_KEY;
    }



    private void drawPolyline (List<Route> route) {
        polylinePaths = new ArrayList<>();
        Log.d(TAG, "drawPolyline: points: " + route);


        PolylineOptions polylineOptions = new PolylineOptions().
                geodesic(true).
                color(Color.BLUE).
                width(10);


        for (Route routes : route) {

            if (route.size() != 0) {
                for (int i = 0; i < routes.getPoints().size(); i++)
                    polylineOptions.add(routes.getPoints().get(i));
                Log.i("waypoints", routes.getPoints().get(0).toString());
                polylinePaths.add(mMap.addPolyline(polylineOptions));
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        LatLng latLngAugsburg = new LatLng(48.3652377,10.8971683); // Start in Augsburg, damit man nicht die Weltkarte am Anfang Sieht
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngAugsburg, 15));



        /* =========================================
                        Golden Nuts
           ========================================= */

        setIntentExtras();

        try {
            latLngNut = new LatLng(latNut, lngNut);

            //Log.d(TAG, "onCreate: latlngtarget " + latLngMyTarget);
            mMap.addMarker(new MarkerOptions()
                    .position(latLngNut)
                    .title("Eine goldene Nuss: " + nutID)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable._map_gold_zirbl))
            );
        } catch (Exception e){
            e.printStackTrace();
        }



        /* =========================================
                        Get & Set Station
           ========================================= */
        try {
            latLngMyTarget = new LatLng(latTarget, lngTarget);

            mMap.addMarker(new MarkerOptions()
                    .position(latLngMyTarget)
                    .title("Nächstes Ziel: " + stationName)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable._map_flag))
            );
        } catch (Exception e){
            e.printStackTrace();
        }

        /* =========================================
                        Get My Location
           ========================================= */
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        // check if network provider is enabled

        // Get the location from the given provider
     /*   Location location = locationManager
                .getLastKnownLocation(provider);

        locationManager.requestLocationUpdates(provider, 1000, 1, this);*/


        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {


            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_INTERVALL_MSEC, MIN_DISTANCE_METERS, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latMyPos = location.getLatitude();
                    double lngMyPos = location.getLongitude();
                    latLngMyPos = new LatLng(latMyPos, lngMyPos);
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> adressList = geocoder.getFromLocation(latMyPos, lngMyPos, 1);
                        String strMyPosMarker = adressList.get(0).getAddressLine(0);

                        mMap.addMarker(new MarkerOptions()
                                .position(latLngMyPos)
                                .title("Mein Standort: " + strMyPosMarker)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable._map_zirbl))
                        );


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });


        } else {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_INTERVALL_MSEC, MIN_DISTANCE_METERS, new LocationListener() {




                    @Override
                    public void onLocationChanged(Location location) {
                        double latMyPos = location.getLatitude();
                        double lngMyPos = location.getLongitude();
                        latLngMyPos = new LatLng(latMyPos, lngMyPos);
                        Geocoder geocoder = new Geocoder(getApplicationContext());
                        try {
                            List<Address> adressList = geocoder.getFromLocation(latMyPos, lngMyPos, 1);
                            String strMyPosMarker = adressList.get(0).getAddressLine(0);

                            mMap.addMarker(new MarkerOptions()
                                    .position(latLngMyPos)
                                    .title("Mein Standort: " + strMyPosMarker)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable._map_zirbl))
                            );
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngMyPos, 19));

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });

                Log.d(TAG, "onMapReady: location" + location);
            }
        }



        //String origin = "" + latLngMyPos.latitude + "," + latLngMyPos.longitude;
        //String destination = "" + latTarget + "," + lngTarget;
        //Log.d(TAG, "onMapReady: orgign " + origin);
        //Log.d(TAG, "onMapReady: destination " + destination);

        //String url = createURL(origin, destination);
        //Log.d(TAG, "onCreate: execute: " + url);

        // new JSONDirectionsAPI(this).execute("https://maps.googleapis.com/maps/api/directions/json?origin=augburg&destination=graben&key=AIzaSyAZKY3-8A2pA2VHyEjXcCuV5aqA1mhB_Q0");


    }
}
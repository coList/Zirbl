package hsaugsburg.zirbl001.TourActivities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import hsaugsburg.zirbl001.Datamanagement.JSONDirectionsAPI;
import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadNutLocation;
import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadStationLocation;
import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadTourChronology;
import hsaugsburg.zirbl001.Interfaces.TourActivity;
import hsaugsburg.zirbl001.Models.ChronologyModel;
import hsaugsburg.zirbl001.Models.MapModels.NutModel;
import hsaugsburg.zirbl001.Models.MapModels.Route;
import hsaugsburg.zirbl001.Models.MapModels.StationModel;
import hsaugsburg.zirbl001.R;

public class NavigationActivity extends AppCompatActivity implements TourActivity, OnMapReadyCallback {

    private Context mContext = NavigationActivity.this;
    private NavigationActivity activity = this;
    private static final String TAG = "NavigationActivity";

    private int chronologyNumber;
    private int selectedTour;
    private int stationID;
    private int currentScore;

    private String stationName;

    private ChronologyModel nextChronologyItem = new ChronologyModel();

    private LoadTourChronology loadTourChronology;

    public static final String GLOBAL_VALUES = "globalValuesFile";
    String serverName;

    public static final String TOUR_VALUES = "tourValuesFile";
    private int totalChronologyValue;

    private List<Route> directionRoute;


    private GoogleMap mMap;
    private double latTarget;
    private double lngTarget;


    LocationManager locationManager;
    private final Integer MIN_DISTANCE_METERS = 0;
    private final Integer LOCATION_UPDATE_INTERVALL_MSEC = 2000;
    private final String DIRECTIONS_API_JSON = "https://maps.googleapis.com/maps/api/directions/json?";

    private LatLng latLngMyTarget;
    private LatLng latLngMyPos;
    private LatLng latLngNut;

    private final String API_KEY = "AIzaSyAZKY3-8A2pA2VHyEjXcCuV5aqA1mhB_Q0";

    private List<Polyline> polylinePaths = new ArrayList<>();
    ArrayList<NutModel> nuts;


    //dot menu
    private TextView title;
    private RelativeLayout dotMenuLayout;
    private boolean dotMenuOpen = false;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        //dot menu

        title = (TextView) findViewById(R.id.titleActionbar);
        dotMenuLayout=(RelativeLayout) this.findViewById(R.id.dotMenu);
        dotMenuLayout.setVisibility(RelativeLayout.GONE);


        chronologyNumber = Integer.parseInt(getIntent().getStringExtra("chronologyNumber"));
        stationID = Integer.parseInt(getIntent().getStringExtra("stationID"));

        //get global tour values
        SharedPreferences tourValues = getSharedPreferences(TOUR_VALUES, 0);
        selectedTour = Integer.parseInt(tourValues.getString("tourID", null));
        totalChronologyValue = Integer.parseInt(tourValues.getString("totalChronology", null));

        currentScore = Integer.parseInt(getIntent().getStringExtra("currentscore"));
        loadTourChronology = new LoadTourChronology(this, this, nextChronologyItem, selectedTour, chronologyNumber, currentScore);
        loadTourChronology.readChronologyFile();

        SharedPreferences globalValues = getSharedPreferences(GLOBAL_VALUES, 0);
        serverName = globalValues.getString("serverName", null);


        Toolbar toolbar = (Toolbar) findViewById(R.id.standard_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Navigation");

        TextView actionbarText = null;
        try {
            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            actionbarText = (TextView) f.get(toolbar);
            actionbarText.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/OpenSans-Bold.ttf"));
            actionbarText.setAllCaps(true);
            actionbarText.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            actionbarText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        } catch (NoSuchFieldException e) {
        }
        catch (IllegalAccessException e) {
        }
        //new JSONStationLocation2(this, selectedTour, stationID).execute(serverName + "/api/selectStationLocationsView.php");

        TextView naviTitle = (TextView) findViewById(R.id.navigationTitle);
        naviTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
        TextView naviInfo = (TextView) findViewById(R.id.navigationInfo);
        naviInfo.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(totalChronologyValue + 1);
        progressBar.setProgress(chronologyNumber + 1);

        setDataView();
        nuts = new LoadNutLocation(this, selectedTour).readFile();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }


    public void setDataView() {
        StationModel result = new LoadStationLocation(this, selectedTour, stationID).readFile();
        stationName = result.getStationName();
        latTarget = result.getLatitude();
        lngTarget = result.getLongitude();
        TextView stationName = (TextView) findViewById(R.id.navigationTitle);
        stationName.setText(result.getStationName());

        TextView mapInstruction = (TextView) findViewById(R.id.navigationInfo);
        mapInstruction.setText(result.getMapInstruction());





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
                color(ContextCompat.getColor(mContext, R.color.colorTurquoise)).
                width(15);


        for (Route routes : route) {

            if (route.size() != 0) {
                for (int i = 0; i < routes.getPoints().size(); i++)
                    polylineOptions.add(routes.getPoints().get(i));
                Log.i("waypoints", routes.getPoints().get(0).toString());
                polylinePaths.add(mMap.addPolyline(polylineOptions));
            }
        }
    }


    private Marker myPosition;

    private boolean positionMarkerWasSet = false;
    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        LatLng latLngAugsburg = new LatLng(48.3652377,10.8971683); // Start in Augsburg, damit man nicht die Weltkarte am Anfang Sieht
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngAugsburg, 12));

        //latLngMyPos = new LatLng(48.3652377,10.8971683);
        //Location myLocation = mMap.getMyLocation();

        //latLngMyPos = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());




        /* =========================================
                        Golden Nuts
           ========================================= */



/*
        try {
            for (NutModel nut: nuts) {
                Double latNut = nut.getLatitude();
                Double lngNut = nut.getLongitude();
                int nutID = nut.getNutID();
                latLngNut = new LatLng(latNut, lngNut);

                //Log.d(TAG, "onCreate: latlngtarget " + latLngMyTarget);
                mMap.addMarker(new MarkerOptions()
                        .position(latLngNut)
                        .title("Eine goldene Nuss: " + nutID)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable._map_gold_zirbl))
                );
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        */




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
                    Log.d(TAG, "location changed");
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> adressList = geocoder.getFromLocation(latMyPos, lngMyPos, 1);
                        String strMyPosMarker = adressList.get(0).getAddressLine(0);

                        if (!positionMarkerWasSet) {
                            myPosition = mMap.addMarker(new MarkerOptions()
                                    .position(latLngMyPos)
                                    .title("Mein Standort: " + strMyPosMarker)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable._map_zirbl))
                            );
                            positionMarkerWasSet = true;
                        } else {
                            myPosition.setPosition(latLngMyPos);
                        }

                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngMyPos, 19)); // 19er Zoom ws am besten


                        setRoute();
                        setNuts();

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

                            if (!positionMarkerWasSet) {
                                myPosition = mMap.addMarker(new MarkerOptions()
                                        .position(latLngMyPos)
                                        .title("Mein Standort: " + strMyPosMarker)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable._map_zirbl))
                                );
                                positionMarkerWasSet = true;
                            } else {
                                myPosition.setPosition(latLngMyPos);
                            }


                            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngMyPos, 19));


                            setRoute();
                            setNuts();
                            //Log.d(TAG, latLngMyPos.toString());



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
            }
        }


    }

    //click on station name to hide or show the mapinstruction
    public void onClick(View view) {
        TextView mapInstruction = (TextView) findViewById(R.id.navigationInfo);
        ImageView arrow = (ImageView) findViewById(R.id.arrow);

        if (mapInstruction.getVisibility() == View.VISIBLE) {
            mapInstruction.setVisibility(View.GONE);
            arrow.setScaleY(-1);
        } else {
            mapInstruction.setVisibility(View.VISIBLE);
            arrow.setScaleY(1);
        }
    }

    public void continueToNextView(View view) {
        loadTourChronology.continueToNextView();
    }

    private void showEndTourDialog(){
        this.runOnUiThread(new Runnable() {
            public void run() {
                EndTourDialog alertEnd = new EndTourDialog(mContext);
                alertEnd.showDialog((Activity) mContext);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            showEndTourDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public String getStationName() {
        return stationName;
    }


    public void showMenu(View view){

        ImageView dotIcon = (ImageView) findViewById(R.id.dotIcon);
        TextView menuStats = (TextView) findViewById(R.id.menuStats);
        TextView menuQuit = (TextView) findViewById(R.id.menuQuit);

        if(dotMenuOpen){
            dotMenuLayout.setVisibility(RelativeLayout.GONE);
            dotMenuOpen = false;
            title.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            dotIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent));
        } else {
            dotMenuLayout.setVisibility(RelativeLayout.VISIBLE);
            dotMenuOpen = true;
            title.setTextColor(ContextCompat.getColor(mContext, R.color.colorTurquoise));
            dotIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.colorTurquoise));
            menuQuit.setTextSize(18);
            menuStats.setTextSize(18);
        }
    }
    public void showStats(View view){
        Log.d(TAG, "showStats: Stats");
    }
    public void quitTour(View view){
        showEndTourDialog();
    }

    private boolean firstCall = true;
    public void setRoute() {
        if (firstCall) {

            firstCall = false;
            String origin = "" + latLngMyPos.latitude + "," + latLngMyPos.longitude;
            String destination = "" + latTarget + "," + lngTarget;
            Log.d(TAG, "onMapReady: orgign " + origin);
            Log.d(TAG, "onMapReady: destination " + destination);

            String url = createURL(origin, destination);
            Log.d(TAG, "onCreate: execute: " + url);


            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngMyPos, 19)); // 19er Zoom ws am besten

            new JSONDirectionsAPI(activity).execute(url);
        }
    }

    private List<Marker> nutMarker = new ArrayList<Marker>();
    public void setNuts() {
        for (NutModel nut: nuts) {
            Double latNut = nut.getLatitude();
            Double lngNut = nut.getLongitude();
            int nutID = nut.getNutID();
            latLngNut = new LatLng(latNut, lngNut);

            //Log.d(TAG, "onCreate: latlngtarget " + latLngMyTarget);
            //latLngMyPos = new LatLng(48.367588, 10.896769);

            if (calculateDistance(latLngMyPos.latitude, latLngMyPos.longitude, latNut, lngNut) <= 0.2 && !nut.isCollected()) {  //befindet sich die Nuss im Radius von x-Kilometern zum User?
                boolean alreadyExists = false;
                for (Marker marker: nutMarker) {
                    if (marker.getPosition().latitude == latNut && marker.getPosition().longitude == lngNut) {  //setzte die Nuss, auf die wir eben geprüft haben, auf invisible
                        marker.setVisible(true);
                        alreadyExists = true;
                    }
                }

                if (!alreadyExists) {
                    nutMarker.add(mMap.addMarker(new MarkerOptions()
                            .position(latLngNut)
                            .title("Eine goldene Nuss: " + nutID)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable._map_gold_zirbl))
                    ));
                }

            } else if (!nut.isCollected()) {
                for (Marker marker: nutMarker) {
                   if (marker.getPosition().latitude == latNut && marker.getPosition().longitude == lngNut) {  //setzte die Nuss, auf die wir eben geprüft haben, auf invisible
                       marker.setVisible(false);
                   }
                }
            } else if (nut.isCollected()) { //wenn die Nuss gesammelt wurde, lösche den Marker
                for (int i = 0; i < nutMarker.size(); i++) {
                    if (nutMarker.get(i).getPosition().latitude == latNut && nutMarker.get(i).getPosition().longitude == lngNut) {
                        nutMarker.get(i).remove();
                    }
                }
            }

        }

    }

    public void processDirectionData(List<Route> route) throws JSONException {
        directionRoute = route;

        drawPolyline(route);

        /*
        for (Route r: route) {
            for (LatLng latLng: r.getPoints()) {
                LatLng point = latLng;

                mMap.addMarker(new MarkerOptions()
                        .position(point)
                        .title("Nächstes Ziel: " + "Punkt")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable._map_flag))
                );
            }
        }
        */


    }


    private static final int earthRadius = 6371;
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2)
    {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a =
                (Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2));
        double c = (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
        double d = earthRadius * c;
        return d;
    }



    public void processData(StationModel result) {
        stationName = result.getStationName();
        TextView stationName = (TextView) findViewById(R.id.navigationTitle);
        stationName.setText(result.getStationName());

        TextView mapInstruction = (TextView) findViewById(R.id.navigationInfo);
        mapInstruction.setText(result.getMapInstruction());
    }

}

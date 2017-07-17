package hsaugsburg.zirbl001.TourActivities.Navigation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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
import java.util.ArrayList;
import java.util.List;

import hsaugsburg.zirbl001.Datamanagement.JSONDirectionsAPI;
import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadLocationDoUKnow;
import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadNutLocation;
import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadStationLocation;
import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadTourChronology;
import hsaugsburg.zirbl001.Interfaces.TourActivity;
import hsaugsburg.zirbl001.Models.ChronologyModel;
import hsaugsburg.zirbl001.Models.DoUKnowModel;
import hsaugsburg.zirbl001.Models.MapModels.NutModel;
import hsaugsburg.zirbl001.Models.MapModels.Route;
import hsaugsburg.zirbl001.Models.MapModels.StationModel;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.TourActivities.DoUKnowActivity;
import hsaugsburg.zirbl001.TourActivities.EndTourDialog;
import hsaugsburg.zirbl001.TourActivities.GoldenActivity;
import hsaugsburg.zirbl001.Utils.ObjectSerializer;

public class NavigationActivity extends AppCompatActivity implements TourActivity, OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Context mContext = NavigationActivity.this;
    private NavigationActivity activity = this;
    private static final String TAG = "NavigationActivity";

    private int chronologyNumber;
    private int selectedTour;
    private int stationID;

    private static final int ACCURACY_HIGH = 3;

    private String stationName;

    private ChronologyModel nextChronologyItem = new ChronologyModel();

    private LoadTourChronology loadTourChronology;

    public static final String GLOBAL_VALUES = "globalValuesFile";
    String serverName;

    SharedPreferences tourValues;
    public static final String TOUR_VALUES = "tourValuesFile";
    private int totalChronologyValue;
    ArrayList<Boolean> listIsNutCollected;
    ArrayList<Boolean> listDoUKnowRead;

    private List<Route> directionRoute;


    private GoogleMap mMap;
    private double latTarget;
    private double lngTarget;


    LocationManager locationManager;
    private final Integer MIN_DISTANCE_METERS = 0;
    private final Integer LOCATION_UPDATE_INTERVALL_MSEC = 300;
    private final String DIRECTIONS_API_JSON = "https://maps.googleapis.com/maps/api/directions/json?";

    private LatLng latLngMyTarget;
    private LatLng latLngMyPos;
    private LatLng latLngNut;

    private final String API_KEY = "AIzaSyAZKY3-8A2pA2VHyEjXcCuV5aqA1mhB_Q0";

    private List<Polyline> polylinePaths = new ArrayList<>();
    private ArrayList<NutModel> nuts;
    private int nutsCollected = 0;

    private ArrayList<DoUKnowModel> doUKnowModels;

    private Marker myPosition;
    private boolean positionMarkerWasSet = false;


    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private FusedLocationProviderApi locationProviderApi = LocationServices.FusedLocationApi;
    private Double latMyPos;
    private Double lngMyPos;


    //dot menu
    private TextView title;
    private RelativeLayout dotMenuLayout;
    private boolean dotMenuOpen = false;




    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        googleApiClient.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();


        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        overridePendingTransition(0, 0);
        //locationManager.removeUpdates(this);
    }

    public void onResume() {
        super.onResume();


        if (googleApiClient.isConnected()){
            requestLocationUpdates();
        }




 /*
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

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_INTERVALL_MSEC, MIN_DISTANCE_METERS, this);
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_INTERVALL_MSEC, MIN_DISTANCE_METERS, this);
        }
      if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_INTERVALL_MSEC, MIN_DISTANCE_METERS, this);
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_INTERVALL_MSEC, MIN_DISTANCE_METERS, this);

        }*/
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(LOCATION_UPDATE_INTERVALL_MSEC);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);






        //dot menu

        title = (TextView) findViewById(R.id.titleActionbar);
        title.setText("Navigation");
        dotMenuLayout=(RelativeLayout) this.findViewById(R.id.dotMenu);
        dotMenuLayout.setVisibility(RelativeLayout.GONE);


        chronologyNumber = Integer.parseInt(getIntent().getStringExtra("chronologyNumber"));
        stationID = Integer.parseInt(getIntent().getStringExtra("stationID"));

        //get global tour values
        tourValues = getSharedPreferences(TOUR_VALUES, 0);
        selectedTour = Integer.parseInt(tourValues.getString("tourID", null));
        totalChronologyValue = Integer.parseInt(tourValues.getString("totalChronology", null));
        listIsNutCollected = new ArrayList<>();
        listDoUKnowRead = new ArrayList<>();
        try {
            listIsNutCollected = (ArrayList<Boolean>) ObjectSerializer.deserialize(tourValues.getString("listIsNutCollected", ObjectSerializer.serialize(new ArrayList<Boolean>())));
            listDoUKnowRead = (ArrayList<Boolean>) ObjectSerializer.deserialize(tourValues.getString("listDoUKnowRead", ObjectSerializer.serialize(new ArrayList<Boolean>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadTourChronology = new LoadTourChronology(this, this, nextChronologyItem, selectedTour, chronologyNumber);
        loadTourChronology.readChronologyFile();

        SharedPreferences globalValues = getSharedPreferences(GLOBAL_VALUES, 0);
        serverName = globalValues.getString("serverName", null);

        TextView naviTitle = (TextView) findViewById(R.id.navigationTitle);
        naviTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
        TextView naviInfo = (TextView) findViewById(R.id.navigationInfo);
        naviInfo.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(totalChronologyValue + 1);
        progressBar.setProgress(chronologyNumber + 1);

        setDataView();
        nuts = new LoadNutLocation(this, selectedTour).readFile();
        doUKnowModels = new LoadLocationDoUKnow(this, selectedTour).readFile();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);



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
                polylinePaths.add(mMap.addPolyline(polylineOptions));
            }
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.clear();
        LatLng latLngAugsburg = new LatLng(48.3652377,10.8971683); // Start in Augsburg, damit man nicht die Weltkarte am Anfang sieht
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngAugsburg, 12));


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



/*        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_INTERVALL_MSEC, MIN_DISTANCE_METERS, this);
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_INTERVALL_MSEC, MIN_DISTANCE_METERS, this);
        } else {
            showDialogGPS();
        }*/


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
        mMap.clear();
        finish();
        loadTourChronology.continueToNextView();
    }

    private void showEndTourDialog(){
        this.runOnUiThread(new Runnable() {
            public void run() {
                EndTourDialog alertEnd = new EndTourDialog(mContext, selectedTour);
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

            String url = createURL(origin, destination);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngMyPos, 19)); // 19er Zoom ws am besten

            new JSONDirectionsAPI(activity).execute(url);
        }
    }

    private List<Marker> nutMarker = new ArrayList<Marker>();
    public void setNuts() {
        for (int h = 0; h < nuts.size(); h++) {
            Double latNut = nuts.get(h).getLatitude();
            Double lngNut = nuts.get(h).getLongitude();
            int nutID = nuts.get(h).getNutID();
            latLngNut = new LatLng(latNut, lngNut);

            //if (calculateDistance(latLngMyPos.latitude, latLngMyPos.longitude, latNut, lngNut) <= 0.02 && !(listIsNutCollected.get(h))) {
            if (distance(latLngMyPos.latitude, latLngMyPos.longitude, latNut, lngNut) <= 0.02 && !(listIsNutCollected.get(h))) {
                nutsCollected ++;
                for (int i = 0; i < nutMarker.size(); i++) {
                    if (nutMarker.get(i).getPosition().latitude == latNut && nutMarker.get(i).getPosition().longitude == lngNut) {
                        nutMarker.get(i).remove();
                    }
                }

                listIsNutCollected.set(h, true);

                SharedPreferences.Editor editor = tourValues.edit();
                editor.putString("nutsCollected", Integer.toString(nutsCollected));
                try {
                    editor.remove("listIsNutCollected");
                    editor.putString("listIsNutCollected", ObjectSerializer.serialize(listIsNutCollected));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                editor.commit();


                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(100);
                Intent intent = new Intent(mContext, GoldenActivity.class);
                intent.putExtra("score", Integer.toString(nuts.get(h).getScore()));
                intent.putExtra("foundText", nuts.get(h).getFoundText());
                intent.putExtra("totalAmountOfNuts", Integer.toString(nuts.size()));
                startActivity(intent);

            } //else if (calculateDistance(latLngMyPos.latitude, latLngMyPos.longitude, latNut, lngNut) <= 0.2 && !listIsNutCollected.get(h)) {  //befindet sich die Nuss im Radius von x-Kilometern zum User?
             else if (distance(latLngMyPos.latitude, latLngMyPos.longitude, latNut, lngNut) <= 0.1 && !listIsNutCollected.get(h)) {
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

            } else if (!listIsNutCollected.get(h)) {
                for (Marker marker: nutMarker) {
                   if (marker.getPosition().latitude == latNut && marker.getPosition().longitude == lngNut) {  //setzte die Nuss, auf die wir eben geprüft haben, auf invisible
                       marker.setVisible(false);
                   }
                }
            }

        }

    }

    public void processDirectionData(List<Route> route) throws JSONException {
        directionRoute = route;

        drawPolyline(route);

    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }



    //return distance in kilometers
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    /*
    private static final int earthRadius = 6371;   //kilometer
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
    }*/


    @Override
    public void onLocationChanged(Location location) {



        latMyPos = location.getLatitude();
        lngMyPos = location.getLongitude();
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

            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngMyPos, 19)); // 19er Zoom ws am besten

            setRoute();
            setNuts();

            //check for infopopup
            for (int i = 0; i < doUKnowModels.size(); i++) {
                if (!listDoUKnowRead.get(i)) {
                    //if (calculateDistance(latLngMyPos.latitude, latLngMyPos.longitude, doUKnowModels.get(i).getLatitude(), doUKnowModels.get(i).getLongitude()) <= 0.02) {
                    if (distance(latLngMyPos.latitude, latLngMyPos.longitude, doUKnowModels.get(i).getLatitude(), doUKnowModels.get(i).getLongitude()) <= 0.02) {
                        listDoUKnowRead.set(i, true);

                        SharedPreferences.Editor editor = tourValues.edit();
                        try {
                            editor.remove("listDoUKnowRead");
                            editor.putString("listDoUKnowRead", ObjectSerializer.serialize(listDoUKnowRead));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        editor.commit();


                        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibe.vibrate(100);
                        Intent intent = new Intent(mContext, DoUKnowActivity.class);
                        intent.putExtra("infopopupid", Integer.toString(doUKnowModels.get(i).getInfoPopupID()));
                        intent.putExtra("chronologyNumber", Integer.toString(-1));
                        intent.putExtra("stationName", getStationName());
                        startActivity(intent);
                    }
                }

            }


            //if (calculateDistance(latLngMyPos.latitude, latLngMyPos.longitude, latLngMyTarget.latitude, latLngMyTarget.longitude) <= 0.02) {
            if (distance(latLngMyPos.latitude, latLngMyPos.longitude, latLngMyTarget.latitude, latLngMyTarget.longitude) <= 0.01) {
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(100);
                loadTourChronology.continueToNextView();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


/*    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Maps", "StatusChanged");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Maps", "ProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Maps", "ProviderDisabled");
    }*/





    public void processData(StationModel result) {
        stationName = result.getStationName();
        TextView stationName = (TextView) findViewById(R.id.navigationTitle);
        stationName.setText(result.getStationName());

        TextView mapInstruction = (TextView) findViewById(R.id.navigationInfo);
        mapInstruction.setText(result.getMapInstruction());
    }

    public void showDialogGPS() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                GPSDialog alertGPS = new GPSDialog(mContext);
                alertGPS.showDialog();
            }
        });
    }





    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {

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
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



}

package hsaugsburg.zirbl001.TourActivities.Navigation;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.MarkerView;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapquest.mapping.MapQuestAccountManager;
import com.mapquest.mapping.maps.MapView;
import com.mapquest.mapping.maps.MapboxMap;
import com.mapquest.mapping.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadLocationDoUKnow;
import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadNutLocation;
import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadStationLocation;
import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadTourChronology;
import hsaugsburg.zirbl001.Interfaces.TourActivity;
import hsaugsburg.zirbl001.Models.TourModels.ChronologyModel;
import hsaugsburg.zirbl001.Models.TourModels.DoUKnowModel;
import hsaugsburg.zirbl001.Models.TourModels.MapModels.GeofenceService;
import hsaugsburg.zirbl001.Models.TourModels.MapModels.NutModel;
import hsaugsburg.zirbl001.Models.TourModels.MapModels.StationModel;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.TourActivities.DoUKnowActivity;
import hsaugsburg.zirbl001.TourActivities.EndTourDialog;
import hsaugsburg.zirbl001.TourActivities.GoldenActivity;
import hsaugsburg.zirbl001.Utils.ObjectSerializer;
import hsaugsburg.zirbl001.Utils.TopDarkActionbar;


public class MapQuestNavigationActivity extends AppCompatActivity implements TourActivity, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private Context mContext = MapQuestNavigationActivity.this;
    private MapQuestNavigationActivity activity = this;

    private int selectedTour;
    private int stationID;
    private String stationName;

    private ChronologyModel nextChronologyItem = new ChronologyModel();
    private LoadTourChronology loadTourChronology;

    public static final String GLOBAL_VALUES = "globalValuesFile";
    String serverName;

    SharedPreferences tourValues;
    public static final String TOUR_VALUES = "tourValuesFile";
    private long startTime;
    private int currentScore;
    ArrayList<Boolean> listIsNutCollected;
    ArrayList<Boolean> listDoUKnowRead;

    private com.google.android.gms.maps.model.Marker myPosition;
    private boolean positionMarkerWasSet = false;

    private ArrayList<NutModel> nuts;
    private int nutsCollected = 0;
    private ArrayList<DoUKnowModel> doUKnowModels;
    private List<MarkerView> nutMarker = new ArrayList<MarkerView>();

    private MapboxMap mMapboxMap;
    private MapView mMapView;
    private final GeoPoint ENDPOINT = new GeoPoint(48.36117, 10.90954);
    private String TAG = "main";
    private GoogleApiClient googleApiClient = null;
    private LocationRequest locationRequest;
    private FusedLocationProviderApi locationProviderApi = LocationServices.FusedLocationApi;
    private double latTarget;
    private double lngTarget;
    private LatLng latLngMyPos;
    private Polyline polyline;
    private PolylineOptions mPolyline = new PolylineOptions();
    private boolean firstStart = true;
    private boolean firstCall = true;
    private String GEOFENCE_ID = "myGeofenceid";
    private Integer zoomLevel;
    private  List<LatLng> shapePoints = new ArrayList<>();

    //dot menu
    private TopDarkActionbar topDarkActionbar;

    private boolean visibleInfo = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapQuestAccountManager.start(getApplicationContext());

        setContentView(R.layout.activity_mapquest_navigation);

        Button cheatButton = (Button) findViewById(R.id.nextTourItem);
        cheatButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        cheatButton.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
        cheatButton.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/OpenSans-Bold.ttf"));

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.d(TAG, "onConnected: Connected to GoogleApiClient");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.d(TAG, "onConnectionSuspended: Suspended connection to GoogleApiClient");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(TAG, "onConnectionFailed: Failded to connect to GoogleApiClient - " + connectionResult.getErrorMessage());
                    }
                })
                .build();



        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);

        int locationUpdateIntervallMsec = 300;
        locationRequest.setFastestInterval(locationUpdateIntervallMsec);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        int chronologyNumber = Integer.parseInt(getIntent().getStringExtra("chronologyNumber"));
        stationID = Integer.parseInt(getIntent().getStringExtra("stationID"));

        //get global tour values
        tourValues = getSharedPreferences(TOUR_VALUES, 0);
        selectedTour = Integer.parseInt(tourValues.getString("tourID", null));
        int totalChronologyValue = Integer.parseInt(tourValues.getString("totalChronology", null));
        startTime = Long.parseLong(tourValues.getString("startTime", null));
        currentScore = Integer.parseInt(tourValues.getString("currentScore", null));

        String titleText = "Navigation";
        topDarkActionbar = new TopDarkActionbar(this, titleText);

        listIsNutCollected = new ArrayList<>();
        listDoUKnowRead = new ArrayList<>();
        try {
            listIsNutCollected = (ArrayList<Boolean>) ObjectSerializer.deserialize(tourValues.getString("listIsNutCollected", ObjectSerializer.serialize(new ArrayList<Boolean>())));
            listDoUKnowRead = (ArrayList<Boolean>) ObjectSerializer.deserialize(tourValues.getString("listDoUKnowRead", ObjectSerializer.serialize(new ArrayList<Boolean>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        startLocationMonitoring();
        startGeofenceMonitoring();


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


        mMapView = (MapView) findViewById(R.id.mapquestMapView);
        mMapView.onCreate(savedInstanceState);


        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mMapboxMap = mapboxMap;
                mMapboxMap.setMyLocationEnabled(true);
                Double userLat = mapboxMap.getMyLocation().getLatitude();
                Double userLng = mapboxMap.getMyLocation().getLongitude();
                LatLng userLocation = new LatLng(userLat, userLng);
                mMapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getMiddlePointUserTarget(), zoomFactor()));

                //Zirbl Marker für aktuelle Postition setzen
                mMapboxMap.getMyLocationViewSettings().setForegroundDrawable(getResources().getDrawable(R.drawable._map_zirbl),getResources().getDrawable(R.drawable._map_zirbl));
                mMapboxMap.getMyLocationViewSettings().setBackgroundTintColor(getResources().getColor(R.color.colorTransparent100));
                mMapboxMap.getMyLocationViewSettings().setAccuracyAlpha(0);

                //setPoyline(mMapboxMap);
                ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
                waypoints.add(new GeoPoint(userLat, userLng));
                waypoints.add(new GeoPoint(latTarget, lngTarget));

                //new JSONUpdateRoad(MapQuestNavigationActivity.this).execute(waypoints);

                polyline = mMapboxMap.addPolyline(new PolylineOptions()
                        .addAll(shapePoints)
                        .color(getResources().getColor(R.color.colorTurquoise))
                        .width(5));

                //Zielflagge setzen
                addOwnMarker(new LatLng(latTarget, lngTarget),
                        "Ziel", "Hier musst du hin",
                        R.drawable._map_flag);
            }
        });
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

        JSONArray waypoints = new JSONArray();

        try {
            waypoints = result.getWayPoints().getJSONArray("waypoints");
            Log.d("MapQuest", result.getWayPoints().getJSONArray("waypoints").toString());


            for (int i = 0; i < waypoints.length(); i+=2) {
                LatLng waypoint = new LatLng(waypoints.getDouble(i), waypoints.getDouble(i+1));

                shapePoints.add(waypoint);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

       Log.d("MapQuestNavigationshape", shapePoints.toString());
        //drawPolyline(shapePoints);


    }

    public void getAllLatLngPoints(List<LatLng> points)  {

        //drawPolyline(points);
    }

    private void drawPolyline(List<LatLng> coordinates) {

        if (polyline != null) {
            mMapboxMap.removePolyline(polyline);
        }

        // Draw Points on MapView
        polyline = mMapboxMap.addPolyline(new PolylineOptions()
                .addAll(coordinates)
                .color(getResources().getColor(R.color.colorTurquoise))
                .width(5));

    }

    private void addOwnMarker( LatLng location, String title, String snippet, int image) {

        Icon icon = IconFactory.getInstance(mContext).fromResource(image);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location);
        markerOptions.title(title);
        markerOptions.snippet(snippet);
        markerOptions.icon(icon);
        mMapboxMap.addMarker(markerOptions);


    }

    private void showEndTourDialog() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                EndTourDialog alertEnd = new EndTourDialog(mContext, selectedTour);
                alertEnd.showDialog((Activity) mContext);
            }
        });
    }

    public String getStationName() {
        return stationName;
    }

    public void showMenu(View view) {
        topDarkActionbar.showMenu();
    } // ends showmenu

    public void showStats(View view) {
        topDarkActionbar.showStats(currentScore, startTime);
    }

    public void quitTour(View view) {
        showEndTourDialog();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            showEndTourDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //return distance in kilometers
    //reference: https://stackoverflow.com/questions/6981916/how-to-calculate-distance-between-two-locations-using-their-longitude-and-latitu (Aufruf: 05.07.2017)
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

    public void setRoute() {
        if (firstCall) {
            firstCall = false;
            String origin = "" + latLngMyPos.getLatitude() + "," + latLngMyPos.getLongitude();
            String destination = "" + latTarget + "," + lngTarget;

            //mMapboxMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(latLngMyPos, 19)); // 19er Zoom ws am besten
        }
    }

    public void setNuts() {

        Double userLat = mMapboxMap.getMyLocation().getLatitude();
        Double userLng = mMapboxMap.getMyLocation().getLongitude();

        for (int h = 0; h < nuts.size(); h++) {
            Double latNut = nuts.get(h).getLatitude();
            Double lngNut = nuts.get(h).getLongitude();
            int nutID = nuts.get(h).getNutID();
            LatLng latLngNut = new LatLng(latNut,lngNut);
            //com.google.android.gms.maps.model.LatLng latLngNut = new com.google.android.gms.maps.model.LatLng(latNut, lngNut);

            if (distance(userLat, userLng, latNut, lngNut) <= 0.02 && !(listIsNutCollected.get(h))) {
                nutsCollected++;
                for (int i = 0; i < nutMarker.size(); i++) {
                    if (nutMarker.get(i).getPosition().getLatitude() == latNut && nutMarker.get(i).getPosition().getLongitude() == lngNut) {
                        nutMarker.get(i).remove();
                    }
                }
                listIsNutCollected.set(h, true);

                SharedPreferences.Editor editor = tourValues.edit();
                editor.putString("nutsCollected", Integer.toString(nutsCollected));
                Log.d("GoldenActivity - nutsCollected", Integer.toString(nutsCollected));
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

            } else if (distance(userLat, userLng, latNut, lngNut) <= 0.1 && !listIsNutCollected.get(h)) {
                boolean alreadyExists = false;
                for (Marker marker : nutMarker) {
                    if (marker.getPosition().getLatitude() == latNut && marker.getPosition().getLongitude() == lngNut) {  //setzte die Nuss, auf die wir eben geprüft haben, auf visible
                        //marker.setVisible(true);
                        alreadyExists = true;
                    }
                }

                if (!alreadyExists) {
                    addOwnMarker(latLngNut,"Goldene Zirblnuss","",R.drawable._map_gold_zirbl);

                } else if(!listIsNutCollected.get(h)){

                    for (Marker marker : nutMarker) {
                        if (marker.getPosition().getLatitude() == latNut && marker.getPosition().getLongitude() == lngNut) {  //setzte die Nuss, auf die wir eben geprüft haben, auf invisible
                            //marker.setVisible(false);
                        }
                    }
                }
            }
        }
    }

    public int zoomFactor(){
        Double userLat = mMapboxMap.getMyLocation().getLatitude();
        Double userLng = mMapboxMap.getMyLocation().getLongitude();
        Double distanceUserTarget = distance(userLat,userLng,latTarget,lngTarget);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int displayWidth = size.x;
        int displayHeight = size.y;



/*       Formel für Zoomlevel (OSM)  S = C*cos(y) / 2^(z + 8) ; Z = log(2)[C*cos(y) / S] - 8
         S: Die Strecke für einen Pixel
         C: Umfang der Erde am Äquator
         z: Zoom-Level
         y: Breite des interessierenden Ortes       */

        // C = 40.074.000 m, y = 10.897790 im falle augsburg,
        Double earthCircumferenceLength = 39351305.71; // C*cos(y)

    Double distPerPx = displayWidth / distanceUserTarget; // S
        Log.d(TAG, "zoomFactor: displayWidth" + displayWidth);
        Log.d(TAG, "zoomFactor: distanceusrtarget " + distanceUserTarget);
        Integer partsOfDistOnEarth = (int)(earthCircumferenceLength / distPerPx); // C*cos(y) / S

        //Log.d(TAG, "zoomFactor: partsofdistonearth: " + partsOfDistOnEarth);
        zoomLevel = (int)(Math.log10(partsOfDistOnEarth) / Math.log10(2)) - 3; // zoomlevel = Log(2)[partsOfDistOnEarth]

        //            log[10]x
        // log[2]x = ----------
        //            log[10]2

        Log.d(TAG, "zoomFactor: " + zoomLevel);

        return zoomLevel;


    }
    public LatLng getMiddlePointUserTarget (){
        Double userLat = mMapboxMap.getMyLocation().getLatitude();
        Double userLng = mMapboxMap.getMyLocation().getLongitude();

        Double middleLat = (userLat + latTarget)/2;
        Double middleLng = (userLng + lngTarget)/2;
        // Wegbeschreibungskasten liegt über der Karte, daher diesen abziehen für neuen mittelpunkt

        //Log.d(TAG, "zoomFactor3: "+ middleLat);
        //Log.d(TAG, "zoomFactor4: "+ middleLng);


        LatLng middleLatLng = new LatLng(middleLat, middleLng);
        return middleLatLng;
    }


    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onResume()
    { super.onResume(); mMapView.onResume(); }

    @Override
    public void onPause()
    { super.onPause();
        mMapView.onPause();
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    @Override
    protected void onDestroy()
    { super.onDestroy(); mMapView.onDestroy(); }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    { super.onSaveInstanceState(outState); mMapView.onSaveInstanceState(outState); }

    //click on station name to hide or show the mapinstruction
    public void onClick(View view) {
        ImageView arrow = (ImageView) findViewById(R.id.arrow);
        RelativeLayout info = (RelativeLayout) findViewById(R.id.stationInfo);
        LinearLayout infoArea = (LinearLayout) findViewById(R.id.infoArea);

        if (visibleInfo) {
            arrow.animate().rotation(-180);
            infoArea.animate().translationY(info.getHeight());
            visibleInfo = false;
        } else {
            arrow.animate().rotation(0);
            infoArea.animate().translationY(0);
            visibleInfo = true;
        }
    }

    public void continueToNextView(View view) {
        mMapboxMap.clear();
        finish();
        loadTourChronology.continueToNextView();
    }


    @Override
    public void onLocationChanged(Location location) {
        Double latMyPos = location.getLatitude();
        Double lngMyPos = location.getLongitude();
        latLngMyPos = new LatLng(latMyPos, lngMyPos);
        Geocoder geocoder = new Geocoder(getApplicationContext());

        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        waypoints.add(new GeoPoint(latMyPos, lngMyPos));
        waypoints.add(new GeoPoint(latTarget,lngTarget));

        try {
            List<Address> adressList = geocoder.getFromLocation(latMyPos, lngMyPos, 1);
            String strMyPosMarker = adressList.get(0).getAddressLine(0);


            // bisherige position
            setRoute();
            setNuts();

            String origin = "" + latLngMyPos.getLatitude() + "," + latLngMyPos.getLongitude();
            String destination = "" + latTarget + "," + lngTarget;




            //check for infopopup
            for (int i = 0; i < doUKnowModels.size(); i++) {
                if (!listDoUKnowRead.get(i)) {
                    if (distance(latLngMyPos.getLatitude(), latLngMyPos.getLongitude(), doUKnowModels.get(i).getLatitude(), doUKnowModels.get(i).getLongitude()) <= 0.02) {
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

            if (distance(latLngMyPos.getLatitude(), latLngMyPos.getLongitude(), latTarget, lngTarget) <= 0.01) {
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(100);
                loadTourChronology.continueToNextView();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }



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
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    public void startLocationMonitoring() {
        Log.d(TAG, "startLocation called");
        try {
            LocationRequest locationRequest = LocationRequest.create()
                    .setInterval(10000)
                    .setFastestInterval(5000)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        } catch (SecurityException e) {
            Log.d(TAG, "SecurityExeption - " + e.getMessage());
        }
    }

    public void startGeofenceMonitoring () {
        try {
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(GEOFENCE_ID)
                    .setCircularRegion( 10.894446, 48.366512, 10000)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setNotificationResponsiveness(1000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .build();

            GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .addGeofence(geofence).build();

            Intent intent = new Intent(this, GeofenceService.class);
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            if(!googleApiClient.isConnected()) {
                Log.d(TAG, "GoogleApiClient Is not connected");
            } else {
                LocationServices.GeofencingApi.addGeofences(googleApiClient, geofencingRequest, pendingIntent)
                        .setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                if (status.isSuccess()) {
                                    Log.d(TAG, "Successfully added geofence");
                                } else {
                                    Log.d(TAG, "Failed to add geofence - " + status.getStatus());
                                }
                            }
                        });
                    }
            } catch (SecurityException e){
            Log.d(TAG, "SecurityException - " + e.getMessage());
        }
    }
}
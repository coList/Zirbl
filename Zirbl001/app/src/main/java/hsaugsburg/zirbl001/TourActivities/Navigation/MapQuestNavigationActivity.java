package hsaugsburg.zirbl001.TourActivities.Navigation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.MarkerView;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapquest.mapping.MapQuestAccountManager;
import com.mapquest.mapping.maps.OnMapReadyCallback;
import com.mapquest.mapping.maps.MapboxMap;
import com.mapquest.mapping.maps.MapView;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.osmdroid.util.GeoPoint;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hsaugsburg.zirbl001.Datamanagement.JSONDownload.JSONUpdateRoad;
import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadLocationDoUKnow;
import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadNutLocation;
import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadStationLocation;
import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadTourChronology;
import hsaugsburg.zirbl001.Interfaces.TourActivity;
import hsaugsburg.zirbl001.Models.TourModels.ChronologyModel;
import hsaugsburg.zirbl001.Models.TourModels.DoUKnowModel;
import hsaugsburg.zirbl001.Models.TourModels.MapModels.NutModel;
import hsaugsburg.zirbl001.Models.TourModels.MapModels.StationModel;
import hsaugsburg.zirbl001.R;
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


    private ArrayList<NutModel> nuts;
    private int nutsCollected = 0;
    private ArrayList<DoUKnowModel> doUKnowModels;
    private List<MarkerView> nutMarker = new ArrayList<MarkerView>();

    private MapboxMap mMapboxMap;
    private MapView mMapView;
    private final GeoPoint ENDPOINT = new GeoPoint(48.36117, 10.90954);
    private String TAG = "main";
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private FusedLocationProviderApi locationProviderApi = LocationServices.FusedLocationApi;
    private double latTarget;
    private double lngTarget;
    private LatLng latLngMyPos;
    private Polyline polyline;
    private PolylineOptions mPolyline = new PolylineOptions();
    private boolean firstStart = true;

    //dot menu
    private TopDarkActionbar topDarkActionbar;

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
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
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
                mMapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 18));

                //Zirbl Marker für aktuelle Postition setzen
                mMapboxMap.getMyLocationViewSettings().setForegroundDrawable(getResources().getDrawable(R.drawable._map_zirbl),getResources().getDrawable(R.drawable._map_zirbl));
                mMapboxMap.getMyLocationViewSettings().setBackgroundTintColor(getResources().getColor(R.color.colorTransparent100));
                mMapboxMap.getMyLocationViewSettings().setAccuracyAlpha(0);

                //setPoyline(mMapboxMap);
                ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
                waypoints.add(new GeoPoint(userLat, userLng));
                waypoints.add(new GeoPoint(latTarget, lngTarget));

                new JSONUpdateRoad(MapQuestNavigationActivity.this).execute(waypoints);

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
    }

    public void getAllLatLngPoints(List<LatLng> points)  {

        drawPolyline(points);
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


                /*
                if (!alreadyExists) {
                    nutMarker.add(mMap.addMarker(new com.google.android.gms.maps.model.MarkerOptions()
                            .position(latLngNut)
                            .title("Eine goldene Nuss: " + nutID)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable._map_gold_zirbl))
                    ));
                }
                } else if (!listIsNutCollected.get(h)) {
                    for (Marker marker : nutMarker) {
                        if (marker.getPosition().latitude == latNut && marker.getPosition().longitude == lngNut) {  //setzte die Nuss, auf die wir eben geprüft haben, auf invisible
                            marker.setVisible(false);
                        }
                }
                */
            }
        }
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
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);}

    @Override
    protected void onDestroy()
    { super.onDestroy(); mMapView.onDestroy(); }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    { super.onSaveInstanceState(outState); mMapView.onSaveInstanceState(outState); }

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

        //setNuts();

        try {
            List<Address> adressList = geocoder.getFromLocation(latMyPos, lngMyPos, 1);

            mMapboxMap.updatePolyline(mPolyline.getPolyline());
            new JSONUpdateRoad(this).execute(waypoints);


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

}

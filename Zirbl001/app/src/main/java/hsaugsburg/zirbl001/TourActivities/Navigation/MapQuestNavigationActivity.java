package hsaugsburg.zirbl001.TourActivities.Navigation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
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
import hsaugsburg.zirbl001.R;


public class MapQuestNavigationActivity extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    final Context context = this;
    private MapboxMap mMapboxMap;
    private MapView mMapView;
    private final GeoPoint ENDPOINT = new GeoPoint(48.36117, 10.90954);
    Context mContext;
    private String TAG = "main";
    private MapQuestNavigationActivity activity;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private FusedLocationProviderApi locationProviderApi = LocationServices.FusedLocationApi;
    private LatLng latLngMyPos;
    private Polyline polyline;
    private PolylineOptions mPolyline = new PolylineOptions();
    private boolean firstStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapQuestAccountManager.start(getApplicationContext());

        firstStart = true;


        setContentView(R.layout.activity_mapquest_navigation);

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
                waypoints.add(ENDPOINT);

                new JSONUpdateRoad(MapQuestNavigationActivity.this).execute(waypoints);

                //Zielflagge setzen
                addMarker(new LatLng(ENDPOINT.getLatitude(), ENDPOINT.getLongitude()),
                        "Ziel", "Hier musst du hin",
                        R.drawable._map_flag);
            }
        });
    }

    public void getAllLatLngPoints(List<LatLng> points)  {

        /*
        if(points.size() > 2) {
            drawPolyline(points);
        }
        */

        drawPolyline(points);
    }

    private void drawPolyline(List<LatLng> coordinates) {

        if (polyline != null) {
            mMapboxMap.removePolyline(polyline);
        }

        // Draw Points on MapView
        polyline = mMapboxMap.addPolyline(new PolylineOptions()
                .addAll(coordinates)
                .color(getResources().getColor(R.color.colorAccent))
                .width(5));

    }

    private void addMarker( LatLng location, String title, String snippet, int image) {

        Icon icon = IconFactory.getInstance(mContext).fromResource(image);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location);
        markerOptions.title(title);
        markerOptions.snippet(snippet);
        markerOptions.icon(icon);
        mMapboxMap.addMarker(markerOptions);
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


    @Override
    public void onLocationChanged(Location location) {
        Double latMyPos = location.getLatitude();
        Double lngMyPos = location.getLongitude();
        latLngMyPos = new LatLng(latMyPos, lngMyPos);
        Geocoder geocoder = new Geocoder(getApplicationContext());

        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        waypoints.add(new GeoPoint(latMyPos, lngMyPos));
        waypoints.add(ENDPOINT);

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

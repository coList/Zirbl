package hsaugsburg.zirbl001.Datamanagement.JSONDownload;

import android.os.AsyncTask;
import android.util.Log;
import com.mapbox.mapboxsdk.geometry.LatLng;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

import hsaugsburg.zirbl001.TourActivities.Navigation.MapQuestNavigationActivity;

public class JSONUpdateRoad extends AsyncTask<Object, Void, List<LatLng>> {

    private MapQuestNavigationActivity activity;
    private String TAG = "UpdateRoad";

    public JSONUpdateRoad(MapQuestNavigationActivity activity) {
        this.activity = activity;
    }

    protected List<LatLng> doInBackground(Object... params) {
        @SuppressWarnings("unchecked")
        ArrayList<GeoPoint> waypoints = (ArrayList<GeoPoint>)params[0];
        Log.d(TAG, "Startpoint: " + waypoints.get(0) + "  -  Endpoint: " + waypoints.get(1));
        RoadManager roadManager = new MapQuestRoadManager("iZOc26RPSvNP2EGtBxvJ0XGbqU1R6A3N");
        roadManager.addRequestOption("routeType=pedestrian");

        List<GeoPoint> overleyGeoPoints = new ArrayList<>();
        List<LatLng> overleyLatLngPoints = new ArrayList<>();

        final Road road = roadManager.getRoad(waypoints);
        Log.d(TAG, "Road Status: " + road.mStatus);


        Polyline roadOverley = RoadManager.buildRoadOverlay(road);

        for (int i = 0; i < roadOverley.getPoints().size(); i++) {
            overleyGeoPoints.add(new GeoPoint(roadOverley.getPoints().get(i)));
            Log.d(TAG, "Points: " + roadOverley.getPoints().get(i));
        }
        //GeoPoints in LatLng Points umwandeln
        for (int i = 0; i < overleyGeoPoints.size(); i++) {
            overleyLatLngPoints.add(new LatLng(overleyGeoPoints.get(i).getLatitude(), overleyGeoPoints.get(i).getLongitude()));
        }



        return overleyLatLngPoints;
    }
    @Override
    protected void onPostExecute(List<LatLng> result) {

        //activity.getAllLatLngPoints(result);

    }
}
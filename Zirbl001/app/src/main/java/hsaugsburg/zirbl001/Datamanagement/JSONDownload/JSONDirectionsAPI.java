package hsaugsburg.zirbl001.Datamanagement.JSONDownload;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import hsaugsburg.zirbl001.Models.TourModels.MapModels.Route;
import hsaugsburg.zirbl001.TourActivities.Navigation.NavigationActivity;


public class JSONDirectionsAPI extends AsyncTask<String, String, List<Route>> {
    private static final String TAG = "Jsondirec1";
    private NavigationActivity activity;

    public JSONDirectionsAPI (NavigationActivity activity) {
        this.activity = activity;

    }


    protected List<Route> doInBackground(String... params) {
        HttpURLConnection connection;
        BufferedReader reader = null;

        try {
            URL url;
            url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer buffer = new StringBuffer();
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            String finalJson = buffer.toString();

            try {
                List<Route> routes = new ArrayList<>();
                JSONObject parentJSON = new JSONObject(finalJson);
                JSONArray routesJSON = parentJSON.getJSONArray("routes");

                for (int i = 0; i < routesJSON.length(); i++){
                    JSONObject routeJSON = routesJSON.getJSONObject(i);
                    Route route = new Route();
                    JSONObject overviewPolylineJSON = routeJSON.getJSONObject("overview_polyline");

                    route.setPoints(decodePolyLine(overviewPolylineJSON.getString("points")));

                    routes.add(route);
                }

                return routes;



            } catch (JSONException e) {
                e.printStackTrace();
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    protected void onPostExecute(List<Route> result){
        super.onPostExecute(result);
        try {
            activity.processDirectionData(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }

}
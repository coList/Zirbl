package hsaugsburg.zirbl001.Datamanagement;

import android.os.AsyncTask;
import android.util.Log;

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

import hsaugsburg.zirbl001.Models.StationLocationModel;
import hsaugsburg.zirbl001.NavigationActivities.Search.SearchActivity;


public class JSONStationLocation extends AsyncTask<String, String, StationLocationModel>{
        private SearchActivity activity;
        private int tourID;

        public JSONStationLocation (SearchActivity activity, int tourId) {
            this.activity = activity;
            this.tourID = tourId;
        }
        protected StationLocationModel doInBackground(String... params) {
            HttpURLConnection connection = null;
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
                    JSONArray parentArray = new JSONArray(finalJson);
                    JSONObject parentObject = parentArray.getJSONObject(0);

                    JSONArray mJSONArrayStationLocations = parentObject.getJSONArray("stationlocations");

                    StationLocationModel stationLocationModel = new StationLocationModel();
                    //StationModel stationModel = new StationModel();

                    for (int i = 0; i<mJSONArrayStationLocations.length()  ; i++) {
                        JSONObject mJSONObjectStationDetails = mJSONArrayStationLocations.optJSONObject(i);

                        if(mJSONObjectStationDetails.getInt("tourid") == tourID) {
                            stationLocationModel.setTourID(mJSONObjectStationDetails.getInt("tourid"));
                            stationLocationModel.setStationModel(mJSONObjectStationDetails.getJSONArray("stations"));
                            Log.d("JSONStationLocation", "doInBackground: hello" + stationLocationModel.getStationModel());

                        }
                }
                    return stationLocationModel;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        protected void onPostExecute(StationLocationModel result){
            super.onPostExecute(result);
            try {
                activity.processData(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }



package hsaugsburg.zirbl001.Datamanagement.DownloadTasks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import hsaugsburg.zirbl001.Interfaces.Callback;
import hsaugsburg.zirbl001.Interfaces.JSONModel;
import hsaugsburg.zirbl001.Models.OwnStatisticsModel;
import hsaugsburg.zirbl001.Models.TourSelectionModel;
import hsaugsburg.zirbl001.NavigationActivities.Profile.ProfileClassFragment;
import hsaugsburg.zirbl001.NavigationActivities.Profile.ProfileOwnFragment;
import hsaugsburg.zirbl001.NavigationActivities.TourDetailActivity;

public class DownloadIsTourFavorised extends AsyncTask<String, String, Boolean> {

    private TourDetailActivity tourDetailActivity;
    private String username;
    private int tourID;
    public DownloadIsTourFavorised (TourDetailActivity tourDetailActivity, String username, int tourID) {
        this.tourDetailActivity = tourDetailActivity;
        this.username = username;
        this.tourID = tourID;
    }

    protected Boolean doInBackground(String... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url;
            url = new URL(params[0] + "?username=" + username + "&tourid=" + tourID);
            Log.d("DownloadIsTourFavorised", url.toString());
            connection = (HttpURLConnection) url.openConnection();

            connection.connect();

            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer buffer = new StringBuffer();
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            String isFavorisedString = buffer.toString();
            Log.d("DownloadIsTourFavorised", isFavorisedString);

            Boolean isFavorised = "1".equals(isFavorisedString);
            Log.d("DownloadIsTourFavorisedBool", isFavorised.toString());

            return isFavorised;


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.toString();
        } finally{
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
    protected void onPostExecute(Boolean result){

        super.onPostExecute(result);
        tourDetailActivity.setIsFavorised(result);
    }

}

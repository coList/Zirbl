package hsaugsburg.zirbl001.Datamanagement.DownloadTasks;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import hsaugsburg.zirbl001.NavigationActivities.TourDetailActivity;

public class DownloadIsTourFavorised extends AsyncTask<String, String, Boolean> {
    private TourDetailActivity tourDetailActivity;
    private String username;
    private String deviceToken;
    private int tourID;

    public DownloadIsTourFavorised (TourDetailActivity tourDetailActivity, String username, String deviceToken, int tourID) {
        this.tourDetailActivity = tourDetailActivity;
        this.username = username;
        this.deviceToken = deviceToken;
        this.tourID = tourID;
    }

    protected Boolean doInBackground(String... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url;
            url = new URL(params[0] + "?username=" + username + "&devicetoken=" + deviceToken + "&tourid=" + tourID);
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

            Boolean isFavorised = "1".equals(isFavorisedString);

            return isFavorised;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
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

    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result != null) {
            tourDetailActivity.setIsFavorised(result);
        } else {
            tourDetailActivity.showNoInternetConnection();
        }
    }
}

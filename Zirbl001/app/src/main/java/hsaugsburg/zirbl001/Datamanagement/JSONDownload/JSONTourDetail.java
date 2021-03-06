package hsaugsburg.zirbl001.Datamanagement.JSONDownload;

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
import java.util.ArrayList;
import java.util.List;

import hsaugsburg.zirbl001.Models.NavigationModels.TourDetailModel;
import hsaugsburg.zirbl001.NavigationActivities.TourDetailActivity;

public class JSONTourDetail extends AsyncTask<String, String, TourDetailModel> {
    private TourDetailActivity tourDetailActivity;
    private int tourID;

    public JSONTourDetail (TourDetailActivity tourDetailActivity, int tourID) {
        this.tourDetailActivity = tourDetailActivity;
        this.tourID = tourID;
    }
    protected TourDetailModel doInBackground(String... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url;
            url = new URL(params[0] + "?tourid=" + tourID);
            Log.d("JSONTourDetail", Integer.toString(tourID));
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
                JSONArray mJsonArrayTourDetails = new JSONArray(finalJson);

                    JSONObject mJsonLObjectTourDetails = mJsonArrayTourDetails.getJSONObject(0);

                    TourDetailModel tourDetailModel = new TourDetailModel();
                    tourDetailModel.setTourName(mJsonLObjectTourDetails.getString("tourname"));
                    tourDetailModel.setTourID(mJsonLObjectTourDetails.getInt("tourid"));
                    tourDetailModel.setCategoryName(mJsonLObjectTourDetails.getString("categoryname"));
                    tourDetailModel.setCosts(mJsonLObjectTourDetails.getString("costs"));
                    tourDetailModel.setDifficultyName(mJsonLObjectTourDetails.getString("difficultyname"));
                    tourDetailModel.setDistance(mJsonLObjectTourDetails.getInt("distance"));
                    tourDetailModel.setDuration(mJsonLObjectTourDetails.getInt("duration"));
                    tourDetailModel.setMainPicture(mJsonLObjectTourDetails.getString("mainpicture"));
                    tourDetailModel.setDescription(mJsonLObjectTourDetails.getString("description"));
                    tourDetailModel.setMapPicture(mJsonLObjectTourDetails.getString("mappicture"));
                    tourDetailModel.setVideoPath(mJsonLObjectTourDetails.getString("videopath"));
                    tourDetailModel.setWarnings(mJsonLObjectTourDetails.getString("warnings"));
                    tourDetailModel.setShortDescription(mJsonLObjectTourDetails.getString("shortdescription"));
                    tourDetailModel.setEndLocation(mJsonLObjectTourDetails.getString("endlocation"));
                    tourDetailModel.setStartLocation(mJsonLObjectTourDetails.getString("startlocation"));

                    List<String> picturesPathList = new ArrayList<>();

                    if (!mJsonLObjectTourDetails.isNull("picturespath")) {
                        for (int j = 0; j < mJsonLObjectTourDetails.getJSONArray("picturespath").length(); j++) {
                            String picturesPath = mJsonLObjectTourDetails.getJSONArray("picturespath").getString(j);
                            picturesPathList.add(picturesPath);
                        }
                        tourDetailModel.setPicturesPath(picturesPathList);
                    }
                return tourDetailModel;
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

    protected void onPostExecute(TourDetailModel result) {
        super.onPostExecute(result);
        tourDetailActivity.processData(result);
    }
}
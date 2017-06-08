package hsaugsburg.zirbl001;

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


public class JSONTourDetail extends AsyncTask<String, String, List<JSONModel>> {
    Callback callback;

    JSONTourDetail (Callback callback) {
        this.callback = callback;
    }
    protected List<JSONModel> doInBackground(String... params) {
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

                JSONArray mJsonArrayTourDetails = parentObject.getJSONArray("tourdetails");

                List<JSONModel> tourDetailModelList = new ArrayList<>();

                for (int i = 0; i < mJsonArrayTourDetails.length(); i++) {
                    JSONObject mJsonLObjectTourDetails = mJsonArrayTourDetails.getJSONObject(i);

                    TourDetailModel tourDetailModel = new TourDetailModel();

                    tourDetailModel.setTourName(mJsonLObjectTourDetails.getString("tourname"));
                    tourDetailModel.setTourID(mJsonLObjectTourDetails.getInt("tourid"));
                    tourDetailModel.setCategoryName(mJsonLObjectTourDetails.getString("categoryname"));
                    tourDetailModel.setCosts(mJsonLObjectTourDetails.getString("costs"));
                    tourDetailModel.setDifficultyName(mJsonLObjectTourDetails.getString("difficultyname"));
                    tourDetailModel.setDistance(mJsonLObjectTourDetails.getInt("distance"));
                    tourDetailModel.setDuration(mJsonLObjectTourDetails.getInt("duration"));
                    tourDetailModel.setDescription(mJsonLObjectTourDetails.getString("description"));
                    tourDetailModel.setMapPicture(mJsonLObjectTourDetails.getString("mappicture"));
                    tourDetailModel.setVideoPath(mJsonLObjectTourDetails.getString("videopath"));
                    tourDetailModel.setWarnings(mJsonLObjectTourDetails.getString("warnings"));

                    List<String> picturesPathList = new ArrayList<>();

                    for (int j = 0; j < mJsonLObjectTourDetails.getJSONArray("picturespath").length(); j++) {
                        String picturesPath = mJsonLObjectTourDetails.getJSONArray("picturespath").getString(j);
                        picturesPathList.add(picturesPath);
                    }

                    tourDetailModel.setPicturesPath(picturesPathList);
                    // adding the final object in the list
                    tourDetailModelList.add(tourDetailModel);
                }

                Log.d("Test", ((TourDetailModel)tourDetailModelList.get(0)).getMapPicture());
                return tourDetailModelList;
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
    protected void onPostExecute(List<JSONModel> result){
        super.onPostExecute(result);

        callback.processData(result);
    }

}
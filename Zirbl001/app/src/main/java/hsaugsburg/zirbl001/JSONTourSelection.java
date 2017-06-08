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


public class JSONTourSelection extends AsyncTask<String, String, List<JSONModel>> {

    Callback callback;

    JSONTourSelection (Callback callback) {
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

                JSONArray mJsonArrayTourSelections = parentObject.getJSONArray("tourselection");

                List<JSONModel> tourSelectionModelList = new ArrayList<>();

                for (int i = 0; i < mJsonArrayTourSelections.length(); i++) {
                    JSONObject mJsonLObjectTourSelection = mJsonArrayTourSelections.getJSONObject(i);

                    TourSelectionModel tourSelectionModel = new TourSelectionModel();

                    tourSelectionModel.setCategoryID(mJsonLObjectTourSelection.getInt("categoryid"));
                    tourSelectionModel.setCategoryName(mJsonLObjectTourSelection.getString("categoryname"));
                    tourSelectionModel.setTourID(mJsonLObjectTourSelection.getInt("tourid"));
                    tourSelectionModel.setTourName(mJsonLObjectTourSelection.getString("tourname"));
                    tourSelectionModel.setDifficultyName(mJsonLObjectTourSelection.getString("difficultyname"));
                    tourSelectionModel.setDuration(mJsonLObjectTourSelection.getInt("duration"));
                    tourSelectionModel.setDistance(mJsonLObjectTourSelection.getInt("distance"));
                    tourSelectionModel.setMainpicture(mJsonLObjectTourSelection.getString("mainpicture"));

                    // adding the final object in the list
                    tourSelectionModelList.add(tourSelectionModel);
                }

                Log.d("TestSelection", ((TourSelectionModel)tourSelectionModelList.get(0)).getDifficultyName());
                return tourSelectionModelList;
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

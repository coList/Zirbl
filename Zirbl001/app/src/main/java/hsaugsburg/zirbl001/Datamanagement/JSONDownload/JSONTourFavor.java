package hsaugsburg.zirbl001.Datamanagement.JSONDownload;

import android.os.AsyncTask;

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

import hsaugsburg.zirbl001.Interfaces.Callback;
import hsaugsburg.zirbl001.Interfaces.JSONModel;
import hsaugsburg.zirbl001.Models.NavigationModels.TourFavorModel;

public class JSONTourFavor extends AsyncTask<String, String, List<JSONModel>> {
    private Callback callback;

    public JSONTourFavor(Callback callback) {
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
                List<JSONModel> tourFavorModelList = new ArrayList<>();

                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject mJsonLObjectTourFavor = parentArray.getJSONObject(i);

                    TourFavorModel tourFavorModel = new TourFavorModel();

                    tourFavorModel.setUserID(mJsonLObjectTourFavor.getInt("userid"));
                    tourFavorModel.setTourID(mJsonLObjectTourFavor.getInt("tourid"));
                    tourFavorModel.setTourName(mJsonLObjectTourFavor.getString("tourname"));
                    tourFavorModel.setDifficultyName(mJsonLObjectTourFavor.getString("difficultyname"));
                    tourFavorModel.setDuration(mJsonLObjectTourFavor.getInt("duration"));
                    tourFavorModel.setDistance(mJsonLObjectTourFavor.getInt("distance"));
                    tourFavorModel.setMainpicture(mJsonLObjectTourFavor.getString("mainpicture"));

                    tourFavorModelList.add(tourFavorModel);
                }
                return tourFavorModelList;
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

    protected void onPostExecute(List<JSONModel> result) {
        super.onPostExecute(result);
        callback.processData(result);
    }
}

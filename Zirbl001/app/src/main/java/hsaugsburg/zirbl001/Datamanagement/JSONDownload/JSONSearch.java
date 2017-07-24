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

import hsaugsburg.zirbl001.Models.NavigationModels.SearchModel;
import hsaugsburg.zirbl001.NavigationActivities.Search.SearchActivity;

public class JSONSearch extends AsyncTask<String, String, List<SearchModel>> {
    private SearchActivity searchActivity;

    public JSONSearch(SearchActivity searchActivity) {
        this.searchActivity = searchActivity;
    }

    protected List<SearchModel> doInBackground(String... params) {
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
                JSONArray mJsonArrayTourDetails = parentObject.getJSONArray("searchdetails");

                List<SearchModel> searchModelList = new ArrayList<>();

                for (int i = 0; i < mJsonArrayTourDetails.length(); i++) {
                    JSONObject mJsonLObjectTourDetails = mJsonArrayTourDetails.getJSONObject(i);
                    SearchModel searchModel = new SearchModel();

                    searchModel.setTourName(mJsonLObjectTourDetails.getString("tourname"));
                    searchModel.setTourID(mJsonLObjectTourDetails.getInt("tourid"));
                    searchModel.setCategoryName(mJsonLObjectTourDetails.getString("categoryname"));
                    searchModel.setDifficultyName(mJsonLObjectTourDetails.getString("difficultyname"));
                    searchModel.setDistance(mJsonLObjectTourDetails.getInt("distance"));
                    searchModel.setDuration(mJsonLObjectTourDetails.getInt("duration"));
                    searchModel.setShortDescription(mJsonLObjectTourDetails.getString("shortdescription"));

                    searchModelList.add(searchModel);
                }
                return searchModelList;
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

    protected void onPostExecute(List<SearchModel> result) {
        super.onPostExecute(result);
        searchActivity.processData(result);
    }
}
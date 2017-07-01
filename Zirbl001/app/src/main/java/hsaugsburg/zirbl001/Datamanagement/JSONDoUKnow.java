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
import java.util.ArrayList;
import java.util.List;

import hsaugsburg.zirbl001.Models.DoUKnowModel;
import hsaugsburg.zirbl001.Models.LettersModel;
import hsaugsburg.zirbl001.TourActivities.DoUKnowActivity;
import hsaugsburg.zirbl001.TourActivities.LettersActivity;

public class JSONDoUKnow extends AsyncTask<String, String, DoUKnowModel> {
    private DoUKnowActivity activity;
    private int selectedTour;
    private int infoPopupID;

    public JSONDoUKnow (DoUKnowActivity activity, int selectedTour, int infoPopupID) {
        this.activity = activity;
        this.selectedTour = selectedTour;
        this.infoPopupID = infoPopupID;
    }

    protected DoUKnowModel doInBackground(String... params) {
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

                JSONArray mJsonArrayTourDoUKnow = parentObject.getJSONArray("tourinfopopups");

                DoUKnowModel doUKnowModel = new DoUKnowModel();
                for (int i = 0; i < mJsonArrayTourDoUKnow.length(); i++) {
                    JSONObject mJsonLObjectTourDoUKnow = mJsonArrayTourDoUKnow.getJSONObject(i);

                    if (mJsonLObjectTourDoUKnow.getInt("tourid") == selectedTour) {

                        JSONArray mJSONArrayDoUKnow = mJsonLObjectTourDoUKnow.getJSONArray("infopopups");

                        for (int j = 0; j < mJSONArrayDoUKnow.length(); j++) {
                            JSONObject mJsonLObjectDoUKnow = mJSONArrayDoUKnow.getJSONObject(j);

                            if (mJsonLObjectDoUKnow.getInt("infopopupid") == infoPopupID) {
                                doUKnowModel.setTourID(mJsonLObjectDoUKnow.getInt("tourid"));
                                doUKnowModel.setInfoPopupID(mJsonLObjectDoUKnow.getInt("infopopupid"));
                                doUKnowModel.setContentText(mJsonLObjectDoUKnow.getString("contenttext"));
                                doUKnowModel.setPicturePath(mJsonLObjectDoUKnow.getString("picturepath"));



                                //doUKnowModel.setLatitude(mJsonLObjectDoUKnow.getDouble("latitude"));
                                //doUKnowModel.setLongitude(mJsonLObjectDoUKnow.getDouble("longitude"));
                            }
                        }
                    }



                }


                return doUKnowModel;
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
    protected void onPostExecute(DoUKnowModel result){
        super.onPostExecute(result);
        activity.processData(result);
    }

}

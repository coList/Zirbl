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

import hsaugsburg.zirbl001.Models.SliderModel;
import hsaugsburg.zirbl001.TourActivities.SliderActivity;

public class JSONSlider extends AsyncTask<String, String, SliderModel> {
    private SliderActivity activity;
    private int taskID;

    public JSONSlider (SliderActivity activity, int taskID) {
        this.activity = activity;
        this.taskID = taskID;
    }

    protected SliderModel doInBackground(String... params) {
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

                JSONArray mJsonArraySlider = parentObject.getJSONArray("guessthenumber");
                SliderModel sliderModel = new SliderModel();
                for (int i = 0; i < mJsonArraySlider.length(); i++) {
                    JSONObject mJsonLObjectSlider = mJsonArraySlider.getJSONObject(i);

                    if (mJsonLObjectSlider.getInt("taskid") == taskID) {
                        sliderModel.setTaskID(mJsonLObjectSlider.getInt("taskid"));
                        sliderModel.setStationID(mJsonLObjectSlider.getInt("stationid"));
                        sliderModel.setTourID(mJsonLObjectSlider.getInt("tourid"));
                        //sliderModel.setScore(mJsonLObjectSlider.getInt("score"));
                        sliderModel.setQuestion(mJsonLObjectSlider.getString("question"));
                        sliderModel.setAnswerCorrect(mJsonLObjectSlider.getString("answercorrect"));
                        sliderModel.setAnswerWrong(mJsonLObjectSlider.getString("answerwrong"));
                        sliderModel.setPicturePath(mJsonLObjectSlider.getString("picturepath"));
                        sliderModel.setRightNumber(mJsonLObjectSlider.getDouble("rightnumber"));
                        sliderModel.setMinRange(mJsonLObjectSlider.getDouble("minrange"));
                        sliderModel.setMaxRange(mJsonLObjectSlider.getDouble("maxrange"));
                    }


                }


                return sliderModel;
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
    protected void onPostExecute(SliderModel result){
        super.onPostExecute(result);
        activity.processData(result);
    }
}

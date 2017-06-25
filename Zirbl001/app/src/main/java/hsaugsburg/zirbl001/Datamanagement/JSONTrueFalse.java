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

import hsaugsburg.zirbl001.Models.LettersModel;
import hsaugsburg.zirbl001.Models.TrueFalseModel;
import hsaugsburg.zirbl001.TourActivities.LettersActivity;
import hsaugsburg.zirbl001.TourActivities.TrueFalseActivity;

public class JSONTrueFalse extends AsyncTask<String, String, TrueFalseModel> {
    private TrueFalseActivity activity;
    private int taskID;

    public JSONTrueFalse (TrueFalseActivity activity, int taskID) {
        this.activity = activity;
        this.taskID = taskID;
    }

    protected TrueFalseModel doInBackground(String... params) {
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

                JSONArray mJsonArrayTrueFalse = parentObject.getJSONArray("truefalse");

                TrueFalseModel trueFalseModel = new TrueFalseModel();

                for (int i = 0; i < mJsonArrayTrueFalse.length(); i++) {
                    JSONObject mJsonLObjectTrueFalse = mJsonArrayTrueFalse.getJSONObject(i);

                    if (mJsonLObjectTrueFalse.getInt("taskid") == taskID) {
                        trueFalseModel.setTaskID(mJsonLObjectTrueFalse.getInt("taskid"));
                        trueFalseModel.setStationID(mJsonLObjectTrueFalse.getInt("stationid"));
                        trueFalseModel.setTourID(mJsonLObjectTrueFalse.getInt("tourid"));
                        trueFalseModel.setScore(mJsonLObjectTrueFalse.getInt("score"));
                        trueFalseModel.setQuestion(mJsonLObjectTrueFalse.getString("question"));

                        trueFalseModel.setAnswerCorrect(mJsonLObjectTrueFalse.getString("answercorrect"));
                        trueFalseModel.setAnswerWrong(mJsonLObjectTrueFalse.getString("answerwrong"));
                        trueFalseModel.setPicturePath(mJsonLObjectTrueFalse.getString("picturepath"));
                        trueFalseModel.setIsTrue(mJsonLObjectTrueFalse.getBoolean("istrue"));
                    }


                }


                return trueFalseModel;
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
    protected void onPostExecute(TrueFalseModel result){
        super.onPostExecute(result);
        activity.processData(result);
    }

}

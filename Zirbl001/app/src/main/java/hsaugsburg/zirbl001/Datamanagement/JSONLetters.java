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
import hsaugsburg.zirbl001.TourActivities.LettersActivity;


public class JSONLetters extends AsyncTask<String, String, LettersModel> {
    private LettersActivity activity;

    private int taskID;

    public JSONLetters (LettersActivity activity, int taskID) {
        this.activity = activity;
        this.taskID = taskID;
    }

    protected LettersModel doInBackground(String... params) {
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

                JSONArray mJsonArrayLetters = parentObject.getJSONArray("letters");

                LettersModel lettersModel = new LettersModel();

                for (int i = 0; i < mJsonArrayLetters.length(); i++) {
                    JSONObject mJsonLObjectLetters = mJsonArrayLetters.getJSONObject(i);

                    if (mJsonLObjectLetters.getInt("taskid") == taskID) {
                        lettersModel.setTaskID(mJsonLObjectLetters.getInt("taskid"));
                        lettersModel.setStationID(mJsonLObjectLetters.getInt("stationid"));
                        lettersModel.setTourID(mJsonLObjectLetters.getInt("tourid"));
                        lettersModel.setScore(mJsonLObjectLetters.getInt("score"));
                        lettersModel.setQuestion(mJsonLObjectLetters.getString("question"));
                        lettersModel.setSolution(mJsonLObjectLetters.getString("solution"));
                        lettersModel.setAnswerCorrect(mJsonLObjectLetters.getString("answercorrect"));
                        lettersModel.setAnswerWrong(mJsonLObjectLetters.getString("answerwrong"));
                        lettersModel.setPicturePath(mJsonLObjectLetters.getString("picturepath"));
                        lettersModel.setOtherLetters(mJsonLObjectLetters.getString("randomletters"));
                    }
                }


                return lettersModel;
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
    protected void onPostExecute(LettersModel result){
        super.onPostExecute(result);
        activity.processData(result);
    }

}

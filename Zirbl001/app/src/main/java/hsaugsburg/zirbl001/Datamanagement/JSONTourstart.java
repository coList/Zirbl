package hsaugsburg.zirbl001.Datamanagement;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import hsaugsburg.zirbl001.Models.ChronologyModel;
import hsaugsburg.zirbl001.Models.LettersModel;
import hsaugsburg.zirbl001.TourActivities.TourstartActivity;

public class JSONTourstart extends AsyncTask<String, String, ChronologyModel> {
    private TourstartActivity activity;

    public JSONTourstart (TourstartActivity activity) {
        this.activity = activity;
    }

    protected ChronologyModel doInBackground(String... params) {
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

                JSONArray mJsonArrayChronologies = parentObject.getJSONArray("tourchronology");

                //List<LettersModel> lettersModelList = new ArrayList<>();
                ChronologyModel firstChronology = new ChronologyModel();


                    FileOutputStream fileout= activity.openFileOutput("chronology.txt", activity.MODE_PRIVATE);
                    OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);




                for (int i = 0; i < mJsonArrayChronologies.length(); i++) {
                    JSONObject mJsonLObjectChronologies = mJsonArrayChronologies.getJSONObject(i);

                    if (mJsonLObjectChronologies.getInt("tourid") == activity.getSelectedTour()) {
                        JSONArray mJsonArrayChronologyItems = mJsonLObjectChronologies.getJSONArray("chronology");
                        outputWriter.write(mJsonArrayChronologyItems.toString());

                        for (int j = 0; j < mJsonArrayChronologyItems.length(); j++) {
                            JSONObject mJsonLObjectChronologyItems = mJsonArrayChronologyItems.getJSONObject(j);
                            if (j == 0) {
                                firstChronology.setTourID(mJsonLObjectChronologyItems.getInt("tourid"));
                                firstChronology.setChronologyNumber(mJsonLObjectChronologyItems.getInt("chronologynumber"));
                                firstChronology.setInfoPopupID(mJsonLObjectChronologyItems.getInt("infopopupid"));
                            }
                        }
                    }
                }

                outputWriter.close();


                return firstChronology;
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
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
    protected void onPostExecute(ChronologyModel result){
        super.onPostExecute(result);
        activity.processData(result);
    }

}

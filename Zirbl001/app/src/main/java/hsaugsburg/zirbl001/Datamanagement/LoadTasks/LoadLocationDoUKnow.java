package hsaugsburg.zirbl001.Datamanagement.LoadTasks;

import android.app.Activity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import hsaugsburg.zirbl001.Models.DoUKnowModel;

public class LoadLocationDoUKnow {

    static final int READ_BLOCK_SIZE = 100;
    private Activity activity;
    private int tourID;

    public LoadLocationDoUKnow(Activity activity, int tourID) {
        this.activity = activity;
        this.tourID = tourID;
    }

    public ArrayList<DoUKnowModel> readFile() {
        ArrayList<DoUKnowModel> doUKnowModels = new ArrayList<>();

        try {
            FileInputStream fileIn = activity.openFileInput("location_infopopups" + tourID + ".txt");
            InputStreamReader InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer = new char[READ_BLOCK_SIZE];
            String s = "";
            int charRead;

            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                s += readstring;

            }
            InputRead.close();

            JSONArray jsonArray = new JSONArray(s);

            Log.d("LoadLocation", "outside for");

            for (int j = 0; j < jsonArray.length(); j++) {
                Log.d("LoadLocation", "inside for");
                JSONObject mJsonLObjectDoUKnow = jsonArray.getJSONObject(j);

                DoUKnowModel doUKnowModel = new DoUKnowModel();

                doUKnowModel.setTourID(mJsonLObjectDoUKnow.getInt("tourid"));
                doUKnowModel.setInfoPopupID(mJsonLObjectDoUKnow.getInt("infopopupid"));
                doUKnowModel.setContentText(mJsonLObjectDoUKnow.getString("contenttext"));
                doUKnowModel.setPicturePath(mJsonLObjectDoUKnow.getString("picture"));
                doUKnowModel.setLatitude(mJsonLObjectDoUKnow.getDouble("latitude"));
                doUKnowModel.setLongitude(mJsonLObjectDoUKnow.getDouble("longitude"));

                doUKnowModels.add(doUKnowModel);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return doUKnowModels;

    }
}

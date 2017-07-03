package hsaugsburg.zirbl001.Datamanagement.LoadTasks;

import android.app.Activity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStreamReader;

import hsaugsburg.zirbl001.Models.DoUKnowModel;

public class LoadDoUKnow {

    static final int READ_BLOCK_SIZE = 100;
    private Activity activity;
    private int tourID;
    private int infoPopupID;

    public LoadDoUKnow(Activity activity, int tourID, int infoPopupID) {
        this.activity = activity;
        this.tourID = tourID;
        this.infoPopupID = infoPopupID;
    }

    public DoUKnowModel readFile() {
        DoUKnowModel doUKnowModel = new DoUKnowModel();
        try {
            FileInputStream fileIn=activity.openFileInput("infopopups" + tourID + ".txt");
            InputStreamReader InputRead= new InputStreamReader(fileIn);

            char[] inputBuffer= new char[READ_BLOCK_SIZE];
            String s="";
            int charRead;

            while ((charRead=InputRead.read(inputBuffer))>0) {
                // char to string conversion
                String readstring=String.copyValueOf(inputBuffer,0,charRead);
                s +=readstring;

            }
            InputRead.close();

            JSONArray jsonArray = new JSONArray(s);

            Log.d("LoadDoUTour", Integer.toString(tourID));

            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject mJsonLObjectDoUKnow = jsonArray.getJSONObject(j);

                if (mJsonLObjectDoUKnow.getInt("infopopupid") == infoPopupID) {
                    doUKnowModel.setTourID(mJsonLObjectDoUKnow.getInt("tourid"));
                    doUKnowModel.setInfoPopupID(mJsonLObjectDoUKnow.getInt("infopopupid"));
                    doUKnowModel.setContentText(mJsonLObjectDoUKnow.getString("contenttext"));
                    doUKnowModel.setPicturePath(mJsonLObjectDoUKnow.getString("picturepath"));



                    //doUKnowModel.setLatitude(mJsonLObjectDoUKnow.getDouble("latitude"));
                    //doUKnowModel.setLongitude(mJsonLObjectDoUKnow.getDouble("longitude"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return doUKnowModel;

    }
}

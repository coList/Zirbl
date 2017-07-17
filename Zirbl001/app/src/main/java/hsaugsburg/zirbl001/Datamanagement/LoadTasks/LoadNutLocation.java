package hsaugsburg.zirbl001.Datamanagement.LoadTasks;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import hsaugsburg.zirbl001.Models.TourModels.MapModels.NutModel;

public class LoadNutLocation {
    private Activity activity;
    private int tourID;

    public LoadNutLocation(Activity activity, int tourID) {
        this.activity = activity;
        this.tourID = tourID;
    }

    public ArrayList<NutModel> readFile() {
        ArrayList<NutModel> nuts = new ArrayList<>();
        try {
            FileInputStream fileIn = activity.openFileInput("nuts" + tourID + ".txt");
            InputStreamReader InputRead = new InputStreamReader(fileIn);

            int READ_BLOCK_SIZE = 100;
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

            for (int y = 0; y < jsonArray.length(); y++) {
                JSONObject mJSONObjectNut = jsonArray.optJSONObject(y);
                NutModel nutModel = new NutModel();

                nutModel.setTourID(mJSONObjectNut.getInt("tourid"));
                nutModel.setNutID(mJSONObjectNut.getInt("nutid"));
                nutModel.setLatitude(mJSONObjectNut.getDouble("latitude"));
                nutModel.setLongitude(mJSONObjectNut.getDouble("longitude"));
                nutModel.setScore(mJSONObjectNut.getInt("score"));
                nutModel.setFoundText(mJSONObjectNut.getString("foundtext"));


                nuts.add(nutModel);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return nuts;

    }
}

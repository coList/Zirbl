package hsaugsburg.zirbl001.Datamanagement.LoadTasks;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStreamReader;

import hsaugsburg.zirbl001.Models.TourModels.SliderModel;

public class LoadSlider {

    static final int READ_BLOCK_SIZE = 100;
    private Activity activity;
    private int tourID;
    private int taskID;

    public LoadSlider(Activity activity, int tourID, int taskID) {
        this.activity = activity;
        this.tourID = tourID;
        this.taskID = taskID;
    }

    public SliderModel readFile() {
        SliderModel sliderModel = new SliderModel();
        try {
            FileInputStream fileIn=activity.openFileInput("guessthenumber" + tourID + ".txt");
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

            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject mJsonLObjectSlider =  jsonArray.getJSONObject(j);
                if (mJsonLObjectSlider.getInt("taskid") == taskID) {
                    sliderModel.setTaskID(mJsonLObjectSlider.getInt("taskid"));

                    if (!mJsonLObjectSlider.isNull("stationid")) {
                        sliderModel.setStationID(mJsonLObjectSlider.getInt("stationid"));
                    }
                    if (!mJsonLObjectSlider.isNull("tourid")) {
                        sliderModel.setTourID(mJsonLObjectSlider.getInt("tourid"));
                    }
                    sliderModel.setScore(mJsonLObjectSlider.getInt("score"));
                    sliderModel.setQuestion(mJsonLObjectSlider.getString("question"));
                    sliderModel.setAnswerCorrect(mJsonLObjectSlider.getString("answercorrect"));
                    sliderModel.setAnswerWrong(mJsonLObjectSlider.getString("answerwrong"));
                    sliderModel.setPicturePath(mJsonLObjectSlider.getString("picturepath"));
                    sliderModel.setMaxRange(mJsonLObjectSlider.getDouble("maxrange"));
                    sliderModel.setMinRange(mJsonLObjectSlider.getDouble("minrange"));
                    sliderModel.setRightNumber(mJsonLObjectSlider.getDouble("rightnumber"));
                    sliderModel.setIsInteger(mJsonLObjectSlider.getBoolean("isinteger"));
                    sliderModel.setToleranceRange(mJsonLObjectSlider.getInt("tolerancerange"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sliderModel;

    }
}

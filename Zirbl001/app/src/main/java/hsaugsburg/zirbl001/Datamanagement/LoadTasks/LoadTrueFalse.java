package hsaugsburg.zirbl001.Datamanagement.LoadTasks;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStreamReader;

import hsaugsburg.zirbl001.Models.TourModels.TrueFalseModel;

public class LoadTrueFalse {
    private Activity activity;
    private int tourID;
    private int taskID;

    public LoadTrueFalse(Activity activity, int tourID, int taskID) {
        this.activity = activity;
        this.tourID = tourID;
        this.taskID = taskID;
    }

    public TrueFalseModel readFile() {
        TrueFalseModel trueFalseModel = new TrueFalseModel();
        try {
            FileInputStream fileIn = activity.openFileInput("truefalse" + tourID + ".txt");
            InputStreamReader InputRead= new InputStreamReader(fileIn);

            int READ_BLOCK_SIZE = 100;
            char[] inputBuffer= new char[READ_BLOCK_SIZE];
            String s="";
            int charRead;

            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                // char to string conversion
                String readstring=String.copyValueOf(inputBuffer,0,charRead);
                s +=readstring;

            }
            InputRead.close();


            JSONArray jsonArray = new JSONArray(s);

            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject mJsonLObjectTrueFalse = jsonArray.getJSONObject(j);

                if (mJsonLObjectTrueFalse.getInt("taskid") == taskID) {
                    trueFalseModel.setTaskID(mJsonLObjectTrueFalse.getInt("taskid"));

                    if (!mJsonLObjectTrueFalse.isNull("stationid")) {
                        trueFalseModel.setStationID(mJsonLObjectTrueFalse.getInt("stationid"));
                    }
                    if (!mJsonLObjectTrueFalse.isNull("tourid")) {
                        trueFalseModel.setTourID(mJsonLObjectTrueFalse.getInt("tourid"));
                    }
                    trueFalseModel.setScore(mJsonLObjectTrueFalse.getInt("score"));
                    trueFalseModel.setQuestion(mJsonLObjectTrueFalse.getString("question"));

                    trueFalseModel.setAnswerCorrect(mJsonLObjectTrueFalse.getString("answercorrect"));
                    trueFalseModel.setAnswerWrong(mJsonLObjectTrueFalse.getString("answerwrong"));
                    trueFalseModel.setPicturePath(mJsonLObjectTrueFalse.getString("picturepath"));
                    trueFalseModel.setIsTrue(mJsonLObjectTrueFalse.getBoolean("istrue"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return trueFalseModel;

    }
}

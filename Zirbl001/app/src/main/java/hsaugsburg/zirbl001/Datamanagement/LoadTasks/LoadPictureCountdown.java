package hsaugsburg.zirbl001.Datamanagement.LoadTasks;

import android.app.Activity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStreamReader;

import hsaugsburg.zirbl001.Models.TourModels.PictureCountdownModel;
import hsaugsburg.zirbl001.Models.TourModels.QuizModel;

public class LoadPictureCountdown {
    private Activity activity;
    private int tourID;
    private int taskID;

    public LoadPictureCountdown(Activity activity, int tourID, int taskID) {
        this.activity = activity;
        this.tourID = tourID;
        this.taskID = taskID;
    }

    public PictureCountdownModel readFile() {
        PictureCountdownModel pictureCountdownModel = new PictureCountdownModel();

        try {
            FileInputStream fileIn = activity.openFileInput("picturecountdown" + tourID + ".txt");
            InputStreamReader InputRead = new InputStreamReader(fileIn);


            int READ_BLOCK_SIZE = 100;
            char[] inputBuffer = new char[READ_BLOCK_SIZE];
            String s = "";
            int charRead;

            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                String readstring = String.copyValueOf(inputBuffer, 0 , charRead);
                s += readstring;
            }
            InputRead.close();

            JSONArray jsonArray = new JSONArray(s);

            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject mJsonLObjectPictureCoundown = jsonArray.getJSONObject(j);

                if (mJsonLObjectPictureCoundown.getInt("taskid") == taskID) {
                    pictureCountdownModel.setTaskID(mJsonLObjectPictureCoundown.getInt("taskid"));
                    if (!mJsonLObjectPictureCoundown.isNull("stationid")) {
                        pictureCountdownModel.setStationID(mJsonLObjectPictureCoundown.getInt("stationid"));
                    }
                    if (!mJsonLObjectPictureCoundown.isNull("tourid")) {
                        pictureCountdownModel.setTourID(mJsonLObjectPictureCoundown.getInt("tourid"));
                    }
                    pictureCountdownModel.setScore(mJsonLObjectPictureCoundown.getInt("score"));
                    pictureCountdownModel.setQuestion(mJsonLObjectPictureCoundown.getString("question"));
                    pictureCountdownModel.setAnswerCorrect(mJsonLObjectPictureCoundown.getString("answercorrect"));
                    pictureCountdownModel.setAnswerWrong(mJsonLObjectPictureCoundown.getString("answerwrong"));
                    pictureCountdownModel.setPicturePath(mJsonLObjectPictureCoundown.getString("picturepath"));
                    pictureCountdownModel.setRightAnswer(mJsonLObjectPictureCoundown.getString("rightanswer"));
                    pictureCountdownModel.setOption2(mJsonLObjectPictureCoundown.getString("option2"));
                    pictureCountdownModel.setOption3(mJsonLObjectPictureCoundown.getString("option3"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pictureCountdownModel;
    }
}

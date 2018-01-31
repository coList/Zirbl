package hsaugsburg.zirbl001.Datamanagement.LoadTasks;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStreamReader;

import hsaugsburg.zirbl001.Models.TourModels.LettersModel;

public class LoadLetters {
    private Activity activity;
    private int tourID;
    private int taskID;

    public LoadLetters(Activity activity, int tourID, int taskID) {
        this.activity = activity;
        this.tourID = tourID;
        this.taskID = taskID;
    }

    public LettersModel readFile() {
        LettersModel lettersModel = new LettersModel();
        try {
            FileInputStream fileIn = activity.openFileInput("letters" + tourID + ".txt");
            InputStreamReader InputRead = new InputStreamReader(fileIn);

            int READ_BLOCK_SIZE = 100;
            char[] inputBuffer = new char[READ_BLOCK_SIZE];
            String s = "";
            int charRead;

            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                s += readstring;
            }
            InputRead.close();

            JSONArray jsonArray = new JSONArray(s);

            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject mJsonLObjectLetters =  jsonArray.getJSONObject(j);
                if (mJsonLObjectLetters.getInt("taskid") == taskID) {
                    lettersModel.setTaskID(mJsonLObjectLetters.getInt("taskid"));

                    if (!mJsonLObjectLetters.isNull("stationid")) {
                        lettersModel.setStationID(mJsonLObjectLetters.getInt("stationid"));
                    }
                    if (!mJsonLObjectLetters.isNull("tourid")) {
                        lettersModel.setTourID(mJsonLObjectLetters.getInt("tourid"));
                    }
                    lettersModel.setScore(mJsonLObjectLetters.getInt("score"));
                    lettersModel.setQuestion(mJsonLObjectLetters.getString("question"));
                    lettersModel.setSolution(mJsonLObjectLetters.getString("solution"));
                    lettersModel.setAnswerCorrect(mJsonLObjectLetters.getString("answercorrect"));
                    lettersModel.setAnswerWrong(mJsonLObjectLetters.getString("answerwrong"));
                    lettersModel.setPicturePath(mJsonLObjectLetters.getString("picturepath"));
                    lettersModel.setOtherLetters(mJsonLObjectLetters.getString("randomletters"));

                    lettersModel.setAnswerPicture(mJsonLObjectLetters.getString("answerpicture"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lettersModel;
    }
}

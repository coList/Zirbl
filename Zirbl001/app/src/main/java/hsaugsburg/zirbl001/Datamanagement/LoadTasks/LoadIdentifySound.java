package hsaugsburg.zirbl001.Datamanagement.LoadTasks;


import android.app.Activity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStreamReader;

import hsaugsburg.zirbl001.Models.TourModels.IdentifySoundModel;

public class LoadIdentifySound {

    private Activity activity;
    private int tourID;
    private int taskID;

    public LoadIdentifySound(Activity activity, int tourID, int taskID) {
        this.activity = activity;
        this.tourID = tourID;
        this.taskID = taskID;
    }

    public IdentifySoundModel readFile() {
        IdentifySoundModel identifySoundModel = new IdentifySoundModel();

        try {
            FileInputStream fileIn = activity.openFileInput("identifysound" + tourID + ".txt");
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
                JSONObject mJsonLObjectSound = jsonArray.getJSONObject(j);

                if (mJsonLObjectSound.getInt("taskid") == taskID) {
                    identifySoundModel.setTaskID(mJsonLObjectSound.getInt("taskid"));
                    if (!mJsonLObjectSound.isNull("stationid")) {
                        identifySoundModel.setStationID(mJsonLObjectSound.getInt("stationid"));
                    }
                    if (!mJsonLObjectSound.isNull("tourid")) {
                        identifySoundModel.setTourID(mJsonLObjectSound.getInt("tourid"));
                    }
                    identifySoundModel.setScore(mJsonLObjectSound.getInt("score"));
                    identifySoundModel.setQuestion(mJsonLObjectSound.getString("question"));
                    identifySoundModel.setAnswerCorrect(mJsonLObjectSound.getString("answercorrect"));
                    identifySoundModel.setAnswerWrong(mJsonLObjectSound.getString("answerwrong"));
                    identifySoundModel.setRightAnswer(mJsonLObjectSound.getString("rightanswer"));
                    identifySoundModel.setOption2(mJsonLObjectSound.getString("option2"));
                    identifySoundModel.setOption3(mJsonLObjectSound.getString("option3"));
                    identifySoundModel.setOption4(mJsonLObjectSound.getString("option4"));
                    identifySoundModel.setAudio(mJsonLObjectSound.getString("audio"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return identifySoundModel;
    }
}

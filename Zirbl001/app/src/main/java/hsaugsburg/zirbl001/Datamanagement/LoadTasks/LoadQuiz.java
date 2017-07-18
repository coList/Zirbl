package hsaugsburg.zirbl001.Datamanagement.LoadTasks;

import android.app.Activity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStreamReader;

import hsaugsburg.zirbl001.Models.TourModels.QuizModel;

public class LoadQuiz {
    private Activity activity;
    private int tourID;
    private int taskID;

    public LoadQuiz(Activity activity, int tourID, int taskID) {
        this.activity = activity;
        this.tourID = tourID;
        this.taskID = taskID;
    }

    public QuizModel readFile() {
        QuizModel quizModel = new QuizModel();
        try {
            FileInputStream fileIn=activity.openFileInput("singlechoice" + tourID + ".txt");
            InputStreamReader InputRead= new InputStreamReader(fileIn);


            int READ_BLOCK_SIZE = 100;
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
            Log.d("LoadQuiz", jsonArray.toString());

            for (int j = 0; j < jsonArray.length(); j++) {
                Log.d("QuizLoad", "for");
                JSONObject mJsonLObjectQuiz = jsonArray.getJSONObject(j);

                if (mJsonLObjectQuiz.getInt("taskid") == taskID) {
                    Log.d("LoadQuiz", "inside if taskid");

                    quizModel.setTaskID(mJsonLObjectQuiz.getInt("taskid"));
                    if (!mJsonLObjectQuiz.isNull("stationid")) {
                        quizModel.setStationID(mJsonLObjectQuiz.getInt("stationid"));
                    }

                    if (!mJsonLObjectQuiz.isNull("tourid")) {
                        quizModel.setTourID(mJsonLObjectQuiz.getInt("tourid"));
                    }
                    quizModel.setScore(mJsonLObjectQuiz.getInt("score"));
                    quizModel.setQuestion(mJsonLObjectQuiz.getString("question"));
                    quizModel.setAnswerCorrect(mJsonLObjectQuiz.getString("answercorrect"));
                    quizModel.setAnswerWrong(mJsonLObjectQuiz.getString("answerwrong"));

                    //if (!mJsonLObjectQuiz.isNull("picturepath")) {
                    quizModel.setPicturePath(mJsonLObjectQuiz.getString("picturepath"));
                    //}
                    quizModel.setRightAnswer(mJsonLObjectQuiz.getString("rightanswer"));
                    quizModel.setOption2(mJsonLObjectQuiz.getString("option2"));
                    quizModel.setOption3(mJsonLObjectQuiz.getString("option3"));
                    quizModel.setOption4(mJsonLObjectQuiz.getString("option4"));

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return quizModel;

    }
}

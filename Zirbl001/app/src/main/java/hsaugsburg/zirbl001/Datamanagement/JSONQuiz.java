package hsaugsburg.zirbl001.Datamanagement;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import hsaugsburg.zirbl001.Models.QuizModel;
import hsaugsburg.zirbl001.TourActivities.QuizActivity;

public class JSONQuiz extends AsyncTask<String, String, QuizModel> {
    private QuizActivity activity;
    private int selectedTour;
    private int taskID;

    public JSONQuiz (QuizActivity activity, int selectedTour, int taskID) {
        this.activity = activity;
        this.selectedTour = selectedTour;
        this.taskID = taskID;
    }

    protected QuizModel doInBackground(String... params) {
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

                JSONArray mJsonArrayTourQuiz = parentObject.getJSONArray("toursinglechoice");

                QuizModel quizModel = new QuizModel();

                for (int i = 0; i < mJsonArrayTourQuiz.length(); i++) {
                    JSONObject mJsonLObjectTourQuiz = mJsonArrayTourQuiz.getJSONObject(i);
                    boolean equalsSelectedTour = false;

                    if (!mJsonLObjectTourQuiz.isNull("tourid")) {
                        if (mJsonLObjectTourQuiz.getInt("tourid") == selectedTour) {
                            equalsSelectedTour = true;
                        }
                    } else {
                        equalsSelectedTour = true;
                    }
                    if (equalsSelectedTour) {

                        JSONArray mJSONArrayQuiz = mJsonLObjectTourQuiz.getJSONArray("singlechoice");

                        for (int j = 0; j < mJSONArrayQuiz.length(); j++) {
                            JSONObject mJsonLObjectQuiz = mJSONArrayQuiz.getJSONObject(j);

                            if (mJsonLObjectQuiz.getInt("taskid") == taskID) {
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
                    }


                }


                return quizModel;
            } catch (JSONException e) {
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
    protected void onPostExecute(QuizModel result){
        super.onPostExecute(result);
        activity.processData(result);
    }

    private void setData() {

    }

}

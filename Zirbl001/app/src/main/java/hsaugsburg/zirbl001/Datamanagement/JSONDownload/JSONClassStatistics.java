package hsaugsburg.zirbl001.Datamanagement.JSONDownload;


import android.os.AsyncTask;

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
import java.util.Iterator;
import java.util.List;

import hsaugsburg.zirbl001.Models.NavigationModels.ClassStatisticsModel;
import hsaugsburg.zirbl001.Models.NavigationModels.ClassesStatModel;
import hsaugsburg.zirbl001.NavigationActivities.Profile.ProfileClassFragment;

public class JSONClassStatistics extends AsyncTask<String, String, List<ClassesStatModel>> {

    private ProfileClassFragment profileClassFragment;
    private String username;
    private String deviceToken;

    public JSONClassStatistics (ProfileClassFragment profileClassFragment, String username, String deviceToken) {
        this.profileClassFragment = profileClassFragment;
        this.username = username;
        this.deviceToken = deviceToken;
    }

    protected List<ClassesStatModel> doInBackground(String... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url;
            url = new URL(params[0] + "?username=" + username + "&devicetoken=" + deviceToken);
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
                ArrayList<ClassesStatModel> classesStatModelList = new ArrayList<>();


                for (int i = 0; i < parentArray.length(); i++) {
                    ClassesStatModel classesStatModel = new ClassesStatModel();
                    ArrayList<ClassStatisticsModel> classStatisticsModelList = new ArrayList<>();
                    JSONArray childArray = parentArray.getJSONArray(i);
                    ArrayList<ClassStatisticsModel> classStatistics = new ArrayList<>();
                    for (int j = 0; j < childArray.length(); j++) {

                        JSONObject mJsonLObjectClassStatistic = childArray.getJSONObject(j);

                        ClassStatisticsModel classStatisticsModel = new ClassStatisticsModel();

                        classStatisticsModel.setClassID(mJsonLObjectClassStatistic.getInt("classid"));
                        classStatisticsModel.setTeacherID(mJsonLObjectClassStatistic.getInt("teacherid"));
                        classStatisticsModel.setTourName(mJsonLObjectClassStatistic.getString("tourname"));
                        classStatisticsModel.setClassName(mJsonLObjectClassStatistic.getString("classname"));
                        classStatisticsModel.setSchoolName(mJsonLObjectClassStatistic.getString("schoolname"));
                        classStatisticsModel.setGroupName(mJsonLObjectClassStatistic.getString("groupname"));
                        classStatisticsModel.setDuration(mJsonLObjectClassStatistic.getInt("duration"));
                        classStatisticsModel.setParticipationDate(mJsonLObjectClassStatistic.getString("participationdate"));
                        classStatisticsModel.setScore(mJsonLObjectClassStatistic.getInt("score"));
                        classStatisticsModel.setClassRanking(mJsonLObjectClassStatistic.getInt("classranking"));

                        JSONObject participantsObject = mJsonLObjectClassStatistic.getJSONObject("participants");
                        ArrayList<String> participants = new ArrayList<>();

                        Iterator<String> iter = participantsObject.keys();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            try {
                                Object value = participantsObject.get(key);
                                participants.add(value.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        classStatisticsModel.setParticipants(participants);


                        classStatistics.add(classStatisticsModel);
                    }
                    classesStatModel.setClassStatisticsModels(classStatistics);
                    classesStatModelList.add(classesStatModel);
                }

                return classesStatModelList;

                } catch (JSONException e) {
                    e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
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
    protected void onPostExecute(List<ClassesStatModel> result){

        super.onPostExecute(result);
        profileClassFragment.processData(result);
    }

}

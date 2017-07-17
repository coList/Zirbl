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

import hsaugsburg.zirbl001.Models.NavigationModels.OwnStatisticsModel;
import hsaugsburg.zirbl001.NavigationActivities.Profile.ProfileOwnFragment;

public class JSONOwnStatistics extends AsyncTask<String, String, List<OwnStatisticsModel>> {

    private ProfileOwnFragment profileOwnFragment;
    private String username;
    public JSONOwnStatistics (ProfileOwnFragment profileOwnFragment, String username) {
        this.profileOwnFragment = profileOwnFragment;
        this.username = username;
    }

    protected List<OwnStatisticsModel> doInBackground(String... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url;
            url = new URL(params[0] + "?username=" + username);
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
                ArrayList<OwnStatisticsModel> ownStatisticsModelList = new ArrayList<>();


                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject mJsonLObjectOwnStatistic = parentArray.getJSONObject(i);

                    OwnStatisticsModel ownStatisticsModel = new OwnStatisticsModel();

                    ownStatisticsModel.setTourID(mJsonLObjectOwnStatistic.getInt("tourid"));
                    ownStatisticsModel.setTourName(mJsonLObjectOwnStatistic.getString("tourname"));
                    ownStatisticsModel.setGroupName(mJsonLObjectOwnStatistic.getString("groupname"));
                    ownStatisticsModel.setDuration(mJsonLObjectOwnStatistic.getInt("duration"));
                    ownStatisticsModel.setParticipationDate(mJsonLObjectOwnStatistic.getString("participationdate"));

                    ownStatisticsModel.setScore(mJsonLObjectOwnStatistic.getInt("score"));
                    ownStatisticsModel.setRank(mJsonLObjectOwnStatistic.getInt("worldranking"));
                    ownStatisticsModel.setTotalParticipations(mJsonLObjectOwnStatistic.getInt("totalparticipations"));


                    JSONObject participantsObject = mJsonLObjectOwnStatistic.getJSONObject("participants");
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

                    ownStatisticsModel.setParticipants(participants);



                    ownStatisticsModelList.add(ownStatisticsModel);

                }


                return ownStatisticsModelList;

            //} catch (JSONException e) {
           //     e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.toString();
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
    protected void onPostExecute(List<OwnStatisticsModel> result){

        super.onPostExecute(result);
        profileOwnFragment.processData(result);
    }

}

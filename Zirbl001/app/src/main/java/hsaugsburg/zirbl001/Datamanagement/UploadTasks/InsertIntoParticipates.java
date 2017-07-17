package hsaugsburg.zirbl001.Datamanagement.UploadTasks;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import hsaugsburg.zirbl001.TourActivities.ResultActivity;

public class InsertIntoParticipates extends AsyncTask<String, Void, String> {
    private String serverName;
    private String userName;
    private int tourID;
    private int classID;
    private String teamname;
    private int score;
    private int duration;
    private ArrayList<String> participants;
    private ResultActivity resultActivity;

    public InsertIntoParticipates(ResultActivity resultActivity, String userName, int tourID, int classID, String teamname, int score, int duration, ArrayList<String> participants, String serverName) {
        this.resultActivity = resultActivity;
        this.userName = userName;
        this.tourID = tourID;
        this.classID = classID;
        this.teamname = teamname;
        this.score = score;
        this.duration = duration;
        this.participants = participants;
        this.serverName = serverName;
    }

    protected void onPreExecute() {
    }

    protected String doInBackground(String... arg0) {

        try {

            URL url = new URL(serverName + "/api/insertIntoParticipates.php");

            JSONObject postDataParams = new JSONObject();
            postDataParams.put("username", userName);
            postDataParams.put("tourid", tourID);
            if (classID > 0) {
                postDataParams.put("classid", classID);  //nicht setzen, wenn keine Klasse vorhanden!
            } 
            postDataParams.put("groupname", teamname);
            postDataParams.put("score", score);
            postDataParams.put("duration", duration);

            JSONObject JSONParticipants= new JSONObject();
            for (int i = 0; i < participants.size(); i++) {
                JSONParticipants.put("participant" + i, participants.get(i));
            }
            postDataParams.put("participants", JSONParticipants); //JSON objekt?

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(new
                        InputStreamReader(
                        conn.getInputStream()));

                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {

                    sb.append(line);
                    break;
                }

                in.close();
                return sb.toString();

            } else {
                return new String("false : " + responseCode);
            }
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }

    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("InsertIntoParticipates", result);

        try {

            JSONObject json = new JSONObject(result);
            resultActivity.setRanking(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while (itr.hasNext()) {

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}
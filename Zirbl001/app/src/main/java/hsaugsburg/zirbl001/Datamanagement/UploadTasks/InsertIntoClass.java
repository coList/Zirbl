package hsaugsburg.zirbl001.Datamanagement.UploadTasks;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import hsaugsburg.zirbl001.NavigationActivities.QrCode.QrDialog;
import hsaugsburg.zirbl001.TourActivities.GenerateQrCodeActivity;

public class InsertIntoClass extends AsyncTask<String, Void, String> {
    private String userName;
    private int tourID;
    private String className;
    private String schoolName;
    private String qrCode;
    private String serverName;
    private GenerateQrCodeActivity activity;

    public InsertIntoClass(String userName, int tourID, String className, String schoolName, String qrCode, String serverName, GenerateQrCodeActivity activity) {
        this.userName = userName;
        this.tourID = tourID;
        this.className = className;
        this.schoolName = schoolName;
        this.qrCode = qrCode;
        this.serverName = serverName;
        this.activity = activity;
    }

    protected void onPreExecute() {
    }

    protected String doInBackground(String... arg0) {

        try {

            URL url = new URL(serverName + "/api/insertIntoClass.php");

            JSONObject postDataParams = new JSONObject();
            postDataParams.put("username", userName);
            postDataParams.put("tourid", tourID);
            postDataParams.put("classname", className);
            postDataParams.put("schoolname", schoolName);
            postDataParams.put("qrcode", qrCode);

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
        Log.d("InsertIntoClass", result);
        activity.setQrCode(result);
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
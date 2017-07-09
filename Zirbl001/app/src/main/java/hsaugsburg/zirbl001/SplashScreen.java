package hsaugsburg.zirbl001;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import hsaugsburg.zirbl001.NavigationActivities.HomeActivity;

import static android.R.attr.name;
import static hsaugsburg.zirbl001.R.id.add;

public class SplashScreen extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    public static final String GLOBAL_VALUES = "globalValuesFile";
    private String serverName = "https://zirbl.de";
    private String userName;
    private String deviceToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferences settings = getSharedPreferences(GLOBAL_VALUES, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("serverName", serverName);

        if (settings.getBoolean("firstTime", true)) {
            //the app is being launched for first time, do something
            final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
            userName = createRandomString(50);
            Log.d("SplashScreenUsername", userName);
            deviceToken = createRandomString(150);
            Log.d("SplashScreenDeviceToken", deviceToken);

            editor.putString("userName", userName);
            editor.putString("deviceToken", deviceToken);
            new SendPostRequest().execute();
            //set userName and deviceToken as sharedPreferences


            // record the fact that the app has been started at least once
            editor.putBoolean("firstTime", false);
        } else {
            Log.d("SplashScreen", "notTheFirstTime!");
        }


        editor.commit();


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, HomeActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }

        }, SPLASH_TIME_OUT);
    }


    public String createRandomString(int length) {
        Random r = new Random();
        char[] choices = ("abcdefghijklmnopqrstuvwxyz" +
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "0123456789" +
                "_#$").toCharArray();

        StringBuilder salt = new StringBuilder(length);
        for (int i = 0; i < length; ++i)
            salt.append(choices[r.nextInt(choices.length)]);
        return salt.toString();

    }

    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL(serverName + "/api/insertIntoUser.php");

                JSONObject postDataParams = new JSONObject();
                Log.d("SplashAsynkUserName", userName);
                Log.d("SplashAsynkDevice", deviceToken);
                postDataParams.put("username", userName);
                postDataParams.put("devicetoken", deviceToken);

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

                int responseCode=conn.getResponseCode();
                Log.d("SplashResponseCode", Integer.toString(responseCode));

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result,
                    Toast.LENGTH_LONG).show();
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
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

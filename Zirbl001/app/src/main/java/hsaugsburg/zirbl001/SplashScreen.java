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

import hsaugsburg.zirbl001.Datamanagement.UploadTasks.InsertIntoUser;
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
            deviceToken = createRandomString(150);

            editor.putString("userName", userName);
            editor.putString("deviceToken", deviceToken);
            new InsertIntoUser(userName, deviceToken, serverName).execute();
            //set userName and deviceToken as sharedPreferences


            // record the fact that the app has been started at least once
            editor.putBoolean("firstTime", false);
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



}

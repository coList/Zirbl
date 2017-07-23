package hsaugsburg.zirbl001;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Random;

import hsaugsburg.zirbl001.Datamanagement.UploadTasks.InsertIntoUser;
import hsaugsburg.zirbl001.Interfaces.InternetActivity;
import hsaugsburg.zirbl001.NavigationActivities.HomeActivity;
import hsaugsburg.zirbl001.NavigationActivities.NoConnectionDialog;
import hsaugsburg.zirbl001.TourActivities.TourstartActivity;

public class SplashScreen extends AppCompatActivity implements InternetActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    public static final String GLOBAL_VALUES = "globalValuesFile";
    private String serverName = "https://zirbl.de";
    private String userName;
    private String deviceToken;
    private boolean hasUsername;

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

            if (!isOnline()) {
                NoConnectionDialog noConnectionDialog = new NoConnectionDialog(this);
                noConnectionDialog.showDialog(this);
            } else {
                new InsertIntoUser(this, userName, deviceToken, serverName).execute();
                // record the fact that the app has been started at least once
                editor.putBoolean("firstTime", false);
            }
            //set userName and deviceToken as sharedPreferences


        } else {
            hasUsername = true;
        }


        editor.commit();

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            public void run() {

                if (hasUsername) {

                    Intent i = new Intent(SplashScreen.this, HomeActivity.class);
                    startActivity(i);

                    // close this activity
                    finish();
                } else {
                    handler.postDelayed(this, 100);
                }
            }
        };
        handler.postDelayed(runnable, SPLASH_TIME_OUT);
    }

    public void setHasUsername(boolean hasUsername) {
        this.hasUsername = hasUsername;
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void tryConnectionAgain() {
        if (!isOnline()) {
            NoConnectionDialog noConnectionDialog = new NoConnectionDialog(this);
            noConnectionDialog.showDialog(this);
        } else {
            new InsertIntoUser(this, userName, deviceToken, serverName).execute();
            // record the fact that the app has been started at least once


            SharedPreferences settings = getSharedPreferences(GLOBAL_VALUES, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("firstTime", false);
            editor.commit();
        }
    }

    public String createRandomString(int length) {
        Random r = new Random();


        char[] choices = ("abcdefghijklmnopqrstuvwxyz" +
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "0123456789").toCharArray();

        /*
        char[] choices = ("abcdefghijklmnopqrstuvwxyz" +
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "0123456789" +
                "_#$").toCharArray();
                */


        StringBuilder salt = new StringBuilder(length);
        for (int i = 0; i < length; ++i)
            salt.append(choices[r.nextInt(choices.length)]);
        return salt.toString();

    }



}

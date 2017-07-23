package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import hsaugsburg.zirbl001.Datamanagement.UploadTasks.InsertIntoParticipates;
import hsaugsburg.zirbl001.Interfaces.InternetActivity;
import hsaugsburg.zirbl001.NavigationActivities.HomeActivity;
import hsaugsburg.zirbl001.NavigationActivities.NoConnectionDialog;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.Utils.ObjectSerializer;
import hsaugsburg.zirbl001.Utils.TopDarkActionbar;

public class ResultActivity extends AppCompatActivity implements InternetActivity {
    private Context mContext = ResultActivity.this;
    private static final String TAG = "ResultActivity";
    private int selectedTour;

    public static final String TOUR_VALUES = "tourValuesFile";

    public static final String GLOBAL_VALUES = "globalValuesFile";

    //dot menu
    private TopDarkActionbar topDarkActionbar;
    private int currentScore;
    private long startTime;

    private long totalTime;
    private String serverName;
    private String userName;
    private String deviceToken;
    private int classID;
    private String teamName;
    private ArrayList<String> participants;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //get global tour values
        SharedPreferences tourValues = getSharedPreferences(TOUR_VALUES, 0);
        selectedTour = Integer.parseInt(tourValues.getString("tourID", null));
        currentScore = Integer.parseInt(tourValues.getString("currentScore", null));
        int totalChronologyValue = Integer.parseInt(tourValues.getString("totalChronology", null));
        classID = Integer.parseInt(tourValues.getString("classID", null));

        teamName = tourValues.getString("teamName", null);
        participants = new ArrayList<>();
        try {
            participants = (ArrayList<String>) ObjectSerializer.deserialize(tourValues.getString("participants", ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        startTime = Long.parseLong(tourValues.getString("startTime", null));
        totalTime = System.currentTimeMillis() - startTime;

        String time = String.format("%d h %d min",
                TimeUnit.MILLISECONDS.toHours(totalTime),
                TimeUnit.MILLISECONDS.toMinutes(totalTime) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totalTime))
        );

        String teamViewStart = "<b>Team: </b>";
        TextView teamNameView = (TextView)findViewById(R.id.resultTeam);
        teamNameView.setText(fromHtml(teamViewStart + teamName));

        String participantsViewText = "<b>Teilnehmer: </b>";
        TextView participantsView = (TextView)findViewById(R.id.resultMembers);
        for (String participant: participants) {
            participantsViewText += participant + ", ";
        }
        participantsViewText = participantsViewText.substring(0, participantsViewText.length() - 2);
        participantsView.setText(fromHtml(participantsViewText));

        TextView durationView = (TextView) findViewById(R.id.textUsedTime);
        durationView.setText(time);

        String titleText = "Ergebnis";
        //dot menu
        topDarkActionbar = new TopDarkActionbar(this, titleText);

        TextView totalScore = (TextView) findViewById(R.id.endPoints);
        totalScore.setText(Integer.toString(currentScore));

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(totalChronologyValue + 1);
        progressBar.setProgress(totalChronologyValue + 1);

        SharedPreferences globalValues = getSharedPreferences(GLOBAL_VALUES, 0);
        serverName = globalValues.getString("serverName", null);
        userName = globalValues.getString("userName", null);
        deviceToken = globalValues.getString("deviceToken", null);

        if (!isOnline()) {
            NoConnectionDialog noConnectionDialog = new NoConnectionDialog(this);
            noConnectionDialog.showDialog(this);
        } else {
            new InsertIntoParticipates(this, userName, deviceToken, selectedTour, classID, teamName, currentScore, (int)totalTime, participants, serverName).execute();
            deleteFiles();
        }
    }

    public void tryConnectionAgain() {
        if (!isOnline()) {
            NoConnectionDialog noConnectionDialog = new NoConnectionDialog(this);
            noConnectionDialog.showDialog(this);
        } else {
            new InsertIntoParticipates(this, userName, deviceToken, selectedTour, classID, teamName, currentScore, (int)totalTime, participants, serverName).execute();
            deleteFiles();
        }
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void setRanking(JSONObject result) {
        try {
            TextView ranking = (TextView) findViewById(R.id.textPlacement);
            String rankingText = result.getInt("worldranking") + ". Platz von " + result.getInt("totalparticipations");
            ranking.setText(rankingText);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void deleteFiles() {
        File dir = getFilesDir();
        ArrayList<File> files = new ArrayList<>();
        files.add(new File(dir, "infopopups" + selectedTour + ".txt"));
        files.add(new File(dir, "letters" + selectedTour + ".txt"));
        files.add(new File(dir, "singlechoice" + selectedTour + ".txt"));
        files.add(new File(dir, "guessthenumber" + selectedTour + ".txt"));
        files.add(new File(dir, "stations" + selectedTour + ".txt"));
        files.add(new File(dir, "truefalse" + selectedTour + ".txt"));
        files.add(new File(dir, "chronology" + selectedTour + ".txt"));
        files.add(new File(dir, "nuts" + selectedTour + ".txt"));
        files.add(new File(dir, "nuts" + selectedTour + ".txt"));

        for (File file: files) {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public void endTour(View view) {
        Intent intent = new Intent(mContext, HomeActivity.class);
        mContext.startActivity(intent);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void showEndTourDialog(){
        this.runOnUiThread(new Runnable() {
            public void run() {
                EndTourDialog alertEnd = new EndTourDialog(mContext, selectedTour);
                alertEnd.showDialog((Activity) mContext);
            }
        });
    }

    public void showMenu(View view){
        topDarkActionbar.showMenu();
    }

    public void showStats(View view){
        topDarkActionbar.showStats(currentScore, startTime);
    }
    public void quitTour(View view){
        showEndTourDialog();
    }


    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

}

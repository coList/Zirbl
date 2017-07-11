package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import hsaugsburg.zirbl001.Datamanagement.UploadTasks.InsertIntoParticipates;
import hsaugsburg.zirbl001.NavigationActivities.HomeActivity;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.Utils.ObjectSerializer;

public class ResultActivity extends AppCompatActivity {
    private int currentScore;
    private Context mContext = ResultActivity.this;
    private static final String TAG = "ResultActivity";
    private int selectedTour;
    private int classID;

    public static final String TOUR_VALUES = "tourValuesFile";
    private int totalChronologyValue;

    public static final String GLOBAL_VALUES = "globalValuesFile";
    private String serverName;

    //dot menu
    private TextView title;
    private RelativeLayout dotMenuLayout;
    private boolean dotMenuOpen = false;

    private String team;
    private String members;

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
        totalChronologyValue = Integer.parseInt(tourValues.getString("totalChronology", null));
        classID = Integer.parseInt(tourValues.getString("classID", null));

        String teamName = tourValues.getString("teamName", null);
        ArrayList<String> participants = new ArrayList<>();
        try {
            participants = (ArrayList<String>) ObjectSerializer.deserialize(tourValues.getString("participants", ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        long startTime = Long.parseLong(tourValues.getString("startTime", null));
        long totalTime = System.currentTimeMillis() - startTime;

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
        title = (TextView) findViewById(R.id.titleActionbar);
        title.setText(titleText);
        dotMenuLayout=(RelativeLayout) this.findViewById(R.id.dotMenu);
        dotMenuLayout.setVisibility(RelativeLayout.GONE);


        TextView totalScore = (TextView) findViewById(R.id.endPoints);
        totalScore.setText(Integer.toString(currentScore));

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(totalChronologyValue + 1);
        progressBar.setProgress(totalChronologyValue + 1);

        SharedPreferences globalValues = getSharedPreferences(GLOBAL_VALUES, 0);
        serverName = globalValues.getString("serverName", null);
        String userName = globalValues.getString("userName", null);

        new InsertIntoParticipates(userName, selectedTour, classID, teamName, currentScore, (int)totalTime, participants, serverName).execute();


    }

    public void endTour(View view) {
        Intent intent = new Intent(mContext, HomeActivity.class);
        mContext.startActivity(intent);
    }


    private void showEndTourDialog(){
        this.runOnUiThread(new Runnable() {
            public void run() {
                EndTourDialog alertEnd = new EndTourDialog(mContext);
                alertEnd.showDialog((Activity) mContext);
            }
        });
    }

    public void showMenu(View view){

        ImageView dotIcon = (ImageView) findViewById(R.id.dotIcon);
        TextView menuStats = (TextView) findViewById(R.id.menuStats);
        TextView menuQuit = (TextView) findViewById(R.id.menuQuit);

        if(dotMenuOpen){
            dotMenuLayout.setVisibility(RelativeLayout.GONE);
            dotMenuOpen = false;
            title.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            dotIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent));
        } else {
            dotMenuLayout.setVisibility(RelativeLayout.VISIBLE);
            dotMenuOpen = true;
            title.setTextColor(ContextCompat.getColor(mContext, R.color.colorTurquoise));
            dotIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.colorTurquoise));
            menuQuit.setTextSize(18);
            menuStats.setTextSize(18);
        }
    }

    public void showStats(View view){
        Log.d(TAG, "showStats: Stats");
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

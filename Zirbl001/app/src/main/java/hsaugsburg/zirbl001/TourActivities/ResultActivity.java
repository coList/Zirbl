package hsaugsburg.zirbl001.TourActivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

import hsaugsburg.zirbl001.NavigationActivities.HomeActivity;
import hsaugsburg.zirbl001.R;

public class ResultActivity extends AppCompatActivity {
    private int currentScore;
    private Context context;


    public static final String TOUR_VALUES = "tourValuesFile";
    private int totalChronologyValue;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = ResultActivity.this;
        setContentView(R.layout.activity_result);

        currentScore = Integer.valueOf(getIntent().getStringExtra("currentscore"));

        TextView totalScore = (TextView) findViewById(R.id.endPoints);
        totalScore.setText(Integer.toString(currentScore));

        //get global tour values
        SharedPreferences tourValues = getSharedPreferences(TOUR_VALUES, 0);
        totalChronologyValue = Integer.parseInt(tourValues.getString("totalChronology", null));

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(totalChronologyValue + 1);
        progressBar.setProgress(totalChronologyValue + 1);

    }

    public void endTour(View view) {
        File file = new File("chronology.txt");
        file.delete();
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
    }
}

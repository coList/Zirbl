package hsaugsburg.zirbl001.TourActivities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import hsaugsburg.zirbl001.NavigationActivities.HomeActivity;
import hsaugsburg.zirbl001.R;

public class ResultActivity extends AppCompatActivity {
    private int currentScore;
    private Context context;

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
    }

    public void endTour(View view) {
        File file = new File("chronology.txt");
        file.delete();
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
    }
}

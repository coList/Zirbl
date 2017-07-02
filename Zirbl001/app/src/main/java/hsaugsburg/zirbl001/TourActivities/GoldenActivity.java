package hsaugsburg.zirbl001.TourActivities;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import hsaugsburg.zirbl001.R;

public class GoldenActivity extends AppCompatActivity {


    private int currentScore;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_golden);

        TextView goldenPoints = (TextView) findViewById(R.id.goldenPoints);
        goldenPoints.setTextColor(ContextCompat.getColor(this, R.color.colorGold));
        goldenPoints.setText(getIntent().getStringExtra("score"));

        TextView text = (TextView) findViewById(R.id.answerText);
        text.setText(getIntent().getStringExtra("foundText"));


        //currentScore = Integer.parseInt(getIntent().getStringExtra("currentscore"));
        //currentScore += Integer.parseInt(getIntent().getStringExtra("score"));

    }

    public void backToNavigation(View view) {

        this.finish();
    }
}

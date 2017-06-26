package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import hsaugsburg.zirbl001.Datamanagement.TourChronologyTask;
import hsaugsburg.zirbl001.Models.ChronologyModel;
import hsaugsburg.zirbl001.NavigationActivities.TourDetailActivity;
import hsaugsburg.zirbl001.R;

public class NavigationActivity extends AppCompatActivity {

    private Context mContext = NavigationActivity.this;

    private int chronologyNumber;
    private int currentScore;
    private ChronologyModel nextChronologyItem = new ChronologyModel();

    private TourChronologyTask tourChronologyTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        chronologyNumber = Integer.parseInt(getIntent().getStringExtra("chronologyNumber"));
        currentScore = Integer.parseInt(getIntent().getStringExtra("currentscore"));
        tourChronologyTask = new TourChronologyTask(this, nextChronologyItem, chronologyNumber, currentScore);
        tourChronologyTask.readChronologyFile();
    }

    public void layoutQuiz(View view) {
        Intent intent = new Intent(mContext, QuizActivity.class);
        startActivity(intent);
    }

    public void layoutSlider(View view) {
        Intent intent = new Intent(mContext, SliderActivity.class);
        startActivity(intent);
    }

    public void layoutTrueFalse(View view) {
        Intent intent = new Intent(mContext, TrueFalseActivity.class);
        startActivity(intent);
    }

    public void layoutLetters(View view) {
        Intent intent = new Intent(mContext, LettersActivity.class);
        startActivity(intent);
    }

    public void layoutDoUKnow(View view) {
        Intent intent = new Intent(mContext, DoUKnowActivity.class);
        startActivity(intent);
    }

    public void continueToNextView(View view) {
        tourChronologyTask.continueToNextView();
    }

    private void showEndTourDialog(){
        this.runOnUiThread(new Runnable() {
            public void run() {
                EndTourDialog alertEnd = new EndTourDialog(mContext);
                alertEnd.showDialog((Activity) mContext);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            showEndTourDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

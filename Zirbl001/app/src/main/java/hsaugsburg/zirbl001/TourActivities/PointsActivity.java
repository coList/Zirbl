package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import hsaugsburg.zirbl001.Datamanagement.TourChronologyTask;
import hsaugsburg.zirbl001.Models.ChronologyModel;
import hsaugsburg.zirbl001.R;

public class PointsActivity extends AppCompatActivity {
    private Context mContext = PointsActivity.this;


    private int chronologyNumber;
    private int currentScore;
    private ChronologyModel nextChronologyItem = new ChronologyModel();

    private TourChronologyTask tourChronologyTask;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);


        chronologyNumber = Integer.parseInt(getIntent().getStringExtra("chronologyNumber"));
        currentScore = Integer.parseInt(getIntent().getStringExtra("currentscore"));

        String solution = getIntent().getStringExtra("solution");
        String userAnswer = getIntent().getStringExtra("userAnswer");
        String answerCorrect = getIntent().getStringExtra("answerCorrect");
        String answerWrong = getIntent().getStringExtra("answerWrong");
        int score = Integer.parseInt(getIntent().getStringExtra("score"));

        TextView answerText = (TextView)findViewById(R.id.answerText);
        ImageView answerImage = (ImageView)findViewById(R.id.pointsImage);
        TextView scoreText = (TextView) findViewById(R.id.points);

        String correct = "RICHTIG";
        String wrong = "FALSCH";
        Toolbar toolbar = (Toolbar) findViewById(R.id.standard_toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().getStringExtra("isSlider").equals("true")) {  //was the task a slider-Task?
            double userInput = Double.valueOf(userAnswer);
            double rightAnswer = Double.valueOf(solution);
            Log.d("PointsActivity", userAnswer);
            Log.d("PointsActivity", solution);

            if (userInput >= rightAnswer - 0.02 * rightAnswer &&
                    userInput <= rightAnswer + 0.02 * rightAnswer) {
                answerText.setText(fromHtml(answerCorrect));
                answerImage.setImageResource(R.drawable.img_right);
                currentScore += score;
                scoreText.setText(Integer.toString(score));
                getSupportActionBar().setTitle(correct);
            } else {
                answerText.setText(fromHtml(answerWrong));
                answerImage.setImageResource(R.drawable.img_wrong);
                getSupportActionBar().setTitle(wrong);
            }
        } else { //if not:
            if (userAnswer.toUpperCase().equals(solution.toUpperCase())) {
                answerText.setText(fromHtml(answerCorrect));
                answerImage.setImageResource(R.drawable.img_right);
                currentScore += score;
                scoreText.setText(Integer.toString(score));
                getSupportActionBar().setTitle(correct);

            } else {
                answerText.setText(fromHtml(answerWrong));
                answerImage.setImageResource(R.drawable.img_wrong);
                getSupportActionBar().setTitle(wrong);
            }
        }


        tourChronologyTask = new TourChronologyTask(this, nextChronologyItem, chronologyNumber, currentScore);

        tourChronologyTask.readChronologyFile();

    }

    public void continueToNextView(View view) {
        tourChronologyTask.continueToNextView();
    }

    public void backToNavigationActivity(View view) {
        Intent intent = new Intent(mContext, NavigationActivity.class);
        startActivity(intent);
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

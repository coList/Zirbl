package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import hsaugsburg.zirbl001.Datamanagement.JSONLetters;
import hsaugsburg.zirbl001.Datamanagement.JSONTrueFalse;
import hsaugsburg.zirbl001.Models.TrueFalseModel;
import hsaugsburg.zirbl001.R;

public class TrueFalseActivity extends AppCompatActivity {

    private Context mContext = TrueFalseActivity.this;

    private boolean answerSelected = false;
    private boolean trueSelected;
    private String rightAnswer;
    private String answerCorrect;
    private String answerWrong;
    private int score;

    private int chronologyNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_true_false);

        chronologyNumber = Integer.parseInt(getIntent().getStringExtra("chronologyNumber"));
        int taskID = Integer.parseInt(getIntent().getStringExtra("taskid"));
        new JSONTrueFalse(this, taskID).execute("https://zirbl.multimedia.hs-augsburg.de/selectTrueFalseView.php");

        //Selection
        Button buttonTruth = (Button) findViewById(R.id.truth);
        buttonTruth.setOnClickListener(answerTruth);
        Button buttonLie = (Button) findViewById(R.id.lie);
        buttonLie.setOnClickListener(answerLie);
        //
    }

    public void continueToNextView(View view) {
        if (answerSelected) {
            String userAnswer;
            if (trueSelected == true) {
                userAnswer = "true";
            } else {
                userAnswer = "false";
            }

            Intent intent = new Intent(mContext, PointsActivity.class);
            intent.putExtra("isSlider", "false");
            intent.putExtra("userAnswer", userAnswer);
            intent.putExtra("solution", rightAnswer);
            intent.putExtra("answerCorrect", answerCorrect);
            intent.putExtra("answerWrong", answerWrong);
            intent.putExtra("score", Integer.toString(score));
            intent.putExtra("chronologyNumber", Integer.toString(chronologyNumber));
            startActivity(intent);
        } else {
            Animation shake = AnimationUtils.loadAnimation(TrueFalseActivity.this, R.anim.shake);
            findViewById(R.id.continueButton).startAnimation(shake);
        }

    }

    //Selection
    View.OnClickListener answerTruth = new View.OnClickListener() {
        public void onClick(View v) {
            answerSelected = true;
            trueSelected = true;
            RelativeLayout selected = (RelativeLayout) findViewById(R.id.truthArea);
            selected.setBackgroundResource(R.color.colorTurquoise);

            RelativeLayout nonSelected = (RelativeLayout) findViewById(R.id.lieArea);
            nonSelected.setBackgroundResource(0);

            Button nonSelectedButton = (Button) findViewById(R.id.lie);

            int colorId = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
            nonSelectedButton.setTextColor(colorId);

            ImageView nonSelectedImageView = (ImageView) findViewById(R.id.iconLie);
            nonSelectedImageView.setImageResource(R.drawable.ic_lie_normal);

            ImageView invertedImg = (ImageView) findViewById(R.id.iconTruth);

            invertedImg.setImageResource(R.drawable.ic_truth_active);
            Button btA = (Button) findViewById(R.id.truth);
            btA.setTextColor(Color.WHITE);
        }
    };
    View.OnClickListener answerLie = new View.OnClickListener() {
        public void onClick(View v) {
            answerSelected = true;
            trueSelected = false;
            RelativeLayout selected = (RelativeLayout) findViewById(R.id.lieArea);
            selected.setBackgroundResource(R.color.colorRed);

            RelativeLayout nonSelected = (RelativeLayout) findViewById(R.id.truthArea);
            nonSelected.setBackgroundResource(0);

            Button nonSelectedButton = (Button) findViewById(R.id.truth);
            int colorId = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
            nonSelectedButton.setTextColor(colorId);

            ImageView nonSelectedImageView = (ImageView) findViewById(R.id.iconTruth);
            nonSelectedImageView.setImageResource(R.drawable.ic_truth_normal);

            ImageView invertedImg = (ImageView) findViewById(R.id.iconLie);

            invertedImg.setImageResource(R.drawable.ic_lie_active);
            Button btA = (Button) findViewById(R.id.lie);
            btA.setTextColor(Color.WHITE);
        }
    };
    //

    public void processData(TrueFalseModel result) {
        TextView question = (TextView) findViewById(R.id.questionText);
        question.setText(result.getQuestion());

        rightAnswer = String.valueOf(result.isTrue());
        answerCorrect = result.getAnswerCorrect();
        answerWrong = result.getAnswerWrong();
        score = result.getScore();

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

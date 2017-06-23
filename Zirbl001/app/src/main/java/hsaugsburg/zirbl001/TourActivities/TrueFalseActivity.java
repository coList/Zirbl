package hsaugsburg.zirbl001.TourActivities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    private boolean trueSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new JSONTrueFalse(this).execute("https://zirbl.multimedia.hs-augsburg.de/selectTrueFalseView.php");
        Log.d("TrueFalse", "onCreate");
        setContentView(R.layout.activity_true_false);

        //Selection
        Button buttonTruth = (Button) findViewById(R.id.truth);
        buttonTruth.setOnClickListener(answerTruth);
        Button buttonLie = (Button) findViewById(R.id.lie);
        buttonLie.setOnClickListener(answerLie);
        //
    }

    //Selection
    View.OnClickListener answerTruth = new View.OnClickListener() {
        public void onClick(View v) {
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

    public void continueToNextView(View view) {
        Intent intent = new Intent(mContext, NavigationActivity.class);
        startActivity(intent);
    }

    public void processData(TrueFalseModel result) {
        TextView question = (TextView) findViewById(R.id.questionText);
        Log.d("TrueFalseActivity", result.getQuestion());
        question.setText(result.getQuestion());

        final String rightAnswer = String.valueOf(result.isTrue());
        final String answerCorrect = result.getAnswerCorrect();
        final String answerWrong = result.getAnswerWrong();

        ImageButton continueButton = (ImageButton) findViewById(R.id.continueButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                startActivity(intent);

            }
        });

    }
}

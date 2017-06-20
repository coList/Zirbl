package hsaugsburg.zirbl001.TourActivities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import hsaugsburg.zirbl001.Datamanagement.JSONLetters;
import hsaugsburg.zirbl001.Datamanagement.JSONTrueFalse;
import hsaugsburg.zirbl001.Models.TrueFalseModel;
import hsaugsburg.zirbl001.R;

public class TrueFalseActivity extends AppCompatActivity {

    private Context mContext = TrueFalseActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new JSONTrueFalse(this).execute("https://zirbl.multimedia.hs-augsburg.de/selectTrueFalseView.php");
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
            RelativeLayout selected = (RelativeLayout)findViewById(R.id.truthArea);
            selected.setBackgroundResource(R.color.colorTruth);
            ImageView invertedImg = (ImageView)findViewById(R.id.iconTruth);
            invertedImg.setImageResource(R.drawable.icon_truth_active);
            Button btA = (Button) findViewById(R.id.truth);
            btA.setTextColor(Color.WHITE);
        }
    };
    View.OnClickListener answerLie = new View.OnClickListener() {
        public void onClick(View v) {
            RelativeLayout selected = (RelativeLayout)findViewById(R.id.lieArea);
            selected.setBackgroundResource(R.color.colorLie);
            ImageView invertedImg = (ImageView)findViewById(R.id.iconLie);
            invertedImg.setImageResource(R.drawable.icon_lie_active);
            Button btA = (Button) findViewById(R.id.lie);
            btA.setTextColor(Color.WHITE);
        }
    };
    //

    public void continueToNextView(View view) {
        Intent intent = new Intent(mContext, NavigationActivity.class);
        startActivity(intent);
    }

    public void processData (TrueFalseModel result) {
        TextView question = (TextView) findViewById(R.id.questionText);
        question.setText(result.getQuestion());
    }
}

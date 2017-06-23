package hsaugsburg.zirbl001.TourActivities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import hsaugsburg.zirbl001.R;

public class PointsActivity extends AppCompatActivity {
    private Context mContext = PointsActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);

        String solution = getIntent().getStringExtra("solution");
        String userAnswer = getIntent().getStringExtra("userAnswer");
        String answerCorrect = getIntent().getStringExtra("answerCorrect");
        String answerWrong = getIntent().getStringExtra("answerWrong");

        TextView answerText = (TextView)findViewById(R.id.answerText);
        ImageView answerImage = (ImageView)findViewById(R.id.pointsImage);

        if (getIntent().getStringExtra("isSlider").equals("true")) {  //was the task a slider-Task?
            double userInput = Double.valueOf(userAnswer);
            double rightAnswer = Double.valueOf(solution);

            if (userInput >= rightAnswer - 0.02 * rightAnswer &&
                    userInput <= rightAnswer + 0.02 * rightAnswer) {
                answerText.setText(answerCorrect);
                answerImage.setImageResource(R.drawable.img_right);
            } else {
                answerText.setText(answerWrong);
                answerImage.setImageResource(R.drawable.img_golden);
            }
        } else { //if not:
            if (userAnswer.toUpperCase().equals(solution.toUpperCase())) {
                answerText.setText(answerCorrect);
                answerImage.setImageResource(R.drawable.img_right);
            } else {
                answerText.setText(answerWrong);
                answerImage.setImageResource(R.drawable.img_golden);
            }
        }

    }

    public void backToNavigationActivity(View view) {
        Intent intent = new Intent(mContext, NavigationActivity.class);
        startActivity(intent);
    }
}

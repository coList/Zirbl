package hsaugsburg.zirbl001.TourActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import hsaugsburg.zirbl001.R;

public class PointsActivity extends AppCompatActivity {

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
        Log.d("PointsActivity", solution);
        Log.d("PointsActivity", userAnswer);
        if (userAnswer.equals(solution)) {
            answerText.setText(answerCorrect);
            answerImage.setImageResource(R.drawable.right);
        } else {
            answerText.setText(answerWrong);
            answerImage.setImageResource(R.drawable.wrong);
        }

    }
}

package hsaugsburg.zirbl001.TourActivities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import hsaugsburg.zirbl001.NavigationActivities.TourDetailActivity;
import hsaugsburg.zirbl001.R;

public class NavigationActivity extends AppCompatActivity {

    private Context mContext = NavigationActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
    }

    /*public void testbuttonCard(View view) {
        Intent intent = new Intent(mContext, "".class);
        startActivity(intent);
    }*/

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
}

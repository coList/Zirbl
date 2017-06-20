package hsaugsburg.zirbl001.TourActivities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
        Log.d("TrueFalse", "onCreate");
        setContentView(R.layout.activity_true_false);
    }

    public void continueToNextView(View view) {
        Intent intent = new Intent(mContext, NavigationActivity.class);
        startActivity(intent);
    }

    public void processData (TrueFalseModel result) {
        TextView question = (TextView) findViewById(R.id.questionText);
        Log.d("TrueFalseActivity", result.getQuestion());
        question.setText(result.getQuestion());
    }
}

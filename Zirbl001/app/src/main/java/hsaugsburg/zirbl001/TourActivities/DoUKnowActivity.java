package hsaugsburg.zirbl001.TourActivities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import hsaugsburg.zirbl001.Datamanagement.JSONDoUKnow;
import hsaugsburg.zirbl001.Models.DoUKnowModel;
import hsaugsburg.zirbl001.R;

public class DoUKnowActivity extends AppCompatActivity {
    private Context mContext = DoUKnowActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new JSONDoUKnow(this).execute("https://zirbl.multimedia.hs-augsburg.de/selectInfoPopupView.php");
        setContentView(R.layout.activity_do_uknow);
    }


    public void continueToNextView(View view) {
        Intent intent  = new Intent(mContext, NavigationActivity.class);
        startActivity(intent);
    }

    public void processData(DoUKnowModel result) {
        TextView doUKnow = (TextView) findViewById(R.id.DoUKnow);
        doUKnow.setText(result.getContentText());
    }
}

package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import hsaugsburg.zirbl001.Datamanagement.JSONDoUKnow;
import hsaugsburg.zirbl001.Datamanagement.TourChronologyTask;
import hsaugsburg.zirbl001.Models.ChronologyModel;
import hsaugsburg.zirbl001.Models.DoUKnowModel;
import hsaugsburg.zirbl001.R;

public class DoUKnowActivity extends AppCompatActivity {
    private Context mContext = DoUKnowActivity.this;

    private int chronologyNumber;
    private ChronologyModel nextChronologyItem = new ChronologyModel();

    private TourChronologyTask tourChronologyTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_uknow);
        chronologyNumber = Integer.parseInt(getIntent().getStringExtra("chronologyNumber"));
        int infoPopupID = Integer.parseInt(getIntent().getStringExtra("infopopupid"));
        new JSONDoUKnow(this, infoPopupID).execute("https://zirbl.multimedia.hs-augsburg.de/selectInfoPopupView.php");
        tourChronologyTask = new TourChronologyTask(this, nextChronologyItem, chronologyNumber);

        tourChronologyTask.readChronologyFile();

    }


    public void continueToNextView(View view) {
        tourChronologyTask.continueToNextView();
    }

    public void processData(DoUKnowModel result) {
        TextView doUKnow = (TextView) findViewById(R.id.DoUKnow);
        doUKnow.setText(result.getContentText());
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

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
import hsaugsburg.zirbl001.Interfaces.TourActivity;
import hsaugsburg.zirbl001.Models.ChronologyModel;
import hsaugsburg.zirbl001.Models.DoUKnowModel;
import hsaugsburg.zirbl001.R;

public class DoUKnowActivity extends AppCompatActivity implements TourActivity{
    private Context mContext = DoUKnowActivity.this;

    private int chronologyNumber;
    private int selectedTour;
    private int currentScore;
    private String stationName;
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
        setContentView(R.layout.activity_do_uknow);
        chronologyNumber = Integer.parseInt(getIntent().getStringExtra("chronologyNumber"));
        selectedTour = Integer.parseInt(getIntent().getStringExtra("selectedTour"));
        currentScore = Integer.parseInt(getIntent().getStringExtra("currentscore"));
        stationName = getIntent().getStringExtra("stationName");
        int infoPopupID = Integer.parseInt(getIntent().getStringExtra("infopopupid"));
        new JSONDoUKnow(this, infoPopupID).execute("https://zirbl.multimedia.hs-augsburg.de/selectInfoPopupView.php");
        tourChronologyTask = new TourChronologyTask(this, this, nextChronologyItem, chronologyNumber, currentScore, selectedTour);

        tourChronologyTask.readChronologyFile();

        Toolbar toolbar = (Toolbar) findViewById(R.id.standard_toolbar);
        setSupportActionBar(toolbar);
        String knowledge = "WISSEN";
        getSupportActionBar().setTitle(knowledge);

    }


    public void continueToNextView(View view) {
        tourChronologyTask.continueToNextView();
    }

    public void processData(DoUKnowModel result) {
        TextView doUKnow = (TextView) findViewById(R.id.DoUKnow);
        doUKnow.setText(fromHtml(result.getContentText()));
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

    public String getStationName() {
        return stationName;
    }
}

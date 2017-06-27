package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import hsaugsburg.zirbl001.Datamanagement.JSONStationLocation;
import hsaugsburg.zirbl001.Datamanagement.JSONStationLocation2;
import hsaugsburg.zirbl001.Datamanagement.TourChronologyTask;
import hsaugsburg.zirbl001.Interfaces.TourActivity;
import hsaugsburg.zirbl001.Models.ChronologyModel;
import hsaugsburg.zirbl001.Models.StationModel;
import hsaugsburg.zirbl001.NavigationActivities.TourDetailActivity;
import hsaugsburg.zirbl001.R;

public class NavigationActivity extends AppCompatActivity implements TourActivity{

    private Context mContext = NavigationActivity.this;

    private int chronologyNumber;
    private int selectedTour;
    private int stationID;
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
        setContentView(R.layout.activity_navigation);
        chronologyNumber = Integer.parseInt(getIntent().getStringExtra("chronologyNumber"));
        stationID = Integer.parseInt(getIntent().getStringExtra("stationID"));
        selectedTour = Integer.parseInt(getIntent().getStringExtra("selectedTour"));
        currentScore = Integer.parseInt(getIntent().getStringExtra("currentscore"));
        tourChronologyTask = new TourChronologyTask(this, this, nextChronologyItem, chronologyNumber, currentScore, selectedTour);
        tourChronologyTask.readChronologyFile();

        new JSONStationLocation2(this, selectedTour, stationID).execute("https://zirbl.multimedia.hs-augsburg.de/selectStationLocationsView.php");
    }

    public void processData(StationModel result) {
        stationName = result.getStationName();
    }

    public void continueToNextView(View view) {
        tourChronologyTask.continueToNextView();
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

    public String getStationName() {
        return stationName;
    }
}

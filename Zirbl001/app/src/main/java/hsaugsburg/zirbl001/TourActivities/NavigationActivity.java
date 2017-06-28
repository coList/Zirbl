package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

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

    public static final String GLOBAL_VALUES = "globalValuesFile";
    String serverName;

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

        SharedPreferences globalValues = getSharedPreferences(GLOBAL_VALUES, 0);
        serverName = globalValues.getString("serverName", null);

        new JSONStationLocation2(this, selectedTour, stationID).execute(serverName + "/selectStationLocationsView.php");

        TextView naviTitle = (TextView) findViewById(R.id.navigationTitle);
        naviTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
        TextView naviInfo = (TextView) findViewById(R.id.navigationInfo);
        naviInfo.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
    }

    public void processData(StationModel result) {
        stationName = result.getStationName();
        TextView stationName = (TextView) findViewById(R.id.navigationTitle);
        stationName.setText(result.getStationName());

        TextView mapInstruction = (TextView) findViewById(R.id.navigationInfo);
        mapInstruction.setText(result.getMapInstruction());
    }


    //click on station name to hide or show the mapinstruction
    public void onClick(View view) {
        TextView mapInstruction = (TextView) findViewById(R.id.navigationInfo);

        if (mapInstruction.getVisibility() == View.VISIBLE) {
            mapInstruction.setVisibility(View.GONE);
        } else {
            mapInstruction.setVisibility(View.VISIBLE);
        }
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

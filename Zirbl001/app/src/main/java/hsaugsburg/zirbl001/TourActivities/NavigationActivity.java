package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.Field;

import hsaugsburg.zirbl001.Datamanagement.JSONStationLocation;
import hsaugsburg.zirbl001.Datamanagement.JSONStationLocation2;
import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadTourChronology;
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

    private LoadTourChronology loadTourChronology;

    public static final String GLOBAL_VALUES = "globalValuesFile";
    String serverName;

    public static final String TOUR_VALUES = "tourValuesFile";
    private int totalChronologyValue;

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

        //get global tour values
        SharedPreferences tourValues = getSharedPreferences(TOUR_VALUES, 0);
        selectedTour = Integer.parseInt(tourValues.getString("tourID", null));
        totalChronologyValue = Integer.parseInt(tourValues.getString("totalChronology", null));

        currentScore = Integer.parseInt(getIntent().getStringExtra("currentscore"));
        loadTourChronology = new LoadTourChronology(this, this, nextChronologyItem, selectedTour, chronologyNumber, currentScore);
        loadTourChronology.readChronologyFile();

        SharedPreferences globalValues = getSharedPreferences(GLOBAL_VALUES, 0);
        serverName = globalValues.getString("serverName", null);


        Toolbar toolbar = (Toolbar) findViewById(R.id.standard_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Navigation");

        TextView actionbarText = null;
        try {
            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            actionbarText = (TextView) f.get(toolbar);
            actionbarText.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/OpenSans-Bold.ttf"));
            actionbarText.setAllCaps(true);
            actionbarText.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            actionbarText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        } catch (NoSuchFieldException e) {
        }
        catch (IllegalAccessException e) {
        }
        new JSONStationLocation2(this, selectedTour, stationID).execute(serverName + "/api/selectStationLocationsView.php");

        TextView naviTitle = (TextView) findViewById(R.id.navigationTitle);
        naviTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
        TextView naviInfo = (TextView) findViewById(R.id.navigationInfo);
        naviInfo.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(totalChronologyValue + 1);
        progressBar.setProgress(chronologyNumber + 1);

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
        ImageView arrow = (ImageView) findViewById(R.id.arrow);

        if (mapInstruction.getVisibility() == View.VISIBLE) {
            mapInstruction.setVisibility(View.GONE);
            arrow.setScaleY(-1);
        } else {
            mapInstruction.setVisibility(View.VISIBLE);
            arrow.setScaleY(1);
        }
    }

    public void continueToNextView(View view) {
        loadTourChronology.continueToNextView();
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

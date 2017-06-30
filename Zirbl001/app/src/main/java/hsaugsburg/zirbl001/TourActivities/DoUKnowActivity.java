package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import hsaugsburg.zirbl001.Datamanagement.JSONDoUKnow;
import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadDoUKnow;
import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadTourChronology;
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

    private LoadTourChronology loadTourChronology;

    public static final String GLOBAL_VALUES = "globalValuesFile";
    private String serverName;

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
        setContentView(R.layout.activity_do_uknow);
        chronologyNumber = Integer.parseInt(getIntent().getStringExtra("chronologyNumber"));

        //get global tour values
        SharedPreferences tourValues = getSharedPreferences(TOUR_VALUES, 0);
        selectedTour = Integer.parseInt(tourValues.getString("tourID", null));
        totalChronologyValue = Integer.parseInt(tourValues.getString("totalChronology", null));


        currentScore = Integer.parseInt(getIntent().getStringExtra("currentscore"));
        stationName = getIntent().getStringExtra("stationName");

        SharedPreferences globalValues = getSharedPreferences(GLOBAL_VALUES, 0);
        serverName = globalValues.getString("serverName", null);

        //new JSONDoUKnow(this, selectedTour, infoPopupID).execute(serverName + "/api/selectInfoPopupView.php");

        loadTourChronology = new LoadTourChronology(this, this, nextChronologyItem, selectedTour, chronologyNumber, currentScore);
        loadTourChronology.readChronologyFile();

        Toolbar toolbar = (Toolbar) findViewById(R.id.standard_toolbar);
        setSupportActionBar(toolbar);
        String knowledge = "WISSEN";
        getSupportActionBar().setTitle(knowledge);

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

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(totalChronologyValue + 1);
        progressBar.setProgress(chronologyNumber + 1);

        setDataView();

    }

    public void setDataView() {

        int infoPopupID = Integer.parseInt(getIntent().getStringExtra("infopopupid"));
        DoUKnowModel result = new LoadDoUKnow(this, selectedTour, infoPopupID).readFile();


        TextView doUKnow = (TextView) findViewById(R.id.DoUKnow);
        doUKnow.setText(fromHtml(result.getContentText()));

        if (result.getPicturePath() != null && !result.getPicturePath().isEmpty()) {
            ImageView zirblImage = (ImageView) findViewById(R.id.themeZirbl);
            ImageLoader.getInstance().displayImage(serverName + result.getPicturePath(), zirblImage);
        }
    }


    public void continueToNextView(View view) {
        loadTourChronology.continueToNextView();
    }

    public void processData(DoUKnowModel result) {
        TextView doUKnow = (TextView) findViewById(R.id.DoUKnow);
        doUKnow.setText(fromHtml(result.getContentText()));

        if (result.getPicturePath() != null && !result.getPicturePath().isEmpty()) {
            ImageView zirblImage = (ImageView) findViewById(R.id.themeZirbl);
            ImageLoader.getInstance().displayImage(serverName + result.getPicturePath(), zirblImage);
        }
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

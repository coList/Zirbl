package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import static hsaugsburg.zirbl001.R.id.dotMenu;
import static hsaugsburg.zirbl001.R.layout.layout_top_dark_actionbar;

public class DoUKnowActivity extends AppCompatActivity implements TourActivity{
    private Context mContext = DoUKnowActivity.this;

    private static final String TAG = "DoUKnowActivity";

    private int chronologyNumber;
    private int selectedTour;
    private String stationName;
    private ChronologyModel nextChronologyItem = new ChronologyModel();

    private LoadTourChronology loadTourChronology;

    public static final String GLOBAL_VALUES = "globalValuesFile";
    private String serverName;

    public static final String TOUR_VALUES = "tourValuesFile";
    private int totalChronologyValue;


    //dot menu
    private TextView title;
    private RelativeLayout dotMenuLayout;
    private boolean dotMenuOpen = false;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_uknow);

        //dot menu
        title = (TextView) findViewById(R.id.titleActionbar);
        String knowledge = "Wissen";
        title.setText(knowledge);
        dotMenuLayout=(RelativeLayout) this.findViewById(R.id.dotMenu);
        dotMenuLayout.setVisibility(RelativeLayout.GONE);


        chronologyNumber = Integer.parseInt(getIntent().getStringExtra("chronologyNumber"));

        //get global tour values
        SharedPreferences tourValues = getSharedPreferences(TOUR_VALUES, 0);
        selectedTour = Integer.parseInt(tourValues.getString("tourID", null));
        totalChronologyValue = Integer.parseInt(tourValues.getString("totalChronology", null));

        stationName = getIntent().getStringExtra("stationName");

        SharedPreferences globalValues = getSharedPreferences(GLOBAL_VALUES, 0);
        serverName = globalValues.getString("serverName", null);

        //new JSONDoUKnow(this, selectedTour, infoPopupID).execute(serverName + "/api/selectInfoPopupView.php");

        loadTourChronology = new LoadTourChronology(this, this, nextChronologyItem, selectedTour, chronologyNumber);
        loadTourChronology.readChronologyFile();

        Toolbar toolbar = (Toolbar) findViewById(R.id.standard_toolbar);
        setSupportActionBar(toolbar);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(totalChronologyValue + 1);
        progressBar.setProgress(chronologyNumber + 1);

        setDataView();

    }

    public void setDataView() {

        int infoPopupID = Integer.parseInt(getIntent().getStringExtra("infopopupid"));
        DoUKnowModel result = new LoadDoUKnow(this, selectedTour, infoPopupID).readFile();


        TextView doUKnow = (TextView) findViewById(R.id.DoUKnow);
        String resultText = result.getContentText();
        doUKnow.setText(fromHtml(resultText));
        int stringLength = resultText.length();
        Log.d(TAG, Integer.toString(stringLength));

        if (result.getPicturePath() != null && !result.getPicturePath().isEmpty()) {
            ImageView zirblImage = (ImageView) findViewById(R.id.themeZirbl);
            ImageLoader.getInstance().displayImage(serverName + result.getPicturePath(), zirblImage);
        }
        // Scroll View State Change
        RelativeLayout zirbl = (RelativeLayout) findViewById(R.id.zirbl);
        LinearLayout continueArea = (LinearLayout) findViewById(R.id.continueArea);
        RelativeLayout.LayoutParams paramsContinue = (RelativeLayout.LayoutParams) continueArea.getLayoutParams();
        RelativeLayout.LayoutParams paramsZirbl = (RelativeLayout.LayoutParams) zirbl.getLayoutParams();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        Log.d(TAG, "Höhe: " + height);
        Log.d(TAG, Double.toString(((double)stringLength)/height));
        if(((double)stringLength)/height <= 0.16) {
            paramsContinue.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            paramsContinue.addRule(RelativeLayout.BELOW, 0);

            paramsZirbl.addRule(RelativeLayout.ABOVE, R.id.continueArea);

            continueArea.setLayoutParams(paramsContinue);
            zirbl.setLayoutParams(paramsZirbl);
        } else {
            paramsZirbl.addRule(RelativeLayout.ABOVE, 0);

            paramsContinue.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            paramsContinue.addRule(RelativeLayout.BELOW, R.id.zirbl);

            continueArea.setLayoutParams(paramsContinue);
            zirbl.setLayoutParams(paramsZirbl);
        }
            //
    }


    public void continueToNextView(View view) {
        if (chronologyNumber < 0) {
            this.finish();
        } else {
            loadTourChronology.continueToNextView();
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


    public void showMenu(View view){

        ImageView dotIcon = (ImageView) findViewById(R.id.dotIcon);
        TextView menuStats = (TextView) findViewById(R.id.menuStats);
        TextView menuQuit = (TextView) findViewById(R.id.menuQuit);

        if(dotMenuOpen){
            dotMenuLayout.setVisibility(RelativeLayout.GONE);
            dotMenuOpen = false;
            title.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            dotIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent));
        } else {
            dotMenuLayout.setVisibility(RelativeLayout.VISIBLE);
            dotMenuOpen = true;
            title.setTextColor(ContextCompat.getColor(mContext, R.color.colorTurquoise));
            dotIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.colorTurquoise));
            menuQuit.setTextSize(18);
            menuStats.setTextSize(18);
        }
    }
    public void showStats(View view){
        Log.d(TAG, "showStats: Stats");
    }
    public void quitTour(View view){
        showEndTourDialog();
    }




    public void processData(DoUKnowModel result) {
        TextView doUKnow = (TextView) findViewById(R.id.DoUKnow);
        doUKnow.setText(fromHtml(result.getContentText()));

        if (result.getPicturePath() != null && !result.getPicturePath().isEmpty()) {
            ImageView zirblImage = (ImageView) findViewById(R.id.themeZirbl);
            ImageLoader.getInstance().displayImage(serverName + result.getPicturePath(), zirblImage);
        }
    }
}

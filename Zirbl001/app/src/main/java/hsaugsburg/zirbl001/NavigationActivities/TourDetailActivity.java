package hsaugsburg.zirbl001.NavigationActivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.List;

import hsaugsburg.zirbl001.Datamanagement.DownloadImageTask;
import hsaugsburg.zirbl001.Datamanagement.DownloadTasks.DownloadJSON;
import hsaugsburg.zirbl001.Interfaces.Callback;
import hsaugsburg.zirbl001.Interfaces.DownloadActivity;
import hsaugsburg.zirbl001.Interfaces.JSONModel;
import hsaugsburg.zirbl001.Datamanagement.JSONTourDetail;
import hsaugsburg.zirbl001.Models.TourDetailModel;
import hsaugsburg.zirbl001.Models.TourSelectionModel;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.TourActivities.ClassRegistrationActivity;
import hsaugsburg.zirbl001.TourActivities.TourstartActivity;
import hsaugsburg.zirbl001.Utils.BottomNavigationViewHelper;
import hsaugsburg.zirbl001.Utils.UniversalImageLoader;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;


public class TourDetailActivity extends AppCompatActivity implements Callback, DownloadActivity {

    private static final String TAG = "TourDetailActivity";
    private static final int ACTIVITY_NUM = 0;
    private Context mContext = TourDetailActivity.this;

    private Bitmap mainPictureBitmap;
    private boolean hasOpeningHours = false;

    private ImageView mDetailPhoto;
    private ProgressBar mProgressBar;

    private int tourID;
    private String tourName;

    public static final String GLOBAL_VALUES = "globalValuesFile";
    String serverName;

    private int downloadTasksCounter = 0;
    private int amountOfDownloadTasks = 7;
    private boolean downloadStarted = false;
    private boolean firstClickOnGo = true;

    public void setMainPictureBitmap(Bitmap mainPictureBitmap) {
        this.mainPictureBitmap = mainPictureBitmap;
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourdetail);
        Log.d(TAG, "onCreate: starting");

        setIntentExtras();

        Toolbar toolbar = (Toolbar) findViewById(R.id.standard_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(tourName);

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


        setupBottomNavigationView();

        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollview);
        OverScrollDecoratorHelper.setUpOverScroll(scrollView);


        mDetailPhoto = (ImageView) findViewById(R.id.image);

        SharedPreferences globalValues = getSharedPreferences(GLOBAL_VALUES, 0);
        serverName = globalValues.getString("serverName", null);

        new JSONTourDetail(this).execute(serverName + "/api/selectTourDetailsView.php");

        initImageLoader();
        //setDetailImage();


        ImageButton goButton = (ImageButton) findViewById(R.id.go);
        goButton.setImageResource(R.drawable.btn_plus);


    }

    public void downloadTour() {
        new DownloadJSON(this, this, serverName, tourID, "tourinfopopups", "infopopups").execute(serverName + "/api/selectInfoPopupView.php");
        new DownloadJSON(this, this, serverName, tourID, "tourletters", "letters").execute(serverName + "/api/selectHangmanView.php");
        new DownloadJSON(this, this, serverName, tourID, "toursinglechoice", "singlechoice").execute(serverName + "/api/selectSingleChoiceView.php");
        new DownloadJSON(this, this, serverName, tourID, "tourguessthenumber", "guessthenumber").execute(serverName + "/api/selectGuessTheNumberView.php");
        new DownloadJSON(this, this, serverName, tourID, "stationlocations", "stations").execute(serverName + "/api/selectStationLocationsView.php");
        new DownloadJSON(this, this, serverName, tourID, "tourtruefalse", "truefalse").execute(serverName + "/api/selectTrueFalseView.php");
        new DownloadJSON(this, this, serverName, tourID, "tourchronology", "chronology").execute(serverName + "/api/selectChronologyView.php");

    }

    public void downloadFinished() {
        downloadTasksCounter++;

        if (downloadTasksCounter >= amountOfDownloadTasks) {
            ImageButton goButton = (ImageButton) findViewById(R.id.go);
            goButton.setImageResource(R.drawable.btn_go);
        }

    }

    public void setIntentExtras(){
        Intent intent = getIntent();
        tourID = Integer.parseInt(intent.getStringExtra("tourID"));
        tourName = intent.getStringExtra("tourName");
    }

    public void startTour(View view){

        if (firstClickOnGo) {
            if (!downloadStarted) {
                downloadTour();
                downloadStarted = true;
                firstClickOnGo = false;
            }
        } else {
            Intent intent = new Intent(mContext, TourstartActivity.class);
            intent.putExtra("tourID", Integer.toString(tourID));
            startActivity(intent);


        }


    }


    static final int READ_BLOCK_SIZE = 100;

    public void readTestFile(String filename) {
        try {
            FileInputStream fileIn = openFileInput(filename);
            InputStreamReader InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer = new char[READ_BLOCK_SIZE];
            String s = "";
            int charRead;

            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                s += readstring;

            }
            InputRead.close();
            Log.d("TourDetailActivity", s.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void classRegistration(View view) {
        Intent intent = new Intent(mContext, ClassRegistrationActivity.class);
        intent.putExtra("tourID", Integer.toString(tourID));
        intent.putExtra("tourName", tourName);
        startActivity(intent);
    }


    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);

        //Richtiges Icon hovern
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    private void initImageLoader(){
        Log.d(TAG, "initImageLoader: zweiter");
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void setDetailImage(String mainPictureURL){
        Log.d(TAG, "setDetailImage: dritter");



        //UniversalImageLoader.setImage(imgURL, mDetailPhoto, mProgressBar);

    }


    public void processData(List<JSONModel> result) {

        TextView duration = (TextView) findViewById(R.id.durationText);
        duration.setText(Integer.toString(((TourDetailModel) result.get(tourID)).getDuration()) + " min");

        TextView distance = (TextView) findViewById(R.id.distanceText);
        double dist = ((TourDetailModel) result.get(tourID)).getDistance() / 1000.0;
        distance.setText(Double.toString(dist) + " km");

        TextView difficultyName = (TextView)findViewById(R.id.difficultyText);
        difficultyName.setText(((TourDetailModel) result.get(tourID)).getDifficultyName());

        TextView description = (TextView)findViewById(R.id.description);
        description.setText(fromHtml(((TourDetailModel) result.get(tourID)).getDescription()));

        if (hasOpeningHours) {
            TextView openingHours = (TextView) findViewById(R.id.openingHours);
            TextView openingHoursTitle = (TextView)findViewById(R.id.openingHoursTitle);

            openingHours.setVisibility(View.VISIBLE);
            openingHours.setVisibility(View.VISIBLE);
        }

        if (!(((TourDetailModel)result.get(tourID)).getWarnings().equals("null"))) {
            TextView warnings = (TextView)findViewById(R.id.warnings);
            TextView warningsTitle = (TextView)findViewById(R.id.warningsTitle);
            warnings.setText(fromHtml(((TourDetailModel)result.get(tourID)).getWarnings()));

            warnings.setVisibility(View.VISIBLE);
            warningsTitle.setVisibility(View.VISIBLE);
        }

        String mainPictureURL = ((TourDetailModel)result.get(tourID)).getMainPicture();
        //load picture from cache or from web
        ImageView mainPicture = (ImageView)findViewById(R.id.image);
        if (MemoryCacheUtils.findCachedBitmapsForImageUri(serverName + mainPictureURL, ImageLoader.getInstance().getMemoryCache()).size() > 0) {
            mainPicture.setImageBitmap(MemoryCacheUtils.findCachedBitmapsForImageUri(serverName + mainPictureURL, ImageLoader.getInstance().getMemoryCache()).get(0));
            //Log.d("TourDetailMemory", MemoryCacheUtils.findCachedBitmapsForImageUri(mainPictureURL, ImageLoader.getInstance().getMemoryCache()).toString());
        } else {
            ImageLoader.getInstance().displayImage(serverName + mainPictureURL, mainPicture);
        }
        //setDetailImage(mainPictureURL);
        //Log.d(TAG, "mainPictureURL: " + mainPictureURL);


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
}

package hsaugsburg.zirbl001.NavigationActivities;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.List;

import hsaugsburg.zirbl001.Datamanagement.DownloadTasks.DownloadIsTourFavorised;
import hsaugsburg.zirbl001.Datamanagement.DownloadTasks.DownloadJSON;
import hsaugsburg.zirbl001.Datamanagement.JSONDownload.JSONTourSelection;
import hsaugsburg.zirbl001.Datamanagement.UploadTasks.InsertIntoFavors;
import hsaugsburg.zirbl001.Interfaces.Callback;
import hsaugsburg.zirbl001.Interfaces.DownloadActivity;
import hsaugsburg.zirbl001.Interfaces.JSONModel;
import hsaugsburg.zirbl001.Datamanagement.JSONDownload.JSONTourDetail;
import hsaugsburg.zirbl001.Models.NavigationModels.TourDetailModel;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.TourActivities.ClassRegistrationActivity;
import hsaugsburg.zirbl001.TourActivities.TourstartActivity;
import hsaugsburg.zirbl001.Utils.BottomNavigationViewHelper;
import hsaugsburg.zirbl001.Utils.UniversalImageLoader;


public class TourDetailActivity extends AppCompatActivity implements Callback, DownloadActivity {

    private static final String TAG = "TourDetailActivity";
    private static final int ACTIVITY_NUM = 0;
    private Context mContext = TourDetailActivity.this;

    private int tourID;
    private String tourName;

    public static final String GLOBAL_VALUES = "globalValuesFile";
    String serverName;
    String userName;
    String deviceToken;

    private int downloadTasksCounter = 0;
    private int amountOfDownloadTasks = 9;
    private boolean downloadFinished;
    private boolean downloadStarted = false;
    private boolean firstClickOnGo = true;

    private MenuItem favIconMenu;

    private boolean isFilled;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourdetail);

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

        SharedPreferences globalValues = getSharedPreferences(GLOBAL_VALUES, 0);
        serverName = globalValues.getString("serverName", null);
        userName = globalValues.getString("userName", null);
        deviceToken = globalValues.getString("deviceToken", null);

        new JSONTourDetail(this).execute(serverName + "/api/selectTourDetailsView.php");
        new DownloadIsTourFavorised(this, userName, tourID).execute(serverName + "/api/selectRFavors.php");

        initImageLoader();

    }

    public void setIsFavorised(Boolean isFavorised) {
        if(isFavorised) {
            favIconMenu.setIcon(R.drawable.ic_star_filled);
            isFilled = true;
        }else{
            favIconMenu.setIcon(R.drawable.ic_bottom_star);
            isFilled = false;
        }
    }

    public void downloadTour() {
        new DownloadJSON(this, this, serverName, tourID, "tourinfopopups", "infopopups").execute(serverName + "/api/selectInfoPopupView.php");
        new DownloadJSON(this, this, serverName, tourID, "tourletters", "letters").execute(serverName + "/api/selectHangmanView.php");
        new DownloadJSON(this, this, serverName, tourID, "toursinglechoice", "singlechoice").execute(serverName + "/api/selectSingleChoiceView.php");
        new DownloadJSON(this, this, serverName, tourID, "tourguessthenumber", "guessthenumber").execute(serverName + "/api/selectGuessTheNumberView.php");
        new DownloadJSON(this, this, serverName, tourID, "stationlocations", "stations").execute(serverName + "/api/selectStationLocationsView.php");
        new DownloadJSON(this, this, serverName, tourID, "tourtruefalse", "truefalse").execute(serverName + "/api/selectTrueFalseView.php");
        new DownloadJSON(this, this, serverName, tourID, "tourchronology", "chronology").execute(serverName + "/api/selectChronologyView.php");
        new DownloadJSON(this, this, serverName, tourID, "nutlocations", "nuts").execute(serverName + "/api/selectNutLocationsView.php");
        new DownloadJSON(this, this, serverName, tourID, "tourlocation_infopopups", "location_infopopups").execute(serverName + "/api/selectLocationInfoPopupView.php");

    }

    public void downloadFinished() {
        downloadTasksCounter++;

        if (downloadTasksCounter >= amountOfDownloadTasks) {
            downloadFinished = true;
            readTestFile("location_infopopups" + tourID + ".txt");
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

                doProgressBarAnimation();
            }
        }
    }

    private int secondCounter = 0;
    public void doProgressBarAnimation() {
        ImageButton goButton = (ImageButton)findViewById(R.id.go);
        goButton.setColorFilter(R.color.colorTransparent30);

        final ProgressBar progressBarDownload = (ProgressBar) findViewById(R.id.progressBarDownload);
        progressBarDownload.setMax(amountOfDownloadTasks - 1);
        progressBarDownload.setProgress(0);
        progressBarDownload.setVisibility(View.VISIBLE);

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            public void run() {

                    if(android.os.Build.VERSION.SDK_INT >= 11){
                        // will update the "progress" propriety of seekbar until it reaches progress
                        ObjectAnimator animation = ObjectAnimator.ofInt(progressBarDownload, "progress", downloadTasksCounter + 1);
                        animation.setDuration(500);
                        animation.setInterpolator(new DecelerateInterpolator());
                        animation.start();
                    }
                    else
                    {
                        progressBarDownload.setProgress(downloadTasksCounter + 1);
                    }

                    if (downloadFinished) {
                        secondCounter++;
                    }



                if (!downloadFinished || secondCounter < 2) {
                    handler.postDelayed(this, 500);
                } else {

                    Intent intent = new Intent(mContext, TourstartActivity.class);
                    intent.putExtra("tourID", Integer.toString(tourID));
                    intent.putExtra("classID", "-1");
                    startActivity(intent);
                }
            }
        };
        handler.postDelayed(runnable, 500);
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
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);

        //Richtiges Icon hovern
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    public void processData(List<JSONModel> result) {


        if(result != null) {

            TextView duration = (TextView) findViewById(R.id.durationText);
            duration.setText(Integer.toString(((TourDetailModel) result.get(tourID)).getDuration()) + " min");

            TextView distance = (TextView) findViewById(R.id.distanceText);
            double dist = ((TourDetailModel) result.get(tourID)).getDistance() / 1000.0;
            distance.setText(Double.toString(dist) + " km");

            TextView difficultyName = (TextView) findViewById(R.id.difficultyText);
            difficultyName.setText(((TourDetailModel) result.get(tourID)).getDifficultyName());

            TextView description = (TextView) findViewById(R.id.description);
            description.setText(fromHtml(((TourDetailModel) result.get(tourID)).getDescription()));

            TextView startEnd = (TextView) findViewById(R.id.startEnd);
            startEnd.setText(fromHtml("<b>Tourstart:</b> " + ((TourDetailModel) result.get(tourID)).getStartLocation() + "<br />" +
            "<b>Tourende:</b> " + ((TourDetailModel) result.get(tourID)).getEndLocation()));

            boolean hasOpeningHours = false;

            if (hasOpeningHours) {
                TextView openingHours = (TextView) findViewById(R.id.openingHours);
                TextView openingHoursTitle = (TextView) findViewById(R.id.openingHoursTitle);

                openingHours.setVisibility(View.VISIBLE);
                openingHours.setVisibility(View.VISIBLE);
            }

            String costsText = ((TourDetailModel) result.get(tourID)).getCosts();
            if (costsText != null && !costsText.isEmpty() && !costsText.equals("null")) {
                TextView costsTitle = (TextView) findViewById(R.id.costsTitle);
                TextView costs = (TextView) findViewById(R.id.costs);

                costsTitle.setVisibility(View.VISIBLE);
                costs.setVisibility(View.VISIBLE);

                costs.setText(fromHtml(costsText));
            }

            if (!(((TourDetailModel) result.get(tourID)).getWarnings().equals("null"))) {
                TextView warnings = (TextView) findViewById(R.id.warnings);
                TextView warningsTitle = (TextView) findViewById(R.id.warningsTitle);
                warnings.setText(fromHtml(((TourDetailModel) result.get(tourID)).getWarnings()));

                warnings.setVisibility(View.VISIBLE);
                warningsTitle.setVisibility(View.VISIBLE);
            }

            String mainPictureURL = ((TourDetailModel) result.get(tourID)).getMainPicture();
            //load picture from cache or from web
            ImageView mainPicture = (ImageView) findViewById(R.id.image);
            if (MemoryCacheUtils.findCachedBitmapsForImageUri(serverName + mainPictureURL, ImageLoader.getInstance().getMemoryCache()).size() > 0) {
                mainPicture.setImageBitmap(MemoryCacheUtils.findCachedBitmapsForImageUri(serverName + mainPictureURL, ImageLoader.getInstance().getMemoryCache()).get(0));
            } else {
                ImageLoader.getInstance().displayImage(serverName + mainPictureURL, mainPicture);
            }

            ImageView mapPicture = (ImageView) findViewById(R.id.map);
            String mapPictureURL = ((TourDetailModel) result.get(tourID)).getMapPicture();
            if (MemoryCacheUtils.findCachedBitmapsForImageUri(serverName + mapPictureURL, ImageLoader.getInstance().getMemoryCache()).size() > 0) {
                mapPicture.setImageBitmap(MemoryCacheUtils.findCachedBitmapsForImageUri(serverName + mapPictureURL, ImageLoader.getInstance().getMemoryCache()).get(0));
            } else {
                ImageLoader.getInstance().displayImage(serverName + mapPictureURL, mapPicture);
            }


        }else{
            TextView noConnection = (TextView)findViewById(R.id.noConnection);
            noConnection.setVisibility(View.VISIBLE);
            ImageView tryAgain = (ImageView) findViewById(R.id.tryAgain);
            tryAgain.setVisibility(View.VISIBLE);
        }
    }

    public void tryConnectionAgain(View view) {
        TextView noConnection = (TextView)findViewById(R.id.noConnection);
        noConnection.setVisibility(View.GONE);
        ImageView tryAgain = (ImageView) findViewById(R.id.tryAgain);
        tryAgain.setVisibility(View.GONE);
        new JSONTourSelection(this).execute(serverName + "/api/selectTourDetailsView.php");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_favorite_icon_menu, menu);
        favIconMenu = menu.findItem(R.id.action_favorite);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_favorite:

                //Funktion, die aufgerufen werden muss, um die Tour als Favorit abzuspeichern
                new InsertIntoFavors(userName, deviceToken, tourID, serverName).execute();
                if(isFilled){
                    isFilled = false;
                    favIconMenu.setIcon(R.drawable.ic_bottom_star);
                }else {
                    isFilled = true;
                    favIconMenu.setIcon(R.drawable.ic_star_filled);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

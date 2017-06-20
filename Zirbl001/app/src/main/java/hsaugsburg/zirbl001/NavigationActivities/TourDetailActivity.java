package hsaugsburg.zirbl001.NavigationActivities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.io.File;
import java.util.List;

import hsaugsburg.zirbl001.Datamanagement.DownloadImageTask;
import hsaugsburg.zirbl001.Interfaces.Callback;
import hsaugsburg.zirbl001.Interfaces.JSONModel;
import hsaugsburg.zirbl001.Datamanagement.JSONTourDetail;
import hsaugsburg.zirbl001.Models.TourDetailModel;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.TourActivities.ClassRegistrationActivity;
import hsaugsburg.zirbl001.TourActivities.TourstartActivity;
import hsaugsburg.zirbl001.Utils.BottomNavigationViewHelper;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;


public class TourDetailActivity extends AppCompatActivity implements Callback {

    private static final String TAG = "TourDetailActivity";
    private static final int ACTIVITY_NUM = 0;

    private Context mContext = TourDetailActivity.this;
    private ImageLoader imageLoader;

    private Bitmap mainPictureBitmap;


    private boolean hasOpeningHours = false;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.standard_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra("tourName"));

        setupBottomNavigationView();

        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollview);
        OverScrollDecoratorHelper.setUpOverScroll(scrollView);

        new JSONTourDetail(this).execute("https://zirbl.multimedia.hs-augsburg.de/selectTourDetailsView.php");

        ImageButton button = (ImageButton)findViewById(R.id.go);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(mContext, TourstartActivity.class);
                startActivityForResult(intent, 0);
            }
        });



        final DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED) //filled width
                .build();

        final ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getApplicationContext())
                .threadPoolSize(5)
                .threadPriority(Thread.MAX_PRIORITY)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);
    }

    public void classRegistration(View view) {
        Intent intent = new Intent(mContext, ClassRegistrationActivity.class);
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


    public void processData(List<JSONModel> result) {
        Intent intent = getIntent();
        int tourID = Integer.parseInt(intent.getStringExtra("tourID"));

       // ((BaseActivity) getActivity()).setActionBarTitle(((TourDetailModel) result.get(tourID)).getTourName());


        TextView duration = (TextView) findViewById(R.id.durationText);
        duration.setText(Integer.toString(((TourDetailModel) result.get(tourID)).getDuration()) + " min");

        TextView distance = (TextView) findViewById(R.id.distanceText);
        double dist = ((TourDetailModel) result.get(tourID)).getDistance() / 1000.0;
        distance.setText(Double.toString(dist) + " km");

        TextView difficultyName = (TextView)findViewById(R.id.difficultyText);
        difficultyName.setText(((TourDetailModel) result.get(tourID)).getDifficultyName());

        TextView description = (TextView)findViewById(R.id.description);
        description.setText(((TourDetailModel) result.get(tourID)).getDescription());

        TextView warnings = (TextView)findViewById(R.id.warnings);
        warnings.setText(((TourDetailModel)result.get(tourID)).getWarnings());

        if (hasOpeningHours) {
            TextView openingHours = (TextView) findViewById(R.id.openingHours);
            TextView openingHoursTitle = (TextView)findViewById(R.id.openingHoursTitle);

            openingHours.setVisibility(View.VISIBLE);
            openingHours.setVisibility(View.VISIBLE);
        }


        //load picture from cache or from web
        String mainPictureURL = ((TourDetailModel)result.get(tourID)).getMainPicture();
        ImageView mainPicture = (ImageView)findViewById(R.id.image);
        if (MemoryCacheUtils.findCachedBitmapsForImageUri(mainPictureURL, ImageLoader.getInstance().getMemoryCache()).size() > 0) {
            mainPicture.setImageBitmap(MemoryCacheUtils.findCachedBitmapsForImageUri(mainPictureURL, ImageLoader.getInstance().getMemoryCache()).get(0));
            Log.d("TourDetailMemory", MemoryCacheUtils.findCachedBitmapsForImageUri(mainPictureURL, ImageLoader.getInstance().getMemoryCache()).toString());
        } else {
            ImageLoader.getInstance().displayImage(mainPictureURL, mainPicture);
        }
    }
}

package hsaugsburg.zirbl001.NavigationActivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.contentful.java.cda.CDAArray;
import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.CDAResource;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import hsaugsburg.zirbl001.Datamanagement.JSONDownload.JSONTourFavor;
import hsaugsburg.zirbl001.Datamanagement.Adapter.TourFavorAdapter;
import hsaugsburg.zirbl001.Interfaces.Callback;
import hsaugsburg.zirbl001.Interfaces.InternetActivity;
import hsaugsburg.zirbl001.Interfaces.JSONModel;
import hsaugsburg.zirbl001.Models.NavigationModels.TourDetailModel;
import hsaugsburg.zirbl001.Models.NavigationModels.TourFavorModel;
import hsaugsburg.zirbl001.Models.NavigationModels.TourSelectionModel;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.Utils.BottomNavigationViewHelper;
import hsaugsburg.zirbl001.Utils.UniversalImageLoader;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FavoriteActivity extends AppCompatActivity implements Callback, InternetActivity {
    private static final int ACTIVITY_NUM = 3;

    private Context mContext = FavoriteActivity.this;
    private ListView mListView;
    private TourFavorAdapter adapter;

    public static final String GLOBAL_VALUES = "globalValuesFile";
    String serverName;
    String userName;

    private ImageLoader imageLoader;

    final List<JSONModel> favorites = new ArrayList<>();

    private int dataCounter = 0;
    private int allData = 0;

    //Animation beim Activity wechsel verhindern
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.standard_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Favoriten");

        TextView actionbarText;
        try {
            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            actionbarText = (TextView) f.get(toolbar);
            actionbarText.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/OpenSans-Bold.ttf"));
            actionbarText.setAllCaps(true);
            actionbarText.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            actionbarText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        SharedPreferences globalValues = getSharedPreferences(GLOBAL_VALUES, 0);
        serverName = globalValues.getString("serverName", null);
        userName = globalValues.getString("userName", null);

        initImageLoader();

        if (!isOnline()) {
            NoConnectionDialog noConnectionDialog = new NoConnectionDialog(this);
            noConnectionDialog.showDialog(this);
        } else {
            new JSONTourFavor(this).execute(serverName + "/api2/selectFavoritesView.php?username="+userName);
        }

        mListView = (ListView) findViewById(R.id.home_list_view);

        setupBottomNavigationView();
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void setupBottomNavigationView() {
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);

        //Richtiges Icon hovern
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    @Override
    public void processData(List<JSONModel> result) {
        allData = result.size();

        if (result != null) {
            for (JSONModel resultItem: result) {
                String contentfulID = ((TourFavorModel) resultItem).getTourContentfulID();
                CDAClient client = CDAClient.builder()
                        .setSpace("age874frqdcf")
                        .setToken("e31cc8f67798c6f2d7162d593da89cf31f9d525aa4ea7d1935ef1231153fab4a")
                        .build();


                client.observe(CDAEntry.class)
                        .one(contentfulID)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Subscriber<CDAEntry>() {
                            CDAEntry result;

                            @Override public void onCompleted() {
                                TourFavorModel tourFavorModel = new TourFavorModel();
                                tourFavorModel.setTourName(result.getField("tourname").toString());

                                CDAEntry difficultyEntry = (CDAEntry) result.getField("difficultyId");
                                CDAAsset mainPictureAsset = (CDAAsset) result.getField("mainPicture");

                                tourFavorModel.setMainPicture("https:" + mainPictureAsset.url());
                                Log.d("FavoriteActivity", "https:" + mainPictureAsset.url());
                                tourFavorModel.setDifficultyName(difficultyEntry.getField("difficultyname").toString());
                                tourFavorModel.setDuration(Double.valueOf(result.getField("duration").toString()).intValue());
                                tourFavorModel.setDistance(Double.valueOf(result.getField("distance").toString()).intValue());

                                favorites.add(tourFavorModel);
                                sendData();
                            }

                            @Override public void onError(Throwable error) {
                                Log.e("Contentful", "could not request entry", error);
                            }

                            @Override public void onNext(CDAEntry cdaEntry) {
                                result = cdaEntry;
                            }
                        });
            }
        } else {
            RelativeLayout rl = (RelativeLayout) this.findViewById(R.id.noFavs);
            rl.setVisibility(View.VISIBLE);
        }
    }

    private void sendData() {
        dataCounter++;

        if (dataCounter == allData) {

            adapter = new TourFavorAdapter(this, favorites, imageLoader);
            mListView.setAdapter(adapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    JSONModel selectedTour = (JSONModel) adapter.getItem(position);

                    Intent intent1 = new Intent(mContext, TourDetailActivity.class);
                    intent1.putExtra("contentfulID", ((TourFavorModel) selectedTour).getTourContentfulID());
                    intent1.putExtra("tourName", ((TourFavorModel) selectedTour).getTourName());
                    startActivity(intent1);
                }
            });
        }
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void tryConnectionAgain() {
        if (!isOnline()) {
        NoConnectionDialog noConnectionDialog = new NoConnectionDialog(this);
        noConnectionDialog.showDialog(this);
        } else {
            new JSONTourFavor(this).execute(serverName + "/api2/selectFavoritesView.php?username="+userName);
        }
    }
}

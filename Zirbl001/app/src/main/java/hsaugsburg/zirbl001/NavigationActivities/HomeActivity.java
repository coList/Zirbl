package hsaugsburg.zirbl001.NavigationActivities;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

import hsaugsburg.zirbl001.CMS.GetTourSelection;
import hsaugsburg.zirbl001.Interfaces.Callback;
import hsaugsburg.zirbl001.Interfaces.InternetActivity;
import hsaugsburg.zirbl001.Interfaces.JSONModel;
import hsaugsburg.zirbl001.Datamanagement.JSONDownload.JSONTourSelection;
import hsaugsburg.zirbl001.Models.NavigationModels.TourSelectionModel;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.Datamanagement.Adapter.TourSelectionAdapter;
import hsaugsburg.zirbl001.Utils.BottomNavigationViewHelper;
import hsaugsburg.zirbl001.Utils.UniversalImageLoader;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeActivity extends AppCompatActivity implements InternetActivity {
    private static final int ACTIVITY_NUM = 0;

    private Context mContext = HomeActivity.this;
    private ListView mListView;

    public static final String GLOBAL_VALUES = "globalValuesFile";
    String serverName;

    private ImageLoader imageLoader;

    //Animation beim Activity wechsel verhindern
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.standard_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Touren");

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

        setupBottomNavigationView();

        SharedPreferences globalValues = getSharedPreferences(GLOBAL_VALUES, 0);
        serverName = globalValues.getString("serverName", null);

        //new JSONTourSelection(this).execute(serverName + "/api2/selectTourSelectionView.php");
        mListView = (ListView) findViewById(R.id.home_list_view);

        initImageLoader();

        CDAClient client = CDAClient.builder()
                .setSpace("age874frqdcf")
                .setToken("e31cc8f67798c6f2d7162d593da89cf31f9d525aa4ea7d1935ef1231153fab4a")
                .build();

        final ArrayList<TourSelectionModel> tourSelectionModelArrayList = new ArrayList<>();

        client.observe(CDAEntry.class)
                .where("content_type", "eTour")
                .all()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<CDAArray>() {
                    CDAArray result;

                    @Override
                    public void onCompleted() {
                        Log.d("Contentful", Integer.toString(result.total()));
                        for (CDAResource entry: result.items()) {
                            CDAEntry item = (CDAEntry) entry;


                            CDAEntry difficultyEntry = (CDAEntry) item.getField("difficultyId");
                            CDAEntry categoryEntry = (CDAEntry) item.getField("categoryId");

                            CDAAsset mainPictureAsset = (CDAAsset) item.getField("mainPicture");

                            TourSelectionModel tourSelectionModel = new TourSelectionModel();
                            tourSelectionModel.setTourName(item.getField("tourname").toString());
                            tourSelectionModel.setCategoryName(categoryEntry.getField("categoryname").toString());
                            tourSelectionModel.setDifficultyName(difficultyEntry.getField("difficultyname").toString());
                            tourSelectionModel.setDuration(Double.valueOf(item.getField("duration").toString()).intValue());
                            tourSelectionModel.setDistance(Double.valueOf(item.getField("distance").toString()).intValue());
                            tourSelectionModel.setMainpicture("https:" + mainPictureAsset.url());

                            Log.d("contentful", item.getField("tourname").toString());

                            tourSelectionModelArrayList.add(tourSelectionModel);

                        }
                        processData(tourSelectionModelArrayList);
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e("Contentful", "could not request entry", error);
                    }

                    @Override
                    public void onNext(CDAArray cdaArray) {
                        result = cdaArray;
                    }
                });

        //new GetTourSelection(this).execute();


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

    public void processData(List<TourSelectionModel> result) {
        if (result != null) {


            /*
            //Entferne Fuggertour aus der Liste
            result.remove(0);
            */


            TourSelectionAdapter adapter = new TourSelectionAdapter(this, result, imageLoader);
            mListView.setAdapter(adapter);
            final List<TourSelectionModel> tourSelectionItems = result;

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    JSONModel selectedTour = tourSelectionItems.get(position);
                    Intent intent1 = new Intent(mContext, TourDetailActivity.class);
                    intent1.putExtra("tourID", Integer.toString(((TourSelectionModel) selectedTour).getTourID()));
                    intent1.putExtra("tourName", ((TourSelectionModel) selectedTour).getTourName());
                    startActivity(intent1);
                }
            });
        } else {
            NoConnectionDialog noConnectionDialog = new NoConnectionDialog(this);
            noConnectionDialog.showDialog(this);
        }
    }

    /*
    public void processData(List<JSONModel> result) {
        if (result != null) {



            //Entferne Fuggertour aus der Liste
            result.remove(0);



            TourSelectionAdapter adapter = new TourSelectionAdapter(this, result, imageLoader);
            mListView.setAdapter(adapter);
            final List<JSONModel> tourSelectionItems = result;

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    JSONModel selectedTour = tourSelectionItems.get(position);
                    Intent intent1 = new Intent(mContext, TourDetailActivity.class);
                    intent1.putExtra("tourID", Integer.toString(((TourSelectionModel) selectedTour).getTourID()));
                    intent1.putExtra("tourName", ((TourSelectionModel) selectedTour).getTourName());
                    startActivity(intent1);
                }
            });
        } else {
            NoConnectionDialog noConnectionDialog = new NoConnectionDialog(this);
            noConnectionDialog.showDialog(this);
        }
    }
    */

    public void tryConnectionAgain() {
        //new JSONTourSelection(this).execute(serverName + "/api2/selectTourSelectionView.php");
    }
}

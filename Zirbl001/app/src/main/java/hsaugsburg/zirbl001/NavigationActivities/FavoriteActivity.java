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
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.reflect.Field;
import java.util.List;

import hsaugsburg.zirbl001.Datamanagement.DownloadTasks.DownloadIsTourFavorised;
import hsaugsburg.zirbl001.Datamanagement.JSONDownload.JSONTourDetail;
import hsaugsburg.zirbl001.Datamanagement.JSONDownload.JSONTourFavor;
import hsaugsburg.zirbl001.Datamanagement.Adapter.TourFavorAdapter;
import hsaugsburg.zirbl001.Interfaces.Callback;
import hsaugsburg.zirbl001.Interfaces.InternetActivity;
import hsaugsburg.zirbl001.Interfaces.JSONModel;
import hsaugsburg.zirbl001.Models.NavigationModels.TourFavorModel;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.Utils.BottomNavigationViewHelper;
import hsaugsburg.zirbl001.Utils.UniversalImageLoader;

public class FavoriteActivity extends AppCompatActivity implements Callback, InternetActivity {

    private static final String TAG = "FavoriteActivity";
    private static final int ACTIVITY_NUM = 3;

    private Context mContext = FavoriteActivity.this;
    private ListView mListView;
    private TourFavorAdapter adapter;

    public static final String GLOBAL_VALUES = "globalValuesFile";
    String serverName;
    String userName;

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
        setContentView(R.layout.activity_favorite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.standard_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Favoriten");

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

        SharedPreferences globalValues = getSharedPreferences(GLOBAL_VALUES, 0);
        serverName = globalValues.getString("serverName", null);
        userName = globalValues.getString("userName", null);

        initImageLoader();

        if (!isOnline()) {
            NoConnectionDialog noConnectionDialog = new NoConnectionDialog(this);
            noConnectionDialog.showDialog(this);
        } else {
            new JSONTourFavor(this).execute(serverName + "/api/selectFavoritesView.php?username="+userName);
        }

        mListView = (ListView) findViewById(R.id.home_list_view);

        setupBottomNavigationView();
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
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

    @Override
    public void processData(List<JSONModel> result) {
        if (result != null) {

            adapter = new TourFavorAdapter(this, result, imageLoader);
            mListView.setAdapter(adapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    JSONModel selectedTour = (JSONModel) adapter.getItem(position);

                    Intent intent1 = new Intent(mContext, TourDetailActivity.class);
                    intent1.putExtra("tourID", Integer.toString(((TourFavorModel) selectedTour).getTourID()));
                    intent1.putExtra("tourName", ((TourFavorModel) selectedTour).getTourName());
                    startActivity(intent1);
                }
            });

        }else {
            RelativeLayout rl = (RelativeLayout) this.findViewById(R.id.noFavs);
            rl.setVisibility(View.VISIBLE);
        }
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void tryConnectionAgain() {
        if (!isOnline()) {
        NoConnectionDialog noConnectionDialog = new NoConnectionDialog(this);
        noConnectionDialog.showDialog(this);
        } else {
            new JSONTourFavor(this).execute(serverName + "/api/selectFavoritesView.php?username="+userName);
        }
    }
}

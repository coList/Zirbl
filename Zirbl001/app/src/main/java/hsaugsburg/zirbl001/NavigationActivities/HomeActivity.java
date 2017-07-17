package hsaugsburg.zirbl001.NavigationActivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.reflect.Field;
import java.util.List;

import hsaugsburg.zirbl001.Interfaces.Callback;
import hsaugsburg.zirbl001.Interfaces.JSONModel;
import hsaugsburg.zirbl001.Datamanagement.JSONDownload.JSONTourSelection;
import hsaugsburg.zirbl001.Models.NavigationModels.TourSelectionModel;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.Datamanagement.Adapter.TourSelectionAdapter;
import hsaugsburg.zirbl001.Utils.BottomNavigationViewHelper;
import hsaugsburg.zirbl001.Utils.UniversalImageLoader;

public class HomeActivity extends AppCompatActivity implements Callback {

    private static final String TAG = "HomeActivity";
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
        new JSONTourSelection(this).execute(serverName + "/api/selectTourSelectionView.php");
        mListView = (ListView) findViewById(R.id.home_list_view);

        initImageLoader();
    }

    public void processData(List<JSONModel> result) {
        if (result != null) {
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
        } else{
            TextView noConnection = (TextView)findViewById(R.id.noConnection);
            noConnection.setVisibility(View.VISIBLE);
            ImageView tryAgain = (ImageView) findViewById(R.id.tryAgain);
            tryAgain.setVisibility(View.VISIBLE);
        }
    }

    private void initImageLoader(){
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


    public void tryConnectionAgain(View view) {
        TextView noConnection = (TextView)findViewById(R.id.noConnection);
        noConnection.setVisibility(View.GONE);
        ImageView tryAgain = (ImageView) findViewById(R.id.tryAgain);
        tryAgain.setVisibility(View.GONE);
        new JSONTourSelection(this).execute(serverName + "/api/selectTourSelectionView.php");
    }
}

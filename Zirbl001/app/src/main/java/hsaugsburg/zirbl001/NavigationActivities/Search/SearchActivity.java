package hsaugsburg.zirbl001.NavigationActivities.Search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.contentful.java.cda.CDAArray;
import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.CDAResource;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import hsaugsburg.zirbl001.Datamanagement.JSONDownload.JSONSearch;
import hsaugsburg.zirbl001.Datamanagement.Adapter.SearchSelectionAdapter;
import hsaugsburg.zirbl001.Datamanagement.JSONDownload.JSONTourFavor;
import hsaugsburg.zirbl001.Interfaces.Callback;
import hsaugsburg.zirbl001.Interfaces.InternetActivity;
import hsaugsburg.zirbl001.Interfaces.JSONModel;
import hsaugsburg.zirbl001.Models.NavigationModels.SearchModel;
import hsaugsburg.zirbl001.Models.NavigationModels.TourDetailModel;
import hsaugsburg.zirbl001.Models.NavigationModels.TourSelectionModel;
import hsaugsburg.zirbl001.NavigationActivities.NoConnectionDialog;
import hsaugsburg.zirbl001.NavigationActivities.TourDetailActivity;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.Utils.BottomNavigationViewHelper;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity implements InternetActivity {
    private static final String TAG = "SearchActivity";
    private static final int ACTIVITY_NUM = 1;

    private Context mContext = SearchActivity.this;

    private ListView mListView;
    public static final String GLOBAL_VALUES = "globalValuesFile";
    String serverName;
    private SearchSelectionAdapter adapter;
    private boolean iConnection = true;


    //Animation beim Activity wechsel verhindern
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.standard_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("SUCHE");

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
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        setupBottomNavigationView();

        SharedPreferences globalValues = getSharedPreferences(GLOBAL_VALUES, 0);
        serverName = globalValues.getString("serverName", null);

        if (!isOnline()) {
            NoConnectionDialog noConnectionDialog = new NoConnectionDialog(this);
            noConnectionDialog.showDialog(this);
        } else {
            //new JSONSearch(this).execute(serverName + "/api2/selectSearchDetailsView.php");

            CDAClient client = CDAClient.builder()
                    .setSpace("age874frqdcf")
                    .setToken("e31cc8f67798c6f2d7162d593da89cf31f9d525aa4ea7d1935ef1231153fab4a")
                    .build();

            final ArrayList<SearchModel> searchModelArrayList = new ArrayList<>();

            client.observe(CDAEntry.class)
                    .where("content_type", "eTour")
                    .all()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<CDAArray>() {
                        CDAArray result;

                        @Override
                        public void onCompleted() {
                            for (CDAResource entry: result.items()) {
                                CDAEntry item = (CDAEntry) entry;


                                CDAEntry difficultyEntry = (CDAEntry) item.getField("difficultyId");
                                CDAEntry categoryEntry = (CDAEntry) item.getField("categoryId");

                                CDAAsset mainPictureAsset = (CDAAsset) item.getField("mainPicture");

                                SearchModel searchModel = new SearchModel();
                                searchModel.setContentfulID(item.id());
                                searchModel.setTourName(item.getField("tourname").toString());
                                searchModel.setCategoryName(categoryEntry.getField("categoryname").toString());
                                searchModel.setDifficultyName(difficultyEntry.getField("difficultyname").toString());
                                searchModel.setDuration(Double.valueOf(item.getField("duration").toString()).intValue());
                                searchModel.setDistance(Double.valueOf(item.getField("distance").toString()).intValue());
                                searchModel.setShortDescription(item.getField("shortdescription").toString());

                                searchModelArrayList.add(searchModel);

                            }
                            processData(searchModelArrayList);
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

        mListView = (ListView) findViewById(R.id.search_list_view);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(iConnection) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.search_view_menu_item, menu);
            MenuItem searchViewItem = menu.findItem(R.id.action_search);
            final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);
            searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchViewAndroidActionBar.clearFocus();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    //Hier muss die Liste gefiltert werden
                    adapter.getFilter().filter(newText);
                    return false;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
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

    public void processData(List<SearchModel> result) {
        if (result != null) {
            adapter = new SearchSelectionAdapter(this, result);
            mListView.setAdapter(adapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SearchModel selectedTour = (SearchModel) adapter.getItem(position);

                    Intent intent1 = new Intent(mContext, TourDetailActivity.class);
                    intent1.putExtra("tourID", Integer.toString(selectedTour.getTourID()));
                    intent1.putExtra("tourName", selectedTour.getTourName());
                    intent1.putExtra("contentfulID", ((SearchModel)selectedTour).getContentfulID());
                    startActivity(intent1);
                }
            });
        } else{
            iConnection = false;
            TextView noConnection = (TextView)findViewById(R.id.noConnection);
            noConnection.setVisibility(View.VISIBLE);
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
            new JSONSearch(this).execute(serverName + "/api2/selectSearchDetailsView.php");
        }
    }

    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }
}

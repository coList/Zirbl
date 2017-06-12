package hsaugsburg.zirbl001;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {

    FragmentPagerAdapter adapterViewPager;
    private int selectedTourID;
    private int currentItem;
    private List<TourSelectionModel> tourSelectionModels = new ArrayList<TourSelectionModel>();
    protected OnBackPressedListener onBackPressedListener;


    public List<TourSelectionModel> getTourSelectionModels() {
        return tourSelectionModels;
    }

    public void setTourSelectionModels(List<TourSelectionModel> tourSelectionModels) {
        this.tourSelectionModels = tourSelectionModels;
    }

    public void clearTourSelectionModels() {
        tourSelectionModels.clear();
    }


    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public int getSelectedTourID() {
        return selectedTourID;
    }


    public void changeToInfo(int tourSelectionTourID) {
        selectedTourID = tourSelectionTourID;

        final ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);
        vpPager.setAdapter(adapterViewPager);
        getSupportActionBar().setTitle("");
        vpPager.setCurrentItem(5,false);
        currentItem = vpPager.getCurrentItem();
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    public void onBackPressed() {

        if (onBackPressedListener != null){
            onBackPressedListener.doBack();
        }
        else {
            super.onBackPressed();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        getSupportActionBar().setTitle("Touren");

        final ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);
        adapterViewPager = new BaseActivity.MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        BottomNavigationViewEx bnve = (BottomNavigationViewEx) findViewById(R.id.bnve2);

        BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {



            switch (item.getItemId()) {
                        case R.id.navigation_home:
                            getSupportActionBar().setTitle("Touren");
                            vpPager.setCurrentItem(0, false);
                            currentItem = vpPager.getCurrentItem();
                            return true;
                        case R.id.navigation_search:
                            getSupportActionBar().setTitle("Suche");
                            vpPager.setCurrentItem(1, false);
                            currentItem = vpPager.getCurrentItem();
                            return true;
                        case R.id.navigation_qr:
                            getSupportActionBar().setTitle("QR-Code");
                            vpPager.setCurrentItem(2, false);
                            currentItem = vpPager.getCurrentItem();
                            return true;
                        case R.id.navigation_fav:
                            getSupportActionBar().setTitle("Favoriten");
                            vpPager.setCurrentItem(3, false);
                            currentItem = vpPager.getCurrentItem();
                            return true;
                        case R.id.navigation_profile:
                            getSupportActionBar().setTitle("Profil");
                            vpPager.setCurrentItem(4, false);
                            currentItem = vpPager.getCurrentItem();
                            return true;
                    }

                    return true;
                }

        };

        bnve.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        bnve.enableAnimation(false);
        bnve.enableShiftingMode(false);
        bnve.enableItemShiftingMode(false);
        bnve.setTextVisibility(false);

    }

    public int getCurrentTabbarItem() {
        return currentItem;
    }


    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 6;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }


        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return HomeFragment.newInstance(0, "Search");
                case 1:
                    return SearchFragment.newInstance(1, "Favorit");
                case 2:
                    return QrFragment.newInstance(2, "Categorie");
                case 3:
                    return FavoriteFragment.newInstance(3, "Profile");
                case 4:
                    return ProfileFragment.newInstance(4, "QRCode");
                case 5:
                    return TourDetailFragment.newInstance(5, "Information");
                default:
                    return null;
            }

        }


        // Returns the page title for the top indicator

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

}
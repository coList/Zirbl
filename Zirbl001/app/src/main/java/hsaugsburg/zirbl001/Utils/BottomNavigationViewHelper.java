package hsaugsburg.zirbl001.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import hsaugsburg.zirbl001.NavigationActivities.FavoriteActivity;
import hsaugsburg.zirbl001.NavigationActivities.HomeActivity;
import hsaugsburg.zirbl001.NavigationActivities.Profile.ProfileActivity;
import hsaugsburg.zirbl001.NavigationActivities.QrCode.QrActivity;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.NavigationActivities.Search.SearchActivity;

public class BottomNavigationViewHelper {
    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx){
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);

    }

    public static void  enableNavigation(final Context context, BottomNavigationViewEx view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        Intent intent1 = new Intent(context, HomeActivity.class);
                        context.startActivity(intent1);
                        break;
                    case R.id.navigation_search:
                        Intent intent2 = new Intent(context, SearchActivity.class);
                        context.startActivity(intent2);
                        break;
                    case R.id.navigation_qr:
                        Intent intent3 = new Intent(context, QrActivity.class);
                        context.startActivity(intent3);
                        break;
                    case R.id.navigation_fav:
                        Intent intent4 = new Intent(context, FavoriteActivity.class);
                        context.startActivity(intent4);
                        break;
                    case R.id.navigation_profile:
                        Intent intent5 = new Intent(context, ProfileActivity.class);
                        context.startActivity(intent5);
                        break;
                }

                return false;
            }
        });

    }
}

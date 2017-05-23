package hsaugsburg.zirbl001;

import android.os.Bundle;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.view.Menu;
import android.view.MenuItem;


public class Profile extends BaseActivity {

/*
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.navigation_profile).setChecked(true);
        //Also you can do this for sub menu
       // menu.getItem(firstItemIndex).getSubMenu().getItem(subItemIndex).setChecked(true);
        return true;
    }
*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Profil");

        //Menu menu = findViewById(R.id.tabbarMenu).getMenu();
        //menu.findItem(R.id.navigation_home).setChecked(true);


        /*
        NavigationView navigationView = (NavigationView) findViewById(R.id.tabbarMenu);
        Menu menuNav=navigationView.getMenu();
        MenuItem nav_item2 = menuNav.findItem(R.id.navigation_profile);
        nav_item2.setChecked(true);
        */


        /*
        BottomNavigationView bottomNavigationView;
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.tabbarMenu);
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
        */

        /*
        MenuItem item_profile = (MenuItem) findViewById(R.id.navigation_profile);
        item_profile.setChecked(true);
        */
    }
}

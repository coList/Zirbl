package hsaugsburg.zirbl001.NavigationActivities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.Utils.BottomNavigationViewHelper;

public class ImpressumActivity extends AppCompatActivity {
    private static final int ACTIVITY_NUM = 4;

    private Context mContext = ImpressumActivity.this;

    //Animation beim Activity wechsel verhindern
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impressum);
        Toolbar toolbar = (Toolbar) findViewById(R.id.standard_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Impressum");
        setupBottomNavigationView();

        setActivityText();

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

    public void setActivityText(){
        TextView zirbl = (TextView) findViewById(R.id.impTitle);
        zirbl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);

        TextView team = (TextView) findViewById(R.id.impTeam);
        team.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);

        TextView teamMembers = (TextView) findViewById(R.id.imp4);
        teamMembers.setText(fromHtml("Corinna List <br />" +
                "Tobias Schäll <br />" +
                "Moritz Preisinger <br />" +
                "Melanie Behrens <br /> <br />" +
                "Melis Kahveci  <br />" +
                "Larissa Jost  <br />" +
                "Verena Gehrig  <br />  <br />" +
                "Kerstin Paukstat  <br />" +
                "Tim Reinelt  <br />" +
                "Simon Albrecht"));
    }

    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }
}

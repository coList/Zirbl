package hsaugsburg.zirbl001.Utils;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import hsaugsburg.zirbl001.R;

public class TopDarkActionbar {
    private RelativeLayout dotMenuLayout;
    LinearLayout timeAndScore;
    LinearLayout statistics;
    LinearLayout firstRow;
    private Activity activity;
    private TextView title;
    private TextView menuStats;
    private TextView menuQuit;
    private boolean dotMenuOpen = false;
    private boolean timeAndScoreOpen = false;

    public TopDarkActionbar(Activity activity, String titleText) {
        dotMenuLayout = (RelativeLayout)activity.findViewById(R.id.dotMenu);
        timeAndScore = (LinearLayout) activity.findViewById(R.id.firstRowFirstElement);
        statistics = (LinearLayout) activity.findViewById(R.id.statistics);
        firstRow = (LinearLayout) activity.findViewById(R.id.firstRow);
        menuStats = (TextView) activity.findViewById(R.id.menuStats);
        menuQuit = (TextView) activity.findViewById(R.id.menuQuit);
        this.activity = activity;
        title = (TextView) activity.findViewById(R.id.titleActionbar);
        title.setText(titleText);
        menuStats.setTextSize(18);
        menuQuit.setTextSize(18);
    }

    public void showStats(int currentScore, long startTime) {
        if (timeAndScoreOpen) {
            statistics.animate().translationY(-1*firstRow.getHeight()+20);
            timeAndScoreOpen = false;
        } else {
            statistics.animate().translationY(0);
            TextView scoreElement = (TextView) activity.findViewById(R.id.scoreElement);
            scoreElement.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
            TextView timeElement = (TextView) activity.findViewById(R.id.timeElement);
            timeElement.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
            long totalTime = System.currentTimeMillis() - startTime;
            String time ="0 h 0 min";
            if(startTime != 0) {
                time = String.format(Locale.GERMANY, "%d h %d min",
                        TimeUnit.MILLISECONDS.toHours(totalTime),
                        TimeUnit.MILLISECONDS.toMinutes(totalTime) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totalTime))
                );
            }
            scoreElement.setText(String.format(Locale.GERMANY, "%d", currentScore));
            timeElement.setText(time);
            timeAndScoreOpen = true;
        }
    }

    public void showMenu(){
        ImageView dotIcon = (ImageView) activity.findViewById(R.id.dotIcon);
        if(dotMenuOpen){
            dotMenuLayout.animate().translationY(-1*dotMenuLayout.getHeight());
            statistics.animate().translationY(-1*firstRow.getHeight()+20);
            title.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
            dotIcon.setColorFilter(ContextCompat.getColor(activity, R.color.colorAccent));
            dotMenuOpen = false;
            timeAndScoreOpen = false;
        } else {
            dotMenuLayout.animate().translationY(0);
            title.setTextColor(ContextCompat.getColor(activity, R.color.colorTurquoise));
            dotIcon.setColorFilter(ContextCompat.getColor(activity, R.color.colorTurquoise));
            dotMenuOpen = true;
        }
    }
}

package hsaugsburg.zirbl001.Utils;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.vision.text.Line;

import java.util.concurrent.TimeUnit;

import hsaugsburg.zirbl001.R;

public class TopDarkActionbar {
    private RelativeLayout dotMenuLayout;
    private Activity activity;
    private TextView title;
    private boolean dotMenuOpen = false;

    public TopDarkActionbar(Activity activity, String titleText) {
        this.activity = activity;
        dotMenuLayout = (RelativeLayout) activity.findViewById(R.id.dotMenu);
        dotMenuLayout.setVisibility(RelativeLayout.GONE);

        title = (TextView) activity.findViewById(R.id.titleActionbar);
        title.setText(titleText);
    }

    public void showStats(int currentScore) {
        LinearLayout firstElement = (LinearLayout) activity.findViewById(R.id.firstRowFirstElement);
        if (firstElement.getVisibility() == View.GONE) {
            firstElement.setVisibility(View.VISIBLE);
            TextView scoreElement = (TextView) activity.findViewById(R.id.scoreElement);
            scoreElement.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
            scoreElement.setText("Punkte: " + currentScore);
        } else {
            firstElement.setVisibility(View.GONE);
        }

    }

    public void showStats(int currentScore, long startTime) {
        showStats(currentScore);
        LinearLayout secondElement = (LinearLayout) activity.findViewById(R.id.firstRowSecondElement);
        if (secondElement.getVisibility() == View.GONE) {

            secondElement.setVisibility(View.VISIBLE);

            long totalTime = System.currentTimeMillis() - startTime;

            String time = String.format("%d h %d min",
                    TimeUnit.MILLISECONDS.toHours(totalTime),
                    TimeUnit.MILLISECONDS.toMinutes(totalTime) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totalTime))
            );

            TextView timeElement = (TextView) activity.findViewById(R.id.timeElement);
            timeElement.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
            timeElement.setText("Zeit: " + time);
        } else {
            secondElement.setVisibility(View.GONE);
        }
    }


    public void showMenu(){
        ImageView dotIcon = (ImageView) activity.findViewById(R.id.dotIcon);
        TextView menuStats = (TextView) activity.findViewById(R.id.menuStats);
        TextView menuQuit = (TextView) activity.findViewById(R.id.menuQuit);

        if(dotMenuOpen){
            dotMenuLayout.setVisibility(RelativeLayout.GONE);
            dotMenuOpen = false;
            title.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
            dotIcon.setColorFilter(ContextCompat.getColor(activity, R.color.colorAccent));
        } else {
            dotMenuLayout.setVisibility(RelativeLayout.VISIBLE);
            dotMenuOpen = true;
            title.setTextColor(ContextCompat.getColor(activity, R.color.colorTurquoise));
            dotIcon.setColorFilter(ContextCompat.getColor(activity, R.color.colorTurquoise));
            menuQuit.setTextSize(18);
            menuStats.setTextSize(18);
        }
    }
}

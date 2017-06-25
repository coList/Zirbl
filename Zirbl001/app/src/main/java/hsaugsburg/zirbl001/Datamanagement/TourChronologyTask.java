package hsaugsburg.zirbl001.Datamanagement;


import android.app.Activity;
import android.content.Intent;
import android.support.v7.view.menu.ActionMenuItem;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStreamReader;

import hsaugsburg.zirbl001.Models.ChronologyModel;
import hsaugsburg.zirbl001.TourActivities.DoUKnowActivity;
import hsaugsburg.zirbl001.TourActivities.LettersActivity;
import hsaugsburg.zirbl001.TourActivities.NavigationActivity;
import hsaugsburg.zirbl001.TourActivities.QuizActivity;
import hsaugsburg.zirbl001.TourActivities.SliderActivity;
import hsaugsburg.zirbl001.TourActivities.TrueFalseActivity;

public class TourChronologyTask {

    static final int READ_BLOCK_SIZE = 100;
    private Activity activity;
    private ChronologyModel nextChronologyItem;
    private int chronologyNumber;

    public TourChronologyTask(Activity activity, ChronologyModel nextChronologyItem, int chronologyNumber) {
        this.activity = activity;
        this.nextChronologyItem = nextChronologyItem;
        this.chronologyNumber = chronologyNumber;
    }

    public ChronologyModel readChronologyFile() {

        try {
            FileInputStream fileIn=activity.openFileInput("chronology.txt");
            InputStreamReader InputRead= new InputStreamReader(fileIn);

            char[] inputBuffer= new char[READ_BLOCK_SIZE];
            String s="";
            int charRead;

            while ((charRead=InputRead.read(inputBuffer))>0) {
                // char to string conversion
                String readstring=String.copyValueOf(inputBuffer,0,charRead);
                s +=readstring;

            }
            InputRead.close();

            JSONArray jsonArray = new JSONArray(s);

            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject mJsonLObjectChronologyItems = jsonArray.getJSONObject(j);
                if (j == chronologyNumber + 1) {

                    nextChronologyItem.setTourID(mJsonLObjectChronologyItems.getInt("tourid"));
                    nextChronologyItem.setChronologyNumber(mJsonLObjectChronologyItems.getInt("chronologynumber"));


                    //check which value is not null
                    if (!(mJsonLObjectChronologyItems.isNull("infopopupid"))) {
                        nextChronologyItem.setInfoPopupID(mJsonLObjectChronologyItems.getInt("infopopupid"));
                    } else if (!(mJsonLObjectChronologyItems.isNull("stationid"))) {
                        nextChronologyItem.setStationID(mJsonLObjectChronologyItems.getInt("stationid"));
                    } else if (!(mJsonLObjectChronologyItems.isNull("taskid"))) {
                        nextChronologyItem.setTaskID(mJsonLObjectChronologyItems.getInt("taskid"));
                        nextChronologyItem.setTaskClassName(mJsonLObjectChronologyItems.getString("tableoid"));
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return nextChronologyItem;
    }

    public void continueToNextView() {

        Intent intent = new Intent();
        if (nextChronologyItem.getInfoPopupID() != null) {
            intent = new Intent(activity, DoUKnowActivity.class);
            intent.putExtra("infopopupid", Integer.toString(nextChronologyItem.getInfoPopupID()));

        } else if (nextChronologyItem.getStationID() != null) {
            intent = new Intent(activity, NavigationActivity.class);

        } else if (nextChronologyItem.getTaskID() != null) {
            if (nextChronologyItem.getTaskClassName().equals("e_singlechoicetask")) {
                intent = new Intent(activity, QuizActivity.class);
            } else if (nextChronologyItem.getTaskClassName().equals("e_hangmantask")) {
                intent = new Intent(activity, LettersActivity.class);
            } else if (nextChronologyItem.getTaskClassName().equals("e_guessthenumbertask")) {
                intent = new Intent(activity, SliderActivity.class);
            } else if (nextChronologyItem.getTaskClassName().equals("e_truefalsetask")) {
                intent = new Intent(activity, TrueFalseActivity.class);
            }
            intent.putExtra("taskid", Integer.toString(nextChronologyItem.getTaskID()));

        }
        int nextchronologyNumber = chronologyNumber + 1;
        intent.putExtra("chronologyNumber", Integer.toString(nextchronologyNumber));
        activity.startActivity(intent);
    }
}

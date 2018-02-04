package hsaugsburg.zirbl001.CMS.LoadTasks;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hsaugsburg.zirbl001.Interfaces.TourActivity;
import hsaugsburg.zirbl001.Models.TourModels.ChronologyModel;
import hsaugsburg.zirbl001.Models.TourModels.PictureCountdownModel;
import hsaugsburg.zirbl001.TourActivities.DoUKnowActivity;
import hsaugsburg.zirbl001.TourActivities.IdentifySoundActivity;
import hsaugsburg.zirbl001.TourActivities.LettersActivity;
import hsaugsburg.zirbl001.TourActivities.Navigation.MapQuestNavigationActivity;
import hsaugsburg.zirbl001.TourActivities.PictureCountdownActivity;
import hsaugsburg.zirbl001.TourActivities.QuizActivity;
import hsaugsburg.zirbl001.TourActivities.ResultActivity;
import hsaugsburg.zirbl001.TourActivities.SliderActivity;
import hsaugsburg.zirbl001.TourActivities.TrueFalseActivity;

public class LoadTourChronology {
    private ChronologyModel nextChronologyItem;
    private Activity activity;
    private TourActivity tourActivity;
    private int chronologyNumber;
    private int lastChronologyValue;
    private String tourContentfulID;

    public LoadTourChronology (Activity activity, TourActivity tourActivity, String tourContentfulID, int chronologyNumber) {
        this.activity = activity;
        this.tourActivity = tourActivity;
        this.chronologyNumber = chronologyNumber;
        this.tourContentfulID = tourContentfulID;
    }

    public ChronologyModel loadData() {

        Gson gson = new Gson();
        try {
            String root = "/data/data/hsaugsburg.zirbl001/";
            BufferedReader br = new BufferedReader(new FileReader(root + "chronology" + tourContentfulID + ".json"));
            ArrayList<ChronologyModel> data = gson.fromJson(br.readLine(), new TypeToken<List<ChronologyModel>>(){}.getType());

            lastChronologyValue = data.size() - 1;
            for (ChronologyModel chronologyModel: data) {
                Log.d("Contentful model", Integer.toString(chronologyModel.getChronologyNumber()));
                if (chronologyModel.getChronologyNumber() == chronologyNumber + 1) {
                    nextChronologyItem = chronologyModel;
                    Log.d("ChronologyItem", Integer.toString(nextChronologyItem.getChronologyNumber()));
                    return chronologyModel;
                }
            }

            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;


        }

    }

    public int getLastChronologyValue() {
        return lastChronologyValue;
    }

    public void continueToNextView() {
        Intent intent = new Intent();

        if (nextChronologyItem.getChronologyNumber() == null) {
            intent = new Intent(activity, ResultActivity.class);
        } else {
            if (nextChronologyItem.getInfoPopupContentfulID() != null) {
                intent = new Intent(activity, DoUKnowActivity.class);
                intent.putExtra("infoPopupContentfulID", nextChronologyItem.getInfoPopupContentfulID());

            } else if (nextChronologyItem.getStationContentfulID() != null) {
                intent = new Intent(activity, MapQuestNavigationActivity.class);
                intent.putExtra("stationContentfulID", nextChronologyItem.getStationContentfulID());

            } else if (nextChronologyItem.getTaskContentfulID() != null) {
                Log.d("Contentful", nextChronologyItem.getTaskClassName());
                if (nextChronologyItem.getTaskClassName().equals("eSingleChoiceTask")) {
                    intent = new Intent(activity, QuizActivity.class);
                } else if (nextChronologyItem.getTaskClassName().equals("eLettersTask")) {
                    intent = new Intent(activity, LettersActivity.class);
                } else if (nextChronologyItem.getTaskClassName().equals("eGuessTheNumberTask")) {
                    intent = new Intent(activity, SliderActivity.class);
                } else if (nextChronologyItem.getTaskClassName().equals("eTrueFalseTask")) {
                    intent = new Intent(activity, TrueFalseActivity.class);
                } else if (nextChronologyItem.getTaskClassName().equals("eGuessTheImageTask")) {
                    intent = new Intent(activity, PictureCountdownActivity.class);
                } else if (nextChronologyItem.getTaskClassName().equals("eIdentifySoundTask")) {
                    intent = new Intent(activity, IdentifySoundActivity.class);
                } else {
                    intent = new Intent(activity, PictureCountdownActivity.class);
                }
                intent.putExtra("taskContentfulID", nextChronologyItem.getTaskContentfulID());
            }
        }

        int nextchronologyNumber = chronologyNumber + 1;
        intent.putExtra("chronologyNumber", Integer.toString(nextchronologyNumber));
        intent.putExtra("stationName", tourActivity.getStationName());
        activity.startActivity(intent);

    }
}

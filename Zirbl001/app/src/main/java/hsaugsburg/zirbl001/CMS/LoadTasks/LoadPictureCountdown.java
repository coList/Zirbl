package hsaugsburg.zirbl001.CMS.LoadTasks;


import android.util.Log;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hsaugsburg.zirbl001.Models.TourModels.IdentifySoundModel;
import hsaugsburg.zirbl001.Models.TourModels.LettersModel;
import hsaugsburg.zirbl001.Models.TourModels.PictureCountdownModel;

public class LoadPictureCountdown {
    private String contentfulID;
    private String tourContentfulID;

    public LoadPictureCountdown(String contentfulID, String tourContentfulID) {
        this.contentfulID = contentfulID;
        this.tourContentfulID = tourContentfulID;

    }

    public PictureCountdownModel loadData() {

        Gson gson = new Gson();
        try {
            String root = "/data/data/hsaugsburg.zirbl001/";
            BufferedReader br = new BufferedReader(new FileReader(root + "pictureCountdownTasks" + tourContentfulID + ".json"));
            ArrayList<PictureCountdownModel> data = gson.fromJson(br.readLine(), new TypeToken<List<PictureCountdownModel>>(){}.getType());

            for (PictureCountdownModel pictureCountdownModel: data) {
                Log.d("Contentful model", pictureCountdownModel.getContentfulID());
                if (pictureCountdownModel.getContentfulID().equals(contentfulID)) {
                    return pictureCountdownModel;
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
}

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
import hsaugsburg.zirbl001.Models.TourModels.QuizModel;
import hsaugsburg.zirbl001.Models.TourModels.SliderModel;

public class LoadSlider {
    private String contentfulID;
    private String tourContentfulID;

    public LoadSlider(String contentfulID, String tourContentfulID) {
        this.contentfulID = contentfulID;
        this.tourContentfulID = tourContentfulID;

    }

    public SliderModel loadData() {

        Gson gson = new Gson();
        try {
            String root = "/data/data/hsaugsburg.zirbl001/";
            BufferedReader br = new BufferedReader(new FileReader(root + "sliderTasks" + tourContentfulID + ".json"));
            ArrayList<SliderModel> data = gson.fromJson(br.readLine(), new TypeToken<List<SliderModel>>(){}.getType());

            for (SliderModel sliderModel: data) {
                Log.d("Contentful model", sliderModel.getContentfulID());
                if (sliderModel.getContentfulID().equals(contentfulID)) {
                    return sliderModel;
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

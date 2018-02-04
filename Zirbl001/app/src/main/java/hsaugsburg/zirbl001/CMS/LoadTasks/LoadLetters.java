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

public class LoadLetters {
    private String contentfulID;
    private String tourContentfulID;

    public LoadLetters(String contentfulID, String tourContentfulID) {
        this.contentfulID = contentfulID;
        this.tourContentfulID = tourContentfulID;

    }

    public LettersModel loadData() {

        Gson gson = new Gson();
        try {
            String root = "/data/data/hsaugsburg.zirbl001/";
            BufferedReader br = new BufferedReader(new FileReader(root + "lettersTasks" + tourContentfulID + ".json"));
            ArrayList<LettersModel> data = gson.fromJson(br.readLine(), new TypeToken<List<LettersModel>>(){}.getType());

            for (LettersModel lettersModel: data) {
                Log.d("Contentful model", lettersModel.getContentfulID());
                if (lettersModel.getContentfulID().equals(contentfulID)) {
                    return lettersModel;
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

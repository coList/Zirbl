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

public class LoadIdentifySound {
    private String contentfulID;
    private String tourContentfulID;

    public LoadIdentifySound(String contentfulID, String tourContentfulID) {
        this.contentfulID = contentfulID;
        this.tourContentfulID = tourContentfulID;

    }

    public IdentifySoundModel loadData() {

        Gson gson = new Gson();
        try {
            String root = "/data/data/hsaugsburg.zirbl001/";
            BufferedReader br = new BufferedReader(new FileReader(root + "identifySoundTasks" + tourContentfulID + ".json"));
            ArrayList<IdentifySoundModel> data = gson.fromJson(br.readLine(), new TypeToken<List<IdentifySoundModel>>(){}.getType());

            for (IdentifySoundModel identifySoundModel: data) {
                Log.d("Contentful model", identifySoundModel.getContentfulID());
                if (identifySoundModel.getContentfulID().equals(contentfulID)) {
                    return identifySoundModel;
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

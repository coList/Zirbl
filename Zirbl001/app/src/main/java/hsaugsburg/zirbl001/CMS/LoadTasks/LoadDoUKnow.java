package hsaugsburg.zirbl001.CMS.LoadTasks;


import android.util.Log;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hsaugsburg.zirbl001.Models.TourModels.DoUKnowModel;

public class LoadDoUKnow {
    private String contentfulID;
    private String tourContentfulID;

    public LoadDoUKnow(String contentfulID, String tourContentfulID) {
        this.contentfulID = contentfulID;
        this.tourContentfulID = tourContentfulID;

    }

    public DoUKnowModel loadData() {

        Gson gson = new Gson();
        try {
            String root = "/data/data/hsaugsburg.zirbl001/";
            BufferedReader br = new BufferedReader(new FileReader(root + "infoPopups" + tourContentfulID + ".json"));
            ArrayList<DoUKnowModel> data = gson.fromJson(br.readLine(), new TypeToken<List<DoUKnowModel>>(){}.getType());

            for (DoUKnowModel doUKnowModel: data) {
                if (doUKnowModel.getContentfulID().equals(contentfulID)) {
                    return doUKnowModel;
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

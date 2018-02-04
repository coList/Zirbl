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
import hsaugsburg.zirbl001.Models.TourModels.MapModels.StationModel;

public class LoadStation {
    private String contentfulID;
    private String tourContentfulID;

    public LoadStation(String contentfulID, String tourContentfulID) {
        this.contentfulID = contentfulID;
        this.tourContentfulID = tourContentfulID;

    }

    public StationModel loadData() {

        Gson gson = new Gson();
        try {
            String root = "/data/data/hsaugsburg.zirbl001/";
            BufferedReader br = new BufferedReader(new FileReader(root + "stations" + tourContentfulID + ".json"));
            ArrayList<StationModel> data = gson.fromJson(br.readLine(), new TypeToken<List<StationModel>>(){}.getType());

            for (StationModel stationModel: data) {
                if (stationModel.getContentfulID().equals(contentfulID)) {
                    return stationModel;
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

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
import hsaugsburg.zirbl001.Models.TourModels.MapModels.NutModel;

public class LoadNuts {
    private String tourContentfulID;

    public LoadNuts(String tourContentfulID) {
        this.tourContentfulID = tourContentfulID;

    }

    public ArrayList<NutModel> loadData() {

        Gson gson = new Gson();
        try {
            String root = "/data/data/hsaugsburg.zirbl001/";
            BufferedReader br = new BufferedReader(new FileReader(root + "nuts" + tourContentfulID + ".json"));
            ArrayList<NutModel> data = gson.fromJson(br.readLine(), new TypeToken<List<NutModel>>(){}.getType());

            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;


        }

    }
}

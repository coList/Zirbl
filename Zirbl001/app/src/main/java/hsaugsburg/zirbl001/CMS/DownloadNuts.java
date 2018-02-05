package hsaugsburg.zirbl001.CMS;

import android.util.Log;

import com.contentful.java.cda.CDAArray;
import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.CDAResource;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hsaugsburg.zirbl001.Interfaces.DownloadActivity;
import hsaugsburg.zirbl001.Models.NavigationModels.TourDetailModel;
import hsaugsburg.zirbl001.Models.TourModels.MapModels.NutModel;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DownloadNuts {
    private DownloadActivity downloadActivity;
    private String selectedTour;

    public DownloadNuts(DownloadActivity downloadActivity, String selectedTour) {
        this.downloadActivity = downloadActivity;
        this.selectedTour = selectedTour;
    }

    public void downloadData() {
        CDAClient client = CDAClient.builder()
                .setSpace("age874frqdcf")
                .setToken("e31cc8f67798c6f2d7162d593da89cf31f9d525aa4ea7d1935ef1231153fab4a")
                .build();

        client.observe(CDAEntry.class)
                .where("content_type", "eNut")
                .where("fields.tourId.sys.id", selectedTour)
                .all()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<CDAArray>() {
                    CDAArray result;

                    @Override public void onCompleted() {
                        ArrayList<NutModel> nutModels = new ArrayList<>();
                        for (CDAResource resource : result.items()) {
                            NutModel nutModel = new NutModel();
                            CDAEntry entry = (CDAEntry) resource;
                            nutModel.setContentfulID(entry.id());
                            nutModel.setTourContentfulID(selectedTour);
                            nutModel.setFoundText(entry.getField("foundText").toString());
                            nutModel.setScore(Double.valueOf(entry.getField("score").toString()).intValue());

                            String location = entry.getField("location").toString();
                            String parts[] = location.split(",");
                            double longitude = Double.valueOf(parts[0].substring(5));
                            double latitude = Double.valueOf(parts[1].substring(5, parts[1].length() - 1));
                            Log.d("ContentfulNuts", Double.toString(latitude));
                            nutModel.setLatitude(latitude);
                            nutModel.setLongitude(longitude);

                            nutModels.add(nutModel);
                        }

                        String root = "/data/data/hsaugsburg.zirbl001/";
                        storeDataInStorage(root + "nuts" + selectedTour + ".json", nutModels);


                        downloadActivity.downloadFinished();

                    }

                    @Override public void onError(Throwable error) {
                        Log.e("Contentful", "could not request entry", error);
                    }

                    @Override
                    public void onNext(CDAArray cdaArray) {
                        result = cdaArray;
                    }
                });
    }

    private void storeDataInStorage(String location, List<?> list) {
        try {
            Gson gson = new Gson();
            FileWriter fileWriter = new FileWriter(location);
            fileWriter.write("[");
            for (int i = 0; i < list.size(); i++) {

                String json = gson.toJson(list.get(i));
                fileWriter.write(json);

                if (i != list.size() - 1) {
                    fileWriter.write(",");
                }
            }

            fileWriter.write("]");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

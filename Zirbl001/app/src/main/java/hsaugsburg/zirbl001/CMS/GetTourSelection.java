package hsaugsburg.zirbl001.CMS;


import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.contentful.java.cda.CDAArray;
import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.CDAResource;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import hsaugsburg.zirbl001.Interfaces.JSONModel;
import hsaugsburg.zirbl001.Models.NavigationModels.TourSelectionModel;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GetTourSelection extends AsyncTask<String, String, List<TourSelectionModel>> {

    private Activity activity;

    public GetTourSelection (Activity activity) {
        this.activity = activity;
    }
    protected List<TourSelectionModel> doInBackground(String... params) {
        CDAClient client = CDAClient.builder()
                .setSpace("age874frqdcf")
                .setToken("e31cc8f67798c6f2d7162d593da89cf31f9d525aa4ea7d1935ef1231153fab4a")
                .build();



        final ArrayList<TourSelectionModel> tourSelectionModels = new ArrayList<>();

        client.observe(CDAEntry.class)
                .where("content_type", "eTour")
                .all()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<CDAArray>() {
                    CDAArray result;

                    @Override
                    public void onCompleted() {
                        Log.d("Contentful", Integer.toString(result.total()));
                        for (CDAResource entry: result.items()) {
                            CDAEntry item = (CDAEntry) entry;


                            CDAEntry difficultyEntry = (CDAEntry) item.getField("difficultyId");
                            CDAEntry categoryEntry = (CDAEntry) item.getField("categoryId");

                            CDAAsset mainPictureAsset = (CDAAsset) item.getField("mainPicture");

                            TourSelectionModel tourSelectionModel = new TourSelectionModel();
                            tourSelectionModel.setTourName(item.getField("tourname").toString());
                            tourSelectionModel.setCategoryName(categoryEntry.getField("categoryname").toString());
                            tourSelectionModel.setDifficultyName(difficultyEntry.getField("difficultyname").toString());
                            tourSelectionModel.setDuration((int)item.getField("duration"));
                            tourSelectionModel.setDistance((int)item.getField("distance"));
                            tourSelectionModel.setMainpicture("https:" + mainPictureAsset.url());

                            tourSelectionModels.add(tourSelectionModel);


                        }


                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e("Contentful", "could not request entry", error);
                    }

                    @Override
                    public void onNext(CDAArray cdaArray) {
                        result = cdaArray;
                    }
                });
        return tourSelectionModels;
    }

    protected void onPostExecute(List<TourSelectionModel> result) {
        super.onPostExecute(result);
        //Log.d("Contentful", result.toString());
        //activity.processData(result);
    }
}

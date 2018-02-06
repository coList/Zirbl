package hsaugsburg.zirbl001.Datamanagement.JSONDownload;

import android.os.AsyncTask;
import android.util.Log;

import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import hsaugsburg.zirbl001.Interfaces.Callback;
import hsaugsburg.zirbl001.Interfaces.JSONModel;
import hsaugsburg.zirbl001.Models.NavigationModels.TourDetailModel;
import hsaugsburg.zirbl001.Models.NavigationModels.TourFavorModel;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class JSONTourFavor extends AsyncTask<String, String, List<JSONModel>> {
    private Callback callback;

    public JSONTourFavor(Callback callback) {
        this.callback = callback;
    }

    protected List<JSONModel> doInBackground(String... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url;
            url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer buffer = new StringBuffer();
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            String finalJson = buffer.toString();

            try {
                JSONArray parentArray = new JSONArray(finalJson);
                List<JSONModel> tourFavorModelList = new ArrayList<>();

                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject mJsonLObjectTourFavor = parentArray.getJSONObject(i);

                    final TourFavorModel tourFavorModel = new TourFavorModel();

                    CDAClient client = CDAClient.builder()
                            .setSpace("age874frqdcf")
                            .setToken("e31cc8f67798c6f2d7162d593da89cf31f9d525aa4ea7d1935ef1231153fab4a")
                            .build();

                    client.observe(CDAEntry.class)
                            .one(mJsonLObjectTourFavor.getString("contentfulid"))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new Subscriber<CDAEntry>() {
                                CDAEntry result;

                                @Override public void onCompleted() {
                                    TourDetailModel tourDetailModel = new TourDetailModel();
                                    tourFavorModel.setTourName(result.getField("tourname").toString());

                                    CDAEntry difficultyEntry = (CDAEntry) result.getField("difficultyId");
                                    CDAEntry startLocation = (CDAEntry) result.getField("startlocation");
                                    CDAEntry endLocation = (CDAEntry) result.getField("endlocation");

                                    CDAAsset mainPictureAsset = (CDAAsset) result.getField("mainPicture");

                                    tourFavorModel.setMainpicture("https:" + mainPictureAsset.url());


                                    tourFavorModel.setDifficultyName(difficultyEntry.getField("difficultyname").toString());


                                    tourFavorModel.setDuration(Double.valueOf(result.getField("duration").toString()).intValue());
                                    tourFavorModel.setDistance(Double.valueOf(result.getField("distance").toString()).intValue());

                                }

                                @Override public void onError(Throwable error) {
                                    Log.e("Contentful", "could not request entry", error);
                                }

                                @Override public void onNext(CDAEntry cdaEntry) {
                                    result = cdaEntry;
                                }
                            });




                    tourFavorModel.setUserID(mJsonLObjectTourFavor.getInt("userid"));
                    tourFavorModel.setTourID(mJsonLObjectTourFavor.getInt("tourid"));
                    tourFavorModel.setTourName(mJsonLObjectTourFavor.getString("tourname"));
                    tourFavorModel.setTourContentfulID(mJsonLObjectTourFavor.getString("contentfulid"));


                    /*
                    tourFavorModel.setDifficultyName(mJsonLObjectTourFavor.getString("difficultyname"));
                    tourFavorModel.setDuration(mJsonLObjectTourFavor.getInt("duration"));
                    tourFavorModel.setDistance(mJsonLObjectTourFavor.getInt("distance"));
                    tourFavorModel.setMainpicture(mJsonLObjectTourFavor.getString("mainpicture"));
                    */

                    tourFavorModelList.add(tourFavorModel);
                }
                return tourFavorModelList;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    protected void onPostExecute(List<JSONModel> result) {
        super.onPostExecute(result);
        callback.processData(result);
    }
}

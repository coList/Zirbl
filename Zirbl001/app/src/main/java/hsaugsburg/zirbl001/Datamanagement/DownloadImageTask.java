package hsaugsburg.zirbl001.Datamanagement;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    ImageView bmImage;
    //TourDetailModel tourDetailModel;
    //TourSelectionModel tourSelectionModel;
    //Context activity;

    /*
    public DownloadImageTask(ImageView bmImage, Context activity, TourSelectionModel tourSelectionModel) {
        this.bmImage = bmImage;
        //this.tourDetailModel = tourDetailModel;
        this.activity = activity;
        this.tourSelectionModel = tourSelectionModel;
    }
    */

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
        //this.tourDetailModel = tourDetailModel;
        //this.tourSelectionModel = tourSelectionModel;
    }


    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
            in.close();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
        //tourDetailModel.setMainPictureBitmap(result);
        /*
        if (tourSelectionModel != null) {
            tourSelectionModel.setMainPictureBitmap(result);
            //((BaseActivity)activity).setTourSelectionModel(tourSelectionModel);
            ((BaseActivity)activity).getTourSelectionModels().add(tourSelectionModel);
        }
        */

    }
}

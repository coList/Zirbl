package hsaugsburg.zirbl001.CMS;


import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadImage extends AsyncTask<String, String, String> {
    private Activity activity;

    public DownloadImage(Activity activity) {
        this.activity = activity;
    }
    protected String doInBackground(String... params) {  //params: url, selectedTour, firstPartOfName, secondPartOfName, secondPartOfName
        String[] parts = params[0].split("\\.");


        Bitmap bitmap = getBitmapFromURL("https:" + params[0]);

        ContextWrapper cw = new ContextWrapper(activity.getApplicationContext());
        File directory = cw.getDir("zirblImages", Context.MODE_PRIVATE);

        String name = params[2] + params[3] + params[4];
        File mypath=new File(directory, params[1] + name + "." + parts[parts.length - 1]);

        FileOutputStream pictureFileout = null;
        try {
            pictureFileout = new FileOutputStream(mypath);
            if (parts[parts.length - 1].equals("png")) {

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, pictureFileout);
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, pictureFileout);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                pictureFileout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "done";
    }


    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }

    private static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            return null;
        }
    }
}

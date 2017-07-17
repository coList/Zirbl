package hsaugsburg.zirbl001.Datamanagement.DownloadTasks;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    private ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }


    protected Bitmap doInBackground(String... urls) {
        final int IMAGE_MAX_SIZE = 1400000;

        String urldisplay = urls[0];
        Bitmap bitmap = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }

            in = new java.net.URL(urldisplay).openStream();
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                //o.inPreferredConfig = Bitmap.Config.RGB_565;
                bitmap = BitmapFactory.decodeStream(in, null, o);


                // resize to desired dimensions
                int height = bitmap.getHeight();
                int width = bitmap.getWidth();

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) x,
                        (int) y, true);
                bitmap.recycle();
                bitmap = scaledBitmap;

                //System.gc();
            } else {
                bitmap = BitmapFactory.decodeStream(in);
            }
            in.close();

            return bitmap;

            //mIcon11 = BitmapFactory.decodeStream(in);
            //in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        //return bitmap;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);

    }

}

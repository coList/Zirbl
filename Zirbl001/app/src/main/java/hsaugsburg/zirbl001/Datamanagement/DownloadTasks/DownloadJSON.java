package hsaugsburg.zirbl001.Datamanagement.DownloadTasks;


import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import hsaugsburg.zirbl001.Interfaces.DownloadActivity;

public class DownloadJSON extends AsyncTask<String, String, String> {
    private Activity activity;
    private DownloadActivity downloadActivity;
    private int selectedTour;
    private String outerName;
    private String innerName;
    private String serverName;

    public DownloadJSON (Activity activity, DownloadActivity downloadActivity, String serverName, int selectedTour, String outerName, String innerName) {
        this.activity = activity;
        this.downloadActivity = downloadActivity;
        this.serverName = serverName;
        this.selectedTour = selectedTour;
        this.outerName = outerName;
        this.innerName = innerName;
    }

    protected String doInBackground(String... params) {
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
                JSONObject parentObject = parentArray.getJSONObject(0);

                JSONArray mJsonArrayTourElement = parentObject.getJSONArray(outerName);

                FileOutputStream fileout= activity.openFileOutput(innerName + selectedTour + ".txt", activity.MODE_PRIVATE);
                OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);

                for (int i = 0; i < mJsonArrayTourElement.length(); i++) {
                    JSONObject mJsonLObjectTourElement = mJsonArrayTourElement.getJSONObject(i);
                    boolean equalsSelectedTour = false;

                    if (mJsonLObjectTourElement.isNull("tourid")) {
                       equalsSelectedTour = true;
                    } else {
                        if (mJsonLObjectTourElement.getInt("tourid") == selectedTour) {
                            equalsSelectedTour = true;
                        }
                    }

                    if (equalsSelectedTour) {
                        JSONArray mJSONArrayElement = mJsonLObjectTourElement.getJSONArray(innerName);

                        for (int j = 0; j < mJSONArrayElement.length(); j++) {
                            JSONObject mJSONObjectElement = mJSONArrayElement.getJSONObject(j);

                            if (mJSONObjectElement.has("picturepath")) {
                                if (!mJSONObjectElement.isNull("picturepath") && !mJSONObjectElement.getString("picturepath").equals("null")) {
                                    String name = "";

                                    if (mJSONObjectElement.has("taskid")) {
                                        name = "taskid" + mJSONObjectElement.getInt("taskid");



                                    } else if (mJSONObjectElement.has("infopopupid")) {
                                        name = "infopopupid" + mJSONObjectElement.getInt("infopopupid");
                                    }

                                    Bitmap bitmap = getBitmapFromURL(serverName + mJSONObjectElement.getString("picturepath"));

                                    String[] parts = mJSONObjectElement.getString("picturepath").split("\\.");

                                    ContextWrapper cw = new ContextWrapper(activity.getApplicationContext());
                                    // path to /data/data/yourapp/app_data/imageDir

                                    File directory = cw.getDir("zirblImages", Context.MODE_PRIVATE);
                                    // Create imageDir
                                    File mypath=new File(directory, selectedTour + name + "." + parts[parts.length - 1]);
                                    Log.d("DoUKnow", selectedTour + name + "." + parts[parts.length - 1]);

                                    FileOutputStream pictureFileout = null;
                                    try {
                                        pictureFileout = new FileOutputStream(mypath);
                                        // Use the compress method on the BitMap object to write image to the OutputStream
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
                                }

                            }
                        }

                        outputWriter.write(mJSONArrayElement.toString());
                    }
                }

                outputWriter.close();

                return "Download finished";
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
    protected void onPostExecute(String result){
        super.onPostExecute(result);
        File file = new File(innerName + selectedTour + ".txt");
        file.deleteOnExit();
        downloadActivity.downloadFinished();
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
            // Log exception
            return null;
        }
    }



}

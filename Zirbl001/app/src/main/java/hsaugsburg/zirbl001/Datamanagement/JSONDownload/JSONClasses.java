package hsaugsburg.zirbl001.Datamanagement.JSONDownload;

import android.os.AsyncTask;

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

import hsaugsburg.zirbl001.Models.NavigationModels.ClassesModel;
import hsaugsburg.zirbl001.NavigationActivities.QrCode.QrSavedFragment;


public class JSONClasses extends AsyncTask<String, String, List<ClassesModel>> {
    QrSavedFragment qrSavedFragment;
    String username;

    public JSONClasses (QrSavedFragment qrSavedFragment, String username) {
        this.qrSavedFragment = qrSavedFragment;
        this.username = username;
    }

    protected List<ClassesModel> doInBackground(String... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url;
            url = new URL(params[0] + "?username=" + username);
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

                List<ClassesModel> classesModelList = new ArrayList<>();


                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject mJsonLObjectClasses = parentArray.getJSONObject(i);

                    ClassesModel classesModel = new ClassesModel();

                    classesModel.setTourID(mJsonLObjectClasses.getInt("tourid"));
                    classesModel.setTourName(mJsonLObjectClasses.getString("tourname"));
                    classesModel.setClassname(mJsonLObjectClasses.getString("classname"));
                    classesModel.setCreationDate(mJsonLObjectClasses.getString("creationdate"));
                    classesModel.setQrCode(mJsonLObjectClasses.getString("qrcode"));
                    classesModel.setSchoolname(mJsonLObjectClasses.getString("schoolname"));

                    classesModelList.add(classesModel);
                }

                return classesModelList;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.toString();
        } finally{
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
    protected void onPostExecute(List<ClassesModel> result){

        super.onPostExecute(result);
        qrSavedFragment.processData(result);


    }

}

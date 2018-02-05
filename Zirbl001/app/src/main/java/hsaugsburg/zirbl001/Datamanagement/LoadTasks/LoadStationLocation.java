package hsaugsburg.zirbl001.Datamanagement.LoadTasks;

import android.app.Activity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStreamReader;

import hsaugsburg.zirbl001.Models.TourModels.MapModels.StationModel;

public class LoadStationLocation {
    private Activity activity;
    private int tourID;
    private int stationID;

    public LoadStationLocation(Activity activity, int tourID, int stationID) {
        this.activity = activity;
        this.tourID = tourID;
        this.stationID = stationID;
    }

    public StationModel readFile() {
        StationModel stationModel = new StationModel();
        try {
            FileInputStream fileIn=activity.openFileInput("stations" + tourID + ".txt");
            InputStreamReader InputRead= new InputStreamReader(fileIn);

            int READ_BLOCK_SIZE = 100;
            char[] inputBuffer= new char[READ_BLOCK_SIZE];
            String s="";
            int charRead;

            while ((charRead=InputRead.read(inputBuffer))>0) {
                // char to string conversion
                String readstring=String.copyValueOf(inputBuffer,0,charRead);
                s +=readstring;

            }
            InputRead.close();



            JSONArray jsonArray = new JSONArray(s);

                    for(int y=0; y<jsonArray.length(); y++){
                        JSONObject mJSONObjectStation = jsonArray.optJSONObject(y);

                        if(mJSONObjectStation.getInt("stationid") == stationID){
                            stationModel.setTourID(mJSONObjectStation.getInt("tourid"));
                            stationModel.setChronologyNumber(mJSONObjectStation.getInt("chronologynumber"));
                            stationModel.setStationID(mJSONObjectStation.getInt("stationid"));
                            stationModel.setLatitude(mJSONObjectStation.getDouble("latitude"));
                            stationModel.setLongitude(mJSONObjectStation.getDouble("longitude"));
                            stationModel.setStationName(mJSONObjectStation.getString("stationname"));
                            stationModel.setMapInstruction(mJSONObjectStation.getString("mapinstruction"));
                            stationModel.setWayPoints(mJSONObjectStation.getJSONArray("waypoints"));

                        }
                    }




        } catch (Exception e) {
            e.printStackTrace();
        }
        return stationModel;

    }
}

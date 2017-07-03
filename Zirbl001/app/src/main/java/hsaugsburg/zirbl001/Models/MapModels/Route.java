package hsaugsburg.zirbl001.Models.MapModels;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.List;

public class Route implements Serializable {

    //public String endAddress;
    //public LatLng endLocation;
    //public String startAddress;
    //public LatLng startLocation;

    private List<LatLng> points;

    public List<LatLng> getPoints() {
        return points;
    }

    public void setPoints(List<LatLng> points) {
        this.points = points;
    }
}
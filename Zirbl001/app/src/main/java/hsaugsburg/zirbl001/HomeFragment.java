package hsaugsburg.zirbl001;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

public class HomeFragment extends Fragment implements Callback{
    private FrameLayout fl;
    private String title;
    private int page;

    public static HomeFragment newInstance(int page, String title) {
        HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        homeFragment.setArguments(args);
        return homeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        new JSONTourSelection(this).execute("http://zirbl.multimedia.hs-augsburg.de/selectTourSelectionView.php");
        fl = (FrameLayout) inflater.inflate(R.layout.fragment_home, container, false);

        return fl;
    }

    public void processData(List<JSONModel> result) {
        for (int i = 0; i < result.size(); i++) {
            if (((TourSelectionModel) result.get(i)).getTourName().equals("Fugger")) {
                TextView tourName = (TextView) fl.findViewById(R.id.title);
                tourName.setText(((TourSelectionModel) result.get(i)).getTourName());

                TextView duration = (TextView) fl.findViewById(R.id.durationText);


                duration.setText(Integer.toString(((TourSelectionModel) result.get(i)).getDuration())  + " min");


                TextView distance = (TextView) fl.findViewById(R.id.distanceText);
                double dist = ((TourSelectionModel)result.get(i)).getDistance() / 1000.0;
                distance.setText(Double.toString(dist) + " km");

                TextView difficultyName = (TextView) fl.findViewById(R.id.difficultyText);
                difficultyName.setText(((TourSelectionModel) result.get(i)).getDifficultyName());
            }
        }

    }
}
package hsaugsburg.zirbl001;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import static hsaugsburg.zirbl001.R.id.container;

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

        new JSONTourSelection(this).execute("http://zirbl.multimedia.hs-augsburg.de/selectTourSelectionView.php");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fl = (FrameLayout) inflater.inflate(R.layout.fragment_home, container, false);

        return fl;
    }

    public void processData(List<JSONModel> result) {
        for (int i = 0; i < result.size(); i++) {
            if (((TourSelectionModel) result.get(i)).getTourName().equals("Fugger")) {
                TextView tourName = (TextView) fl.findViewById(R.id.title);
                tourName.setText(((TourSelectionModel) result.get(0)).getTourName());

                TextView duration = (TextView) fl.findViewById(R.id.watchText);


                duration.setText(Integer.toString(((TourSelectionModel) result.get(0)).getDuration()));


                TextView distance = (TextView) fl.findViewById(R.id.streetText);
                distance.setText(Integer.toString(((TourSelectionModel) result.get(0)).getDistance()));

                TextView difficultyName = (TextView) fl.findViewById(R.id.weightText);
                difficultyName.setText(((TourSelectionModel) result.get(0)).getDifficultyName());
            }
        }

    }
}
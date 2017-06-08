package hsaugsburg.zirbl001;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

public class TourDetailFragment extends Fragment implements Callback{
    private FrameLayout fl;
    private String title;
    private int page;

    public static TourDetailFragment newInstance(int page, String title) {
        TourDetailFragment tourDetailFragment = new TourDetailFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        tourDetailFragment.setArguments(args);
        return tourDetailFragment;
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
        //return inflater.inflate(R.layout.fragment_tourdetail, container, false);
        new JSONTourDetail(this).execute("http://zirbl.multimedia.hs-augsburg.de/selectTourDetailsView.php");
        fl = (FrameLayout) inflater.inflate(R.layout.fragment_tourdetail, container, false);

        return fl;
    }


    public void processData(List<JSONModel> result) {

        for (int i = 0; i < result.size(); i++) {
            if (((TourDetailModel)result.get(i)).getTourName().equals("Fugger")) {
                ((BaseActivity) getActivity()).setActionBarTitle(((TourDetailModel) result.get(i)).getTourName());
                TextView duration = (TextView) fl.findViewById(R.id.durationText);
                duration.setText(Integer.toString(((TourDetailModel)result.get(i)).getDuration()));

                TextView distance = (TextView) fl.findViewById(R.id.distanceText);
                distance.setText(Integer.toString(((TourDetailModel)result.get(i)).getDistance()));

                TextView difficultyName = (TextView) fl.findViewById(R.id.difficultyText);
                difficultyName.setText(((TourDetailModel)result.get(i)).getDifficultyName());

                TextView description = (TextView) fl.findViewById(R.id.textView);
                description.setText(((TourDetailModel)result.get(i)).getDescription());
            }
        }

    }
}

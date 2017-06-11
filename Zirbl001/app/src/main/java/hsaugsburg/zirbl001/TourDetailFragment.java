package hsaugsburg.zirbl001;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class TourDetailFragment extends Fragment implements Callback {
    private FrameLayout fl;
    private String title;
    private int page;

    public static TourDetailFragment newInstance(int page, String title) {
        TourDetailFragment tourDetailFragment = new TourDetailFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        tourDetailFragment.setArguments(args);
        Log.d("Test", "TourDetailnewInstance");
        return tourDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
        Log.d("Test", "onCreateTourDetail");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (((BaseActivity) getActivity()).getCurrentTabbarItem() == 5) {
            new JSONTourDetail(this).execute("http://zirbl.multimedia.hs-augsburg.de/selectTourDetailsView.php");
        }
        fl = (FrameLayout) inflater.inflate(R.layout.fragment_tourdetail, container, false);

        Log.d("TestTourDetailFragment", "ausgeführt");
        return fl;

    }


    public void processData(List<JSONModel> result) {
        int tourID = ((BaseActivity) getActivity()).getSelectedTourID();


        ((BaseActivity) getActivity()).setActionBarTitle(((TourDetailModel) result.get(tourID)).getTourName());


        TextView duration = (TextView) fl.findViewById(R.id.durationText);
        duration.setText(Integer.toString(((TourDetailModel) result.get(tourID)).getDuration()) + " min");

        TextView distance = (TextView) fl.findViewById(R.id.distanceText);
        double dist = ((TourDetailModel) result.get(tourID)).getDistance() / 1000.0;
        distance.setText(Double.toString(dist) + " km");

        TextView difficultyName = (TextView) fl.findViewById(R.id.difficultyText);
        difficultyName.setText(((TourDetailModel) result.get(tourID)).getDifficultyName());

        TextView description = (TextView) fl.findViewById(R.id.textView);
        description.setText(((TourDetailModel) result.get(tourID)).getDescription());

        String mainPictureURL = ((TourDetailModel)result.get(tourID)).getMainPicture();
        //Log.d("TestMainPicture", mainPictureURL);
        new DownloadImageTask((ImageView) fl.findViewById(R.id.image)).execute(mainPictureURL);


    }
}

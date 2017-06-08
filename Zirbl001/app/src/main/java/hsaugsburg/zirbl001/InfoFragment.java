package hsaugsburg.zirbl001;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

public class InfoFragment extends Fragment implements Callback{
    private FrameLayout fl;
    private String title;
    private int page;

    public static InfoFragment newInstance(int page, String title) {
        InfoFragment infoFragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        infoFragment.setArguments(args);
        return infoFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");

        new JSONTourDetail(this).execute("http://zirbl.multimedia.hs-augsburg.de/selectTourDetailsView.php");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_info, container, false);
        fl = (FrameLayout) inflater.inflate(R.layout.fragment_info, container, false);

        return fl;
    }


    public void processData(List<JSONModel> result) {

        for (int i = 0; i < result.size(); i++) {
            if (((TourDetailModel)result.get(i)).getTourName().equals("Fugger")) {
                ((BaseActivity) getActivity()).setActionBarTitle(((TourDetailModel) result.get(i)).getTourName());
                TextView duration = (TextView) fl.findViewById(R.id.watchText);
                duration.setText(Integer.toString(((TourDetailModel)result.get(i)).getDuration()));

                TextView distance = (TextView) fl.findViewById(R.id.streetText);
                distance.setText(Integer.toString(((TourDetailModel)result.get(i)).getDistance()));

                TextView difficultyName = (TextView) fl.findViewById(R.id.weightText);
                difficultyName.setText(((TourDetailModel)result.get(i)).getDifficultyName());

                TextView description = (TextView) fl.findViewById(R.id.textView);
                description.setText(((TourDetailModel)result.get(i)).getDescription());
            }
        }

    }
}

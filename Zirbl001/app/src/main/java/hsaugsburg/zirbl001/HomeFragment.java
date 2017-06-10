package hsaugsburg.zirbl001;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.List;


public class HomeFragment extends Fragment implements Callback{
    private FrameLayout fl;
    private String title;
    private int page;
    private ListView mListView;
    private Activity context;

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

        context = getActivity();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        new JSONTourSelection(this).execute("http://zirbl.multimedia.hs-augsburg.de/selectTourSelectionView.php");
        fl = (FrameLayout) inflater.inflate(R.layout.fragment_home, container, false);
        mListView = (ListView)fl.findViewById(R.id.home_list_view);

        return fl;
    }

    public void processData(List<JSONModel> result) {

        TourSelectionAdapter adapter = new TourSelectionAdapter(getActivity(), result);
        mListView.setAdapter(adapter);
        final List<JSONModel> tourSelectionItems = result;

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONModel selectedTour = tourSelectionItems.get(position);

                ((BaseActivity)getActivity()).changeToInfo(((TourSelectionModel)selectedTour).getTourID());
            }
        });




    }
}

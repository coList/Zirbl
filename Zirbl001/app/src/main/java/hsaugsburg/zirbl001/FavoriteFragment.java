package hsaugsburg.zirbl001;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

public class FavoriteFragment extends Fragment {
    private ListView mListView;
    private String title;
    private int page;
    private FrameLayout fl;

    public static FavoriteFragment newInstance(int page, String title) {
        FavoriteFragment fragmentFavorite = new FavoriteFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFavorite.setArguments(args);
        return fragmentFavorite;
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
        fl = (FrameLayout) inflater.inflate(R.layout.fragment_home, container, false);
        return fl;
    }
}

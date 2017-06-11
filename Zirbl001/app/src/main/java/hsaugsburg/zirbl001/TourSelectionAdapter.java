package hsaugsburg.zirbl001;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;


public class TourSelectionAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<JSONModel> mDataSource;

    public TourSelectionAdapter(Context context, List<JSONModel> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //1
    @Override
    public int getCount() {
        return mDataSource.size();
    }

    //2
    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    //3
    @Override
    public long getItemId(int position) {
        return position;
    }

    //4
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.list_item_tourselection, parent, false);
        TextView tourName = (TextView) rowView.findViewById(R.id.title);

        TextView duration = (TextView) rowView.findViewById(R.id.durationText);
        TextView distance = (TextView) rowView.findViewById(R.id.distanceText);
        TextView difficultyName = (TextView) rowView.findViewById(R.id.difficultyText);


        TourSelectionModel tourSelection = (TourSelectionModel) getItem(position);
        tourName.setText(tourSelection.getTourName());
        duration.setText(Integer.toString(tourSelection.getDuration()) + " min");
        double dist = tourSelection.getDistance() / 1000.0;
        distance.setText(Double.toString(dist) + " km");
        difficultyName.setText(tourSelection.getDifficultyName());

        new DownloadImageTask((ImageView) rowView.findViewById(R.id.imageView)).execute(tourSelection.getMainpicture());

        return rowView;

    }
}
package hsaugsburg.zirbl001.Datamanagement;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.util.List;

import hsaugsburg.zirbl001.Interfaces.JSONModel;
import hsaugsburg.zirbl001.Models.TourSelectionModel;
import hsaugsburg.zirbl001.R;


public class TourSelectionAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<JSONModel> mDataSource;
    private ImageLoader imageLoader;

    public static final String GLOBAL_VALUES = "globalValuesFile";
    String serverName;


    public TourSelectionAdapter(Context context, List<JSONModel> items, ImageLoader imageLoader) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        SharedPreferences globalValues = context.getSharedPreferences(GLOBAL_VALUES, 0);
        serverName = globalValues.getString("serverName", null);

        this.imageLoader = imageLoader;
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

        //new DownloadImageTask((ImageView) rowView.findViewById(R.id.imageView)).execute(tourSelection.getMainpicture());
        ImageView mainPicture = (ImageView)rowView.findViewById(R.id.imageView);
        ImageLoader.getInstance().displayImage(serverName + tourSelection.getMainpicture(), mainPicture);
        return rowView;

    }
}
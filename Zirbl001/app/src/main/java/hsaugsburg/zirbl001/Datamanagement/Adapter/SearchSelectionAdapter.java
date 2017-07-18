package hsaugsburg.zirbl001.Datamanagement.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hsaugsburg.zirbl001.Interfaces.JSONModel;
import hsaugsburg.zirbl001.Models.NavigationModels.SearchModel;
import hsaugsburg.zirbl001.Models.NavigationModels.TourDetailModel;
import hsaugsburg.zirbl001.R;


public class SearchSelectionAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<SearchModel> originalData;
    private List<SearchModel> filteredData;
    private static final String TAG = "SearchSelectionAdapter";

    public static final String GLOBAL_VALUES = "globalValuesFile";


    public SearchSelectionAdapter(Context context, List<SearchModel> items) {
        mContext = context;
        originalData = items;
        filteredData = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        SharedPreferences globalValues = context.getSharedPreferences(GLOBAL_VALUES, 0);

    }

    //1
    @Override
    public int getCount() {
        return filteredData.size();
    }

    //2
    @Override
    public Object getItem(int position) {
        return filteredData.get(position);
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
        View rowView = mInflater.inflate(R.layout.list_item_searchselection, parent, false);
        TextView tourName = (TextView) rowView.findViewById(R.id.tourTitle);

        TextView duration = (TextView) rowView.findViewById(R.id.durationText);
        TextView distance = (TextView) rowView.findViewById(R.id.distanceText);
        TextView difficultyName = (TextView) rowView.findViewById(R.id.difficultyText);
        TextView descriptionShort = (TextView) rowView.findViewById(R.id.descriptionShort);


        SearchModel searchModel = (SearchModel) getItem(position);
        tourName.setText(searchModel.getTourName());

        descriptionShort.setText(fromHtml(searchModel.getShortDescription()));

        duration.setText(Integer.toString(searchModel.getDuration()) + " min");
        double dist = searchModel.getDistance() / 1000.0;
        distance.setText(Double.toString(dist) + " km");
        difficultyName.setText(searchModel.getDifficultyName());


        tourName.setTextColor(ContextCompat.getColor(mContext, R.color.colorStandardText));
        tourName.setTextSize(30);
        duration.setTextColor(ContextCompat.getColor(mContext, R.color.colorStandardText));
        distance.setTextColor(ContextCompat.getColor(mContext, R.color.colorStandardText));
        difficultyName.setTextColor(ContextCompat.getColor(mContext, R.color.colorStandardText));

        return rowView;

    }


    @Override
    public Filter getFilter()
    {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence)
            {
                FilterResults results = new FilterResults();

                //If there's nothing to filter on, return the original data for your list
                if(charSequence == null || charSequence.length() == 0)
                {
                    results.values = originalData;
                    results.count = originalData.size();
                }
                else
                {
                    List<SearchModel> filterResultsData = new ArrayList<>();

                    for(SearchModel data : originalData)
                    {
                        SearchModel searchModel = (SearchModel) data;

<<<<<<< HEAD
                        if((tourDetail.getTourName()).toLowerCase().contains(charSequence.toString().toLowerCase()) ||
                                tourDetail.getDescription().toLowerCase().contains(charSequence.toString().toLowerCase()) ||
                                tourDetail.getCategoryName().toLowerCase().contains(charSequence.toString().toLowerCase()))
=======
                        if((searchModel.getTourName()).toLowerCase().contains(charSequence.toString().toLowerCase()))
>>>>>>> master
                        {
                            filterResultsData.add(data);
                        }
                    }

                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults)
            {
                filteredData = (List<SearchModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

}
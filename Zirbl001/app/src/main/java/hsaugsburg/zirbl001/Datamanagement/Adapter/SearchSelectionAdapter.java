package hsaugsburg.zirbl001.Datamanagement.Adapter;

import android.content.Context;
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import hsaugsburg.zirbl001.Models.NavigationModels.SearchModel;
import hsaugsburg.zirbl001.R;

public class SearchSelectionAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<SearchModel> originalData;
    private List<SearchModel> filteredData;

    public SearchSelectionAdapter(Context context, List<SearchModel> items) {
        mContext = context;
        originalData = items;
        filteredData = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = mInflater.inflate(R.layout.list_item_searchselection, parent, false);
        TextView tourName = (TextView) rowView.findViewById(R.id.tourTitle);

        TextView duration = (TextView) rowView.findViewById(R.id.durationText);
        TextView distance = (TextView) rowView.findViewById(R.id.distanceText);
        TextView difficultyName = (TextView) rowView.findViewById(R.id.difficultyText);
        TextView descriptionShort = (TextView) rowView.findViewById(R.id.descriptionShort);

        SearchModel searchModel = (SearchModel) getItem(position);
        tourName.setText(searchModel.getTourName());

        descriptionShort.setText(fromHtml(searchModel.getShortDescription()));

        duration.setText(String.format(Locale.GERMANY, "%d min", searchModel.getDuration()));
        double dist = searchModel.getDistance() / 1000.0;
        NumberFormat nf = new DecimalFormat("##.##");
        nf.format(dist);
        distance.setText(dist + " km");
        difficultyName.setText(searchModel.getDifficultyName());

        tourName.setTextColor(ContextCompat.getColor(mContext, R.color.colorStandardText));
        tourName.setTextSize(30);
        duration.setTextColor(ContextCompat.getColor(mContext, R.color.colorStandardText));
        distance.setTextColor(ContextCompat.getColor(mContext, R.color.colorStandardText));
        difficultyName.setTextColor(ContextCompat.getColor(mContext, R.color.colorStandardText));

        return rowView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();

                //If there's nothing to filter on, return the original data for your list
                if(charSequence == null || charSequence.length() == 0) {
                    results.values = originalData;
                    results.count = originalData.size();
                } else {
                    List<SearchModel> filterResultsData = new ArrayList<>();

                    for(SearchModel data : originalData) {
                        SearchModel searchModel = (SearchModel) data;

                        if((searchModel.getTourName()).toLowerCase().contains(charSequence.toString().toLowerCase()) ||
                                searchModel.getShortDescription().toLowerCase().contains(charSequence.toString().toLowerCase()) ||
                                searchModel.getCategoryName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            filterResultsData.add(data);
                        }
                    }
                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredData = (List<SearchModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }
}
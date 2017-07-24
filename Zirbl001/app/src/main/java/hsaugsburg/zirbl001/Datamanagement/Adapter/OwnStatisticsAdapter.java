package hsaugsburg.zirbl001.Datamanagement.Adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import hsaugsburg.zirbl001.Models.NavigationModels.OwnStatisticsModel;
import hsaugsburg.zirbl001.R;

public class OwnStatisticsAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<OwnStatisticsModel> mDataSource;

    public OwnStatisticsAdapter(Context context, List<OwnStatisticsModel> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = mInflater.inflate(R.layout.list_item_own_statistic, parent, false);
        TextView tourName = (TextView) rowView.findViewById(R.id.titleOfStatistic);
        tourName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
        tourName.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
        TextView participationDate = (TextView) rowView.findViewById(R.id.date);
        TextView duration = (TextView) rowView.findViewById(R.id.textUsedTime);
        TextView ranking = (TextView) rowView.findViewById(R.id.textPlacement);
        TextView teamname = (TextView) rowView.findViewById(R.id.teamName);
        TextView participants = (TextView) rowView.findViewById(R.id.membersOfTeam);
        TextView score = (TextView) rowView.findViewById(R.id.statPoints);

        OwnStatisticsModel ownStatisticsModel = (OwnStatisticsModel) getItem(position);

        tourName.setText(ownStatisticsModel.getTourName());

        String strCurrentDate = ownStatisticsModel.getParticipationDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);
        String date = "";

        try {

            Date newDate = format.parse(strCurrentDate);
            format = new SimpleDateFormat("dd. MMMM yyyy", Locale.GERMANY);
            date = format.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        participationDate.setText(date);

        int totalTime = ownStatisticsModel.getDuration();
        String time = String.format(Locale.GERMANY, "%d h %d min",
                TimeUnit.MILLISECONDS.toHours(totalTime),
                TimeUnit.MILLISECONDS.toMinutes(totalTime) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totalTime))
        );
        duration.setText(time);
        String rankingText = ownStatisticsModel.getRank() + ". Platz von " + ownStatisticsModel.getTotalParticipations();
        ranking.setText(rankingText);
        teamname.setText(String.format(Locale.GERMANY, "%s:", ownStatisticsModel.getGroupName()));

        String participantsViewText = "";
        for (String participant: ownStatisticsModel.getParticipants()) {
            participantsViewText += participant + ", ";
        }
        participantsViewText = participantsViewText.substring(0, participantsViewText.length() - 2);
        participants.setText(participantsViewText);

        score.setText(String.format(Locale.GERMANY, "%d", ownStatisticsModel.getScore()));

        return rowView;
    }
}
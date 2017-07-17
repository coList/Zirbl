package hsaugsburg.zirbl001.Datamanagement.Adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import hsaugsburg.zirbl001.Models.NavigationModels.ClassStatisticsModel;
import hsaugsburg.zirbl001.Models.NavigationModels.ClassesStatModel;
import hsaugsburg.zirbl001.R;


public class ClassStatisticsAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<ClassesStatModel> mDataSource;



    public ClassStatisticsAdapter(Context context, List<ClassesStatModel> items) {
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
        final View rowView = mInflater.inflate(R.layout.list_item_class_statistic, parent, false);
        rowView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                    LinearLayout gonePart = (LinearLayout) rowView.findViewById(R.id.gonePart);

                    if (gonePart.getVisibility() == View.GONE) {
                        gonePart.setVisibility(View.VISIBLE);
                    } else {
                        gonePart.setVisibility(View.GONE);
                    }

            }
        });
        TextView tourName = (TextView) rowView.findViewById(R.id.titleOfStatistic);
        tourName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
        tourName.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
        TextView participationDate = (TextView) rowView.findViewById(R.id.dateOfTour);
        TextView school = (TextView) rowView.findViewById(R.id.school);

        ClassesStatModel classesStatModel = (ClassesStatModel) getItem(position);

        ClassStatisticsModel firstClassStatisticModel = classesStatModel.getClassStatisticsModels().get(0);
        tourName.setText(firstClassStatisticModel.getTourName());

        String strCurrentDate = firstClassStatisticModel.getParticipationDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = "";
        try {

            Date newDate = format.parse(strCurrentDate);
            Calendar cal = Calendar.getInstance();
            format = new SimpleDateFormat("dd. MMMM yyyy");
            date = format.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        participationDate.setText(date);

        String schoolText = "Klasse " + firstClassStatisticModel.getClassName() + ", " + firstClassStatisticModel.getSchoolName();
        school.setText(schoolText);



        LinearLayout layout = (LinearLayout) rowView.findViewById(R.id.gonePart);
        for (int i = 0; i < classesStatModel.getClassStatisticsModels().size(); i++) {

            ClassStatisticsModel classStatisticsModel = classesStatModel.getClassStatisticsModels().get(i);

            View teamStatistic = mInflater.inflate(R.layout.list_element_team_statistic, null);
            TextView teamName = (TextView) teamStatistic.findViewById(R.id.teamName);
            TextView participants = (TextView) teamStatistic.findViewById(R.id.membersOfTeam);
            TextView ranking = (TextView) teamStatistic.findViewById(R.id.rankingText);
            ranking.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            TextView duration = (TextView) teamStatistic.findViewById(R.id.durationText);
            duration.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            TextView score = (TextView) teamStatistic.findViewById(R.id.scoreText);
            score.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

            teamName.setText(classStatisticsModel.getGroupName());

            String participantsViewText = "";
            for (String participant: classStatisticsModel.getParticipants()) {
                participantsViewText += participant + ", ";
            }
            participantsViewText = participantsViewText.substring(0, participantsViewText.length() - 2);
            participants.setText(participantsViewText);

            ranking.setText(classStatisticsModel.getClassRanking() + ". Platz");

            int totalTime = classStatisticsModel.getDuration();
            String time = String.format("%d h %d min",
                    TimeUnit.MILLISECONDS.toHours(totalTime),
                    TimeUnit.MILLISECONDS.toMinutes(totalTime) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totalTime))
            );
            duration.setText(time);

            score.setText(Integer.toString(classStatisticsModel.getScore()));


            layout.addView(teamStatistic);


        }
        return rowView;

    }
}
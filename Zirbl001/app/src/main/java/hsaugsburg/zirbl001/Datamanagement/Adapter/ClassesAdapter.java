package hsaugsburg.zirbl001.Datamanagement.Adapter;

import android.content.Context;
import android.content.Intent;
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

import hsaugsburg.zirbl001.Models.NavigationModels.ClassesModel;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.TourActivities.GenerateQrCodeActivity;


public class ClassesAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<ClassesModel> mDataSource;


    public ClassesAdapter(Context context, List<ClassesModel> items) {
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
        View rowView = mInflater.inflate(R.layout.list_item_saved_qrcode, parent, false);

        TextView className = (TextView) rowView.findViewById(R.id.savedClass);
        TextView schoolName = (TextView) rowView.findViewById(R.id.savedSchool);
        TextView creationDate = (TextView) rowView.findViewById(R.id.savedDate);

        final ClassesModel classesModel = (ClassesModel) getItem(position);

        rowView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(mContext, GenerateQrCodeActivity.class);
                intent.putExtra("tourID", Integer.toString(classesModel.getTourID()));
                intent.putExtra("tourName", classesModel.getTourName());
                intent.putExtra("school", classesModel.getSchoolname());
                intent.putExtra("className", classesModel.getClassname());
                intent.putExtra("qrCode", classesModel.getQrCode());
                mContext.startActivity(intent);
            }
        });

        String classNameText = "Klasse " + classesModel.getClassname();
        className.setText(classNameText);
        schoolName.setText(classesModel.getSchoolname());

        String strCurrentDate = classesModel.getCreationDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);
        String date = "";
        try {

            Date newDate = format.parse(strCurrentDate);
            format = new SimpleDateFormat("dd. MMMM yyyy", Locale.GERMANY);
            date = format.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        creationDate.setText(date);

        return rowView;
    }
}
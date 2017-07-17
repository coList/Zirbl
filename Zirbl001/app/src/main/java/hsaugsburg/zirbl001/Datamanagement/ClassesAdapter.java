package hsaugsburg.zirbl001.Datamanagement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.google.zxing.FormatException;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import hsaugsburg.zirbl001.Interfaces.JSONModel;
import hsaugsburg.zirbl001.Models.ClassesModel;
import hsaugsburg.zirbl001.Models.OwnStatisticsModel;
import hsaugsburg.zirbl001.Models.TourSelectionModel;
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


        className.setText(classesModel.getClassname());
        schoolName.setText(classesModel.getSchoolname());

        String strCurrentDate = classesModel.getCreationDate();
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
        creationDate.setText(date);

        return rowView;

    }
}
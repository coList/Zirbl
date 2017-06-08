package hsaugsburg.zirbl001;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

public class QrFragment extends Fragment {


    TextView barcodeResult;
    private String title;
    private int page;

    public static QrFragment newInstance(int page, String title) {
        QrFragment qrFavorite = new QrFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        qrFavorite.setArguments(args);
        return qrFavorite;
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

        if (container == null) {
            return null;
        }

        RelativeLayout rr = (RelativeLayout )inflater.inflate(R.layout.fragment_qr, container, false);
        barcodeResult = (TextView) rr.findViewById(R.id.barcode_result);

        Button button = (Button) rr.findViewById(R.id.scan_barcode);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), ScanBarcodeActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        return rr;

    }


    @Override
    public  void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(requestCode==0){
            if(resultCode == CommonStatusCodes.SUCCESS){
                if(data != null) {
                    Barcode barcode = data.getParcelableExtra("barcode");
                    barcodeResult.setText("Barcode value : "+ barcode.displayValue);
                } else {
                    barcodeResult.setText("Ich nix finden");
                }
            }


        } else {

            super.onActivityResult(requestCode, resultCode, data);
        }

    }
}
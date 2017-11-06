package hsaugsburg.zirbl001.TourActivities;

        import android.Manifest;
        import android.graphics.Bitmap;
        import android.graphics.Color;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.DisplayMetrics;
        import android.util.Log;
        import android.view.Display;
        import android.view.MotionEvent;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.ImageButton;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.TableRow;

        import hsaugsburg.zirbl001.R;

public class PictureCountdownActivity extends AppCompatActivity {


    int n = 30;
    TableRow[] rowForPixels = new TableRow[n];
    ImageView[][]  colorField = new ImageView[n][n];

    ImageView pic;
    Bitmap bit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_countdown);

        pic = (ImageView) findViewById(R.id.pic);

        pic.setDrawingCacheEnabled(true);
        pic.buildDrawingCache(true);

        final ImageButton whiteBtn = (ImageButton) findViewById(R.id.whiteBtn);
        whiteBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                whiteBtn.setBackgroundColor(Color.TRANSPARENT);
                bit = pic.getDrawingCache();
                int height = bit.getHeight();
                int width = bit.getWidth();

                LinearLayout pixelMap = (LinearLayout) findViewById(R.id.pixelMap);
                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 0);
                rowParams.weight = 1;
                TableRow.LayoutParams pixelParams = new TableRow.LayoutParams(
                        0, ViewGroup.LayoutParams.MATCH_PARENT);
                pixelParams.weight = 1;

                for(int a = 0; a<n; a++){
                    rowForPixels[a] = new TableRow(getApplicationContext());
                    rowForPixels[a].setBackgroundColor(Color.RED);
                    pixelMap.addView(rowForPixels[a], rowParams);
                    for(int c = 0; c<n; c++){
                        colorField[a][c] = new ImageView(getApplicationContext());
                        rowForPixels[a].addView(colorField[a][c], pixelParams);
                    }
                }

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

                int [] xCoordinate = new int[n];
                int [] yCoordinate = new int[n];
                int [][] pixel = new int[n][n];
                int [][] r = new int[n][n];
                int [][] g = new int[n][n];
                int [][] b = new int[n][n];

                int denom = 1;
                for(int i = 0; i<n; i++){
                    xCoordinate[i] = (width/(n*2))*denom;
                    yCoordinate[i] = (height/(n*2))*denom;
                    denom += 2;
                }
                denom = 1;

                for(int a = 0; a<n; a++){
                    for(int c = 0; c<n; c++){
                        pixel[a][c] = bit.getPixel(xCoordinate[c], yCoordinate[a]);
                        r[a][c] = Color.red(pixel[a][c]);
                        g[a][c] = Color.green(pixel[a][c]);
                        b[a][c] = Color.blue(pixel[a][c]);
                        colorField[a][c].setBackgroundColor(Color.rgb(r[a][c], g[a][c], b[a][c]));
                    }
                }
            }
        });
    }
}

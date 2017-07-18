package hsaugsburg.zirbl001.Fonts;

        import android.content.Context;
        import android.graphics.Typeface;
        import android.support.v4.content.ContextCompat;
        import android.util.AttributeSet;
        import android.util.TypedValue;

        import hsaugsburg.zirbl001.R;

public class QuicksandRegularPrimaryEdit extends android.support.v7.widget.AppCompatEditText {

        private Context context;
        private AttributeSet attrs;
        private int defStyle;

        public QuicksandRegularPrimaryEdit(Context context) {
            super(context);
            this.context=context;
            init();
        }

        public QuicksandRegularPrimaryEdit(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.context=context;
            this.attrs=attrs;
            init();
        }

        public QuicksandRegularPrimaryEdit(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            this.context=context;
            this.attrs=attrs;
            this.defStyle=defStyle;
            init();
        }

        private void init() {
            Typeface font=Typeface.createFromAsset(getContext().getAssets(), "fonts/Quicksand-Regular.ttf");
            this.setTypeface(font);
            this.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            this.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        }
}



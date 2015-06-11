package andrzej.example.com.fab;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.nirhart.parallaxscroll.views.ParallaxScrollView;

public class ObservableScrollView extends ParallaxScrollView {

    public interface OnScrollChangedListener {
        void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt);
    }

    public interface OnScrollStoppedListener{
        void onScrollStopped();
    }

    private Runnable scrollerTask;
    private int initialPosition;

    private int newCheck = 100;

    private OnScrollStoppedListener onScrollStoppedListener;

    private OnScrollChangedListener mOnScrollChangedListener;

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        scrollerTask = new Runnable() {

            public void run() {

                int newPosition = getScrollY();
                if (initialPosition - newPosition == 0) {//has stopped

                    if (onScrollStoppedListener != null) {

                        onScrollStoppedListener.onScrollStopped();
                    }
                } else {
                    initialPosition = getScrollY();
                    ObservableScrollView.this.postDelayed(scrollerTask, newCheck);
                }
            }
        };
    }

    public ObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChangedListener != null) {
            mOnScrollChangedListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        mOnScrollChangedListener = listener;
    }

    public void setOnScrollStoppedListener(ObservableScrollView.OnScrollStoppedListener listener){
        onScrollStoppedListener = listener;
    }

    public void startScrollerTask(){

        initialPosition = getScrollY();
        ObservableScrollView.this.postDelayed(scrollerTask, newCheck);
    }

}
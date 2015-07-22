package andrzej.example.com.libraries.expandablelayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import andrzej.example.com.mlpwiki.R;

/**
 * Created by andrzej on 21.07.15.
 */
public class ExpandableRelativeLayout extends RelativeLayout {

    //Configs
    private long animationDuration = 200;

    private boolean expaned = false;

    private View normalPart;
    private View extendedPart;

    private ExpandableLayoutListner mExpandableEventListener;
    private OnExpandableClickListener mOnClickListener;

    Context context;

    public ExpandableRelativeLayout(Context context) {
        super(context);
        this.context = context;
    }

    public ExpandableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void setExpandEventListener(ExpandableLayoutListner mListener) {
        mExpandableEventListener = mListener;
    }

    public void setOnClickListener(OnExpandableClickListener mOnClickListener){
        this.mOnClickListener = mOnClickListener;
    }

    private void expandLayout() {
        expaned = true;
        extendedPart.setVisibility(View.VISIBLE);

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.expand_less_to_expand_more);
        animation.setDuration(animationDuration);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

        });

        extendedPart.startAnimation(animation);

        if (mExpandableEventListener != null)
            mExpandableEventListener.onViewExpand(this, extendedPart, normalPart);
    }


    private void concealLayout() {
        expaned = false;

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.expand_more_to_expand_less);
        animation.setDuration(animationDuration);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                extendedPart.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}

        });
        extendedPart.startAnimation(animation);


        if (mExpandableEventListener != null)
            mExpandableEventListener.onViewCollapse(this, extendedPart, normalPart);

    }

    public void concealOrExpandLayout() {
        if (expaned)
            concealLayout();
        else
            expandLayout();

    }

    public boolean isExpaned() {
        return expaned;
    }

    public void setExpaned(boolean expaned) {
        this.expaned = expaned;

        if (this.expaned)
            extendedPart.setVisibility(View.VISIBLE);
        else
            extendedPart.setVisibility(View.GONE);
    }

    public void setAnimationDuration(long animationDuration) {
        this.animationDuration = animationDuration;
    }

    public void setNormalAndExpandPart(View normalPart, View extendedPart) {
        this.normalPart = normalPart;
        this.extendedPart = extendedPart;

        this.extendedPart.setVisibility(View.GONE);

        this.normalPart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnClickListener!=null)
                    mOnClickListener.onClick(v);
            }
        });
    }

    public View getExtendedPart() {
        return extendedPart;
    }

    public View getNormalPart() {
        return normalPart;
    }
}

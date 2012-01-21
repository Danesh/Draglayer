package com.dragdrop;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.LinearLayout;

/**
 * Allows a linearlayout's children to be dragged/re-arranged
 * The current order of views can be queried at anytime
 * via getIds();
 *
 * @author Danesh M
 */
public class Draglayer extends LinearLayout implements OnTouchListener {

    //Keep track of the order of view ids
    private ArrayList<Integer> mIds = new ArrayList<Integer>();
    //Keeps track of motion
    private float prevX;
    //Scroll Vertically
    private boolean mVertical = true;

    public Draglayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onFinishInflate () {
        setupListeners();
    }

    public ArrayList<Integer> getIds() {
        return mIds;
    }

    private void setupListeners() {
        for (int cc = 0; cc < getChildCount(); cc++) {
            View v = getChildAt(cc);
            mIds.add(v.getId());
            v.setOnTouchListener(this);
        }
    }

    public int findInterceptingViewIndex(float x, View v) {
        int location[] = new int[2];
        int dragLoc[] = new int[2];
        v.getLocationOnScreen(dragLoc);
        float vLeftEdge = dragLoc[0];
        float vRightEdge = dragLoc[0] + v.getWidth();
        float vTopEdge = dragLoc[1];
        float vBottomEdge = dragLoc[1] + v.getHeight();
        for (int cc = 0; cc < mIds.size(); cc++) {
            View tmpV = findViewById(mIds.get(cc));
            tmpV.getLocationOnScreen(location);
            float cLeftEdge = location[0];
            float cRightEdge = location[0] + tmpV.getWidth();
            float cTopEdge = location[1];
            float cBottomEdge = location[1] + tmpV.getHeight();
            int VIEW_SCALE_HEIGHT = tmpV.getHeight()/2;
            int VIEW_SCALE_WIDTH = tmpV.getWidth()/2;
            if (tmpV == v) {
                continue;
            } else if (!mVertical) {
                if ((vRightEdge > cLeftEdge + VIEW_SCALE_WIDTH) && (vRightEdge < cRightEdge)) {
                    return cc;
                }
                if ((vLeftEdge < cRightEdge - VIEW_SCALE_WIDTH) && (vLeftEdge > cLeftEdge)) {
                    return cc;
                }
            } else if (mVertical) {
                if ((vBottomEdge > cTopEdge + VIEW_SCALE_HEIGHT) && (cBottomEdge > vBottomEdge)) {
                    return cc;
                }
                if ((vTopEdge < cBottomEdge - VIEW_SCALE_HEIGHT) && (vTopEdge > cTopEdge)) {
                    return cc;
                }
            }
        }
        return -1;
    }

    public boolean onTouch(View view, MotionEvent event) {
        float curPos = event.getRawX();
        if (mVertical) {
            curPos = event.getRawY();
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int secondLoc[] = new int[2];
            view.getLocationOnScreen(secondLoc);
            setTag(Float.valueOf(secondLoc[0]));
            setTag(getId(),Math.abs(curPos - secondLoc[0]));
            if (mVertical) {
                setTag(Float.valueOf(secondLoc[1]));
                setTag(getId(),Math.abs(curPos - secondLoc[1]));
            }
            prevX = curPos;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            view.bringToFront();
            int secondLoc[] = new int[2];
            view.getLocationOnScreen(secondLoc);
            if (!mVertical && (((secondLoc[0] + view.getWidth()) > (getLeft() + getWidth()) && curPos > prevX) || (secondLoc[0] < getLeft()) && curPos < prevX)) {
                return true;
            }
            if (mVertical && ((secondLoc[1] + view.getHeight()) > (getTop() + getHeight() + getBarHeight())) || (secondLoc[1] < getTop() + getBarHeight())) {
                return true;
            }
            if (!mVertical) {
                view.setX(curPos - getLeft() - (Float)getTag(getId()));
            } else {
                view.setY(curPos - getTop() - getBarHeight() - (Float)getTag(getId()));
            }
            int viewPosition = mIds.indexOf(view.getId());
            if (viewPosition == -1 || (curPos > prevX && viewPosition == mIds.size()-1) || (curPos < prevX && viewPosition == 0)) {
                return true;
            }
            int affectedViewPosition = findInterceptingViewIndex(curPos, view);
            if (affectedViewPosition == -1) {
                return true;
            }
            switchId(viewPosition, affectedViewPosition, view);
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            if (!mVertical) {
                view.setX((Float) getTag() - getLeft());
            } else {
                view.setY((Float) getTag() - getTop() - getBarHeight());
            }
        }
        prevX = curPos;
        return true;
    }

    public int getBarHeight() {
        Window w = ((Activity) getContext()).getWindow();
        return w.findViewById(Window.ID_ANDROID_CONTENT).getTop();
    }

    private void switchId(int from, int to, View view) {
        View toView = findViewById(mIds.get(to));
        int secondLoc[] = new int[2];
        toView.getLocationOnScreen(secondLoc);
        if (!mVertical) {
            toView.setX((Float) getTag() - getLeft());
            setTag(Float.valueOf(secondLoc[0]));
        } else {
            toView.setY((Float) getTag() - getTop() - getBarHeight());
            setTag(Float.valueOf(secondLoc[1]));
        }
        Collections.swap(mIds,to,from);
    }
}

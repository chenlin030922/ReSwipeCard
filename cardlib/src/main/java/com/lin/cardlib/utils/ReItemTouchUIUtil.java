 

package com.lin.cardlib.utils;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.view.View;

 
public interface ReItemTouchUIUtil {

     
    void onDraw(Canvas c, RecyclerView recyclerView, View view,
                float dX, float dY, int actionState, boolean isCurrentlyActive);

     
    void onDrawOver(Canvas c, RecyclerView recyclerView, View view,
                    float dX, float dY, int actionState, boolean isCurrentlyActive);

     
    void clearView(View view);

     
    void onSelected(View view);
}


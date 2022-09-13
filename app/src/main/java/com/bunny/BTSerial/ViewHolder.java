/**                     
    * Project:  BTSerial
    * JDK version used: <JDK1.8>
    * Author： Bunny     Github: https://github.com/bunny-chz/
    * Create Date：2022-05-11
    * Version: 1.0
    */

package com.bunny.BTSerial;

import android.util.SparseArray;
import android.view.View;

public class ViewHolder {

    public static <T extends View> T getView(View convertView, int childViewId) {
        SparseArray<View> viewHolder = (SparseArray<View>) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<>();
            convertView.setTag(viewHolder);
        }

        View childView = viewHolder.get(childViewId);
        if (childView == null) {
            childView = convertView.findViewById(childViewId);
            viewHolder.put(childViewId, childView);
        }

        return (T) childView;
    }
}

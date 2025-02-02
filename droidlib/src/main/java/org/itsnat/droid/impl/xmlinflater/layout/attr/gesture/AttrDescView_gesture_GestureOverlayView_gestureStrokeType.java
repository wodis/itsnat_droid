package org.itsnat.droid.impl.xmlinflater.layout.attr.gesture;

import android.gesture.GestureOverlayView;
import android.view.View;

import org.itsnat.droid.impl.util.MapSmart;
import org.itsnat.droid.impl.xmlinflater.layout.AttrLayoutContext;
import org.itsnat.droid.impl.xmlinflater.layout.classtree.ClassDescViewBased;
import org.itsnat.droid.impl.xmlinflater.shared.attr.AttrDescReflecMethodNameSingle;

/**
 * Created by jmarranz on 30/04/14.
 */
public class AttrDescView_gesture_GestureOverlayView_gestureStrokeType extends AttrDescReflecMethodNameSingle<Integer,ClassDescViewBased,View,AttrLayoutContext>
{
    public static final MapSmart<String,Integer> valueMap = MapSmart.<String,Integer>create( 2 );
    static
    {
        valueMap.put("single", GestureOverlayView.GESTURE_STROKE_TYPE_SINGLE);
        valueMap.put("multiple",GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);
    }

    public AttrDescView_gesture_GestureOverlayView_gestureStrokeType(ClassDescViewBased parent)
    {
        super(parent,"gestureStrokeType",int.class,valueMap,"single");
    }

}

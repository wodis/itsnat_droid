package org.itsnat.droid.impl.xmlinflater.layout.attr.widget;

import android.view.View;

import org.itsnat.droid.impl.util.MapSmart;
import org.itsnat.droid.impl.xmlinflater.layout.AttrLayoutContext;
import org.itsnat.droid.impl.xmlinflater.layout.classtree.ClassDescViewBased;
import org.itsnat.droid.impl.xmlinflater.shared.attr.AttrDescReflecMethodNameMultiple;

/**
 * Created by jmarranz on 30/04/14.
 */
public class AttrDescView_widget_TextView_autoLink extends AttrDescReflecMethodNameMultiple<ClassDescViewBased,View,AttrLayoutContext>
{
    public static final MapSmart<String,Integer> valueMap = MapSmart.<String,Integer>create(6);
    static
    {
        valueMap.put("none",0x00);
        valueMap.put("web",0x01);
        valueMap.put("email",0x02);
        valueMap.put("phone",0x04);
        valueMap.put("map",0x08);
        valueMap.put("all",0x0f);
    }

    public AttrDescView_widget_TextView_autoLink(ClassDescViewBased parent)
    {
        super(parent,"autoLink","setAutoLinkMask",valueMap,"none");
    }

}

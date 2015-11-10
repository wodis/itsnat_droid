package org.itsnat.droid.impl.xmlinflater.drawable.attr;

import android.graphics.drawable.GradientDrawable;

import org.itsnat.droid.impl.dom.DOMAttr;
import org.itsnat.droid.impl.util.MapSmart;
import org.itsnat.droid.impl.xmlinflater.drawable.AttrDrawableContext;
import org.itsnat.droid.impl.xmlinflater.drawable.classtree.ClassDescDrawable;

/**
 * Created by jmarranz on 30/04/14.
 */
public class AttrDescDrawable_GradientDrawable_shape extends AttrDescDrawableReflecMethodSingleName<Integer, GradientDrawable>
{
    public static final MapSmart<String,Integer> valueMap = MapSmart.<String,Integer>create(4);
    static
    {
        valueMap.put("rectangle",0);
        valueMap.put("oval",1);
        valueMap.put("line",2);
        valueMap.put("ring",3);
    }

    public AttrDescDrawable_GradientDrawable_shape(ClassDescDrawable parent)
    {
        super(parent,"shape",int.class,valueMap);
    }

    @Override
    public void setAttribute(GradientDrawable draw, DOMAttr attr, AttrDrawableContext attrCtx)
    {
        int shape = this.<Integer>parseSingleName(attr.getValue(), valueMap); // Valor concreto no puede ser un recurso

        draw.setShape(shape);
    }
}

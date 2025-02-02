package org.itsnat.droid.impl.xmlinflater.layout.classtree;

import org.itsnat.droid.impl.xmlinflater.layout.ClassDescViewMgr;
import org.itsnat.droid.impl.xmlinflater.layout.attr.widget.AttrDescView_widget_GridView_stretchMode;
import org.itsnat.droid.impl.xmlinflater.shared.GravityUtil;
import org.itsnat.droid.impl.xmlinflater.shared.attr.AttrDescReflecMethodDimensionIntFloor;
import org.itsnat.droid.impl.xmlinflater.shared.attr.AttrDescReflecMethodInt;
import org.itsnat.droid.impl.xmlinflater.shared.attr.AttrDescReflecMethodNameMultiple;

/**
 * Created by jmarranz on 30/04/14.
 */
public class ClassDescView_widget_GridView extends ClassDescViewBased
{
    public ClassDescView_widget_GridView(ClassDescViewMgr classMgr,ClassDescView_widget_AbsListView parentClass)
    {
        super(classMgr,"android.widget.GridView",parentClass);
    }

    @SuppressWarnings("unchecked")
    protected void init()
    {
        super.init();

        addAttrDescAN(new AttrDescReflecMethodDimensionIntFloor(this, "columnWidth", null));
        addAttrDescAN(new AttrDescReflecMethodNameMultiple(this, "gravity", GravityUtil.valueMap, "left"));
        addAttrDescAN(new AttrDescReflecMethodDimensionIntFloor(this, "horizontalSpacing", 0f));
        addAttrDescAN(new AttrDescReflecMethodInt(this, "numColumns", 1));
        addAttrDescAN(new AttrDescView_widget_GridView_stretchMode(this));
        addAttrDescAN(new AttrDescReflecMethodDimensionIntFloor(this, "verticalSpacing", 0f));
    }
}


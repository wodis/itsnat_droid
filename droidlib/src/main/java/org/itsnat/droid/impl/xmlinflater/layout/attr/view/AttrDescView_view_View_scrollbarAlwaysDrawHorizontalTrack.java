package org.itsnat.droid.impl.xmlinflater.layout.attr.view;

import android.view.View;

import org.itsnat.droid.impl.dom.DOMAttr;
import org.itsnat.droid.impl.util.MiscUtil;
import org.itsnat.droid.impl.xmlinflater.layout.AttrLayoutContext;
import org.itsnat.droid.impl.xmlinflater.layout.OneTimeAttrProcess;
import org.itsnat.droid.impl.xmlinflater.layout.attr.AttrDescViewReflecFieldFieldMethod;
import org.itsnat.droid.impl.xmlinflater.layout.classtree.ClassDescViewBased;


/**
 * Created by jmarranz on 30/04/14.
 */
public class AttrDescView_view_View_scrollbarAlwaysDrawHorizontalTrack extends AttrDescViewReflecFieldFieldMethod
{
    public AttrDescView_view_View_scrollbarAlwaysDrawHorizontalTrack(ClassDescViewBased parent)
    {
        super(parent,"scrollbarAlwaysDrawHorizontalTrack","mScrollCache","scrollBar","setAlwaysDrawHorizontalTrack",
                MiscUtil.resolveClass("android.view.View$ScrollabilityCache"), MiscUtil.resolveClass("android.widget.ScrollBarDrawable"),
                boolean.class);
    }

    public void setAttribute(final View view, DOMAttr attr, AttrLayoutContext attrCtx)
    {
        final boolean convertedValue = getBoolean(attr.getValue(),attrCtx.getContext());

        OneTimeAttrProcess oneTimeAttrProcess = attrCtx.getOneTimeAttrProcess();
        if (oneTimeAttrProcess != null)
        {
            // Delegamos al final para que esté totalmente claro si hay o no scrollbars
            oneTimeAttrProcess.addLastTask(new Runnable()
            {
                @Override
                public void run()
                {
                    callFieldFieldMethod(view, convertedValue);
                }
            });
        }
        else
        {
            callFieldFieldMethod(view, convertedValue);
        }
    }

    public void removeAttribute(View view, AttrLayoutContext attrCtx)
    {
        setToRemoveAttribute(view, "false",attrCtx);
    }

}

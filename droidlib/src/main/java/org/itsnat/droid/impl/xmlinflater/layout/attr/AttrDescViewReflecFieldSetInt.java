package org.itsnat.droid.impl.xmlinflater.layout.attr;

import android.view.View;

import org.itsnat.droid.impl.dom.DOMAttr;
import org.itsnat.droid.impl.xmlinflater.layout.AttrLayoutContext;
import org.itsnat.droid.impl.xmlinflater.layout.classtree.ClassDescViewBased;


/**
 * Created by jmarranz on 30/04/14.
 */
public class AttrDescViewReflecFieldSetInt extends AttrDescViewReflecFieldSet
{
    protected int defaultValue;

    public AttrDescViewReflecFieldSetInt(ClassDescViewBased parent, String name, String fieldName, int defaultValue)
    {
        super(parent,name,fieldName);
        this.defaultValue = defaultValue;
    }

    public void setAttribute(View view, DOMAttr attr, AttrLayoutContext attrCtx)
    {
        int convertedValue = getInteger(attr.getValue(),attrCtx.getContext());

        setField(view,convertedValue);
    }

    public void removeAttribute(View view, AttrLayoutContext attrCtx)
    {
        setField(view,defaultValue);
    }

}

package org.itsnat.droid.impl.xmlinflater.shared.attr;

import android.graphics.drawable.Drawable;

import org.itsnat.droid.impl.dom.DOMAttr;
import org.itsnat.droid.impl.xmlinflater.AttrContext;
import org.itsnat.droid.impl.xmlinflater.shared.classtree.ClassDesc;


/**
 * Created by jmarranz on 30/04/14.
 */
public abstract class AttrDescReflecFieldFieldMethodDrawable<TclassDesc extends ClassDesc,TattrTarget,TattrContext extends AttrContext>
        extends AttrDescReflecFieldFieldMethod<TclassDesc,TattrTarget,TattrContext>
{
    public AttrDescReflecFieldFieldMethodDrawable(TclassDesc parent, String name, String fieldName1, String fieldName2, String methodName, Class field2Class, Class methodClass, Class paramClass)
    {
        super(parent,name,fieldName1,fieldName2,methodName,field2Class,methodClass,paramClass);
    }

    @Override
    public void setAttribute(final TattrTarget target,final DOMAttr attr,final TattrContext attrCtx)
    {
        Drawable convValue = getDrawable(attr,attrCtx.getContext(),attrCtx.getXMLInflater());
        callFieldFieldMethod(target, convValue);
    }

}

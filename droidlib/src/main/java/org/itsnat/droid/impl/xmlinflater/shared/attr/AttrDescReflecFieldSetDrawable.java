package org.itsnat.droid.impl.xmlinflater.shared.attr;

import android.graphics.drawable.Drawable;

import org.itsnat.droid.impl.dom.DOMAttr;
import org.itsnat.droid.impl.dom.DOMAttrRemote;
import org.itsnat.droid.impl.xmlinflater.AttrContext;
import org.itsnat.droid.impl.xmlinflater.ClassDesc;


/**
 * Created by jmarranz on 30/04/14.
 */
public class AttrDescReflecFieldSetDrawable<TclassDesc extends ClassDesc,TattrTarget,TattrContext extends AttrContext>
        extends AttrDescReflecFieldSet<TclassDesc,TattrTarget,TattrContext>
{
    protected String defaultValue;

    public AttrDescReflecFieldSetDrawable(TclassDesc parent, String name, String fieldName, String defaultValue)
    {
        super(parent,name,fieldName);
        this.defaultValue = defaultValue;
    }

    @Override
    public void setAttribute(final TattrTarget target, final DOMAttr attr,final TattrContext attrCtx)
    {
        Runnable task = new Runnable()
        {
            @Override
            public void run()
            {
                Drawable convertedValue = getDrawable(attr, attrCtx.getContext(),attrCtx.getXMLInflater());
                setField(target,convertedValue);
            }
        };
        if (DOMAttrRemote.isPendingToDownload(attr))
            processDownloadTask((DOMAttrRemote)attr,task,attrCtx.getXMLInflater());
        else
            task.run();
    }

    @Override
    public void removeAttribute(TattrTarget target, TattrContext attrCtx)
    {
        if (defaultValue != null)
            setToRemoveAttribute(target, defaultValue,attrCtx);
    }
}

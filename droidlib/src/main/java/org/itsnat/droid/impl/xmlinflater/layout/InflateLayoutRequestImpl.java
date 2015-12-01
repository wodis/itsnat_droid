package org.itsnat.droid.impl.xmlinflater.layout;

import android.content.Context;

import org.itsnat.droid.AttrDrawableInflaterListener;
import org.itsnat.droid.AttrLayoutInflaterListener;
import org.itsnat.droid.impl.ItsNatDroidImpl;
import org.itsnat.droid.impl.browser.PageImpl;
import org.itsnat.droid.impl.dom.layout.XMLDOMLayout;
import org.itsnat.droid.impl.xmlinflated.layout.InflatedLayoutImpl;
import org.itsnat.droid.impl.xmlinflated.layout.InflatedLayoutPageImpl;
import org.itsnat.droid.impl.xmlinflated.layout.InflatedLayoutStandaloneImpl;

import java.util.List;

/**
 * Created by jmarranz on 5/06/14.
 */
public abstract class InflateLayoutRequestImpl
{
    protected ItsNatDroidImpl itsNatDroid;

    public InflateLayoutRequestImpl(ItsNatDroidImpl itsNatDroid)
    {
        this.itsNatDroid = itsNatDroid;
    }

    public ItsNatDroidImpl getItsNatDroidImpl()
    {
        return itsNatDroid;
    }

    public abstract int getBitmapDensityReference();

    public abstract String getEncoding();

    public abstract AttrLayoutInflaterListener getAttrLayoutInflaterListener();

    public abstract AttrDrawableInflaterListener getAttrDrawableInflaterListener();

    public abstract Context getContext();

    public XMLInflaterLayout inflateLayout(XMLDOMLayout xmlDOMLayout, String[] loadScript, List<String> scriptList, PageImpl page)
    {
        return XMLInflaterLayout.inflateLayout(itsNatDroid,xmlDOMLayout,loadScript,scriptList,getBitmapDensityReference(),getAttrLayoutInflaterListener(),getAttrDrawableInflaterListener(),getContext(),page);
    }

}

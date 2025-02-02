package org.itsnat.droid.impl.xmlinflated.layout;

import android.content.Context;

import org.itsnat.droid.impl.ItsNatDroidImpl;
import org.itsnat.droid.impl.browser.PageImpl;
import org.itsnat.droid.impl.dom.layout.XMLDOMLayout;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by jmarranz on 20/08/14.
 */
public class InflatedLayoutPageImpl extends InflatedLayoutImpl
{
    protected PageImpl page;
    protected String loadScript;
    protected List<String> scriptList = new LinkedList<String>();

    public InflatedLayoutPageImpl(PageImpl page,ItsNatDroidImpl itsNatDroid,XMLDOMLayout domLayout,Context ctx)
    {
        super(itsNatDroid, domLayout, ctx);
        this.page = page; // NO puede ser nulo
    }

    public PageImpl getPageImpl()
    {
        return page;
    }

    public String getLoadScript()
    {
        return loadScript;
    }

    public void setLoadScript(String loadScript)
    {
        this.loadScript = loadScript;
    }

    public List<String> getScriptList()
    {
        return scriptList;
    }

    public void addScript(String code)
    {
        scriptList.add(code);
    }
}

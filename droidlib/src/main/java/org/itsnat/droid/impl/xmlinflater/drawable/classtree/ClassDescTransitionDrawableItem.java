package org.itsnat.droid.impl.xmlinflater.drawable.classtree;

import android.content.Context;

import org.itsnat.droid.impl.dom.DOMElement;
import org.itsnat.droid.impl.xmlinflated.drawable.ElementDrawable;
import org.itsnat.droid.impl.xmlinflated.drawable.ElementDrawableChild;
import org.itsnat.droid.impl.xmlinflated.drawable.TransitionDrawableItem;
import org.itsnat.droid.impl.xmlinflater.drawable.ClassDescDrawableMgr;
import org.itsnat.droid.impl.xmlinflater.drawable.XMLInflaterDrawable;

/**
 * Created by jmarranz on 10/11/14.
 */
public class ClassDescTransitionDrawableItem extends ClassDescElementDrawableChildWithDrawable<TransitionDrawableItem>
{
    public ClassDescTransitionDrawableItem(ClassDescDrawableMgr classMgr,ClassDescLayerDrawableItem parentClass)
    {
        super(classMgr,"transition:item",parentClass);
    }

    @Override
    public Class<TransitionDrawableItem> getDrawableOrElementDrawableClass()
    {
        return TransitionDrawableItem.class;
    }

    @Override
    public ElementDrawableChild createElementDrawableChild(DOMElement domElement, DOMElement domElementParent, XMLInflaterDrawable inflaterDrawable, ElementDrawable parentChildDrawable, Context ctx)
    {
        return new TransitionDrawableItem(parentChildDrawable);
    }

    protected void init()
    {
        super.init();

        // Se definen en la clase base item de LayerDrawable
    }

}

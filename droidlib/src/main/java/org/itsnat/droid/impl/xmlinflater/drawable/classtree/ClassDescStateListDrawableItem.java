package org.itsnat.droid.impl.xmlinflater.drawable.classtree;

import android.content.Context;

import org.itsnat.droid.impl.dom.DOMElement;
import org.itsnat.droid.impl.xmlinflated.drawable.ElementDrawable;
import org.itsnat.droid.impl.xmlinflated.drawable.ElementDrawableChild;
import org.itsnat.droid.impl.xmlinflated.drawable.StateListDrawableItem;
import org.itsnat.droid.impl.xmlinflater.drawable.ClassDescDrawableMgr;
import org.itsnat.droid.impl.xmlinflater.drawable.XMLInflaterDrawable;
import org.itsnat.droid.impl.xmlinflater.shared.attr.AttrDescReflecMethodBoolean;
import org.itsnat.droid.impl.xmlinflater.shared.attr.AttrDescReflecMethodDrawable;

/**
 * Created by jmarranz on 10/11/14.
 */
public class ClassDescStateListDrawableItem extends ClassDescElementDrawableChildWithDrawable<StateListDrawableItem>
{
    public ClassDescStateListDrawableItem(ClassDescDrawableMgr classMgr)
    {
        super(classMgr,"selector:item");
    }

    @Override
    public Class<StateListDrawableItem> getDrawableOrElementDrawableClass()
    {
        return StateListDrawableItem.class;
    }

    @Override
    public ElementDrawableChild createElementDrawableChild(DOMElement domElement, DOMElement domElementParent, XMLInflaterDrawable inflaterDrawable, ElementDrawable parentChildDrawable, Context ctx)
    {
        return new StateListDrawableItem(parentChildDrawable);
    }

    @SuppressWarnings("unchecked")
    protected void init()
    {
        super.init();


        addAttrDescAN(new AttrDescReflecMethodBoolean(this, "constantSize", false));

        addAttrDescAN(new AttrDescReflecMethodDrawable(this, "drawable", "@null"));

        // Documentados en https://developer.android.com/guide/topics/resources/drawable-resource.html#StateList
        addAttrDescAN(new AttrDescReflecMethodBoolean(this, "state_pressed", false));
        addAttrDescAN(new AttrDescReflecMethodBoolean(this, "state_focused", false));
        addAttrDescAN(new AttrDescReflecMethodBoolean(this, "state_hovered", false));
        addAttrDescAN(new AttrDescReflecMethodBoolean(this, "state_selected", false));
        addAttrDescAN(new AttrDescReflecMethodBoolean(this, "state_checkable", false));
        addAttrDescAN(new AttrDescReflecMethodBoolean(this, "state_checked", false));
        addAttrDescAN(new AttrDescReflecMethodBoolean(this, "state_enabled", false));
        addAttrDescAN(new AttrDescReflecMethodBoolean(this, "state_activated", false));
        addAttrDescAN(new AttrDescReflecMethodBoolean(this, "state_window_focused", false));

        // Presentes en source code y en el javadoc de ICS (level 15) pero no en el tutorial
        // http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/4.0.3_r1/android/graphics/drawable/StateListDrawable.java
        addAttrDescAN(new AttrDescReflecMethodBoolean(this, "state_active", false));
        addAttrDescAN(new AttrDescReflecMethodBoolean(this, "state_single", false));
        addAttrDescAN(new AttrDescReflecMethodBoolean(this, "state_first", false));
        addAttrDescAN(new AttrDescReflecMethodBoolean(this, "state_middle", false));
        addAttrDescAN(new AttrDescReflecMethodBoolean(this, "state_last", false));

        // En teoria hay más android.R.attr.state_* en level 15 pero no tenemos ni idea de como funcionan

        addAttrDescAN(new AttrDescReflecMethodBoolean(this, "variablePadding", false));
        addAttrDescAN(new AttrDescReflecMethodBoolean(this, "visible", false));
    }

}

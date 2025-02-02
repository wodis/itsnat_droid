package org.itsnat.droid.impl.xmlinflater.drawable;

import android.graphics.drawable.Drawable;

import org.itsnat.droid.impl.xmlinflater.ClassDescMgr;
import org.itsnat.droid.impl.xmlinflater.XMLInflateRegistry;
import org.itsnat.droid.impl.xmlinflater.drawable.classtree.ClassDescAnimationDrawable;
import org.itsnat.droid.impl.xmlinflater.drawable.classtree.ClassDescAnimationDrawableItem;
import org.itsnat.droid.impl.xmlinflater.drawable.classtree.ClassDescBitmapDrawable;
import org.itsnat.droid.impl.xmlinflater.drawable.classtree.ClassDescClipDrawable;
import org.itsnat.droid.impl.xmlinflater.drawable.classtree.ClassDescColorDrawable;
import org.itsnat.droid.impl.xmlinflater.drawable.classtree.ClassDescDrawable;
import org.itsnat.droid.impl.xmlinflater.drawable.classtree.ClassDescElementDrawableChildDrawableBridge;
import org.itsnat.droid.impl.xmlinflater.drawable.classtree.ClassDescGradientDrawable;
import org.itsnat.droid.impl.xmlinflater.drawable.classtree.ClassDescGradientDrawableItemCorners;
import org.itsnat.droid.impl.xmlinflater.drawable.classtree.ClassDescGradientDrawableItemGradient;
import org.itsnat.droid.impl.xmlinflater.drawable.classtree.ClassDescGradientDrawableItemPadding;
import org.itsnat.droid.impl.xmlinflater.drawable.classtree.ClassDescGradientDrawableItemSize;
import org.itsnat.droid.impl.xmlinflater.drawable.classtree.ClassDescGradientDrawableItemSolid;
import org.itsnat.droid.impl.xmlinflater.drawable.classtree.ClassDescGradientDrawableItemStroke;
import org.itsnat.droid.impl.xmlinflater.drawable.classtree.ClassDescInsetDrawable;
import org.itsnat.droid.impl.xmlinflater.drawable.classtree.ClassDescLayerDrawable;
import org.itsnat.droid.impl.xmlinflater.drawable.classtree.ClassDescLayerDrawableItem;
import org.itsnat.droid.impl.xmlinflater.drawable.classtree.ClassDescLevelListDrawable;
import org.itsnat.droid.impl.xmlinflater.drawable.classtree.ClassDescLevelListDrawableItem;
import org.itsnat.droid.impl.xmlinflater.drawable.classtree.ClassDescNinePatchDrawable;
import org.itsnat.droid.impl.xmlinflater.drawable.classtree.ClassDescRotateDrawable;
import org.itsnat.droid.impl.xmlinflater.drawable.classtree.ClassDescScaleDrawable;
import org.itsnat.droid.impl.xmlinflater.drawable.classtree.ClassDescStateListDrawable;
import org.itsnat.droid.impl.xmlinflater.drawable.classtree.ClassDescStateListDrawableItem;
import org.itsnat.droid.impl.xmlinflater.drawable.classtree.ClassDescTransitionDrawable;
import org.itsnat.droid.impl.xmlinflater.drawable.classtree.ClassDescTransitionDrawableItem;

/**
 * Created by jmarranz on 30/04/14.
 */
public class ClassDescDrawableMgr extends ClassDescMgr<ClassDescDrawable,Drawable>
{
    public ClassDescDrawableMgr(XMLInflateRegistry parent)
    {
        super(parent);
        initClassDesc();
    }

    @Override
    public ClassDescDrawable get(String classOrDOMElemName)
    {
        return classes.get(classOrDOMElemName);
    }


    @Override
    protected void initClassDesc()
    {
        ClassDescElementDrawableChildDrawableBridge childBridge = new ClassDescElementDrawableChildDrawableBridge(this);
        addClassDesc(childBridge);

        ClassDescBitmapDrawable bitmap = new ClassDescBitmapDrawable(this);
        addClassDesc(bitmap);

        ClassDescColorDrawable color = new ClassDescColorDrawable(this);
        addClassDesc(color);

        // DrawableContainer

            ClassDescAnimationDrawable animation = new ClassDescAnimationDrawable(this);
            addClassDesc(animation);
            ClassDescAnimationDrawableItem animationItem = new ClassDescAnimationDrawableItem(this);
            addClassDesc(animationItem);

            ClassDescLevelListDrawable levelList = new ClassDescLevelListDrawable(this);
            addClassDesc(levelList);
            ClassDescLevelListDrawableItem levelListItem = new ClassDescLevelListDrawableItem(this);
            addClassDesc(levelListItem);

            ClassDescStateListDrawable stateList = new ClassDescStateListDrawable(this);
            addClassDesc(stateList);
            ClassDescStateListDrawableItem stateListItem = new ClassDescStateListDrawableItem(this);
            addClassDesc(stateListItem);

                // AnimatedStateListDrawable hereda de StateListDrawable pero es level 21

        // DrawableWrapper es level 23 pero lo vamos teniendo en cuenta

            ClassDescClipDrawable clip = new ClassDescClipDrawable(this);
            addClassDesc(clip);

            ClassDescInsetDrawable inset = new ClassDescInsetDrawable(this);
            addClassDesc(inset);

            ClassDescRotateDrawable rotate = new ClassDescRotateDrawable(this);
            addClassDesc(rotate);

            ClassDescScaleDrawable scale = new ClassDescScaleDrawable(this);
            addClassDesc(scale);


        ClassDescGradientDrawable gradient = new ClassDescGradientDrawable(this);
        addClassDesc(gradient);
        ClassDescGradientDrawableItemCorners gradientCornersItem = new ClassDescGradientDrawableItemCorners(this);
        addClassDesc(gradientCornersItem);
        ClassDescGradientDrawableItemGradient gradientGradientItem = new ClassDescGradientDrawableItemGradient(this);
        addClassDesc(gradientGradientItem);
        ClassDescGradientDrawableItemPadding gradientPaddingItem = new ClassDescGradientDrawableItemPadding(this);
        addClassDesc(gradientPaddingItem);
        ClassDescGradientDrawableItemSize gradientSizeItem = new ClassDescGradientDrawableItemSize(this);
        addClassDesc(gradientSizeItem);
        ClassDescGradientDrawableItemSolid gradientSolidItem = new ClassDescGradientDrawableItemSolid(this);
        addClassDesc(gradientSolidItem);
        ClassDescGradientDrawableItemStroke gradientStrokeItem = new ClassDescGradientDrawableItemStroke(this);
        addClassDesc(gradientStrokeItem);

        ClassDescLayerDrawable layer = new ClassDescLayerDrawable(this);
        addClassDesc(layer);
        ClassDescLayerDrawableItem layerItem = new ClassDescLayerDrawableItem(this);
        addClassDesc(layerItem);

            ClassDescTransitionDrawable transition = new ClassDescTransitionDrawable(this,layer);
            addClassDesc(transition);
            ClassDescTransitionDrawableItem transitionItem = new ClassDescTransitionDrawableItem(this,layerItem);
            addClassDesc(transitionItem);

            // RippleDrawable es level 21

        ClassDescNinePatchDrawable ninePatch = new ClassDescNinePatchDrawable(this);
        addClassDesc(ninePatch);

        // ShapeDrawable no se como usarlo como XML pues <shape> se refiere a GradientDrawable, parece que ShapeDrawable es una antigualla o bien realmente sólo se puede usar via objeto Java
    }
}

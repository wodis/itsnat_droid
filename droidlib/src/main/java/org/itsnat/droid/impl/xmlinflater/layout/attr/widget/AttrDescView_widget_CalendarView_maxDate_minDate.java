package org.itsnat.droid.impl.xmlinflater.layout.attr.widget;

import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.CalendarView;

import org.itsnat.droid.ItsNatDroidException;
import org.itsnat.droid.impl.dom.DOMAttr;
import org.itsnat.droid.impl.util.MiscUtil;
import org.itsnat.droid.impl.xmlinflater.FieldContainer;
import org.itsnat.droid.impl.xmlinflater.MethodContainer;
import org.itsnat.droid.impl.xmlinflater.layout.AttrLayoutContext;
import org.itsnat.droid.impl.xmlinflater.layout.classtree.ClassDescViewBased;
import org.itsnat.droid.impl.xmlinflater.shared.attr.AttrDesc;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by jmarranz on 30/04/14.
 */
public class AttrDescView_widget_CalendarView_maxDate_minDate extends AttrDesc<ClassDescViewBased,View,AttrLayoutContext>
{
    private static final String DEFAULT_MIN_DATE = "01/01/1900";
    private static final String DEFAULT_MAX_DATE = "01/01/2100";

    protected FieldContainer<Object> fieldDelegate;
    protected FieldContainer<Locale> fieldCurrentLocale;
    protected MethodContainer<Boolean> methodParseDate;
    protected FieldContainer<Calendar> fieldMaxMinDate;

    public AttrDescView_widget_CalendarView_maxDate_minDate(ClassDescViewBased parent, String name)
    {
        super(parent,name);

        Class calendarClassWithCurrentLocale,calendarClassWithParseDate,calendarClassWithMaxMinDate;
        if (Build.VERSION.SDK_INT < MiscUtil.LOLLIPOP) // < 21
        {
            calendarClassWithCurrentLocale = parent.getDeclaredClass(); // CalendarView
            calendarClassWithParseDate = calendarClassWithCurrentLocale;
            calendarClassWithMaxMinDate = calendarClassWithCurrentLocale;
        }
        else // Lollipop y superiores
        {
            this.fieldDelegate = new FieldContainer<Object>(parent.getDeclaredClass(), "mDelegate");
            calendarClassWithCurrentLocale = MiscUtil.resolveClass(CalendarView.class.getName() + "$AbstractCalendarViewDelegate");
            if (Build.VERSION.SDK_INT == MiscUtil.LOLLIPOP) // 21 (5.0)
            {
                calendarClassWithParseDate = MiscUtil.resolveClass(CalendarView.class.getName() + "$LegacyCalendarViewDelegate");
                calendarClassWithMaxMinDate = calendarClassWithParseDate;
            }
            else if (Build.VERSION.SDK_INT == MiscUtil.LOLLIPOP_MR1) // 22 (5.1)
            {
                // mDelegate puede ser un objeto android.widget.CalendarViewLegacyDelegate o android.widget.CalendarViewMaterialDelegate (ambas clases autónomas) dependiendo
                // de un  MODE_HOLO o MODE_MATERIAL, NO SOPORTAMOS por ahora MODE_MATERIAL por lo que se creará un CalendarViewLegacyDelegate
                calendarClassWithParseDate = calendarClassWithCurrentLocale; // parseDate está ya en $AbstractCalendarViewDelegate
                calendarClassWithMaxMinDate = MiscUtil.resolveClass("android.widget.CalendarViewLegacyDelegate"); // CalendarViewLegacyDelegate no está ya embebida en CalendarView, es autónoma
            }
            else // 23 en adelante
            {
                calendarClassWithParseDate = parent.getDeclaredClass(); // CalendarView
                calendarClassWithMaxMinDate = MiscUtil.resolveClass("android.widget.CalendarViewLegacyDelegate"); // CalendarViewLegacyDelegate no está ya embebida en CalendarView, es autónoma
            }
        }

        this.fieldCurrentLocale = new FieldContainer<Locale>(calendarClassWithCurrentLocale, "mCurrentLocale");
        this.methodParseDate = new MethodContainer<Boolean>(calendarClassWithParseDate,"parseDate",new Class[]{String.class,Calendar.class});

        String fieldName = null;
        if ("maxDate".equals(name))
            fieldName = "mMaxDate";
        else if ("minDate".equals(name))
            fieldName = "mMinDate";
        this.fieldMaxMinDate = new FieldContainer<Calendar>(calendarClassWithMaxMinDate,fieldName);
    }

    @Override
    public void setAttribute(View view, DOMAttr attr, AttrLayoutContext attrCtx)
    {
        String date = getString(attr.getValue(),attrCtx.getContext());

        Object calendarObject = getCalendarObject((CalendarView)view);

        Locale currentLocale = fieldCurrentLocale.get(calendarObject);
        Calendar outDate = Calendar.getInstance(currentLocale);
        // No es necesario: outDate.clear();

        if (!TextUtils.isEmpty(date))
        {
            if (!parseDate(calendarObject,date, outDate)) // El código fuente de Android tolera un mal formato, nosotros no pues no hace más que complicarlo todo
                throw new ItsNatDroidException("Date: " + date + " not in format: " + "MM/dd/yyyy");
        }
        else // Caso de eliminación de atributo, interpretamos el "" como el deseo de poner los valores por defecto (más o menos es así en el código fuente)
        {
            String defaultMaxMin = null;
            if ("maxDate".equals(name))
                defaultMaxMin = DEFAULT_MAX_DATE;
            else if ("minDate".equals(name))
                defaultMaxMin = DEFAULT_MIN_DATE;

            parseDate(calendarObject,defaultMaxMin, outDate);
        }

        fieldMaxMinDate.set(calendarObject,outDate);
    }

    @Override
    public void removeAttribute(View view, AttrLayoutContext attrCtx)
    {
        String value = null;
        if ("maxDate".equals(name))
            value = DEFAULT_MAX_DATE;
        else if ("minDate".equals(name))
            value = DEFAULT_MIN_DATE;
        setToRemoveAttribute(view, value,attrCtx);
    }

    private Object getCalendarObject(CalendarView view)
    {
        if (Build.VERSION.SDK_INT < MiscUtil.LOLLIPOP)
            return view;
        else
            return fieldDelegate.get(view);
    }

    private boolean parseDate(Object calendarObject,String date, Calendar outDate)
    {
        return methodParseDate.invoke(calendarObject,date,outDate);
    }
}

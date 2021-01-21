package com.procrastinator.proccy;

import android.graphics.Color;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;


public class DotSpanDecorator implements DayViewDecorator {

    private final int color = Color.parseColor("#FF0000");
    private final CalendarDay dates;

    public DotSpanDecorator(CalendarDay dates){
        this.dates = dates;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates==day;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(5, color));
    }
}

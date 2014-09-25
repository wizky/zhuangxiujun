package com.campingfun.vacancyhunter.campsitehunter;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by wayliu on 9/3/2014.
 */
public class Event_AvailabilityAlertDateSelected {
    private GregorianCalendar date;

    public Event_AvailabilityAlertDateSelected(GregorianCalendar date) {
        this.date = date;
    }

    public GregorianCalendar getDate() {
        return date;
    }
}

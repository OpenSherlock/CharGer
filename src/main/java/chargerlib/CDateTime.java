/*
 * Mental Model Acquisition Tool (MMAT) Client Software. 
 * Copyright 2014-2018 by The University of Alabama in Huntsville (UAH), Huntsville, AL 35899, USA. All Rights Reserved. 
 * Unless permission is granted, this material may not be copied, reproduced or coded for reproduction
 *  by any electrical, mechanical or chemical process or combination thereof, now known or later developed.
 * Licensed by UAH for use by Teamwork Evaluation and Modeling, Inc.
 * 
 * $Date$   $Rev$   $Author$
 * 
 */

package chargerlib;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;


/**
 * /** Encapsulates the representation of date/time values.
 * As a wrapper for ZonedDateTime, an internal time value is represented by a ZonedDateTime.
 * The style of its format depends on its use at different places 
 * in the MMAT. 
 *
 * @author Harry S. Delugach (delugach@uah.edu)
 */
public class CDateTime implements Serializable {

    /** Indicates differing string formats used in the MMAT.
     * Each style (except for ISO_OFFSET) has its own built-in formatter for generating its format.
     * @see DateTimeFormatter
     */
    public enum STYLE {
        /** A human-readable date/time format with an alphabetic time zone.. 
         * Uses the pattern "MMM d yyyy, H:mm:ss z" (e.g., "Jan 1 2018, 19:07:53 CST" )
         */
        ZONED_TIMESTAMP( "MMM d yyyy, H:mm:ss z"),
       
        /** The ISO 8601 time format. 
         * Its declared pattern is ignored; uses the built-in static formatter for ISO_OFFSET_DATE_TIME.
         * Produces a string of the form "2018-01-01T19:07:53-06:00"
         */
        ISO_OFFSET( "MMM"),
        
        /** A numeric-zoned format that includes the weekday.
         * Uses the pattern "EEE, d MMM yyyy HH:mm:ss z" (e.g., "Wed, 10 Oct 2018 19:07:53 -05:00" )
         */
        ZONED_WEEKDAY_TIMESTAMP( "EEE, d MMM yyyy HH:mm:ss z"),
        
        /** A short date/time format.
         * Uses the pattern "dd-MMM-yyyy" (e.g., "12-Jan-2018" )
         */
        D_M_Y_ONLY( "d-MMM-yyyy"),
        
        /** A year-month-day-hour-min-second format useful for appending to file/folder names for
         * timestamping purposes.
         * Uses the pattern "yyyy.MM.dd.HH.mm.ss" (e.g., "2018.01.12.05.18.40" )
         */
        YMDHMS_DOTTED( "yyyy.MM.dd.HH.mm.ss"),
        
        /** A year-month-day-hour-min-second format useful for appending to file/folder names for
         * timestamping purposes.
         * Uses the pattern "yyyy-MM-dd-HHmmss" (e.g., "2018-01-12-05-18-40" )
         */
        YMDHMS_DASHED( "yyyy-MM-dd-HHmmss"),
        
        /** A legacy time format, corresponding to an old date format of MEDIUM and time format of MEDIUM
         * in the use of the old DateFormat class.
         * Uses the pattern "MMM d, yyyy h:mm:ss a" (e.g., "Jan 1, 2018 7:07:53 PM" )
         */
        LEGACY_MEDIUM_MEDIUM( "MMM d, yyyy h:mm:ss a");

        private String pattern;
        private DateTimeFormatter formatter;

        private STYLE( String pattern ) {
            this.pattern = pattern;
            formatter = DateTimeFormatter.ofPattern( pattern );
        }
    }
    
    
    ZonedDateTime dateTime = null;

    /** Creates a new instance corresponding to the current date/time ("now")
     * in the system default time zone as defined in its Locale.
     */
    public CDateTime() {
        dateTime = ZonedDateTime.now();
    }
    
    /** Create a new instance corresponding to the date/time represented
     * by the string. Applies each of the available style format parsers
     * until it finds
     * one that works.
     *
     * @param timeString A date/time string recognized by one of the defined
     * style's formatters. If it is not recognized by any of them, an exception
     * is thrown.
     */
    public CDateTime( String timeString ) throws DateTimeException {
        TemporalAccessor temporalObject = null;
        try {
            temporalObject = STYLE.ZONED_TIMESTAMP.formatter.parse( timeString );
            dateTime = ZonedDateTime.from( temporalObject );
        } catch ( DateTimeException ex ) {
            // If this one doesn't work, try another one.
        }
        if ( dateTime != null ) {
            return;
        }
        try {
            temporalObject = STYLE.LEGACY_MEDIUM_MEDIUM.formatter.parse( timeString );
            LocalDateTime ldt = LocalDateTime.from( temporalObject );
            dateTime = ldt.atZone( ZoneId.systemDefault() );
        } catch ( DateTimeException ex ) {

        }
        if ( dateTime != null ) {
            return;
        }
        try {
            temporalObject = STYLE.ZONED_WEEKDAY_TIMESTAMP.formatter.parse( timeString );
            dateTime = ZonedDateTime.from( temporalObject );
        } catch ( DateTimeException ex ) {
            // If this one doesn't work, try another one.
        }
        if ( dateTime != null ) {
            return;
        }
        try {
            temporalObject = STYLE.D_M_Y_ONLY.formatter.parse( timeString );
            LocalDate ld = LocalDate.from( temporalObject );
            dateTime = ld.atTime( 23, 59 ).atZone( ZoneId.systemDefault() );
        } catch ( DateTimeException ex ) {
            // If this one doesn't work, try another one.
        }
        if ( dateTime != null ) {
            return;
        }
        try {
            temporalObject = STYLE.YMDHMS_DASHED.formatter.parse( timeString );
            LocalDateTime ldt = LocalDateTime.from( temporalObject );
            dateTime = ldt.atZone( ZoneId.systemDefault() );
        } catch ( DateTimeException ex ) {
            // If this one doesn't work, try another one.
        }
        if ( dateTime != null ) {
            return;
        }
        try {
            temporalObject = STYLE.YMDHMS_DOTTED.formatter.parse( timeString );
            LocalDateTime ldt = LocalDateTime.from( temporalObject );
            dateTime = ldt.atZone( ZoneId.systemDefault() );
        } catch ( DateTimeException ex ) {
            // If this one doesn't work, try another one.
        }
        if ( dateTime != null ) {
            return;
        }
        try {
            temporalObject = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse( timeString );
            dateTime = ZonedDateTime.from( temporalObject );
        } catch ( DateTimeException ex ) {
//            System.out.println( this.getClass().getCanonicalName() + ": constructor: gave up trying to parse " + timeString );
            throw ex;
        }
    }

    

    /** Use one of the pre-defined styles to format the date string.
     * 
     * @param style
     * @return the date in string from as specified by the style.
     */
    public String formatted( STYLE style ) {
        if ( style == STYLE.ISO_OFFSET )
            return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format( dateTime );
        return style.formatter.format( dateTime );
    }
    
    /**
     * Use any format pattern to display the string.
     *
     * @param pattern
     * @return
     * @see DateTimeFormatter
     */
    public String formatted( String pattern ) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern( pattern );
        return formatter.format( dateTime );
    }

    /** Get this date/time value in zoned date time form.
     * 
     * @return the date/time value of this object.
     */
    public ZonedDateTime getZonedDateTimeValue() {
        return this.dateTime;
    }
    
    /** Set the date/time value from a zoned date time value.
     * 
     * @param dateTime non-null date time value.
     */
//    @XmlElement
    public void setZonedDateTimeValue( ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }
    
    
    
    /** Convenience method for using the ZONED_TIMESTAMP style.
     * 
     * @return 
     */
    public String toString() {
        return this.formatted( STYLE.ZONED_TIMESTAMP );
    }

    /** Compare this date/time value with another.
     * 
     * @param other another date/time value
     * @return true if the other value is an instant after this one; false otherwise.
     */
    public boolean after( CDateTime other ) {
        return this.getZonedDateTimeValue().isAfter( other.getZonedDateTimeValue() );
    }

    /** Compare this date/time value with another.
     * 
     * @param other another date/time value
     * @return true if the other value is an instant before this one; false otherwise.
     */
    public boolean before( CDateTime other ) {
        return this.getZonedDateTimeValue().isBefore( other.getZonedDateTimeValue() );
    }
    
    /** Compare this date/time value with another.
     * 
     * @param other another date/time value
     * @return true if the other value is equal to this one, accounting for time zone.
     * @see ZonedDateTime#equals(java.lang.Object) 
     */
    public boolean equals( CDateTime other ) {
        return this.getZonedDateTimeValue().equals( other.getZonedDateTimeValue() );
    }
    
    /** Get the date time object that corresponds to the beginning of the epoch.
     * Note that it will reflect the time zone, so the date may actually be Dec 31, 1969.
     * @return 
     */
    public static CDateTime getEpoch() {
        CDateTime epoch = new CDateTime();
        epoch.setZonedDateTimeValue( ZonedDateTime.ofInstant( Instant.EPOCH, ZoneId.systemDefault() ) );
        return epoch;
    }
    
    /**
     * Fills the same role as the old Date.time() method, returning the number of milliseconds since the epoch.
     * @return 
     */
    public long getTime() {
        return getEpoch().getZonedDateTimeValue().until( this.getZonedDateTimeValue(), ChronoUnit.MILLIS  );
    }
    
    /**
     * Determines whether the given date/time is in the future when the method is called.
     * @return true if it's in the future; false otherwise.
     */
    public boolean inFuture() {
        return this.after( new CDateTime() );
    }

    /**
     * Determines whether the given date/time is in the past when the method is called.
     * @return true if it's in the future; false otherwise.
     */
    public boolean inPast() {
        return this.before( new CDateTime() );
    }
}
